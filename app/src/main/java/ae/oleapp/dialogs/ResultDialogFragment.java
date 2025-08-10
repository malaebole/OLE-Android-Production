package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import  ae.oleapp.R;
import ae.oleapp.databinding.FragmentResultDialogBinding;
import ae.oleapp.models.GameHistory;
import ae.oleapp.util.AppManager;
import  ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentResultDialogBinding binding;
    private GameHistory result;
    private ResultDialogFragmentCallback dialogFragmentCallback;
    private String popupId = "", msg = "";

    public ResultDialogFragment() {
        // Required empty public constructor
    }

    public ResultDialogFragment(GameHistory result, String popupId, String msg) {
        this.result = result;
        this.popupId = popupId;
        this.msg = msg;
    }

    public void setDialogFragmentCallback(ResultDialogFragmentCallback dialogFragmentCallback) {
        this.dialogFragmentCallback = dialogFragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResultDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        populateData();

        binding.btnClose.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            dismissPopupAPI(true, false);
        }
        else if (view == binding.btnShare) {
            dismissPopupAPI(true, true);
        }
    }

    private void populateData() {
        binding.tvMsg.setText(msg);
        binding.tvGroupName.setText(result.getGroupName());
        binding.tvDate.setText(String.format("%s, %s", result.getGameDate(), result.getGameTime()));
        binding.tvTeamA.setText(result.getTeamA().getTeamAName());
        binding.tvTeamB.setText(result.getTeamB().getTeamBName());
        binding.tvTeamAP.setText(result.getTeamAPlayer().getNickName());
        binding.tvTeamBP.setText(result.getTeamBPlayer().getNickName());
        Glide.with(getContext()).load(result.getTeamAPlayer().getEmojiUrl()).into(binding.emojiImgVuP1);
        Glide.with(getContext()).load(result.getTeamBPlayer().getEmojiUrl()).into(binding.emojiImgVuP2);
        Glide.with(getContext()).load(result.getTeamAPlayer().getBibUrl()).into(binding.shirtImgVuP1);
        Glide.with(getContext()).load(result.getTeamBPlayer().getBibUrl()).into(binding.shirtImgVuP2);
        if (result.getTeamAPlayer().getIsCaptain().equalsIgnoreCase("1")) {
            binding.teamACaptain.setVisibility(View.VISIBLE);
        }
        else {
            binding.teamACaptain.setVisibility(View.INVISIBLE);
        }
        if (result.getTeamBPlayer().getIsCaptain().equalsIgnoreCase("1")) {
            binding.teamBCaptain.setVisibility(View.VISIBLE);
        }
        else {
            binding.teamBCaptain.setVisibility(View.INVISIBLE);
        }

        if (result.getTeamA().getStatus().equalsIgnoreCase("WON")) {
            binding.teamAWin.setVisibility(View.VISIBLE);
            binding.teamAWinCup.setVisibility(View.VISIBLE);
            binding.teamAVu.setAlpha(1.0f);
        }
        else if (result.getTeamA().getStatus().equalsIgnoreCase("LOST")) {
            binding.teamAWin.setVisibility(View.INVISIBLE);
            binding.teamAWinCup.setVisibility(View.GONE);
            binding.teamAVu.setAlpha(0.5f);
        }
        else if (result.getTeamA().getStatus().equalsIgnoreCase("DRAW")) {
            binding.teamAWin.setVisibility(View.INVISIBLE);
            binding.teamAWinCup.setVisibility(View.GONE);
            binding.teamAVu.setAlpha(1.0f);
        }

        if (result.getTeamB().getStatus().equalsIgnoreCase("WON")) {
            binding.teamBWin.setVisibility(View.VISIBLE);
            binding.teamBWinCup.setVisibility(View.VISIBLE);
            binding.teamBVu.setAlpha(1.0f);
        }
        else if (result.getTeamB().getStatus().equalsIgnoreCase("LOST")) {
            binding.teamBWin.setVisibility(View.INVISIBLE);
            binding.teamBWinCup.setVisibility(View.GONE);
            binding.teamBVu.setAlpha(0.5f);
        }
        else if (result.getTeamB().getStatus().equalsIgnoreCase("DRAW")) {
            binding.teamBWin.setVisibility(View.INVISIBLE);
            binding.teamBWinCup.setVisibility(View.GONE);
            binding.teamBVu.setAlpha(1.0f);
        }
    }

    private void dismissPopupAPI(boolean isLoader, boolean isShare) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.dismissPopup(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), popupId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isShare) {
                                dialogFragmentCallback.shareClicked(ResultDialogFragment.this);
                            }
                            else {
                                dialogFragmentCallback.onDismiss(ResultDialogFragment.this);
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            dialogFragmentCallback.onDismiss(ResultDialogFragment.this);
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

    public interface ResultDialogFragmentCallback {
        void shareClicked(DialogFragment df);
        void onDismiss(DialogFragment df);
    }
}