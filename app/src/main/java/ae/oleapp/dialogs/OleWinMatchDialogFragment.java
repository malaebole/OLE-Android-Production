package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentWinMatchDialogBinding;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleWinMatchDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentWinMatchDialogBinding binding;
    private WinMatchDialogFragmentCallback dialogCallback;
    private String popupId = "", msg = "";
    private OleMatchResults match;

    public OleWinMatchDialogFragment() {
        // Required empty public constructor
    }

    public OleWinMatchDialogFragment(String popupId, String msg, OleMatchResults matchResult) {
        this.popupId = popupId;
        this.msg = msg;
        this.match = matchResult;
    }

    public void setDialogCallback(WinMatchDialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentWinMatchDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.profileVu1.populateData(match.getPlayerOne().getNickName(), match.getPlayerOne().getPhotoUrl(), match.getPlayerOne().getLevel(), true);
        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
            binding.tvPoints.setText(String.format("%s:%s", match.getPlayerTwo().getGoals(), match.getPlayerOne().getGoals()));
        }
        else {
            binding.tvPoints.setText(String.format("%s:%s", match.getPlayerOne().getGoals(), match.getPlayerTwo().getGoals()));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(match.getMatchDate());
            dateFormat.applyPattern("dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
            binding.tvDate.setText("");
        }
        binding.tvClubName.setText(match.getClubName());
        binding.tvMsg.setText(msg);

        OlePlayerInfo player2 = match.getPlayerTwo();
        // check object null or not
        if (!player2.isEmpty()) {
            binding.profileVu2.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
        }
        else {
            binding.profileVu2.populateData("", "", null, false);
        }

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
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismissPopupAPI(true, false);
        }
        else if (v == binding.btnShare) {
            dismissPopupAPI(true, true);
        }
    }

    private void dismissPopupAPI(boolean isLoader, boolean isShare) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.dismissWinMatch(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), popupId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isShare) {
                                dialogCallback.shareClicked(OleWinMatchDialogFragment.this);
                            }
                            else {
                                dialogCallback.onDismiss(OleWinMatchDialogFragment.this);
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            dialogCallback.onDismiss(OleWinMatchDialogFragment.this);
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

    public interface WinMatchDialogFragmentCallback {
        void shareClicked(DialogFragment df);
        void onDismiss(DialogFragment df);
    }
}