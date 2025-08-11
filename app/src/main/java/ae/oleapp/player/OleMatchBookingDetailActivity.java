package ae.oleapp.player;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityMatchBookingDetailBinding;
import ae.oleapp.dialogs.OleBookingPriceDetailDialogFragment;
import ae.oleapp.dialogs.OleCancelBookingDialog;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OleUpdateFacilityBottomDialog;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleMatchBookingDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityMatchBookingDetailBinding binding;
    private String bookingId = "";
    private OlePlayerBookingList bookingDetail;
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private OleFacilityAdapter oleFacilityAdapter;
    private final boolean isInvitationAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMatchBookingDetailBinding.inflate(getLayoutInflater());
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

        binding.btns.setVisibility(View.GONE);
        binding.btnVisible.setVisibility(View.GONE);
        binding.relProfile1.setVisibility(View.GONE);
        binding.relProfile2.setVisibility(View.GONE);
        binding.tvFacility.setVisibility(View.GONE);
        binding.relCall.setVisibility(View.GONE);
        binding.toolbarBadge.setVisibility(View.GONE);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.relCall.setOnClickListener(this);
        binding.btnAddFac.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.receivedRequestVu.setOnClickListener(this);
        binding.joinedPlayerVu.setOnClickListener(this);
        binding.btnVisible.setOnClickListener(this);
        binding.btnCall1.setOnClickListener(this);
        binding.btnCall2.setOnClickListener(this);
        binding.btnPriceInfo.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.mapVu) {
            locationVuClicked();
        }
        else if (v == binding.btnPay) {
            payClicked();
        }
        else if (v == binding.relCall) {
            phoneClicked();
        }
        else if (v == binding.btnAddFac) {
            addFacClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
        else if (v == binding.btnConfirm) {
            confirmClicked();
        }
        else if (v == binding.btnChat) {
            chatClicked();
        }
        else if (v == binding.receivedRequestVu) {
            recRequestClicked();
        }
        else if (v == binding.joinedPlayerVu) {
            joinedPlayerClicked();
        }
        else if (v == binding.btnVisible) {
            visibleClicked();
        }
        else if (v == binding.btnCall1) {
            p1CallClicked();
        }
        else if (v == binding.btnCall2) {
            p2CallClicked();
        }
        else if (v == binding.btnPriceInfo) {
            priceInfoClicked();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void shareClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleFootballMatchShareActivity.class);
            intent.putExtra("club_name", bookingDetail.getClubName());
            intent.putExtra("date", bookingDetail.getBookingDate());
            intent.putExtra("time", bookingDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(bookingDetail.getCreatedBy()));
            if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge) && bookingDetail.getJoinedPlayers().size() > 0) {
                intent.putExtra("player_two", gson.toJson(bookingDetail.getJoinedPlayers().get(0)));
            }
            else if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge) && playerList.size() > 0) {
                intent.putExtra("player_two", gson.toJson(playerList.get(0)));
            }
            startActivity(intent);
        }
    }

    private void locationVuClicked() {
        if (bookingDetail != null) {
            String uri = "http://maps.google.com/maps?daddr=" + bookingDetail.getClubLatitude() + "," + bookingDetail.getClubLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
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

    private void phoneClicked() {
        if (bookingDetail != null && (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge) || bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge))) {
            if (!binding.tvPhone.getText().toString().isEmpty()) {
                makeCall(binding.tvPhone.getText().toString());
            }
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

    private void cancelClicked() {
        if (bookingDetail == null) {
            return;
        }
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.dismiss))
                .setOtherButtonTitles(getResources().getString(R.string.cancel_booking), getString(R.string.cancel_match))
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

    private void recRequestClicked() {
        if (bookingDetail != null) {
            if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
                Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
                intent.putExtra("is_selection", true);
                startActivityForResult(intent, 106);
            }
            else {
                Intent intent = new Intent(getContext(), OleReceivedRequestActivity.class);
                intent.putExtra("match_type", bookingDetail.getBookingType());
                intent.putExtra("booking_id", bookingDetail.getBookingId());
                intent.putExtra("req_status", bookingDetail.getRequestStatus());
                startActivity(intent);
            }
        }
    }

    private void joinedPlayerClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleJoinedPlayersActivity.class);
            intent.putExtra("match_type", bookingDetail.getBookingType());
            intent.putExtra("booking_id", bookingDetail.getBookingId());
            intent.putExtra("req_status", bookingDetail.getRequestStatus());
            intent.putExtra("booking_status", bookingDetail.getBookingStatus());
            intent.putExtra("rem_amount", "");
            intent.putExtra("currency", "");
            intent.putExtra("matchCreatedId", "");
            intent.putExtra("isFromMatchDetail", false);
            startActivity(intent);
        }
    }

    private void visibleClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleCreateMatchActivity.class);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("club_id", bookingDetail.getClubId());
            intent.putExtra("is_update", true);
            startActivity(intent);
        }
    }

    private void p1CallClicked() {
        if (bookingDetail != null) {
            makeCall(bookingDetail.getCreatedBy().getPhone());
        }
    }

    private void p2CallClicked() {
        if (bookingDetail != null && bookingDetail.getJoinedPlayers().size() > 0) {
            makeCall(bookingDetail.getJoinedPlayers().get(0).getPhone());
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
        binding.btnVisible.setVisibility(View.GONE);
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

        if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
            if (bookingDetail.getJoinedPlayers().size() > 0) {
                // player accepted challenge
                binding.joinedPlayerVu.setVisibility(View.GONE);
                binding.receivedRequestVu.setVisibility(View.GONE);
                binding.relProfile1.setVisibility(View.VISIBLE);
                binding.relProfile2.setVisibility(View.VISIBLE);
                binding.profileVu1.populateData(bookingDetail.getCreatedBy().getNickName(), bookingDetail.getCreatedBy().getPhotoUrl(), bookingDetail.getCreatedBy().getLevel(), true);
                binding.profileVu1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoProfile(bookingDetail.getCreatedBy().getId());
                    }
                });

                playerList.clear();
                playerList.addAll(bookingDetail.getJoinedPlayers());
                OlePlayerInfo player2 = bookingDetail.getJoinedPlayers().get(0);
                binding.profileVu2.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
                binding.profileVu2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoProfile(player2.getId());
                    }
                });
            }
            else {
                // player not accepted challenge
                binding.joinedPlayerVu.setVisibility(View.VISIBLE);
                binding.receivedRequestVu.setVisibility(View.VISIBLE);
                binding.btnVisible.setVisibility(View.VISIBLE);
                binding.tvJoinedPlayer.setText(R.string.invited_players);
                binding.tvReceiveReq.setText(R.string.invite_player);
                binding.relProfile1.setVisibility(View.GONE);
                binding.relProfile2.setVisibility(View.GONE);
                playerList.clear();
                playerList.addAll(bookingDetail.getJoinedPlayers());
                binding.tvJoinedCount.setText(String.valueOf(bookingDetail.getRequestedPlayers().size()));
                binding.tvRequestCount.setText("");
            }
        }
        else {
            // public challenge
            binding.relProfile1.setVisibility(View.GONE);
            binding.relProfile2.setVisibility(View.GONE);
            playerList.clear();
            playerList.addAll(bookingDetail.getJoinedPlayers());
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    playerList.remove(i);
                    break;
                }
            }

            if (playerList.size() > 0) {
                binding.relCall.setVisibility(View.VISIBLE);
                binding.tvPhone.setText(playerList.get(0).getPhone());
                binding.receivedRequestVu.setVisibility(View.GONE);
            }
            binding.tvJoinedCount.setText(String.valueOf(bookingDetail.getJoinedPlayers().size()));
            binding.tvRequestCount.setText(String.valueOf(bookingDetail.getRequestedPlayers().size()));
        }

        setBadgeValue(bookingDetail.getUnreadChatCount());

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
            Glide.with(getApplicationContext()).load(url).into(binding.mapVu);
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

    private void gotoProfile(String pId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", pId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>() {
            }.getType());
            List<OlePlayerInfo> newList = new ArrayList<>();
            for (OlePlayerInfo info : list) {
                if (!isExistInPlayerList(info.getId())) {
                    newList.add(info);
                }
            }
            if (newList.size() > 0) {
                inviteMorePlayer(true, newList);
            }
        }
    }

    private boolean isExistInPlayerList(String id) {
        boolean result = false;
        for (OlePlayerInfo info : playerList) {
            if (info.getId().equalsIgnoreCase(id)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void getBookingDetail(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerBookingDetail(Functions.getAppLang(getContext()), bookingId, Functions.getPrefValue(getContext(), Constants.kUserID));
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

    private void cancelConfirmBookingAPI(boolean isLoader, String status, String note) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelConfirmBooking(Functions.getAppLang(getContext()), bookingId, status, note, "", "", "", "", Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
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
        call.enqueue(new Callback<>() {
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

    private void inviteMorePlayer(boolean isLoader, List<OlePlayerInfo> list) {
        String ids = "";
        for (OlePlayerInfo info: list) {
            if (ids.isEmpty()) {
                ids = info.getId();
            }
            else {
                ids = String.format("%s,%s", ids, info.getId());
            }
        }
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.inviteMorePlayers(Functions.getAppLang(getContext()), ids, bookingId, Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            playerList.addAll(list);
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
        call.enqueue(new Callback<>() {
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

}