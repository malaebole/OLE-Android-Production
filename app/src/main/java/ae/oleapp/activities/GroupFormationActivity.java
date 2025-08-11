package ae.oleapp.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.PlayerListAdapter;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.adapters.ShirtsTeamAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityGroupFormationBinding;
import ae.oleapp.dialogs.FriendOptionsDialogFragment;
import ae.oleapp.dialogs.PlayerStatusDialogFragment;
import ae.oleapp.dialogs.SelectionListDialog;
import ae.oleapp.dialogs.SwapPlayerDialogFragment;
import ae.oleapp.models.Country;
import ae.oleapp.models.DataModel;
import ae.oleapp.models.DragData;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.SelectionList;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.Team;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.PreviewFieldView;
import ae.oleapp.zegocloudexpress.ExpressManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFormationActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGroupFormationBinding binding;
    private GameTeam gameTeam;
    private String gameId = "";
    private int teamAVuWidth = 0, teamAVuHeight = 0, teamBVuWidth = 0, teamBVuHeight = 0, selectedTab = 0;
    private float subVuH = 0, subVuW = 0;
    private boolean teamACaptainAvailable = false, teamBCaptainAvailable = false;
    private String teamACaptainId = "", teamBCaptainId = "", teamACaptainName = "", teamBCaptainName = "";
    private PlayerListAdapter playerAdapter;
    private ShirtsTeamAdapter teamAdapter;
    private ShirtListAdapter shirtAdapter;
    private ShirtListAdapter gkAdapter;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private final List<Country> countryList = new ArrayList<>();
    private final List<Team> teamList = new ArrayList<>();
    private final List<Shirt> shirtList = new ArrayList<>();
    private final List<Shirt> gkShirtList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ValueEventListener databaseHandle;
    private boolean captainATurn = false, captainBTurn = false;
    private String selectedCountryId = "", selectedTeamId = "", selectedShirtId = "", selectedGkShirtId = "";
    private final boolean isAudioRoomActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupFormationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameId = bundle.getString("game_id", "");
        }
        //Players
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.playersRecyclerVu.setLayoutManager(layoutManager);
        playerAdapter = new PlayerListAdapter(getContext(), playerList);
        playerAdapter.setItemClickListener(playerClickListener);
        binding.playersRecyclerVu.setAdapter(playerAdapter);

        //Teams
        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.teamRecyclerVu.setLayoutManager(teamLayoutManager);
        teamAdapter = new ShirtsTeamAdapter(getContext(), teamList);
        teamAdapter.setItemClickListener(teamClickListener);
        binding.teamRecyclerVu.setAdapter(teamAdapter);
        //Shirts
        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new ShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);
        //Goal keeper
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

        PreviewFieldView fieldView = new PreviewFieldView(getContext()); //grid or ground resolution es PreviewFieldView main haii
        binding.vuTeamA.addView(fieldView);
        fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                subVuW = fieldView.getWidth();
                subVuH = fieldView.getHeight();
                binding.vuTeamA.removeView(fieldView);
                getTeams(true);
            }
        });


        binding.tvTurn.setVisibility(View.GONE);
        binding.btmVu.setVisibility(View.INVISIBLE);

        binding.tabTeamA.setOnDragListener(vuDragListener);
        binding.tabTeamB.setOnDragListener(vuDragListener);
        binding.vuTeamA.setOnDragListener(vuDragListener);
        binding.vuTeamB.setOnDragListener(vuDragListener);
        tabSelected(binding.tabTeamA);

