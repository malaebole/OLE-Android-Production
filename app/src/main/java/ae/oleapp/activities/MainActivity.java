package ae.oleapp.activities;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import ae.oleapp.MyApp;
import ae.oleapp.R;
import ae.oleapp.adapters.ChairListAdapter;
import ae.oleapp.adapters.FieldImageListAdapter;
import ae.oleapp.adapters.PlayerListAdapter;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.adapters.ShirtsTeamAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityMainBinding;
import ae.oleapp.dialogs.AddPlayerFragment;
import ae.oleapp.dialogs.ChooseTeamDialogFragment;
import ae.oleapp.dialogs.EditGameDialogFragment;
import ae.oleapp.dialogs.FriendOptionsDialogFragment;
import ae.oleapp.dialogs.GameCaptainDialogFragment;
import ae.oleapp.dialogs.GameResultDialogFragment;
import ae.oleapp.dialogs.OleUpdatePassDialog;
import ae.oleapp.dialogs.PlayerStatusDialogFragment;
import ae.oleapp.dialogs.ResultDialogFragment;
import ae.oleapp.dialogs.SelectionListDialog;
import ae.oleapp.dialogs.StartLineupDialogFragment;
import ae.oleapp.dialogs.SwapPlayerDialogFragment;
import ae.oleapp.fragments.AdsSubscriptionPopupFragment;

import ae.oleapp.fragments.CaptainWaitingDialogFragment;
import ae.oleapp.fragments.UnlockedJerseyPopupFragment;
import ae.oleapp.models.Chair;
import ae.oleapp.models.Country;

import ae.oleapp.models.DragData;
import ae.oleapp.models.FieldImage;
import ae.oleapp.models.GameHistory;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.RewardedAdManager;
import ae.oleapp.models.SelectionList;
import ae.oleapp.models.SelectionModel;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.SocketGameTeam;
import ae.oleapp.models.Team;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.signup.SplashActivity;
import ae.oleapp.socket.SocketManager;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.PreviewFieldView;
import ae.oleapp.voiceutils.DataModel;
import ae.oleapp.voiceutils.DataModelType;
import ae.oleapp.webrtc.BluetoothManager;
import ae.oleapp.webrtc.MyPeerConnectionObserver;
import ae.oleapp.webrtc.RTCAudioManager;
import ae.oleapp.webrtc.WebRTCClient;
import ae.oleapp.zegocloudexpress.ExpressManager;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.parser.Base64;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

public class MainActivity extends BaseActivity implements View.OnClickListener, SliderAdapter.OnItemClickListener, WebRTCClient.Listener {

    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123;
    // private static final int BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE = 121;
    private ActivityMainBinding binding;
    private PlayerListAdapter playerAdapter;
    private ShirtsTeamAdapter teamAdapter;
    private ShirtListAdapter shirtAdapter;
    private ShirtListAdapter gkAdapter;
    private ChairListAdapter chairAdapter;
    private FieldImageListAdapter fieldAdapter;
    private SelectionModel selectionModel;
    private final List<Country> countryList = new ArrayList<>();
    private final List<Team> teamList = new ArrayList<>();
    private final List<Shirt> shirtList = new ArrayList<>();
    private final List<Chair> chairList = new ArrayList<>();
    private final List<Shirt> gkShirtList = new ArrayList<>();
    private final List<FieldImage> fieldList = new ArrayList<>();
    private final List<PlayerInfo> friendList = new ArrayList<>();
    private int friendsCount = 0, teamPodition = 0;
    private String selectedCountryId = "", selectedTeamId = "", selectedShirtId = "", selectedChairId = "",
            selectedFieldId = "", selectedGkShirtId = "",  teamAId = "", captainAId = "", captainAFriendshipId = "",
            teamBId = "", captainBId = "", captainBFriendshipId = "", turnValue="";

    private GameTeam gameTeam;
    private int teamAVuWidth = 0, teamAVuHeight = 0, teamBVuWidth = 0, teamBVuHeight = 0, selectedTab = 0;
    private float subVuH = 0, subVuW = 0;
    private PlayerInfo selectedPlayerForSubstitute;
    private FormationTeams teamA;
    private FormationTeams teamB;
    private boolean teamACaptainAvailable = false, teamBCaptainAvailable = false;
    private String teamACaptainId = "", teamBCaptainId = "", teamACaptainName = "", teamBCaptainName = "";
    private boolean captainATurn = false;
    private boolean captainBTurn = false;
    private AlertDialog playerChooseAlert;
    private final List<SliderModelClass> listItems = new ArrayList<>();
    private ViewPager page;
    private String GameID = "";
    private String FriendId = "";
    private String dotPosition = "";
    private String userModule = "";
    private String userIpDetails = "";
    private String subscribedShirtId = "";
    private String subscribedShirt = "";
    private String subSelectedTeamId = "";
    private String subSelectedShirtId = "";
    SliderAdapter itemsPager_adapter;
    LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private RewardedAdManager rewardedAdManager;
    private Vibrator vibrator;
    private String showcaseHasBeenShown = "0";
    private String showcaseVsHasBeenShown = "0";
    private int index = 0;
    private SocketManager socketManager;
    private Socket socket;
    private ConnectivityReceiver connectivityReceiver;
    private KProgressHUD fastLineupLoader;
    private boolean isRecording = true;
    private BluetoothManager bluetoothManager;
    private RTCAudioManager rtcAudioManager;
    String otherCaptainId = "";
    private WebRTCClient webRTCClient;
    private String currentUsername;
    private String target;
    public Listener listener;
    public boolean vsMode = false;
    private Uri deepLinkUri;
    private String deepUrlGameId = "";
    private CaptainWaitingDialogFragment dialogFragment;
    private GameCaptainDialogFragment captainDialogFragment;
    private boolean isExpired = true;
    private KProgressHUD captainloader;
    private final Handler handler = new Handler();
    private final int bounceDuration = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        page = findViewById(R.id.logo_vuu);
        pager_indicator = findViewById(R.id.viewPagerCountDots);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (FriendId == null || FriendId.isEmpty()) {
            FriendId = Functions.getPrefValue(getContext(), Constants.kUserID);
        }

        socketManager = SocketManager.getInstance();
        socket = socketManager.getSocket();
        rtcAudioManager = new RTCAudioManager(this);
        bluetoothManager = BluetoothManager.create(getApplicationContext(), rtcAudioManager);

        rewardedAdManager = new RewardedAdManager(this);
        rewardedAdManager.loadRewardedAd();

        getProfileAPI(true);
        ipdetails(true, "");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            subSelectedTeamId = bundle.getString("teamId", "");
            subSelectedShirtId = bundle.getString("shirtId", "");
            deepLinkUri = bundle.getParcelable("invite_url");
        }

        GridLayoutManager playerLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        binding.playersRecyclerVu.setLayoutManager(playerLayoutManager);
        playerAdapter = new PlayerListAdapter(getContext(), friendList);
        playerAdapter.setItemClickListener(playerClickListener);
        binding.playersRecyclerVu.setAdapter(playerAdapter);

        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.teamRecyclerVu.setLayoutManager(teamLayoutManager);
        teamAdapter = new ShirtsTeamAdapter(getContext(), teamList);
        teamAdapter.setItemClickListener(teamClickListener);
        binding.teamRecyclerVu.setAdapter(teamAdapter);

        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new ShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);

        LinearLayoutManager chairLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.chairRecyclerVu.setLayoutManager(chairLayoutManager);
        chairAdapter = new ChairListAdapter(getContext(), chairList);
        chairAdapter.setItemClickListener(chairClickListener);
        binding.chairRecyclerVu.setAdapter(chairAdapter);

        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new FieldImageListAdapter(getContext(), fieldList);
        fieldAdapter.setItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        LinearLayoutManager gkLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.gkRecyclerVu.setLayoutManager(gkLayoutManager);
        gkAdapter = new ShirtListAdapter(getContext(), gkShirtList);
        gkAdapter.setItemClickListener(gkClickListener);
        binding.gkRecyclerVu.setAdapter(gkAdapter);


        binding.vuTeamA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamA.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                teamAVuWidth = binding.vuTeamA.getWidth();
                teamAVuHeight = binding.vuTeamA.getHeight();
            }
        });

        binding.vuTeamB.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamB.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                teamBVuWidth = binding.vuTeamB.getWidth();
                teamBVuHeight = binding.vuTeamB.getHeight();
            }
        });

        PreviewFieldView fieldView = new PreviewFieldView(getContext());
        binding.vuTeamA.addView(fieldView);
        fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                subVuW = fieldView.getWidth();
                subVuH = fieldView.getHeight();
                binding.vuTeamA.removeView(fieldView);
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.tabTeamA.setOnDragListener(vuDragListener);
        binding.tabTeamB.setOnDragListener(vuDragListener);
        binding.vuTeamA.setOnDragListener(vuDragListener);
        binding.vuTeamB.setOnDragListener(vuDragListener);
        tabSelected(binding.tabTeamA);

        binding.toolbarBadge.setVisibility(View.GONE);
        binding.tvTurn.setVisibility(View.GONE);
        getGoalKeeperShirts(false);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        binding.btnMenu.setOnClickListener(this);
        binding.tabTeamA.setOnClickListener(this);
        binding.tabTeamB.setOnClickListener(this);
        binding.btnChair.setOnClickListener(this);
        binding.btnShirt.setOnClickListener(this);
        binding.btnField.setOnClickListener(this);
        binding.btnGkShirts.setOnClickListener(this);
        binding.countryVu.setOnClickListener(this);
        binding.logoutVu.setOnClickListener(this);
        binding.shopVu.setOnClickListener(this);
        binding.comparisonVu.setOnClickListener(this);
        binding.startVu.setOnClickListener(this);
        binding.fastLineup.setOnClickListener(this);
        binding.addPlayerVu.setOnClickListener(this);
        binding.editGameVu.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.headerVu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.groupVu.setOnClickListener(this);
        binding.suggestVu.setOnClickListener(this);
        binding.useVu.setOnClickListener(this);
        binding.contactVu.setOnClickListener(this);
        binding.friendRequestVu.setOnClickListener(this);
        binding.gameHistoryVu.setOnClickListener(this);
        binding.clubHome.setOnClickListener(this);
        binding.lineupHome.setOnClickListener(this);
        binding.padelHome.setOnClickListener(this);
        binding.btnHideFace.setOnClickListener(this);
        binding.deleteUserAcc.setOnClickListener(this);
        binding.subscriptionVu.setOnClickListener(this);
        binding.passVu.setOnClickListener(this);
        binding.tcVu.setOnClickListener(this);
        binding.ppVu.setOnClickListener(this);
        binding.globalLineup.setOnClickListener(this);
        binding.realLineup.setOnClickListener(this);
        // binding.btnTalk.setOnClickListener(this);
        binding.vsMode.setOnClickListener(this);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                //Get new FCM registration token
                String token = task.getResult();
                Log.d("TOKEN", token);
                sendFcmTokenApi(token);
            }
        });

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.padelHome.setEnabled(false);
            binding.clubHome.setEnabled(true);
            binding.lineupHome.setEnabled(true);

        } else if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kFootballModule)) {
            binding.padelHome.setEnabled(true);
            binding.clubHome.setEnabled(false);
            binding.lineupHome.setEnabled(true);

        } else if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            binding.padelHome.setEnabled(true);
            binding.clubHome.setEnabled(true);
            binding.lineupHome.setEnabled(false);
        }
        currentUsername = Functions.getPrefValue(getContext(), Constants.kUserID);
        checkPermissionForVoice();
        if (deepLinkUri != null) {
            deepLinkFound(deepLinkUri);
        }
        Runnable runnable = new Runnable() {
            public void run() {
                bounceButton(bounceDuration);
                handler.postDelayed(this, bounceDuration);
            }
        };
        handler.post(runnable);

//        inAppUpdates();

    }


    private void checkPermissionForVoice() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.BLUETOOTH_CONNECT};
        }else{
            createWebRtcConnection();
        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                createWebRtcConnection();
            }
        });
    }

    private void deepLinkFound(Uri deepLinkUri) {
        String path = deepLinkUri.getPath();

        if ("/invite".equals(path)) {

            String gameEncoded = deepLinkUri.getQueryParameter("game");
            byte[] decodedBytes = Base64.decode(gameEncoded, Base64.DEFAULT);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            try {
                JSONObject gameData = new JSONObject(decodedString);
                String gameId = gameData.getString("game_id");
                String friendCreatorId = gameData.getString("owner_id");
                String createdAt = gameData.getString("created_at");
                deepUrlGameId = gameId;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date createdDate = dateFormat.parse(createdAt);

                Date currentDate = new Date();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String createdTime = timeFormat.format(createdDate);
                String currentTime = timeFormat.format(currentDate);
                showCaptainWaitingPopup();
                captainloader = Functions.showLoader(getContext());

                if (isExpired(createdTime, currentTime)) {
                    isExpired = true;
                    Functions.showToast(getContext(), "Link has been expired.", FancyToast.ERROR);
                    dismissCaptainWaitingPopup();
                    Functions.hideLoader(captainloader);

                } else {
                    isExpired = false;
                    JSONObject data = new JSONObject();
                    data.put("game_id", deepUrlGameId);
                    socket.emit("game:join-via-link", data);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissCaptainWaitingPopup();
                            letsBeginVsMode();
                        }
                    }, 5000);

                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isExpired(String createdTime, String currentTime) {
        // Split time strings into hour, minute, and second
        String[] createdParts = createdTime.split(":");
        String[] currentParts = currentTime.split(":");

        // Convert time parts to integers
        int createdHour = Integer.parseInt(createdParts[0]);
        int createdMinute = Integer.parseInt(createdParts[1]);
        int createdSecond = Integer.parseInt(createdParts[2]);

        int currentHour = Integer.parseInt(currentParts[0]);
        int currentMinute = Integer.parseInt(currentParts[1]);
        int currentSecond = Integer.parseInt(currentParts[2]);

        // Calculate the difference in seconds
        int differenceSeconds = (currentHour - createdHour) * 3600 +
                (currentMinute - createdMinute) * 60 +
                (currentSecond - createdSecond);

        // Check if the difference is greater than 5 minutes (300 seconds)
        return differenceSeconds > 300;
    }

    private void createWebRtcConnection(){

        webRTCClient = new WebRTCClient(getContext(), new MyPeerConnectionObserver() {
            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                try {
                    if (mediaStream.audioTracks.size() > 0) {
                        // Assuming audio track is at index 0
                        mediaStream.audioTracks.get(0).setEnabled(true);

                        Log.d("TAG", "streamAdded: " + mediaStream.audioTracks.get(0));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
                Log.d("TAG", "onConnectionChange: " + newState);
                super.onConnectionChange(newState);
                if (newState == PeerConnection.PeerConnectionState.CONNECTED && listener != null) {
                    listener.webrtcConnected();
                    Log.d("TAG", "Connected: " + "Connected");

                }

                if (newState == PeerConnection.PeerConnectionState.CLOSED ||
                        newState == PeerConnection.PeerConnectionState.DISCONNECTED) {
                    if (listener != null) {
                        listener.webrtcClosed();
                        Log.d("TAG", "connectionClosed: " + "connectionClosed");

                    }
                }
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                webRTCClient.sendIceCandidate(iceCandidate, target);
                Log.d("TAG", "iceCandiate: " + iceCandidate + target);

                // Assuming target is the remote user's ID, send the ICE candidate data to the other user
                DataModel iceCandidateData = new DataModel(target, currentUsername, String.valueOf(iceCandidate), DataModelType.IceCandidate);
                sendMessageToOtherUser(iceCandidateData);



            }
        }, currentUsername);
        webRTCClient.listener = this;

    }

    private void startAudioRecording() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST_CODE);
            return;
        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE);
//            return;
//        }
        binding.btnSpeak.setImageResource(R.drawable.mic_activel);
        isRecording = false;

