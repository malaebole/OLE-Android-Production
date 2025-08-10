package ae.oleapp.dialogs;

import static ae.oleapp.util.Functions.hideLoader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
//import com.gao.jiefly.abilitychartlibrary.AbilityChatView;
//import com.github.mikephil.charting.data.RadarData;
//import com.github.mikephil.charting.data.RadarDataSet;
//import com.github.mikephil.charting.data.RadarEntry;
//import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentFriendOptionsDialogBinding;
import ae.oleapp.external.RadarView.RadarData;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.PreviewFieldView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendOptionsDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentFriendOptionsDialogBinding binding;
    private FriendOptionsDialogFragmentCallback dialogCallback;

    private boolean isTeam = false, isShowRemovePlayer = false, isShowSubstitute = false, isShowSwap = false;
    private PlayerInfo playerInfo;
    private String teamAName = "", teamBName = "";
    private boolean teamACaptainAvailable = false, teamBCaptainAvailable = false;
    private String dotPosition;
    private FormationTeams TeamA, TeamB;
    List<Integer> layerColor = new ArrayList<>();
    KProgressHUD hud;


    public FriendOptionsDialogFragment() {
        // Required empty public constructor
    }

    public FriendOptionsDialogFragment(boolean isTeam, PlayerInfo playerInfo, String dotPosition, FormationTeams teamA, FormationTeams teamB) {
        this.isTeam = isTeam;
        this.playerInfo = playerInfo;
        this.dotPosition = dotPosition;
        this.TeamA = teamA;
        this.TeamB = teamB;
    }

    public void setData(String teamAName, String teamBName, boolean teamACaptainAvailable, boolean teamBCaptainAvailable, boolean isShowRemovePlayer, boolean isShowSubstitute, boolean isShowSwap) {
        this.teamAName = teamAName;
        this.teamBName = teamBName;
        this.teamACaptainAvailable = teamACaptainAvailable;
        this.teamBCaptainAvailable = teamBCaptainAvailable;
        this.isShowRemovePlayer = isShowRemovePlayer;
        this.isShowSubstitute = isShowSubstitute;
        this.isShowSwap = isShowSwap;
    }

    public void setDialogCallback(FriendOptionsDialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFriendOptionsDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setDimAmount(0.5f);
            getDialog().getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

        }


        getProfileAPI(true, playerInfo.getId(), playerInfo.getFriendShipId());



