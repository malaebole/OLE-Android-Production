package ae.oleapp.player;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleCreateMatchGroupAdapter;
import ae.oleapp.adapters.OlePlayerListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCreateMatchBinding;
import ae.oleapp.dialogs.OleCreateGroupDialogFragment;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayersGroup;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.apptik.widget.MultiSlider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreateMatchActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreateMatchBinding binding;
    private String bookingId = "";
    private String clubId = "";
    private String bookingPaymentMethod = "";
    private boolean isForUpdate = false;
    private String matchType = "";
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private final List<OlePlayerInfo> favoriteList = new ArrayList<>();
    private final List<OlePlayersGroup> groupList = new ArrayList<>();
    private boolean isVisibleForAll = false;
    private OlePlayerListAdapter adapter;
    private String isFavourite = "0";
    private OlePlayersGroup selectedGroup = null;
    private String matchAllowed = "";
    private String gameAllowed = "";
    private OleCreateMatchGroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreateMatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.create_match);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            clubId = bundle.getString("club_id", "");
            bookingPaymentMethod = bundle.getString("payment_method", "");
            isForUpdate = bundle.getBoolean("is_update", false);
            matchAllowed = bundle.getString("match_allow", "");
            gameAllowed = bundle.getString("game_allow", "");
        }

        setupSliders();

        binding.tvSelectedPlayer.setText(getResources().getString(R.string.player_selected, 0));

        binding.btnAddFav.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        LinearLayoutManager favLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.favRecyclerVu.setLayoutManager(favLayoutManager);
        adapter = new OlePlayerListAdapter(getContext(), playerList, false);
        adapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);
        binding.favRecyclerVu.setAdapter(adapter);

        LinearLayoutManager groupLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.groupRecyclerVu.setLayoutManager(groupLayoutManager);
        groupAdapter = new OleCreateMatchGroupAdapter(getContext(), groupList);
        groupAdapter.setOnItemClickListener(groupClickListener);
        binding.groupRecyclerVu.setAdapter(groupAdapter);

        if (isForUpdate) {
            binding.relFriendly.setVisibility(View.GONE);
            binding.cardFriendly.setClickable(false);
            binding.cardChallenge.setClickable(false);
            binding.inviteVu.setClickable(false);
            binding.visibleVu.setClickable(false);
            challengeClicked();
            visibleVuClicked();
        }
        else {
            binding.relFriendly.setVisibility(View.VISIBLE);
            binding.cardFriendly.setClickable(true);
            binding.cardChallenge.setClickable(true);
            binding.inviteVu.setClickable(true);
            binding.visibleVu.setClickable(true);
            challengeClicked();
            visibleVuClicked();
        }

        binding.etTotalPlayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.etPlayer.setText("");
                if (!s.toString().equalsIgnoreCase("")) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 0) {
                        binding.etPlayer.setText(String.valueOf(value-1));
                    }
                }
            }
        });

        binding.favSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                favSwitchChanged();
            }
        });

        binding.groupSwitch.setChecked(false);
        groupSwitchChanged();
        binding.groupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                groupSwitchChanged();
            }
        });

        binding.bar.backBtn.setOnClickListener(this);
        binding.cardFriendly.setOnClickListener(this);
        binding.cardChallenge.setOnClickListener(this);
        binding.inviteVu.setOnClickListener(this);
        binding.visibleVu.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);
        binding.btnChallenge.setOnClickListener(this);
        binding.btnSelectPlayers.setOnClickListener(this);
        binding.btnAddFav.setOnClickListener(this);
        binding.btnCreateGroup.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroupListAPI(groupList.isEmpty());
    }

    OlePlayerListAdapter.OnItemClickListener clickListener = new OlePlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (isFavourite.equalsIgnoreCase("1")) {
                Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
                intent.putExtra("player_id", playerList.get(pos).getId());
                startActivity(intent);
            }
            else {
                ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                        .setCancelButtonTitle(getResources().getString(R.string.cancel))
                        .setOtherButtonTitles(getResources().getString(R.string.remove))
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                if (index == 0) {
                                    playerList.remove(pos);
                                    adapter.notifyItemRemoved(pos);
                                    adapter.notifyItemRangeChanged(pos, playerList.size());
                                    binding.tvSelectedPlayer.setText(getResources().getString(R.string.player_selected, playerList.size()));
                                }
                            }
                        }).show();
            }
        }

        @Override
        public void OnImageClick(View v, int pos) {
            OnItemClick(v, pos);
        }
    };

    OleCreateMatchGroupAdapter.OnItemClickListener groupClickListener = new OleCreateMatchGroupAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            groupAdapter.setSelectedIndex(pos);
            selectedGroup = groupList.get(pos);
        }
    };

    private void setupSliders() {
        UserInfo userInfo = Functions.getUserinfo(this);
        if (userInfo != null) {
            if (userInfo.getUserAge() != null && !userInfo.getUserAge().equalsIgnoreCase("")) {
                int age = Integer.parseInt(userInfo.getUserAge());
                if (age > 17) {
                    binding.slider.getThumb(0).setValue(17);
                    binding.slider.getThumb(1).setValue(55);
                    binding.tvMinFriendlyAge.setText("17");
                    binding.tvMaxFriendlyAge.setText("55");

                    binding.challengeSlider.getThumb(0).setValue(17);
                    binding.challengeSlider.getThumb(1).setValue(55);
                    binding.tvMinAgeChallenge.setText("17");
                    binding.tvMaxAgeChallenge.setText("55");
                }
                else {
                    binding.slider.getThumb(0).setValue(12);
                    binding.slider.getThumb(1).setValue(16);
                    binding.tvMinFriendlyAge.setText("12");
                    binding.tvMaxFriendlyAge.setText("16");

                    binding.challengeSlider.getThumb(0).setValue(12);
                    binding.challengeSlider.getThumb(1).setValue(16);
                    binding.tvMinAgeChallenge.setText("12");
                    binding.tvMaxAgeChallenge.setText("16");
                }
            }
            else {
                binding.slider.getThumb(0).setValue(17);
                binding.slider.getThumb(1).setValue(55);
                binding.tvMinFriendlyAge.setText("17");
                binding.tvMaxFriendlyAge.setText("55");

                binding.challengeSlider.getThumb(0).setValue(17);
                binding.challengeSlider.getThumb(1).setValue(55);
                binding.tvMinAgeChallenge.setText("17");
                binding.tvMaxAgeChallenge.setText("55");
            }
        }
        binding.slider.setMax(60);
        binding.slider.setMin(12);
        binding.challengeSlider.setMax(60);
        binding.challengeSlider.setMin(12);
        binding.slider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    binding.tvMinFriendlyAge.setText(String.valueOf(thumb.getValue()));
                }
                else {
                    binding.tvMaxFriendlyAge.setText(String.valueOf(thumb.getValue()));
                }
            }
        });

        binding.challengeSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    binding.tvMinAgeChallenge.setText(String.valueOf(thumb.getValue()));
                }
                else {
                    binding.tvMaxAgeChallenge.setText(String.valueOf(thumb.getValue()));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.cardFriendly) {
            friendlyClicked();
        }
        else if (v == binding.cardChallenge) {
            challengeClicked();
        }
        else if (v == binding.inviteVu) {
            inviteVuClicked();
        }
        else if (v == binding.visibleVu) {
            visibleVuClicked();
        }
        else if (v == binding.btnCreate) {
            createClicked();
        }
        else if (v == binding.btnChallenge) {
            challengeNowClicked();
        }
        else if (v == binding.btnSelectPlayers || v == binding.btnAddFav) {
            btnSelectClicked();
        }
        else if (v == binding.btnCreateGroup) {
            if (groupList.size() > 0) {
                Intent intent = new Intent(getContext(), OleGroupListActivity.class);
                startActivity(intent);
            }
            else {
                createGroupClicked();
            }
        }
    }

    private void createGroupClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("CreateGroupDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleCreateGroupDialogFragment dialogFragment = new OleCreateGroupDialogFragment();
        dialogFragment.setDialogCallback(new OleCreateGroupDialogFragment.CreateGroupDialogCallback() {
            @Override
            public void groupCreated(String groupId, String name) {
                Intent intent = new Intent(getContext(), OleGroupPlayersActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("group_name", name);
                startActivity(intent);
            }
        });
        dialogFragment.show(fragmentTransaction, "CreateGroupDialogFragment");
    }

    private void friendlyClicked() {
        binding.imgVuFriendly.setImageResource(R.drawable.selected_friendly_game);
        binding.imgVuFriendlyRound.setImageResource(R.drawable.selected_friendly_game_round);
        binding.tvFriendly.setTextColor(getResources().getColor(R.color.greenColor));
        binding.cardFriendly.setStrokeColor(getResources().getColor(R.color.greenColor));
        binding.friendlyDetailVu.setVisibility(View.VISIBLE);

        binding.imgVuChallenge.setImageResource(R.drawable.challenge_match);
        binding.imgVuChallengeRound.setImageResource(R.drawable.friendly_game_round);
        binding.tvChallenge.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.cardChallenge.setStrokeColor(getResources().getColor(R.color.whiteColor));
        binding.challengeDetailVu.setVisibility(View.GONE);
        binding.favSwitch.setChecked(false);
        favSwitchChanged();
        binding.groupSwitch.setChecked(false);
        groupSwitchChanged();
    }

    private void challengeClicked() {
        binding.imgVuChallenge.setImageResource(R.drawable.selected_challenge_match);
        binding.imgVuChallengeRound.setImageResource(R.drawable.selected_challenge_match_round);
        binding.tvChallenge.setTextColor(getResources().getColor(R.color.redColor));
        binding.cardChallenge.setStrokeColor(getResources().getColor(R.color.redColor));
        binding.challengeDetailVu.setVisibility(View.VISIBLE);

        binding.imgVuFriendly.setImageResource(R.drawable.friendly_game);
        binding.imgVuFriendlyRound.setImageResource(R.drawable.friendly_game_round);
        binding.tvFriendly.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.cardFriendly.setStrokeColor(getResources().getColor(R.color.whiteColor));
        binding.friendlyDetailVu.setVisibility(View.GONE);
        binding.favSwitch.setChecked(false);
        favSwitchChanged();
        adapter.setDatasource(playerList);
        adapter.setFromFav(false);
        binding.groupSwitch.setChecked(false);
        groupSwitchChanged();
    }

    private void inviteVuClicked() {
        isVisibleForAll = false;
        binding.btnSelectPlayers.setVisibility(View.VISIBLE);
        binding.tvSelectedPlayer.setVisibility(View.VISIBLE);
        binding.recyclerVu.setVisibility(View.VISIBLE);
        adapter.setDatasource(playerList);
        adapter.setFromFav(false);
        binding.challengeSliderVu.setVisibility(View.GONE);
        binding.imgVuInvite.setImageResource(R.drawable.check);
        binding.imgVuVisible.setImageResource(R.drawable.friendly_game_round);
    }

    private void visibleVuClicked() {
        isVisibleForAll = true;
        binding.btnSelectPlayers.setVisibility(View.GONE);
        binding.tvSelectedPlayer.setVisibility(View.GONE);
        binding.recyclerVu.setVisibility(View.GONE);
        binding.challengeSliderVu.setVisibility(View.VISIBLE);
        binding.imgVuInvite.setImageResource(R.drawable.friendly_game_round);
        binding.imgVuVisible.setImageResource(R.drawable.check);
    }

    private void favSwitchChanged() {
        if (binding.favSwitch.isChecked()) {
            isFavourite = "1";
            binding.favRecyclerVu.setVisibility(View.VISIBLE);
            binding.btnAddFav.setVisibility(View.VISIBLE);
            if (favoriteList.size() == 0) {
                getFavListAPI(true);
            }
            else {
                adapter.setFromFav(true);
                adapter.setDatasource(favoriteList);
            }
            binding.groupSwitch.setChecked(false);
            groupSwitchChanged();
        }
        else {
            isFavourite = "0";
            binding.favRecyclerVu.setVisibility(View.GONE);
            binding.btnAddFav.setVisibility(View.GONE);
        }
    }

    private void groupSwitchChanged() {
        if (binding.groupSwitch.isChecked()) {
            binding.btnCreateGroup.setVisibility(View.VISIBLE);
            if (groupList.size() > 0) {
                binding.groupRecyclerVu.setVisibility(View.VISIBLE);
                binding.tvCreateGroup.setText(R.string.see_all_groups);
                groupAdapter.setSelectedIndex(-1);
            }
            else {
                binding.groupRecyclerVu.setVisibility(View.GONE);
                binding.tvCreateGroup.setText(R.string.create_group);
            }
            binding.favSwitch.setChecked(false);
            favSwitchChanged();
        }
        else {
            binding.groupRecyclerVu.setVisibility(View.GONE);
            binding.btnCreateGroup.setVisibility(View.GONE);
            selectedGroup = null;
        }
    }

    private void createClicked() {
        if (!gameAllowed.equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.club_not_allow_game), FancyToast.ERROR);
            return;
        }
        if (binding.etTotalPlayer.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_total_player), FancyToast.ERROR);
            return;
        }
        if (Integer.parseInt(binding.etTotalPlayer.getText().toString()) > 22 || Integer.parseInt(binding.etTotalPlayer.getText().toString()) < 10) {
            Functions.showToast(getContext(), getString(R.string.max_player_22), FancyToast.ERROR);
            return;
        }
        if (Integer.parseInt(binding.etTotalPlayer.getText().toString()) % 2 != 0) {
            Functions.showToast(getContext(), getString(R.string.players_should_even), FancyToast.ERROR);
            return;
        }
        if (isFavourite.equalsIgnoreCase("1") && favoriteList.size() < Integer.parseInt(binding.etTotalPlayer.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.fav_players_not_enough), FancyToast.ERROR);
            return;
        }
        if (binding.groupSwitch.isChecked() && selectedGroup == null) {
            Functions.showToast(getContext(), getString(R.string.select_group), FancyToast.ERROR);
            return;
        }
        if (binding.groupSwitch.isChecked() && selectedGroup.getPlayersCount() < Integer.parseInt(binding.etTotalPlayer.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.group_players_not_enough), FancyToast.ERROR);
            return;
        }
