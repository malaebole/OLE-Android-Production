package ae.oleapp.player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.adapters.OleTeamAdapter;
import ae.oleapp.adapters.OleTeamDragItem;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityGameBookingDetailBinding;
import ae.oleapp.dialogs.OleBookingPriceDetailDialogFragment;
import ae.oleapp.dialogs.OleCancelBookingDialog;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OleUpdateFacilityBottomDialog;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleDataModel;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleGameBookingDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityGameBookingDetailBinding binding;
    private String bookingId = "";
    private OlePlayerBookingList bookingDetail;
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private final List<OlePlayerInfo> allPlayerList = new ArrayList<>();
    private final List<OlePlayerInfo> teamAList = new ArrayList<>();
    private final List<OlePlayerInfo> teamBList = new ArrayList<>();
    private OleFacilityAdapter oleFacilityAdapter;
    private boolean isCaptainAvailable = false;
    private String captainId = "";
    private boolean isMyTurn = true;
    private DatabaseReference databaseReference;
    private ValueEventListener databaseHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityGameBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.booking_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
        }

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), clubFacilities, true);
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        binding.tvTurn.setVisibility(View.GONE);
        binding.btns.setVisibility(View.GONE);
        binding.formationInfoVu.setVisibility(View.GONE);
        binding.tvFacility.setVisibility(View.GONE);
        binding.relChat.setVisibility(View.GONE);
        binding.toolbarBadge.setVisibility(View.GONE);
        binding.btnEdit.setVisibility(View.GONE);
        binding.tvFacility.setVisibility(View.GONE);

        detailVuClicked();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    detailVuClicked();
                }
                else {
                    formationVuClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnAddFac.setOnClickListener(this);
        binding.receivedRequestVu.setOnClickListener(this);
        binding.joinedPlayerVu.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnCreateTeam.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnPreview.setOnClickListener(this);
        binding.btnPriceInfo.setOnClickListener(this);
        binding.groupVu.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
    }

    private void favSwitchChanged() {
        if (binding.favSwitch.isChecked()) {
            hideShowFavAPI(true, "1");
        }
        else {
            hideShowFavAPI(true, "0");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (databaseReference != null && databaseHandle != null) {
            databaseReference.child("match").child(bookingId).removeEventListener(databaseHandle);
        }
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBookingDetail(bookingDetail == null);
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive_new_msg"));
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("receive_new_msg")) {
                Bundle bundle = intent.getExtras();
                String bookId = bundle.getString("booking_id", "");
                String recId = bundle.getString("receiver_id", "");
                if (bookId.equalsIgnoreCase(bookingId) && recId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    String count = bundle.getString("unread_chat_count", "");
                    if (!count.equalsIgnoreCase("")) {
                        setBadgeValue(count);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnChat) {
            chatClicked();
        }
        else if (v == binding.mapVu) {
            locationVuClicked();
        }
        else if (v == binding.btnAddFac) {
            addFacClicked();
        }
        else if (v == binding.receivedRequestVu) {
            recRequestClicked();
        }
        else if (v == binding.joinedPlayerVu) {
            joinedPlayerClicked();
        }
        else if (v == binding.btnEdit) {
            editClicked();
        }
        else if (v == binding.btnCreateTeam) {
            createTeamClicked();
        }
        else if (v == binding.btnPay) {
            payClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
        else if (v == binding.btnConfirm) {
            confirmClicked();
        }
        else if (v == binding.btnPreview) {
            previewClicked();
        }
        else if (v == binding.btnPriceInfo) {
            priceInfoClicked();
        }
        else if (v == binding.groupVu) {
            groupClicked();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void shareClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleFriendlyGameShareActivity.class);
            intent.putExtra("club_name", bookingDetail.getClubName());
            intent.putExtra("date", bookingDetail.getBookingDate());
            intent.putExtra("time", bookingDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(bookingDetail.getCreatedBy()));
            intent.putExtra("player_list", gson.toJson(bookingDetail.getJoinedPlayers()));
            int totalPlayers = 0;
            if (bookingDetail.getTotalPlayers() == null || bookingDetail.getTotalPlayers().isEmpty()) {
                totalPlayers = 0;
            }
            else {
                totalPlayers = Integer.parseInt(bookingDetail.getTotalPlayers());
            }
            intent.putExtra("req_players", totalPlayers);
            startActivity(intent);
        }
    }

    private void groupClicked() {
        if (bookingDetail != null && bookingDetail.getPlayersGroup() != null) {
            Intent intent = new Intent(getContext(), OleGroupPlayersActivity.class);
            intent.putExtra("group_id", bookingDetail.getPlayersGroup().getId());
            intent.putExtra("group_name", bookingDetail.getPlayersGroup().getName());
            startActivity(intent);
        }
    }

    private void chatClicked() {
        if (bookingDetail == null) {
            return;
        }
        if (playerList.size() == 0) {
            Functions.showToast(getContext(), getString(R.string.players_not_avail_chat), FancyToast.ERROR);
            return;
        }

        Intent intent = new Intent(getContext(), OleChatActivity.class);
        intent.putExtra("booking_id", bookingDetail.getBookingId());
        intent.putExtra("booking_status", bookingDetail.getBookingStatus());
        intent.putExtra("booking_type", bookingDetail.getBookingType());
        intent.putExtra("is_match_detail", false);
        Gson gson = new Gson();
        intent.putExtra("join_players", gson.toJson(playerList));
        intent.putExtra("p1_info", gson.toJson(bookingDetail.getCreatedBy()));
        if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge) && bookingDetail.getJoinedPlayers().size() > 0) {
            intent.putExtra("p2_info", gson.toJson(bookingDetail.getJoinedPlayers().get(0)));
        }
        else if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge)) {
            intent.putExtra("p2_info", gson.toJson(playerList.get(0)));
        }
        startActivity(intent);
    }

    private void detailVuClicked() {
        binding.detailInfoVu.setVisibility(View.VISIBLE);
        binding.formationInfoVu.setVisibility(View.GONE);
        binding.relChat.setVisibility(View.VISIBLE);
        if (bookingDetail != null) {
            setBadgeValue(bookingDetail.getUnreadChatCount());
        }
        binding.btnEdit.setVisibility(View.GONE);
    }

    private void formationVuClicked() {
        binding.detailInfoVu.setVisibility(View.GONE);
        binding.formationInfoVu.setVisibility(View.VISIBLE);
        binding.btnEdit.setVisibility(View.GONE);
        binding.relChat.setVisibility(View.VISIBLE);
        if (bookingDetail != null) {
            setBadgeValue(bookingDetail.getUnreadChatCount());
        }
        if (bookingDetail != null && !bookingDetail.getGameTeams().isEmpty()) {
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.relChat.setVisibility(View.GONE);
            setBadgeValue("0");
        }
        setupBoard();
    }

    private void setBadgeValue(String chatCount) {
        int count = Integer.parseInt(chatCount);
        if (count > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(count);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void locationVuClicked() {
        if (bookingDetail != null) {
            String uri = "http://maps.google.com/maps?daddr=" + bookingDetail.getClubLatitude() + "," + bookingDetail.getClubLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    }

    private void addFacClicked() {
        if (bookingDetail != null && bookingDetail.getPaymentMethod().equalsIgnoreCase("cash")) {
            OleUpdateFacilityBottomDialog bottomDialog = new OleUpdateFacilityBottomDialog(bookingDetail.getClubFacilities(), bookingDetail.getFacilities(), bookingId);
            bottomDialog.setDialogCallback(new OleUpdateFacilityBottomDialog.UpdateFacilityBottomDialogCallback() {
                @Override
                public void didUpdateFacilities() {
                    getBookingDetail(bookingDetail == null);
                }
            });
            bottomDialog.show(getSupportFragmentManager(), "UpdateFacilityBottomDialog");
        }
        else {
            Functions.showToast(getContext(), getResources().getString(R.string.you_can_update_fac_in_cash), FancyToast.ERROR);
        }
    }

    private void recRequestClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleReceivedRequestActivity.class);
            intent.putExtra("match_type", bookingDetail.getBookingType());
            intent.putExtra("booking_id", bookingDetail.getBookingId());
            intent.putExtra("req_status", bookingDetail.getRequestStatus());
            startActivity(intent);
        }
    }

    private void joinedPlayerClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleJoinedPlayersActivity.class);
            intent.putExtra("match_type", bookingDetail.getBookingType());
            intent.putExtra("booking_id", bookingDetail.getBookingId());
            intent.putExtra("req_status", bookingDetail.getRequestStatus());
            intent.putExtra("booking_status", bookingDetail.getBookingStatus());
            intent.putExtra("rem_amount", bookingDetail.getRemainingAmount());
            intent.putExtra("currency", bookingDetail.getCurrency());
            intent.putExtra("matchCreatedId", bookingDetail.getCreatedBy().getId());
            intent.putExtra("isFromMatchDetail", false);
            startActivity(intent);
        }
    }

    private void editClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleCreateTeamActivity.class);
            intent.putExtra("booking_id", bookingDetail.getBookingId());
            Gson gson = new Gson();
            intent.putExtra("team", gson.toJson(bookingDetail.getGameTeams()));
            startActivityForResult(intent, 109);
        }
    }

    private void createTeamClicked() {
        if (bookingDetail != null) {
            if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                Functions.showToast(getContext(), getString(R.string.booking_finished_cannot_create_team), FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent(getContext(), OleCreateTeamActivity.class);
            intent.putExtra("booking_id", bookingDetail.getBookingId());
            intent.putExtra("team", "");
            startActivityForResult(intent, 109);
        }
    }

    private void payClicked() {
        if (bookingDetail != null) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.pay_full_amount), getResources().getString(R.string.pay_for_my_team))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        int selectedIndex = 0;
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                            if (!isCancel) {
                                if (selectedIndex == 0) {
                                    // pay full amount
                                    openPaymentDialog(bookingDetail.getBookingPrice(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", true, false, "0", "", bookingDetail.getClubId(), "1", new OlePaymentDialogFragment.PaymentDialogCallback() {
                                        @Override
                                        public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                            addBookingPaymentAPI(true, paymentMethod, orderRef, paidPrice, walletPaid, cardPaid);
                                        }
                                    });
                                }
                                else {
                                    // pay ony your part
                                    openPaymentDialog(bookingDetail.getBookingPrice(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", true, false, "0", "", bookingDetail.getClubId(), "0", new OlePaymentDialogFragment.PaymentDialogCallback() {
                                        @Override
                                        public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                            addBookingPaymentAPI(true, paymentMethod, orderRef, paidPrice, walletPaid, cardPaid);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            selectedIndex = index;
                            actionSheet.dismiss();
                        }
                    }).show();
        }
    }

    private void cancelClicked() {
        if (bookingDetail == null) {
            return;
        }
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.dismiss))
                .setOtherButtonTitles(getResources().getString(R.string.cancel_booking), getString(R.string.cancel_game))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            OleCancelBookingDialog bookingDialog = new OleCancelBookingDialog(getContext());
                            bookingDialog.setDialogCallback(new OleCancelBookingDialog.CancelBookingDialogCallback() {
                                @Override
                                public void enteredNote(String note) {
                                    cancelConfirmBookingAPI(true, "cancel", note);
                                }
                            });
                            bookingDialog.show();
                        }
                        else {
                            cancelMatchAPI(true);
                        }
                    }
                }).show();
    }

    private void confirmClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.confirm_booking))
                .setMessage(getResources().getString(R.string.do_you_want_to_confirm_booking))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelConfirmBookingAPI(true, "confirm", "");
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void previewClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OlePreviewFieldActivity.class);
            intent.putExtra("is_from_booking", true);
            intent.putExtra("is_captain", isCaptainAvailable);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("creator_id", bookingDetail.getCreatedBy().getId());
            Gson gson = new Gson();
            intent.putExtra("team", gson.toJson(bookingDetail.getGameTeams()));
            startActivity(intent);
        }

    }

    private void priceInfoClicked() {
        if (bookingDetail != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("BookingPriceDetailDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleBookingPriceDetailDialogFragment dialogFragment = new OleBookingPriceDetailDialogFragment(bookingDetail.getBookingPrice(), bookingDetail.getMatchFee(), bookingDetail.getCurrency());
            dialogFragment.show(fragmentTransaction, "BookingPriceDetailDialogFragment");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 109) {
                String str = data.getExtras().getString("team");
                Gson gson = new Gson();
                OleGameTeam oleGameTeam = gson.fromJson(str, OleGameTeam.class);
                bookingDetail.setGameTeams(oleGameTeam);
                checkTeamCreatedOrNot();
            }
            else if (requestCode == 110) {
                boolean result = data.getExtras().getBoolean("add_to_game");
                if (result) {
                    if (isCaptainAvailable) {
                        saveData("", "add_player_to_game");
                    }
                }
            }
        }
    }

    private void gotoGameHistory(OlePlayerInfo olePlayerInfo) {
        Intent intent = new Intent(getContext(), OleGameHistoryActivity.class);
        intent.putExtra("match_type", bookingDetail.getBookingType());
        intent.putExtra("booking_id", bookingId);
        intent.putExtra("req_status", bookingDetail.getRequestStatus());
        intent.putExtra("player_id", olePlayerInfo.getId());
        intent.putExtra("player_confirmed", olePlayerInfo.getPlayerConfirmed());
        intent.putExtra("booking_status", bookingDetail.getBookingStatus());
        intent.putExtra("creator_id", "");
        intent.putExtra("match_detail", false);
        intent.putExtra("booking_detail", true);
        startActivity(intent);
    }

    private void populateData() throws ParseException {
        if (bookingDetail == null) {
            return;
        }
        binding.tvClubName.setText(bookingDetail.getClubName());
        binding.tvCity.setText(bookingDetail.getCity());
        binding.tvFieldName.setText(String.format("%s (%s)", bookingDetail.getFieldName(), bookingDetail.getFieldSize()));
        binding.tvTime.setText(String.format("%s (%s)", bookingDetail.getBookingTime().split("-")[0], bookingDetail.getDuration()));
        binding.tvInvoice.setText(bookingDetail.getInvoiceNo());
        binding.tvNote.setText(bookingDetail.getNote());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = dateFormat.parse(bookingDetail.getBookingDate());
        dateFormat.applyPattern("EEEE, dd/MM/yyyy");
        binding.tvDate.setText(dateFormat.format(date));
        binding.tvPhone.setText(bookingDetail.getCreatedBy().getPhone());
        binding.tvPaidAmount.setText(String.format("%s %s", bookingDetail.getPaidAmount(), bookingDetail.getCurrency()));
        binding.tvUnpaidAmount.setText(String.format("%s %s", bookingDetail.getUnpaidAmount(), bookingDetail.getCurrency()));

        if (bookingDetail.getPaymentStatus().equalsIgnoreCase("1")) {
            binding.tvPaymentStatus.setText(R.string.paid);
            binding.btnPay.setVisibility(View.GONE);
            binding.tvPaymentStatus.setTextColor(getResources().getColor(R.color.greenColor));
        }
        else {
            binding.tvPaymentStatus.setText(R.string.unpaid);
            binding.tvPaymentStatus.setTextColor(Color.parseColor("#ff9f00"));
            if (bookingDetail.getClubPaymentMethod().equalsIgnoreCase("cash")) {
                binding.btnPay.setVisibility(View.GONE);
            }
            else {
                binding.btnPay.setVisibility(View.VISIBLE);
            }
        }

        if (bookingDetail.getPlayersGroup() != null) {
            binding.groupVu.setVisibility(View.VISIBLE);
            binding.tvGroupName.setText(String.format("%s: %s", getString(R.string.group), bookingDetail.getPlayersGroup().getName()));
        }
        else {
            binding.groupVu.setVisibility(View.GONE);
        }

        if (binding.tabLayout.getSelectedTabPosition() == 0) {
            binding.relChat.setVisibility(View.VISIBLE);
            setBadgeValue(bookingDetail.getUnreadChatCount());
            binding.btnEdit.setVisibility(View.GONE);
        }

        binding.favSwitch.setChecked(bookingDetail.getShowToFavorites().equalsIgnoreCase("1"));

        binding.favSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                favSwitchChanged();
            }
        });

        playerList.clear();
        playerList.addAll(bookingDetail.getJoinedPlayers());
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                playerList.remove(i);
                break;
            }
        }

        binding.tvJoinedCount.setText(String.valueOf(playerList.size()));
        binding.tvRequestCount.setText(String.valueOf(bookingDetail.getRequestedPlayers().size()));

        // create team
        checkTeamCreatedOrNot();

        if (bookingDetail.getMatchFee().equalsIgnoreCase("0")) {
            binding.tvPrice.setText(String.format("%s %s", bookingDetail.getBookingPrice(), bookingDetail.getCurrency()));
        }
        else {
            double total = Double.parseDouble(bookingDetail.getBookingPrice()) + Double.parseDouble(bookingDetail.getMatchFee());
            binding.tvPrice.setText(String.format("%s %s", total, bookingDetail.getCurrency()));
        }

        if (bookingDetail.getJoiningFee().equalsIgnoreCase("0")) {
            binding.joinFeeVu.setVisibility(View.GONE);
        }
        else {
            binding.joinFeeVu.setVisibility(View.VISIBLE);
            binding.tvJoinFee.setText(String.format("%s %s", bookingDetail.getJoiningFee(), bookingDetail.getCurrency()));
        }

        clubFacilities.clear();
        clubFacilities.addAll(bookingDetail.getFacilities());
        oleFacilityAdapter.notifyDataSetChanged();

        if (clubFacilities.size() == 0) {
            binding.tvFacility.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvFacility.setVisibility(View.GONE);
        }

        if (!bookingDetail.getClubLatitude().equalsIgnoreCase("") && !bookingDetail.getClubLongitude().equalsIgnoreCase("")) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            String url = "https://maps.google.com/maps/api/staticmap?center=" + bookingDetail.getClubLatitude() + "," + bookingDetail.getClubLongitude() + "&zoom=16&size="+width+"x300&sensor=false&key="+getString(R.string.maps_api_key);
            Glide.with(getContext()).load(url).into(binding.mapVu);
        }

        binding.btnConfirm.setVisibility(View.GONE);
        binding.btnAddFac.setVisibility(View.GONE);
        if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kPendingBooking)) {
            binding.tvStatus.setText(R.string.pending);
            binding.tvStatus.setTextColor(Color.parseColor("#ff9f00"));
            binding.btns.setVisibility(View.VISIBLE);
            binding.btnConfirm.setVisibility(View.VISIBLE);
            binding.btnAddFac.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kConfirmedByPlayerBooking)) {
            binding.tvStatus.setText(R.string.confirm_by_player);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.btns.setVisibility(View.VISIBLE);
            binding.btnAddFac.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kConfirmedByOwnerBooking)) {
            binding.tvStatus.setText(R.string.confirm_by_owner);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.btns.setVisibility(View.VISIBLE);
            binding.btnAddFac.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            binding.tvStatus.setText(R.string.finished);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.btns.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking)) {
            binding.tvStatus.setText(R.string.cancel_by_player);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking)) {
            binding.tvStatus.setText(R.string.cancel_by_owner);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
            binding.tvStatus.setText(R.string.blocked);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kExpiredBooking)) {
            binding.tvStatus.setText(R.string.expired);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
        }

        if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            binding.noteVu.setVisibility(View.VISIBLE);
            binding.invoiceVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.noteVu.setVisibility(View.GONE);
            binding.invoiceVu.setVisibility(View.GONE);
        }

    }

    private void checkTeamCreatedOrNot() {
        if (bookingDetail == null) { return; }
        if (bookingDetail.getGameTeams().isEmpty()) {
            binding.noTeamVu.setVisibility(View.VISIBLE);
            binding.teamVu.setVisibility(View.INVISIBLE);
        }
        else {
            binding.noTeamVu.setVisibility(View.GONE);
            binding.teamVu.setVisibility(View.VISIBLE);

            binding.boardVu.setSnapToColumnsWhenScrolling(true);
            binding.boardVu.setSnapToColumnWhenDragging(true);
            binding.boardVu.setSnapDragItemToTouch(true);
            binding.boardVu.setSnapToColumnInLandscape(false);
            binding.boardVu.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
            binding.boardVu.setBoardListener(boardListener);
            binding.boardVu.setBoardCallback(boardCallback);

            OleGameTeam oleGameTeam = bookingDetail.getGameTeams();
            binding.tvTeamA.setText(oleGameTeam.getTeamAName());
            binding.tvTeamB.setText(oleGameTeam.getTeamBName());
            binding.vuColorA.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamAColor()));
            binding.vuColorB.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamBColor()));
            allPlayerList.clear();
            allPlayerList.addAll(bookingDetail.getJoinedPlayers());
            allPlayerList.addAll(bookingDetail.getMaunalPlayers());
            teamAList.clear();
            teamAList.addAll(oleGameTeam.getTeamAPlayers());
            teamBList.clear();
            teamBList.addAll(oleGameTeam.getTeamBPlayers());
            Iterator<OlePlayerInfo> iterator = allPlayerList.iterator();
            while (iterator.hasNext()) {
                OlePlayerInfo info = iterator.next();
                int index = checkPlayerExistInTeamA(info.getId());
                if (index != -1) {
                    iterator.remove();
                }
                index = checkPlayerExistInTeamB(info.getId());
                if (index != -1) {
                    iterator.remove();
                }
            }
            checkCaptainAvailable();
            showHideTeamPlaceholder();
            if (binding.formationInfoVu.getVisibility() == View.VISIBLE) {
                // if formation vu is showing then need it
                setupBoard();
            }
        }
    }

    private void setupBoard() {
        binding.boardVu.clearBoard();
        binding.boardVu.setCustomDragItem(new OleTeamDragItem(getContext() , R.layout.oleteam_player));
        binding.boardVu.setCustomColumnDragItem(null);
        binding.boardVu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.boardVu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int boardWidth = binding.boardVu.getWidth();
                if (boardWidth<=0) {
                    return;
                }
                int width = boardWidth -(int)getResources().getDimension(R.dimen._10sdp);
                binding.boardVu.setColumnWidth(width/3);
                addTeamAList();
                addAllPlayerList();
                addTeamBList();
            }
        });
    }

    BoardView.BoardListener boardListener = new BoardView.BoardListenerAdapter() {
        @Override
        public void onItemChangedColumn(int oldColumn, int newColumn) {

        }

        @Override
        public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
            if (fromColumn != toColumn) {
                OleGameTeam oleGameTeam = bookingDetail.getGameTeams();
                OlePlayerInfo olePlayerInfo = (OlePlayerInfo) binding.boardVu.getAdapter(toColumn).getItemList().get(toRow);
                if (olePlayerInfo != null) {
                    if (toColumn == 0) {
                        // team A
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), oleGameTeam.getTeamAId(), olePlayerInfo.getId(), "add");
                    } else if (toColumn == 2) {
                        // team B
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), oleGameTeam.getTeamBId(), olePlayerInfo.getId(), "add");
                    } else if (toColumn == 1) {
                        // no team
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), "", olePlayerInfo.getId(), "remove");
                    }
                }
                showHideTeamPlaceholder();
            }
        }
    };

    BoardView.BoardCallback boardCallback = new BoardView.BoardCallback() {
        @Override
        public boolean canDragItemAtPosition(int column, int row) {
            if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                return false;
            }
            if (isCaptainAvailable && column == 2) {
                return false;
            }
            else if (isCaptainAvailable && column == 0 && teamAList.get(row).getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                // user cannot remove self from team if captain is there
                return false;
            }
            else {
                // if my turn then item can be draggable
                return isMyTurn;
            }
        }

        @Override
        public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
            if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                return false;
            }
            if (isCaptainAvailable && newColumn == 2) {
                return false;
            }
            else {
                // if my turn then item can be dropable
                return isMyTurn;
            }
        }
    };

    private void addAllPlayerList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), allPlayerList, R.layout.oleteam_player, R.id.item_layout, true, 1);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    private void addTeamAList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), teamAList, R.layout.oleteam_player, R.id.item_layout, true, 0);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    private void addTeamBList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), teamBList, R.layout.oleteam_player, R.id.item_layout, true, 2);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    OleTeamAdapter.OnItemClickListener teamItemClickListener = new OleTeamAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int pos, int columnIndex) {
            if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                return;
            }
            if (columnIndex == 2) { //team B
                OlePlayerInfo info = teamBList.get(pos);
                if (info.getId().equalsIgnoreCase(captainId)) {
                    removeCaptain(pos);
                }
            }
            else if (columnIndex == 1) { // all player
                OlePlayerInfo info = allPlayerList.get(pos);
                if (info.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    return;
                }
                if (info.getType() != null && info.getType().equalsIgnoreCase(Constants.kManualPlayer)) {
                    removeFromGame(pos);
                }
                else {
                    makeCaptain(pos);
                }
            }
        }
    };

    private void removeCaptain(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.remove_captain))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            addRemoveCaptainAPI(true, bookingDetail.getGameTeams().getTeamBId(), teamBList.get(pos).getId(), "0", pos);
                        }
                    }
                }).show();
    }

    private void makeCaptain(int pos) {
        String[] titles;
        if (!isCaptainAvailable) {
            titles = new String[2];
            titles[0] = getResources().getString(R.string.make_captain);
            titles[1] = getResources().getString(R.string.profile);
        }
        else {
            titles = new String[1];
            titles[0] = getResources().getString(R.string.profile);
        }
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(titles)
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    int ind = 0;
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                        if (!isCancel) {
                            if (!isCaptainAvailable) {
                                if (ind == 0) {
                                    if (teamAList.size() > 0 || teamBList.size() > 0) {
                                        Functions.showToast(getContext(), getString(R.string.remove_player_before_make_captain), FancyToast.ERROR);
                                    } else {
                                        addRemoveCaptainAPI(true, bookingDetail.getGameTeams().getTeamBId(), allPlayerList.get(pos).getId(), "1", pos);
                                    }
                                } else {
                                    gotoGameHistory(allPlayerList.get(pos));
                                }
                            } else {
                                gotoGameHistory(allPlayerList.get(pos));
                            }
                        }
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        actionSheet.dismiss();
                        ind = index;
                    }
                }).show();
    }

    private void removeFromGame(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.remove_from_game))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            removePlayerFromGame(true, allPlayerList.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    private void checkCaptainAvailable() {
        for (OlePlayerInfo info : teamBList) {
            if (info.getIsCaptain().equalsIgnoreCase("1")) {
                isCaptainAvailable = true;
                captainId = info.getId();
                if (!bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                    observeChange();
                }
                break;
            }
        }
    }

    private int checkPlayerExistInTeamA(String id) {
        int result = -1;
        for (int i = 0; i < teamAList.size(); i++) {
            if (teamAList.get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private int checkPlayerExistInTeamB(String id) {
        int result = -1;
        for (int i = 0; i < teamBList.size(); i++) {
            if (teamBList.get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void showHideTeamPlaceholder() {
        if (teamAList.size() > 0) {
            binding.teamAPlace.setVisibility(View.GONE);
        }
        else {
            binding.teamAPlace.setVisibility(View.VISIBLE);
        }
        if (teamBList.size() > 0) {
            binding.teamBPlace.setVisibility(View.GONE);
        }
        else {
            binding.teamBPlace.setVisibility(View.VISIBLE);
        }
    }

    private void getBookingDetail(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerBookingDetail(Functions.getAppLang(getContext()), bookingId, Functions.getPrefValue(getContext(), Constants.kUserID));
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
                            bookingDetail = gson.fromJson(obj.toString(), OlePlayerBookingList.class);
                            populateData();
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

    private void addBookingPaymentAPI(boolean isLoader, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addBookingPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), orderRef, paymentMethod, paidPrice, walletPaid, cardPaid, bookingId, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            getBookingDetail(true);
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

    private void cancelConfirmBookingAPI(boolean isLoader, String status, String note) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelConfirmBooking(Functions.getAppLang(getContext()), bookingId, status, note, "", "", "", "", Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (status.equalsIgnoreCase("confirm")) {
                                bookingDetail.setBookingStatus(Constants.kConfirmedByPlayerBooking);
                            }
                            else{
                                bookingDetail.setBookingStatus(Constants.kCancelledByPlayerBooking);
                            }
                            populateData();
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

    private void cancelMatchAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.matchToBooking(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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

    private void addRemovePlayerToTeam(boolean isLoader, String teamAId, String teamBId, String targetTeamId, String playerId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addRemovePlayerFromTeam(Functions.getAppLang(getContext()), teamAId, teamBId, targetTeamId, playerId, "0", type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isLoader) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            }
                            if (isCaptainAvailable) {
                                saveData(playerId, type);
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

    private void addRemoveCaptainAPI(boolean isLoader, String teamId, String playerId, String status, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addRemoveCaptain(Functions.getAppLang(getContext()), teamId, playerId, status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (status.equalsIgnoreCase("1")) {
                                allPlayerList.get(pos).setIsCaptain("1");
                                // add captain to team B and remove it from all players
                                teamBList.add(allPlayerList.get(pos));
                                allPlayerList.remove(pos);
                                // add creator to team A as captain and remove it from all players
                                int index = findCreatorObj();
                                OlePlayerInfo creator = allPlayerList.get(index);
                                creator.setIsCaptain("1");
                                teamAList.add(creator);
                                allPlayerList.remove(index);
                                addRemovePlayerToTeam(false, bookingDetail.getGameTeams().getTeamAId(), bookingDetail.getGameTeams().getTeamBId(), bookingDetail.getGameTeams().getTeamAId(), creator.getId(), "add");
                                saveData(playerId, "captain_created");
                                showPlayerChooseAlert();
                                checkCaptainAvailable();
                                binding.boardVu.getAdapter(0).notifyDataSetChanged();
                                binding.boardVu.getAdapter(1).notifyDataSetChanged();
                                binding.boardVu.getAdapter(2).notifyDataSetChanged();
                                showHideTeamPlaceholder();
                            }
                            else {
                                getBookingDetail(true);
                                isCaptainAvailable = false;
                                captainId = "";
                                saveData("", "captain_removed");
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

    private void hideShowFavAPI(boolean isLoader, String isFav) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showHideFav(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, isFav);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            bookingDetail.setShowToFavorites(isFav);
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

    private void removePlayerFromGame(boolean isLoader, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removePlayerFromGame(Functions.getAppLang(getContext()), bookingId, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            allPlayerList.remove(pos);
                            binding.boardVu.getAdapter(1).notifyDataSetChanged();
                            if (isCaptainAvailable) {
                                saveData(playerId, "remove_player_from_game");
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

    private void showPlayerChooseAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("")
                .setMessage(getResources().getString(R.string.do_you_want_captain_choose_player))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveData("", "captain_choose");
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveData("", "creator_choose");
                    }
                }).create();
        builder.show();
    }

    private int findCreatorObj() {
        int result = -1;
        for (int i = 0; i < allPlayerList.size(); i++) {
            if (allPlayerList.get(i).getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void saveData(String playerId, String type) {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("turn", "creator");
        map.put("type", type);
        map.put("player_id", playerId);
        map.put("booking_id", bookingId);
        databaseReference.child("match").child(bookingId).setValue(map);
        if (type.equalsIgnoreCase("captain_removed") || allPlayerList.size() == 0) {
            isMyTurn = true;
            binding.tvTurn.setVisibility(View.GONE);
        }
        else if (type.equalsIgnoreCase("captain_choose")) {
            isMyTurn = false;
            binding.tvTurn.setText(R.string.captain_turn);
        }
        else if (type.equalsIgnoreCase("creator_choose")) {
            isMyTurn = true;
            binding.tvTurn.setText(R.string.your_turn);
        }
        else if (type.equalsIgnoreCase("add_player_to_game")) {
            isMyTurn = true;
            binding.tvTurn.setVisibility(View.VISIBLE);
            binding.tvTurn.setText(R.string.your_turn);
        }
        else {
            isMyTurn = false;
            binding.tvTurn.setVisibility(View.VISIBLE);
            binding.tvTurn.setText(R.string.captain_turn);
        }
    }

    private void observeChange() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        databaseHandle = databaseReference.child("match").child(bookingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.tvTurn.setVisibility(View.GONE);
                    return;
                }

                try {
                    OleDataModel object = snapshot.getValue(OleDataModel.class);
                    if (object!=null) {
                        String bookId = object.getBooking_id();
                        if (!bookId.equalsIgnoreCase(bookingId)) {
                            binding.tvTurn.setVisibility(View.GONE);
                            return;
                        }
                        String type = object.getType();
                        String turn = object.getTurn();
                        if (turn.equalsIgnoreCase("captain")) {
                            if (isCaptainAvailable) {
                                binding.tvTurn.setVisibility(View.VISIBLE);
                                binding.tvTurn.setText(R.string.your_turn);
                                if (allPlayerList.size() == 0) {
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                            }
                            else {
                                binding.tvTurn.setVisibility(View.GONE);
                            }
                            if (type.equalsIgnoreCase("add")) {
                                String playerId = object.getPlayer_id();
                                // captain can just add player in team B, so here need to add this player in team B for sync
                                for (int i = 0; i < allPlayerList.size(); i++) {
                                    OlePlayerInfo info = allPlayerList.get(i);
                                    if (info.getId().equalsIgnoreCase(playerId)) {
                                        teamBList.add(info);
                                        allPlayerList.remove(i);
                                        binding.boardVu.getAdapter(1).notifyDataSetChanged();
                                        binding.boardVu.getAdapter(2).notifyDataSetChanged();
                                        isMyTurn = true;
                                        if (allPlayerList.size() == 0) {
                                            binding.tvTurn.setVisibility(View.GONE);
                                        }
                                        break;
                                    }
                                }
                            }
                            if (type.equalsIgnoreCase("remove")) {
                                String playerId = object.getPlayer_id();
                                // captain can just remove player from team B, so here need to remove this player from team B for sync
                                for (int i = 0; i < teamBList.size(); i++) {
                                    OlePlayerInfo info = teamBList.get(i);
                                    if (info.getId().equalsIgnoreCase(playerId)) {
                                        allPlayerList.add(info);
                                        teamBList.remove(i);
                                        binding.boardVu.getAdapter(1).notifyDataSetChanged();
                                        binding.boardVu.getAdapter(2).notifyDataSetChanged();
                                        isMyTurn = true;
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            if (allPlayerList.size() == 0) {
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