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
import ae.oleapp.databinding.FragmentPlayerStatusDialogBinding;
import  ae.oleapp.models.PlayerInfo;
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

public class PlayerStatusDialogFragment extends DialogFragment implements View.OnClickListener {

    private PlayerStatusDialogFragmentCallback dialogCallback;
    private FragmentPlayerStatusDialogBinding binding;
    private PlayerInfo playerInfo;
    private String selectedStatus = "";

    public PlayerStatusDialogFragment() {
        // Required empty public constructor
    }

    public PlayerStatusDialogFragment(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public void setDialogCallback(PlayerStatusDialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlayerStatusDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Glide.with(getActivity()).load(playerInfo.getBibUrl()).into(binding.shirtImgVu);
        Glide.with(getActivity()).load(playerInfo.getEmojiUrl()).into(binding.emojiImgVu);
        binding.tvName.setText(playerInfo.getNickName());

        binding.btnClose.setOnClickListener(this);
        binding.relWin.setOnClickListener(this);
        binding.relLost.setOnClickListener(this);
        binding.relPlayed.setOnClickListener(this);
        binding.relSkills.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.relWin) {
            selectedStatus = "win";
            binding.imgWin.setImageResource(R.drawable.rating_check);
            binding.tvWin.setTextColor(getResources().getColor(R.color.yellowColor));
            binding.imgLost.setImageResource(R.drawable.rating_uncheck);
            binding.tvLost.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgPlayed.setImageResource(R.drawable.rating_uncheck);
            binding.tvPlayed.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgSkills.setImageResource(R.drawable.rating_uncheck);
            binding.tvSkills.setTextColor(Color.parseColor("#99FFFFFF"));
        }
        else if (v == binding.relLost) {
            selectedStatus = "loss";
            binding.imgWin.setImageResource(R.drawable.rating_uncheck);
            binding.tvWin.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgLost.setImageResource(R.drawable.rating_check);
            binding.tvLost.setTextColor(getResources().getColor(R.color.yellowColor));
            binding.imgPlayed.setImageResource(R.drawable.rating_uncheck);
            binding.tvPlayed.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgSkills.setImageResource(R.drawable.rating_uncheck);
            binding.tvSkills.setTextColor(Color.parseColor("#99FFFFFF"));
        }
        else if (v == binding.relPlayed) {
            selectedStatus = "played";
            binding.imgWin.setImageResource(R.drawable.rating_uncheck);
            binding.tvWin.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgLost.setImageResource(R.drawable.rating_uncheck);
            binding.tvLost.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgPlayed.setImageResource(R.drawable.rating_check);
            binding.tvPlayed.setTextColor(getResources().getColor(R.color.yellowColor));
            binding.imgSkills.setImageResource(R.drawable.rating_uncheck);
            binding.tvSkills.setTextColor(Color.parseColor("#99FFFFFF"));
        }
        else if (v == binding.relSkills) {
            selectedStatus = "skills";
            binding.imgWin.setImageResource(R.drawable.rating_uncheck);
            binding.tvWin.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgLost.setImageResource(R.drawable.rating_uncheck);
            binding.tvLost.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgPlayed.setImageResource(R.drawable.rating_uncheck);
            binding.tvPlayed.setTextColor(Color.parseColor("#99FFFFFF"));
            binding.imgSkills.setImageResource(R.drawable.rating_check);
            binding.tvSkills.setTextColor(getResources().getColor(R.color.yellowColor));
        }
        else if (v == binding.btnSubmit) {
            if (selectedStatus.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_status), FancyToast.ERROR);
                return;
            }
            setPlayerStatusAPI(true);
        }
    }

    private void setPlayerStatusAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.playerStatus(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerInfo.getId(), playerInfo.getFriendShipId(), selectedStatus);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dialogCallback.statusDone(PlayerStatusDialogFragment.this);
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    public interface PlayerStatusDialogFragmentCallback {
        void statusDone(DialogFragment df);
    }
}