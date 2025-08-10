package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentSwapPlayerDialogBinding;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.socket.SocketManager;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.PreviewFieldView;
import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SwapPlayerDialogFragment extends DialogFragment {

    private FragmentSwapPlayerDialogBinding binding;
    private SwapPlayerDialogFragmentCallback dialogCallback;
    private GameTeam gameTeam;
    private Drawable bgImg, fieldImg;
    private int teamAVuWidth = 0, teamAVuHeight = 0;
    private float subVuH = 0, subVuW = 0;
    private String playerId = "", playerTeamId = "",playerFriendShipId="", isCaptain="";
    private FormationTeams teamA, teamB;
    private Socket socket;
    private SocketManager socketManager;

    public SwapPlayerDialogFragment() {
        // Required empty public constructor
    }

    public SwapPlayerDialogFragment(GameTeam gameTeam, FormationTeams teamA, FormationTeams teamB, Drawable bgImg, Drawable fieldImg, String playerId, String playerFriendShipId, String playerTeamId, String isCaptain) {
        this.gameTeam = gameTeam;
        this.bgImg = bgImg;
        this.fieldImg = fieldImg;
        this.playerId = playerId;
        this.playerTeamId = playerTeamId;
        this.playerFriendShipId = playerFriendShipId;
        this.teamA = teamA;
        this.teamB = teamB;
        this.isCaptain = isCaptain;
    }

    public void setDialogCallback(SwapPlayerDialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSwapPlayerDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        socketManager = SocketManager.getInstance();
        socket = socketManager.getSocket();

        binding.bgImgVu.setImageDrawable(bgImg);
        binding.fieldImgVu.setImageDrawable(fieldImg);

        binding.vuTeamA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamA.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                teamAVuWidth = binding.vuTeamA.getWidth();
                teamAVuHeight = binding.vuTeamA.getHeight();
                PreviewFieldView fieldView = new PreviewFieldView(getContext());
                binding.vuTeamA.addView(fieldView);
                fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        subVuW = fieldView.getWidth();
                        subVuH = fieldView.getHeight();
                        binding.vuTeamA.removeView(fieldView);
                        if (playerTeamId.equalsIgnoreCase(teamA.getId())) {
                            binding.tvTitle.setText(String.format("%s %s", teamB.getTeamName(), getString(R.string.players)));
                            for (PlayerInfo info : teamB.getPlayers()) {
                                replaceViewTeam(info);
                            }
                        }
                        else {
                            binding.tvTitle.setText(String.format("%s %s", teamA.getTeamName(), getString(R.string.players)));
                            for (PlayerInfo info : teamA.getPlayers()) {
                                replaceViewTeam(info);
                            }
                        }
                    }
                });
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void replaceViewTeam(PlayerInfo info) {
//        PreviewFieldView fieldViewA = new PreviewFieldView(getContext());
        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
        populateDataInTeamAVu(fieldViewA, info, teamAVuWidth, teamAVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * teamAVuWidth ; //((BaseActivity)getActivity()).getScreenWidth();
            float actualYValue = yValue * teamAVuHeight; //((BaseActivity)getActivity()).getScreenHeight();
            setViewMargin(params, actualXValue, actualYValue);
            binding.vuTeamA.addView(fieldViewA, params);
        }
    }

    private void setViewMargin(RelativeLayout.LayoutParams params, float xValue, float yValue) {
        params.leftMargin = (int) xValue;
        if (teamAVuWidth-params.leftMargin < subVuW) {
            params.leftMargin = teamAVuWidth - (int) subVuW;
        }
        params.topMargin = (int) yValue;
        if (teamAVuHeight-params.topMargin < subVuH) {
            params.topMargin = teamAVuHeight - (int) subVuH;
        }
    }

    private void populateDataInTeamAVu(PreviewFieldView viewA, PlayerInfo playerInfo, int viewWidth, int viewHeight) {
        viewA.setParentViewSize(viewWidth, viewHeight);
       if (playerTeamId.equalsIgnoreCase(teamA.getId())){
           viewA.setPlayerInfo(playerInfo, teamB.getTeamShirt(), teamB.getTeamGkShirt());
       }else{
           viewA.setPlayerInfo(playerInfo, teamA.getTeamShirt(), teamA.getTeamGkShirt());
       }
        viewA.setPreviewFieldItemCallback(new PreviewFieldView.PreviewFieldItemCallback() {
            @Override
            public void itemClicked(PlayerInfo playerInfo) {
                if (playerTeamId.equalsIgnoreCase(teamA.getId())) {
                    if (isCaptain !=null && isCaptain.equalsIgnoreCase("1")){
                        Functions.showToast(getContext(),"You cannot Swap Captains", FancyToast.ERROR);
                    }else if (playerInfo.getIsCaptain() !=null && playerInfo.getIsCaptain().equalsIgnoreCase("1")){
                            Functions.showToast(getContext(),"You cannot Swap Captains", FancyToast.ERROR);
                    }
                    else{
                        swapPlayerApi(true, playerId, playerFriendShipId, playerTeamId,   playerInfo.getId(), teamB.getId(), playerInfo.getFriendShipId());
                    }

                }
                else {
                    if (isCaptain !=null && isCaptain.equalsIgnoreCase("1")){
                        Functions.showToast(getContext(),"You cannot Swap Captains", FancyToast.ERROR);
                    }else if (playerInfo.getIsCaptain() !=null && playerInfo.getIsCaptain().equalsIgnoreCase("1")){
                        Functions.showToast(getContext(),"You cannot Swap Captains", FancyToast.ERROR);
                    }else{
                        swapPlayerApi(true, playerInfo.getId(), playerInfo.getFriendShipId(), teamA.getId(), playerId, playerTeamId, playerFriendShipId);
                    }
                }
            }
        });
    }

    private void swapPlayerApi(boolean isLoader, String pAId, String pAFriendshipId, String teamAId, String pBId, String teamBId, String pBFriendshipId) {




        JSONObject data = new JSONObject();
        try {
            // Create the JSON structure for your data
            JSONObject player1 = new JSONObject();
            player1.put("friend_id", pAId);
            player1.put("friendship_id", pAFriendshipId);
            player1.put("team_id", teamAId);

            JSONObject player2 = new JSONObject();
            player2.put("friend_id", pBId);
            player2.put("friendship_id", pBFriendshipId);
            player2.put("team_id", teamBId);

            data.put("game_id", gameTeam.getGameId());
            data.put("player_1", player1);
            data.put("player_2", player2);

            socket.emit("game:swap-player", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialogCallback.swapDone(SwapPlayerDialogFragment.this);



//
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.swapPlayer(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), teamAId, pAId, teamBId, pBId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            dialogCallback.swapDone(SwapPlayerDialogFragment.this);
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

    public interface SwapPlayerDialogFragmentCallback {
        void swapDone(DialogFragment df);
    }
}