//        if (binding.etPlayer.getText().toString().isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.enter_player_count), FancyToast.ERROR);
//            return;
//        }
        matchType = "0";
        String groupId = "";
        if (binding.groupSwitch.isChecked() && selectedGroup != null) {
            groupId = selectedGroup.getId();
        }
        String finalGroupId = groupId;
        openPaymentDialog("0", Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, binding.etTotalPlayer.getText().toString(), true, false, "1", getString(R.string.friendly_game_notes), clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
            @Override
            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                createMatchAPI(true, "", binding.etTotalPlayer.getText().toString(), "", binding.tvMinFriendlyAge.getText().toString(), binding.tvMaxFriendlyAge.getText().toString(), orderRef, paymentMethod, cardPaid, walletPaid, finalGroupId);
            }
        });
    }

    private void challengeNowClicked() {
        if (isForUpdate) {
            changeMatch();
        }
        else {
            if (!matchAllowed.equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.club_not_allow_match), FancyToast.ERROR);
                return;
            }
            if (isVisibleForAll) {
                matchType = "1";
                boolean cashHide = !bookingPaymentMethod.equalsIgnoreCase("cash");
                openPaymentDialog("0", Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", cashHide, false, "1", getString(R.string.match_notes), clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                    @Override
                    public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                        createMatchAPI(true, "", "", "", binding.tvMinAgeChallenge.getText().toString(), binding.tvMaxAgeChallenge.getText().toString(), orderRef, paymentMethod, cardPaid, walletPaid, "");
                    }
                });
            }
            else {
                if (playerList.size() == 0) {
                    Functions.showToast(getContext(), getString(R.string.select_players), FancyToast.ERROR);
                    return;
                }
                matchType = "2";
                String ids = "";
                for (OlePlayerInfo info : playerList) {
                    if (ids.isEmpty()) {
                        ids = info.getId();
                    }
                    else {
                        ids = String.format("%s,%s", ids, info.getId());
                    }
                }
                boolean cashHide = !bookingPaymentMethod.equalsIgnoreCase("cash");
                String finalIds = ids;
                openPaymentDialog("0", Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", cashHide, false, "1", getString(R.string.match_notes), clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                    @Override
                    public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                        createMatchAPI(true, "", "", finalIds, "", "", orderRef, paymentMethod, cardPaid, walletPaid, "");
                    }
                });
            }
        }
    }

    private void changeMatch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(getResources().getString(R.string.do_you_want_to_change_match))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changePrivateMatchToPublicAPI(true, binding.tvMinAgeChallenge.getText().toString(), binding.tvMaxAgeChallenge.getText().toString());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void btnSelectClicked() {
        Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
        intent.putExtra("is_selection", true);
        startActivityForResult(intent, 106);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            if (isFavourite.equalsIgnoreCase("1")) {
                String ids = "";
                for (OlePlayerInfo info: list) {
                    if (ids.isEmpty()) {
                        ids = info.getId();
                    }
                    else {
                        ids = String.format("%s,%s", ids, info.getId());
                    }
                }
                addFav(ids);
            }
            else {
                playerList.clear();
                playerList.addAll(list);
                binding.tvSelectedPlayer.setText(getResources().getString(R.string.player_selected, playerList.size()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void createMatchAPI(boolean isLoader, String reqPlayers, String totalPlayers, String playerIds, String minAge, String maxAge, String orderRef, String paymentMethod, String cardPaid, String walletPaid, String groupId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.createMatch(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, matchType, reqPlayers, totalPlayers, minAge, maxAge, playerIds, orderRef, cardPaid, walletPaid, paymentMethod, isFavourite, Functions.getIPAddress(), groupId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                            intent.putExtra("tab_position", 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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

    private void changePrivateMatchToPublicAPI(boolean isLoader, String minAge, String maxAge) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.changePrivateMatchToPublic(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, minAge, maxAge);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void getFavListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFavList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrP = object.getJSONObject(Constants.kData).getJSONArray("players");
                            Gson gson = new Gson();
                            favoriteList.clear();
                            for (int i = 0; i < arrP.length(); i++) {
                                favoriteList.add(gson.fromJson(arrP.get(i).toString(), OlePlayerInfo.class));
                            }
                        }
                        else {
                            favoriteList.clear();
                        }
                        adapter.setFromFav(true);
                        adapter.setDatasource(favoriteList);
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

    private void getGroupListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getGroupList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            groupList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                groupList.add(gson.fromJson(arr.get(i).toString(), OlePlayersGroup.class));
                            }
                            if (binding.groupSwitch.isChecked() && groupList.size() > 0) {
                                binding.groupRecyclerVu.setVisibility(View.VISIBLE);
                                binding.tvCreateGroup.setText(R.string.see_all_groups);
                            }
                        }
                        else {
                            groupList.clear();
                            binding.groupRecyclerVu.setVisibility(View.GONE);
                            binding.tvCreateGroup.setText(R.string.create_group);
                        }
                        selectedGroup = null;
                        groupAdapter.setSelectedIndex(-1);
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

    private void addFav(String playerId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addRemoveFavAPI(playerId, "1", "player", new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(getContext(), msg, FancyToast.SUCCESS);
                    getFavListAPI(false);
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }
}