//        currentCaptainId = Functions.getPrefValue(getContext(), Constants.kUserID);
//        if (Functions.getPrefValue(getContext(), Constants.kUserID).equalsIgnoreCase(gameTeam.getCaptains().get(0))){
//            otherCaptainId = gameTeam.getCaptains().get(1);
//        }
//        else if (Functions.getPrefValue(getContext(), Constants.kUserID).equalsIgnoreCase(gameTeam.getCaptains().get(1))) {
//            otherCaptainId = gameTeam.getCaptains().get(0);
//        }
        List<String> captains = gameTeam.getCaptains();
        if (captains != null && captains.size() >= 2) {
            String currentUserID = Functions.getPrefValue(getContext(), Constants.kUserID);
            if (currentUserID.equalsIgnoreCase(captains.get(0))) {
                otherCaptainId = captains.get(1);
            } else if (currentUserID.equalsIgnoreCase(captains.get(1))) {
                otherCaptainId = captains.get(0);
            } else {
                // Handle the case where the current user is not found in the captains list
                // This could indicate an unexpected state or an error in data
                //  Functions.showToast(getContext(),"User not found", FancyToast.ERROR);
            }
        }
        else {
            // Handle the case where captains list is null or does not contain enough elements
            // This could indicate missing or invalid data
//            JSONObject data = new JSONObject(); //temp solution
//            try {
//                data.put("game_id", GameID);
//                socket.emit("game:join", data);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            Functions.showToast(getContext(),"Captains Ids are empty", FancyToast.ERROR);
        }
        if (webRTCClient != null){
            webRTCClient.call(otherCaptainId);
            webRTCClient.toggleAudio(true);
        }

    }

    private void sendMessageToOtherUser(DataModel dataModel) {

        target = dataModel.getTarget();

        if (dataModel.getType().equals(DataModelType.Offer)){

            // Offer data
            JSONObject offerData = new JSONObject();
            try {
                offerData.put("user", target);
                offerData.put("game_id", GameID);
                offerData.put("offer", new Gson().toJson(dataModel));
                socket.emit("call:offer", offerData);
//                Log.d("TAG", "call:offerEmitter: " + offerData + target + dataModel.getData() + dataModel.getTarget() + dataModel.getSender() + dataModel.getType());

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        else if (dataModel.getType().equals(DataModelType.Answer)) {
            JSONObject answerData = new JSONObject();
            try {
                answerData.put("user", target);
                answerData.put("game_id", GameID);
                answerData.put("answer", new Gson().toJson(dataModel));
                socket.emit("call:answer", answerData);
//                Log.d("TAG", "call:answerEmitter: " + answerData + target + dataModel.getData() + dataModel.getTarget() + dataModel.getSender() + dataModel.getType());


            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else if (dataModel.getType().equals(DataModelType.IceCandidate)) {

            JSONObject candidateData = new JSONObject();
            try {
                candidateData.put("user", target);
                candidateData.put("game_id", GameID);
                candidateData.put("candidate", new Gson().toJson(dataModel));
                socket.emit("call:candidate", candidateData);
//                Log.d("TAG", "call:candidate: " + candidateData + target);
//                Log.d("TAG", "call:candidateEmitter: " + candidateData + target + dataModel.getData() + dataModel.getTarget() + dataModel.getSender() + dataModel.getType());



            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
    }

    private void stopAudioRecording() {
        isRecording = true;
        binding.btnSpeak.setImageResource(R.drawable.mic_deactivel);
        if (webRTCClient != null) {
            webRTCClient.toggleAudio(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startAudioRecording();
            } else {
                // Permission denied, handle accordingly (show a message, disable functionality, etc.)
                Log.d("TAG", "RECORD_AUDIO permission denied");
            }
        }
//        else if (requestCode == BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                startAudioRecording();
//            } else {
//                // Permission denied, handle accordingly (show a message, disable functionality, etc.)
//                Log.d("TAG", "RECORD_AUDIO permission denied");
//            }
//        }
    }

    @Override
    public void onTransferDataToOtherPeer(DataModel model) {
        sendMessageToOtherUser(model);
        Log.d("TAG", "DataModel:onTransferDataToOtherPeer " + model.getData() + model.getSender() + model.getTarget() + model.getType());
    }

    public class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean isConnected = isConnected(context);
                if (isConnected) {
                    performTasksWhenConnected();
                } else {
                    performTasksWhenDisconnected();
                }
            }
        }

        private boolean isConnected(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            return false;
        }

        private void performTasksWhenConnected() {
            if (!socket.connected()) {
                socket.connect();
                try {
                    if(dotPosition !=null &&  (dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase(""))){
                        ipdetails(false,"");
                    }

                    if (GameID !=null && !GameID.isEmpty()){
                        JSONObject data = new JSONObject();
                        try {
                            data.put("game_id", GameID);
                            socket.emit("game:join", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject dataa = new JSONObject();
                        try {
                            dataa.put("player_id", FriendId);
                            dataa.put("game_id", GameID);
                            socket.emit("player:friends", dataa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        JSONObject dataaa = new JSONObject();
                        try {
                            dataaa.put("player_id", FriendId);
                            socket.emit("player:friends", dataaa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//                        Functions.showToast(getContext(),"Socket Reconnected", FancyToast.SUCCESS);

            }
//                    Functions.showToast(getContext(),"Internet is connected", FancyToast.SUCCESS);

        }

        private void performTasksWhenDisconnected() {
            Functions.showToast(getContext(),"Internet Problem: Please turn off and on your internet!", FancyToast.ERROR);
        }
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(),Constants.kUserID),"", Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            Functions.saveUserinfo(getContext(), userInfo);
                            if (userInfo.getGlobalLineup().equalsIgnoreCase("1")){
                                binding.globalLineup.setVisibility(View.VISIBLE);
                            }else{
                                binding.globalLineup.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        Functions.hideLoader(hud);
                        e.printStackTrace();

                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  //remove if any error
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            finish();
        }
    }
    private void populateData(){
        userModule = Functions.getPrefValue(getContext(), Constants.kUserModule);
        if (!userModule.equalsIgnoreCase("all")){
            binding.clubHome.setVisibility(View.GONE);
            binding.padelHome.setVisibility(View.GONE);
            binding.lineupHome.setVisibility(View.GONE);
        }else{
            binding.clubHome.setVisibility(View.VISIBLE);
            binding.padelHome.setVisibility(View.VISIBLE);
            binding.lineupHome.setVisibility(View.VISIBLE);
            if (!Functions.getPrefValue(getContext(), Constants.firstTimeLineup).isEmpty()){
                showcaseHasBeenShown = Functions.getPrefValue(getContext(), Constants.firstTimeLineup);
            }
            if (showcaseHasBeenShown.equalsIgnoreCase("0")) {
                new GuideView.Builder(this)
                        .setTitle("Exit Lineup?")
                        .setContentText("By tapping club it will take you to your\n booking screen!")
                        .setGravity(Gravity.auto)
                        .setTargetView(binding.btnMenu)
                        .build()
                        .show();
                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.firstTimeLineup, "1");
                editor.apply();
            }
        }

    }
    protected void sendAppLangApi() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendAppLang(userId, Functions.getAppLang(getContext()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }
    @Override
    protected void onResume() {

        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);

        if (Functions.getPrefValue(getContext(),Constants.kIsSignIn).equalsIgnoreCase("1")){
            callUnreadNotifAPI();
        }
        checkWinMatchAPI();
        UserInfo info = Functions.getUserinfo(getContext());
        if(info.getNickName() !=null){
            binding.tvName.setText(info.getNickName());
        }
        else{
            binding.tvName.setText("Ole user");
        }

        binding.friendRequestCount.setText(info.getFriendRequestCount());
        binding.tvPhone.setText(info.getPhone());
        Glide.with(getApplicationContext()).load(info.getEmojiUrl()).into(binding.emojiImgVu);
        Glide.with(getApplicationContext()).load(info.getBibUrl()).placeholder(R.drawable.bibl).into(binding.shirtImgVu);

        if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (gameTeam!=null){
                if (gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
                    try {
                        if (webRTCClient == null){
                            checkPermissionForVoice();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("receive_push"));
        if (rewardedAdManager.getStatus() && !subscribedShirtId.isEmpty()){
            unlockOneJersey(subscribedShirtId, subscribedShirt);
        }
        if (!socket.connected()){
            socketManager =  SocketManager.getInstance();
            socket = socketManager.getSocket();
        }
        if (info !=null){
            updateGroups(info.getFriendRequestCount());
        }

        super.onResume();

    }

    private void updateGroups(String countt) {
        if (Functions.getPrefValue(getContext(), Constants.groupCallStatus).equalsIgnoreCase("true")){
            try {
                socket.emit("player:friends-groups");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
            editor.putString(Constants.groupCallStatus, "false");
            editor.apply();

            int count = Integer.parseInt(countt);
            if (count>0){
                String newCount = String.valueOf(count -1);
                binding.friendRequestCount.setText(newCount);
            }

        }


    }

    @Override
    protected void onPause() {

        try {
            if (webRTCClient != null){
                webRTCClient.toggleAudio(false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(connectivityReceiver);
        page.removeOnPageChangeListener(onPageChangeListener);
        disconnectSocketListeners();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            stopAudioRecording();
            webRTCClient.closeConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();

    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String gameId = intent.getStringExtra("game_id");
            if (type.equalsIgnoreCase("oleLineupInvitationAccepted")) {
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (type.equalsIgnoreCase("newCaptain") || type.equalsIgnoreCase("removeCaptain")) {
                checkCaptainAPI();
            }
            if (type.equalsIgnoreCase("gameScoreAdded")){
                showBestPlayerDialog(gameId);
            }
            UserInfo info = Functions.getUserinfo(getContext());
            if (type.equalsIgnoreCase("lineupEmployeeAdded")){
                info.setGlobalLineup("1");
                binding.globalLineup.setVisibility(View.VISIBLE);
            }
            if (type.equalsIgnoreCase("lineupEmployeeRemoved")){
                info.setGlobalLineup("0");
                binding.globalLineup.setVisibility(View.GONE);
            }
        }
    };

    PlayerListAdapter.ItemClickListener playerClickListener = new PlayerListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (pos >= 0 && pos < friendList.size()) {
                PlayerInfo info = friendList.get(pos);
                if (info != null && info.getInGame().equalsIgnoreCase("1")) {
                    if (FriendId !=null && !FriendId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("player_id", info.getId());
                        intent.putExtra("friendship_id", info.getFriendShipId());
                        intent.putExtra("is_team", false);
                        intent.putExtra("dotPosition", dotPosition);
                        intent.putExtra("friendId", FriendId);
                        startActivity(intent);
                    }else{
                        showOptionDialog(info, false, pos, null);
                    }
                }
                else {
                    if (info != null) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("player_id", info.getId());
                        intent.putExtra("friendship_id", info.getFriendShipId());
                        intent.putExtra("is_team", false);
                        intent.putExtra("dotPosition", dotPosition);
                        intent.putExtra("friendId",FriendId);
                        profileResultLauncher.launch(intent);
                    }
                    else {
                        gotoAddPlayer();
                    }
                }
            }

        }
    };

    ShirtsTeamAdapter.ItemClickListener teamClickListener = new ShirtsTeamAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedTeamId = teamList.get(pos).getId();
            subSelectedTeamId = selectedTeamId;
            teamAdapter.setSelectedId(selectedTeamId);
            selectedShirtId = "";
            shirtList.clear();
            shirtList.addAll(teamList.get(pos).getShirts());
            shirtAdapter.setSelectedId(selectedShirtId);
            teamPodition = pos;
            saveAppSettings(false, selectedTeamId, "team", playerAdapter.getChairUrl(), "","");

        }
    };

    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            subSelectedShirtId = shirt.getId();
            if (shirt.getType().equalsIgnoreCase("paid")){
                showAdsSubscriptionPopup(shirt.getId(), shirt.getPhotoUrl());
            } else{
                //this code was outside of else condition
                if (selectedTab == 0) {
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                        if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                            selectedShirtId = shirt.getId();
                            shirtAdapter.setSelectedId(selectedShirtId);
                            if (gameTeam !=null){
                                updateTeamShirtAPI(teamA.getId(),  selectedShirtId, "team");
                                teamA.setTeamShirt(shirt.getPhotoUrl());
                            }
                            // saveData("", "team_a_shirt", "", "", "", "", shirt.getPhotoUrl(), "", "", "");
                            for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                    PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                    if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(shirt.getPhotoUrl());
                                    }
                                }
                            }
                        }
                        else {
                            Functions.showToast(getContext(), getString(R.string.please_finalize_team_first), FancyToast.ERROR, Toast.LENGTH_SHORT);
                        }
                    }
                    else {
                        selectedShirtId = shirt.getId();
                        shirtAdapter.setSelectedId(selectedShirtId);
                        if (gameTeam !=null){
                            updateTeamShirtAPI(teamA.getId(),  selectedShirtId, "team");
                            teamA.setTeamShirt(shirt.getPhotoUrl());
                        }
                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                    vu.setImage(shirt.getPhotoUrl());
                                }
                            }
                        }
                    }
                }
                else {
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                        if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                            selectedShirtId = shirt.getId();
                            shirtAdapter.setSelectedId(selectedShirtId);
                            if (gameTeam !=null){
                                updateTeamShirtAPI(teamB.getId(), selectedShirtId, "team");
                                teamB.setTeamShirt(shirt.getPhotoUrl());
                            }
//                            saveData("", "team_b_shirt", "", "", "", "", "", "", shirt.getPhotoUrl(), "");
                            for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                    PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                    if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(shirt.getPhotoUrl());
                                    }
                                }
                            }
                        }
                        else {
                            Functions.showToast(getContext(), getString(R.string.please_finalize_team_first), FancyToast.ERROR, Toast.LENGTH_SHORT);
                        }
                    }
                    else {
                        selectedShirtId = shirt.getId();
                        shirtAdapter.setSelectedId(selectedShirtId);
                        if (gameTeam !=null){
                            updateTeamShirtAPI(teamB.getId(), selectedShirtId,"team");
                            teamB.setTeamShirt(shirt.getPhotoUrl());
                        }
                        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                    vu.setImage(shirt.getPhotoUrl());
                                }
                            }
                        }
                    }
                }

            }
        }
    };

    private void bounceButton(int duration) {
        float scaleUpFactor = 1.4f; // Increase scale factor for pop-up
        float scaleDownFactor = 1.0f;

        ScaleAnimation scaleUp = new ScaleAnimation(scaleDownFactor, scaleUpFactor, scaleDownFactor, scaleUpFactor,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(duration / 2);
        scaleUp.setFillAfter(true);

        ScaleAnimation scaleDown = new ScaleAnimation(scaleUpFactor, scaleDownFactor, scaleUpFactor, scaleDownFactor,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(duration / 2);
        scaleDown.setFillAfter(true);

        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.vsMode.startAnimation(scaleDown);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.vsMode.startAnimation(scaleUp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        binding.vsMode.startAnimation(scaleUp);
    }


    protected void showAdsSubscriptionPopup(String shirtId, String photoUrl) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("AdsSubscriptionPopupFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        AdsSubscriptionPopupFragment dialogFragment = new AdsSubscriptionPopupFragment(photoUrl);
        dialogFragment.setDialogCallback((df, choice) -> {
            df.dismiss();
            if (choice == 1){
                VibrationEffect effect = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                    vibrator.vibrate(effect);
                }
                //showAds Unlock Jersey
                rewardedAdManager.showRewardedAd();
                subscribedShirtId = shirtId;
                subscribedShirt = photoUrl;
                //showAds
                //Unlock Jersey
            }else if (choice == 0){
                VibrationEffect effect = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                    vibrator.vibrate(effect);
                }


                //show subscribe Activity
                Intent intent = new Intent(getContext(), SubscriptionActivity.class);
                intent.putExtra("country_id", selectedCountryId);
                intent.putExtra("team_id", selectedTeamId);
                intent.putExtra("shirt_id", selectedShirtId);
                subscriptionResultLauncher.launch(intent);
            }

        });
        dialogFragment.show(fragmentTransaction, "AdsSubscriptionPopupFragment");
    }

    protected void showCaptainWaitingPopup() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CaptainWaitingDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment = new CaptainWaitingDialogFragment();
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();
        });
        dialogFragment.show(fragmentTransaction, "CaptainWaitingDialogFragment");
    }
    protected void dismissCaptainWaitingPopup() {
        if (dialogFragment != null && dialogFragment.isAdded()) {
            dialogFragment.dismiss();
        }
    }
    protected void dismissCaptainDialogPopup() {
        if (captainDialogFragment != null && captainDialogFragment.isAdded()) {
            captainDialogFragment.dismiss();
        }
    }

    private void unlockOneJersey(String shrtId, String shirt) {
        KProgressHUD hud = Functions.showLoader(getContext());
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.unlockOneJersey(Functions.getAppLang(getContext()), shrtId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            gotoJerseypopup(shirt);

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

    private void gotoJerseypopup(String shirt) {
        getAllCountries(false);
        if(!Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry).isEmpty()){
            selectedCountryId = Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry);
        }
        getTeamAndShirtDetails(true, selectedCountryId);
        rewardedAdManager.setStatus(false);
        showUnlockedJerseyPopup(shirt);

    }

    protected void playSoundFromAssets(String fileName) {
        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            final MediaPlayer mp = new MediaPlayer();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mp.setDataSource(afd);
            } else {
                FileDescriptor fd = afd.getFileDescriptor();
                mp.setDataSource(fd, afd.getStartOffset(), afd.getLength());
            }
            afd.close();
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void showUnlockedJerseyPopup(String photoUrl) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("UnlockedJerseyPopupFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        UnlockedJerseyPopupFragment dialogFragment = new UnlockedJerseyPopupFragment(photoUrl);
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();
        });
        dialogFragment.show(fragmentTransaction, "UnlockedJerseyPopupFragment");
        playSoundFromAssets("congratulations_tone.mp3");


    }

    ChairListAdapter.ItemClickListener chairClickListener = new ChairListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedChairId = chairList.get(pos).getId();
            chairAdapter.setSelectedId(selectedChairId);
            saveAppSettings(false, selectedChairId, "chair", chairList.get(pos).getPhotoUrl(), "", "");
        }
    };

    FieldImageListAdapter.ItemClickListener fieldClickListener = new FieldImageListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            FieldImage fieldImage = fieldList.get(pos);
            selectedFieldId = fieldImage.getId();
            fieldAdapter.setSelectedId(selectedFieldId);
            saveAppSettings(false, selectedFieldId, "background", "", fieldImage.getBgImg(), fieldImage.getFieldImg());
        }
    };

    ShirtListAdapter.ItemClickListener gkClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (pos >= 0 && pos < gkShirtList.size()) {
                Shirt shirt = gkShirtList.get(pos);
                if (selectedTab == 0) {
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                        if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                            selectedGkShirtId = shirt.getId();
                            gkAdapter.setSelectedId(selectedGkShirtId);
//                            saveData("", "team_a_gk_shirt", "", "", "", "", "", shirt.getPhotoUrl(), "", "");
                            if (gameTeam !=null){
                                updateTeamShirtAPI(teamA.getId(),selectedGkShirtId,"goalkeeper");
                                teamA.setTeamGkShirt(shirt.getPhotoUrl());
                            }
                            for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                    PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                    if (vu.getPlayerInfo().getIsGoalkeeper() !=null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(shirt.getPhotoUrl());
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            Functions.showToast(getContext(), getString(R.string.please_finalize_team_first), FancyToast.ERROR, Toast.LENGTH_SHORT);
                        }
                    }
                    else {
                        selectedGkShirtId = shirt.getId();
                        gkAdapter.setSelectedId(selectedGkShirtId);
                        if (gameTeam !=null){
                            updateTeamShirtAPI(teamA.getId(), selectedGkShirtId,"goalkeeper");
                            teamA.setTeamGkShirt(shirt.getPhotoUrl());
                        }
                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                if (vu.getPlayerInfo().getIsGoalkeeper()!=null){ //checkx
                                    if (vu.getPlayerInfo().getIsGoalkeeper() !=null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(shirt.getPhotoUrl());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                        if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                            selectedGkShirtId = shirt.getId();
                            gkAdapter.setSelectedId(selectedGkShirtId);
//                            saveData("", "team_b_gk_shirt", "", "", "", "", "", "", "", shirt.getPhotoUrl());
                            if (gameTeam !=null){
                                updateTeamShirtAPI(teamB.getId(), selectedGkShirtId,"goalkeeper");
                                teamB.setTeamGkShirt(shirt.getPhotoUrl());
                            }
                            for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                    PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                    if (vu.getPlayerInfo().getIsGoalkeeper() !=null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(shirt.getPhotoUrl());
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            Functions.showToast(getContext(), getString(R.string.please_finalize_team_first), FancyToast.ERROR, Toast.LENGTH_SHORT);
                        }
                    }
                    else {
                        selectedGkShirtId = shirt.getId();
                        gkAdapter.setSelectedId(selectedGkShirtId);
                        if (gameTeam !=null){
                            updateTeamShirtAPI(teamB.getId(),selectedGkShirtId,"goalkeeper");
                            teamB.setTeamGkShirt(shirt.getPhotoUrl());
                        }
                        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                if (vu.getPlayerInfo().getIsGoalkeeper() !=null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                    vu.setImage(shirt.getPhotoUrl());
                                    break;
                                }
                            }
                        }
                    }
                }
            }else{
                Log.e("ShirtListAdapter", "Invalid index: " + pos);
            }
        }
    };

    View.OnDragListener vuDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v == binding.tabTeamA) {
                        binding.tabTeamA.setCardBackgroundColor(Color.GREEN);
                    }
                    else if (v == binding.tabTeamB) {
                        binding.tabTeamB.setCardBackgroundColor(Color.GREEN);
                    }
                    else if (v == binding.vuTeamA) {
                        binding.vuTeamA.setBackgroundColor(getResources().getColor(R.color.greenColor50));
                    }
                    else if (v == binding.vuTeamB) {
                        binding.vuTeamB.setBackgroundColor(getResources().getColor(R.color.greenColor50));
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED: case DragEvent.ACTION_DRAG_ENDED:
                    if (v == binding.tabTeamA) {
                        binding.tabTeamA.setCardBackgroundColor(Color.parseColor("#29000000"));
                    }
                    else if (v == binding.tabTeamB) {
                        binding.tabTeamB.setCardBackgroundColor(Color.parseColor("#29000000"));
                    }
                    else if (v == binding.vuTeamA) {
                        binding.vuTeamA.setBackgroundColor(Color.TRANSPARENT);
                    }
                    else if (v == binding.vuTeamB) {
                        binding.vuTeamB.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    final DragData state = (DragData) event.getLocalState();
                    if (v == binding.tabTeamA || v == binding.vuTeamA) {
                        if (gameTeam !=null && gameTeam.getCreatedBy() !=null && !gameTeam.getCreatedBy().isEmpty() && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) & !teamACaptainAvailable){
                            Functions.showToast(getContext(), getString(R.string.you_are_not_captain), FancyToast.ERROR, FancyToast.LENGTH_LONG);

                        }else{
                            if (teamACaptainAvailable) {
                                if (captainATurn && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                                    if (binding.vuTeamA.getChildCount() < limit) {
                                        if (v == binding.vuTeamA) {
                                            replaceViewTeamA(state, (int) event.getX(), (int) event.getY(), true);
                                        } else {
                                            replaceViewTeamA(state, 0, 0, true);
                                        }

                                        teamA.getPlayers().add(state.getItem());
                                        friendList.remove(state.getPos());
                                        playerAdapter.notifyDataSetChanged();
                                        binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                    }
                                    else {
//                                        saveData("", "add", teamACaptainId, "", "", "", "", "", "", "");
                                    }
                                }
                            }
                            else {


                                if (v == binding.vuTeamA) {
                                    replaceViewTeamA(state, (int)event.getX(), (int)event.getY(), true);
                                }
                                else {
                                    replaceViewTeamA(state, 0, 0, true);
                                }
                                teamA.getPlayers().add(state.getItem());
                                friendList.remove(state.getPos());
                                binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                playerAdapter.notifyDataSetChanged();

                            }
                            if (binding.vuTeamA.getChildCount() == 8 || binding.vuTeamB.getChildCount() == 8) {
                                binding.vuTeamA.removeAllViews();
                                binding.vuTeamB.removeAllViews();
                                for (PlayerInfo info : teamA.getPlayers()) {
                                    replaceViewTeamA(new DragData(info, -1), 0, 0, false);
                                }

                                for (PlayerInfo info : teamB.getPlayers()) {
                                    replaceViewTeamB(new DragData(info, -1), 0, 0, false);
                                }
                            }
                        }

                    }
                    else if (v == binding.tabTeamB || v == binding.vuTeamB) {
                        if (!gameTeam.getCreatedBy().isEmpty() && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) & !teamBCaptainAvailable){
                            Functions.showToast(getContext(), getString(R.string.you_are_not_captain), FancyToast.ERROR, FancyToast.LENGTH_LONG);
                        }else{
                            if (teamBCaptainAvailable) {
                                if (captainBTurn && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                                    if (binding.vuTeamB.getChildCount() < limit) {
                                        if (v == binding.vuTeamB) {
                                            replaceViewTeamB(state, (int) event.getX(), (int) event.getY(), true);
                                        } else {
                                            replaceViewTeamB(state, 0, 0, true);
                                        }
                                        teamB.getPlayers().add(state.getItem());
                                        friendList.remove(state.getPos());
                                        playerAdapter.notifyDataSetChanged();
                                        binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                    }
                                    else {
//                                        saveData("", "add", teamBCaptainId, "", "", "", "", "", "", "");
                                    }
                                }
                            }
                            else {
                                if (v == binding.vuTeamB) {
                                    replaceViewTeamB(state, (int)event.getX(), (int)event.getY(), true);
                                }
                                else {
                                    replaceViewTeamB(state, 0, 0, true);
                                }
                                teamB.getPlayers().add(state.getItem());
                                friendList.remove(state.getPos());
                                binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                playerAdapter.notifyDataSetChanged();

                            }
                            if (binding.vuTeamA.getChildCount() == 8 || binding.vuTeamB.getChildCount() == 8) {
                                binding.vuTeamA.removeAllViews();
                                binding.vuTeamB.removeAllViews();
                                for (PlayerInfo info : teamA.getPlayers()) {
                                    replaceViewTeamA(new DragData(info, -1), 0, 0, false);
                                }

                                for (PlayerInfo info : teamB.getPlayers()) {
                                    replaceViewTeamB(new DragData(info, -1), 0, 0, false);
                                }
                            }
                        }

                    }

                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void callUnreadNotifAPI() {
        getUnreadNotificationAPI(new BaseActivity.UnreadCountCallback() {
            @Override
            public void unreadNotificationCount(int count) {
                AppManager.getInstance().notificationCount = count;
                if (count > 0) {
                    binding.toolbarBadge.setVisibility(View.VISIBLE);
                    binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
                }
                else  {
                    binding.toolbarBadge.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showOptionDialog(PlayerInfo info, boolean isTeam, int pos, PreviewFieldView view) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FriendOptionsDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        FriendOptionsDialogFragment dialogFragment = new FriendOptionsDialogFragment(isTeam, info, dotPosition, teamA, teamB);
        boolean isShowRemovePlayer = true;
        boolean isShowSwap = false;
        if (teamACaptainAvailable || teamBCaptainAvailable) {
            int limit = Integer.parseInt(gameTeam.getGamePlayers()) / 2;
            if (isTeam && binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                isShowRemovePlayer = false;
                isShowSwap = true;
            }
        }
        else {
            int limit = Integer.parseInt(gameTeam.getGamePlayers()) / 2;
            if (isTeam && binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                isShowSwap = true;
            }
        }

        dialogFragment.setData(teamA.getTeamName(), teamB.getTeamName(), teamACaptainAvailable, teamBCaptainAvailable, isShowRemovePlayer, true, isShowSwap);
        dialogFragment.setDialogCallback(new FriendOptionsDialogFragment.FriendOptionsDialogFragmentCallback() {
            @Override
            public void makeCaptain(DialogFragment df) {
                df.dismiss();
                if (info.getIsManual() !=null && info.getIsManual().equalsIgnoreCase("1")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Not a Register Player")
                            .setMessage("You can't make non registered player as captain, Please link this player first to make as captain!")
                            .setPositiveButton("Link Player", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                                    intent.putExtra("player_id", info.getId());
                                    intent.putExtra("friendship_id", info.getFriendShipId());
                                    intent.putExtra("is_team", isTeam);
                                    profileResultLauncher.launch(intent);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    builder.show();

                    Functions.showToast(getContext(), "This is manual player you cannot make captain", FancyToast.ERROR, FancyToast.LENGTH_LONG);
                }
                else{
                    // df.dismiss();
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        Functions.showToast(getContext(), getString(R.string.captain_already_exist), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                        return;
                    }
                    if (binding.vuTeamA.getChildCount() > 0 || binding.vuTeamB.getChildCount() > 0) {
                        Functions.showToast(getContext(), getString(R.string.remove_player_before_make_captain), FancyToast.ERROR, FancyToast.LENGTH_LONG);
                        return;
                    }

                    if (gameTeam.getGameType() !=null && !gameTeam.getGameType().isEmpty() && !gameTeam.getGameType().equalsIgnoreCase("normal")) {

                        Functions.showToast(getContext(), "Please Click the VS icon to divide players.", FancyToast.ERROR, FancyToast.LENGTH_LONG);

                    }else{
                        showTeamDialog(info);
                    }

                }
            }

            @Override
            public void removeCaptain(DialogFragment df) {
                df.dismiss();
                if (!gameTeam.getCreatedBy().isEmpty() && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(),Constants.kUserID))){
                    df.dismiss();
                    Functions.showToast(getContext(), getString(R.string.only_game_creator_can_remove_you), FancyToast.ERROR);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getResources().getString(R.string.remove_captain))
                            .setMessage(getResources().getString(R.string.do_you_want_remove_captain))
                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (selectedTab == 0) {
                                        removeCaptainAPI(true, teamA.getId(), info.getId(), gameTeam.getGameId());
                                    }
                                    else {
                                        removeCaptainAPI(true, teamB.getId(), info.getId(),gameTeam.getGameId());
                                    }
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

            @Override
            public void substitute(DialogFragment df) {
                df.dismiss();
                if (gameTeam.getCreatedBy() !=null && !gameTeam.getCreatedBy().isEmpty() && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(),Constants.kUserID))) {
                    df.dismiss();
                    Functions.showToast(getContext(), getString(R.string.only_game_creator_can_replace_player), FancyToast.ERROR);

                }
                else {
                    if (isTeam) {
                        if (view.getPlayerInfo()!=null &&  view.getPlayerInfo().getIsCaptain() !=null  && view.getPlayerInfo().getIsCaptain().equalsIgnoreCase("1")){
                            Functions.showToast(getContext(), "You cannot replace captain, please choose another player", FancyToast.ERROR);
                        }
                        if (view.getPlayerInfo() !=null){
                            selectedPlayerForSubstitute = view.getPlayerInfo();
                        }else {
                            Functions.showToast(getContext(), "please try again", FancyToast.ERROR);
                            return;
                        }
                    }
                    else {
                        selectedPlayerForSubstitute = friendList.get(pos);
                    }

                    Intent intent = new Intent(getContext(), FriendsListActivity.class);
                    intent.putExtra("substitute", true);
                    intent.putExtra("game_id", gameTeam.getGameId());
                    intent.putExtra("friend_id", FriendId);
                    intent.putExtra("players", "1");
                    intent.putExtra("is_team", isTeam);
                    intent.putExtra("vs_mode", false);
                    substituteResultLauncher.launch(intent);
                }
            }

            @Override
            public void profile(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("player_id", info.getId());
                intent.putExtra("friendship_id", info.getFriendShipId());
                intent.putExtra("is_team", isTeam);
                profileResultLauncher.launch(intent);
            }

            @Override
            public void manualEdit(DialogFragment df) {
                df.dismiss();
                showEditPlayerDialog(info,isTeam);
            }

            @Override
            public void remove(DialogFragment df) {
                df.dismiss();
                playSoundFromAssets("kickout.mpeg");
                if (info.getId().equalsIgnoreCase(teamACaptainId) || info.getId().equalsIgnoreCase(teamBCaptainId)) {
                    if (selectedTab == 0) {
                        removeCaptainAPI(true, teamA.getId(), info.getId(), gameTeam.getGameId());
                    }
                    else {
                        removeCaptainAPI(true, teamB.getId(), info.getId(),gameTeam.getGameId());
                    }
                }
                else {
                    if (selectedTab == 0) {
                        removePlayerFromTeamAPI(true, info.getId(), info.getFriendShipId(), teamA.getId(), view);
                    } else {
                        removePlayerFromTeamAPI(true, info.getId(), info.getFriendShipId(), teamB.getId(), view);
                    }
                }
            }

            @Override
            public void status(DialogFragment df) {
                df.dismiss();
                showStatusDialog(info);
            }

            @Override
            public void swapPlayer(DialogFragment df) {
                df.dismiss();
                if (selectedTab == 0) {
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                            showSwapDialog(info, teamA.getId());
                        }
                    }
                    else {
                        showSwapDialog(info, teamA.getId());
                    }
                }
                else {
                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                        if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                            showSwapDialog(info, teamB.getId());
                        }
                    }
                    else {
                        showSwapDialog(info, teamB.getId());
                    }
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "FriendOptionsDialogFragment");
    }

    private void showEditPlayerDialog(PlayerInfo info , Boolean isTeam) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("AddPlayerFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        AddPlayerFragment playerFragment = new AddPlayerFragment(info, isTeam);
        playerFragment.setDialogCallback(new AddPlayerFragment.AddPlayerDialogCallback() {
            @Override
            public void didAddPlayer(PlayerInfo userInfo) {

            }

            @Override
            public void didUpdatePlayer(PlayerInfo userInfo) {
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void didDeletePlayer(String id) {
                Intent intent = new Intent();
                intent.putExtra("is_team", isTeam);
                setResult(RESULT_OK, intent);
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        playerFragment.show(fragmentTransaction, "AddPlayerFragment");
    }

    private void showSwapDialog(PlayerInfo info, String teamId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("SwapPlayerDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        SwapPlayerDialogFragment dialogFragment = new SwapPlayerDialogFragment(gameTeam, teamA, teamB, binding.fieldBgImgVu.getDrawable(), binding.fieldImgVu.getDrawable(), info.getId(), info.getFriendShipId(), teamId, info.getIsCaptain());
        dialogFragment.setDialogCallback(new SwapPlayerDialogFragment.SwapPlayerDialogFragmentCallback() {
            @Override
            public void swapDone(DialogFragment df) {
                df.dismiss();
            }
        });
        dialogFragment.show(fragmentTransaction, "SwapPlayerDialogFragment");
    }

    private void showStatusDialog(PlayerInfo info) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("PlayerStatusDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        PlayerStatusDialogFragment dialogFragment = new PlayerStatusDialogFragment(info);
        dialogFragment.setDialogCallback(new PlayerStatusDialogFragment.PlayerStatusDialogFragmentCallback() {
            @Override
            public void statusDone(DialogFragment df) {
                df.dismiss();
                // getFormationDetails(true,GameID); //removed

            }
        });
        dialogFragment.show(fragmentTransaction, "PlayerStatusDialogFragment");
    }

    private void showTeamDialog(PlayerInfo info) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ChooseTeamDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        ChooseTeamDialogFragment dialogFragment = new ChooseTeamDialogFragment(info, GameID, FriendId, gameTeam, teamA, teamB);
        dialogFragment.setDialogFragmentCallback(new ChooseTeamDialogFragment.ChooseTeamDialogFragmentCallback() {
            @Override
            public void captainAdded(DialogFragment df, String capAname, String capBname) {
                teamACaptainName = capAname;
                teamBCaptainName = capBname;
                df.dismiss();
                showPlayerChooseAlert();
            }
        });
        dialogFragment.show(fragmentTransaction, "ChooseTeamDialogFragment");
    }

    private void gotoAddPlayer() {
        Intent intent = new Intent(getContext(), PlayerListActivity.class);
        addPlayerResultLauncher.launch(intent);

    }

    private void showLineupDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("StartLineupDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        StartLineupDialogFragment dialogFragment = new StartLineupDialogFragment();
        dialogFragment.setDialogCallback(new StartLineupDialogFragment.StartLineupDialogCallback() {
            @Override
            public void didStartLineup(String date, String time, String players, String stadiumName, String cityName) {

                Intent intent = new Intent(getContext(), FriendsListActivity.class);
                intent.putExtra("substitute", false);
                intent.putExtra("game_date", date);
                intent.putExtra("game_time", time);
                intent.putExtra("players",   players);
                intent.putExtra("club_name", stadiumName);
                intent.putExtra("city_name", cityName);
                intent.putExtra("friend_id", FriendId);
                activityResultLauncher.launch(intent);

            }
        });

        dialogFragment.show(fragmentTransaction, "StartLineupDialogFragment");

    }

    private void showEditDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("EditGameDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        EditGameDialogFragment dialogFragment = new EditGameDialogFragment(gameTeam,teamA,teamB);
        dialogFragment.setDialogCallback(new EditGameDialogFragment.EditGameDialogCallback() {
            @Override
            public void didUpdate(String teamAname, String teamBName, String date, String time, String players,String stadiumName, String cityName) {
                teamA.setTeamName(teamAname);
                teamB.setTeamName(teamBName);
                gameTeam.setGameDate(date);
                gameTeam.setGameTime(time);
                gameTeam.setGamePlayers(players);
                gameTeam.setClubName(stadiumName);
                gameTeam.setCityName(cityName);
                setTabName();
            }

            @Override
            public void didAddPlayers() {
                gameTeam.setIsGameOn("1");
                playerAdapter.setIsGameOn("1", false);
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
        dialogFragment.show(fragmentTransaction, "EditGameDialogFragment");
    }

    private void setTabName() {

        if (teamACaptainName.equalsIgnoreCase("")) {
            binding.tvTeamA.setText(teamA.getTeamName());
        }
        else {
            binding.tvTeamA.setText(String.format("%s(%s)", teamA.getTeamName(), teamACaptainName));
        }
        if (teamBCaptainName.equalsIgnoreCase("")) {
            binding.tvTeamB.setText(teamB.getTeamName());
        }
        else {
            binding.tvTeamB.setText(String.format("%s(%s)", teamB.getTeamName(), teamBCaptainName));
        }
    }

    private void showResultDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("GameResultDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        GameResultDialogFragment dialogFragment = new GameResultDialogFragment(gameTeam,teamA,teamB);
        dialogFragment.setDialogCallback(new GameResultDialogFragment.ResultDialogCallback() {
            @Override
            public void didSubmitResult(DialogFragment df, String gameId, String winnerTeamId, String loserTeamId, String isDraw, boolean isCancelled) {
                df.dismiss();

                if (!isCancelled) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("game_id", gameId);
                        data.put("winner_id", winnerTeamId);
                        data.put("looser_id", loserTeamId);
                        data.put("is_draw", isDraw);
                        socket.emit("game:complete", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showBestPlayerDialog(gameTeam.getGameId());
                    listItems.get(0).setGameId("");
                    listItems.get(0).setInGame("0");
                    //itemsPager_adapter.notifyDataSetChanged();
                    pager_indicator.removeAllViews();
                    itemsPager_adapter = new SliderAdapter(getContext(), listItems, page);
                    itemsPager_adapter.setOnItemClickListener(MainActivity.this);
                    page.setAdapter(itemsPager_adapter);
                    setPageViewIndicator();
                    itemsPager_adapter.notifyDataSetChanged();
                } else{
                    try {
                        JSONObject data = new JSONObject();
                        data.put("game_id", gameId);
                        socket.emit("game:reset",data);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                JSONObject dataa = new JSONObject();
                try {
                    dataa.put("player_id", FriendId);
                    socket.emit("player:friends", dataa);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "GameResultDialogFragment");
    }

    //clear
    private void showWinGameDialog(GameHistory result, String popupId, String msg) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ResultDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        ResultDialogFragment dialogFragment = new ResultDialogFragment(result, popupId, msg);
        dialogFragment.setDialogFragmentCallback(new ResultDialogFragment.ResultDialogFragmentCallback() {
            @Override
            public void shareClicked(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), ResultShareActivity.class);
                intent.putExtra("result", new Gson().toJson(result));
                startActivity(intent);
            }
            @Override
            public void onDismiss(DialogFragment df) {
                df.dismiss();
                checkCaptainAPI();
            }
        });
        dialogFragment.show(fragmentTransaction, "ResultDialogFragment");
    }

    private void showCaptainDialog(String popupId, String msg, String gameId, String type) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ResultDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        captainDialogFragment = new GameCaptainDialogFragment(popupId, msg, type);
        captainDialogFragment.setDialogFragmentCallback(new GameCaptainDialogFragment.GameCaptainDialogFragmentCallback() {
            @Override
            public void gameClicked(DialogFragment df) {
                df.dismiss();
                if (!type.equalsIgnoreCase("captainRemoved")) {
                    //getGroups();
                    for (int i=0; i < listItems.size(); i++){
                        if (listItems.get(i).getGameId().equalsIgnoreCase(gameId)){
                            page.setCurrentItem(i);
                            Functions.setCurrentPage(getContext(), String.valueOf(i));
                            setPageViewIndicator();
                        }
                    }
                }
            }
        });
        captainDialogFragment.show(fragmentTransaction, "ResultDialogFragment");
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {

                vsMode = result.getData().getExtras().getBoolean("vs_mode");
                teamACaptainName = result.getData().getExtras().getString("teamA_captain_name");
                teamBCaptainName = result.getData().getExtras().getString("teamB_captain_name");
                if (vsMode){
                    JSONObject data = new JSONObject();
                    try {

                        //Create the JSON structure for your data
                        JSONObject TeamA = new JSONObject();
                        TeamA.put("team_id", teamAId);
                        TeamA.put("captain_id", captainAId);
                        TeamA.put("captain_friendship_id", captainAFriendshipId);

                        JSONObject TeamB = new JSONObject();
                        TeamB.put("team_id", teamBId);
                        TeamB.put("captain_id", captainBId);
                        TeamB.put("captain_friendship_id",captainBFriendshipId);

                        data.put("game_id", gameTeam.getGameId());
                        data.put("selection_type", turnValue);
                        data.put("team_a", TeamA);
                        data.put("team_b", TeamB);

                        //Emit the JSON object over the socket
                        socket.emit("game:make-captain", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    recreate(); //Temp Solution
                }

                //boolean isAdded = result.getData().getExtras().getBoolean("is_added");
                // if (isAdded) {
//                    gameTeam.setIsGameOn("1");
//                    gameTeam.setGamePlayersCount(gamePlayers);
//                    playerAdapter.setIsGameOn("1", false);
//                    //getFriends(true,false,FriendId); //after add to game
//                    JSONObject data = new JSONObject();
//                    try {
//                        data.put("player_id", FriendId);
//                        socket.emit("player:friends", data);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                // }
            }
        }
    });
    ActivityResultLauncher<Intent> captainResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {

                boolean isAdded = result.getData().getExtras().getBoolean("is_added");
                if (isAdded){

                    teamAId = result.getData().getExtras().getString("team_a_id");
                    captainAId = result.getData().getExtras().getString("captain_a_id");
                    captainAFriendshipId = result.getData().getExtras().getString("captain_a_friendship_id");
                    teamBId = result.getData().getExtras().getString("team_b_id");
                    captainBId = result.getData().getExtras().getString("captain_b_id");
                    captainBFriendshipId = result.getData().getExtras().getString("captain_b_friendship_id");
                    turnValue = result.getData().getExtras().getString("turn_value");
                    teamACaptainName = result.getData().getExtras().getString("teamA_captain_name");
                    teamBCaptainName = result.getData().getExtras().getString("teamB_captain_name");

                    Intent intent = new Intent(getContext(), FriendsListActivity.class);
                    intent.putExtra("substitute", false);
                    intent.putExtra("game_id", gameTeam.getGameId());
                    intent.putExtra("game_date", gameTeam.getGameDate());
                    intent.putExtra("game_time", gameTeam.getGameTime());
                    intent.putExtra("players",   gameTeam.getGamePlayers());
                    intent.putExtra("club_name", "");
                    intent.putExtra("city_name", "");
                    intent.putExtra("friend_id", FriendId);
                    intent.putExtra("vs_mode", true);
                    intent.putExtra("captain_a_id", captainAId);
                    intent.putExtra("team_a_id", teamAId);
                    intent.putExtra("captain_b_id", captainBId);
                    intent.putExtra("team_b_id", teamBId);
                    intent.putExtra("teamA_captain_name", teamACaptainName);
                    intent.putExtra("teamB_captain_name", teamBCaptainName);

                    activityResultLauncher.launch(intent);
                }else{
                    JSONObject dataa = new JSONObject();
                    try {
                        dataa.put("game_id", gameTeam.getGameId());
                        socket.emit("game:join", dataa);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dismissCaptainDialogPopup();
                }


            }
        }
    });
    ActivityResultLauncher<Intent> subscriptionResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK) {
                boolean isSubscribed = result.getData().getExtras().getBoolean("is_subscribed");
                if (isSubscribed){
                    getAllCountries(false);
                    if(!Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry).isEmpty()){
                        selectedCountryId = Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry);
                    }
                    getTeamAndShirtDetails(true, selectedCountryId);
                }

            }
        }
    });

    ActivityResultLauncher<Intent> profileResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                boolean isTeam = result.getData().getExtras().getBoolean("is_team");
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    });

    ActivityResultLauncher<Intent> substituteResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                String player = result.getData().getExtras().getString("player");
                PlayerInfo newPlayer = new Gson().fromJson(player, PlayerInfo.class);
                substitutePlayerAPI(true, selectedPlayerForSubstitute.getId(), selectedPlayerForSubstitute.getFriendShipId(), newPlayer.getId(), newPlayer.getFriendShipId(), "");
            }
            else if (result.getResultCode() == 123) {
                String player = result.getData().getExtras().getString("player");
                PlayerInfo newPlayer = new Gson().fromJson(player, PlayerInfo.class);
                if (selectedTab == 0) {
                    substitutePlayerAPI(true, selectedPlayerForSubstitute.getId(), selectedPlayerForSubstitute.getFriendShipId(), newPlayer.getId(), newPlayer.getFriendShipId(), teamA.getId());
                }
                else {
                    substitutePlayerAPI(true, selectedPlayerForSubstitute.getId(), selectedPlayerForSubstitute.getFriendShipId(), newPlayer.getId(), newPlayer.getFriendShipId(), teamA.getId());
                }
            }
        }
    });

    ActivityResultLauncher<Intent> addPlayerResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                String player = result.getData().getExtras().getString("player");
                PlayerInfo newPlayer = new Gson().fromJson(player, PlayerInfo.class);
                friendList.add(friendList.size() - 1, newPlayer);
                friendsCount = friendsCount + 1;
                showHideBtnVu();
                playerAdapter.notifyDataSetChanged();
            }
            else if (result.getResultCode() == 456) {
//                getFriends(true,false,FriendId);
                JSONObject data = new JSONObject();
                try {
                    data.put("player_id", FriendId);
                    socket.emit("player:friends", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    });

    @Override
    public void onClick(View v) {
        if (v == binding.btnMenu) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
        else if (v == binding.tabTeamA || v == binding.tabTeamB) {
            tabSelected(v);
        }
        else if (v == binding.btnHideFace){
            if (gameTeam !=null){
                if (gameTeam.getIsGameOn() != null && gameTeam.getIsGameOn().equalsIgnoreCase("1")){
                    binding.btnChair.setImageResource(R.drawable.chair_inactive);
                    binding.btnShirt.setImageResource(R.drawable.shirt_inactive);
                    binding.btnField.setImageResource(R.drawable.field_inactivel);
                    binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_inactivel);
                    if (Functions.getPrefValue(getContext(), Constants.kFaceHide).equalsIgnoreCase("false")){
                        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                        editor.putString(Constants.kFaceHide, "true");
                        editor.apply();
                        binding.btnHideFace.setImageResource(R.drawable.face_active);
                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                vu.hideShowface(true);
                            }
                        }
                        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                vu.hideShowface(true);
                            }
                        }
                    }
                    else{
                        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                        editor.putString(Constants.kFaceHide, "false");
                        editor.apply();
                        binding.btnHideFace.setImageResource(R.drawable.face_inactive);
                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                vu.hideShowface(false);
                            }
                        }
                        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                vu.hideShowface(false);
                            }
                        }
                    }
