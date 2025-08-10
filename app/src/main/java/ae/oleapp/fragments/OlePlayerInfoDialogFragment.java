package ae.oleapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OlefragmentProfileInfoBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerInfoDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentProfileInfoBinding binding;
    private String playerId = "";
    private String bookingId = "";
    private OlePlayerInfo olePlayerInfo;
    private PlayerInfoDialogFragmentCallback fragmentCallback;
    private Context context;

    public OlePlayerInfoDialogFragment() {
    }

    public OlePlayerInfoDialogFragment(Context context, String playerId, String bookingId) {
        this.playerId = playerId;
        this.bookingId = bookingId;
        this.context = context;
    }

    public void setFragmentCallback(PlayerInfoDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OlefragmentProfileInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.phoneVu.setVisibility(View.GONE);

        getProfileAPI(true);

        binding.btnFav.setOnClickListener(this);
        binding.phoneVu.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (fragmentCallback != null) {
            fragmentCallback.OnDismissListener();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnFav) {
            favClicked();
        }
        else if (v == binding.phoneVu) {
            phoneClicked();
        }
    }

    private void favClicked() {
        if (olePlayerInfo == null) { return; }
        if (olePlayerInfo.getFavorite().equalsIgnoreCase("1")) {
            addRemoveFav(true, olePlayerInfo.getId(), "0");
        }
        else {
            addRemoveFav(true, olePlayerInfo.getId(), "1");
        }
    }

    private  void phoneClicked() {
        if (olePlayerInfo == null) { return; }
        ((BaseActivity)getActivity()).makeCall(olePlayerInfo.getPhone());
    }

    private void populateData() {
        if (olePlayerInfo == null) { return; }
        Glide.with(context).load(olePlayerInfo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.imgVu);
        if (olePlayerInfo.getLevel() != null && !olePlayerInfo.getLevel().isEmpty()) {
            if (olePlayerInfo.getLevel().getValue().equalsIgnoreCase("1")) {
                binding.imgVuRank.setVisibility(View.VISIBLE);
                binding.imgVuRank.setImageResource(R.drawable.rank_badge_one);
            } else if (olePlayerInfo.getLevel().getValue().equalsIgnoreCase("2")) {
                binding.imgVuRank.setVisibility(View.VISIBLE);
                binding.imgVuRank.setImageResource(R.drawable.rank_badge_two);
            } else if (olePlayerInfo.getLevel().getValue().equalsIgnoreCase("3")) {
                binding.imgVuRank.setVisibility(View.VISIBLE);
                binding.imgVuRank.setImageResource(R.drawable.rank_badge_three);
            } else {
                binding.imgVuRank.setVisibility(View.GONE);
            }
        } else {
            binding.imgVuRank.setVisibility(View.GONE);
        }

        if (olePlayerInfo.getNickName().isEmpty()) {
            binding.tvName.setText(olePlayerInfo.getName());
        }
        else {
            binding.tvName.setText(String.format("%s (%s)", olePlayerInfo.getName(), olePlayerInfo.getNickName()));
        }
        if (olePlayerInfo.getWinPercentage().isEmpty()) {
            binding.tvWinPerc.setText("0%");
        }
        else {
            binding.tvWinPerc.setText(String.format("%s%%", olePlayerInfo.getWinPercentage()));
        }

        if (olePlayerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(context, Constants.kUserID)) || Functions.getPrefValue(context, Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            binding.favVu.setVisibility(View.GONE);
        }
        else {
            binding.favVu.setVisibility(View.VISIBLE);
            if (olePlayerInfo.getFavorite().equalsIgnoreCase("1")) {
                binding.tvFav.setText(R.string.remove_from_fav);
                binding.btnFav.setImageResource(R.drawable.fav_green);
            }
            else {
                binding.tvFav.setText(R.string.add_to_fav);
                binding.btnFav.setImageResource(R.drawable.club_unfav);
            }
        }

        binding.tvPlayed.setText(olePlayerInfo.getMatchPlayed());
        binding.tvWon.setText(olePlayerInfo.getMatchWon());
        binding.tvFriendly.setText(olePlayerInfo.getFriendlyGames());
        binding.tvDrawM.setText(olePlayerInfo.getMatchDrawn());
        binding.tvLostM.setText(olePlayerInfo.getMatchLoss());
        binding.tvPoints.setText(olePlayerInfo.getPoints());
        binding.tvPhone.setText(olePlayerInfo.getPhone());
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(context, "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(context), playerId,"", Functions.getPrefValue(getContext(),Constants.kAppModule));
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
                            olePlayerInfo = gson.fromJson(obj.toString(), OlePlayerInfo.class);
                            populateData();
                        }
                        else {
                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(context, e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(context, getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(context, getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(context, t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void addRemoveFav(boolean isLoader, String playerId, String status) {
        KProgressHUD hud = Functions.showLoader(context, "Image processing");
        ((BaseActivity)getActivity()).addRemoveFavAPI(playerId, status, "player", new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(context, msg, FancyToast.SUCCESS);
                    olePlayerInfo.setFavorite(status);
                    if (status.equalsIgnoreCase("1")) {
                        binding.tvFav.setText(R.string.remove_from_fav);
                        binding.btnFav.setImageResource(R.drawable.fav_green);
                    }
                    else {
                        binding.tvFav.setText(R.string.add_to_fav);
                        binding.btnFav.setImageResource(R.drawable.club_unfav);
                    }
                }
                else {
                    Functions.showToast(context, msg, FancyToast.ERROR);
                }
            }
        });
    }

    public interface PlayerInfoDialogFragmentCallback {
        void OnDismissListener();
    }
}
