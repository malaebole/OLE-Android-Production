package ae.oleapp.padel;

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
import ae.oleapp.databinding.OleactivityPadelMatchBookingDetailBinding;
import ae.oleapp.dialogs.OleBookingPriceDetailDialogFragment;
import ae.oleapp.dialogs.OleCancelBookingDialog;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OleUpdateFacilityBottomDialog;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.player.OleReceivedRequestActivity;
import ae.oleapp.player.OlePadelMatchShareActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePadelMatchBookingDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelMatchBookingDetailBinding binding;
    private String bookingId = "";
    private OlePlayerBookingList bookingDetail;
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private OleFacilityAdapter oleFacilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelMatchBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
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
        binding.tvFacility.setVisibility(View.GONE);
        binding.relCall.setVisibility(View.GONE);
        binding.coverVu.setVisibility(View.VISIBLE);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.relCall.setOnClickListener(this);
        binding.btnAddFac.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.receivedRequestVu.setOnClickListener(this);
        binding.btnChnagePartner.setOnClickListener(this);
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
        else if (v == binding.btnPriceInfo) {
            priceInfoClicked();
        }
        else if (v == binding.myProfileVu) {
            gotoProfile(bookingDetail.getCreatedBy().getId());
        }
        else if (v == binding.myPartnerProfileVu) {
            gotoProfile(bookingDetail.getCreatorPartner().getId());
        }
        else if (v == binding.opponentProfileVu) {
            gotoProfile(bookingDetail.getPlayerTwo().getId());
        }
        else if (v == binding.opponentPartnerProfileVu) {
            gotoProfile(bookingDetail.getPlayerTwoPartner().getId());
        }
        else if (v == binding.btnChnagePartner) {
            Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
            intent.putExtra("is_selection", true);
            intent.putExtra("is_single_selection", true);
            startActivityForResult(intent, 106);
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void shareClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OlePadelMatchShareActivity.class);
            intent.putExtra("club_name", bookingDetail.getClubName());
            intent.putExtra("date", bookingDetail.getBookingDate());
            intent.putExtra("time", bookingDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(bookingDetail.getCreatedBy()));
            intent.putExtra("player_one_partner", gson.toJson(bookingDetail.getCreatorPartner()));
            if (bookingDetail.getPlayerTwo() != null && !bookingDetail.getPlayerTwo().isEmpty()) {
                intent.putExtra("player_two", gson.toJson(bookingDetail.getPlayerTwo()));
            }
            if (bookingDetail.getPlayerTwoPartner() != null && !bookingDetail.getPlayerTwoPartner().isEmpty()) {
                intent.putExtra("player_two_partner", gson.toJson(bookingDetail.getPlayerTwoPartner()));
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
        String[] arr;
        if (bookingDetail.getPlayerTwo() != null && !bookingDetail.getPlayerTwo().isEmpty()) {
            arr = new String[]{getResources().getString(R.string.cancel_booking), getString(R.string.cancel_match), getString(R.string.remove_opponent)};
        }
        else {
            arr = new String[]{getResources().getString(R.string.cancel_booking), getString(R.string.cancel_match)};
        }
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.dismiss))
                .setOtherButtonTitles(arr)
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (bookingDetail.getPlayerTwo() != null && !bookingDetail.getPlayerTwo().isEmpty()) {
                            if (index == 0) {
                                cancelBooking();
                            }
                            else if (index == 1) {
                                cancelMatchAPI(true);
                            }
                            else {
                                removeOpponentAPI(true, bookingDetail.getPlayerTwo().getId());
                            }
                        }
                        else {
                            if (index == 0) {
                                cancelBooking();
                            }
                            else {
                                cancelMatchAPI(true);
                            }
                        }
                    }
                }).show();
    }

    private void cancelBooking() {
        OleCancelBookingDialog bookingDialog = new OleCancelBookingDialog(getContext());
        bookingDialog.setDialogCallback(new OleCancelBookingDialog.CancelBookingDialogCallback() {
            @Override
            public void enteredNote(String note) {
                cancelConfirmBookingAPI(true, "cancel", note);
            }
        });
        bookingDialog.show();
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
        }

