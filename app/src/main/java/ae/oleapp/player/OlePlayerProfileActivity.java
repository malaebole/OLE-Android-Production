package ae.oleapp.player;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.OleFullImageActivity;
import ae.oleapp.adapters.OleLevelAttributeAdapter;
import ae.oleapp.adapters.OleProfileAchievementAdapter;
import ae.oleapp.adapters.OleProfileLevelAdapter;
import ae.oleapp.adapters.OleProfileMatchHistoryAdapter;
import ae.oleapp.adapters.OleProfilePadelMatchHistoryAdapter;
import ae.oleapp.adapters.OleProfilePlayersAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPlayerProfileBinding;
import ae.oleapp.dialogs.OleMissionCompleteDialogFragment;
import ae.oleapp.models.OleLevelsTarget;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerLevel;
import ae.oleapp.models.OleProfileMission;
import ae.oleapp.models.OleProfileAchievement;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleBookingCountDetailActivity;
import ae.oleapp.padel.OleProfilePadelMatchHistoryDetailsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerProfileActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPlayerProfileBinding binding;
    private OlePlayerInfo olePlayerInfo;
    private OleProfileAchievementAdapter achievementAdapter;
    private OleProfilePlayersAdapter playersAdapter;
    private OleProfileLevelAdapter levelAdapter;
    private final List<OlePlayerInfo> mostPlayedPlayers = new ArrayList<>();
    private final List<OleMatchResults> oleMatchResults = new ArrayList<>();
    private final List<OlePadelMatchResults> olePadelMatchResults = new ArrayList<>();
    private final List<OleProfileAchievement> achievements = new ArrayList<>();
    private final List<OlePlayerLevel> olePlayerLevels = new ArrayList<>();
    private String selectedModule = "";
    private String playerId = "";
    private LinearLayoutManager levelLayoutManager;
    private OleProfileMission oleProfileMission;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerId = bundle.getString("player_id", "");
        }
        else {
            playerId = Functions.getPrefValue(getContext(), Constants.kUserID);
        }

        binding.tvLevel.setVisibility(View.INVISIBLE);
        binding.btnStat.setVisibility(View.INVISIBLE);
        binding.lastBookingVu.setVisibility(View.GONE);
        binding.balanceVu.setVisibility(View.GONE);
        binding.invitationVu.setVisibility(View.GONE);

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            padelClicked();
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1), true);
        }
        else {
            footballClicked();
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0), true);
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    footballClicked();
                }
                else {
                    padelClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        LinearLayoutManager matchLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.matchRecyclerVu.setLayoutManager(matchLayoutManager);

        LinearLayoutManager achievementLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.achievementRecyclerVu.setLayoutManager(achievementLayoutManager);
        achievementAdapter = new OleProfileAchievementAdapter(getContext(), achievements);
        achievementAdapter.setItemClickListener(achievementClickListener);
        binding.achievementRecyclerVu.setAdapter(achievementAdapter);

        LinearLayoutManager playersLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.playersRecyclerVu.setLayoutManager(playersLayoutManager);
        playersAdapter = new OleProfilePlayersAdapter(getContext(), mostPlayedPlayers);
        playersAdapter.setOnItemClickListener(playerClickListener);
        binding.playersRecyclerVu.setAdapter(playersAdapter);

        levelLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.levelRecyclerVu.setLayoutManager(levelLayoutManager);
        levelAdapter = new OleProfileLevelAdapter(getContext(), olePlayerLevels, binding.levelRecyclerVu, selectedModule);
        levelAdapter.setOnItemClickListener(levelClickListener);
        binding.levelRecyclerVu.setAdapter(levelAdapter);

        binding.missionVu.setVisibility(View.GONE);

        binding.btnEdit.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnFav.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
        binding.reviewsVu.setOnClickListener(this);
        binding.btnRewards.setOnClickListener(this);
        //binding.imgVu.setOnClickListener(this);
        binding.missionVu.setOnClickListener(this);
        binding.btnStat.setOnClickListener(this);
        binding.balanceVu.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);
    }

    OleProfileMatchHistoryAdapter.ItemClickListener matchItemClickListener = new OleProfileMatchHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleProfileMatchHistoryDetailsActivity.class);
            intent.putExtra("match", new Gson().toJson(oleMatchResults.get(pos)));
            intent.putExtra("player_id", playerId);
            startActivity(intent);
        }
    };

    OleProfilePadelMatchHistoryAdapter.ItemClickListener padelMatchItemClickListener = new OleProfilePadelMatchHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleProfilePadelMatchHistoryDetailsActivity.class);
            intent.putExtra("match", new Gson().toJson(olePadelMatchResults.get(pos)));
            intent.putExtra("player_id", playerId);
            startActivity(intent);
        }
    };

    OleProfilePlayersAdapter.OnItemClickListener playerClickListener = new OleProfilePlayersAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", mostPlayedPlayers.get(pos).getId());
            startActivity(intent);
        }
    };

    OleProfileAchievementAdapter.ItemClickListener achievementClickListener = new OleProfileAchievementAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
    };

    OleProfileLevelAdapter.OnItemClickListener levelClickListener = new OleProfileLevelAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OleProfileLevelDetailsActivity.class);
            intent.putExtra("level_id", olePlayerLevels.get(pos).getId());
            intent.putExtra("player_id", playerId);
            intent.putExtra("module", selectedModule);
            startActivity(intent);
        }
    };

    private void footballClicked() {
        selectedModule = Constants.kFootballModule;
        binding.imgVuHeader.setImageResource(R.drawable.profile_header);
        binding.tabVu.setCardBackgroundColor(Color.parseColor("#004484"));
        binding.btnCall.setImageResource(R.drawable.green_call_btn);
        binding.matchWonPointsVu.setVisibility(View.VISIBLE);
        binding.goalsVu.setVisibility(View.VISIBLE);
        binding.friendlyGameVu.setVisibility(View.VISIBLE);
        binding.statisticsVu.setVisibility(View.GONE);
        binding.playersVu.setVisibility(View.GONE);
        binding.levelRecyclerVu.setVisibility(View.INVISIBLE);
        binding.missionBgVu.setImageResource(R.drawable.level_bg);
        binding.relMission.setBackgroundColor(Color.parseColor("#004484"));
        binding.bigCircle.setCardBackgroundColor(Color.parseColor("#005BB0"));
        binding.smallCircle.setCardBackgroundColor(Color.parseColor("#004484"));
        getProfileAPI(true);
        binding.missionVu.setVisibility(View.GONE);
        getMissionsAPI(false);
    }

    private void padelClicked() {
        selectedModule = Constants.kPadelModule;
        binding.imgVuHeader.setImageResource(R.drawable.padel_profile_header);
        binding.tabVu.setCardBackgroundColor(Color.parseColor("#1D643B"));
        binding.btnCall.setImageResource(R.drawable.white_call_btn);
        binding.matchWonPointsVu.setVisibility(View.GONE);
        binding.goalsVu.setVisibility(View.GONE);
        binding.friendlyGameVu.setVisibility(View.GONE);
        binding.statisticsVu.setVisibility(View.VISIBLE);
        binding.playersVu.setVisibility(View.VISIBLE);
        binding.levelRecyclerVu.setVisibility(View.INVISIBLE);
        binding.missionBgVu.setImageResource(R.drawable.padel_level_bg);
        binding.relMission.setBackgroundColor(Color.parseColor("#1A5A35"));
        binding.bigCircle.setCardBackgroundColor(Color.parseColor("#0A8C41"));
        binding.smallCircle.setCardBackgroundColor(Color.parseColor("#1A5A35"));
        getProfileAPI(true);
        binding.missionVu.setVisibility(View.GONE);
        getMissionsAPI(false);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnEdit) {
            editClicked();
        }
        else if (v == binding.btnCall) {
            if (olePlayerInfo != null) {
                makeCall(olePlayerInfo.getPhone());
            }
        }
        else if (v == binding.reviewsVu) {
            Intent intent = new Intent(getContext(), OlePlayerReviewsActivity.class);
            intent.putExtra("player_id", olePlayerInfo.getId());
            startActivity(intent);
        }
        else if (v == binding.btnRewards) {

        }
        else if (v == binding.btnFav) {
            if (olePlayerInfo == null) { return; }
            if (olePlayerInfo.getFavorite().equalsIgnoreCase("1")) {
                addRemoveFav(true, olePlayerInfo.getId(), "0");
            }
            else {
                addRemoveFav(true, olePlayerInfo.getId(), "1");
            }
        }