//                    binding.vuTeamA.removeAllViews();
//                    binding.vuTeamB.removeAllViews();
//                    populateTeamData();
                    //playerAdapter.notifyDataSetChanged();
                }
            }else{
                Functions.showToast(getContext(),"To use this feature, Please start the game first.", FancyToast.ERROR);
            }

        }
        else if (v == binding.btnChair) {
            getAllChairs(false);
            binding.btnChair.setImageResource(R.drawable.chair_active);
            binding.btnShirt.setImageResource(R.drawable.shirt_inactive);
            binding.btnField.setImageResource(R.drawable.field_inactivel);
            binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_inactivel);
            binding.btnHideFace.setImageResource(R.drawable.face_inactive);
            binding.chairRecyclerVu.setVisibility(View.VISIBLE);
            binding.teamRecyclerVu.setVisibility(View.GONE);
            binding.shirtRecyclerVu.setVisibility(View.GONE);
            binding.fieldRecyclerVu.setVisibility(View.GONE);
            binding.gkRecyclerVu.setVisibility(View.GONE);
            binding.countryVu.setVisibility(View.INVISIBLE);
            binding.logo.setVisibility(View.VISIBLE);
        }
        else if (v == binding.btnShirt) {
            getAllCountries(false);
            if(!Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry).isEmpty()){
                selectedCountryId = Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry);
            }
            getTeamAndShirtDetails(true, selectedCountryId);
            binding.btnChair.setImageResource(R.drawable.chair_inactive);
            binding.btnShirt.setImageResource(R.drawable.shirt_active);
            binding.btnField.setImageResource(R.drawable.field_inactivel);
            binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_inactivel);
            binding.btnHideFace.setImageResource(R.drawable.face_inactive);
            binding.teamRecyclerVu.setVisibility(View.VISIBLE);
            binding.shirtRecyclerVu.setVisibility(View.VISIBLE);
            binding.chairRecyclerVu.setVisibility(View.GONE);
            binding.fieldRecyclerVu.setVisibility(View.GONE);
            binding.gkRecyclerVu.setVisibility(View.GONE);
            binding.countryVu.setVisibility(View.VISIBLE);
            binding.logo.setVisibility(View.INVISIBLE);
        }
        else if (v == binding.btnField) {
            getAllFields(false);
            binding.btnChair.setImageResource(R.drawable.chair_inactive);
            binding.btnShirt.setImageResource(R.drawable.shirt_inactive);
            binding.btnField.setImageResource(R.drawable.field_activel);
            binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_inactivel);
            binding.btnHideFace.setImageResource(R.drawable.face_inactive);
            binding.fieldRecyclerVu.setVisibility(View.VISIBLE);
            binding.chairRecyclerVu.setVisibility(View.GONE);
            binding.teamRecyclerVu.setVisibility(View.GONE);
            binding.shirtRecyclerVu.setVisibility(View.GONE);
            binding.gkRecyclerVu.setVisibility(View.GONE);
            binding.countryVu.setVisibility(View.INVISIBLE);
            binding.logo.setVisibility(View.VISIBLE);
        }
        else if (v == binding.btnGkShirts) {
            getGoalKeeperShirts(false);
            binding.btnChair.setImageResource(R.drawable.chair_inactive);
            binding.btnShirt.setImageResource(R.drawable.shirt_inactive);
            binding.btnField.setImageResource(R.drawable.field_inactivel);
            binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_activel);
            binding.btnHideFace.setImageResource(R.drawable.face_inactive);
            binding.fieldRecyclerVu.setVisibility(View.GONE);
            binding.chairRecyclerVu.setVisibility(View.GONE);
            binding.teamRecyclerVu.setVisibility(View.GONE);
            binding.shirtRecyclerVu.setVisibility(View.GONE);
            binding.gkRecyclerVu.setVisibility(View.VISIBLE);
            binding.countryVu.setVisibility(View.INVISIBLE);
            binding.logo.setVisibility(View.VISIBLE);
        }
        else if (v == binding.countryVu) {

            List<SelectionList> selectionList = new ArrayList<>();
            for (int i = 0; i < countryList.size(); i++) {
                selectionList.add(new SelectionList(String.valueOf(i), countryList.get(i).getName(), countryList.get(i).getFlag()));
            }
            SelectionListDialog dialog = new SelectionListDialog(getContext(), getString(R.string.select_country), false);
            dialog.setLists(selectionList);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new SelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<SelectionList> selectedItems) {
                    SelectionList item = selectedItems.get(0);
                    Country country = countryList.get(Integer.parseInt(item.getId()));
                    binding.tvCountry.setText(country.getShortName());
                    selectedCountryId = country.getId();
                    getTeamAndShirtDetails(true,selectedCountryId);
                    selectedTeamId = "";
                    selectedShirtId = "";
                    Functions.setSelectedCountry(getContext(), selectedCountryId);
                    Log.d("selectedCountryId", item.getId());  // countryID
                    populateCountryData(country);
                    saveAppSettings(false, selectedCountryId, "country", playerAdapter.getChairUrl(), "", "");

                }
            });
            dialog.show();
        }
        else if (v == binding.logoutVu) {
            signOutClicked();
            //Functions.setSelectedCountry(getContext(), "0");
        }
        else if (v == binding.subscriptionVu) {
            String packageName = getPackageName();
            Uri uri = Uri.parse("https://play.google.com/store/account/subscriptions?package=" + packageName);
            Intent manageSubscriptionIntent = new Intent(Intent.ACTION_VIEW, uri);
            //Ensure that the Play Store app is installed on the device before launching the intent
            if (manageSubscriptionIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(manageSubscriptionIntent);
            }

        } else if (v == binding.shopVu) {
            Functions.showToast(getContext(), "Coming Soon!", FancyToast.ERROR);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (v == binding.comparisonVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), PlayerComparisonActivity.class);
            startActivity(intent);
        }
        else if (v == binding.groupVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), MyGroupsActivity.class);
            startActivity(intent);
        }
        else if (v == binding.suggestVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), SuggestJerseyActivity.class);
            startActivity(intent);
        }
        else if (v == binding.useVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), HowToUse.class);
            startActivity(intent);
        }
        else if (v == binding.contactVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            String url = "https://api.whatsapp.com/send?phone=+971547215551";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else if (v == binding.friendRequestVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), FriendRequestActivity.class);
            startActivity(intent);
            // String url = "https://api.whatsapp.com/send?phone=+971547215551";
            // Intent i = new Intent(Intent.ACTION_VIEW);
            // i.setData(Uri.parse(url));
            // startActivity(i);
        }
        else if (v == binding.gameHistoryVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), GameHistoryActivity.class);
            startActivity(intent);
        }
        else if (v == binding.headerVu) { //checkx Header mn edit ka button nh show ho rha
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("player_id", "");
            intent.putExtra("is_team", false);
            profileResultLauncher.launch(intent);
        }
        else if (v == binding.addPlayerVu) {
            if(dotPosition !=null && !dotPosition.equalsIgnoreCase("0") && !dotPosition.equalsIgnoreCase("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Go To My Group")
                        .setMessage("Go to your group to start lineup")
                        .setPositiveButton("My Group", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                page.setCurrentItem(0);
                                Functions.setCurrentPage(getContext(), String.valueOf(0));
                                // page.addOnPageChangeListener(onPageChangeListener);
                                setPageViewIndicator();
//                                String kCurrentpage = Functions.getCurrentPage(getContext(), Constants.kCurrentPage);
//                                onPageChangeListener.onPageSelected(Integer.parseInt(kCurrentpage)); //checkx
//                                page.setCurrentItem(Integer.parseInt(kCurrentpage));

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                builder.show();
            }
            else {
                gotoAddPlayer();
            }
        }
        else if (v == binding.editGameVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            if (gameTeam != null) {
                showEditDialog();
            }
        }
        else if (v == binding.startVu) {
            VibrationEffect effect = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                vibrator.vibrate(effect);
            }
            if(dotPosition !=null && !dotPosition.equalsIgnoreCase("0") && !dotPosition.equalsIgnoreCase("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Go To My Group")
                        .setMessage("Go to your group to start lineup")
                        .setPositiveButton("My Group", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                page.setCurrentItem(0);
                                Functions.setCurrentPage(getContext(), String.valueOf(0));
                                setPageViewIndicator();
//                                page.addOnPageChangeListener(onPageChangeListener);
//                                String kCurrentpage = Functions.getCurrentPage(getContext(), Constants.kCurrentPage);
//                                onPageChangeListener.onPageSelected(Integer.parseInt(kCurrentpage)); //checkx
//                                page.setCurrentItem(Integer.parseInt(kCurrentpage));

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                builder.show();
            }
            else if (friendList.size() > 8) {
                showLineupDialog();
            }
            else {
                Functions.showToast(getContext(), getString(R.string.minimum_8_players_required_start_lineup), FancyToast.ERROR);
            }

        }

        else if (v == binding.fastLineup){

            VibrationEffect effect = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                vibrator.vibrate(effect);
            }
            if(dotPosition !=null && !dotPosition.equalsIgnoreCase("0") && !dotPosition.equalsIgnoreCase("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Go To My Group")
                        .setMessage("Go to your group to start lineup")
                        .setPositiveButton("My Group", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                page.setCurrentItem(0);
                                Functions.setCurrentPage(getContext(), String.valueOf(0));
                                setPageViewIndicator();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                builder.show();
            }
            else if (friendList.size() > 8) {
                fastLineupLoader = Functions.showLoader(getContext());
                fastLineup();
                binding.fastLineup.setEnabled(false);
            }
            else {
                Functions.showToast(getContext(), getString(R.string.minimum_8_players_required_start_lineup), FancyToast.ERROR);
            }
        }

        else if (v == binding.btnShare) {
            if (gameTeam != null && !gameTeam.getGameId().isEmpty()) {
                Intent intent = new Intent(getContext(), ShareFieldActivity.class);
                intent.putExtra("game_id", gameTeam.getGameId());
                intent.putExtra("is_group_formation", false);
                startActivity(intent);
            }
        }

        else if (v == binding.btnSpeak) {
            VibrationEffect effect = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                vibrator.vibrate(effect);
            }
            buttonSpeakClicked();

        }
        else if (v == binding.relNotif) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), NotificationsActivityLineup.class);
            startActivity(intent);
        }
        else if (v == binding.btnReset) {
            if (gameTeam == null || gameTeam.getGameId().isEmpty()) {
                return;
            }
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getString(R.string.cancel))
                    .setOtherButtonTitles(getString(R.string.reset_game),getString(R.string.complete_game))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle(getResources().getString(R.string.reset))
                                        .setMessage(getResources().getString(R.string.do_you_want_reset_game_and_start_again))
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                resetGameAPI();
                                                binding.startVu.setEnabled(true);
                                                binding.fastLineup.setEnabled(true);
                                                //handler.removeCallbacksAndMessages(null);
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create();
                                builder.show();
                            }
                            if (index == 1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Complete Game")
                                        .setMessage("Are you sure you want to complete this game?")
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (teamA.getPlayers().size() > 0 && teamB.getPlayers().size() > 0){
                                                    if (gameTeam.getIsGameOn().equalsIgnoreCase("1")){
                                                        showResultDialog();
                                                        dialog.dismiss();
                                                    }
                                                }else{
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle(getResources().getString(R.string.reset))
                                                            .setMessage("There are no players in the team, please reset game first!")
                                                            .setPositiveButton(getResources().getString(R.string.reset), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    resetGameAPI();
                                                                    binding.startVu.setEnabled(true);
                                                                    binding.fastLineup.setEnabled(true);
                                                                    //handler.removeCallbacksAndMessages(null);
                                                                }
                                                            })
                                                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            }).create();
                                                    builder.show();

                                                }

                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                                builder.show();

                            }
                        }
                    }).show();
        }
        else if (v == binding.clubHome){
            switchToClubs();
        }
        else if (v == binding.lineupHome){
            switchToLineup();
        }
        else if (v == binding.padelHome){
            switchToPadel();
        }
        else if (v == binding.deleteUserAcc){
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            }else{
                DeleteUserAccountClicked();
            }
        }
        else if (v == binding.passVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            passClicked();
        }
        else if (v == binding.tcVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), OleWebVuActivity.class);
            intent.putExtra("url","tc");
            startActivity(intent);
        }
        else if (v == binding.ppVu) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getContext(), OleWebVuActivity.class);
            intent.putExtra("url","pp");
            startActivity(intent);
        }
        else if (v == binding.globalLineup) {
            globalLineUpClicked();

        }
        else if (v == binding.realLineup) {
            realLineupClicked();
        }
        else if (v == binding.vsMode) {
            VibrationEffect effect = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                vibrator.vibrate(effect);
            }
            if (gameTeam != null) {
                if (teamACaptainAvailable || teamBCaptainAvailable) {
                    Functions.showToast(getContext(), "Captain already exist", FancyToast.ERROR);
                    return;
                }
                if (!teamA.getPlayers().isEmpty() || !teamB.getPlayers().isEmpty()) {
                    Functions.showToast(getContext(), " Remove player from teams then you can divide players", FancyToast.ERROR);
                    return;
                }
                captainloader = Functions.showLoader(getContext());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        letsBeginVsMode();
                    }
                }, 2000);
            }
        }
    }

    private void letsBeginVsMode() {
        Intent intent = new Intent(getContext(), MakeCaptainActivity.class);
        intent.putExtra("game", new Gson().toJson(gameTeam));
        intent.putExtra("team_id", teamA.getId());
        intent.putExtra("team_a", new Gson().toJson(teamA));
        intent.putExtra("team_b", new Gson().toJson(teamB));
        intent.putExtra("vs_mode", true);
        intent.putExtra("is_deep_link_user", !FriendId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)));
        Functions.hideLoader(captainloader);
        captainResultLauncher.launch(intent);
    }


    private void globalLineUpClicked() {
        Intent intent = new Intent(getContext(), LineupGlobalActivity.class);
        startActivity(intent);
    }

    private void realLineupClicked() {
        Intent intent = new Intent(getContext(), LineupRealActivity.class);
        startActivity(intent);
    }

    private void passClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        OleUpdatePassDialog oleUpdatePassDialog = new OleUpdatePassDialog(getContext());
        oleUpdatePassDialog.setCanceledOnTouchOutside(false);
        oleUpdatePassDialog.show();
    }

    private void DeleteUserAccountClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_account))
                .setMessage(getResources().getString(R.string.confirm_delete_account))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disableUserAccountApi();
                        logout();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void disableUserAccountApi() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.disableUserAccountApi(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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

    private void buttonSpeakClicked(){
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        Permissions.check(getContext(), permissions, null/*rationale*/,null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {

                if (gameTeam != null){
                    if (isRecording) {
                        startAudioRecording();
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    } else {

                        stopAudioRecording();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    }
                }
            }
        });
    }

    private void switchToPadel() {
        if (Functions.getAppLangAr(getContext()).equalsIgnoreCase("ar")){
            Functions.setAppLang(getContext(), "ar");
            Functions.changeLanguage(getContext(),"ar");
            sendAppLangApi();
        }
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kAppModule, Constants.kPadelModule);
        editor.apply();
        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void switchToClubs() {
        if (Functions.getAppLangAr(getContext()).equalsIgnoreCase("ar")){
            Functions.setAppLang(getContext(), "ar");
            Functions.changeLanguage(getContext(),"ar");
            sendAppLangApi();
        }
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kAppModule, Constants.kFootballModule);
        editor.apply();
        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void switchToLineup() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kAppModule, Constants.kLineupModule);
        editor.apply();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void tabSelected(View v) {
        if (v == binding.tabTeamA) {
            selectedTab = 0;
            binding.tabBgTeamA.setVisibility(View.VISIBLE);
            binding.tabBgTeamB.setVisibility(View.INVISIBLE);
            binding.teamACircle.setStrokeColor(getResources().getColor(R.color.yellowColor));
            binding.teamBCircle.setStrokeColor(Color.WHITE);
            binding.vuTeamA.setVisibility(View.VISIBLE);
            binding.vuTeamB.setVisibility(View.INVISIBLE);

            if (gameTeam !=null && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))
                    && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                binding.btnChair.setVisibility(View.GONE);
                binding.btnField.setVisibility(View.GONE);
                binding.chairlineview.setVisibility(View.GONE);
                binding.gklineview.setVisibility(View.GONE);
                binding.btmVu.setVisibility(View.VISIBLE);
                binding.btnGkShirts.setEnabled(true);
                binding.btnShirt.setEnabled(true);
                binding.countryVu.setEnabled(true);
            }else if (gameTeam !=null && gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || dotPosition ==null || dotPosition.equalsIgnoreCase("0") || dotPosition.isEmpty() && teamACaptainId.equalsIgnoreCase("")){
                binding.btnChair.setVisibility(View.VISIBLE);
                binding.btnField.setVisibility(View.VISIBLE);
                binding.chairlineview.setVisibility(View.VISIBLE);
                binding.gklineview.setVisibility(View.VISIBLE);
                binding.btmVu.setVisibility(View.VISIBLE);
                binding.btnGkShirts.setEnabled(true);
                binding.btnShirt.setEnabled(true);
            }else{
                binding.gkRecyclerVu.setVisibility(View.GONE);
                binding.shirtRecyclerVu.setVisibility(View.GONE);
                binding.btnGkShirts.setEnabled(false);
                binding.btnShirt.setEnabled(false);
            }
        }
        else {
            selectedTab = 1;
            binding.tabBgTeamA.setVisibility(View.INVISIBLE);
            binding.tabBgTeamB.setVisibility(View.VISIBLE);
            binding.teamACircle.setStrokeColor(Color.WHITE);
            binding.teamBCircle.setStrokeColor(getResources().getColor(R.color.yellowColor));
            binding.vuTeamA.setVisibility(View.INVISIBLE);
            binding.vuTeamB.setVisibility(View.VISIBLE);
            if (gameTeam !=null && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                binding.btnChair.setVisibility(View.GONE);
                binding.btnField.setVisibility(View.GONE);
                binding.chairlineview.setVisibility(View.GONE);
                binding.gklineview.setVisibility(View.GONE);
                binding.btmVu.setVisibility(View.VISIBLE);
                binding.btnGkShirts.setEnabled(true);
                binding.btnShirt.setEnabled(true);
                binding.countryVu.setEnabled(true);
            }else if (gameTeam !=null && gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || dotPosition == null || dotPosition.equalsIgnoreCase("0") || dotPosition.isEmpty() && teamBCaptainId.equalsIgnoreCase("")){
                binding.btnChair.setVisibility(View.VISIBLE);
                binding.btnField.setVisibility(View.VISIBLE);
                binding.chairlineview.setVisibility(View.VISIBLE);
                binding.gklineview.setVisibility(View.VISIBLE);
                binding.btmVu.setVisibility(View.VISIBLE);
                binding.btnGkShirts.setEnabled(true);
                binding.btnShirt.setEnabled(true);
            }else{
                binding.gkRecyclerVu.setVisibility(View.GONE);
                binding.shirtRecyclerVu.setVisibility(View.GONE);
                binding.btnGkShirts.setEnabled(false);
            }
        }
    }

    private void populateCountryData(Country country) {
        if (country == null && countryList.size() > 0) {
            country = countryList.get(0);
        }
        if (country != null) {
            binding.tvCountry.setText(country.getShortName());
            Glide.with(getContext()).load(country.getFlag()).into(binding.flagImgVu);
            //teamList.clear();
            shirtList.clear();
            //teamList.addAll(country.getTeams());
            if (selectedTeamId.equalsIgnoreCase("") && teamList.size() > 0) {
                if (!subSelectedTeamId.isEmpty()){
                    selectedTeamId = subSelectedTeamId;
                    shirtList.addAll(teamList.get(teamPodition).getShirts()); // teampdoition into 0 if any issue
                }else{
                    selectedTeamId = teamList.get(teamPodition).getId();
                    shirtList.addAll(teamList.get(teamPodition).getShirts());
                }

            }
            else {
                for (int i = 0; i < teamList.size(); i++) {
                    if (!subSelectedTeamId.isEmpty()){
                        if (teamList.get(i).getId().equalsIgnoreCase(subSelectedTeamId)) {
                            shirtList.clear();
                            shirtList.addAll(teamList.get(i).getShirts());
                            break;
                        }
                    }else{
                        if (teamList.get(i).getId().equalsIgnoreCase(selectedTeamId)) {
                            shirtList.clear();
                            shirtList.addAll(teamList.get(i).getShirts());
                            break;
                        }
                    }
                }

            }
            if (!subSelectedTeamId.isEmpty()) {
                teamAdapter.setSelectedId(subSelectedTeamId);
                shirtAdapter.setSelectedId(subSelectedShirtId);
            } else {
                teamAdapter.setSelectedId(selectedTeamId);
                shirtAdapter.setSelectedId(selectedShirtId);
            }
            shirtAdapter.notifyDataSetChanged();


        }
    }

    private void populateTeamData() {
        setTabName();

//        // Remove player who's in team
//        Iterator<PlayerInfo> iterator = friendList.iterator();
//        List<PlayerInfo> playersToRemove = new ArrayList<>();
//
//        while (iterator.hasNext()) {
//            PlayerInfo info = iterator.next();
//            if (info != null) {
//                int indexA = checkPlayerExistInTeamA(info.getId());
//                int indexB = checkPlayerExistInTeamB(info.getId());
//
//                if (indexA != -1 || indexB != -1) {
//                    playersToRemove.add(info);
//                }
//            }
//        }
//
//        friendList.removeAll(playersToRemove);

        //remove player who's in team
        Iterator<PlayerInfo> iterator = friendList.iterator();
        while (iterator.hasNext()) {
            PlayerInfo info = iterator.next();
            if (info != null) {
                int index = checkPlayerExistInTeamA(info.getId());
                if (index != -1) {
                    iterator.remove();
                    continue;

                }
                index = checkPlayerExistInTeamB(info.getId());
                if (index != -1) {
                    iterator.remove();
                }
            }
        }

        playerAdapter.notifyDataSetChanged();

        if (teamA.getPlayers().size() > 0) {
            binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
        }
        else {
            binding.tvTeamACount.setText("0");
        }
        if (teamB.getPlayers().size() > 0) {
            binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
        }
        else {
            binding.tvTeamBCount.setText("0");
        }

        checkCaptainAvailable();

        //Condition
        //        float viewWidth = vuTeamA.getWidth();
        //
        //        if (gameTeam.getTeamAPlayers().size() >= 8 || gameTeam.getTeamBPlayers().size() >= 8) {
        //            subVuH = 105.0f;
        //            subVuW = viewWidth / 8.0f;
        //        } else {
        //            subVuH = 115.0f;
        //            subVuW = viewWidth / 6.0f;
        //        }
        //        if (gameTeam.getTeamAPlayers().size() > 2 || gameTeam.getTeamBPlayers().size() > 2) {
        //            subVuH = subVuH - 105.0f;
        //            subVuW  =subVuW - 50.0f;
        //        }else{
        //            subVuH = 221.0f;
        //            subVuW = 318.0f;
        //        }

        for (PlayerInfo info : teamA.getPlayers()) {
            replaceViewTeamA(new DragData(info, -1), 0, 0, false);
        }

        for (PlayerInfo info : teamB.getPlayers()) {
            replaceViewTeamB(new DragData(info, -1), 0, 0, false);
        }

    }
    private int checkPlayerExistInTeamA(String id) {
        int result = -1;
        for (int i = 0; i < teamA.getPlayers().size(); i++) {
            if (teamA.getPlayers().get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }
    private int checkPlayerExistInTeamB(String id) {
        int result = -1;
        for (int i = 0; i < teamB.getPlayers().size(); i++) {
            if (teamB.getPlayers().get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }
    private void showHideBtnVu() {
        if (friendsCount >= 8) {
            binding.addPlayerVu.setVisibility(View.GONE);
        }
        else {
            binding.addPlayerVu.setVisibility(View.VISIBLE);
        }
        if (gameTeam != null) {
            if (gameTeam.getGameDate().equalsIgnoreCase("") && gameTeam.getIsGameOn().equalsIgnoreCase("0")) {
                // start lineup
                binding.startVu.setVisibility(View.VISIBLE);
                binding.fastLineup.setVisibility(View.VISIBLE);

            }
            else if (!gameTeam.getGameDate().equalsIgnoreCase("") && gameTeam.getIsGameOn().equalsIgnoreCase("0")) {
                // show result
                binding.startVu.setVisibility(View.GONE);
                binding.fastLineup.setVisibility(View.GONE);
                if (teamA.getPlayers().size() > 0 && teamB.getPlayers().size() > 0) {
                    showResultDialog();
                }
            }
            else {
                // game is on
                binding.startVu.setVisibility(View.GONE);
                binding.fastLineup.setVisibility(View.GONE);
                //binding.btnEndGame.setVisibility(View.VISIBLE);
            }
        }
        if (binding.addPlayerVu.getVisibility() == View.VISIBLE || binding.startVu.getVisibility() == View.VISIBLE ||  binding.fastLineup.getVisibility() == View.VISIBLE) {
            binding.btnVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnVu.setVisibility(View.GONE);
        }
    }
    private void replaceViewTeamA(DragData state, int x, int y, boolean isAdd) {
        PlayerInfo info = state.getItem();

        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
        populateDataInTeamAVu(fieldViewA, info, teamAVuWidth, teamAVuHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * teamAVuWidth; //getScreenWidth();
            float actualYValue = yValue *   teamAVuHeight ;   //getScreenHeight();
            setViewMargin(params, actualXValue, actualYValue);
            binding.vuTeamA.addView(fieldViewA, params);
        }
        else {
            if (x == 0 && y == 0) {
                setViewMargin(params, getRandomX(teamAVuWidth, subVuW), getRandomY(teamAVuHeight, subVuH));
            }
            else {
                setViewMargin(params, x, y);
            }
            binding.vuTeamA.addView(fieldViewA, params);
            float relX = (float) params.leftMargin / (float) teamAVuWidth; //getScreenWidth();
            float relY = (float) (params.topMargin) / (float) teamAVuHeight; // getScreenHeight();
            info.setxCoordinate(String.valueOf(relX));
            info.setyCoordinate(String.valueOf(relY));
            //setSelectedPlayerId(info.getId());
            //saveCoordinateAPI(true, gameTeam.getTeamAId(), info.getId(), relX, relY, "0", isAdd);
            saveCordinates(teamA.getId(), info.getId(), relX, relY, "0", info.getFriendShipId(), isAdd);
        }
    }
    private void replaceViewTeamB(DragData state, int x, int y, boolean isAdd) {
        PlayerInfo info = state.getItem();
        PreviewFieldView fieldView = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
        populateDataInTeamBVu(fieldView, info, teamBVuWidth, teamBVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * teamBVuWidth; //getScreenWidth();
            float actualYValue = yValue * teamBVuHeight; //getScreenHeight();
            setViewMargin(params, actualXValue, actualYValue);
            binding.vuTeamB.addView(fieldView, params);
        }
        else {
            if (x == 0 && y == 0) {
                setViewMargin(params, getRandomX(teamBVuWidth, subVuW), getRandomY(teamBVuHeight, subVuH));
            }
            else {
                setViewMargin(params, x, y);
            }
            binding.vuTeamB.addView(fieldView, params);
            float relX = (float) params.leftMargin / (float) teamBVuWidth ; //getScreenWidth();
            float relY = (float) (params.topMargin) / (float) teamBVuHeight; //getScreenHeight();
            info.setxCoordinate(String.valueOf(relX));
            info.setyCoordinate(String.valueOf(relY));
            //saveCoordinateAPI(true, gameTeam.getTeamBId(), info.getId(), relX, relY, "0", isAdd);
            saveCordinates(teamB.getId(), info.getId(), relX, relY, "0",info.getFriendShipId(), isAdd);
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
        if (gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
            if (teamACaptainAvailable) {
                if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    viewA.setParentViewSize(viewWidth, viewHeight);
                }
            }
            else if (!gameTeam.getCreatedBy().isEmpty() && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){

            } else{
                viewA.setParentViewSize(viewWidth, viewHeight);
            }
            viewA.setPreviewFieldACallback(previewFieldCallback);
        }
        viewA.setPlayerInfo(playerInfo, teamA.getTeamShirt(), teamA.getTeamGkShirt());
        if (viewA.isIstouchListerActive()){
            viewA.setPreviewFieldItemCallback(new PreviewFieldView.PreviewFieldItemCallback() {
                @Override
                public void itemClicked(PlayerInfo playerInfo) {
                    if (teamACaptainAvailable) {
                        if ((getGamePlayersCount() == 0 || captainATurn) && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                            showOptionDialog(playerInfo, true, 0, viewA);
                            //getProfileAPI(true, playerInfo.getId(),0,true, viewA);

                        } else if (playerInfo.getIsCaptain() !=null && playerInfo.getIsCaptain().equalsIgnoreCase("1")) {
                            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                                    .setCancelButtonTitle(getString(R.string.cancel))
                                    .setOtherButtonTitles(getString(R.string.remove_captain))
                                    .setCancelableOnTouchOutside(true)
                                    .setListener(new ActionSheet.ActionSheetListener() {
                                        @Override
                                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                                        }

                                        @Override
                                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                            if (index == 0) {
                                                removeCaptainAPI(true, teamA.getId(), playerInfo.getId(),gameTeam.getGameId());
                                            }
                                        }
                                    }).show();
                        }
//                        else {
//                            showOptionDialog(playerInfo, true, 0, viewA);
//                            //getProfileAPI(true,playerInfo.getId(),0,true,viewA);
//                        }
                    }
                    else {
                        showOptionDialog(playerInfo, true, 0, viewA);
                        //getProfileAPI(true,playerInfo.getId(),0,true,viewA);
                    }

                }
            });
        }
        else{
            viewA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //binding.vuTeamA.setEnabled(true);
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("player_id", playerInfo.getId());
                    intent.putExtra("friendship_id", playerInfo.getFriendShipId());
                    intent.putExtra("is_team", false);
                    startActivity(intent);
                }
            });
        }
    }
    private void populateDataInTeamBVu(PreviewFieldView viewA, PlayerInfo playerInfo, int viewWidth, int viewHeight) {
        if (gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
            if (teamBCaptainAvailable) {
                if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    viewA.setParentViewSize(viewWidth, viewHeight);
                }
            }
            else if (!gameTeam.getCreatedBy().isEmpty() && !gameTeam.getCreatedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){

            } else{
                viewA.setParentViewSize(viewWidth, viewHeight);

            }
            viewA.setPreviewFieldACallback(previewFieldCallback);
        }

        viewA.setPlayerInfo(playerInfo, teamB.getTeamShirt(), teamB.getTeamGkShirt());
        if (viewA.isIstouchListerActive()){
            viewA.setPreviewFieldItemCallback(new PreviewFieldView.PreviewFieldItemCallback() {
                @Override
                public void itemClicked(PlayerInfo playerInfo) {
                    if (teamBCaptainAvailable) {
                        if ((getGamePlayersCount() == 0 || captainBTurn) && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                            showOptionDialog(playerInfo, true, 0, viewA);
                            //getProfileAPI(true, playerInfo.getId(),0,true,viewA);
                        }
                        else if (playerInfo.getIsCaptain() != null && playerInfo.getIsCaptain().equalsIgnoreCase("1")) {
                            ActionSheet.createBuilder(getContext(), getSupportFragmentManager() )
                                    .setCancelButtonTitle(getString(R.string.cancel))
                                    .setOtherButtonTitles(getString(R.string.remove_captain))
                                    .setCancelableOnTouchOutside(true)
                                    .setListener(new ActionSheet.ActionSheetListener() {
                                        @Override
                                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                                        }

                                        @Override
                                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                            if (index == 0) {
                                                removeCaptainAPI(true, teamB.getId(), playerInfo.getId(),gameTeam.getGameId());
                                            }
                                        }
                                    }).show();
                        }
//                        else {
//                            showOptionDialog(playerInfo, true, 0, viewA);
//                            //getProfileAPI(true,playerInfo.getId(),0,true,viewA);
//                        }
                    }
                    else {
                        showOptionDialog(playerInfo, true, 0, viewA);
                        //getProfileAPI(true, playerInfo.getId(), 0,true,viewA);
                    }
                }
            });
        }
        else{
            viewA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("player_id", playerInfo.getId());
                    intent.putExtra("friendship_id", playerInfo.getFriendShipId());
                    intent.putExtra("is_team", false);
                    startActivity(intent);
                }
            });
        }

    }
    PreviewFieldView.PreviewFieldViewCallback previewFieldCallback = new PreviewFieldView.PreviewFieldViewCallback() {
        @Override
        public void didStartDrag(PreviewFieldView view, PlayerInfo playerInfo, float newX, float newY) {
            float relX = newX / (float)  teamAVuWidth;  //getScreenWidth();
            float relY = newY / (float)  teamAVuHeight; //getScreenHeight();
            for (int i = 0; i<teamA.getPlayers().size(); i++){
                if (playerInfo.getId().equals(teamA.getPlayers().get(i).getId())){
                    cordEmitter(teamA.getId() , playerInfo.getId(),  relX, relY, playerInfo.getIsGoalkeeper());  // is any issue make this lsat parameter to "" empty string
                }
            }
            for (int i = 0; i<teamB.getPlayers().size(); i++){
                if (playerInfo.getId().equals(teamB.getPlayers().get(i).getId())){
                    cordEmitter(teamB.getId(), playerInfo.getId(),  relX, relY, playerInfo.getIsGoalkeeper());  // is any issue make this lsat parameter to "" empty string
                }
            }

        }

        @Override
        public void didEndDrag(PreviewFieldView view, PlayerInfo playerInfo, float newX, float newY) {
            float relX = newX / (float)  teamAVuWidth;  //getScreenWidth();
            float relY = newY / (float)  teamAVuHeight; //getScreenHeight();
            boolean isGK = false;
            if (selectedTab == 0) {
                if (newY + view.getHeight() > teamAVuHeight - 50) {
                    int w = teamAVuWidth / 4;
                    //goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamAVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    //check gk exist already && replace position
                    PreviewFieldView existingGk = checkTeamAGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamA.removeView(existingGk);
                        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
                        populateDataInTeamAVu(fieldViewA, existingGk.getPlayerInfo(), teamAVuWidth, teamAVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue * teamAVuWidth; // getScreenWidth();
                        float actualYValue = yValue * teamAVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamA.addView(fieldViewA, params);
                        //saveCoordinateAPI(false, gameTeam.getTeamAId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", false);
                        saveCordinates(teamA.getId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", existingGk.getPlayerInfo().getFriendShipId(), false);
                    }
                    //saveCoordinateAPI(false, gameTeam.getTeamAId(), playerInfo.getId(), relX, relY, "1", false);
                    saveCordinates(teamA.getId(), playerInfo.getId(), relX, relY, "1", playerInfo.getFriendShipId(), false);
                    view.setImage(teamA.getTeamGkShirt());
                    view.getPlayerInfo().setIsGoalkeeper("1");
                }
                else {
                    //saveCoordinateAPI(false, gameTeam.getTeamAId(), playerInfo.getId(), relX, relY, "0", false);
                    saveCordinates(teamA.getId(), playerInfo.getId(), relX, relY, "0",playerInfo.getFriendShipId(), false);
                    view.getPlayerInfo().setIsGoalkeeper("0");
                    view.setImage(teamA.getTeamShirt());

                }
                view.getPlayerInfo().setxCoordinate(String.valueOf(relX));
                view.getPlayerInfo().setyCoordinate(String.valueOf(relY));

            }
            else {
                if (newY + view.getHeight() > teamBVuHeight - 50) {
                    int w = teamBVuWidth / 4;
                    //goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamBVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    //check gk exist already && replace position
                    PreviewFieldView existingGk = checkTeamBGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamB.removeView(existingGk);
                        PreviewFieldView fieldView = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
                        populateDataInTeamBVu(fieldView, existingGk.getPlayerInfo(), teamBVuWidth, teamBVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue * teamBVuWidth; // getScreenWidth();
                        float actualYValue = yValue * teamBVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamB.addView(fieldView, params);
                        //saveCoordinateAPI(false, gameTeam.getTeamBId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", false);
                        saveCordinates(teamB.getId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", existingGk.getPlayerInfo().getFriendShipId(), false);

                    }
                    //saveCoordinateAPI(false, gameTeam.getTeamBId(), playerInfo.getId(), relX, relY, "1", false);
                    saveCordinates(teamB.getId(),  playerInfo.getId(), relX, relY, "1",playerInfo.getFriendShipId(), false);
                    view.setImage(teamB.getTeamGkShirt());
                    playerInfo.setIsGoalkeeper("1");
                }
                else {
                    //saveCoordinateAPI(false, gameTeam.getTeamBId(), playerInfo.getId(), relX, relY, "0", false);
                    saveCordinates(teamB.getId(),  playerInfo.getId(), relX, relY, "0",playerInfo.getFriendShipId(), false);
                    view.setImage(teamB.getTeamShirt());
                    playerInfo.setIsGoalkeeper("0");
                }
                playerInfo.setxCoordinate(String.valueOf(relX));
                playerInfo.setyCoordinate(String.valueOf(relY));
            }
        }
    };
    private PreviewFieldView checkTeamAGkExist() {
        PreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    break;
                }
            }
        }
        return view;
    }
    private PreviewFieldView checkTeamBGkExist() {
        PreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    break;
                }
            }
        }
        return view;
    }
    private void checkCaptainAvailable() {

        teamACaptainAvailable = false;
        teamACaptainId = "";
        teamACaptainName = "";

        teamBCaptainAvailable = false;
        teamBCaptainId = "";
        teamBCaptainName = "";

        for (PlayerInfo info : teamA.getPlayers()) {
            if (info.getIsCaptain() !=null && info.getIsCaptain().equalsIgnoreCase("1")) { //cehckx info.getIsCaptain() !=null &&
                teamACaptainAvailable = true;
                teamACaptainId = info.getId();
                teamACaptainName = info.getNickName();
                break;
            }
        }
        for (PlayerInfo info : teamB.getPlayers()) {
            if (info.getIsCaptain() !=null && info.getIsCaptain().equalsIgnoreCase("1")) {
                teamBCaptainAvailable = true;
                teamBCaptainId = info.getId();
                teamBCaptainName = info.getNickName();
                break;
            }
        }

        if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (gameTeam !=null && gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
                binding.btnSpeak.setOnClickListener(this);
            }
        }
        else {
            binding.btnSpeak.setOnClickListener(null);
            binding.btnSpeak.setImageResource(R.drawable.mic_deactivel);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            try {

                if (webRTCClient !=null) {
                    webRTCClient.toggleAudio(false);
                }
//                ExpressManager.getInstance().enableMic(false);
//                ExpressManager.getInstance().leaveRoom();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            binding.tvTurn.setVisibility(View.GONE);
        }

        setTabName();

    }
    private void showPlayerChooseAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("")
                .setMessage(getResources().getString(R.string.which_team_captain_choose_player))
                .setPositiveButton(teamACaptainName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        // Example: Emit a new event
                        JSONObject joinData = new JSONObject();
                        try {
                            joinData.put("game_id", GameID);
                            joinData.put("team_id", teamA.getId());
                            socket.emit("game:selection-first-turn", joinData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // saveData("", "captain_a_choose", "", "", "", "", "", "", "", "");

                    }
                })
                .setNegativeButton(teamBCaptainName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Example: Emit a new event
                        JSONObject joinData = new JSONObject();
                        try {
                            joinData.put("game_id", GameID);
                            joinData.put("team_id", teamB.getId());
                            socket.emit("game:selection-first-turn", joinData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        playerChooseAlert = builder.create();
        playerChooseAlert.setCancelable(false);
        playerChooseAlert.show();
    }
    private void signOutClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.sign_out))
                .setMessage(getResources().getString(R.string.do_you_want_signout))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        builder.show();
    }
    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.remove(Constants.kIsSignIn);
        editor.remove(Constants.kUserInfo);
        editor.remove(Constants.kCurrency);
        editor.remove(Constants.kUserType);
        editor.remove(Constants.kAppModule);
        editor.apply();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(getContext()).clearDiskCache();
            }
        });

        logoutApi();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void logoutApi() {
        KProgressHUD hud = Functions.showLoader(getContext());
        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.logout(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), uniqueID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            SocketManager.resetInstance();
                            socket.disconnect();
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.remove(Constants.kUserID);
                            editor.remove(Constants.kaccessToken);
                            editor.apply();
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
    private void ipdetails(Boolean isLoader, String ip) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody>  call = AppManager.getInstance().apiInterfaceNode.getIpDetails(ip,Functions.getAppLang(getContext()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            userIpDetails = data.getString("login_type");
                            userModule = data.getString("allow_module");
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kLoginType, userIpDetails);
                            editor.putString(Constants.kUserModule, userModule);
                            editor.apply();
                            populateData();
                            if (socket != null){
                                disconnectSocketListeners();
                            }
                            socketConnection();
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
    public void disconnectSocketListeners(){

        socket.off("error");
        socket.off("my:friends-groups");
        socket.off("my:selections");
        socket.off("game:details");
        socket.off("game:teams");
        socket.off("game:players");
        socket.off("game:join-via-link");
        socket.off("player:update-friends-group");
        socket.off("game:player-dragging");
        socket.off("team:player-removed");
        socket.off("game:player-replaced");
        socket.off("game:player-swapped");
        socket.off("game:captains-created");
        socket.off("game:captain-turn");
        socket.off("game:captains-removed");
        socket.off("game:player-dragged");
        socket.off("team:shirt-changed");
        socket.off("game:zego-details");
        socket.off("game:reset");
        socket.off("game:audio");
        socket.off("call:offer");
        socket.off("call:answer");
        socket.off("call:candidate");



    }
    private void getAllCountries(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getAllCountries();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray country = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            countryList.clear();
                            for (int i = 0; i < country.length(); i++) {
                                countryList.add(gson.fromJson(country.get(i).toString(), Country.class));
                            }
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
    private void getTeamAndShirtDetails(boolean isLoader, String countryId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getTeamAndShirtDetails(countryId,"android");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray dataArray = object.getJSONArray(Constants.kData);
                            teamList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject teamObject = dataArray.getJSONObject(i);
                                Team team = gson.fromJson(teamObject.toString(), Team.class);
                                teamList.add(team);
                            }

                            Country countryData = null;
                            for (int i = 0; i < countryList.size(); i++) {
                                if (countryList.get(i).getId().equalsIgnoreCase(selectedCountryId)) {
                                    countryData = countryList.get(i);
                                    break;
                                }
                            }
                            populateCountryData(countryData);
                            teamAdapter.notifyDataSetChanged();
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
    private void getGoalKeeperShirts(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getGoalKeeperShirts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray gk_shirts = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            gkShirtList.clear();
                            for (int i = 0; i < gk_shirts.length(); i++) {
                                gkShirtList.add(gson.fromJson(gk_shirts.get(i).toString(), Shirt.class));
                            }
                            gkAdapter.notifyDataSetChanged();

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
    private void getAllFields(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getAllFields();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray fields = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            fieldList.clear();
                            for (int i = 0; i < fields.length(); i++) {
                                fieldList.add(gson.fromJson(fields.get(i).toString(), FieldImage.class));
                            }

                            fieldAdapter.notifyDataSetChanged();
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
    private void getAllChairs(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getAllChairs();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray chairs = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            chairList.clear();
                            for (int i = 0; i < chairs.length(); i++) {
                                chairList.add(gson.fromJson(chairs.get(i).toString(), Chair.class));
                            }
                            if (selectedChairId.equalsIgnoreCase("")) {
                                if (chairList.size() > 0) {
                                    selectedChairId = chairList.get(0).getId();
                                    playerAdapter.setChairUrl(chairList.get(0).getPhotoUrl());
                                }
                            }
                            else {
                                for (int i = 0; i < chairList.size(); i++) {
                                    if (chairList.get(i).getId().equalsIgnoreCase(selectedChairId)) {
                                        playerAdapter.setChairUrl(chairList.get(i).getPhotoUrl());
                                        break;
                                    }
                                }
                            }
                            chairAdapter.setSelectedId(selectedChairId);
                            chairAdapter.notifyDataSetChanged();

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
    private void saveAppSettings(boolean isLoader, String targetId, String type, String chairUrl, String fieldBgImg, String fieldImg) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.lineupSettings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), targetId, type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (type.equalsIgnoreCase("chair")) {
                                playerAdapter.setChairUrl(chairUrl);
                            }
                            else {
                                if (!fieldBgImg.isEmpty() && !fieldImg.isEmpty()){
                                    Glide.with(getContext()).load(fieldBgImg).into(binding.fieldBgImgVu);
                                    Glide.with(getContext()).load(fieldImg).into(binding.fieldImgVu);
                                }
                            }
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
    private void fastLineup() {
        socket.emit("game:fast-lineup");
        if (!Functions.getPrefValue(getContext(), Constants.firstLineup).isEmpty()){
            showcaseVsHasBeenShown = Functions.getPrefValue(getContext(), Constants.firstLineup);
        }
        if (showcaseVsHasBeenShown.equalsIgnoreCase("0")) {
            new GuideView.Builder(this)
                    .setTitle( "* Team Splitting *\n* تقسيم الفريقين *")
                    .setContentText("* ميزة تقسيم الفريقين مع لاعب اخر *" +
                            "\n" + " اضغط على هذا الزر لاختيار لاعب ليقوم بتقسيم الفريقين معك ، يمكنك أيضًا مشاركة رابط مع الأصدقاء.  يمكن  لأي شخص لديه الرابط الانضمام إليك عبر التطبيق لتقسيم الفريقين معا ، مما يجعل التحدي اكثر اثارة و متعه ." + "\n" +
                            "\n" + "* Team Splitting Feature with Another Player *" + "\n" +
                            "Click this button to choose a player to split the teams with you. You can also share a link with friends. Anyone with the link can join you through the app to split the teams together, making the challenge more exciting and enjoyable.")
                    .setGravity(Gravity.center)
                    .setTargetView(binding.vsMode)
                    .build()
                    .show();
            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
            editor.putString(Constants.firstLineup, "1");
            editor.apply();
        }

    }
    private void updateTeamShirtAPI(String teamId, String shirtId, String type) { // teamaid, teambid, teamashirt, teambshirt, teamagkshirt, teambgkashirt

        JSONObject data = new JSONObject();
        try {
            data.put("team_id", teamId);
            data.put("shirt_id", shirtId);
            data.put("type", type);
            socket.emit("team:change-shirt", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void resetGameAPI() {
        try {
            JSONObject data = new JSONObject();
            data.put("game_id", gameTeam.getGameId());
            socket.emit("game:reset",data);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void removeCaptainAPI(boolean isLoader, String teamId, String captainId, String gameID) {

        JSONObject data = new JSONObject();
        try {
            data.put("game_id", gameID);
            socket.emit("game:remove-captain", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void substitutePlayerAPI(boolean isLoader, String oldId, String oldFriendshipId, String newId, String newFriendshipId, String teamId) {

        JSONObject data = new JSONObject();
        try {
            //Create the JSON structure for your data
            JSONObject oldPlayer = new JSONObject();
            oldPlayer.put("friend_id", oldId);
            oldPlayer.put("friendship_id", oldFriendshipId);
            oldPlayer.put("team_id", teamId);

            JSONObject newPlayer = new JSONObject();
            newPlayer.put("friend_id", newId);
            newPlayer.put("friendship_id", newFriendshipId);

            data.put("game_id", GameID);
            data.put("old_player", oldPlayer);
            data.put("new_player", newPlayer);

            //Emit the JSON object over the socket
            socket.emit("game:replace-player", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void removePlayerFromTeamAPI(boolean isLoader, String pId, String friendShipId, String teamId, PreviewFieldView view) {

        JSONObject data = new JSONObject();
        try {
            data.put("friend_id", pId);
            data.put("friendship_id", friendShipId);
            data.put("game_id", GameID);
            data.put("team_id", teamId);
            socket.emit("team:remove-player", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void checkWinMatchAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showWinMatchPopup(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String msg = obj.getString("message");
                            String popId = obj.getString("id");
                            GameHistory matchResult = new Gson().fromJson(obj.getJSONObject("match_result").toString(), GameHistory.class);
                            showWinGameDialog(matchResult, popId, msg);
                        }
                        else {
                            checkCaptainAPI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void checkCaptainAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showCaptainPopup(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            dismissCaptainWaitingPopup();
                            Functions.hideLoader(captainloader);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String msg = obj.getString("message");
                            String popId = obj.getString("id");
                            String gameId = obj.getString("game_id");
                            String type = "";
                            if (!obj.isNull("type")) {
                                type = obj.getString("type");
                            }
                            if (listItems.get(0).getGameId().isEmpty() || !listItems.get(0).getGameId().equalsIgnoreCase(gameId)){
                                showCaptainDialog(popId, msg, gameId, type); // important check
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void setPageViewIndicator() {
//        runPageAdapter = false;
        pager_indicator.removeAllViews(); // 6
        dotsCount = 0;
        if (itemsPager_adapter != null){
            dotsCount = itemsPager_adapter.getCount();
            dots = new ImageView[dotsCount];
            String ingame = "0";
            String gameId = "";
            for (int i = 0; i < listItems.size(); i++) {
                SliderModelClass model = listItems.get(i);
                ingame = model.getInGame(); //0,1
                gameId = model.getGameId(); //0,1
                dots[i] = new ImageView(this);
                if (i == 0) {
                    if (gameId.isEmpty() && ingame.equalsIgnoreCase("0")) {
                        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_yellowdotl));
                    }else if (!gameId.isEmpty() || ingame.equalsIgnoreCase("1")){
                        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_greendotl));

                    }
                }
                else {
                    if (gameId.isEmpty() && ingame.equalsIgnoreCase("0")) {
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_yellowdotl));

                    } else if (!gameId.isEmpty() || ingame.equalsIgnoreCase("1")) {
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_greendotl));
                    }
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(4, 0, 4, 0);
                pager_indicator.addView(dots[i], params);
            }
            page.removeOnPageChangeListener(onPageChangeListener);
            page.addOnPageChangeListener(onPageChangeListener);
            String kCurrentpage = Functions.getCurrentPage(getContext(), Constants.kCurrentPage);
            Log.d("kCurrentPage", kCurrentpage);

            if (!kCurrentpage.equalsIgnoreCase("") && listItems.size() > 1){
                binding.addPlayerVu.setVisibility(View.GONE);

                if (kCurrentpage.equalsIgnoreCase(String.valueOf(listItems.size()))){
                    onPageChangeListener.onPageSelected(Integer.parseInt(kCurrentpage)-1); //checkx
                    page.setCurrentItem(Integer.parseInt(kCurrentpage)-1);
                }else{
                    onPageChangeListener.onPageSelected(Integer.parseInt(kCurrentpage)); //checkx
                    page.setCurrentItem(Integer.parseInt(kCurrentpage));
                }

            }
            else{
                onPageChangeListener.onPageSelected(0); //checkx
                page.setCurrentItem(0);
            }
        }
    }
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (index == listItems.size()){
                index = index - 1;
            }
            if (position != index){
                if (!listItems.get(index).getGameId().isEmpty()){ // index = 1 issue
                    JSONObject data = new JSONObject();
                    try {
                        data.put("game_id", listItems.get(index).getGameId());
                        socket.emit("game:leave", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                index = position; //1
            }

        }
        @Override
        public void onPageSelected(int position) {

            dotPosition = String.valueOf(position);
            Functions.setCurrentPage(getContext(), dotPosition);
            for (int i = 0; i < listItems.size(); i++) {
                SliderModelClass model = listItems.get(i);
                if (model.getGameId().equalsIgnoreCase("") && model.getInGame().equalsIgnoreCase("0")) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_yellowdotl));
                    if (dots[i] == dots[position]) {
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_yellowdotl));
                    }

                } else if (!model.getGameId().isEmpty() || model.getInGame().equalsIgnoreCase("1")) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_greendotl));
                    if (dots[i] == dots[position]) {
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_greendotl));
                    }
                }
            }
            GameID = listItems.get(position).getGameId();
            FriendId = listItems.get(position).getFriendId();
            if (!dotPosition.equalsIgnoreCase("0")){ //1
                if (!GameID.isEmpty()){ //Yes
                    enableTouch(false);
                    binding.vuTeamA.removeAllViews();
                    binding.vuTeamB.removeAllViews();
                    binding.tvTeamA.setText("Team A");
                    binding.tvTeamB.setText("Team B");
                    binding.tvTeamACount.setText("0");
                    binding.tvTeamBCount.setText("0");
                    binding.startVu.setVisibility(View.GONE);
                    binding.fastLineup.setVisibility(View.GONE);
                    binding.startVu.setEnabled(false);
                    binding.fastLineup.setEnabled(false);


                    JSONObject dataa = new JSONObject();
                    try {
                        dataa.put("player_id", FriendId);
                        dataa.put("game_id", GameID);
                        socket.emit("player:friends", dataa);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject data = new JSONObject();
                    try {
                        data.put("game_id", GameID);
                        socket.emit("game:join", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                else{  //1 index : No game
                    binding.startVu.setVisibility(View.GONE); //1
                    binding.fastLineup.setVisibility(View.VISIBLE);
                    binding.startVu.setEnabled(true);
                    binding.fastLineup.setEnabled(true);
                    gameTeam = null;
                    binding.vuTeamA.removeAllViews();
                    binding.vuTeamB.removeAllViews();
                    binding.tvTeamA.setText("Team A");
                    binding.tvTeamB.setText("Team B");
                    binding.tvTeamACount.setText("0");
                    binding.tvTeamBCount.setText("0");
                    binding.tvTurn.setVisibility(View.GONE);
                    teamACaptainAvailable = false;
                    teamBCaptainAvailable = false;

                    JSONObject data = new JSONObject();
                    try {
                        data.put("player_id", FriendId);
                        socket.emit("player:friends", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject dataa = new JSONObject();
                    try {
                        dataa.put("player_id", FriendId);
                        socket.emit("player:teams", dataa);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            else { //0
                if (!GameID.isEmpty()) { // Yes
                    enableTouch(true);
                    binding.startVu.setVisibility(View.GONE);
                    binding.fastLineup.setVisibility(View.GONE);

                    JSONObject dataa = new JSONObject();
                    try {
                        dataa.put("player_id", FriendId);
                        dataa.put("game_id", GameID);
                        socket.emit("player:friends", dataa);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject data = new JSONObject();
                    try {
                        data.put("game_id", GameID);
                        socket.emit("game:join", data);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                else { //0 index : No game
                    binding.vuTeamA.removeAllViews();
                    binding.vuTeamB.removeAllViews();
                    binding.tvTeamA.setText("Team A");
                    binding.tvTeamB.setText("Team B");
                    binding.tvTeamACount.setText("0");
                    binding.tvTeamBCount.setText("0");
                    binding.tvTurn.setVisibility(View.GONE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("player_id", FriendId);
                        socket.emit("player:friends", data);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject dataa = new JSONObject();
                    try {
                        dataa.put("player_id", FriendId);
                        socket.emit("player:teams", dataa);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    enableTouch(true);
                    binding.startVu.setVisibility(View.GONE);
                    binding.fastLineup.setVisibility(View.VISIBLE);
                    binding.startVu.setEnabled(true);
                    binding.fastLineup.setEnabled(true);

                }

            }

        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };
    private void enableTouch(Boolean status){
        if (status){
            gameTeam = null;
            binding.tvTeamA.setText("Team A");
            binding.tvTeamB.setText("Team B");
            binding.vuTeamA.removeAllViews();
            binding.vuTeamB.removeAllViews();
            binding.vuTeamA.setEnabled(true);
            binding.vuTeamB.setEnabled(true);
            binding.tabTeamA.setEnabled(true);
            binding.tabTeamB.setEnabled(true);
            binding.btnMenu.setEnabled(true);
            binding.btnChair.setEnabled(true);
            binding.btnShirt.setEnabled(true);
            binding.btnField.setEnabled(true);
            binding.btnGkShirts.setEnabled(true);
            binding.countryVu.setEnabled(true);
            binding.logoutVu.setEnabled(true);
            binding.shopVu.setEnabled(true);
            binding.comparisonVu.setEnabled(true);
            binding.addPlayerVu.setEnabled(true);
            binding.editGameVu.setEnabled(true);
            binding.btnShare.setEnabled(true);
            binding.btnReset.setEnabled(true);
            binding.relNotif.setEnabled(true);
            binding.groupVu.setEnabled(true);
            binding.suggestVu.setEnabled(true);
            binding.useVu.setEnabled(true);
            binding.startVu.setEnabled(true);
            binding.fastLineup.setEnabled(true);
            binding.btnChair.setVisibility(View.VISIBLE);
            binding.btnField.setVisibility(View.VISIBLE);
            binding.chairlineview.setVisibility(View.VISIBLE);
            binding.gklineview.setVisibility(View.VISIBLE);
        }else{
            binding.btnChair.setEnabled(false);
            binding.btnShirt.setEnabled(false);
            binding.btnField.setEnabled(false);
            binding.btnGkShirts.setEnabled(false);
            binding.countryVu.setEnabled(false);
            binding.logoutVu.setEnabled(false);
            binding.shopVu.setEnabled(false);
            binding.comparisonVu.setEnabled(false);
            binding.startVu.setEnabled(false);
            binding.fastLineup.setEnabled(false);
            binding.addPlayerVu.setEnabled(false);
            binding.editGameVu.setEnabled(false);
            binding.btnReset.setEnabled(false);
            binding.relNotif.setEnabled(false);
            binding.groupVu.setEnabled(false);
            binding.suggestVu.setEnabled(false);
            binding.useVu.setEnabled(false);
            binding.teamRecyclerVu.setVisibility(View.GONE);
            binding.shirtRecyclerVu.setVisibility(View.GONE);
            binding.fieldRecyclerVu.setVisibility(View.GONE);
            binding.chairRecyclerVu.setVisibility(View.GONE);
            binding.gkRecyclerVu.setVisibility(View.GONE);
            binding.startVu.setVisibility(View.GONE);
            binding.fastLineup.setVisibility(View.GONE);


        }

    }
    @Override
    public void onItemClick(String friendId, String groupName) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getString(R.string.cancel))
                .setOtherButtonTitles("Delete "+groupName)
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (dotPosition !=null && (dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase(""))){
                            Functions.showToast(getContext(), "You Cannot Delete Your Own Group!", FancyToast.ERROR);
                        }else{
                            JSONObject data = new JSONObject();
                            try {
                                data.put("friend_id", friendId);
                                socket.emit("player:delete-friend-group", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Functions.showToast(getContext(), "Group Deleted Successfully!", FancyToast.SUCCESS);
                            page.setCurrentItem(0);
                            Functions.setCurrentPage(getContext(), String.valueOf(0));
//                            runPageAdapter = true;
                        }
                    }
                }).show();

    }
    private int getGamePlayersCount() {
        int count = 0;
        for (int i = 0; i < friendList.size(); i++) {
            PlayerInfo info = friendList.get(i);
            if (info != null && info.getInGame().equalsIgnoreCase("1")) {
                count += 1;
            }
        }
        return count;
    }

    //    private Emitter.Listener error = new Emitter.Listener() {
//
//        @Override
//        public void call(Object... args) {
//
//        }
//    };
    public void socketConnection()  {

        socket.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Functions.hideLoader(fastLineupLoader);
                            //String msg = data.getString("message");
                            String error = data.getString("error");
                            // Functions.showToast(getContext(), msg +" "+error,FancyToast.ERROR);
                            //Log.d("Socket Error", msg + " ---------- " + error);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        socket.on("my:friends-groups", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONArray dataArray = (JSONArray) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listItems.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < dataArray.length(); i++) {
                                SliderModelClass info =  gson.fromJson(dataArray.get(i).toString(), SliderModelClass.class);
                                listItems.add(info);
                            }

                            pager_indicator.removeAllViews();
                            itemsPager_adapter = new SliderAdapter(getContext(), listItems,page);
                            itemsPager_adapter.setOnItemClickListener(MainActivity.this);
                            page.setAdapter(itemsPager_adapter);

                            if (!deepUrlGameId.isEmpty()){
                                if (!listItems.isEmpty()){
                                    for (int i=0; i < listItems.size(); i++){
                                        if (listItems.get(i).getGameId().equalsIgnoreCase(deepUrlGameId)){
                                            page.setCurrentItem(i);
                                            Functions.setCurrentPage(getContext(), String.valueOf(i));
                                            setPageViewIndicator();
                                            itemsPager_adapter.notifyDataSetChanged();
                                            deepUrlGameId = "";
                                            break;
                                        }
                                    }
                                }
                            }
                            else{
                                setPageViewIndicator();
                                itemsPager_adapter.notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        socket.on("my:selections", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Gson gson = new Gson();
                            selectionModel = gson.fromJson(data.toString(), SelectionModel.class);
                            selectedCountryId = selectionModel.getCountry();
                            selectedTeamId = selectionModel.getTeam();
                            selectedShirtId = selectionModel.getShirt();
                            selectedChairId = selectionModel.getChair();
                            selectedGkShirtId = selectionModel.getGoalkeeper();
                            selectedChairId = selectionModel.getChair();
                            Glide.with(getApplicationContext()).load(selectionModel.getFieldImageUrl()).into(binding.fieldImgVu);
                            Glide.with(getApplicationContext()).load(selectionModel.getBgImageUrl()).into(binding.fieldBgImgVu);
                        } catch (JsonSyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });


            }
        });
        socket.on("game:details", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            gameTeam = null;
                            binding.vuTeamA.removeAllViews();
                            binding.vuTeamB.removeAllViews();
                            gameTeam = new Gson().fromJson(data.toString(), GameTeam.class);
                            Glide.with(getApplicationContext()).load(gameTeam.getBgImage()).into(binding.fieldBgImgVu);
                            Glide.with(getApplicationContext()).load(gameTeam.getFieldImage()).into(binding.fieldImgVu);
                            playerAdapter.setIsGameOn(gameTeam.getIsGameOn(), true);
                            Functions.hideLoader(fastLineupLoader);

                        } catch (JsonSyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        socket.on("game:teams", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            teamA = null;
                            teamB = null;
                            binding.vuTeamA.removeAllViews();
                            binding.vuTeamB.removeAllViews();
                            Gson gson = new Gson();
                            teamA =   gson.fromJson(data.getJSONObject("team_a").toString(), FormationTeams.class);
                            teamB =   gson.fromJson(data.getJSONObject("team_b").toString(), FormationTeams.class);
                            if (gameTeam!=null){
                                populateTeamData();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        socket.on("game:players", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONArray dataArray = (JSONArray) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            friendList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < dataArray.length(); i++) {
                                PlayerInfo info = gson.fromJson(dataArray.get(i).toString(), PlayerInfo.class);
                                friendList.add(info);
                            }
                            friendsCount = friendList.size();
                            if (FriendId.equals(Functions.getPrefValue(getContext(), Constants.kUserID))) { //checkx
                                friendList.add(null);
                            }
                            showHideBtnVu();
                            if (teamA !=null || teamB !=null){
                                //remove player who's in team
                                Iterator<PlayerInfo> iterator = friendList.iterator();
                                while (iterator.hasNext()) {
                                    PlayerInfo info = iterator.next();
                                    if (info != null) {
                                        int index = checkPlayerExistInTeamA(info.getId());
                                        if (index != -1) {
                                            iterator.remove();
                                            continue;
                                        }
                                        index = checkPlayerExistInTeamB(info.getId());
                                        if (index != -1) {
                                            iterator.remove();
                                        }
                                    }
                                }
                            }
                            playerAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });
            }
        });
        socket.on("game:join-via-link", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String msg = data.getString("message");
                            Functions.showToast(getContext(), msg, FancyToast.SUCCESS);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                });
            }
        });
        socket.on("player:update-friends-group", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String friendId = data.getString("friend_id");
                            String gameId = data.getString("game_id");
                            String inGame = data.getString("in_game");
                            for (int i = 0; i<listItems.size(); i++){
                                if (friendId.equalsIgnoreCase(listItems.get(i).getFriendId())){
                                    listItems.get(i).setGameId(gameId);
                                    listItems.get(i).setInGame(inGame);
                                }
                            }
                            pager_indicator.removeAllViews();
                            itemsPager_adapter = new SliderAdapter(getContext(), listItems, page);
                            itemsPager_adapter.setOnItemClickListener(MainActivity.this);
                            page.setAdapter(itemsPager_adapter);
                            setPageViewIndicator();
                            itemsPager_adapter.notifyDataSetChanged();
                            if (inGame.equalsIgnoreCase("0") && gameId.equalsIgnoreCase(GameID)){
                                gameTeam = null;
                                teamA.getPlayers().clear();
                                teamB.getPlayers().clear();
                                GameID = "";
                                populateTeamData();
                                showHideBtnVu();

                            }
                        }
                        catch (JsonSyntaxException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        socket.on("game:player-dragging", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String teamId = data.getString("team_id");
                            String gameId = data.getString("game_id");
                            String playerId = data.getString("playerId");
                            float newX = (float) data.getDouble("xOffset");
                            float newY = (float) data.getDouble("yOffset");
                            String isGoalKeeper = data.getString("is_goalkeeper");

                            if (gameId.equalsIgnoreCase(GameID)){
                                if (teamId.equalsIgnoreCase(teamA.getId())){
                                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                        if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                            PreviewFieldView vuA = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                            if (vuA.getPlayerInfo().getId().equalsIgnoreCase(playerId) && gameId.equalsIgnoreCase(GameID)) {
                                                vuA.getPlayerInfo().setIsGoalkeeper(isGoalKeeper);
                                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                float xValue = newX;
                                                float yValue = newY;
                                                float actualXValue = xValue * teamAVuWidth;
                                                float actualYValue = yValue * teamAVuHeight;
                                                // setViewMargin(params, actualXValue, actualYValue);
                                                vuA.moveImage(actualXValue, actualYValue);
                                                if (isGoalKeeper.equalsIgnoreCase("1")){
                                                    vuA.setImage(teamA.getTeamGkShirt());
                                                }else{
                                                    vuA.setImage(teamA.getTeamShirt());
                                                }

                                                break;
                                            }

                                        }
                                    }
                                }
                                else {
                                    for (int j = 0; j < binding.vuTeamB.getChildCount(); j++) {
                                        if (binding.vuTeamB.getChildAt(j) instanceof PreviewFieldView) {
                                            PreviewFieldView vuB = (PreviewFieldView) binding.vuTeamB.getChildAt(j);
                                            if (vuB.getPlayerInfo().getId().equalsIgnoreCase(playerId) && gameId.equalsIgnoreCase(GameID)) {
                                                vuB.getPlayerInfo().setIsGoalkeeper(isGoalKeeper);
                                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                float xValue = newX;
                                                float yValue = newY;
                                                float actualXValue = xValue * teamBVuWidth;
                                                float actualYValue = yValue * teamBVuHeight;
                                                //setViewMargin(params, actualXValue, actualYValue);
                                                vuB.moveImage(actualXValue, actualYValue);
                                                if (isGoalKeeper.equalsIgnoreCase("1")){
                                                    vuB.setImage(teamB.getTeamGkShirt());
                                                }else{
                                                    vuB.setImage(teamB.getTeamShirt());
                                                }

                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        socket.on("team:player-removed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String friendId = data.getString("friend_id");
                            String friendshipId = data.getString("friendship_id");
                            String gameId = data.getString("game_id");
                            String teamId = data.getString("team_id");
                            if (gameId.equalsIgnoreCase(GameID)){
                                for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                    if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                        PreviewFieldView vuA = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                        if (vuA.getPlayerInfo().getId().equalsIgnoreCase(friendId) && gameId.equalsIgnoreCase(GameID)) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    PlayerInfo info = vuA.getPlayerInfo();
                                                    info.setIsGoalkeeper("0");
                                                    info.setInGame("1");
                                                    info.setxCoordinate("");
                                                    info.setyCoordinate("");
                                                    info.setCardType("");
                                                    friendList.add(0, info);
                                                    if (teamId.equalsIgnoreCase(teamA.getId())) {
                                                        binding.vuTeamA.removeView(vuA);
                                                        for (int i = 0; i < teamA.getPlayers().size(); i++) {
                                                            if (teamA.getPlayers().get(i).getId().equalsIgnoreCase(info.getId())) {
                                                                teamA.getPlayers().remove(i);
                                                                break;
                                                            }
                                                        }
                                                        binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                                    }



//                                                if (teamACaptainAvailable || teamBCaptainAvailable) {
//                                                    if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                                                        saveData(friendId, "remove", teamACaptainId, teamId, "", "", "", "", "", "");
//                                                    }
//                                                    else if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                                                        saveData(friendId, "remove", teamBCaptainId, teamId, "", "", "", "", "", "");
//                                                    }
//                                                }


                                                    playerAdapter.notifyDataSetChanged();

                                                }
                                            });
                                            break;
                                        }

                                    }
                                }

                                for (int j = 0; j < binding.vuTeamB.getChildCount(); j++) {
                                    if (binding.vuTeamB.getChildAt(j) instanceof PreviewFieldView) {
                                        PreviewFieldView vuB = (PreviewFieldView) binding.vuTeamB.getChildAt(j);
                                        if (vuB.getPlayerInfo().getId().equalsIgnoreCase(friendId) && gameId.equalsIgnoreCase(GameID)) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    PlayerInfo info = vuB.getPlayerInfo();
                                                    info.setIsGoalkeeper("0");
                                                    info.setInGame("1");
                                                    info.setxCoordinate("");
                                                    info.setyCoordinate("");
                                                    info.setCardType("");
                                                    friendList.add(0, info);
                                                    if (teamId.equalsIgnoreCase(teamB.getId())) {
                                                        binding.vuTeamB.removeView(vuB);
                                                        for (int i = 0; i < teamB.getPlayers().size(); i++) {
                                                            if (teamB.getPlayers().get(i).getId().equalsIgnoreCase(info.getId())) {
                                                                teamB.getPlayers().remove(i);
                                                                break;
                                                            }
                                                        }
                                                        binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                                    }
//                                                if (teamACaptainAvailable || teamBCaptainAvailable) {
//                                                    if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                                                        saveData(friendId, "remove", teamACaptainId, teamId, "", "", "", "", "", "");
//                                                    }
//                                                    else if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                                                        saveData(friendId, "remove", teamBCaptainId, teamId, "", "", "", "", "", "");
//                                                    }
//                                                }

                                                    playerAdapter.notifyDataSetChanged();
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            }



                        } catch (JsonSyntaxException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        socket.on("game:player-replaced", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String gameId = data.getString("game_id");

                            if (gameId.equalsIgnoreCase(GameID)){
                                JSONObject oldPlayer = data.getJSONObject("old_player");
                                String oldFriendId = oldPlayer.getString("friend_id");
                                String oldPlayerTeamId = oldPlayer.getString("team_id");

                                JSONObject newPlayer = data.getJSONObject("new_player");
                                String newFriendId = newPlayer.getString("id");
                                String newPlayerTeamId = newPlayer.getString("team_id");
                                String xCord = newPlayer.getString("x_coordinate");
                                String yCord = newPlayer.getString("y_coordinate");
                                String inGame = newPlayer.getString("in_game");
                                String inTeam = newPlayer.getString("in_team");

                                if (inTeam.equalsIgnoreCase("0")){
                                    //Update friendList based on player replacement
                                    for (int i = 0; i < friendList.size(); i++) {
                                        if (friendList.get(i) != null) {
                                            if (newFriendId.equalsIgnoreCase(friendList.get(i).getId())) {
                                                friendList.get(i).setInGame("1");
                                            }
                                            if (oldFriendId.equalsIgnoreCase(friendList.get(i).getId())) {
                                                friendList.get(i).setInGame("0");
                                            }
                                        }
                                    }
                                    playerAdapter.notifyDataSetChanged();
                                }else{
                                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                        if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                            PreviewFieldView vuA = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                            if (vuA.getPlayerInfo().getId().equalsIgnoreCase(oldFriendId) && gameId.equalsIgnoreCase(GameID)) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Remove old player from UI
                                                        PlayerInfo oldInfo = vuA.getPlayerInfo();
                                                        oldInfo.setIsGoalkeeper("0");
                                                        oldInfo.setInGame("0");
                                                        oldInfo.setInTeam("0");
                                                        oldInfo.setxCoordinate("");
                                                        oldInfo.setyCoordinate("");
                                                        oldInfo.setCardType("");
                                                        friendList.add(0, oldInfo);
                                                        if (oldPlayerTeamId.equalsIgnoreCase(teamA.getId())) {
                                                            binding.vuTeamA.removeView(vuA);
                                                            for (int i = 0; i < teamA.getPlayers().size(); i++) {
                                                                if (teamA.getPlayers().get(i).getId().equalsIgnoreCase(oldInfo.getId())) {
                                                                    teamA.getPlayers().remove(i);
                                                                    break;
                                                                }
                                                            }
                                                            binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                                        }

                                                        // Add new player to UI
                                                        PreviewFieldView newVuA = new PreviewFieldView(getContext(), teamA.getPlayers().size(),teamB.getPlayers().size());
                                                        PlayerInfo newInfo = findPlayerInfoInFriendList(newFriendId);
                                                        if (newInfo != null) {
                                                            populateDataInTeamAVu(newVuA, newInfo, teamAVuWidth, teamAVuHeight);
                                                            newInfo.setxCoordinate(xCord);
                                                            newInfo.setyCoordinate(yCord);
                                                            newInfo.setInTeam(inTeam);
                                                            newInfo.setInGame(inGame);
                                                            // Add newVuA to vuTeamA with appropriate params
                                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                            if (newInfo.getxCoordinate() != null && !newInfo.getxCoordinate().isEmpty() && newInfo.getyCoordinate() != null && !newInfo.getyCoordinate().isEmpty()) {
                                                                float xValue = Float.parseFloat(newInfo.getxCoordinate());
                                                                float yValue = Float.parseFloat(newInfo.getyCoordinate());
                                                                float actualXValue = xValue * teamAVuWidth;
                                                                float actualYValue = yValue *   teamAVuHeight ;
                                                                setViewMargin(params, actualXValue, actualYValue);
                                                                binding.vuTeamA.addView(newVuA, params);
                                                            }
                                                            teamA.getPlayers().add(newInfo);
                                                            binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                                            friendList.remove(newInfo);
                                                            playerAdapter.notifyDataSetChanged();

                                                        } else if (teamACaptainAvailable || teamBCaptainAvailable){
                                                            PlayerInfo newInfoA = new Gson().fromJson(newPlayer.toString(), PlayerInfo.class);
                                                            populateDataInTeamAVu(newVuA, newInfoA, teamAVuWidth, teamAVuHeight);
                                                            newInfoA.setxCoordinate(xCord);
                                                            newInfoA.setyCoordinate(yCord);
                                                            newInfoA.setInTeam(inTeam);
                                                            newInfoA.setInGame(inGame);
                                                            // Add newVuA to vuTeamA with appropriate params
                                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                            if (newInfoA.getxCoordinate() != null && !newInfoA.getxCoordinate().isEmpty() && newInfoA.getyCoordinate() != null && !newInfoA.getyCoordinate().isEmpty()) {
                                                                float xValue = Float.parseFloat(newInfoA.getxCoordinate());
                                                                float yValue = Float.parseFloat(newInfoA.getyCoordinate());
                                                                float actualXValue = xValue * teamAVuWidth;
                                                                float actualYValue = yValue *   teamAVuHeight ;
                                                                setViewMargin(params, actualXValue, actualYValue);
                                                                binding.vuTeamA.addView(newVuA, params);
                                                            }
                                                            teamA.getPlayers().add(newInfoA);
                                                            binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                                            friendList.remove(newInfoA);
                                                            playerAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                                break;
                                            }
                                        }
                                    }

                                    for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                        if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                            PreviewFieldView vuB = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                            if (vuB.getPlayerInfo().getId().equalsIgnoreCase(oldFriendId) && gameId.equalsIgnoreCase(GameID)) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Remove old player from UI
                                                        PlayerInfo oldInfo = vuB.getPlayerInfo();
                                                        oldInfo.setIsGoalkeeper("0");
                                                        oldInfo.setInGame("0");
                                                        oldInfo.setInTeam("0");
                                                        oldInfo.setxCoordinate("");
                                                        oldInfo.setyCoordinate("");
                                                        oldInfo.setCardType("");
                                                        friendList.add(0, oldInfo);
                                                        if (oldPlayerTeamId.equalsIgnoreCase(teamB.getId())) {
                                                            binding.vuTeamB.removeView(vuB);
                                                            for (int i = 0; i < teamB.getPlayers().size(); i++) {
                                                                if (teamB.getPlayers().get(i).getId().equalsIgnoreCase(oldInfo.getId())) {
                                                                    teamB.getPlayers().remove(i);
                                                                    break;
                                                                }
                                                            }
                                                            binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                                        }

                                                        // Add new player to UI
                                                        PreviewFieldView newVuB = new PreviewFieldView(getContext(), teamA.getPlayers().size(),teamB.getPlayers().size());
                                                        PlayerInfo newInfo = findPlayerInfoInFriendList(newFriendId);
                                                        if (newInfo != null) {
                                                            populateDataInTeamBVu(newVuB, newInfo, teamBVuWidth, teamBVuHeight);
                                                            newInfo.setxCoordinate(xCord);
                                                            newInfo.setyCoordinate(yCord);
                                                            newInfo.setInTeam(inTeam);
                                                            newInfo.setInGame(inGame);
                                                            // Add newVuA to vuTeamA with appropriate params
                                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                            if (newInfo.getxCoordinate() != null && !newInfo.getxCoordinate().isEmpty() && newInfo.getyCoordinate() != null && !newInfo.getyCoordinate().isEmpty()) {
                                                                float xValue = Float.parseFloat(newInfo.getxCoordinate());
                                                                float yValue = Float.parseFloat(newInfo.getyCoordinate());
                                                                float actualXValue = xValue * teamBVuWidth;
                                                                float actualYValue
                                                                        = yValue *   teamBVuHeight ;
                                                                setViewMargin(params, actualXValue, actualYValue);
                                                                binding.vuTeamB.addView(newVuB, params);
                                                            }
                                                            teamB.getPlayers().add(newInfo);
                                                            binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                                            friendList.remove(newInfo);
                                                            playerAdapter.notifyDataSetChanged();

                                                        }else if (teamACaptainAvailable || teamBCaptainAvailable){
                                                            PlayerInfo newInfoB = new Gson().fromJson(newPlayer.toString(), PlayerInfo.class);
                                                            populateDataInTeamBVu(newVuB, newInfoB, teamBVuWidth, teamBVuHeight);
                                                            newInfoB.setxCoordinate(xCord);
                                                            newInfoB.setyCoordinate(yCord);
                                                            newInfoB.setInTeam(inTeam);
                                                            newInfoB.setInGame(inGame);
                                                            // Add newVuA to vuTeamA with appropriate params
                                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                            if (newInfoB.getxCoordinate() != null && !newInfoB.getxCoordinate().isEmpty() && newInfoB.getyCoordinate() != null && !newInfoB.getyCoordinate().isEmpty()) {
                                                                float xValue = Float.parseFloat(newInfoB.getxCoordinate());
                                                                float yValue = Float.parseFloat(newInfoB.getyCoordinate());
                                                                float actualXValue = xValue * teamBVuWidth;
                                                                float actualYValue = yValue *   teamBVuHeight ;
                                                                setViewMargin(params, actualXValue, actualYValue);
                                                                binding.vuTeamB.addView(newVuB, params);
                                                            }
                                                            teamB.getPlayers().add(newInfoB);
                                                            binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                                            friendList.remove(newInfoB);
                                                            playerAdapter.notifyDataSetChanged();
                                                        }
                                                    }

                                                });
                                                break;
                                            }
                                        }
                                    }
                                }

                            }

                        } catch (JsonSyntaxException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        socket.on("game:player-swapped", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String gameId = data.getString("game_id");
                            if (GameID !=null){
                                if (gameId.equalsIgnoreCase(GameID)){
                                    JSONObject dataa = new JSONObject();
                                    try {
                                        dataa.put("game_id", gameId);
                                        socket.emit("game:join", dataa);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

//                            JSONObject player1 = data.getJSONObject("player_1");
//                            String player1Id = player1.getString("friend_id");
//                            String player1FriendshipId = player1.getString("friendship_id");
//                            String player1TeamId = player1.getString("team_id");
//
//                            JSONObject player2 = data.getJSONObject("player_2");
//                            String player2Id = player2.getString("friend_id");
//                            String player2FriendshipId = player2.getString("friendship_id");
//                            String player2TeamId = player2.getString("team_id");
//
//                            // Add new player to UI
////                            PreviewFieldView newVuA = new PreviewFieldView(getContext());
////                            PlayerInfo newInfo = null;
////                            for (int i =0; i<teamB.getPlayers().size(); i++){
////                                if (player2Id.equalsIgnoreCase(teamB.getPlayers().get(i).getId())){
////                                    newInfo = findPlayerInfoInteamB(player2Id);
////                                }
////                            }
////                            if (newInfo != null) {
////                                populateDataInTeamAVu(newVuA, newInfo, teamAVuWidth, teamAVuHeight);
////                                // Add newVuA to vuTeamA with appropriate params
////                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                                if (newInfo.getxCoordinate() != null && !newInfo.getxCoordinate().isEmpty() && newInfo.getyCoordinate() != null && !newInfo.getyCoordinate().isEmpty()) {
////                                    float xValue = Float.parseFloat(newInfo.getxCoordinate());
////                                    float yValue = Float.parseFloat(newInfo.getyCoordinate());
////                                    float actualXValue = xValue * teamAVuWidth;
////                                    float actualYValue = yValue *   teamAVuHeight ;
////                                    setViewMargin(params, actualXValue, actualYValue);
////                                    binding.vuTeamA.addView(newVuA, params);
////                                }
////                                teamA.getPlayers().add(newInfo);
////                            }
//
//
//                            for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
//                                if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
//                                    PreviewFieldView vuA = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
//                                    if (vuA.getPlayerInfo().getId().equalsIgnoreCase(player1Id) && gameId.equalsIgnoreCase(GameID)) {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                //Remove old player from UI
//                                                PlayerInfo playerInfo1 = vuA.getPlayerInfo();
//                                                binding.vuTeamA.removeView(vuA);
//
////                                                for (int i = 0; i < teamA.getPlayers().size(); i++) {
////                                                        if (teamA.getPlayers().get(i).getId().equalsIgnoreCase(playerInfo1.getId())) {
////                                                            teamA.getPlayers().remove(i);
////                                                            break;
////                                                        }
////                                                    }
//
////                                                if (oldInfo.getId().equalsIgnoreCase(player1Id)) {
//
////                                                }
//
//                                                // Add new player to UI
//                                                PreviewFieldView newVuA = new PreviewFieldView(getContext());
//                                                PlayerInfo playerInfo2 = null;
//                                                for (int i =0; i<teamB.getPlayers().size(); i++){
//                                                    if (player2Id.equalsIgnoreCase(teamB.getPlayers().get(i).getId())){
//                                                        playerInfo2 = findPlayerInfoInteamB(player2Id);
//                                                    }
//                                                }
//                                                if (playerInfo2 != null) {
//                                                    populateDataInTeamAVu(newVuA, playerInfo2, teamAVuWidth, teamAVuHeight);
//                                                    //Add newVuA to vuTeamA with appropriate params
//                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                                    if (playerInfo2.getxCoordinate() != null && !playerInfo2.getxCoordinate().isEmpty() && playerInfo2.getyCoordinate() != null && !playerInfo2.getyCoordinate().isEmpty()) {
//                                                        float xValue = Float.parseFloat(playerInfo2.getxCoordinate());
//                                                        float yValue = Float.parseFloat(playerInfo2.getyCoordinate());
//                                                        float actualXValue = xValue * teamAVuWidth;
//                                                        float actualYValue = yValue *   teamAVuHeight;
//                                                        setViewMargin(params, actualXValue, actualYValue);
//                                                        binding.vuTeamA.addView(newVuA, params);
//                                                    }
//                                                    teamA.getPlayers().add(playerInfo2);
//                                                   // updatePlayerList(teamA.getPlayers(),oldInfo,newInfo);
////
//                                                }
//                                            }
//                                        });
//                                        break;
//                                    }
//                                }
//                            }
//
//                            for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
//                                if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
//                                    PreviewFieldView vuB = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
//                                    if (vuB.getPlayerInfo().getId().equalsIgnoreCase(player2Id) && gameId.equalsIgnoreCase(GameID)) {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                //Remove old player from UI
//                                                PlayerInfo playerInfo1 = vuB.getPlayerInfo();
//                                                binding.vuTeamB.removeView(vuB);
//
//                                                // Add new player to UI
//                                                PreviewFieldView newVuB = new PreviewFieldView(getContext());
//                                                PlayerInfo playerInfo2 = null;
//                                                for (int i =0; i<teamA.getPlayers().size(); i++){
//                                                    if (player1Id.equalsIgnoreCase(teamA.getPlayers().get(i).getId())){
//                                                        playerInfo2 = findPlayerInfoInteamA(player1Id);
//                                                    }
//                                                }
//                                                if (playerInfo2 != null) {
//                                                    populateDataInTeamBVu(newVuB, playerInfo2, teamBVuWidth, teamBVuHeight);
//                                                    // Add newVuA to vuTeamA with appropriate params
//                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                                    if (playerInfo2.getxCoordinate() != null && !playerInfo2.getxCoordinate().isEmpty() && playerInfo2.getyCoordinate() != null && !playerInfo2.getyCoordinate().isEmpty()) {
//                                                        float xValue = Float.parseFloat(playerInfo2.getxCoordinate());
//                                                        float yValue = Float.parseFloat(playerInfo2.getyCoordinate());
//                                                        float actualXValue = xValue * teamBVuWidth;
//                                                        float actualYValue = yValue *   teamBVuHeight ;
//                                                        setViewMargin(params, actualXValue, actualYValue);
//                                                        binding.vuTeamB.addView(newVuB, params);
//                                                    }
//                                                    teamB.getPlayers().add(playerInfo2);
//
//                                                    //teamA.getPlayers().remove(playerInfo1);
////                                                    teamA.getPlayers().add(playerInfo2);
////                                                    teamA.getPlayers().remove(playerInfo1);
////                                                    for (int i = 0; i < teamB.getPlayers().size(); i++) {
////                                                        if (teamB.getPlayers().get(i).getId().equalsIgnoreCase(oldInfo.getId())) {
////                                                            teamB.getPlayers().remove(i);
////                                                            break;
////                                                        }
////                                                    }
//                                                }
//
//
//
//
//                                            }
//                                        });
//                                        break;
//                                    }
//                                }
//                            }



                        }
                        catch (JsonSyntaxException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });




            }
        });
        socket.on("game:captains-created", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String gameId = data.getString("game_id");
                            String selectionType = data.getString("selection_type");
                            GameID = gameId;

                            JSONObject team_a = data.getJSONObject("team_a");
                            String teamAId = team_a.getString("team_id");
                            teamACaptainId = team_a.getString("captain_id");

                            JSONObject team_b = data.getJSONObject("team_b");
                            String teamBId = team_b.getString("team_id");
                            teamBCaptainId = team_b.getString("captain_id");

                            for (int i = 0; i < friendList.size(); i++) {
                                if (friendList.get(i) != null) {
                                    if (teamACaptainId.equalsIgnoreCase(friendList.get(i).getId())) {
                                        friendList.get(i).setIsCaptain("1");
                                        teamA.getPlayers().add(friendList.get(i));

                                    }
                                    if (teamBCaptainId.equalsIgnoreCase(friendList.get(i).getId())) {
                                        friendList.get(i).setIsCaptain("1");
                                        teamB.getPlayers().add(friendList.get(i));

                                    }
                                }
                            }
                            binding.vuTeamA.removeAllViews();
                            binding.vuTeamB.removeAllViews();
                            if (gameTeam != null){
                                List<String> captains = new ArrayList<>();
                                captains.add(teamACaptainId);
                                captains.add(teamBCaptainId);
                                gameTeam.setCaptains(captains);
                                populateTeamData();
                            }
                            if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) && teamACaptainId.equalsIgnoreCase(gameTeam.getCreatedBy())){
                                showPlayerChooseAlert();
                            } else if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) && teamBCaptainId.equalsIgnoreCase(gameTeam.getCreatedBy())) {
                                showPlayerChooseAlert();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        socket.on("game:captain-turn", new Emitter.Listener() {  //runs 2 times (fix)
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String gameId = data.getString("game_id");
                            String captainId = data.getString("captain_id");
                            if (teamA == null || teamB == null){
                                JSONObject dataa = new JSONObject();
                                try {
                                    dataa.put("game_id", gameId);
                                    socket.emit("game:join", dataa);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (gameId.equalsIgnoreCase(GameID)){
                                if (!captainId.isEmpty() && captainId.equalsIgnoreCase(teamACaptainId)) {
                                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                                        binding.tvTurn.setVisibility(View.VISIBLE);
                                        if (getGamePlayersCount() > 0) {
                                            captainATurn = true;
                                            captainBTurn = false;
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tabSelected(binding.tabTeamA);
                                                }
                                            }, 20);

                                            if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                                playSoundFromAssets("yourturn_arabic.mp3"); //beep.mp3
                                            }
                                        }
                                        binding.tvTurn.setText(getString(R.string.captain_turn_place, teamA.getTeamName()));
                                        if (getGamePlayersCount() == 0) {
                                            binding.tvTurn.setVisibility(View.GONE);
                                        }
                                    }
                                    else {
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                } else if (!captainId.isEmpty() && captainId.equalsIgnoreCase(teamBCaptainId)) {
                                    if (teamACaptainAvailable || teamBCaptainAvailable) {
                                        binding.tvTurn.setVisibility(View.VISIBLE);
                                        if (getGamePlayersCount() > 0) {
                                            captainATurn = false;
                                            captainBTurn = true;
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tabSelected(binding.tabTeamB);
                                                }
                                            }, 20);
                                            if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                                playSoundFromAssets("yourturn_arabic.mp3"); //beep.mp3
                                            }
                                        }
                                        binding.tvTurn.setText(getString(R.string.captain_turn_place, teamB.getTeamName()));
                                        if (getGamePlayersCount() == 0) {
                                            binding.tvTurn.setVisibility(View.GONE);
                                        }
                                    }
                                    else {
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        socket.on("game:captains-removed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String gameId = data.getString("game_id");
                            if (gameId.equalsIgnoreCase(GameID)){
                                binding.tvTurn.setVisibility(View.GONE);
                                teamACaptainAvailable = false;
                                teamBCaptainAvailable = false;
                                teamACaptainId = "";
                                teamBCaptainId = "";
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("player_id", FriendId);
                                    socket.emit("player:friends", data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                binding.vuTeamA.removeAllViews();
                                binding.vuTeamB.removeAllViews();
                                binding.tvTeamA.setText("Team A");
                                binding.tvTeamB.setText("Team B");
                                binding.tvTeamACount.setText("0");
                                binding.tvTeamBCount.setText("0");
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
        socket.on("game:player-dragged", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String gameId = data.getString("game_id");
                            String teamId = data.getString("team_id");
                            String friendId = data.getString("friend_id");
                            String xCoordinate = data.getString("x_coordinate");
                            String yCoordinate = data.getString("y_coordinate");
                            String isGoalKeeper = data.getString("is_goalkeeper");
                            String friendShipId = data.getString("friendship_id");
                            if (gameId.equalsIgnoreCase(GameID)){
                                if (gameId.equalsIgnoreCase(GameID) && teamId.equalsIgnoreCase(teamA.getId())){
                                    PreviewFieldView newVuA = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
                                    PlayerInfo newInfo = findPlayerInfoInFriendList(friendId);
                                    if (newInfo != null) {
                                        populateDataInTeamAVu(newVuA, newInfo, teamAVuWidth, teamAVuHeight);
                                        newInfo.setxCoordinate(xCoordinate);
                                        newInfo.setyCoordinate(yCoordinate);
                                        newInfo.setInTeam("1");
                                        newInfo.setInGame("1");
                                        newInfo.setIsGoalkeeper(isGoalKeeper);
                                        // Add newVuA to vuTeamA with appropriate params
                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        if (newInfo.getxCoordinate() != null && !newInfo.getxCoordinate().isEmpty() && newInfo.getyCoordinate() != null && !newInfo.getyCoordinate().isEmpty()) {
                                            float xValue = Float.parseFloat(newInfo.getxCoordinate());
                                            float yValue = Float.parseFloat(newInfo.getyCoordinate());
                                            float actualXValue = xValue * teamAVuWidth;
                                            float actualYValue = yValue *   teamAVuHeight ;
                                            setViewMargin(params, actualXValue, actualYValue);
                                            binding.vuTeamA.addView(newVuA, params);
                                        }
                                        teamA.getPlayers().add(newInfo);
                                        binding.tvTeamACount.setText(String.valueOf(teamA.getPlayers().size()));
                                        friendList.remove(newInfo);

                                    }
                                    playerAdapter.notifyDataSetChanged();

                                }
                                else if (gameId.equalsIgnoreCase(GameID) && teamId.equalsIgnoreCase(teamB.getId())){
                                    PreviewFieldView newVuB = new PreviewFieldView(getContext(),teamA.getPlayers().size(), teamB.getPlayers().size());
                                    PlayerInfo newInfo = findPlayerInfoInFriendList(friendId);
                                    if (newInfo != null) {
                                        populateDataInTeamBVu(newVuB, newInfo, teamBVuWidth, teamBVuHeight);
                                        newInfo.setxCoordinate(xCoordinate);
                                        newInfo.setyCoordinate(yCoordinate);
                                        newInfo.setInTeam("1");
                                        newInfo.setInGame("1");
                                        newInfo.setIsGoalkeeper(isGoalKeeper);
                                        // Add newVuA to vuTeamA with appropriate params
                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        if (newInfo.getxCoordinate() != null && !newInfo.getxCoordinate().isEmpty() && newInfo.getyCoordinate() != null && !newInfo.getyCoordinate().isEmpty()) {
                                            float xValue = Float.parseFloat(newInfo.getxCoordinate());
                                            float yValue = Float.parseFloat(newInfo.getyCoordinate());
                                            float actualXValue = xValue * teamBVuWidth;
                                            float actualYValue = yValue *   teamBVuHeight ;
                                            setViewMargin(params, actualXValue, actualYValue);
                                            binding.vuTeamB.addView(newVuB, params);
                                        }
                                        teamB.getPlayers().add(newInfo);
                                        binding.tvTeamBCount.setText(String.valueOf(teamB.getPlayers().size()));
                                        friendList.remove(newInfo);

                                    }
                                    playerAdapter.notifyDataSetChanged();


                                }
                                if (binding.vuTeamA.getChildCount() == 8 || binding.vuTeamB.getChildCount() == 8) {
                                    binding.vuTeamA.removeAllViews();
                                    binding.vuTeamB.removeAllViews();
                                    for (PlayerInfo info : teamA.getPlayers()) {
                                        replaceViewTeamA(new DragData(info, -1), 0, 0, false);
                                    }

                                    for (PlayerInfo info : teamB.getPlayers()) {
                                        replaceViewTeamB(new DragData(info, -1), 0, 0, false);
                                    }
                                }
                                if (teamACaptainAvailable || teamBCaptainAvailable) {
                                    if (getGamePlayersCount() == 0){
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                }
                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });
        socket.on("team:shirt-changed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String teamId = data.getString("team_id");
                            String shirtId = data.getString("team_shirt_id");
                            String teamShirt = data.getString("team_shirt");
                            String type = data.getString("type");
                            String gameId = data.getString("game_id");
                            if (gameId.equalsIgnoreCase(GameID)){
                                if (teamA !=null && teamId.equalsIgnoreCase(teamA.getId())){
                                    if (type.equalsIgnoreCase("team")){
                                        teamA.setTeamShirt(teamShirt);
                                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                                if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                                    vu.setImage(teamShirt);
                                                }
                                            }
                                        }
                                    }else{
                                        teamA.setTeamGkShirt(teamShirt);
                                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                                if (vu.getPlayerInfo().getIsGoalkeeper() !=null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                                    vu.setImage(teamA.getTeamGkShirt());
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                }
                                else if (teamB !=null && teamId.equalsIgnoreCase(teamB.getId())){
                                    if (type.equalsIgnoreCase("team")){
                                        teamB.setTeamShirt(teamShirt);
                                        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                                if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                                    vu.setImage(teamShirt);
                                                }
                                            }
                                        }
                                    }else{
                                        teamB.setTeamGkShirt(teamShirt);
                                        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                                                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                                if (vu.getPlayerInfo().getIsGoalkeeper() !=null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                                    vu.setImage(teamB.getTeamGkShirt());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });
        socket.on("game:zego-details", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String roomId = data.getString("chat_room_id");
                            String zegoToken = data.getString("zego_token");
                            String gameId = data.getString("game_id");
                            if (gameId.equalsIgnoreCase(GameID)){
                                if (gameTeam!=null){
                                    gameTeam.setRoomId(roomId);
                                    gameTeam.setZegoToken(zegoToken);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });

        socket.on("game:reset", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String gameId = data.getString("game_id");
                            if (gameId.equalsIgnoreCase(listItems.get(0).getGameId())){
                                binding.tvTurn.setVisibility(View.GONE);
                                if (teamACaptainAvailable || teamBCaptainAvailable) {
                                    teamACaptainAvailable = false;
                                    teamBCaptainAvailable = false;
                                    teamACaptainId = "";
                                    teamBCaptainId = "";
                                }
                                gameTeam = null;
                                teamA.getPlayers().clear();
                                teamB.getPlayers().clear();
                                GameID = "";
                                populateTeamData();
                                showHideBtnVu();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });
        // Offer listener
        socket.on("call:offer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String user = data.optString("user", "");
                        String gameId = data.optString("game_id", "");
                        String offerSessionDescription = data.optString("offer", "");

                        if (gameId.equalsIgnoreCase(GameID)){
                            DataModel model = new Gson().fromJson(offerSessionDescription, DataModel.class);
                            if (!user.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                // Use the sender ID as the target ID when handling the offer
                                target = model.getSender();
                                if (model.getData() != null) {
                                    webRTCClient.onRemoteSessionReceived(new SessionDescription(SessionDescription.Type.OFFER, model.getData()));
                                }

                                String[] permissions = {Manifest.permission.RECORD_AUDIO};
                                Permissions.check(getContext(), permissions, null/*rationale*/,null/*options*/, new PermissionHandler() {
                                    @Override
                                    public void onGranted() {
                                        binding.btnSpeak.setImageResource(R.drawable.mic_activel);
                                        isRecording = false;
                                        webRTCClient.toggleAudio(true);
                                        webRTCClient.answer(model.getSender());
                                    }
                                });




                            }
                            Log.d("TAG", "call:offerListner: " + target + model.getData() + model.getTarget() + model.getSender() + model.getType());
                        }

                    }
                });
            }
        });
        // Answer listener
        socket.on("call:answer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String user = data.optString("user", "");
                        String gameId = data.optString("game_id", "");
                        String answerDescription = data.optString("answer", "");

                        if (gameId.equalsIgnoreCase(GameID)){
                            DataModel model = new Gson().fromJson(answerDescription, DataModel.class);
                            if (!user.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                // Use the sender ID as the target ID when handling the answer
                                target = model.getSender();
                                if (model.getData() != null) {
                                    webRTCClient.onRemoteSessionReceived(new SessionDescription(SessionDescription.Type.ANSWER, model.getData()));
                                }
                            }
                            Log.d("TAG", "call:answerListner: " + target + model.getData() + model.getTarget() + model.getSender() + model.getType());
                        }

                    }
                });
            }
        });
        // Candidate listener
        socket.on("call:candidate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String user = data.optString("user", "");
                        String gameId = data.optString("game_id", "");
                        String candidateDescription = data.optString("candidate", "");

                        if (gameId.equalsIgnoreCase(GameID)) {
                            DataModel model = new Gson().fromJson(candidateDescription, DataModel.class);
                            if (!user.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                try {
                                    IceCandidate candidate = new Gson().fromJson(model.getData(), IceCandidate.class);
                                    webRTCClient.addIceCandidate(candidate);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.d("TAG", "call:candidaterListner: " + target + model.getData() + model.getTarget() + model.getSender() + model.getType());
                        }


                    }
                });
            }
        });


        socket.emit("game:startup");

    }
    private PlayerInfo findPlayerInfoInFriendList(String playerId) {
        for (PlayerInfo info : friendList) {
            if (info != null && playerId.equalsIgnoreCase(info.getId())) {
                return info;
            }
        }
        return null;
    }
    public void cordEmitter(String teamId, String playerID, float x, float y, String isGoalKeeper) {
        if (GameID !=null && !GameID.equalsIgnoreCase("")){
            JSONObject data = new JSONObject();
            try {
                data.put("team_id", teamId);
                data.put("playerId", playerID);
                data.put("xOffset", x);
                data.put("yOffset", y);
                data.put("game_id", GameID);
                data.put("is_goalkeeper", isGoalKeeper);
                socket.emit("game:player-dragging", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveCordinates(String teamId, String friendId, float xCoordinate, float yCoordinate, String isGoalKeeper, String friendShipId, boolean isAdd){
        if (!GameID.equalsIgnoreCase("")){
            cordEmitter(teamId, friendId,  xCoordinate, yCoordinate, isGoalKeeper);
            JSONObject data = new JSONObject();
            try {
                data.put("game_id",GameID);
                data.put("team_id", teamId);
                data.put("friend_id", friendId);
                data.put("x_coordinate", String.valueOf(xCoordinate));
                data.put("y_coordinate", String.valueOf(yCoordinate));
                data.put("is_goalkeeper", isGoalKeeper);
                data.put("friendship_id", friendShipId);
                socket.emit("game:player-dragged", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (teamACaptainAvailable || teamBCaptainAvailable) {
            if (getGamePlayersCount() == 0){
                binding.tvTurn.setVisibility(View.GONE);
            }
        }

    }
    public interface Listener{
        void webrtcConnected();
        void webrtcClosed();
    }


}