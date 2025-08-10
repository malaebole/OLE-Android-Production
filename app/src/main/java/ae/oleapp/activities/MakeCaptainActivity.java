package ae.oleapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.PlayerComparisonAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityMakeCaptainBinding;
import ae.oleapp.models.DragData;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.socket.SocketManager;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeCaptainActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMakeCaptainBinding binding;
    private PlayerComparisonAdapter adapter;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private PlayerInfo playerInfo1, playerInfo2;
    private GameTeam gameTeam;
    private String teamId = "";
    private String turnValue="";
    private final String userId="";
    private final String player2UserId = "";
    private FormationTeams teamA, teamB;
    private MediaController mediaController;
    private Socket socket;
    private SocketManager socketManager;
    private boolean vsMode = false;
    private boolean isDeepLinkUser = false;
    private boolean captainAisReady = false;
    private boolean captainBisReady = false;
    private boolean captainMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMakeCaptainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        socketManager = SocketManager.getInstance();
        socket = socketManager.getSocket();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameTeam = new Gson().fromJson(bundle.getString("game", ""), GameTeam.class);
            teamId = bundle.getString("team_id", "");
            teamA = new Gson().fromJson(bundle.getString("team_a", ""), FormationTeams.class);
            teamB = new Gson().fromJson(bundle.getString("team_b", ""), FormationTeams.class);
            vsMode =  bundle.getBoolean("vs_mode");
            isDeepLinkUser =  bundle.getBoolean("is_deep_link_user");

        }

        JSONObject data = new JSONObject();
        try {
            data.put("player_id", gameTeam.getCreatedBy());
            data.put("game_id", gameTeam.getGameId());
            socket.emit("player:friends", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getPlayers();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new PlayerComparisonAdapter(getContext(), playerList);
        binding.recyclerVu.setAdapter(adapter);

        binding.p1Vu.setVisibility(View.INVISIBLE);
        binding.p2Vu.setVisibility(View.INVISIBLE);
        binding.p1Card.setOnDragListener(vuDragListener);
        binding.p2Card.setOnDragListener(vuDragListener);
        //binding.tvTeamA.setText(teamA.getTeamName());
        //binding.tvTeamB.setText(teamB.getTeamName());

        binding.btnClose.setOnClickListener(this);
        binding.btnRemoveP1.setOnClickListener(this);
        binding.btnRemoveP2.setOnClickListener(this);
        binding.btnMakeCaptain.setOnClickListener(this);
        binding.turn11Btn.setOnClickListener(this);
        binding.turn12Btn.setOnClickListener(this);
        binding.invite.setOnClickListener(this);
        binding.captainAReadyBtn.setOnClickListener(this);
        binding.captainBReadyBtn.setOnClickListener(this);

        if (isDeepLinkUser){
            adapter.user(false);
            binding.btnRemoveP1.setVisibility(View.GONE);
            binding.btnRemoveP2.setVisibility(View.GONE);
            binding.buttonLayout.setVisibility(View.GONE);
            binding.invite.setVisibility(View.GONE);
            binding.turn11Btn.setEnabled(false);
            binding.turn12Btn.setEnabled(false);
        }else{
            adapter.user(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("is_added", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            onBackPressed();
        }
        else if (view == binding.btnRemoveP1) {
            if (playerInfo1 != null) {
                removeTempCaptainEmit(teamA.getId(), "");
            }

        }
        else if (view == binding.btnRemoveP2) {
            if (playerInfo2 != null) {
                removeTempCaptainEmit("", teamB.getId());
            }
        }
        else if (view == binding.btnMakeCaptain) {
            if (playerInfo1 == null || playerInfo2 == null) {
                Functions.showToast(getContext(), getString(R.string.select_captain_both_team), FancyToast.ERROR);
                return;
            }
            if (!captainAisReady  || !captainBisReady){
                Functions.showToast(getContext(), "please click ready button.", FancyToast.ERROR);
                return;
            }
            makeCaptainAPI();
        }
        else if (view == binding.turn11Btn) {
            selectionTempCaptainEmit("turn1");
        }
        else if (view == binding.turn12Btn) {
            selectionTempCaptainEmit("turn2");
        }
        else if (view == binding.invite) {

            if (gameTeam!=null){
                shareGameCount(false, gameTeam.getGameId(), "invite");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, playerInfo1.getName()+" has invited you to divide players."+"\n"+"\n"+gameTeam.getInviteUrl());
                startActivity(Intent.createChooser(shareIntent, "Share game URL"));
            }

        }
        else if (view == binding.captainAReadyBtn) {
            if (playerInfo1.getId().equalsIgnoreCase(gameTeam.getCreatedBy()) && playerInfo1.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                if (playerInfo1 != null && playerInfo2 !=null){
                    selectionTempCaptainEmit("captainaready");
                }else{
                    Functions.showToast(getContext(), "Please select captains first", FancyToast.ERROR);

                }
            } else{
                Functions.showToast(getContext(), "Sorry, you are not captain.", FancyToast.ERROR);
            }
        }
        else if (view == binding.captainBReadyBtn) {

            if (playerInfo1.getId().equalsIgnoreCase(gameTeam.getCreatedBy()) && playerInfo1.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                if (playerInfo1 != null && playerInfo2 !=null){
                    selectionTempCaptainEmit("captainbready");

                }else{
                    Functions.showToast(getContext(), "Please select captains First", FancyToast.ERROR);
                }
            } else if (playerInfo2.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                if (playerInfo1 != null && playerInfo2 !=null){
                    selectionTempCaptainEmit("captainbready");

                }else{
                    Functions.showToast(getContext(), "Please select captains First", FancyToast.ERROR);
                }
            }
            else{
                Functions.showToast(getContext(), "Sorry, you are not captain.", FancyToast.ERROR);
            }
        }
    }

    private void populateCaptainA(String pId) {
        if (playerInfo1 != null) {
            binding.p1Vu.setVisibility(View.INVISIBLE);
            binding.p1PlaceVu.setVisibility(View.VISIBLE);
            playerInfo1.setIsLink("1");
            playerList.add(0, playerInfo1);
            adapter.notifyDataSetChanged();
            playerInfo1 = null;
        }

        for (int i=0; i<playerList.size(); i++){
            if (pId.equalsIgnoreCase(playerList.get(i).getId())){
                playerInfo1 = playerList.get(i);
                playerList.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
        if (playerInfo1 !=null){
            binding.p1Vu.setVisibility(View.VISIBLE);
            binding.p1PlaceVu.setVisibility(View.INVISIBLE);
            String[] arr = playerInfo1.getNickName().split(" ");
            if (arr.length > 0) {
                binding.tvP1Name.setText(arr[0]);
            } else {
                binding.tvP1Name.setText(playerInfo1.getNickName());
            }
            Glide.with(getApplicationContext()).load(playerInfo1.getEmojiUrl()).into(binding.emojiImgVuP1);
            Glide.with(getApplicationContext()).load(playerInfo1.getBibUrl()).into(binding.shirtImgVuP1);
        }
    }
    private void populateCaptainB(String pId){
        if (playerInfo2 != null) {
            binding.p2Vu.setVisibility(View.INVISIBLE);
            binding.p2PlaceVu.setVisibility(View.VISIBLE);
            playerInfo2.setIsLink("1");
            playerList.add(0, playerInfo2);
            adapter.notifyDataSetChanged();
            playerInfo2 = null;
        }

        for (int i=0; i<playerList.size(); i++){
            if (pId.equalsIgnoreCase(playerList.get(i).getId())){
                playerInfo2 = playerList.get(i);
                playerList.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
        if (playerInfo2 !=null){
            binding.p2Vu.setVisibility(View.VISIBLE);
            binding.p2PlaceVu.setVisibility(View.INVISIBLE);
            String[] arr = playerInfo2.getNickName().split(" ");
            if (arr.length > 0) {
                binding.tvP2Name.setText(arr[0]);
            } else {
                binding.tvP2Name.setText(playerInfo2.getNickName());
            }
            Glide.with(getApplicationContext()).load(playerInfo2.getEmojiUrl()).into(binding.emojiImgVuP2);
            Glide.with(getApplicationContext()).load(playerInfo2.getBibUrl()).into(binding.shirtImgVuP2);
        }

    }

    private void turnOneOneClicked(){
        turnValue = "1-1";
        binding.videoCard.setVisibility(View.VISIBLE);
        binding.turn11Btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rectangle_turn_selected));
        binding.turn12Btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rectangle_turn));
        binding.turn11Text.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.turn12Text.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        playVideoFromRawResource(R.raw.one_one);
    }
    private void turnOnetwoClicked(){
        turnValue = "1-2";
        binding.videoCard.setVisibility(View.VISIBLE);
        binding.turn11Btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rectangle_turn));
        binding.turn12Btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rectangle_turn_selected));
        binding.turn11Text.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        binding.turn12Text.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        playVideoFromRawResource(R.raw.one_two);


    }

    private void captainAClickedReady(Boolean isReady){
        if (isReady){
            binding.captainAReadyBtn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.captain_ready_selected));
        }else{
            binding.captainAReadyBtn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.captain_ready_unselected));
        }
    }

    private void captainBClickedReady(Boolean isReady){
        if (isReady){
            binding.captainBReadyBtn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.captain_ready_selected));
        }else{
            binding.captainBReadyBtn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.captain_ready_unselected));
        }

    }


    protected void playVideoFromRawResource(int rawResourceId) {
        //String path = "android.resource://" + getPackageName() + "/" + rawResourceId;


        binding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + rawResourceId));

        // Set properties for center-cropping the video
        binding.videoView.setMediaController(null);
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mediaPlayer.setLooping(true);  // Loop the video
            }
        });

        // Start preparing the video asynchronously
        binding.videoView.requestFocus();
        binding.videoView.start();


    }

    View.OnDragListener vuDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v == binding.p1Card) {
                        binding.p1Card.setCardBackgroundColor(Color.GREEN);
                    }
                    else if (v == binding.p2Card) {
                        binding.p2Card.setCardBackgroundColor(Color.GREEN);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED: case DragEvent.ACTION_DRAG_ENDED:
                    if (v == binding.p1Card) {
                        binding.p1Card.setCardBackgroundColor(Color.WHITE);
                    }
                    else if (v == binding.p2Card) {
                        binding.p2Card.setCardBackgroundColor(Color.WHITE);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    final DragData state = (DragData) event.getLocalState();
                    if (v == binding.p1Card) {
                        if (state.getItem().getIsLink().equalsIgnoreCase("1")) {
                            int pos = state.getPos();
                            binding.p1Vu.setVisibility(View.VISIBLE);
                            binding.p1PlaceVu.setVisibility(View.INVISIBLE);
                            if (playerInfo1 != null) {
                                playerList.add(0, playerInfo1);
                                pos += 1;
                            }
                            playerInfo1 = state.getItem();
                            String[] arr = playerInfo1.getNickName().split(" ");
                            if (arr.length > 0) {
                                binding.tvP1Name.setText(arr[0]);
                            } else {
                                binding.tvP1Name.setText(playerInfo1.getNickName());
                            }
                            Glide.with(getApplicationContext()).load(playerInfo1.getEmojiUrl()).into(binding.emojiImgVuP1);
                            Glide.with(getApplicationContext()).load(playerInfo1.getBibUrl()).into(binding.shirtImgVuP1);


                            makeTempCaptainEmit(teamA.getId(), "", playerInfo1.getId());

                            playerList.remove(pos);
                            adapter.notifyDataSetChanged();


                        }
                        else {
                            Functions.showToast(getContext(), getString(R.string.only_app_user_be_captain), FancyToast.ERROR);
                        }

                    }
                    else if (v == binding.p2Card) {
                        // Check if binding.p2Vu is empty
                        if (state.getItem().getIsLink().equalsIgnoreCase("1")) {
                            int pos = state.getPos();
                            binding.p2Vu.setVisibility(View.VISIBLE);
                            binding.p2PlaceVu.setVisibility(View.INVISIBLE);
                            if (playerInfo2 != null) {
                                playerList.add(0, playerInfo2);
                                pos += 1;
                            }
                            playerInfo2 = state.getItem();
                            String[] arr = playerInfo2.getNickName().split(" ");
                            if (arr.length > 0) {
                                binding.tvP2Name.setText(arr[0]);
                            } else {
                                binding.tvP2Name.setText(playerInfo2.getNickName());
                            }
                            Glide.with(getApplicationContext()).load(playerInfo2.getEmojiUrl()).into(binding.emojiImgVuP2);
                            Glide.with(getApplicationContext()).load(playerInfo2.getBibUrl()).into(binding.shirtImgVuP2);

                            makeTempCaptainEmit("", teamB.getId(), playerInfo2.getId());

                            playerList.remove(pos);
                            adapter.notifyDataSetChanged();


                        }
                        else {
                            Functions.showToast(getContext(), getString(R.string.only_app_user_be_captain), FancyToast.ERROR);
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private  void makeTempCaptainEmit(String teamAid, String teamBid, String pId) {
        try {
            JSONObject captainData = new JSONObject();
            captainData.put("game_id", gameTeam.getGameId());
            if (!teamAid.isEmpty()) {
                JSONObject team_a = new JSONObject();
                team_a.put("team_id", teamAid);
                team_a.put("captain_id", pId);
                captainData.put("team_a", team_a);
            }
            if (!teamBid.isEmpty()) {
                JSONObject team_b = new JSONObject();
                team_b.put("team_id", teamBid);
                team_b.put("captain_id", pId);
                captainData.put("team_b", team_b);
            }
            socket.emit("game:make-temp-captain", captainData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private  void removeTempCaptainEmit(String teamAid, String teamBid) {
        try {
            JSONObject captainData = new JSONObject();
            captainData.put("game_id", gameTeam.getGameId());
            if (!teamAid.isEmpty()) {
                JSONObject team_a = new JSONObject();
                team_a.put("team_id", teamAid);
                team_a.put("captain_id", "");
                captainData.put("team_a", team_a);
            }
            if (!teamBid.isEmpty()) {
                JSONObject team_b = new JSONObject();
                team_b.put("team_id", teamBid);
                team_b.put("captain_id", "");
                captainData.put("team_b", team_b);
            }
            socket.emit("game:remove-temp-captain", captainData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private  void selectionTempCaptainEmit(String type) {
        try {
            JSONObject captainData = new JSONObject();
            captainData.put("game_id", gameTeam.getGameId());
            captainData.put("selection_type", type);
            socket.emit("game:temp-captain-broadcast", captainData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPlayers() {

            socket.on("game:players", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONArray dataArray = (JSONArray) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                playerList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    PlayerInfo info = gson.fromJson(dataArray.get(i).toString(), PlayerInfo.class);
                                    if (info.getIsLink() !=null){
                                        if (info.getIsLink().equalsIgnoreCase("1")) {
                                            playerList.add(info);
                                        }
                                    }
                                }

                                JSONObject data = new JSONObject();
                                try {
                                    data.put("game_id", gameTeam.getGameId());
                                    socket.emit("game:temp-captains", data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                adapter.notifyDataSetChanged();
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });
                }
            });
            socket.on("game:temp-captains", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject receivedData = (JSONObject) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String gameId = receivedData.getString("game_id");
                                if (gameId.equalsIgnoreCase(gameTeam.getGameId())){
                                    String teamACaptainId = receivedData.getJSONObject("team_a").getString("captain_id");
                                    String teamBCaptainId = receivedData.getJSONObject("team_b").getString("captain_id");
                                    boolean isAmCaptain = teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID));

                                    if (!isAmCaptain) {
                                        if (teamACaptainId.isEmpty() && teamBCaptainId.isEmpty()){
                                            makeTempCaptainEmit(teamA.getId(), "", Functions.getPrefValue(getContext(), Constants.kUserID));
                                        }
                                        else if (teamACaptainId.isEmpty()){
                                            makeTempCaptainEmit(teamA.getId(), "", Functions.getPrefValue(getContext(), Constants.kUserID));
                                            populateCaptainB(teamBCaptainId);
                                        }
                                        else if (teamBCaptainId.isEmpty()) {
                                            populateCaptainA(teamACaptainId);
                                            makeTempCaptainEmit("", teamB.getId(), Functions.getPrefValue(getContext(), Constants.kUserID));
                                        }
                                        else {
                                            populateCaptainA(teamACaptainId);
                                            populateCaptainB(teamBCaptainId);
                                        }
                                    }
                                    else {
                                        populateCaptainA(teamACaptainId);
                                        populateCaptainB(teamBCaptainId);
                                    }
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });
                }
            });
            socket.on("game:temp-captains-created", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject receivedData = (JSONObject) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String gameId = receivedData.getString("game_id");
                                if (gameId.equalsIgnoreCase(gameTeam.getGameId())){
                                    if (!receivedData.isNull("team_a")) {
                                        String pId = receivedData.getJSONObject("team_a").getString("captain_id");
                                        populateCaptainA(pId);
                                    }
                                    if (!receivedData.isNull("team_b")) {
                                        String pId = receivedData.getJSONObject("team_b").getString("captain_id");
                                        populateCaptainB(pId);
                                    }
                                }
                                captainAClickedReady(false);
                                captainBClickedReady(false);
                                captainAisReady = false;
                                captainBisReady = false;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });
            socket.on("game:temp-captains-removed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject receivedData = (JSONObject) args[0];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String gameId = receivedData.getString("game_id");
                                if (gameId.equalsIgnoreCase(gameTeam.getGameId())){
                                    if (!receivedData.isNull("team_a")) {
                                        if (playerInfo1 != null) {
                                            binding.p1Vu.setVisibility(View.INVISIBLE);
                                            binding.p1PlaceVu.setVisibility(View.VISIBLE);
                                            playerInfo1.setIsLink("1");
                                            playerList.add(0, playerInfo1);
                                            adapter.notifyDataSetChanged();
                                            playerInfo1 = null;
                                        }
                                    }
                                    if (!receivedData.isNull("team_b")) {
                                        if (playerInfo2 != null) {
                                            binding.p2Vu.setVisibility(View.INVISIBLE);
                                            binding.p2PlaceVu.setVisibility(View.VISIBLE);
                                            playerInfo2.setIsLink("1");
                                            playerList.add(0, playerInfo2);
                                            adapter.notifyDataSetChanged();
                                            playerInfo2 = null;
                                        }
                                    }
                                }
                                captainAClickedReady(false);
                                captainBClickedReady(false);
                                captainAisReady = false;
                                captainBisReady = false;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });
            socket.on("game:temp-captain-broadcast", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject receivedData = (JSONObject) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String gameId = receivedData.getString("game_id");
                                if (gameId.equalsIgnoreCase(gameTeam.getGameId())){
                                    String type = receivedData.getString("selection_type");
                                    if (type.equalsIgnoreCase("turn1")) {
                                        turnOneOneClicked();
                                    }
                                    else if (type.equalsIgnoreCase("turn2")) {
                                        turnOnetwoClicked();
                                    }
                                    else if (type.equalsIgnoreCase("captainaready")) {
                                        captainAClickedReady(true);
                                        captainAisReady = true;
                                    }
                                    else if (type.equalsIgnoreCase("captainbready")) {
                                        captainBClickedReady(true);
                                        captainBisReady = true;
                                    }
                                    else if (type.equalsIgnoreCase("captainsCreated")) {
                                        captainMode = false;
                                        if (!gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                                            finish();
                                        }
                                    }

                                    if (captainAisReady && captainBisReady){
                                        binding.gifLay.setVisibility(View.VISIBLE);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                binding.gifLay.setVisibility(View.GONE);
                                                if (playerInfo1.getId().equalsIgnoreCase(gameTeam.getCreatedBy()) && playerInfo1.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                                                    captainMode = true;
                                                    makeCaptainAPI();
                                                }else{
                                                    binding.loadingLay.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }, 5000);

                                    }
                                    else{
                                        binding.gifLay.setVisibility(View.GONE);
                                    }

                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });

    }

    private void makeCaptainAPI() {

        if (playerInfo1 !=null && playerInfo2 !=null){
            if (turnValue.equalsIgnoreCase("")){
                turnValue = "1-1";
            }

            if (captainMode){
                Intent intent = new Intent();
                intent.putExtra("is_added", true);
                intent.putExtra("team_a_id", teamA.getId());
                intent.putExtra("captain_a_id", playerInfo1.getId());
                intent.putExtra("captain_a_friendship_id",  playerInfo1.getFriendShipId());
                intent.putExtra("team_b_id", teamB.getId());
                intent.putExtra("captain_b_id", playerInfo2.getId());
                intent.putExtra("captain_b_friendship_id",  playerInfo2.getFriendShipId());
                intent.putExtra("turn_value",  turnValue);
                intent.putExtra("teamA_captain_name", playerInfo1.getNickName());
                intent.putExtra("teamB_captain_name", playerInfo2.getNickName());
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Intent intent = new Intent();
                intent.putExtra("is_added", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void shareGameCount(boolean isLoader, String gameId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.shareGameCount(gameId,type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                        } else {
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
}