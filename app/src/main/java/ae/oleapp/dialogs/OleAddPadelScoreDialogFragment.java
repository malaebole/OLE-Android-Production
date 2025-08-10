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
import ae.oleapp.databinding.OlefragmentAddPadelScoreDialogBinding;
import ae.oleapp.models.OleMatchScore;
import ae.oleapp.models.OlePadelScore;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OleAddPadelScoreDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentAddPadelScoreDialogBinding binding;
    private UserInfo playerOne;
    private OlePlayerInfo playerTwo;
    private OleMatchScore oleMatchScore;
    private String bookingId = "";
    private String teamASet1Score = "", teamASet2Score = "", teamASet3Score = "", teamBSet1Score = "", teamBSet2Score = "", teamBSet3Score = "";
    private OleAddFootballScoreDialogFragment.AddScoreDialogCallback dialogCallback;

    public OleAddPadelScoreDialogFragment() {
        // Required empty public constructor
    }

    public OleAddPadelScoreDialogFragment(UserInfo playerOne, OlePlayerInfo playerTwo, OleMatchScore oleMatchScore, String bookingId) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.oleMatchScore = oleMatchScore;
        this.bookingId = bookingId;
    }

    public void setDialogCallback(OleAddFootballScoreDialogFragment.AddScoreDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddPadelScoreDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvTeamA.setText(String.format("%s(%s)", getString(R.string.team_a), playerOne.getNickName()));
        binding.tvTeamB.setText(String.format("%s(%s)", getString(R.string.team_b), playerTwo.getNickName()));
        if (oleMatchScore != null) {
            if (oleMatchScore.getPadelTeamA().getSetOne() == 1) {
                binding.imgTeamASet1.setImageResource(R.drawable.check);
            }
            if (oleMatchScore.getPadelTeamA().getSetTwo() == 1) {
                binding.imgTeamASet2.setImageResource(R.drawable.check);
            }
            if (oleMatchScore.getPadelTeamA().getSetThree() == 1) {
                binding.imgTeamASet3.setImageResource(R.drawable.check);
            }
            if (oleMatchScore.getPadelTeamB().getSetOne() == 1) {
                binding.imgTeamBSet1.setImageResource(R.drawable.check);
            }
            if (oleMatchScore.getPadelTeamB().getSetTwo() == 1) {
                binding.imgTeamBSet2.setImageResource(R.drawable.check);
            }
            if (oleMatchScore.getPadelTeamB().getSetThree() == 1) {
                binding.imgTeamBSet3.setImageResource(R.drawable.check);
            }
            binding.btnAdd.setVisibility(View.GONE);
        }
        else {
            binding.vuTeamASet1.setOnClickListener(this);
            binding.vuTeamASet2.setOnClickListener(this);
            binding.vuTeamASet3.setOnClickListener(this);
            binding.vuTeamBSet1.setOnClickListener(this);
            binding.vuTeamBSet2.setOnClickListener(this);
            binding.vuTeamBSet3.setOnClickListener(this);
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
            if (teamASet1Score.equalsIgnoreCase("") && teamASet2Score.equalsIgnoreCase("") && teamASet3Score.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.enter_score), FancyToast.ERROR);
                return;
            }
            if (teamBSet1Score.equalsIgnoreCase("") && teamBSet2Score.equalsIgnoreCase("") && teamBSet3Score.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.enter_score), FancyToast.ERROR);
                return;
            }
            addScoreAPI(true);
        }
        else if (view == binding.vuTeamASet1) {
            if (teamASet1Score.isEmpty() || teamASet1Score.equalsIgnoreCase("0")) {
                teamASet1Score = "1";
                binding.imgTeamASet1.setImageResource(R.drawable.check);
                teamBSet1Score = "0";
                binding.imgTeamBSet1.setImageResource(R.drawable.uncheck);
            }
        }
        else if (view == binding.vuTeamASet2) {
            if (teamASet2Score.isEmpty() || teamASet2Score.equalsIgnoreCase("0")) {
                teamASet2Score = "1";
                binding.imgTeamASet2.setImageResource(R.drawable.check);
                teamBSet2Score = "0";
                binding.imgTeamBSet2.setImageResource(R.drawable.uncheck);
            }
        }
        else if (view == binding.vuTeamASet3) {
            if (teamASet3Score.isEmpty() || teamASet3Score.equalsIgnoreCase("0")) {
                teamASet3Score = "1";
                binding.imgTeamASet3.setImageResource(R.drawable.check);
                teamBSet3Score = "0";
                binding.imgTeamBSet3.setImageResource(R.drawable.uncheck);
            }
        }
        else if (view == binding.vuTeamBSet1) {
            if (teamBSet1Score.isEmpty() || teamBSet1Score.equalsIgnoreCase("0")) {
                teamBSet1Score = "1";
                binding.imgTeamBSet1.setImageResource(R.drawable.check);
                teamASet1Score = "0";
                binding.imgTeamASet1.setImageResource(R.drawable.uncheck);
            }
        }
        else if (view == binding.vuTeamBSet2) {
            if (teamBSet2Score.isEmpty() || teamBSet2Score.equalsIgnoreCase("0")) {
                teamBSet2Score = "1";
                binding.imgTeamBSet2.setImageResource(R.drawable.check);
                teamASet2Score = "0";
                binding.imgTeamASet2.setImageResource(R.drawable.uncheck);
            }
        }
        else if (view == binding.vuTeamBSet3) {
            if (teamBSet3Score.isEmpty() || teamBSet3Score.equalsIgnoreCase("0")) {
                teamBSet3Score = "1";
                binding.imgTeamBSet3.setImageResource(R.drawable.check);
                teamASet3Score = "0";
                binding.imgTeamASet3.setImageResource(R.drawable.uncheck);
            }
        }
    }

    private void addScoreAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPadelScore(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerOne.getId(), playerTwo.getId(), teamASet1Score, teamASet2Score, teamASet3Score, teamBSet1Score, teamBSet2Score, teamBSet3Score);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            OlePadelScore teamA = new OlePadelScore();
                            teamA.setSetOne(Integer.parseInt(teamASet1Score));
                            teamA.setSetTwo(Integer.parseInt(teamASet2Score));
                            teamA.setSetThree(Integer.parseInt(teamASet3Score));
                            OlePadelScore teamB = new OlePadelScore();
                            teamB.setSetOne(Integer.parseInt(teamBSet1Score));
                            teamB.setSetTwo(Integer.parseInt(teamBSet2Score));
                            teamB.setSetThree(Integer.parseInt(teamBSet3Score));
                            OleMatchScore score = new OleMatchScore();
                            score.setPadelTeamA(teamA);
                            score.setPadelTeamB(teamB);
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
}