//        else if (v == binding.imgVu) {
//            if (olePlayerInfo != null && !olePlayerInfo.getPhotoUrl().equalsIgnoreCase("")) {
//                Intent intent = new Intent(getContext(), OleFullImageActivity.class);
//                intent.putExtra("URL", olePlayerInfo.getPhotoUrl());
//                startActivity(intent);
//            }
//        }
        else if (v == binding.missionVu) {
            if (oleProfileMission != null) {
                Intent intent = new Intent(getContext(), OleProfileMissionDetailsActivity.class);
                intent.putExtra("module", selectedModule);
                intent.putExtra("player_id", playerId);
                intent.putExtra("mission", new Gson().toJson(oleProfileMission));
                startActivity(intent);
            }
        }
        else if (v == binding.btnStat) {
            if (olePlayerInfo != null) {
                Intent bookingIntent = new Intent(getContext(), OleBookingCountDetailActivity.class);
                bookingIntent.putExtra("player_id", olePlayerInfo.getId());
                bookingIntent.putExtra("player_phone", olePlayerInfo.getPhone());
                bookingIntent.putExtra("player_name", olePlayerInfo.getName());
                startActivity(bookingIntent);
            }
        }
        else if (v == binding.balanceVu) {
            if (olePlayerInfo != null) {
                Intent bookingIntent = new Intent(getContext(), OlePlayerOutstandingBalanceActivity.class);
                bookingIntent.putExtra("player_id", olePlayerInfo.getId());
                startActivity(bookingIntent);
            }
        }
        else if (v == binding.btnAccept) {
            final String appPackageName = "com.olesports";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    private void editClicked() {
        startActivity(new Intent(getContext(), OleEditPlayerActivity.class));
    }

    @Override
    protected void onResume() {
        getProfileAPI(true);
        super.onResume();
    }

    private void populateData() {
        if (olePlayerInfo == null) {
            return;
        }
        if (olePlayerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {

            binding.btnCall.setVisibility(View.GONE);
            binding.btnFav.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.balanceVu.setVisibility(View.VISIBLE);
        }
        else {
           // Log.d("printUserId", Functions.getPrefValue(getContext(), Constants.kUserID));
            binding.btnCall.setVisibility(View.VISIBLE);
            binding.btnFav.setVisibility(View.VISIBLE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.balanceVu.setVisibility(View.GONE);
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                binding.btnFav.setVisibility(View.INVISIBLE);
                binding.balanceVu.setVisibility(View.VISIBLE);
            }
        }
        Glide.with(getApplicationContext()).load(olePlayerInfo.getEmojiUrl()).into(binding.pEmojiImgVu);
        Glide.with(getApplicationContext()).load(olePlayerInfo.getBibUrl()).into(binding.pShirtImgVu);
        
        if (olePlayerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            userInfo = Functions.getUserinfo(getContext());
            //checkX

            if (olePlayerInfo.getEmojiUrl()!=null){
                userInfo.setEmojiUrl(olePlayerInfo.getEmojiUrl());
            }else{
                userInfo.setEmojiUrl("");
            }
            userInfo.setBibUrl(olePlayerInfo.getBibUrl());
            Functions.saveUserinfo(getContext(),userInfo);
        }



        if (olePlayerInfo.getNickName().isEmpty()) {
            binding.tvName.setText(olePlayerInfo.getName());
        }
        else {
            binding.tvName.setText(String.format("%s(%s)", olePlayerInfo.getName(), olePlayerInfo.getNickName()));
        }
        if (olePlayerInfo.getPendingBalance().equalsIgnoreCase("") || olePlayerInfo.getPendingBalance().equalsIgnoreCase("0")) {
            binding.balanceVu.setVisibility(View.GONE);
        }
        binding.tvBalance.setText(String.format("%s: %s %s", getString(R.string.unpaid_amount), olePlayerInfo.getPendingBalance(), olePlayerInfo.getCurrency()));
        if (olePlayerInfo.getLevel() != null && !olePlayerInfo.getLevel().isEmpty() && !olePlayerInfo.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvLevel.setVisibility(View.VISIBLE);
            binding.tvLevel.setText(String.format("LV: %s", olePlayerInfo.getLevel().getValue()));
        }
        else {
            binding.tvLevel.setVisibility(View.INVISIBLE);
        }
        binding.tvCancelled.setText(olePlayerInfo.getCancelCount());
        binding.tvCompletedBookings.setText(olePlayerInfo.getTotalBookings());
        binding.tvCancelledPerc.setText(String.format("%s%% %s", olePlayerInfo.getCancelPercentage(), getString(R.string.cancelled)));
        if (olePlayerInfo.getCancelPercentage().equalsIgnoreCase("0")) {
            binding.circularProgressBar.setProgress(100.0f);
            binding.cancelEmoji.setAlpha(1.0f);
            binding.circularProgressBar.setProgressBarColor(getResources().getColor(R.color.greenColor));
            binding.cancelEmoji.setImageResource(R.drawable.happy_face);
        }
        else {
            binding.circularProgressBar.setProgress(Float.parseFloat(olePlayerInfo.getCancelPercentage()));
            binding.circularProgressBar.setProgressBarColor(getResources().getColor(R.color.redColor));
            binding.cancelEmoji.setAlpha(Float.parseFloat(olePlayerInfo.getCancelPercentage())/100.0f);
            binding.cancelEmoji.setImageResource(R.drawable.angry_face);
        }

        binding.tvBookingDate.setText(olePlayerInfo.getLastBookingDate());

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            binding.btnRewards.setVisibility(View.INVISIBLE);
            binding.btnStat.setVisibility(View.VISIBLE);
            binding.lastBookingVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnStat.setVisibility(View.INVISIBLE);
            binding.lastBookingVu.setVisibility(View.GONE);
            if (olePlayerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) && olePlayerInfo.getIsRewarded().equalsIgnoreCase("1")) {
                binding.btnRewards.setVisibility(View.VISIBLE);
            } else {
                binding.btnRewards.setVisibility(View.INVISIBLE);
            }
        }

        binding.tvMatches.setText(olePlayerInfo.getMatchPlayed());
        binding.tvLostMatches.setText(olePlayerInfo.getMatchLoss());
        binding.tvWon.setText(olePlayerInfo.getMatchWon());
        binding.tvDrawMatches.setText(olePlayerInfo.getMatchDrawn());
        binding.tvPoints.setText(olePlayerInfo.getPoints());
        binding.tvWinPerc.setText(String.format("%s%%", olePlayerInfo.getWinPercentage()));
        binding.tvFriendly.setText(olePlayerInfo.getFriendlyGames());
        binding.tvFriendlyPerc.setText(String.format("%s%%", olePlayerInfo.getGamesRanking()));
        binding.friendlyProgressBar.setProgress(Float.parseFloat(olePlayerInfo.getGamesRanking()));
        binding.tvRate.setText(olePlayerInfo.getReviews());
        binding.tvMyGoals.setText(olePlayerInfo.getGoals());
        binding.tvOpponentGoals.setText(olePlayerInfo.getOpponentGoals());

        // padel match stats
        binding.tvTotals.setText(olePlayerInfo.getMatchPlayed());
        binding.tvLostPadelMatches.setText(olePlayerInfo.getMatchLoss());
        binding.tvPadelWon.setText(olePlayerInfo.getMatchWon());
        binding.tvPadelWinPerc.setText(String.format("%s%%", olePlayerInfo.getWinPercentage()));
        binding.padelProgressBar.setProgress(Float.parseFloat(olePlayerInfo.getWinPercentage()));

        if (olePlayerInfo.getFavorite().equalsIgnoreCase("1")) {
            binding.btnFav.setImageResource(R.drawable.yellow_fav_ic);
        }
        else {
            binding.btnFav.setImageResource(R.drawable.shop_unfav_ic);
        }

        if (olePlayerInfo.getAchievements() != null && olePlayerInfo.getAchievements().size() > 0) {
            binding.achievementVu.setVisibility(View.VISIBLE);
            achievements.clear();
            achievements.addAll(olePlayerInfo.getAchievements());
            achievementAdapter.notifyDataSetChanged();
        }
        else {
            binding.achievementVu.setVisibility(View.GONE);
        }

        olePlayerLevels.clear();
        olePlayerLevels.addAll(olePlayerInfo.getPlayerLevels());
        levelLayoutManager.setStackFromEnd(olePlayerLevels.size() > 1);
        levelAdapter.notifyDataSetChanged();

        if (selectedModule.equalsIgnoreCase(Constants.kPadelModule)) {
            // padel
            if (olePlayerInfo.getMostPlayed() != null && olePlayerInfo.getMostPlayed().size() > 0) {
                binding.playersVu.setVisibility(View.VISIBLE);
                mostPlayedPlayers.clear();
                mostPlayedPlayers.addAll(olePlayerInfo.getMostPlayed());
                playersAdapter.notifyDataSetChanged();
            }
            else {
                binding.playersVu.setVisibility(View.GONE);
            }
            if (olePlayerInfo.getPadelMatches() != null && olePlayerInfo.getPadelMatches().size() > 0) {
                binding.matchHistoryVu.setVisibility(View.VISIBLE);
                olePadelMatchResults.clear();
                olePadelMatchResults.addAll(olePlayerInfo.getPadelMatches());
                OleProfilePadelMatchHistoryAdapter matchHistorytAdapter = new OleProfilePadelMatchHistoryAdapter(getContext(), olePadelMatchResults);
                matchHistorytAdapter.setItemClickListener(padelMatchItemClickListener);
                binding.matchRecyclerVu.setAdapter(matchHistorytAdapter);
            }
            else {
                binding.matchHistoryVu.setVisibility(View.GONE);
            }
        }
        else {
            // football
            if (olePlayerInfo.getFootballMatches() != null && olePlayerInfo.getFootballMatches().size() > 0) {
                binding.matchHistoryVu.setVisibility(View.VISIBLE);
                oleMatchResults.clear();
                oleMatchResults.addAll(olePlayerInfo.getFootballMatches());
                OleProfileMatchHistoryAdapter matchHistorytAdapter = new OleProfileMatchHistoryAdapter(getContext(), oleMatchResults);
                matchHistorytAdapter.setItemClickListener(matchItemClickListener);
                binding.matchRecyclerVu.setAdapter(matchHistorytAdapter);
            } else {
                binding.matchHistoryVu.setVisibility(View.GONE);
            }
        }

        //// LEVELS /////
        binding.levelRecyclerVu.setVisibility(View.VISIBLE);
        levelAdapter.setModule(selectedModule);
        levelAdapter.notifyDataSetChanged();
    }

    private void populateMissionData() {
        if (oleProfileMission != null) {
            binding.missionVu.setVisibility(View.VISIBLE);
            binding.tvMissionTitle.setText(oleProfileMission.getTitle());
            binding.tvMissionDesc.setText(oleProfileMission.getRewardDesc());
            Glide.with(getApplicationContext()).load(oleProfileMission.getRewardPhoto()).into(binding.imgVuMission);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
            binding.missionRecyclerVu.setLayoutManager(layoutManager);
            OleLevelAttributeAdapter adapter = new OleLevelAttributeAdapter(getContext(), oleProfileMission.getTargets(), selectedModule, false);
            adapter.setOnItemClickListener(new OleLevelAttributeAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View v, int pos) {
                    onClick(binding.missionVu);
                }
            });
            binding.missionRecyclerVu.setAdapter(adapter);

            OleLevelsTarget oleLevelsTarget = null;
            for (OleLevelsTarget target : oleProfileMission.getTargets()) {
                if (target.getRemaining() != 0) {
                    oleLevelsTarget = target;
                    break;
                }
            }

            if (oleLevelsTarget != null) {
                binding.tvRemaining.setVisibility(View.VISIBLE);
                int completed = oleLevelsTarget.getTotal() - oleLevelsTarget.getRemaining();
                binding.tvRemaining.setText(String.format(Locale.ENGLISH, "%d/%d %s", completed, oleLevelsTarget.getTotal(), getString(R.string.completed)));
                binding.missionCircular.setCurProcess((completed*100)/ oleLevelsTarget.getTotal());
                binding.tvNextTitle.setText(oleLevelsTarget.getTitle());
                Glide.with(getApplicationContext()).load(oleLevelsTarget.getActiveIcon()).into(binding.nextAttrImgVu);
            }
            else {
                // completed
                binding.tvRemaining.setVisibility(View.GONE);
                binding.missionCircular.setCurProcess(100);
                binding.tvNextTitle.setText(R.string.completed);
                if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) && !oleProfileMission.getRewardCollected().equalsIgnoreCase("1")) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("MissionCompleteDialogFragment");
                    if (prev != null) {
                        fragmentTransaction.remove(prev);
                    }
                    fragmentTransaction.addToBackStack(null);
                    OleMissionCompleteDialogFragment dialogFragment = new OleMissionCompleteDialogFragment(oleProfileMission, selectedModule);
                    dialogFragment.show(getSupportFragmentManager(), "MissionCompleteDialogFragment");
                }
            }
        }
        else {
            binding.missionVu.setVisibility(View.GONE);
        }
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), playerId,"", selectedModule);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            olePlayerInfo = gson.fromJson(obj.toString(), OlePlayerInfo.class);
//                            String bibUrl = object.getJSONObject(Constants.kData).getString("bib_url");
//                            String emojiurl = object.getJSONObject(Constants.kData).getString("emoji_url");
//                            userInfo.setBibUrl(bibUrl);
//                            userInfo.setEmojiUrl(emojiurl);
//                            Functions.saveUserinfo(getContext(), userInfo);
                            populateData();
