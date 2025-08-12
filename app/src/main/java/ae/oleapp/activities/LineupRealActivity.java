package ae.oleapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import android.widget.RelativeLayout;
import android.widget.Toast;


import com.baoyz.actionsheet.ActionSheet;
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
import ae.oleapp.adapters.ChairListAdapter;
import ae.oleapp.adapters.FieldImageListAdapter;
import ae.oleapp.adapters.LineupRealPlayerListAdapter;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.adapters.ShirtsTeamAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityLineupRealBinding;
import ae.oleapp.dialogs.SelectionListDialog;
import ae.oleapp.fragments.AdsSubscriptionPopupFragment;
import ae.oleapp.fragments.UnlockedJerseyPopupFragment;
import ae.oleapp.models.Chair;
import ae.oleapp.models.Country;
import ae.oleapp.models.FieldImage;
import ae.oleapp.models.LineupGlobalPlayers;
import ae.oleapp.models.LineupRealDragData;
import ae.oleapp.models.LineupRealGameTeam;
import ae.oleapp.models.LineupSelections;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.RewardedAdManager;
import ae.oleapp.models.SelectionList;
import ae.oleapp.models.SelectionModel;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.Team;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.LineupRealPreviewFieldView;

import ae.oleapp.util.PreviewFieldView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LineupRealActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLineupRealBinding binding;
    private ShirtsTeamAdapter teamAdapter;
    private ShirtListAdapter shirtAdapter;
    private ShirtListAdapter gkAdapter;
    private ChairListAdapter chairAdapter;
    private FieldImageListAdapter fieldAdapter;
    private LineupRealPlayerListAdapter lineupRealPlayerListAdapter;
    private final List<Country> countryList = new ArrayList<>();
    private final List<Team> teamList = new ArrayList<>();
    private final List<Shirt> shirtList = new ArrayList<>();
    private final List<Chair> chairList = new ArrayList<>();
    private final List<Shirt> gkShirtList = new ArrayList<>();
    private final List<FieldImage> fieldList = new ArrayList<>();
    private final List<LineupGlobalPlayers> friendList = new ArrayList<>();

    private String selectedCountryId = "", selectedTeamId = "", selectedShirtId = "", selectedChairId = "",
            selectedFieldId = "", selectedGkShirtId = "", subscribedShirtId="", subscribedShirt="";
    private LineupRealGameTeam lineupRealGameTeam;
    private LineupSelections lineupSelections;
    private int teamAVuWidth = 0;
    private int teamAVuHeight = 0;
    private float subVuH = 0, subVuW = 0;
    private Vibrator vibrator;
    private RewardedAdManager rewardedAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLineupRealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        applyEdgeToEdgee(binding.getRoot());
        getLineupSelections(true);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        rewardedAdManager = new RewardedAdManager(this);
        rewardedAdManager.loadRewardedAd();

        GridLayoutManager playerLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        binding.playersRecyclerVu.setLayoutManager(playerLayoutManager);
        lineupRealPlayerListAdapter = new LineupRealPlayerListAdapter(getContext(), friendList);
        lineupRealPlayerListAdapter.setItemClickListener(playerClickListener);
        binding.playersRecyclerVu.setAdapter(lineupRealPlayerListAdapter);

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
        LineupRealPreviewFieldView fieldView = new LineupRealPreviewFieldView(getContext());
        binding.vuTeamA.addView(fieldView);
        fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                subVuW = fieldView.getWidth();
                subVuH = fieldView.getHeight();
                binding.vuTeamA.removeView(fieldView);
            }
        });

        binding.vuTeamA.setOnDragListener(vuDragListener);
        binding.btnClose.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnGkShirts.setOnClickListener(this);
        binding.btnShirt.setOnClickListener(this);
        binding.countryVu.setOnClickListener(this);
        binding.btnChair.setOnClickListener(this);
        binding.btnField.setOnClickListener(this);
        binding.btnHideFace.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.btnChangeName.setOnClickListener(this);

    }

    ///// EDGE TO EDGE START ////
    protected void applyEdgeToEdgee(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Keep content inside safe area without shifting vertically unless needed
            return WindowInsetsCompat.CONSUMED;
        });
        setStatusBarAndNavBarColor();
    }

    private void setStatusBarAndNavBarColor() {
        Window window = getWindow();
        int green = ContextCompat.getColor(this, R.color.transparent);

        if (Build.VERSION.SDK_INT >= 34) { // Android 15+
            boolean isLandscape = getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE;
            ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), (view, windowInsets) -> {
                Insets statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
                Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());

                // Apply background color to the whole decor view
                view.setBackgroundColor(green);

                // Combine paddings for status + navigation bars
                int left = isLandscape ? navBarInsets.left : Math.max(statusBarInsets.left, navBarInsets.left);
                int top = 0;
                int right = isLandscape ? navBarInsets.right : Math.max(statusBarInsets.right, navBarInsets.right);
                int bottom = navBarInsets.bottom;

                view.setPadding(left, top, right, bottom);
                return windowInsets;
            });
        } else {
            window.setStatusBarColor(green);
            window.setNavigationBarColor(green);
        }
    }
    ///// EDGE TO EDGE END ////


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
        else if (view == binding.btnHideFace){
            if (lineupRealGameTeam !=null){
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
                            if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
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
                            if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                                vu.hideShowface(false);
                            }
                        }
                    }

            }else{
                Functions.showToast(getContext(),"To use this feature, Please start the game first.", FancyToast.ERROR);
            }

        }
        else if (view == binding.btnChangeName){
            if (lineupRealGameTeam !=null){
                binding.btnChair.setImageResource(R.drawable.chair_inactive);
                binding.btnShirt.setImageResource(R.drawable.shirt_inactive);
                binding.btnField.setImageResource(R.drawable.field_inactivel);
                binding.btnGkShirts.setImageResource(R.drawable.gk_shirt_inactivel);
                if (Functions.getPrefValue(getContext(), Constants.kNameHide).equalsIgnoreCase("false")){
                    SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                    editor.putString(Constants.kNameHide, "true");
                    editor.apply();
                    binding.btnChangeName.setImageResource(R.drawable.change_name_ar);
                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                        if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                            vu.hideShowName(true);
                        }
                    }
                }
                else{
                    SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                    editor.putString(Constants.kNameHide, "false");
                    editor.apply();
                    binding.btnChangeName.setImageResource(R.drawable.change_name_en);
                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                        if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                            vu.hideShowName(false);
                        }
                    }
                }

            }else{
                Functions.showToast(getContext(),"To use this feature, Please start the game first.", FancyToast.ERROR);
            }
        }
        else if (view == binding.btnChair) {
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
        else if (view == binding.btnShirt) {
            //getAllCountries(false);
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
        else if (view == binding.btnField) {
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
        else if (view == binding.btnGkShirts) {
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
                    saveAppSettings(true, selectedCountryId, "country", lineupRealPlayerListAdapter.getChairUrl(), "", "");

                }
            });
            dialog.show();
        }
        else if (view == binding.btnShare) {
            if (lineupRealGameTeam != null && !lineupRealGameTeam.getMatchId().isEmpty()) {
                Intent intent = new Intent(getContext(), LineupRealShareActivity.class);
                intent.putExtra("team_id", selectedTeamId);
                startActivity(intent);
            }
        }
        else if (view == binding.btnReset) {
            if (lineupRealGameTeam !=null && lineupRealGameTeam.getPlayers().size() > 0){
                resetGame(lineupRealGameTeam.getMatchId());
            }else{
                Functions.showToast(getContext(),"Game is Already Reset, Please add player", FancyToast.SUCCESS);
            }


        }
    }

    @Override
    protected void onResume() {
        if (rewardedAdManager.getStatus() && !subscribedShirtId.isEmpty()){
            unlockOneJersey(subscribedShirtId, subscribedShirt);
        }
        if (!selectedTeamId.isEmpty()){
            lineupGlobalTeamPlayers(false,false, selectedTeamId);
        }
        super.onResume();
    }

    private void unlockOneJersey(String shrtId, String shirt) {
        KProgressHUD hud = Functions.showLoader(getContext());
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.unlockOneJersey(Functions.getAppLang(getContext()), shrtId);
        call.enqueue(new Callback<>() {
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

    private void resetGame(String matchId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reset Game")
                .setMessage("Are you sure you want to reset this game?")
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGameAPI(true, matchId);
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

    LineupRealPlayerListAdapter.ItemClickListener playerClickListener = new LineupRealPlayerListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            LineupGlobalPlayers info = friendList.get(pos);

                if (info != null && info.getAddedBy() !=null && info.getAddedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    Intent intent = new Intent(getContext(), AddLineupGlobalPlayerActivity.class);
                    intent.putExtra("is_update",true);
                    intent.putExtra("country_id",selectedCountryId);
                    intent.putExtra("team_id", selectedTeamId);
                    intent.putExtra("player_id", info.getId());
                    intent.putExtra("player", new Gson().toJson(info));
                    intent.putExtra("shirts", new Gson().toJson(shirtList));
                    startActivity(intent);
                }else{
                    if (info == null){
                        Intent intent = new Intent(getContext(), AddLineupGlobalPlayerActivity.class);
                        intent.putExtra("is_update",false);
                        intent.putExtra("country_id",selectedCountryId);
                        intent.putExtra("team_id",selectedTeamId);
                        intent.putExtra("shirts", new Gson().toJson(shirtList));
                        startActivity(intent);
                    }

                }
        }
    };

    ShirtsTeamAdapter.ItemClickListener teamClickListener = new ShirtsTeamAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
                selectedTeamId = teamList.get(pos).getId();
                teamAdapter.setSelectedId(selectedTeamId);
                selectedShirtId = "";
                shirtList.clear();
                shirtList.addAll(teamList.get(pos).getShirts());
                shirtAdapter.setSelectedId(selectedShirtId);
                saveAppSettings(false, selectedTeamId, "team", lineupRealPlayerListAdapter.getChairUrl(), "","");
        }
    };

    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            if (shirt.getType().equalsIgnoreCase("paid")){
                showAdsSubscriptionPopup(shirt.getId(), shirt.getPhotoUrl());
            } else{
                        selectedShirtId = shirt.getId();
                        shirtAdapter.setSelectedId(selectedShirtId);
                            if (lineupRealGameTeam !=null){
                                lineupRealGameTeam.setTeamShirt(shirt.getPhotoUrl());
                            }
                        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                            if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                                if (vu.getPlayerInfo().getIsGoalkeeper() == null || !vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                                    vu.setImage(shirt.getPhotoUrl());
                                }
                            }
                        }

                        if (lineupRealGameTeam!=null){
                            if (!lineupRealGameTeam.getMatchId().isEmpty()){
                                saveRealLineupMatchShirts(false, lineupRealGameTeam.getMatchId(), selectedShirtId, "");
                            }
                        }



            }
        }
    };

    ShirtListAdapter.ItemClickListener gkClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = gkShirtList.get(pos);
            selectedGkShirtId = shirt.getId();
            gkAdapter.setSelectedId(selectedGkShirtId);
            if (lineupRealGameTeam != null){
                lineupRealGameTeam.setTeamGkShirt(shirt.getPhotoUrl());
            }
            for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                    if (vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                        vu.setImage(shirt.getPhotoUrl());
                        break;
                    }
                }
            }
            if (lineupRealGameTeam!=null){
                if (!lineupRealGameTeam.getMatchId().isEmpty()){
                    saveRealLineupMatchShirts(false, lineupRealGameTeam.getMatchId(), "", selectedGkShirtId);
                }
            }
                //            saveAppSettings(false, selectedGkShirtId, "goalkeeper", lineupRealPlayerListAdapter.getChairUrl(), "", "");

        }
    };

    ChairListAdapter.ItemClickListener chairClickListener = new ChairListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedChairId = chairList.get(pos).getId();
            chairAdapter.setSelectedId(selectedChairId);
            lineupRealPlayerListAdapter.setChairUrl(chairList.get(pos).getPhotoUrl());
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

    private void populateCountryData(Country country) {
        if (country == null && countryList.size() > 0) {
            country = countryList.get(0);
        }
        if (country != null) {
            binding.tvCountry.setText(country.getShortName());
            Glide.with(getApplicationContext()).load(country.getFlag()).into(binding.flagImgVu);
            shirtList.clear();
            if (selectedTeamId.equalsIgnoreCase("") && teamList.size() > 0) {
                selectedTeamId = teamList.get(0).getId();
                shirtList.addAll(teamList.get(0).getShirts());
                selectedShirtId = shirtList.get(0).getId();
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
            saveAppSettings(false, selectedTeamId, "team", lineupRealPlayerListAdapter.getChairUrl(), "","");

        }
    }

    private void populateTeamData() {
        Iterator<LineupGlobalPlayers> iterator = friendList.iterator();
        while (iterator.hasNext()) {
            LineupGlobalPlayers info = iterator.next();
            if (info != null) {
                int index = checkPlayerExistInTeamA(info.getId());
                if (index != -1) {
                    iterator.remove();
                }
            }
        }
        lineupRealPlayerListAdapter.notifyDataSetChanged();
        for (LineupGlobalPlayers info : lineupRealGameTeam.getPlayers()) {
            replaceViewTeamA(new LineupRealDragData(info, -1), 0, 0, false);
        }
    }

    private void replaceViewTeamA(LineupRealDragData state, int x, int y, boolean isAdd) {
        LineupGlobalPlayers info = state.getItem();
        LineupRealPreviewFieldView fieldViewA = new LineupRealPreviewFieldView(getContext());
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
            saveRealLineupPlayerPositions(false, lineupRealGameTeam.getMatchId(), info.getId(), relX, relY, "0", isAdd);
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

    private void populateDataInTeamAVu(LineupRealPreviewFieldView viewA, LineupGlobalPlayers playerInfo, int viewWidth, int viewHeight) {
                viewA.setParentViewSize(viewWidth, viewHeight);
                viewA.setPreviewFieldACallback(previewFieldCallback);
                viewA.setPlayerInfo(playerInfo, lineupRealGameTeam.getTeamShirt(), lineupRealGameTeam.getTeamGkShirt());
                if (viewA.isIstouchListerActive()){
                    viewA.setPreviewFieldItemCallback(new LineupRealPreviewFieldView.LineupRealPreviewFieldItemCallback() {
                        @Override
                        public void itemClicked(LineupGlobalPlayers playerInfo) {
                            executePlayerRemoveFunc(lineupRealGameTeam.getMatchId(), playerInfo.getId(), viewA);

                        }
                    });
                }
    }

    LineupRealPreviewFieldView.LineupRealPreviewFieldViewCallback previewFieldCallback = new LineupRealPreviewFieldView.LineupRealPreviewFieldViewCallback() {
        @Override
        public void didStartDrag(LineupRealPreviewFieldView view, LineupGlobalPlayers playerInfo, float newX, float newY) {

        }

        @Override
        public void didEndDrag(LineupRealPreviewFieldView view, LineupGlobalPlayers playerInfo, float newX, float newY) {
            float relX = newX / (float) teamAVuWidth; //getScreenWidth();
            float relY = newY / (float) teamAVuHeight; //getScreenHeight();
            boolean isGK = false;
                if (newY + view.getHeight() > teamAVuHeight - 50) {
                    int w = teamAVuWidth / 4;
                    // goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamAVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    LineupRealPreviewFieldView existingGk = checkTeamAGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamA.removeView(existingGk);
                        LineupRealPreviewFieldView fieldViewA = new LineupRealPreviewFieldView(getContext());
                        populateDataInTeamAVu(fieldViewA, existingGk.getPlayerInfo(), teamAVuWidth, teamAVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue * teamAVuWidth; //getScreenWidth();
                        float actualYValue = yValue * teamAVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamA.addView(fieldViewA, params);
                        saveRealLineupPlayerPositions(false, lineupRealGameTeam.getMatchId(), existingGk.getPlayerInfo().getId(), relX, relY, "0", false);

                    }
                    saveRealLineupPlayerPositions(false, lineupRealGameTeam.getMatchId(), playerInfo.getId(), relX, relY, "1", false);
                    view.setImage(lineupRealGameTeam.getTeamGkShirt());
                    view.getPlayerInfo().setIsGoalkeeper("1");
                }
                else {
                    saveRealLineupPlayerPositions(false, lineupRealGameTeam.getMatchId(), playerInfo.getId(), relX, relY, "0", false);
                    view.getPlayerInfo().setIsGoalkeeper("0");
                    view.setImage(lineupRealGameTeam.getTeamShirt());
                }
                view.getPlayerInfo().setxCoordinate(String.valueOf(relX));
                view.getPlayerInfo().setyCoordinate(String.valueOf(relY));


        }
    };
    View.OnDragListener vuDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v == binding.vuTeamA) {
                        binding.vuTeamA.setBackgroundColor(getResources().getColor(R.color.greenColor50));
                    }
                    break;

                case DragEvent.ACTION_DRAG_EXITED: case DragEvent.ACTION_DRAG_ENDED:
                    if (v == binding.vuTeamA) {
                        binding.vuTeamA.setBackgroundColor(Color.TRANSPARENT);
                    }

                    break;
                case DragEvent.ACTION_DROP:
                    final LineupRealDragData state = (LineupRealDragData) event.getLocalState();

                    if (v == binding.vuTeamA) {
                        replaceViewTeamA(state, (int)event.getX(), (int)event.getY(), true);
                    }
                    else {
                        replaceViewTeamA(state, 0, 0, true);
                    }
                           lineupRealGameTeam.getPlayers().add(state.getItem());
                            friendList.remove(state.getPos());
                            lineupRealPlayerListAdapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private LineupRealPreviewFieldView checkTeamAGkExist() {
        LineupRealPreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
            if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    break;
                }
            }
        }
        return view;
    }
    private int checkPlayerExistInTeamA(String id) {
        int result = -1;
        for (int i = 0; i < lineupRealGameTeam.getPlayers().size(); i++) {
            if (lineupRealGameTeam.getPlayers().get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }
    private void saveRealLineupPlayerPositions(boolean isLoader, String matchId, String playerId, float xCoordinate, float yCoordinate, String isGoalKeeper, boolean isAdd) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.saveRealLineupPlayerPositions(matchId, playerId, xCoordinate, yCoordinate, isGoalKeeper);
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
    private void removeRealLineupPlayer(boolean isLoader, String matchId, String pId, LineupRealPreviewFieldView view) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.removeRealLineupPlayer(matchId, pId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            LineupGlobalPlayers info = view.getPlayerInfo();
                            info.setIsGoalkeeper("0");
                            info.setxCoordinate("");
                            info.setyCoordinate("");
                            friendList.add(0, info);
                            if (matchId.equalsIgnoreCase(lineupRealGameTeam.getMatchId())) {
                                binding.vuTeamA.removeView(view);
                                for (int i = 0; i < lineupRealGameTeam.getPlayers().size(); i++) {
                                    if (lineupRealGameTeam.getPlayers().get(i).getId().equalsIgnoreCase(info.getId())) {
                                        lineupRealGameTeam.getPlayers().remove(i);
                                        break;
                                    }
                                }
                            }

                            lineupRealPlayerListAdapter.notifyDataSetChanged();
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
    private void getAllCountries(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.realLineupCountries();
        call.enqueue(new Callback<>() {
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
                            getTeamAndShirtDetails(true, selectedCountryId);
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
        call.enqueue(new Callback<>() {
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
        call.enqueue(new Callback<>() {
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
        call.enqueue(new Callback<>() {
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
    private void getTeamAndShirtDetails(boolean isLoader, String countryId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getTeamAndShirtDetails(countryId,"android");
        call.enqueue(new Callback<>() {
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
    private void lineupGlobalTeamPlayers(boolean isLoader, Boolean startlineup, String teamId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.lineupGlobalTeamPlayers(teamId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray globalPlayers = object.getJSONArray(Constants.kData);
                            friendList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < globalPlayers.length(); i++) {
                                LineupGlobalPlayers player = gson.fromJson(globalPlayers.get(i).toString(), LineupGlobalPlayers.class);
                                if (lineupRealGameTeam !=null){
                                    if (checkPlayerExistInTeamA(player.getId()) == -1) {
                                        friendList.add(player);
                                    }
                                }else{
                                    friendList.add(player);
                                }

                            }
                            friendList.add(null);

                            if (startlineup){
                                startRealLineup(true, teamId);
                            }
                            lineupRealPlayerListAdapter.notifyDataSetChanged();

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
    private void startRealLineup(boolean isLoader, String teamId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.startRealLineup(teamId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Gson gson = new Gson();
                            lineupRealGameTeam = gson.fromJson(object.getJSONObject(Constants.kData).toString(), LineupRealGameTeam.class);
                            binding.vuTeamA.removeAllViews();
                            // selectedCountryId = lineupRealGameTeam.getCountry();
                            // selectedTeamId = lineupRealGameTeam.getTeam();
            //                            selectedShirtId = lineupRealGameTeam.getShirt();
            //                            selectedChairId = lineupRealGameTeam.getChair();
            //                            selectedFieldId = lineupRealGameTeam.getBackground();
            //                            selectedGkShirtId = lineupRealGameTeam.getGoalkeeper();
                            Glide.with(getApplicationContext()).load(lineupRealGameTeam.getBgImageUrl()).into(binding.fieldBgImgVu);
                            Glide.with(getApplicationContext()).load(lineupRealGameTeam.getFieldImageUrl()).into(binding.fieldImgVu);
                            populateTeamData();
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
    private void getLineupSelections(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getLineupSelections();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Gson gson = new Gson();
                            lineupSelections = gson.fromJson(object.getJSONObject(Constants.kData).toString(), LineupSelections.class);
                            selectedCountryId = lineupSelections.getCountry();
                            selectedTeamId = lineupSelections.getTeam();
                            selectedShirtId = lineupSelections.getShirt();
                            selectedChairId = lineupSelections.getChair();
                            selectedFieldId = lineupSelections.getBackground();
                            selectedGkShirtId = lineupSelections.getGoalkeeper();
                            Glide.with(getApplicationContext()).load(lineupSelections.getBgImageUrl()).into(binding.fieldBgImgVu);
                            Glide.with(getApplicationContext()).load(lineupSelections.getFieldImageUrl()).into(binding.fieldImgVu);
                            getAllCountries(false);
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
    private void resetGameAPI(boolean isLoader, String matchId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.resetRealLineup(matchId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            binding.vuTeamA.removeAllViews();
                            lineupRealGameTeam = null;
                            lineupGlobalTeamPlayers(false,true, selectedTeamId);

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
    private void saveAppSettings(boolean isLoader, String targetId, String type, String chairUrl, String fieldBgImg, String fieldImg) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.lineupSettings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), targetId, type);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (type.equalsIgnoreCase("chair")) {
                                lineupRealPlayerListAdapter.setChairUrl(chairUrl);
                            }
                            else {
                                if (!fieldBgImg.isEmpty() && !fieldImg.isEmpty()){
                                    Glide.with(getApplicationContext()).load(fieldBgImg).into(binding.fieldBgImgVu);
                                    Glide.with(getApplicationContext()).load(fieldImg).into(binding.fieldImgVu);
                                }
                            }
                            if (type.equalsIgnoreCase("team")){
                                lineupGlobalTeamPlayers(false,true, targetId);
                            }
                            else if (type.equalsIgnoreCase("country")) {
                                getTeamAndShirtDetails(true, targetId);

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
    private void saveRealLineupMatchShirts(boolean isLoader, String matchId, String teamShirtId, String goalkeeperShirtId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.saveRealLineupMatchShirts(matchId,teamShirtId,goalkeeperShirtId);
        call.enqueue(new Callback<>() {
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
    private void executePlayerRemoveFunc(String matchId, String playerId, LineupRealPreviewFieldView view) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
            .setCancelButtonTitle(getString(R.string.cancel))
            .setOtherButtonTitles("Remove Player")
            .setCancelableOnTouchOutside(true).setListener(new ActionSheet.ActionSheetListener() {
        @Override
        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

        }

        @Override
        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
            if (index == 0) {
                removeRealLineupPlayer(true, matchId, playerId, view);
            }
        }
    }).show();
    }

}