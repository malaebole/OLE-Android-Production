package ae.oleapp.dialogs;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentGameResultDialogBinding;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Team;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameResultDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentGameResultDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String winnerTeamId = "", loserTeamId = "", isDraw = "0";
    private GameTeam gameTeam;
    private FormationTeams teamA;
    private FormationTeams teamB;

    public GameResultDialogFragment() {
        // Required empty public constructor
    }

    public GameResultDialogFragment(GameTeam gameTeam, FormationTeams teamA, FormationTeams teamB) {
        this.gameTeam = gameTeam;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGameResultDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding.tvTeamA.setText(teamA.getTeamName());
        //binding.tvTeamA.setText(gameTeam.getTeamAName());
        binding.tvTeamB.setText(teamB.getTeamName());
        //binding.tvTeamB.setText(gameTeam.getTeamBName());
        Glide.with(getActivity()).load(teamA.getTeamShirt()).into(binding.shirtImgVuP1);
        Glide.with(getActivity()).load(teamB.getTeamShirt()).into(binding.shirtImgVuP2);
        if (teamA.getPlayers().size() > 0) {
            PlayerInfo info = teamA.getPlayers().get(0);
            binding.tvTeamAP.setText(info.getNickName());
            Glide.with(getActivity()).load(info.getEmojiUrl()).into(binding.emojiImgVuP1);
        }
        if (teamB.getPlayers().size() > 0) {
            PlayerInfo info = teamB.getPlayers().get(0);
            binding.tvTeamBP.setText(info.getNickName());
            Glide.with(getActivity()).load(info.getEmojiUrl()).into(binding.emojiImgVuP2);
        }

        binding.btnClose.setVisibility(View.INVISIBLE);
        binding.btnClose.setOnClickListener(this);
        binding.btnDraw.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.teamAVu.setOnClickListener(this);
        binding.teamBVu.setOnClickListener(this);

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
            dismiss();
        }
        else if (v == binding.btnDraw) {
            isDraw = "1";
            winnerTeamId = teamA.getId();
            loserTeamId = teamB.getId();
            binding.teamAVu.setStrokeColor(getResources().getColor(R.color.transparent));
            binding.teamBVu.setStrokeColor(getResources().getColor(R.color.transparent));
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.match_draw))
                    .setMessage(getResources().getString(R.string.are_you_sure_this_match_is_draw))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            submitResultAPI(true, "0");
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
        else if (v == binding.btnSubmit) {
            if (winnerTeamId.isEmpty() || loserTeamId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_winner_team), FancyToast.ERROR);
                return;
            }
            submitResultAPI(true, "0");
        }
        else if (v == binding.teamAVu) {
            isDraw = "0";
            winnerTeamId = teamA.getId();
            loserTeamId = teamB.getId();
            binding.teamAVu.setStrokeColor(getResources().getColor(R.color.yellowColor));
            binding.teamBVu.setStrokeColor(getResources().getColor(R.color.transparent));
        }
        else if (v == binding.teamBVu) {
            isDraw = "0";
            winnerTeamId = teamA.getId();
            loserTeamId = teamB.getId();
            binding.teamAVu.setStrokeColor(getResources().getColor(R.color.transparent));
            binding.teamBVu.setStrokeColor(getResources().getColor(R.color.yellowColor));
        }
        else if (v == binding.btnCancel) {
            isDraw = "0";
            winnerTeamId = teamA.getId();
            loserTeamId = teamB.getId();
            binding.teamAVu.setStrokeColor(getResources().getColor(R.color.transparent));
            binding.teamBVu.setStrokeColor(getResources().getColor(R.color.transparent));
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.match_cancelled))
                    .setMessage(getResources().getString(R.string.are_you_sure_this_match_is_cancelled))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            submitResultAPI(true, "1");
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }

    private void submitResultAPI(boolean isLoader, String cancel) {


        dialogCallback.didSubmitResult(GameResultDialogFragment.this, gameTeam.getGameId(), winnerTeamId, loserTeamId, isDraw, cancel.equalsIgnoreCase("1"));

//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addScore(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), gameTeam.getGameId(), winnerTeamId, loserTeamId, isDraw, cancel);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            dialogCallback.didSubmitResult(GameResultDialogFragment.this, cancel.equalsIgnoreCase("1"));
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df,String gameId, String winnerTeamId,String loserTeamId,String isDraw, boolean isCancelled);
    }
}