//                            if (olePlayerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//                                JSONObject requestObj = object.getJSONObject("lineup_request");
//                                if (requestObj.length() == 0) {
//                                    binding.invitationVu.setVisibility(View.GONE);
//                                }
//                                else {
//                                    binding.invitationVu.setVisibility(View.VISIBLE);
//                                    binding.tvInvitation.setText("");
//                                    Glide.with(getApplicationContext()).load("").into(binding.shirtImgVu);
//                                    Glide.with(getApplicationContext()).load("").into(binding.emojiImgVu);
//                                    binding.tvPerc.setText("0%");
//                                    binding.tvLineupGames.setText("");
//                                }
//                            }
//                            else {
//                                binding.invitationVu.setVisibility(View.GONE);
//                            }
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

    private void getMissionsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.oelMission(Functions.getAppLang(getContext()), playerId, selectedModule);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            oleProfileMission = gson.fromJson(obj.toString(), OleProfileMission.class);
                            populateMissionData();
                        }
                        else {
                            binding.missionVu.setVisibility(View.GONE);
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

    private void addRemoveFav(boolean isLoader, String playerId, String status) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addRemoveFavAPI(playerId, status, "player", selectedModule, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(getContext(), msg, FancyToast.SUCCESS);
                    olePlayerInfo.setFavorite(status);
                    if (status.equalsIgnoreCase("1")) {
                        binding.btnFav.setImageResource(R.drawable.yellow_fav_ic);
                    }
                    else {
                        binding.btnFav.setImageResource(R.drawable.shop_unfav_ic);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }
}
