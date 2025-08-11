package ae.oleapp.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;
import org.json.JSONObject;
import java.io.File;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import ae.oleapp.R;
import ae.oleapp.activities.OleFullImageActivity;
import ae.oleapp.adapters.OleClubDetailFacAdapter;
import ae.oleapp.adapters.OleJoinedGridAdapter;
import ae.oleapp.adapters.OleTeamAdapter;
import ae.oleapp.adapters.OleTeamDragItem;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBookingDetailBinding;
import ae.oleapp.dialogs.OleAddFootballScoreDialogFragment;
import ae.oleapp.dialogs.OleBlockReasonDialog;
import ae.oleapp.dialogs.OleBookingPaymentDetailDialogFragment;
import ae.oleapp.dialogs.OleCancelBookingDialog;
import ae.oleapp.dialogs.OleReservationDetailDialogFragment;
import ae.oleapp.dialogs.OleUpdateCallBookingFragment;
import ae.oleapp.dialogs.OleUpdateFacilityBottomDialog;
import ae.oleapp.models.OleBookingList;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.models.OleMatchScore;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.player.OleGameHistoryActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.player.OlePreviewFieldActivity;
import ae.oleapp.player.OleFootballMatchShareActivity;
import ae.oleapp.player.OleFriendlyGameShareActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingDetailBinding binding;
    private String bookingId = "";
    private OleBookingList bookingDetail;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private final List<OlePlayerInfo> allPlayerList = new ArrayList<>();
    private final List<OlePlayerInfo> teamAList = new ArrayList<>();
    private final List<OlePlayerInfo> teamBList = new ArrayList<>();
    private OleJoinedGridAdapter oleJoinedGridAdapter;
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private OleClubDetailFacAdapter facilityAdapter;
    private boolean isBookingVuHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.booking_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            if (bundle.containsKey("isBookingVuHidden")) {
                isBookingVuHidden = bundle.getBoolean("isBookingVuHidden");
            }
        }

        GridLayoutManager facLayoutManager  = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        facilityAdapter = new OleClubDetailFacAdapter(getContext(), clubFacilities);
        binding.facRecyclerVu.setAdapter(facilityAdapter);

        binding.btnInvoice.setVisibility(View.GONE);
        binding.tabVu.setVisibility(View.GONE);
        binding.playersVu.setVisibility(View.GONE);
        if (isBookingVuHidden) {
            binding.bookingInfoVu.setVisibility(View.GONE);
        }
        else {
            binding.bookingInfoVu.setVisibility(View.VISIBLE);
        }

        detailVuClicked();

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnCall1.setOnClickListener(this);
        binding.btnCall2.setOnClickListener(this);
        binding.profileVu1.setOnClickListener(this);
        binding.profileVu2.setOnClickListener(this);
        binding.btnBlock.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.reservationVu.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.relLifetime.setOnClickListener(this);
        binding.relMonth.setOnClickListener(this);
        binding.btnPreview.setOnClickListener(this);
        binding.btnEditFac.setOnClickListener(this);
        binding.vuCancelNote.setOnClickListener(this);
        binding.btnPaymentInfo.setOnClickListener(this);
        binding.btnAddScore.setOnClickListener(this);
        binding.singlePlayerVu.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.tvViewReceipt.setOnClickListener(this);
        binding.btnTimeEdit.setOnClickListener(this);
        binding.btnInvoice.setOnClickListener(this);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    detailVuClicked();
                }
                else if (tab.getPosition() == 1) {
                    joinedVuClicked();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBookingDetail(bookingDetail == null);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnEdit) {
            editClicked();
        }
        else if (v == binding.btnCall || v == binding.btnCall1) {
            p1PhoneClicked();
        }
        else if (v == binding.btnCall2) {
            p2PhoneClicked();
        }
        else if (v == binding.profileVu1 || v == binding.singlePlayerVu) {
            p1ImgClicked();
        }
        else if (v == binding.profileVu2) {
            p2ImgClicked();
        }
        else if (v == binding.btnBlock) {
            blockClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
        else if (v == binding.reservationVu) {
            reservationClicked();
        }
        else if (v == binding.btnConfirm) {
            confirmClicked();
        }
        else if (v == binding.relLifetime || v == binding.relMonth) {
            bookingsClicked(v);
        }
        else if (v == binding.btnPreview) {
            previewClicked();
        }
        else if (v == binding.btnEditFac) {
            addFacClicked();
        }
        else if (v == binding.vuCancelNote) {
            cancelVuClicked();
        }
        else if (v == binding.btnPaymentInfo) {
            paymentInfoClicked();
        }
        else if (v == binding.btnAddScore) {
            addScoreClicked();
        }
        else if (v == binding.btnShare) {
            if (bookingDetail != null) {
                shareClicked();
            }
        }
        else if (v == binding.btnInvoice) {
            if (bookingDetail != null) {
                String url = bookingDetail.getInvoiceUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;

                }
                //Log.d("bigurl", url);  //Done
                String invoiceurl = url;

                Intent intent = new Intent(OleBookingDetailActivity.this, OleInvoiceWebview.class);
                intent.putExtra("invoiceurl", invoiceurl);
                startActivity(intent);

               // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));



            }
        }
        else if (v == binding.tvViewReceipt) {
            if (bookingDetail != null && !bookingDetail.getPosReceipt().isEmpty()) {
                Intent intent = new Intent(getContext(), OleFullImageActivity.class);
                intent.putExtra("URL", bookingDetail.getPosReceipt());
                startActivity(intent);
            }
        }
        else if (v == binding.btnTimeEdit) {
            if (bookingDetail != null) {
                Intent intent = new Intent(getContext(), OleChangeBookingTimeActivity.class);
                intent.putExtra("booking_id", bookingDetail.getId());
                intent.putExtra("booking_time", bookingDetail.getBookingTime());
                intent.putExtra("slot60", bookingDetail.getSlots60());
                intent.putExtra("slot90", bookingDetail.getSlots90());
                intent.putExtra("slot120", bookingDetail.getSlots120());
                intent.putExtra("club_type", Constants.kFootballModule);
                intent.putExtra("date", bookingDetail.getBookingDate());
                if (bookingDetail.getDuration().contains("90")) {
                    intent.putExtra("duration", "1.5");
                }
                else if (bookingDetail.getDuration().contains("120")) {
                    intent.putExtra("duration", "2");
                }
                else {
                    intent.putExtra("duration", "1");
                }
                startActivity(intent);
            }
        }
    }

    private void shareClicked() {
        if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
            Intent intent = new Intent(getContext(), OleFriendlyGameShareActivity.class);
            intent.putExtra("club_name", bookingDetail.getClubName());
            intent.putExtra("date", bookingDetail.getBookingDate());
            intent.putExtra("time", bookingDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(bookingDetail.getUser()));
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
        else if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge) || bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
            Intent intent = new Intent(getContext(), OleFootballMatchShareActivity.class);
            intent.putExtra("club_name", bookingDetail.getClubName());
            intent.putExtra("date", bookingDetail.getBookingDate());
            intent.putExtra("time", bookingDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(bookingDetail.getUser()));
            if (bookingDetail.getPlayerTwo() != null && !bookingDetail.getPlayerTwo().isEmpty()) {
                intent.putExtra("player_two", gson.toJson(bookingDetail.getPlayerTwo()));
            }
            startActivity(intent);
        }
    }

    private void addScoreClicked() {
        if (bookingDetail != null && bookingDetail.getStatus().equalsIgnoreCase(Constants.kFinishedBooking) && !bookingDetail.getPlayerTwo().isEmpty()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("AddFootballScoreDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleAddFootballScoreDialogFragment dialogFragment = new OleAddFootballScoreDialogFragment(bookingDetail.getUser(), bookingDetail.getPlayerTwo(), bookingDetail.getId(), bookingDetail.getMatchScore());
            dialogFragment.setDialogCallback(new OleAddFootballScoreDialogFragment.AddScoreDialogCallback() {
                @Override
                public void scoreAdded(OleMatchScore oleMatchScore) {
                    bookingDetail.setMatchScore(oleMatchScore);
                    binding.tvScore.setText(R.string.score);
                }
            });
            dialogFragment.show(fragmentTransaction, "AddFootballScoreDialogFragment");
        }
        else {
            Functions.showToast(getContext(), getString(R.string.add_score_after_complete_booking), FancyToast.ERROR);
        }
    }

    private void paymentInfoClicked() {
        if (bookingDetail != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("BookingPaymentDetailDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleBookingPaymentDetailDialogFragment dialogFragment = new OleBookingPaymentDetailDialogFragment(bookingDetail.getPosPaid(), bookingDetail.getCashPaid(), bookingDetail.getCardPaid(), bookingDetail.getWalletPaid(), bookingDetail.getCurrency(), bookingDetail.getTotalPaid(), bookingDetail.getPendingBalance(), bookingDetail.getPaidBalance());
            dialogFragment.show(fragmentTransaction, "BookingPaymentDetailDialogFragment");
        }
    }

    private void editClicked() {
        if (bookingDetail == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("UpdateCallBookingFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleUpdateCallBookingFragment dialogFragment = new OleUpdateCallBookingFragment(bookingDetail.getUser().getName(), bookingDetail.getUser().getPhone(), bookingId, bookingDetail.getCallBooking(), bookingDetail.getBookingPrice(), bookingDetail.getCurrency(), bookingDetail.getDuration());
        dialogFragment.setFragmentCallback(new OleUpdateCallBookingFragment.UpdateCallBookingFragmentCallback() {
            @Override
            public void didUpdateDetails(String name, String phone) {
                getBookingDetail(false);
            }
        });
        dialogFragment.show(fragmentTransaction, "UpdateCallBookingFragment");
    }

    private void detailVuClicked() {
        binding.detailInfoVu.setVisibility(View.VISIBLE);
        binding.joinRecyclerVu.setVisibility(View.GONE);
        binding.formationInfoVu.setVisibility(View.GONE);
    }

    private void joinedVuClicked() {
        binding.detailInfoVu.setVisibility(View.GONE);
        binding.joinRecyclerVu.setVisibility(View.VISIBLE);
        binding.formationInfoVu.setVisibility(View.GONE);
    }

    private void formationVuClicked() {
        binding.detailInfoVu.setVisibility(View.GONE);
        binding.joinRecyclerVu.setVisibility(View.GONE);
        if (bookingDetail != null && !bookingDetail.getGameTeam().isEmpty()) {
            binding.formationInfoVu.setVisibility(View.VISIBLE);
            setupBoard();
        }
        else {
            binding.formationInfoVu.setVisibility(View.GONE);
        }
    }

    private void p1PhoneClicked() {
        if (bookingDetail != null && !bookingDetail.getUser().getPhone().isEmpty()) {
            makeCall(bookingDetail.getUser().getPhone());
        }
    }

    private void p2PhoneClicked() {
        if (bookingDetail != null) {
            if (!bookingDetail.getPlayerTwo().isEmpty() && !bookingDetail.getPlayerTwo().getPhone().isEmpty()) {
                makeCall(bookingDetail.getPlayerTwo().getPhone());
            }
        }
    }

    private void p1ImgClicked() {
        if (bookingDetail != null) {
            if (bookingDetail.getUser().getId().equalsIgnoreCase("")) {
                Intent bookingIntent = new Intent(getContext(), OleBookingCountDetailActivity.class);
                bookingIntent.putExtra("player_id", "");
                bookingIntent.putExtra("player_phone", bookingDetail.getUser().getPhone());
                bookingIntent.putExtra("player_name", bookingDetail.getUser().getName());
                startActivity(bookingIntent);
            }
            else {
                gotoProfile(bookingDetail.getUser().getId());
            }
        }
    }

    private void p2ImgClicked() {
        if (bookingDetail != null && !bookingDetail.getPlayerTwo().isEmpty()) {
            gotoProfile(bookingDetail.getPlayerTwo().getId());
        }
    }

    private void blockClicked() {
        if (bookingDetail == null) {
            return;
        }
        if (bookingDetail.getIsBlocked().equalsIgnoreCase("1")) {
            unblockUser();
        }
        else {
            blockUser();
        }
    }

    private void cancelClicked() {
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
                                cancelConfirmBookingAPI(true, "cancel", note, "", "", "", "", "", "", "");
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

    private void reservationClicked() {
        if (bookingDetail != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("ReservationDetailDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleReservationDetailDialogFragment dialogFragment = new OleReservationDetailDialogFragment(bookingDetail.getReservationDetails());
            dialogFragment.show(fragmentTransaction, "ReservationDetailDialogFragment");
        }
    }

    private void confirmClicked() {
        if (bookingDetail == null) {
            return;
        }
        if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kConfirmedByOwnerBooking) || bookingDetail.getStatus().equalsIgnoreCase(Constants.kConfirmedByPlayerBooking)) {
            completeBooking();
        }
        else {
            confirmBooking();
        }
    }

    private void bookingsClicked(View view) {
        if (bookingDetail == null) {
            return;
        }
        Intent intent = new Intent(getContext(), OleLifetimeBookingsActivity.class);
        intent.putExtra("club_id", bookingDetail.getClubId());
        intent.putExtra("player_id", bookingDetail.getUser().getId());
        intent.putExtra("is_call", bookingDetail.getCallBooking());
        intent.putExtra("phone", bookingDetail.getUser().getPhone());
        intent.putExtra("app_module", bookingDetail.getBookingFieldType());
        if (view.getId() == R.id.rel_lifetime) {
            intent.putExtra("this_month", "0");
        }
        else {
            intent.putExtra("this_month", "1");
        }
        startActivity(intent);
    }

    private void unblockUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.unblock_user))
                .setMessage(getResources().getString(R.string.do_you_want_to_unblock_user))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bookingDetail.getCallBooking().equalsIgnoreCase("1")) {
                            blockUnblockUserAPI(true, "0", "", "", bookingDetail.getUser().getPhone());
                        }
                        else {
                            blockUnblockUserAPI(true, "0", bookingDetail.getUser().getId(), "", "");
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

    private void blockUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.block_user))
                .setMessage(getResources().getString(R.string.do_you_want_to_block_user))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OleBlockReasonDialog reasonDialog = new OleBlockReasonDialog(getContext());
                        reasonDialog.setDialogCallback(new OleBlockReasonDialog.BlockReasonDialogCallback() {
                            @Override
                            public void enteredReason(String reason) {
                                if (bookingDetail.getCallBooking().equalsIgnoreCase("1")) {
                                    blockUnblockUserAPI(true, "1", "", reason, bookingDetail.getUser().getPhone());
                                }
                                else {
                                    blockUnblockUserAPI(true, "1", bookingDetail.getUser().getId(), reason, "");
                                }
                            }
                        });
                        reasonDialog.show();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void confirmBooking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.confirm_booking))
                .setMessage(getResources().getString(R.string.do_you_want_to_confirm_booking))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelConfirmBookingAPI(true, "confirm", "", "", "", "", "", "", "", "");
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void completeBooking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.complete))
                .setMessage(getResources().getString(R.string.do_you_want_to_complete))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openCompleteDialog();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void openCompleteDialog() {
        Intent intent = new Intent(getContext(), OleCompleteBookingActivity.class);
        intent.putExtra("price", bookingDetail.getBookingPrice());
        intent.putExtra("currency", bookingDetail.getCurrency());
        intent.putExtra("duration", bookingDetail.getDuration());
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1000) {
            Bundle bundle = data.getExtras();
            String note = bundle.getString("note");
            String discount = bundle.getString("discount");
            String invoiceNo = bundle.getString("invoiceNo");
            String extraTime = bundle.getString("extraTime");
            String price = bundle.getString("price");
            String balance = bundle.getString("balance");
            String posPayment = bundle.getString("posPayment");
            String filePath = bundle.getString("filePath");
            cancelConfirmBookingAPI(true, "finished", note, invoiceNo, discount, extraTime, price, posPayment, balance, filePath);
        }
    }

    private void gotoProfile(String pId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", pId);
        startActivity(intent);
    }

    private void previewClicked() {
        if (bookingDetail != null) {
            Intent intent = new Intent(getContext(), OlePreviewFieldActivity.class);
            intent.putExtra("is_from_booking", true);
            intent.putExtra("is_captain", false);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("creator_id", bookingDetail.getUser().getId());
            Gson gson = new Gson();
            intent.putExtra("team", gson.toJson(bookingDetail.getGameTeam()));
            startActivity(intent);
        }
    }

    private void addFacClicked() {
        if (bookingDetail != null) {
            OleUpdateFacilityBottomDialog bottomDialog = new OleUpdateFacilityBottomDialog(bookingDetail.getClubFacilities(), bookingDetail.getFacilities(), bookingId);
            bottomDialog.setDialogCallback(new OleUpdateFacilityBottomDialog.UpdateFacilityBottomDialogCallback() {
                @Override
                public void didUpdateFacilities() {
                    getBookingDetail(bookingDetail == null);
                }
            });
            bottomDialog.show(getSupportFragmentManager(), "UpdateFacilityBottomDialog");
        }
    }

    private void cancelVuClicked() {
        if (bookingDetail == null) {
            return;
        }
        Intent intent = new Intent(getContext(), OleCancelledBookingsActivity.class);
        intent.putExtra("club_id", bookingDetail.getClubId());
        if (bookingDetail.getCallBooking().equalsIgnoreCase("1")) {
            intent.putExtra("player_id", "");
        }
        else {
            intent.putExtra("player_id", bookingDetail.getUser().getId());
        }
        intent.putExtra("player_phone", bookingDetail.getUser().getPhone());
        intent.putExtra("app_module", bookingDetail.getBookingFieldType());
        intent.putExtra("is_player_stat", false);
        startActivity(intent);
    }

    private void populateData() throws ParseException {
        if (bookingDetail == null) {
            return;
        }
        binding.tvClubName.setText(bookingDetail.getClubName());
        binding.tvFieldName.setText(String.format("%s (%s)", bookingDetail.getFieldName(), bookingDetail.getFieldSize()));
        binding.tvInvoice.setText(bookingDetail.getInvoiceNo());
        binding.tvNote.setText(bookingDetail.getNote());
        binding.tvTime.setText(String.format("%s (%s)", bookingDetail.getBookingTime(), bookingDetail.getDuration()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = dateFormat.parse(bookingDetail.getBookingDate());
        dateFormat.applyPattern("EEEE, dd/MM/yyyy");
        binding.tvDate.setText(dateFormat.format(date));
        binding.tvPaidAmount.setText(String.format("%s %s", bookingDetail.getPaidAmount(), bookingDetail.getCurrency()));
        binding.tvUnpaidAmount.setText(String.format("%s %s", bookingDetail.getUnpaidAmount(), bookingDetail.getCurrency()));
        clubFacilities.clear();
        clubFacilities.addAll(bookingDetail.getFacilities());
        facilityAdapter.notifyDataSetChanged();
        if (clubFacilities.size() == 0) {
            binding.facRecyclerVu.setVisibility(View.GONE);
            binding.tvFacility.setVisibility(View.VISIBLE);
        }
        else {
            binding.facRecyclerVu.setVisibility(View.VISIBLE);
            binding.tvFacility.setVisibility(View.GONE);
        }
        binding.tvPrice.setText(String.format("%s %s", bookingDetail.getBookingPrice(), bookingDetail.getCurrency()));

        if (bookingDetail.getPaymentStatus().equalsIgnoreCase("1")) {
            binding.tvPaymentStatus.setText(R.string.paid);
            binding.tvPaymentStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.paymentStatusCard.setCardBackgroundColor(Color.parseColor("#1A49D483"));
        }
        else {
            binding.tvPaymentStatus.setText(R.string.unpaid);
            binding.tvPaymentStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.paymentStatusCard.setCardBackgroundColor(Color.parseColor("#1Af02301"));
        }

        if (bookingDetail.getPosReceipt().isEmpty()) {
            binding.tvViewReceipt.setVisibility(View.GONE);
        }
        else {
            binding.tvViewReceipt.setVisibility(View.VISIBLE);
        }

        if (bookingDetail.getInvoiceUrl().equalsIgnoreCase("")) {
            binding.btnInvoice.setVisibility(View.GONE);
        }
        else {
            binding.btnInvoice.setVisibility(View.VISIBLE);
        } 

        binding.tvDiscount.setTextColor(Color.parseColor("#F02301"));
        if (bookingDetail.getDiscountType().equalsIgnoreCase("normal")) {
            binding.tvDiscount.setText(String.format("%s %s", bookingDetail.getDiscount(), bookingDetail.getCurrency()));
        }
        else if (bookingDetail.getDiscountType().equalsIgnoreCase("promo")) {
            binding.tvDiscount.setText(getResources().getString(R.string.promo_place, bookingDetail.getDiscount(), bookingDetail.getCurrency()));
        }
        else if (bookingDetail.getDiscountType().equalsIgnoreCase("offer")) {
            binding.tvDiscount.setText(getResources().getString(R.string.offer_place, bookingDetail.getDiscount(), bookingDetail.getCurrency()));
        }
        else if (bookingDetail.getDiscountType().equalsIgnoreCase("loyalty")) {
            binding.tvDiscount.setText(getResources().getString(R.string.loyalty_place, bookingDetail.getDiscount(), bookingDetail.getCurrency()));
        } else {
            binding.tvDiscount.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.tvDiscount.setText(String.format("%s %s", bookingDetail.getDiscount(), bookingDetail.getCurrency()));
        }

        if (bookingDetail.getPromoCode() != null && !bookingDetail.getPromoCode().equalsIgnoreCase("")) {
            binding.tvPromoCode.setText(bookingDetail.getPromoCode());
            binding.promoCodeVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.promoCodeVu.setVisibility(View.GONE);
        }

        if (bookingDetail.getCallBooking().equalsIgnoreCase("1")) {
            binding.tvPlayerName.setText(bookingDetail.getUser().getName());
            binding.singlePlayerImageVu.setImageResource(R.drawable.player_active);
            binding.tvLevel.setVisibility(View.INVISIBLE);
            binding.leftBookingVu.setVisibility(View.GONE);
        }
        else {
            binding.tvPlayerName.setText(bookingDetail.getUser().getName());
            Glide.with(getApplicationContext()).load(bookingDetail.getUser().getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.singlePlayerImageVu);
            if (bookingDetail.getUser().getLevel() != null && !bookingDetail.getUser().getLevel().isEmpty() && !bookingDetail.getUser().getLevel().getValue().equalsIgnoreCase("")) {
                binding.tvLevel.setVisibility(View.VISIBLE);
                binding.tvLevel.setText(String.format("LV: %s", bookingDetail.getUser().getLevel().getValue()));
            }
            else {
                binding.tvLevel.setVisibility(View.INVISIBLE);
            }
            if (bookingDetail.getUser().getCardDiscountValue().isEmpty()) {
                binding.leftBookingVu.setVisibility(View.GONE);
            }
            else {
                binding.leftBookingVu.setVisibility(View.VISIBLE);
                if (bookingDetail.getUser().getDiscountBookingRemaining().equalsIgnoreCase("0")) {
                    binding.tvLeftBooking.setText(getResources().getString(R.string.place_discount_on_next_booking, bookingDetail.getUser().getCardDiscountValue()));
                }
                else {
                    binding.tvLeftBooking.setText(getResources().getString(R.string.place_booking_remaining_discount, bookingDetail.getUser().getDiscountBookingRemaining(), bookingDetail.getUser().getCardDiscountValue()));
                }
                binding.tvPerc.setText(bookingDetail.getUser().getCardDiscountValue());
                int target = Integer.parseInt(bookingDetail.getUser().getDiscountBookingTarget());
                int remain = Integer.parseInt(bookingDetail.getUser().getDiscountBookingRemaining());
                int current = target - remain;
                if (current > 0) {
                    binding.stepView.setCompletedPosition(current-1);
                    binding.stepView.setProgressColorIndicator(getResources().getColor(R.color.greenColor));
                }
                else {
                    binding.stepView.setProgressColorIndicator(getResources().getColor(R.color.bgVuColor));
                }
                String[] arr = new String[target];
                for (int i = 0; i < target; i++) {
                    arr[i] = String.valueOf(i+1);
                }
                binding.stepView.setLabels(arr);
            }
        }

        binding.tvLifetimeBooking.setText(bookingDetail.getUser().getLifetimeBookings());
        binding.tvMonthBooking.setText(bookingDetail.getUser().getThisMonthBookings());
        binding.tvConfirmPerc.setText(bookingDetail.getUser().getConfirmPercentage());
        if (bookingDetail.getUser().getCanceledTimes().isEmpty() || bookingDetail.getUser().getCanceledTimes().equalsIgnoreCase("0")) {
            binding.vuCancelNote.setVisibility(View.GONE);
        }
        else {
            binding.vuCancelNote.setVisibility(View.VISIBLE);
            binding.tvCancelNote.setText(getResources().getString(R.string.cancel_note_place, bookingDetail.getUser().getCanceledTimes(), bookingDetail.getCanceledHours()));
        }

        if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
            binding.tabVu.setVisibility(View.GONE);
            binding.singlePlayerVu.setVisibility(View.VISIBLE);
            binding.playersVu.setVisibility(View.GONE);
            binding.btnAddScore.setVisibility(View.GONE);
            binding.btnShare.setVisibility(View.GONE);
        }
        else if (bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge) || bookingDetail.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge)) {
            binding.playersVu.setVisibility(View.VISIBLE);
            binding.singlePlayerVu.setVisibility(View.GONE);
            binding.btnAddScore.setVisibility(View.VISIBLE);
            binding.btnShare.setVisibility(View.VISIBLE);
            binding.tabVu.setVisibility(View.GONE);
            binding.profileVu1.populateData(bookingDetail.getUser().getName(), bookingDetail.getUser().getPhotoUrl(), bookingDetail.getUser().getLevel(), true);
            if (!bookingDetail.getPlayerTwo().isEmpty()) {
                // player accepted challenge
                binding.joinRecyclerVu.setVisibility(View.GONE);
                binding.detailInfoVu.setVisibility(View.VISIBLE);
                OlePlayerInfo player2 = bookingDetail.getPlayerTwo();
                binding.profileVu2.populateData(player2.getName(), player2.getPhotoUrl(), player2.getLevel(), true);
            }
            else {
                // player not accepted challenge
                binding.profileVu2.populateData("?", "", null, false);
            }
            if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                if (bookingDetail.getMatchScore() != null) {
                    binding.tvScore.setText(R.string.score);
                }
                else {
                    binding.tvScore.setText(R.string.add_score);
                }
            }
        }
        else {
            // friendly game
            binding.tabVu.setVisibility(View.VISIBLE);
            binding.singlePlayerVu.setVisibility(View.VISIBLE);
            binding.btnAddScore.setVisibility(View.GONE);
            binding.btnShare.setVisibility(View.VISIBLE);
            playerList.clear();
            playerList.addAll(bookingDetail.getJoinedPlayers());

            if (oleJoinedGridAdapter == null) {
                binding.joinRecyclerVu.setLayoutManager(new GridLayoutManager(getContext(), 2));
                oleJoinedGridAdapter = new OleJoinedGridAdapter(getContext(), playerList, true);
                oleJoinedGridAdapter.setOnItemClickListener(gridItemClickListener);
                oleJoinedGridAdapter.setBookingStatus(bookingDetail.getStatus());
                binding.joinRecyclerVu.setAdapter(oleJoinedGridAdapter);
            }
            else {
                oleJoinedGridAdapter.setBookingStatus(bookingDetail.getStatus());
                oleJoinedGridAdapter.notifyDataSetChanged();
            }

            // create team
            checkTeamCreatedOrNot();

            if (playerList.size() > 0) {
                binding.tabLayout.getTabAt(1).setText(getResources().getString(R.string.joined_place, playerList.size()));
            }
            else {
                binding.tabLayout.getTabAt(1).setText(getResources().getString(R.string.joined));
            }
        }

        clubFacilities.clear();
        clubFacilities.addAll(bookingDetail.getFacilities());
        facilityAdapter.notifyDataSetChanged();

        binding.btnEditFac.setVisibility(View.GONE);
        if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kPendingBooking)) {
            binding.tvStatus.setText(R.string.pending);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1Af02301"));
            binding.detailsBtnVu.setVisibility(View.VISIBLE);
            binding.btnConfirm.setVisibility(View.VISIBLE);
            binding.btnEditFac.setVisibility(View.VISIBLE);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kConfirmedByPlayerBooking)) {
            binding.tvStatus.setText(R.string.confirm_by_player);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1A49d483"));
            binding.detailsBtnVu.setVisibility(View.VISIBLE);
            binding.btnEditFac.setVisibility(View.VISIBLE);
            binding.tvConfirm.setText(R.string.complete);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kConfirmedByOwnerBooking)) {
            binding.tvStatus.setText(R.string.confirm_by_owner);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1A49d483"));
            binding.detailsBtnVu.setVisibility(View.VISIBLE);
            binding.btnEditFac.setVisibility(View.VISIBLE);
            binding.tvConfirm.setText(R.string.complete);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            binding.tvStatus.setText(R.string.finished);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.greenColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1A49d483"));
            binding.detailsBtnVu.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnTimeEdit.setVisibility(View.GONE);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking)) {
            binding.tvStatus.setText(R.string.cancel_by_player);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1Af02301"));
            binding.detailsBtnVu.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnTimeEdit.setVisibility(View.GONE);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking)) {
            binding.tvStatus.setText(R.string.cancel_by_owner);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1Af02301"));
            binding.detailsBtnVu.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnTimeEdit.setVisibility(View.GONE);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
            binding.tvStatus.setText(R.string.blocked);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1Af02301"));
            binding.detailsBtnVu.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnTimeEdit.setVisibility(View.GONE);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kExpiredBooking)) {
            binding.tvStatus.setText(R.string.expired);
            binding.tvStatus.setTextColor(getResources().getColor(R.color.redColor));
            binding.statusCard.setCardBackgroundColor(Color.parseColor("#1Af02301"));
            binding.detailsBtnVu.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnTimeEdit.setVisibility(View.GONE);
        }

        if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            binding.noteVu.setVisibility(View.VISIBLE);
            binding.invoiceVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.noteVu.setVisibility(View.GONE);
            binding.invoiceVu.setVisibility(View.GONE);
        }

        if (bookingDetail.getIsBlocked().equalsIgnoreCase("1")) {
            binding.btnBlock.setText(R.string.unblock_this_user);
            binding.blockVu.setVisibility(View.VISIBLE);
            binding.tvBlockedBy.setText(bookingDetail.getBlockedBy());
            binding.tvBlockedDate.setText(bookingDetail.getBlockedDate());
            binding.tvReason.setText(bookingDetail.getBlockedReason());
        }
        else {
            binding.btnBlock.setText(R.string.block_this_user);
            binding.blockVu.setVisibility(View.GONE);
        }
    }

    OleJoinedGridAdapter.OnItemClickListener gridItemClickListener = new OleJoinedGridAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            gotoGameHistory(playerList.get(pos));
        }

        @Override
        public void OnDeleteClick(View v, int pos) {

        }

        @Override
        public void OnRateClick(View v, int pos) {

        }
    };

    private void gotoGameHistory(OlePlayerInfo olePlayerInfo) {
        Intent intent = new Intent(getContext(), OleGameHistoryActivity.class);
        intent.putExtra("match_type", bookingDetail.getBookingType());
        intent.putExtra("booking_id", bookingId);
        intent.putExtra("req_status", "");
        intent.putExtra("player_id", olePlayerInfo.getId());
        intent.putExtra("player_confirmed", olePlayerInfo.getPlayerConfirmed());
        intent.putExtra("booking_status", bookingDetail.getStatus());
        intent.putExtra("creator_id", "");
        intent.putExtra("match_detail", false);
        intent.putExtra("booking_detail", true);
        startActivity(intent);
    }

    private void checkTeamCreatedOrNot() {
        if (bookingDetail == null || bookingDetail.getGameTeam().isEmpty()) {
            return;
        }
        binding.boardVu.setSnapToColumnsWhenScrolling(true);
        binding.boardVu.setSnapToColumnWhenDragging(true);
        binding.boardVu.setSnapDragItemToTouch(true);
        binding.boardVu.setSnapToColumnInLandscape(false);
        binding.boardVu.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        binding.boardVu.setBoardCallback(boardCallback);

        OleGameTeam oleGameTeam = bookingDetail.getGameTeam();
        binding.tvTeamA.setText(oleGameTeam.getTeamAName());
        binding.tvTeamB.setText(oleGameTeam.getTeamBName());
        binding.vuColorA.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamAColor()));
        binding.vuColorB.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamBColor()));
        allPlayerList.clear();
        allPlayerList.addAll(bookingDetail.getJoinedPlayers());
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
        showHideTeamPlaceholder();
