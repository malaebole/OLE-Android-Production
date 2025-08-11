package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentGameCaptainDialogBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameCaptainDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentGameCaptainDialogBinding binding;
    private String popupId = "", msg = "", type = "";
    private GameCaptainDialogFragmentCallback dialogFragmentCallback;

    public GameCaptainDialogFragment() {
        // Required empty public constructor
    }

    public GameCaptainDialogFragment(String popupId, String msg, String type) {
        this.popupId = popupId;
        this.msg = msg;
        this.type = type;
    }

    public void setDialogFragmentCallback(GameCaptainDialogFragmentCallback dialogFragmentCallback) {
        this.dialogFragmentCallback = dialogFragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGameCaptainDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvMsg.setText(msg);
        if (type.equalsIgnoreCase("captainRemoved")) {
            binding.tvGame.setText(R.string.dismiss);
        }
        else {
            binding.tvGame.setText(R.string.go_to_game);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnGame.setOnClickListener(this);

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
        else if (view == binding.btnGame) {
            dismissPopupAPI(true, true);
        }
    }

    private void dismissPopupAPI(boolean isLoader, boolean isGame) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.dismissPopup(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), popupId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isGame) {
                                dialogFragmentCallback.gameClicked(GameCaptainDialogFragment.this);
                            }
                            else {
                                dismiss();
                            }
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

    public interface GameCaptainDialogFragmentCallback {
        void gameClicked(DialogFragment df);
    }
}