//        Intent intent = new Intent(getContext(), ChatActivity.class);
//        intent.putExtra("booking_id", bookingDetail.getBookingId());
//        intent.putExtra("booking_status", bookingDetail.getBookingStatus());
//        intent.putExtra("booking_type", bookingDetail.getBookingType());
//        intent.putExtra("is_match_detail", false);
//        Gson gson = new Gson();
//        intent.putExtra("join_players", gson.toJson(playerList));
//        intent.putExtra("p1_info", gson.toJson(bookingDetail.getCreatedBy()));
//        if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge) && bookingDetail.getJoinedPlayers().size() > 0) {
//            intent.putExtra("p2_info", gson.toJson(bookingDetail.getJoinedPlayers().get(0)));
//        }
//        else if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge)) {
//            intent.putExtra("p2_info", gson.toJson(playerList.get(0)));
//        }
//        startActivity(intent);
    }

    private void recRequestClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OleReceivedRequestActivity.class);
            intent.putExtra("match_type", "");
            intent.putExtra("booking_id", bookingDetail.getBookingId());
            intent.putExtra("req_status", "");
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

    private void populateData() throws ParseException {
        if (bookingDetail == null) {
            return;
        }
        binding.coverVu.setVisibility(View.GONE);
        binding.tvClubName.setText(bookingDetail.getClubName());
        binding.tvCity.setText(bookingDetail.getCity());
        binding.tvFieldName.setText(bookingDetail.getFieldName());
        binding.tvTime.setText(String.format("%s (%s)", bookingDetail.getBookingTime().split("-")[0], bookingDetail.getDuration()));
        binding.tvInvoice.setText(bookingDetail.getInvoiceNo());
        binding.tvNote.setText(bookingDetail.getNote());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = dateFormat.parse(bookingDetail.getBookingDate());
        dateFormat.applyPattern("EEEE, dd/MM/yyyy");
        binding.tvDate.setText(dateFormat.format(date));
        binding.tvSkillLevel.setText(bookingDetail.getSkillLevel());
        binding.tvPaidAmount.setText(String.format("%s %s", bookingDetail.getPaidAmount(), bookingDetail.getCurrency()));
        binding.tvUnpaidAmount.setText(String.format("%s %s", bookingDetail.getUnpaidAmount(), bookingDetail.getCurrency()));
        binding.tvRequestCount.setText(bookingDetail.getRequestedPlayersCount());

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

        binding.myProfileVu.populateData(bookingDetail.getCreatedBy().getNickName(), bookingDetail.getCreatedBy().getPhotoUrl(), bookingDetail.getCreatedBy().getLevel(), true);
        binding.myProfileVu.setOnClickListener(this);
        binding.myPartnerProfileVu.populateData(bookingDetail.getCreatorPartner().getNickName(), bookingDetail.getCreatorPartner().getPhotoUrl(), bookingDetail.getCreatorPartner().getLevel(), true);
        binding.myPartnerProfileVu.setOnClickListener(this);
        if (bookingDetail.getPlayerTwo() != null && !bookingDetail.getPlayerTwo().isEmpty()) {
            binding.opponentProfileVu.populateData(bookingDetail.getPlayerTwo().getNickName(), bookingDetail.getPlayerTwo().getPhotoUrl(), bookingDetail.getPlayerTwo().getLevel(), true);
            binding.opponentProfileVu.setOnClickListener(this);
        }
        else {
            binding.opponentProfileVu.populateData("?", "", null, false);
        }
        if (bookingDetail.getPlayerTwoPartner() != null && !bookingDetail.getPlayerTwoPartner().isEmpty()) {
            binding.opponentPartnerProfileVu.populateData(bookingDetail.getPlayerTwoPartner().getNickName(), bookingDetail.getPlayerTwoPartner().getPhotoUrl(), bookingDetail.getPlayerTwoPartner().getLevel(), true);
            binding.opponentPartnerProfileVu.setOnClickListener(this);
        }
        else {
            binding.opponentPartnerProfileVu.populateData("?", "", null, false);
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
            binding.btnChnagePartner.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kConfirmedByPlayerBooking)) {
            binding.tvStatus.setText(R.string.confirm_by_player);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.btns.setVisibility(View.VISIBLE);
            binding.btnAddFac.setVisibility(View.VISIBLE);
            binding.btnChnagePartner.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kConfirmedByOwnerBooking)) {
            binding.tvStatus.setText(R.string.confirm_by_owner);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.btns.setVisibility(View.VISIBLE);
            binding.btnAddFac.setVisibility(View.VISIBLE);
            binding.btnChnagePartner.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            binding.tvStatus.setText(R.string.finished);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.btns.setVisibility(View.GONE);
            binding.btnChnagePartner.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking)) {
            binding.tvStatus.setText(R.string.cancel_by_player);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
            binding.btnChnagePartner.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking)) {
            binding.tvStatus.setText(R.string.cancel_by_owner);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
            binding.btnChnagePartner.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
            binding.tvStatus.setText(R.string.blocked);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
            binding.btnChnagePartner.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingStatus().equalsIgnoreCase(Constants.kExpiredBooking)) {
            binding.tvStatus.setText(R.string.expired);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.btns.setVisibility(View.GONE);
            binding.btnChnagePartner.setVisibility(View.GONE);
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
            if (list.size() > 0) {
                changePartnerAPI(true, list.get(0), bookingDetail.getCreatorPartner().getId());
            }
        }
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
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelMatch(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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

    private void removeOpponentAPI(boolean isLoader, String playerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelAcceptedChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId);
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

    private void changePartnerAPI(boolean isLoader, OlePlayerInfo partner, String oldPartnerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.changePartner(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, partner.getId(), oldPartnerId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            bookingDetail.setCreatorPartner(partner);
                            binding.myPartnerProfileVu.populateData(bookingDetail.getCreatorPartner().getNickName(), bookingDetail.getCreatorPartner().getPhotoUrl(), bookingDetail.getCreatorPartner().getLevel(), true);
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