//        getTeams(true);

        binding.btnClose.setOnClickListener(this);
        binding.tabTeamA.setOnClickListener(this);
        binding.tabTeamB.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnGkShirts.setOnClickListener(this);
        binding.btnShirt.setOnClickListener(this);
        binding.countryVu.setOnClickListener(this);
        binding.btnRefresh.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (databaseReference != null && databaseHandle != null) {
            databaseReference.child("lineup").child(gameTeam.getGameId()).removeEventListener(databaseHandle);
        }
        try {
            ExpressManager.getInstance().enableMic(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameTeam != null && databaseReference != null && databaseHandle != null) {
            if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                if (gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
                    observeChange();
                    try {
                        ExpressManager.getInstance().enableMic(isAudioRoomActive);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            ExpressManager.getInstance().leaveRoom();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            GroupFormationActivity.this.finish(); //srf finish() tha
        }
        else if (view == binding.tabTeamA || view == binding.tabTeamB) {
            tabSelected(view);
            getTeams(false);
        }
        else if (view == binding.btnShirt) {
            binding.btnShirt.setImageResource(R.drawable.shirt_active);
            binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_inactivel);
            binding.teamRecyclerVu.setVisibility(View.VISIBLE);
            binding.shirtRecyclerVu.setVisibility(View.VISIBLE);
            binding.gkRecyclerVu.setVisibility(View.GONE);
            binding.countryVu.setVisibility(View.VISIBLE);
            binding.logo.setVisibility(View.INVISIBLE);
        }
        else if (view == binding.btnGkShirts) {
            binding.btnShirt.setImageResource(R.drawable.shirt_inactive);
            binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_activel);
            binding.teamRecyclerVu.setVisibility(View.GONE);
            binding.shirtRecyclerVu.setVisibility(View.GONE);
            binding.gkRecyclerVu.setVisibility(View.VISIBLE);
            binding.countryVu.setVisibility(View.INVISIBLE);
            binding.logo.setVisibility(View.VISIBLE);
        }
        else if (view == binding.btnShare) {
            if (gameTeam != null && !gameTeam.getGameId().isEmpty()) {
                Intent intent = new Intent(getContext(), ShareFieldActivity.class);
                intent.putExtra("game_id", gameTeam.getGameId());
                intent.putExtra("is_group_formation", true);
                startActivity(intent);
            }
        }
        else if (view == binding.btnSpeak) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO};
            Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                @Override
                public void onGranted() {
//                    if (isAudioRoomActive) {
//                        isAudioRoomActive = false;
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                        binding.btnSpeak.setImageResource(R.drawable.mic_deactivel);
//                        try {
//                            ExpressManager.getInstance().enableMic(false);
//                            ExpressManager.getInstance().leaveRoom();
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else {
//                        isAudioRoomActive = true;
//                        binding.btnSpeak.setImageResource(R.drawable.mic_activel);
//                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                        createZegoEngine();
//                        UserInfo userInfo = Functions.getUserinfo(getContext());
//                        joinRoom(gameTeam.getRoomId(), userInfo.getPhone(), userInfo.getId(),gameTeam.getZegoToken());
//                        ExpressManager.getInstance().enableMic(isAudioRoomActive);
//                    }
                }
            });
        }
        else if (view == binding.countryVu) {
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
                    selectedTeamId = "";
                    selectedShirtId = "";
                    populateCountryData(country);
                }
            });
            dialog.show();
        }
        else if (view == binding.btnRefresh) {
            if (gameTeam == null || gameTeam.getGameId().isEmpty()) {
                return;
            }
            getTeams(true);
        }
    }

    PlayerListAdapter.ItemClickListener playerClickListener = new PlayerListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            PlayerInfo info = playerList.get(pos);
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("player_id", info.getId());
            intent.putExtra("is_team", false);
            intent.putExtra("is_captain", true);
            startActivity(intent);
        }
    }; //diff

    ShirtsTeamAdapter.ItemClickListener teamClickListener = new ShirtsTeamAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedTeamId = teamList.get(pos).getId();
            teamAdapter.setSelectedId(selectedTeamId);
            selectedShirtId = "";
            shirtList.clear();
            shirtList.addAll(teamList.get(pos).getShirts());
            shirtAdapter.setSelectedId(selectedShirtId);
        }
    }; //same

    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            if (selectedTab == 0 && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                    selectedShirtId = shirt.getId();
                    shirtAdapter.setSelectedId(selectedShirtId);
                    updateTeamShirtAPI(gameTeam.getTeamAId(), "", selectedShirtId, "", "", "");
                    gameTeam.setTeamAShirt(shirt.getPhotoUrl());
                    saveData("", "team_a_shirt", "", "", "", "", shirt.getPhotoUrl(), "", "", "");
                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                        if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView vu) {
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
            else if (selectedTab == 1 && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                    selectedShirtId = shirt.getId();
                    shirtAdapter.setSelectedId(selectedShirtId);
                    updateTeamShirtAPI("", gameTeam.getTeamBId(), "", selectedShirtId, "", "");
                    gameTeam.setTeamBShirt(shirt.getPhotoUrl());
                    saveData("", "team_b_shirt", "", "", "", "", "", "", shirt.getPhotoUrl(), "");
                    for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                        if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView vu) {
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
                Functions.showToast(getContext(), getString(R.string.only_captain_can_change_jersey), FancyToast.ERROR, Toast.LENGTH_SHORT);
            }
        }
    }; //Diff

    ShirtListAdapter.ItemClickListener gkClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = gkShirtList.get(pos);
            if (selectedTab == 0 && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                    selectedGkShirtId = shirt.getId();
                    gkAdapter.setSelectedId(selectedGkShirtId);
                    updateTeamShirtAPI(gameTeam.getTeamAId(), "", "", "", selectedGkShirtId, "");
                    gameTeam.setTeamAgkShirt(shirt.getPhotoUrl());
                    saveData("", "team_a_gk_shirt", "", "", "", "", "", shirt.getPhotoUrl(), "", "");
                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                        if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView vu) {
                            if (vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
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
            else if (selectedTab == 1 && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                if (binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
                    selectedGkShirtId = shirt.getId();
                    gkAdapter.setSelectedId(selectedGkShirtId);
                    updateTeamShirtAPI("", gameTeam.getTeamBId(), "", "", "", selectedGkShirtId);
                    gameTeam.setTeamBgkShirt(shirt.getPhotoUrl());
                    saveData("", "team_b_gk_shirt", "", "", "", "", "", "", "", shirt.getPhotoUrl());
                    for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                        if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView vu) {
                            if (vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
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
                Functions.showToast(getContext(), getString(R.string.only_captain_can_change_jersey), FancyToast.ERROR, Toast.LENGTH_SHORT);
            }
        }
    };

    private void tabSelected(View v) {
        if (v == binding.tabTeamA) {
            selectedTab = 0;
            binding.tabBgTeamA.setVisibility(View.VISIBLE);
            binding.tabBgTeamB.setVisibility(View.INVISIBLE);
            binding.teamACircle.setStrokeColor(getResources().getColor(R.color.yellowColor));
            binding.teamBCircle.setStrokeColor(Color.WHITE);
            binding.vuTeamA.setVisibility(View.VISIBLE);
            binding.vuTeamB.setVisibility(View.INVISIBLE);
        }
        else {
            selectedTab = 1;
            binding.tabBgTeamA.setVisibility(View.INVISIBLE);
            binding.tabBgTeamB.setVisibility(View.VISIBLE);
            binding.teamACircle.setStrokeColor(Color.WHITE);
            binding.teamBCircle.setStrokeColor(getResources().getColor(R.color.yellowColor));
            binding.vuTeamA.setVisibility(View.INVISIBLE);
            binding.vuTeamB.setVisibility(View.VISIBLE);
        }
    }

    private void populateCountryData(Country country) {
        if (country == null && countryList.size() > 0) {
            country = countryList.get(0);
        }
        if (country != null) {
            binding.tvCountry.setText(country.getShortName());
            Glide.with(getApplicationContext()).load(country.getFlag()).into(binding.flagImgVu);
            teamList.clear();
            shirtList.clear();
            teamList.addAll(country.getTeams());
            if (selectedTeamId.equalsIgnoreCase("") && teamList.size() > 0) {
                shirtList.clear();
                selectedTeamId = teamList.get(0).getId();
                shirtList.addAll(teamList.get(0).getShirts());
            }
            else {
                for (int i = 0; i < teamList.size(); i++) {
                    if (teamList.get(i).getId().equalsIgnoreCase(selectedTeamId)) {
                        shirtList.clear();
                        shirtList.addAll(teamList.get(i).getShirts());
                        break;
                    }
                }
            }
            teamAdapter.setSelectedId(selectedTeamId);
            shirtAdapter.setSelectedId(selectedShirtId);
        }
    }

    private void setTabName() {
        if (teamACaptainName.equalsIgnoreCase("")) {
            binding.tvTeamA.setText(gameTeam.getTeamAName());
        }
        else {
            binding.tvTeamA.setText(String.format("%s(%s)", gameTeam.getTeamAName(), teamACaptainName));
        }
        if (teamBCaptainName.equalsIgnoreCase("")) {
            binding.tvTeamB.setText(gameTeam.getTeamBName());
        }
        else {
            binding.tvTeamB.setText(String.format("%s(%s)", gameTeam.getTeamBName(), teamBCaptainName));
        }
    }

    private void populateTeamData() {
        binding.tvTitle.setText(gameTeam.getGroupName());
        setTabName();

        if (gameTeam.getTeamAPlayers().size() > 0) {
            binding.tvTeamACount.setText(String.valueOf(gameTeam.getTeamAPlayers().size()));
        }
        else {
            binding.tvTeamACount.setText("0");
        }
        if (gameTeam.getTeamBPlayers().size() > 0) {
            binding.tvTeamBCount.setText(String.valueOf(gameTeam.getTeamBPlayers().size()));
        }
        else {
            binding.tvTeamBCount.setText("0");
        }

        checkCaptainAvailable();

        for (PlayerInfo info : gameTeam.getTeamAPlayers()) {
            replaceViewTeamA(new DragData(info, -1), 0, 0, false);
        }

        for (PlayerInfo info : gameTeam.getTeamBPlayers()) {
            replaceViewTeamB(new DragData(info, -1), 0, 0, false);
        }
    }

    private void checkCaptainAvailable() {
        teamACaptainAvailable = false;
        teamACaptainId = "";
        teamACaptainName = "";
        teamBCaptainAvailable = false;
        teamBCaptainId = "";
        teamBCaptainName = "";
        for (PlayerInfo info : gameTeam.getTeamAPlayers()) {
            if (info.getIsCaptain().equalsIgnoreCase("1")) {
                teamACaptainAvailable = true;
                teamACaptainId = info.getId();
                teamACaptainName = info.getNickName();
                break;
            }
        }
        for (PlayerInfo info : gameTeam.getTeamBPlayers()) {
            if (info.getIsCaptain().equalsIgnoreCase("1")) {
                teamBCaptainAvailable = true;
                teamBCaptainId = info.getId();
                teamBCaptainName = info.getNickName();
                break;
            }
        }
        if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
                if (databaseHandle == null) {
                    observeChange();
                }
                binding.btnSpeak.setOnClickListener(this);
                getPlayers(true);
                binding.btmVu.setVisibility(View.VISIBLE);
                getTeamData(true);
            }
        }
        else {
            binding.btnSpeak.setOnClickListener(null);
            try {
                ExpressManager.getInstance().enableMic(false);
                ExpressManager.getInstance().leaveRoom();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            binding.btnSpeak.setImageResource(R.drawable.mic_deactivel);
            binding.btmVu.setVisibility(View.INVISIBLE);
            binding.playersRecyclerVu.setVisibility(View.INVISIBLE);
            binding.tvTurn.setVisibility(View.GONE);
            if (databaseReference != null && databaseHandle != null) {
                databaseReference.child("lineup").child(gameTeam.getGameId()).removeEventListener(databaseHandle);
            }
        }

        setTabName();
    }

    private void replaceViewTeamA(DragData state, int x, int y, boolean isAdd) {
        PlayerInfo info = state.getItem();
        PreviewFieldView fieldViewA = new PreviewFieldView(getContext());
        populateDataInTeamAVu(fieldViewA, info, teamAVuWidth, teamAVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue *  teamAVuWidth; //getScreenWidth();
            float actualYValue = yValue *  teamAVuHeight; //getScreenHeight();
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
            float relX = (float) params.leftMargin / (float) teamAVuWidth; // getScreenWidth();
            float relY = (float) (params.topMargin) / (float) teamAVuHeight; //getScreenHeight();
            info.setxCoordinate(String.valueOf(relX));
            info.setyCoordinate(String.valueOf(relY));
            saveCoordinateAPI(true, gameTeam.getTeamAId(), info.getId(), relX, relY, "0", isAdd);
        }
    }

    private void replaceViewTeamB(DragData state, int x, int y, boolean isAdd) {
        PlayerInfo info = state.getItem();
        PreviewFieldView fieldView = new PreviewFieldView(getContext());
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
            float relX = (float) params.leftMargin / (float) teamBVuWidth;  //getScreenWidth();
            float relY = (float) (params.topMargin) / (float)  teamBVuHeight; //getScreenHeight();
            info.setxCoordinate(String.valueOf(relX));
            info.setyCoordinate(String.valueOf(relY));
            saveCoordinateAPI(true, gameTeam.getTeamBId(), info.getId(), relX, relY, "0", isAdd);
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
            if (teamACaptainAvailable && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                viewA.setParentViewSize(viewWidth, viewHeight);
                viewA.setPreviewFieldItemCallback(new PreviewFieldView.PreviewFieldItemCallback() {
                    @Override
                    public void itemClicked(PlayerInfo playerInfo) {
                        if (playerList.size() == 0 || captainATurn) {
                            showOptionDialog(playerInfo, true, 0, viewA);
                        }
                    }
                });
            }
            viewA.setPreviewFieldACallback(previewFieldCallback);
        }
        viewA.setPlayerInfo(playerInfo, gameTeam.getTeamAShirt(), gameTeam.getTeamAgkShirt());
    }

    private void populateDataInTeamBVu(PreviewFieldView viewA, PlayerInfo playerInfo, int viewWidth, int viewHeight) {
        if (gameTeam.getIsGameOn().equalsIgnoreCase("1")) {
            if (teamBCaptainAvailable && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                viewA.setParentViewSize(viewWidth, viewHeight);
                viewA.setPreviewFieldItemCallback(new PreviewFieldView.PreviewFieldItemCallback() {
                    @Override
                    public void itemClicked(PlayerInfo playerInfo) {
                        if (playerList.size() == 0 || captainBTurn) {
                            showOptionDialog(playerInfo, true, 0, viewA);
                        }
                    }
                });
            }
            viewA.setPreviewFieldACallback(previewFieldCallback);
        }
        viewA.setPlayerInfo(playerInfo, gameTeam.getTeamBShirt(), gameTeam.getTeamBgkShirt());
    }

    private void showOptionDialog(PlayerInfo info, boolean isTeam, int pos, PreviewFieldView view) {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FriendOptionsDialogFragment");
//        if (fragment != null) {
//            fragmentTransaction.remove(fragment);
//        }
//        fragmentTransaction.addToBackStack(null);
//        FriendOptionsDialogFragment dialogFragment = new FriendOptionsDialogFragment(isTeam, info,"0");
//        boolean isShowRemovePlayer = true;
//        boolean isShowSwap = false;
//        int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
//        if (isTeam && binding.vuTeamA.getChildCount() == limit && binding.vuTeamB.getChildCount() == limit) {
//            isShowRemovePlayer = false;
//            isShowSwap = true;
//        }
//        else if (isTeam && info.getIsCaptain().equalsIgnoreCase("1")) {
//            isShowRemovePlayer = false;
//        }
//        dialogFragment.setData(gameTeam.getTeamAName(), gameTeam.getTeamBName(), teamACaptainAvailable, teamBCaptainAvailable, isShowRemovePlayer, false, isShowSwap);
//        dialogFragment.setDialogCallback(new FriendOptionsDialogFragment.FriendOptionsDialogFragmentCallback() {
//            @Override
//            public void makeCaptain(DialogFragment df) {
//                df.dismiss();
//            }
//
//            @Override
//            public void removeCaptain(DialogFragment df) {
//                df.dismiss();
//                Functions.showToast(getContext(), getString(R.string.only_game_creator_can_remove_you), FancyToast.ERROR);
//            }
//
//            @Override
//            public void substitute(DialogFragment df) {
//                df.dismiss();
//            }
//
//            @Override
//            public void profile(DialogFragment df) {
//                df.dismiss();
//                Intent intent = new Intent(getContext(), ProfileActivity.class);
//                intent.putExtra("player_id", info.getId());
//                intent.putExtra("edit", false);
//                startActivity(intent);
//            }
//
//            @Override
//            public void manualEdit(DialogFragment df) {
//                df.dismiss();
//                Intent intent = new Intent(getContext(), ProfileActivity.class);
//                intent.putExtra("player_id", info.getId());
//                intent.putExtra("edit", false);
//                startActivity(intent);
//            }
//
//            @Override
//            public void remove(DialogFragment df) {
//                df.dismiss();
//                playSoundFromAssets("kickout.mpeg");
//                if (selectedTab == 0) {
//                    removePlayerFromTeamAPI(true, info.getId(), gameTeam.getTeamAId(), view);
//                }
//                else {
//                    removePlayerFromTeamAPI(true, info.getId(), gameTeam.getTeamBId(), view);
//                }
//            }
//
//            @Override
//            public void status(DialogFragment df) {
//                df.dismiss();
//                showStatusDialog(info);
//            }
//
//            @Override
//            public void swapPlayer(DialogFragment df) {
//                df.dismiss();
//                if (selectedTab == 0) {
//                    if (teamACaptainAvailable && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                      //  showSwapDialog(info, gameTeam.getTeamAId());
//                    }
//                }
//                else {
//                    if (teamBCaptainAvailable && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                       // showSwapDialog(info, gameTeam.getTeamBId());
//                    }
//                }
//            }
//        });
//        dialogFragment.show(fragmentTransaction, "FriendOptionsDialogFragment");
    }