//        List<Double> data = new ArrayList<>();
//        data.add(90d);
//        data.add(80d);
//        data.add(70d);
//        data.add(60d);
//        data.add(50d);
//        data.add(40d);
//        List<Float> values = new ArrayList<>();
//        Collections.addAll(values, 90f, 80f, 70f, 60f, 50f,40f);
//        RadarData data = new RadarData(values);
//        binding.radarView.addData(data);
//        binding.radarView.setCenterTextColor(Color.parseColor("#00767E"));

        binding.btnClose.setOnClickListener(this);
        binding.btnCaptain.setOnClickListener(this);
        binding.btnRemoveCaptain.setOnClickListener(this);
        binding.btnSubstitute.setOnClickListener(this);
        binding.btnProfile.setOnClickListener(this);
        binding.btnRemove.setOnClickListener(this);
        binding.btnStatus.setOnClickListener(this);
        binding.btnSwap.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);

        return view;
    }
    private float dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void getProfileAPI(boolean isLoader, String playerId, String friendShipID) {
        hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), playerId, friendShipID, Functions.getPrefValue(getContext(),Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Gson gson = new Gson();
                            playerInfo = gson.fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                UserInfo userInfo = gson.fromJson(object.getJSONObject(Constants.kData).toString(), UserInfo.class);
                                Functions.saveUserinfo(getContext(), userInfo);
                                for (int i=0; i<TeamA.getPlayers().size(); i++){
                                    if (TeamA.getPlayers().get(i).getIsCaptain() != null){
                                        if (playerId.equalsIgnoreCase(TeamA.getPlayers().get(i).getId()) && TeamA.getPlayers().get(i).getIsCaptain().equalsIgnoreCase("1")){
                                            playerInfo.setInGame("1");
                                            playerInfo.setInTeam("1");
                                            playerInfo.setIsCaptain("1");
                                        }
                                    }

                                }
                                for (int i=0; i<TeamB.getPlayers().size(); i++){
                                    if (TeamB.getPlayers().get(i).getIsCaptain() != null){
                                        if (playerId.equalsIgnoreCase(TeamB.getPlayers().get(i).getId()) && TeamB.getPlayers().get(i).getIsCaptain().equalsIgnoreCase("1")){
                                            playerInfo.setInGame("1");
                                            playerInfo.setInTeam("1");
                                            playerInfo.setIsCaptain("1");
                                        }
                                    }

                                }
                            }

                            populateDialogData();

                        }
                        else {
                            hideLoader(hud);
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        hideLoader(hud);
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    hideLoader(hud);
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }


    private void populateDialogData(){

        Collections.addAll(layerColor,
                Color.parseColor("#00767E"),
                Color.parseColor("#004C55"),
                Color.parseColor("#00767E"),
                Color.parseColor("#004C55"),
                Color.parseColor("#00767E"));
        binding.radarView.setLayerColor(layerColor);

        List<Integer> res = new ArrayList<>();
        Collections.addAll(res, R.drawable.iq_icon,  R.drawable.fitness_icon,  R.drawable.defence_icon,
                R.drawable.speed_icon,  R.drawable.shooting_icon,  R.drawable.dribble_icon);
        binding.radarView.setVertexIconResid(res);

        List<Float> values = new ArrayList<>();
        Collections.addAll(values, Float.parseFloat(playerInfo.getRatingData().getIqLevel()),
                Float.parseFloat(playerInfo.getRatingData().getFitness()),
                Float.parseFloat(playerInfo.getRatingData().getDefence()),
                Float.parseFloat(playerInfo.getRatingData().getSpeed()),
                Float.parseFloat(playerInfo.getRatingData().getShooting()),
                Float.parseFloat(playerInfo.getRatingData().getDribble()));
        RadarData data = new RadarData(values);
        data.setColor(Color.parseColor("#ff0000"));
        data.setLineWidth(5.0F);
        binding.radarView.addData(data);

        List<String> vertexText = new ArrayList<>();
        Collections.addAll(vertexText,
                "IQ "+playerInfo.getRatingData().getIqLevel()+"%",
                "Fitness "+playerInfo.getRatingData().getFitness()+"%",
                "Defense "+playerInfo.getRatingData().getDefence()+"%",
                "Speed "+playerInfo.getRatingData().getSpeed()+"%",
                "Shooting "+playerInfo.getRatingData().getShooting()+"%",
                "Dribble "+playerInfo.getRatingData().getDribble()+"%");
        binding.radarView.setVertexText(vertexText);

        binding.winProgressbar.setThumbImage(R.drawable.progress_ball, 55);
        binding.skillsProgressbar.setThumbImage(R.drawable.progress_ball, 55);

        binding.winProgressbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        binding.skillsProgressbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        binding.gamecardVu.setVisibility(View.GONE);
        Glide.with(getActivity()).load(playerInfo.getBibUrl()).into(binding.shirtImgVu);
        Glide.with(getActivity()).load(playerInfo.getEmojiUrl()).into(binding.emojiImgVu);
        binding.tvName.setText(playerInfo.getNickName());
        binding.tvLost.setText(playerInfo.getMatchLoss());
        binding.tvWin.setText(playerInfo.getMatchWon());
        binding.tvDraw.setText(playerInfo.getMatchDrawn());
        binding.tvPlayed.setText(playerInfo.getMatchPlayed());
        binding.tvTotalMatch.setText(playerInfo.getMatchPlayed());
        binding.tvTotalRedYellow.setText(playerInfo.getCardCount());

        if (playerInfo !=null){
            if (dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase("")){
                if (playerInfo.getFreindshipStatus().equalsIgnoreCase("linked")){
                    binding.btnEdit.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("followed")){
                    binding.btnEdit.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("unlinked")){ //manual player
                    binding.btnEdit.setVisibility(View.VISIBLE);

                } if (playerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                    binding.btnEdit.setVisibility(View.VISIBLE);

                }
            }else{
                if (playerInfo.getFreindshipStatus().equalsIgnoreCase("linked")){
                    binding.btnEdit.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("followed")){
                    binding.btnEdit.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("unlinked")){ //manual player

                    binding.btnEdit.setVisibility(View.GONE);

                } if (playerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                    binding.btnEdit.setVisibility(View.GONE);
                }
            }
        }

        if (playerInfo.getWinPercentage().equalsIgnoreCase("0")) {
            binding.winProgressbar.setProgress(0);
            binding.tvWinPerc.setText("0%");
            binding.tvPercentage.setText("0%");
        }
        else {
            binding.winProgressbar.setProgress((int) Float.parseFloat(playerInfo.getWinPercentage()));
            binding.tvWinPerc.setText(String.format("%s%%", playerInfo.getWinPercentage()));
            binding.tvPercentage.setText(String.format("%s%%", playerInfo.getWinPercentage()));
        }
        if (playerInfo.getPlayingSkills().equalsIgnoreCase("0")) {
            binding.skillsProgressbar.setProgress(0);
            binding.tvPlayingSkills.setText("0%");
        }
        else {
            binding.skillsProgressbar.setProgress(Integer.parseInt(playerInfo.getPlayingSkills()));
            binding.tvPlayingSkills.setText(String.format("%s%%", playerInfo.getPlayingSkills()));
        }

        if (isTeam) {
            if (isShowRemovePlayer) {
                binding.btnRemove.setVisibility(View.VISIBLE);
            }
            else {
                binding.btnRemove.setVisibility(View.GONE);
            }
            if (isShowSwap) {
                binding.btnSwap.setVisibility(View.VISIBLE);
            }
            else {
                binding.btnSwap.setVisibility(View.GONE);
            }
            binding.btnStatus.setVisibility(View.VISIBLE);
            binding.btnCaptain.setVisibility(View.GONE);
            if (playerInfo.getIsCaptain() != null && playerInfo.getIsCaptain().equalsIgnoreCase("1")) {
                binding.btnRemoveCaptain.setVisibility(View.VISIBLE);
            }
            else {
                binding.btnRemoveCaptain.setVisibility(View.GONE);
            }
        }
        else {
            if (playerInfo.getIsLink().equalsIgnoreCase("1")) {
                binding.btnCaptain.setVisibility(View.VISIBLE);
            }
            else {
                binding.btnCaptain.setVisibility(View.GONE);
            }
            binding.btnRemoveCaptain.setVisibility(View.GONE);
            binding.btnRemove.setVisibility(View.GONE);
            binding.btnSwap.setVisibility(View.GONE);
            binding.btnStatus.setVisibility(View.GONE);
        }

        if (isShowSubstitute) {
            binding.btnSubstitute.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnSubstitute.setVisibility(View.GONE);
        }

        if (playerInfo.getCardType() !=null && !playerInfo.getCardType().equalsIgnoreCase("")){
            if (playerInfo.getCardType().equalsIgnoreCase("red")){
                binding.gamecardVu.setVisibility(View.VISIBLE);
                binding.colorCardVu.setBackgroundColor(Color.parseColor("#f02301"));
            }else {
                binding.gamecardVu.setVisibility(View.VISIBLE);
                binding.colorCardVu.setBackgroundColor(Color.parseColor("#ffe200"));
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                hideLoader(hud);
            }
        }, 1500);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnCaptain) {
            dialogCallback.makeCaptain(this);
        }
        else if (v == binding.btnRemoveCaptain) {
            dialogCallback.removeCaptain(this);
        }
        else if (v == binding.btnSubstitute) {
            dialogCallback.substitute(this);
        }
        else if (v == binding.btnProfile) {
            dialogCallback.profile(this);
        }
        else if (v == binding.btnRemove) {
            dialogCallback.remove(this);
        }
        else if (v == binding.btnStatus) {
            dialogCallback.status(this);
        }
        else if (v == binding.btnSwap) {
            dialogCallback.swapPlayer(this);
        }
        else if (v == binding.btnEdit){
            dialogCallback.manualEdit(this);
        }

    }
    public interface FriendOptionsDialogFragmentCallback {
        void makeCaptain(DialogFragment df);
        void removeCaptain(DialogFragment df);
        void substitute(DialogFragment df);
        void profile(DialogFragment df);
        void remove(DialogFragment df);
        void status(DialogFragment df);
        void swapPlayer(DialogFragment df);
        void manualEdit(DialogFragment df);
    }
}