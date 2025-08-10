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

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentPadelWinMatchDialogBinding;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OlePadelWinMatchDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentPadelWinMatchDialogBinding binding;
    private String popupId = "", msg = "";
    private OlePadelMatchResults match;
    private OleWinMatchDialogFragment.WinMatchDialogFragmentCallback dialogCallback;

    public OlePadelWinMatchDialogFragment() {
        // Required empty public constructor
    }

    public OlePadelWinMatchDialogFragment(String popupId, String msg, OlePadelMatchResults match) {
        this.popupId = popupId;
        this.msg = msg;
        this.match = match;
    }

    public void setDialogCallback(OleWinMatchDialogFragment.WinMatchDialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPadelWinMatchDialogBinding.inflate(inflater, container, false);
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
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismissPopupAPI(true, false);
        }
        else if (v == binding.btnShare) {
            dismissPopupAPI(true, true);
        }
    }

    private void populateData() {
        binding.tvMsg.setText(msg);
        binding.tvClubName.setText(match.getClubName());
        binding.myProfileVu.populateData(match.getCreatedBy().getNickName(), match.getCreatedBy().getPhotoUrl(), match.getCreatedBy().getLevel(), true);
        binding.myPartnerProfileVu.populateData(match.getCreatorPartner().getNickName(), match.getCreatorPartner().getPhotoUrl(), match.getCreatorPartner().getLevel(), true);
        binding.tvDate.setText(match.getMatchDate());
        if (match.getCreatorScore().getSetOne() == 1) {
            binding.imgTeamASet1.setImageResource(R.drawable.set_one_green);
        }
        else {
            binding.imgTeamASet1.setImageResource(R.drawable.set_one_red);
        }
        if (match.getCreatorScore().getSetTwo() == 1) {
            binding.imgTeamASet2.setImageResource(R.drawable.set_two_green);
        }
        else {
            binding.imgTeamASet2.setImageResource(R.drawable.set_two_red);
        }
        if (match.getCreatorScore().getSetThree() == 1) {
            binding.imgTeamASet3.setImageResource(R.drawable.set_three_green);
        }
        else {
            binding.imgTeamASet3.setImageResource(R.drawable.set_three_red);
        }

        if (match.getPlayerTwoScore().getSetOne() == 1) {
            binding.imgTeamBSet1.setImageResource(R.drawable.set_one_green);
        }
        else {
            binding.imgTeamBSet1.setImageResource(R.drawable.set_one_red);
        }
        if (match.getPlayerTwoScore().getSetTwo() == 1) {
            binding.imgTeamBSet2.setImageResource(R.drawable.set_two_green);
        }
        else {
            binding.imgTeamBSet2.setImageResource(R.drawable.set_two_red);
        }
        if (match.getPlayerTwoScore().getSetThree() == 1) {
            binding.imgTeamBSet3.setImageResource(R.drawable.set_three_green);
        }
        else {
            binding.imgTeamBSet3.setImageResource(R.drawable.set_three_red);
        }

        if (match.getCreatorWin().equalsIgnoreCase("1")) {
            binding.winnerBadge1.setImageResource(R.drawable.match_winner_badge);
        }
        else {
            binding.winnerBadge1.setImageResource(R.drawable.match_loser_badge);
        }

        binding.opponentProfileVu.populateData(match.getPlayerTwo().getNickName(), match.getPlayerTwo().getPhotoUrl(), match.getPlayerTwo().getLevel(), true);
        binding.opponentPartnerProfileVu.populateData(match.getPlayerTwoPartner().getNickName(), match.getPlayerTwoPartner().getPhotoUrl(), match.getPlayerTwoPartner().getLevel(), true);
        if (match.getPlayerTwoWin().equalsIgnoreCase("1")) {
            binding.winnerBadge2.setImageResource(R.drawable.match_winner_badge);
        }
        else {
            binding.winnerBadge2.setImageResource(R.drawable.match_loser_badge);
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
                                dialogCallback.shareClicked(OlePadelWinMatchDialogFragment.this);
                            }
                            else {
                                dialogCallback.onDismiss(OlePadelWinMatchDialogFragment.this);
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            dialogCallback.onDismiss(OlePadelWinMatchDialogFragment.this);
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
}