//        if (binding.formationVu.getVisibility() == View.VISIBLE) {
//            // if formation vu is showing then need it
//            setupBoard();
//        }
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

    private void addAllPlayerList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), allPlayerList, R.layout.oleteam_player, R.id.item_layout, true, 1);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    BoardView.BoardCallback boardCallback = new BoardView.BoardCallback() {
        @Override
        public boolean canDragItemAtPosition(int column, int row) {
            return false;
        }

        @Override
        public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
            return false;
        }

        @Override
        public boolean canDragColumnAtPosition(int index) {
            return false;
        }

        @Override
        public boolean canDropColumnAtPosition(int oldIndex, int newIndex) {
            return false;
        }
    };

    private void getBookingDetail(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getBookingDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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
                            bookingDetail = gson.fromJson(obj.toString(), OleBookingList.class);
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

    private void cancelConfirmBookingAPI(boolean isLoader, String status, String note, String invoiceNo, String discount, String time, String price, String posPayment, String balance, String filePath) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (!filePath.isEmpty()) {
            File file = new File(filePath);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("receipt", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelConfirmBooking(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), bookingId),
                RequestBody.create(MediaType.parse("text/plain"), status),
                RequestBody.create(MediaType.parse("text/plain"), note),
                RequestBody.create(MediaType.parse("text/plain"), discount),
                RequestBody.create(MediaType.parse("text/plain"), invoiceNo),
                RequestBody.create(MediaType.parse("text/plain"), time),
                RequestBody.create(MediaType.parse("text/plain"), price),
                RequestBody.create(MediaType.parse("text/plain"), posPayment),
                RequestBody.create(MediaType.parse("text/plain"), balance),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)));
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
                                bookingDetail.setStatus(Constants.kConfirmedByOwnerBooking);
                                populateData();
                            }
                            else if (status.equalsIgnoreCase("finished")) {
                                bookingDetail.setStatus(Constants.kFinishedBooking);
                                bookingDetail.setPosReceipt(object.getJSONObject(Constants.kData).getString("pos_receipt"));
                                getBookingDetail(false);
                            }
                            else{
                                bookingDetail.setStatus(Constants.kCancelledByOwnerBooking);
                                populateData();
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

    private void blockUnblockUserAPI(boolean isLoader, String status, String userId, String reason, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.blockUnblockUser(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), userId, status, reason, phone);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (status.equalsIgnoreCase("1")) {
                                bookingDetail.setStatus(Constants.kBlockedBooking);
                            }
                            else{
                                bookingDetail.setStatus(Constants.kCancelledByOwnerBooking);
                            }
                            bookingDetail.setIsBlocked(status);
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
}
