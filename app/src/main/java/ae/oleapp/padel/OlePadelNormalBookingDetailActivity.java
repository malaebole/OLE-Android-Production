package ae.oleapp.padel;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import ae.oleapp.databinding.OleactivityPadelNormalBookingDetailBinding;
import ae.oleapp.dialogs.OleCancelBookingDialog;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.dialogs.OleUpdateFacilityBottomDialog;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePadelNormalBookingDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelNormalBookingDetailBinding binding;
    private String bookingId = "";
    private OlePlayerBookingList bookingDetail;
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private OleFacilityAdapter oleFacilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelNormalBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.booking_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
        }

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), clubFacilities, true);
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        binding.coverVu.setVisibility(View.VISIBLE);
        binding.bookingHolderVu.setVisibility(View.GONE);
        binding.invitationBtns.setVisibility(View.GONE);
        binding.btns.setVisibility(View.GONE);
        binding.tvFacility.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnAddFac.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.partnerVu.setOnClickListener(this);
        binding.btnReject.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBookingDetail(bookingDetail == null);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.mapVu) {
            locationVuClicked();
        }
        else if (v == binding.btnAddFac) {
            addFacClicked();
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
        else if (v == binding.partnerVu) {
            if (bookingDetail != null) {
                Intent intent = new Intent(getContext(), OlePartnerListActivity.class);
                Gson gson = new Gson();
                intent.putExtra("data", gson.toJson(bookingDetail));
                startActivity(intent);
            }
        }
        else if (v == binding.btnAccept) {
            acceptClicked();
        }
        else if (v == binding.btnReject) {
            rejectClicked();
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
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("UpdateFacilityBottomDialog");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleUpdateFacilityBottomDialog dialogFragment = new OleUpdateFacilityBottomDialog(bookingDetail.getClubFacilities(), bookingDetail.getFacilities(), bookingId);
            dialogFragment.setDialogCallback(new OleUpdateFacilityBottomDialog.UpdateFacilityBottomDialogCallback() {
                @Override
                public void didUpdateFacilities() {
                    getBookingDetail(bookingDetail == null);
                }
            });
            dialogFragment.show(fragmentTransaction, "UpdateFacilityBottomDialog");
        }
        else {
            Functions.showToast(getContext(), getResources().getString(R.string.you_can_update_fac_in_cash), FancyToast.ERROR);
        }
    }

    private void payClicked() {
        if (bookingDetail != null) {
            if (bookingDetail.getJoinedPlayers().size() > 0) {
                openPaymentDialog(bookingDetail.getBookingPrice(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", true, false, "0", "", bookingDetail.getClubId(), "0", new OlePaymentDialogFragment.PaymentDialogCallback() {
                    @Override
                    public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                        addBookingPaymentAPI(true, paymentMethod, orderRef, paidPrice, walletPaid, cardPaid);
                    }
                });
            }
            else {
                // pay full amount
                openPaymentDialog(bookingDetail.getBookingPrice(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", true, false, "0", "", bookingDetail.getClubId(), "1", new OlePaymentDialogFragment.PaymentDialogCallback() {
                    @Override
                    public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                        addBookingPaymentAPI(true, paymentMethod, orderRef, paidPrice, walletPaid, cardPaid);
                    }
                });
            }
        }
    }

    private void cancelClicked() {
        if (bookingDetail == null) {
            return;
        }
        if (bookingDetail.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (bookingDetail.getJoinedPlayers().size() > 0) {
                ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                        .setCancelButtonTitle(getResources().getString(R.string.cancel))
                        .setOtherButtonTitles(getResources().getString(R.string.cancel_booking), getResources().getString(R.string.remove_myself), getResources().getString(R.string.assign_booking_to_player))
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//
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
                                else if (index == 1) {
                                    List<OleSelectionList> oleSelectionList = new ArrayList<>();
                                    for (OlePlayerInfo info: bookingDetail.getJoinedPlayers()) {
                                        oleSelectionList.add(new OleSelectionList(info.getId(), info.getNickName()));
                                    }
                                    OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.your_partners), false);
                                    dialog.setLists(oleSelectionList);
                                    dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                                        @Override
                                        public void selectedItem(List<OleSelectionList> selectedItems) {
                                            OleSelectionList selectedItem = selectedItems.get(0);
                                            assignToOtherAPI(true, selectedItem.getId(), "team");
                                        }
                                    });
                                    dialog.show();
                                }
                                else {
                                    Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
                                    intent.putExtra("is_selection", true);
                                    intent.putExtra("is_single_selection", true);
                                    startActivityForResult(intent, 106);
                                }
                            }
                        }).show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.cancel_booking))
                        .setMessage(getResources().getString(R.string.do_you_want_to_cancel_booking))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OleCancelBookingDialog bookingDialog = new OleCancelBookingDialog(getContext());
                                bookingDialog.setDialogCallback(new OleCancelBookingDialog.CancelBookingDialogCallback() {
                                    @Override
                                    public void enteredNote(String note) {
                                        cancelConfirmBookingAPI(true, "cancel", note);
                                    }
                                });
                                bookingDialog.show();
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
        else {
            // invitee
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.cancel_booking))
                    .setMessage(getResources().getString(R.string.do_you_want_to_cancel_booking))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rejectInvitationAPI(true);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            if (list.size() > 0) {
                assignToOtherAPI(true, list.get(0).getId(), "new");
            }
        }
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

    private void rejectClicked() {
        if (bookingDetail == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.do_you_want_to_reject_request))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rejectInvitationAPI(true);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void acceptClicked() {
        if (bookingDetail == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.do_you_want_to_accept_request))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPaymentDialog(bookingDetail.getBookingPrice(), bookingDetail.getCurrency(), "", "", "", true, false, "0", "", new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                acceptInvitationAPI(true, paymentMethod, orderRef, bookingDetail.getBookingPrice(), cardPaid, walletPaid);
                            }
                        });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void populateData() throws ParseException {
        if (bookingDetail == null) {
            return;
        }
        binding.coverVu.setVisibility(View.GONE);
        binding.tvClubName.setText(bookingDetail.getClubName());
        binding.tvFieldName.setText(bookingDetail.getFieldName());
        binding.tvDuration.setText(bookingDetail.getDuration());
        binding.tvInvoice.setText(bookingDetail.getInvoiceNo());
        binding.tvNote.setText(bookingDetail.getNote());
        binding.tvTime.setText(bookingDetail.getBookingTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = dateFormat.parse(bookingDetail.getBookingDate());
        dateFormat.applyPattern("EEEE, dd/MM/yyyy");
        binding.tvDate.setText(dateFormat.format(date));
        binding.tvPhone.setText(bookingDetail.getCreatedBy().getPhone());
        binding.tvPrice.setText(String.format("%s %s", bookingDetail.getBookingPrice(), bookingDetail.getCurrency()));
        binding.tvCity.setText(bookingDetail.getCity());
        binding.tvPaidAmount.setText(String.format("%s %s", bookingDetail.getPaidAmount(), bookingDetail.getCurrency()));
        binding.tvUnpaidAmount.setText(String.format("%s %s", bookingDetail.getUnpaidAmount(), bookingDetail.getCurrency()));
        binding.tvPartnerCount.setText(String.valueOf(bookingDetail.getJoinedPlayers().size()));
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

        if (!bookingDetail.getClubLatitude().equalsIgnoreCase("") && !bookingDetail.getClubLongitude().equalsIgnoreCase("")) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            String url = "https://maps.google.com/maps/api/staticmap?center=" + bookingDetail.getClubLatitude() + "," + bookingDetail.getClubLongitude() + "&zoom=16&size="+width+"x300&sensor=false&key="+getString(R.string.maps_api_key);
            Glide.with(getContext()).load(url).into(binding.mapVu);
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

        if (bookingDetail.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            binding.bookingHolderVu.setVisibility(View.GONE);
            binding.invitationBtns.setVisibility(View.GONE);
            binding.paidAmountVu.setVisibility(View.VISIBLE);
            binding.remainingAmountVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.bookingHolderVu.setVisibility(View.VISIBLE);
            binding.tvBookingHolder.setText(bookingDetail.getCreatedBy().getNickName());
            binding.paidAmountVu.setVisibility(View.GONE);
            binding.remainingAmountVu.setVisibility(View.GONE);
            binding.btnPay.setVisibility(View.GONE);
            binding.btnAddFac.setVisibility(View.GONE);
            if (bookingDetail.getPaymentStatus().equalsIgnoreCase("1")) {
                binding.invitationBtns.setVisibility(View.GONE);
                binding.btns.setVisibility(View.VISIBLE);
                binding.btnConfirm.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
            else {
                binding.invitationBtns.setVisibility(View.VISIBLE);
                binding.btns.setVisibility(View.GONE);
            }
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

    private void rejectInvitationAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.rejectInvitation(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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

    private void acceptInvitationAPI(boolean isLoader, String paymentMethod, String orderRef, String amount, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptInvitation(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, amount, paymentMethod, Functions.getIPAddress(), orderRef,cardPaid, walletPaid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            bookingDetail.setPaymentStatus("1");
                            binding.invitationBtns.setVisibility(View.GONE);
                            binding.tvPaymentStatus.setText(R.string.paid);
                            binding.tvPaymentStatus.setTextColor(getResources().getColor(R.color.greenColor));
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

    private void assignToOtherAPI(boolean isLoader, String playerId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.assignToOther(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId, type);
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
}