//    private void showSwapDialog(PlayerInfo info, String teamId) {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("SwapPlayerDialogFragment");
//        if (fragment != null) {
//            fragmentTransaction.remove(fragment);
//        }
//        fragmentTransaction.addToBackStack(null);
//        SwapPlayerDialogFragment dialogFragment = new SwapPlayerDialogFragment(gameTeam, binding.fieldBgImgVu.getDrawable(), binding.fieldImgVu.getDrawable(), info.getId(), teamId);
//        dialogFragment.setDialogCallback(new SwapPlayerDialogFragment.SwapPlayerDialogFragmentCallback() {
//            @Override
//            public void swapDone(DialogFragment df) {
//                df.dismiss();
//                saveData(info.getId(), "swap_player", "", "", "", "", "", "", "", "");
//            }
//        });
//        dialogFragment.show(fragmentTransaction, "SwapPlayerDialogFragment");
//    }

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
                getTeams(true);
            }
        });
        dialogFragment.show(fragmentTransaction, "PlayerStatusDialogFragment");
    }

    PreviewFieldView.PreviewFieldViewCallback previewFieldCallback = new PreviewFieldView.PreviewFieldViewCallback() {
        @Override
        public void didStartDrag(PreviewFieldView view, PlayerInfo playerInfo, float newX, float newY) {

        }

        @Override
        public void didEndDrag(PreviewFieldView view, PlayerInfo playerInfo, float newX, float newY) {
            System.out.println(newX);
            System.out.println(newY);
            float relX = newX / (float) teamAVuWidth; //getScreenWidth();
            float relY = newY / (float) teamAVuHeight; //getScreenHeight();
            boolean isGK = false;
            if (selectedTab == 0) {
                if (newY + view.getHeight() > teamAVuHeight - 50) {
                    int w = teamAVuWidth / 4;
                    // goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamAVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    // check gk exist already && replace position
                    PreviewFieldView existingGk = checkTeamAGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamA.removeView(existingGk);
                        PreviewFieldView fieldViewA = new PreviewFieldView(getContext());
                        populateDataInTeamAVu(fieldViewA, existingGk.getPlayerInfo(), teamAVuWidth, teamAVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue * teamAVuWidth; //getScreenWidth();
                        float actualYValue = yValue * teamAVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamA.addView(fieldViewA, params);
                        saveCoordinateAPI(false, gameTeam.getTeamAId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", false);
                    }
                    //////
                    saveCoordinateAPI(false, gameTeam.getTeamAId(), playerInfo.getId(), relX, relY, "1", false);
                    view.setImage(gameTeam.getTeamAgkShirt());
                    view.getPlayerInfo().setIsGoalkeeper("1");
                }
                else {
                    saveCoordinateAPI(false, gameTeam.getTeamAId(), playerInfo.getId(), relX, relY, "0", false);
                    view.getPlayerInfo().setIsGoalkeeper("0");
                    view.setImage(gameTeam.getTeamAShirt());
                }
                view.getPlayerInfo().setxCoordinate(String.valueOf(relX));
                view.getPlayerInfo().setyCoordinate(String.valueOf(relY));
            }
            else {
                if (newY + view.getHeight() > teamBVuHeight - 50) {
                    int w = teamBVuWidth / 4;
                    // goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamBVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    // check gk exist already && replace position
                    PreviewFieldView existingGk = checkTeamBGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamB.removeView(existingGk);
                        PreviewFieldView fieldView = new PreviewFieldView(getContext());
                        populateDataInTeamBVu(fieldView, existingGk.getPlayerInfo(), teamBVuWidth, teamBVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue * teamBVuWidth;   //getScreenWidth();
                        float actualYValue = yValue *  teamBVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamB.addView(fieldView, params);
                        saveCoordinateAPI(false, gameTeam.getTeamBId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", false);
                    }
                    //////
                    saveCoordinateAPI(false, gameTeam.getTeamBId(), playerInfo.getId(), relX, relY, "1", false);
                    view.setImage(gameTeam.getTeamBgkShirt());
                    playerInfo.setIsGoalkeeper("1");
                }
                else {
                    saveCoordinateAPI(false, gameTeam.getTeamBId(), playerInfo.getId(), relX, relY, "0", false);
                    view.setImage(gameTeam.getTeamBShirt());
                    playerInfo.setIsGoalkeeper("0");
                }
                playerInfo.setxCoordinate(String.valueOf(relX));
                playerInfo.setyCoordinate(String.valueOf(relY));

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
                        if (teamACaptainAvailable) {
                            if (captainATurn && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                                if (binding.vuTeamA.getChildCount() < limit) {
                                    if (v == binding.vuTeamA) {
                                        replaceViewTeamA(state, (int) event.getX(), (int) event.getY(), true);
                                    } else {
                                        replaceViewTeamA(state, 0, 0, true);
                                    }
                                    gameTeam.getTeamAPlayers().add(state.getItem());
                                    playerList.remove(state.getPos());
                                    playerAdapter.notifyDataSetChanged();
                                    binding.tvTeamACount.setText(String.valueOf(gameTeam.getTeamAPlayers().size()));
                                }
                                else {
                                    saveData("", "add", teamACaptainId, "", "", "", "", "", "", "");
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
                            gameTeam.getTeamAPlayers().add(state.getItem());
                            playerList.remove(state.getPos());
                            playerAdapter.notifyDataSetChanged();
                            binding.tvTeamACount.setText(String.valueOf(gameTeam.getTeamAPlayers().size()));
                        }
                    }
                    else if (v == binding.tabTeamB || v == binding.vuTeamB) {
                        if (teamBCaptainAvailable) {
                            if (captainBTurn && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                int limit = Integer.parseInt(gameTeam.getGamePlayers())/2;
                                if (binding.vuTeamB.getChildCount() < limit) {
                                    if (v == binding.vuTeamB) {
                                        replaceViewTeamB(state, (int) event.getX(), (int) event.getY(), true);
                                    } else {
                                        replaceViewTeamB(state, 0, 0, true);
                                    }
                                    gameTeam.getTeamBPlayers().add(state.getItem());
                                    playerList.remove(state.getPos());
                                    playerAdapter.notifyDataSetChanged();
                                    binding.tvTeamBCount.setText(String.valueOf(gameTeam.getTeamBPlayers().size()));
                                }
                                else {
                                    saveData("", "add", teamBCaptainId, "", "", "", "", "", "", "");
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
                            gameTeam.getTeamBPlayers().add(state.getItem());
                            playerList.remove(state.getPos());
                            playerAdapter.notifyDataSetChanged();
                            binding.tvTeamBCount.setText(String.valueOf(gameTeam.getTeamBPlayers().size()));
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private PreviewFieldView checkTeamAGkExist() {
        PreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView vu) {
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
            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView vu) {
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    vu.getPlayerInfo().getIsCaptain().equalsIgnoreCase("1");
                    break;
                }
            }
        }
        return view;
    }

    private int checkPlayerExistInTeamA(String id) {
        int result = -1;
        for (int i = 0; i < gameTeam.getTeamAPlayers().size(); i++) {
            if (gameTeam.getTeamAPlayers().get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private int checkPlayerExistInTeamB(String id) {
        int result = -1;
        for (int i = 0; i < gameTeam.getTeamBPlayers().size(); i++) {
            if (gameTeam.getTeamBPlayers().get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void getTeams(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.formationDetails(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), gameId, "", "", "", "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            gameTeam = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), GameTeam.class);
                            binding.vuTeamA.removeAllViews();
                            binding.vuTeamB.removeAllViews();
                            populateTeamData();
                            Glide.with(getApplicationContext()).load(object.getJSONObject(Constants.kData).getString("bg_image")).into(binding.fieldBgImgVu);
                            Glide.with(getApplicationContext()).load(object.getJSONObject(Constants.kData).getString("field_image")).into(binding.fieldImgVu);
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            finish();
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

    private void getPlayers(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.gamePlayers(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), gameTeam.getGameId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            playerList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < array.length(); i++) {
                                PlayerInfo info = gson.fromJson(array.get(i).toString(), PlayerInfo.class);
                                info.setInGame("1");
                                playerList.add(info);
                            }
                            // remove player who's in team
                            if (gameTeam != null) {
                                Iterator<PlayerInfo> iterator = playerList.iterator();
                                while (iterator.hasNext()) {
                                    PlayerInfo info = iterator.next();
                                    if (info != null) {
                                        int index = checkPlayerExistInTeamA(info.getId());
                                        if (index != -1) {
                                            iterator.remove();
                                        }
                                        index = checkPlayerExistInTeamB(info.getId());
                                        if (index != -1) {
                                            iterator.remove();
                                        }
                                    }
                                }
                            }
                            binding.playersRecyclerVu.setVisibility(View.VISIBLE);
                            playerAdapter.setIsGameOn(gameTeam.getIsGameOn(), true);
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

    private void saveCoordinateAPI(boolean isLoader, String teamId, String friendId, float xCoordinate, float yCoordinate, String isGoalKeeper, boolean isAdd) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.saveCoordinate(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), teamId, gameTeam.getGameId(), friendId, xCoordinate, yCoordinate, isGoalKeeper);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isAdd && (teamACaptainAvailable || teamBCaptainAvailable)) {
                                if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    saveData(friendId, "add", teamACaptainId, teamId, String.valueOf(xCoordinate), String.valueOf(yCoordinate), "", "", "", "");
                                }
                                else if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    saveData(friendId, "add", teamBCaptainId, teamId, String.valueOf(xCoordinate), String.valueOf(yCoordinate), "", "", "", "");
                                }
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

    private void removePlayerFromTeamAPI(boolean isLoader, String pId, String teamId, PreviewFieldView view) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeFromTeam(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), teamId, pId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            PlayerInfo info = view.getPlayerInfo();
                            info.setIsGoalkeeper("0");
                            info.setInGame("1");
                            info.setxCoordinate("");
                            info.setyCoordinate("");
                            playerList.add(0, info);
                            if (teamId.equalsIgnoreCase(gameTeam.getTeamAId())) {
                                binding.vuTeamA.removeView(view);
                                for (int i = 0; i < gameTeam.getTeamAPlayers().size(); i++) {
                                    if (gameTeam.getTeamAPlayers().get(i).getId().equalsIgnoreCase(info.getId())) {
                                        gameTeam.getTeamAPlayers().remove(i);
                                        break;
                                    }
                                }
                                binding.tvTeamACount.setText(String.valueOf(gameTeam.getTeamAPlayers().size()));
                            }
                            else {
                                binding.vuTeamB.removeView(view);
                                for (int i = 0; i < gameTeam.getTeamBPlayers().size(); i++) {
                                    if (gameTeam.getTeamBPlayers().get(i).getId().equalsIgnoreCase(info.getId())) {
                                        gameTeam.getTeamBPlayers().remove(i);
                                        break;
                                    }
                                }
                                binding.tvTeamBCount.setText(String.valueOf(gameTeam.getTeamBPlayers().size()));
                            }
                            if (teamACaptainAvailable || teamBCaptainAvailable) {
                                if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    saveData(pId, "remove", teamACaptainId, teamId, "", "", "", "", "", "");
                                }
                                else if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    saveData(pId, "remove", teamBCaptainId, teamId, "", "", "", "", "", "");
                                }
                            }
                            playerAdapter.notifyDataSetChanged();
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

    private void getTeamData(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.teamsData(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),"android");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            selectedCountryId = data.getString("selected_country_id");
                            selectedTeamId = data.getString("selected_team_id");
                            selectedShirtId = data.getString("selected_shirt_id");
                            JSONArray country = data.getJSONArray("countries");
                            JSONArray gk_shirts = data.getJSONArray("gk_shirts");
                            countryList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < country.length(); i++) {
                                countryList.add(gson.fromJson(country.get(i).toString(), Country.class));
                            }

                            gkShirtList.clear();
                            for (int i = 0; i < gk_shirts.length(); i++) {
                                gkShirtList.add(gson.fromJson(gk_shirts.get(i).toString(), Shirt.class));
                            }
                            gkAdapter.notifyDataSetChanged();

                            Country countryData = null;
                            for (int i = 0; i < countryList.size(); i++) {
                                if (countryList.get(i).getId().equalsIgnoreCase(selectedCountryId)) {
                                    countryData = countryList.get(i);
                                    break;
                                }
                            }
                            populateCountryData(countryData);

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

    private void updateTeamShirtAPI(String teamAId, String teamBId, String teamAShirt, String teamBShirt, String teamAgkShirt, String teamBgkShirt) {
        KProgressHUD hud = Functions.showLoader(getContext());
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateTeamShirt(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), teamAId, teamAShirt, teamAgkShirt, teamBId, teamBShirt, teamBgkShirt);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

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

    private void saveData(String playerId, String type, String captainId, String teamId, String xCoordinate, String yCoordinate, String teamAShirt, String teamAGkShirt, String teamBShirt, String teamBGkShirt) {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("turn", "captain_b");
        map.put("type", type);
        map.put("player_id", playerId);
        map.put("captain_id", captainId);
        map.put("team_id", teamId);
        map.put("x_coord", xCoordinate);
        map.put("y_coord", yCoordinate);
        map.put("team_a_shirt", teamAShirt);
        map.put("team_a_gk_shirt", teamAGkShirt);
        map.put("team_b_shirt", teamBShirt);
        map.put("team_b_gk_shirt", teamBGkShirt);
        map.put("game_id", gameTeam.getGameId());
        databaseReference.child("lineup").child(gameTeam.getGameId()).setValue(map);
        if (playerList.size() == 0) {
            binding.tvTurn.setVisibility(View.GONE);
        }
        else {
            if (!captainId.isEmpty() && !type.equalsIgnoreCase("remove")) {
                if (captainId.equalsIgnoreCase(teamACaptainId)) {
                    captainBTurn = true;
                    captainATurn = false;
                } else {
                    captainBTurn = false;
                    captainATurn = true;
                }
            }
        }
        if (!type.equalsIgnoreCase("team_a_shirt") &&
                !type.equalsIgnoreCase("team_b_shirt") &&
                !type.equalsIgnoreCase("team_a_gk_shirt") &&
                !type.equalsIgnoreCase("team_b_gk_shirt") &&
                !type.equalsIgnoreCase("swap_player")) {
            binding.tvTurn.setVisibility(View.VISIBLE);
            if (captainATurn) {
                binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamAName()));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabSelected(binding.tabTeamA);
                    }
                }, 1000);
            }
            if (captainBTurn) {
                binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamBName()));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabSelected(binding.tabTeamB);
                    }
                }, 1000);
            }
        }
    }

    private void observeChange() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        databaseHandle = databaseReference.child("lineup").child(gameTeam.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.tvTurn.setVisibility(View.GONE);
                    return;
                }

                try {
                    DataModel object = snapshot.getValue(DataModel.class);
                    if (object!=null) {
                        String gameId = object.getGame_id();
                        if (!gameId.equalsIgnoreCase(gameTeam.getGameId())) {
                            binding.tvTurn.setVisibility(View.GONE);
                            return;
                        }
                        String type = object.getType();
                        String turn = object.getTurn();
                        String captainId = object.getCaptain_id();
                        String teamId = object.getTeam_id();
                        if (!captainId.isEmpty() && captainId.equalsIgnoreCase(teamACaptainId)) {
                            if (type.equalsIgnoreCase("remove")) {
                                if (teamACaptainAvailable || teamBCaptainAvailable) {
                                    if (playerList.size() > 0) {
                                        captainATurn = true;
                                        captainBTurn = false;
                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                tabSelected(binding.tabTeamA);
                                            }
                                        }, 1000);
                                    }
                                    binding.tvTurn.setVisibility(View.VISIBLE);
                                    binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamAName()));
                                    if (playerList.size() == 0) {
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                            }
                            else {
                                if (teamACaptainAvailable || teamBCaptainAvailable) {
                                    if (playerList.size() > 0) {
                                        captainATurn = false;
                                        captainBTurn = true;
                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                tabSelected(binding.tabTeamB);
                                            }
                                        }, 1000);
                                        if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                            playSoundFromAssets("beep.mp3");
                                        }
                                    }
                                    binding.tvTurn.setVisibility(View.VISIBLE);
                                    binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamBName()));
                                    if (playerList.size() == 0) {
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                            }
                            if (type.equalsIgnoreCase("substitute") || type.equalsIgnoreCase("captain_created") || type.equalsIgnoreCase("captain_removed")) {
                                if (type.equalsIgnoreCase("captain_removed")) {
                                    teamACaptainAvailable = false;
                                    teamBCaptainAvailable = false;
                                    teamACaptainId = "";
                                    teamBCaptainId = "";
                                    databaseReference.removeValue();
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                                getTeams(true);
                            }
                            else if (type.equalsIgnoreCase("add")) {
                                String playerId = object.getPlayer_id();
                                // captain can just add player in team B, so here need to add this player in team B for sync
                                for (int i = 0; i < playerList.size(); i++) {
                                    PlayerInfo info = playerList.get(i);
                                    if (info.getId().equalsIgnoreCase(playerId)) {
                                        info.setxCoordinate(object.getX_coord());
                                        info.setyCoordinate(object.getY_coord());
                                        replaceViewTeamA(new DragData(info, -1), 0, 0, true);
                                        playerList.remove(i);
                                        playerAdapter.notifyDataSetChanged();
                                        if (playerList.size() == 0) {
                                            binding.tvTurn.setVisibility(View.GONE);
                                        }
                                        binding.tvTeamACount.setText(String.valueOf(binding.vuTeamA.getChildCount()));
                                        break;
                                    }
                                }
                            }
                            else if (type.equalsIgnoreCase("remove")) {
                                String playerId = object.getPlayer_id();
                                // captain just remove player from team B, so here need to remove this player from team B for sync
                                for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                    PreviewFieldView fieldView = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                                    PlayerInfo info = fieldView.getPlayerInfo();
                                    if (info.getId().equalsIgnoreCase(playerId)) {
                                        info.setIsGoalkeeper("0");
                                        info.setInGame("1");
                                        playerList.add(0, info);
                                        playerAdapter.notifyDataSetChanged();
                                        binding.vuTeamA.removeView(fieldView);
                                        binding.tvTeamACount.setText(String.valueOf(binding.vuTeamA.getChildCount()));
                                        break;
                                    }
                                }
                            }
                        }
                        else if (!captainId.isEmpty() && captainId.equalsIgnoreCase(teamBCaptainId)) {
                            if (type.equalsIgnoreCase("remove")) {
                                if (teamACaptainAvailable || teamBCaptainAvailable) {
                                    if (playerList.size() > 0) {
                                        captainATurn = false;
                                        captainBTurn = true;
                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                tabSelected(binding.tabTeamB);
                                            }
                                        }, 1000);
                                    }
                                    binding.tvTurn.setVisibility(View.VISIBLE);
                                    binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamBName()));
                                    if (playerList.size() == 0) {
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                            }
                            else {
                                if (teamACaptainAvailable || teamBCaptainAvailable) {
                                    if (playerList.size() > 0) {
                                        captainATurn = true;
                                        captainBTurn = false;
                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                tabSelected(binding.tabTeamA);
                                            }
                                        }, 1000);
                                        if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                            playSoundFromAssets("beep.mp3");
                                        }
                                    }
                                    binding.tvTurn.setVisibility(View.VISIBLE);
                                    binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamAName()));
                                    if (playerList.size() == 0) {
                                        binding.tvTurn.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                            }
                            if (type.equalsIgnoreCase("substitute") || type.equalsIgnoreCase("captain_created") || type.equalsIgnoreCase("captain_removed")) {
                                if (type.equalsIgnoreCase("captain_removed")) {
                                    teamACaptainAvailable = false;
                                    teamBCaptainAvailable = false;
                                    teamACaptainId = "";
                                    teamBCaptainId = "";
                                    databaseReference.removeValue();
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                                getTeams(true);
                            }
                            else if (type.equalsIgnoreCase("add")) {
                                String playerId = object.getPlayer_id();
                                // captain can just add player in team B, so here need to add this player in team B for sync
                                for (int i = 0; i < playerList.size(); i++) {
                                    PlayerInfo info = playerList.get(i);
                                    if (info.getId().equalsIgnoreCase(playerId)) {
                                        info.setxCoordinate(object.getX_coord());
                                        info.setyCoordinate(object.getY_coord());
                                        replaceViewTeamB(new DragData(info, -1), 0, 0, true);
                                        playerList.remove(i);
                                        playerAdapter.notifyDataSetChanged();
                                        if (playerList.size() == 0) {
                                            binding.tvTurn.setVisibility(View.GONE);
                                        }
                                        binding.tvTeamBCount.setText(String.valueOf(binding.vuTeamB.getChildCount()));
                                        break;
                                    }
                                }
                            }
                            else if (type.equalsIgnoreCase("remove")) {
                                String playerId = object.getPlayer_id();
                                // captain just remove player from team B, so here need to remove this player from team B for sync
                                for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                    PreviewFieldView fieldView = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                                    PlayerInfo info = fieldView.getPlayerInfo();
                                    if (info.getId().equalsIgnoreCase(playerId)) {
                                        info.setIsGoalkeeper("0");
                                        info.setInGame("1");
                                        playerList.add(0, info);
                                        playerAdapter.notifyDataSetChanged();
                                        binding.vuTeamB.removeView(fieldView);
                                        binding.tvTeamBCount.setText(String.valueOf(binding.vuTeamB.getChildCount()));
                                        break;
                                    }
                                }
                            }
                        }
                        else if (type.equalsIgnoreCase("captain_a_choose")) {
                            if (teamACaptainAvailable || teamBCaptainAvailable) {
                                captainATurn = true;
                                captainBTurn = false;
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tabSelected(binding.tabTeamA);
                                    }
                                }, 1000);
                                if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    playSoundFromAssets("beep.mp3");
                                }
                                binding.tvTurn.setVisibility(View.VISIBLE);
                                binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamAName()));
                            }
                        }
                        else if (type.equalsIgnoreCase("captain_b_choose")) {
                            if (teamACaptainAvailable || teamBCaptainAvailable) {
                                captainATurn = false;
                                captainBTurn = true;
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tabSelected(binding.tabTeamB);
                                    }
                                }, 1000);
                                if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    playSoundFromAssets("beep.mp3");
                                }
                                binding.tvTurn.setVisibility(View.VISIBLE);
                                binding.tvTurn.setText(getString(R.string.captain_turn_place, gameTeam.getTeamBName()));
                            }
                        }
                        else if (type.equalsIgnoreCase("team_a_shirt")) {
                            gameTeam.setTeamAShirt(object.getTeam_a_shirt());
                            for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView vu) {
                                    if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(object.getTeam_a_shirt());
                                    }
                                }
                            }
                        }
                        else if (type.equalsIgnoreCase("team_a_gk_shirt")) {
                            gameTeam.setTeamAgkShirt(object.getTeam_a_gk_shirt());
                            for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                                if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView vu) {
                                    if (vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(object.getTeam_a_gk_shirt());
                                        break;
                                    }
                                }
                            }
                        }
                        else if (type.equalsIgnoreCase("team_b_shirt")) {
                            gameTeam.setTeamBShirt(object.getTeam_b_shirt());
                            for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView vu) {
                                    if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(object.getTeam_b_shirt());
                                    }
                                }
                            }
                        }
                        else if (type.equalsIgnoreCase("team_b_gk_shirt")) {
                            gameTeam.setTeamBgkShirt(object.getTeam_b_gk_shirt());
                            for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                                if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView vu) {
                                    if (vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                        vu.setImage(object.getTeam_b_gk_shirt());
                                        break;
                                    }
                                }
                            }
                        }
                        else if (type.equalsIgnoreCase("swap_player")) {
                            getTeams(true);
                        }
                        else {
                            if (playerList.size() == 0) {
                                binding.tvTurn.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}