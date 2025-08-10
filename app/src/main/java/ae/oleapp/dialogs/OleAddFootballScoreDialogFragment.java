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

import ae.oleapp.databinding.OlefragmentAddFootballScoreDialogBinding;
import ae.oleapp.models.OleMatchScore;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleAddFootballScoreDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentAddFootballScoreDialogBinding binding;
    private UserInfo playerOne;
    private OlePlayerInfo playerTwo;
    private OleMatchScore oleMatchScore;
    private String bookingId = "";
    private AddScoreDialogCallback dialogCallback;

    public OleAddFootballScoreDialogFragment() {
        // Required empty public constructor
    }

    public OleAddFootballScoreDialogFragment(UserInfo playerOne, OlePlayerInfo playerTwo, String bookingId, OleMatchScore oleMatchScore) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.bookingId = bookingId;
        this.oleMatchScore = oleMatchScore;
    }

    public void setDialogCallback(AddScoreDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddFootballScoreDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvTeamA.setText(String.format("%s(%s)", getString(R.string.team_a), playerOne.getNickName()));
        binding.tvTeamB.setText(String.format("%s(%s)", getString(R.string.team_b), playerTwo.getNickName()));
        if (oleMatchScore != null) {
            binding.etTeamAScore.setText(oleMatchScore.getFootballTeamA());
            binding.etTeamBScore.setText(oleMatchScore.getFootballTeamB());
            binding.etTeamAScore.setEnabled(false);
            binding.etTeamBScore.setEnabled(false);
            binding.btnAdd.setVisibility(View.GONE);
        }

        binding.btnAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnAdd) {
            if (binding.etTeamAScore.getText().toString().isEmpty() || binding.etTeamBScore.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_score), FancyToast.ERROR);
                return;
            }
            addScoreAPI(true, binding.etTeamAScore.getText().toString(), binding.etTeamBScore.getText().toString());
        }
    }

    private void addScoreAPI(boolean isLoader, String teamAScore, String teamBScore) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addFootballScore(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerOne.getId(), playerTwo.getId(), bookingId, teamAScore, teamBScore);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            OleMatchScore score = new OleMatchScore();
                            score.setFootballTeamA(teamAScore);
                            score.setFootballTeamB(teamBScore);
                            dialogCallback.scoreAdded(score);
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

    public interface AddScoreDialogCallback {
        void scoreAdded(OleMatchScore oleMatchScore);
    }
}