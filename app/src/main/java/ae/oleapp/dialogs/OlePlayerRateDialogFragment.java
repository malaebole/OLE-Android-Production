package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentPlayerRateDialogBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.feeeei.circleseekbar.CircleSeekBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OlePlayerRateDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentPlayerRateDialogBinding binding;
    private String playerId = "";
    private String bookingId = "";
    private String reachTime = "";
    private PlayerRateDialogCallback dialogCallback;

    public OlePlayerRateDialogFragment() {
        // Required empty public constructor
    }

    public OlePlayerRateDialogFragment(String playerId, String bookingId) {
        this.playerId = playerId;
        this.bookingId = bookingId;
    }

    public void setDialogCallback(PlayerRateDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPlayerRateDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.progressVu.setVisibility(View.GONE);

        binding.circular.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                binding.tvLevel.setText(String.format("%s%%", curValue));
            }
        });

        binding.circular.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle Seek bar touch events.
            v.onTouchEvent(event);
            return true;
        });

        binding.relBeforeTime.setOnClickListener(this);
        binding.relLate.setOnClickListener(this);
        binding.relNotCome.setOnClickListener(this);
        binding.relOnTime.setOnClickListener(this);
        binding.btnDismiss.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

        getProfileAPI(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relBeforeTime) {
            beforeTimeClicked();
        }
        else if (v == binding.relOnTime) {
            onTimeClicked();
        }
        else if (v == binding.relLate) {
            lateClicked();
        }
        else if (v == binding.relNotCome) {
            notComeClicked();
        }
        else if (v == binding.btnDismiss) {
            dismiss();
        }
        else if (v == binding.btnSubmit) {
            submitClicked();
        }
    }

    private void beforeTimeClicked() {
        reachTime = "1";
        binding.imgBeforeTime.setImageResource(R.drawable.check);
        binding.imgOnTime.setImageResource(R.drawable.uncheck);
        binding.imgLate.setImageResource(R.drawable.uncheck);
        binding.imgNotCome.setImageResource(R.drawable.uncheck);
    }

    private void onTimeClicked() {
        reachTime = "2";
        binding.imgBeforeTime.setImageResource(R.drawable.uncheck);
        binding.imgOnTime.setImageResource(R.drawable.check);
        binding.imgLate.setImageResource(R.drawable.uncheck);
        binding.imgNotCome.setImageResource(R.drawable.uncheck);
    }

    private void lateClicked() {
        reachTime = "3";
        binding.imgBeforeTime.setImageResource(R.drawable.uncheck);
        binding.imgOnTime.setImageResource(R.drawable.uncheck);
        binding.imgLate.setImageResource(R.drawable.check);
        binding.imgNotCome.setImageResource(R.drawable.uncheck);
    }

    private void notComeClicked() {
        reachTime = "4";
        binding.imgBeforeTime.setImageResource(R.drawable.uncheck);
        binding.imgOnTime.setImageResource(R.drawable.uncheck);
        binding.imgLate.setImageResource(R.drawable.uncheck);
        binding.imgNotCome.setImageResource(R.drawable.check);
    }

    private void submitClicked() {
        if (binding.circular.getCurProcess() == 0) {
            Functions.showToast(getContext(), getString(R.string.playing_level_not_zero), FancyToast.ERROR);
            return;
        }
        if (reachTime.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_player_time), FancyToast.ERROR);
            return;
        }
        rateProfileAPI(true, binding.etMsg.getText().toString(), binding.circular.getCurProcess());
    }

    private void populateData(OlePlayerInfo olePlayerInfo) {
        binding.tvName.setText(olePlayerInfo.getNickName());
        binding.tvPoints.setText(getString(R.string.points_place, olePlayerInfo.getPoints()));
        Glide.with(this).load(olePlayerInfo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.playerImage);
        if (olePlayerInfo.getWinPercentage().isEmpty()) {
            binding.tvPerc.setText("0%");
        }
        else {
            binding.tvPerc.setText(String.format("%s%%", olePlayerInfo.getWinPercentage()));
        }
        if (olePlayerInfo.getLevel() != null && !olePlayerInfo.getLevel().isEmpty() && !olePlayerInfo.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvRank.setVisibility(View.VISIBLE);
            binding.tvRank.setText(String.format("LV: %s", olePlayerInfo.getLevel().getValue()));
        }
        else {
            binding.tvRank.setVisibility(View.INVISIBLE);
        }
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), playerId,"",  Functions.getPrefValue(getContext(),Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            OlePlayerInfo olePlayerInfo = gson.fromJson(obj.toString(), OlePlayerInfo.class);
                            populateData(olePlayerInfo);
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void rateProfileAPI(boolean isLoader, String feedback, int rate) {
        binding.progressVu.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addGameRating(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), playerId, bookingId, reachTime, feedback, rate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                binding.progressVu.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dialogCallback.didRatePlayer();
                            dismiss();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.progressVu.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    public interface PlayerRateDialogCallback {
        void didRatePlayer();
    }
}
