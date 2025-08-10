package ae.oleapp.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.OleFullImageActivity;
import ae.oleapp.adapters.OleBookingDayLimitAdapter;
import ae.oleapp.adapters.OlePlayerBalanceAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBookingCountDetailBinding;
import ae.oleapp.dialogs.OleBlockReasonDialog;
import ae.oleapp.dialogs.OlePayBalanceDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OlePlayerBalance;
import ae.oleapp.models.OlePlayerBalanceDetail;
import ae.oleapp.models.OlePlayerStat;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.player.OlePlayerReviewsActivity;
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

public class OleBookingCountDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingCountDetailBinding binding;
    private String clubId = "", clubType = "", playerId = "", playerPhone = "", playerName = "";
    private OlePlayerStat olePlayerStat;
    private final List<OleKeyValuePair> daysList = new ArrayList<>();
    private final List<OleKeyValuePair> paymentTypes = new ArrayList<>();
    private final List<OleKeyValuePair> bookingLimits = new ArrayList<>();
    private String days = "";
    private String paymentType = "";
    private String bookingLimit = "";
    private String allowContinuous = "0";
    private OleBookingDayLimitAdapter dayLimitAdapter;
    private OleBookingDayLimitAdapter paymentAdapter;
    private OleBookingDayLimitAdapter bookingLimitAdapter;
    private OleRankClubAdapter oleRankClubAdapter;
    private final List<Club> clubList = new ArrayList<>();
    private final List<OlePlayerBalance> balanceList = new ArrayList<>();
    private OlePlayerBalanceAdapter balanceAdapter;
    private OlePayBalanceDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingCountDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.player);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerId = bundle.getString("player_id", "");
            playerPhone = bundle.getString("player_phone", "");
            playerName = bundle.getString("player_name", "");
            clubId = bundle.getString("club_id", "");
        }

        int pos = 0;
        Club club = null;
        if (clubId.isEmpty()) {
            if (AppManager.getInstance().clubs.size() > 0) {
                club = AppManager.getInstance().clubs.get(0);
            }
        }
        else {
            for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
                if (clubId.equalsIgnoreCase(AppManager.getInstance().clubs.get(i).getId())) {
                    club = AppManager.getInstance().clubs.get(i);
                    pos = i;
                    break;
                }
            }
        }

        if (club != null) {
            clubId = club.getId();
            clubType = club.getClubType();
            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                binding.friendlyGameVu.setVisibility(View.GONE);
            } else {
                binding.friendlyGameVu.setVisibility(View.VISIBLE);
            }
        }

        club = new Club();
        club.setId("");
        club.setClubType("");
        club.setName(getString(R.string.all));
        clubList.add(club);
        pos = pos + 1;
        clubList.addAll(AppManager.getInstance().clubs);
        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), clubList, pos, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        getDaysData();
        days = daysList.get(0).getKey();
        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        dayLimitAdapter = new OleBookingDayLimitAdapter(getContext(), daysList, days);
        dayLimitAdapter.setOnItemClickListener(onItemClickListener);
        binding.daysRecyclerVu.setAdapter(dayLimitAdapter);

        getPaymentTypes();
        paymentType = paymentTypes.get(0).getKey();
        LinearLayoutManager paymentLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.paymentRecyclerVu.setLayoutManager(paymentLayoutManager);
        paymentAdapter = new OleBookingDayLimitAdapter(getContext(), paymentTypes, paymentType);
        paymentAdapter.setOnItemClickListener(paymentClickListener);
        binding.paymentRecyclerVu.setAdapter(paymentAdapter);

        getBookingLimits();
        bookingLimit = bookingLimits.get(0).getKey();
        LinearLayoutManager bookingLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.bookingLimitRecyclerVu.setLayoutManager(bookingLayoutManager);
        bookingLimitAdapter = new OleBookingDayLimitAdapter(getContext(), bookingLimits, bookingLimit);
        bookingLimitAdapter.setOnItemClickListener(bookingLimitClickListener);
        binding.bookingLimitRecyclerVu.setAdapter(bookingLimitAdapter);

        LinearLayoutManager balanceLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.balanceRecyclerVu.setLayoutManager(balanceLayoutManager);
        balanceAdapter = new OlePlayerBalanceAdapter(getContext(), balanceList);
        balanceAdapter.setItemClickListener(itemClickListener);
        binding.balanceRecyclerVu.setAdapter(balanceAdapter);

        binding.discountCardVu.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.profileVu.setOnClickListener(this);
        binding.cancelledVu.setOnClickListener(this);
        binding.hotCancelledVu.setOnClickListener(this);
        binding.bookingsVu.setOnClickListener(this);
        binding.hoursVu.setOnClickListener(this);
        binding.callBookingsVu.setOnClickListener(this);
        binding.appBookingsVu.setOnClickListener(this);
        binding.upcomingBookingsVu.setOnClickListener(this);
        binding.btnBlock.setOnClickListener(this);
        binding.reviewsVu.setOnClickListener(this);
        binding.btnYes.setOnClickListener(this);
        binding.btnNo.setOnClickListener(this);

        getProfileAPI(true);
    }

    private void getDaysData() {
        daysList.add(new OleKeyValuePair("1", getString(R.string.one_day)));
        daysList.add(new OleKeyValuePair("2", getString(R.string.two_days)));
        daysList.add(new OleKeyValuePair("4", getString(R.string.four_days)));
        daysList.add(new OleKeyValuePair("7", getString(R.string.one_week)));
        daysList.add(new OleKeyValuePair("15", getString(R.string.two_weeks)));
        daysList.add(new OleKeyValuePair("30", getString(R.string.one_month)));
        daysList.add(new OleKeyValuePair("60", getString(R.string.two_months)));
        daysList.add(new OleKeyValuePair("90", getString(R.string.three_months)));
        daysList.add(new OleKeyValuePair("180", getString(R.string.six_months)));
        daysList.add(new OleKeyValuePair("270", getString(R.string.nine_months)));
        daysList.add(new OleKeyValuePair("365", getString(R.string.one_year)));
    }

    private void getPaymentTypes() {
        paymentTypes.add(new OleKeyValuePair("both", getString(R.string.cash_card)));
        paymentTypes.add(new OleKeyValuePair("cash", getString(R.string.cash)));
        paymentTypes.add(new OleKeyValuePair("card", getString(R.string.card)));
    }

    private void getBookingLimits() {
        bookingLimits.add(new OleKeyValuePair("", getString(R.string.unlimited)));
        bookingLimits.add(new OleKeyValuePair("1", getString(R.string.one_booking)));
        bookingLimits.add(new OleKeyValuePair("2", getString(R.string.two_bookings)));
        bookingLimits.add(new OleKeyValuePair("4", getString(R.string.four_bookings)));
        bookingLimits.add(new OleKeyValuePair("7", getString(R.string.seven_bookings)));
        bookingLimits.add(new OleKeyValuePair("15", getString(R.string.fifteen_bookings)));
        bookingLimits.add(new OleKeyValuePair("30", getString(R.string.thirty_bookings)));
    }

    OlePlayerBalanceAdapter.OnItemClickListener itemClickListener = new OlePlayerBalanceAdapter.OnItemClickListener() {
        @Override
        public void itemClickListener(OlePlayerBalance balance) {
            if (clubType.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR, Toast.LENGTH_SHORT);
                return;
            }
            if (clubType.equalsIgnoreCase(Constants.kFootballModule)) {
                Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                intent.putExtra("booking_id", balance.getBookingId());
                startActivity(intent);
            }
            else {
                // padel
                Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                intent.putExtra("booking_id", balance.getBookingId());
                startActivity(intent);
            }
        }

        @Override
        public void childItemClickListener(OlePlayerBalanceDetail detail) {
            if (!detail.getReceipt().equalsIgnoreCase("")) {
                Intent intent = new Intent(getContext(), OleFullImageActivity.class);
                intent.putExtra("URL", detail.getReceipt());
                startActivity(intent);
            }
        }

        @Override
        public void payClickListener(OlePlayerBalance balance) {
            if (olePlayerStat != null) {
                payClicked(balance);
            }
        }
    };

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            Club club = clubList.get(pos);
            clubId = club.getId();
            clubType = club.getClubType();
            if (!club.getId().equalsIgnoreCase("")) {
                binding.friendlyGameVu.setVisibility(View.VISIBLE);
                binding.btnBlock.setVisibility(View.VISIBLE);
                if (olePlayerStat.getId().equalsIgnoreCase("")) {
                    binding.matchWonPointsVu.setVisibility(View.GONE);
                }
                else {
                    binding.matchWonPointsVu.setVisibility(View.VISIBLE);
                }
                if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule) || olePlayerStat.getId().equalsIgnoreCase("")) {
                    binding.friendlyGameVu.setVisibility(View.GONE);
                }
                else {
                    binding.friendlyGameVu.setVisibility(View.VISIBLE);
                }
            }
            else {
                binding.friendlyGameVu.setVisibility(View.GONE);
                binding.matchWonPointsVu.setVisibility(View.GONE);
                binding.btnBlock.setVisibility(View.GONE);
            }
            getProfileAPI(true);
        }
    };

    OleBookingDayLimitAdapter.OnItemClickListener onItemClickListener = new OleBookingDayLimitAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (clubId.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            if (olePlayerStat != null && !olePlayerStat.getId().equalsIgnoreCase("")) {
                days = daysList.get(pos).getKey();
                dayLimitAdapter.setSelectedDay(days);
                updatePlayerMethodAPI(true, "", days, olePlayerStat.getId());
            }
        }
    };

    OleBookingDayLimitAdapter.OnItemClickListener paymentClickListener = new OleBookingDayLimitAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (clubId.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            if (olePlayerStat != null && !olePlayerStat.getId().equalsIgnoreCase("")) {
                paymentType = paymentTypes.get(pos).getKey();
                paymentAdapter.setSelectedDay(paymentType);
                updatePlayerMethodAPI(true, paymentType, "", olePlayerStat.getId());
            }
        }
    };

    OleBookingDayLimitAdapter.OnItemClickListener bookingLimitClickListener = new OleBookingDayLimitAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (clubId.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            if (olePlayerStat != null) {
                bookingLimit = bookingLimits.get(pos).getKey();
                bookingLimitAdapter.setSelectedDay(bookingLimit);
                if (olePlayerStat.getId().equalsIgnoreCase("")) {
                    updateBookingLimitAPI(true, bookingLimit, "", playerPhone);
                }
                else {
                    updateBookingLimitAPI(true, bookingLimit, olePlayerStat.getId(), "");
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.profileVu) {
            if (olePlayerStat != null && !olePlayerStat.getId().equalsIgnoreCase("")) {
                Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
                intent.putExtra("player_id", olePlayerStat.getId());
                startActivity(intent);
            }
        }
        else if (v == binding.btnBlock) {
            blockClicked();
        }
        else if (v == binding.reviewsVu) {
            if (olePlayerStat != null && !olePlayerStat.getId().equalsIgnoreCase("")) {
                Intent intent = new Intent(getContext(), OlePlayerReviewsActivity.class);
                intent.putExtra("player_id", olePlayerStat.getId());
                startActivity(intent);
            }
        }
        else if (v == binding.cancelledVu) {
            bookingClicked("all_canceled");
        }
        else if (v == binding.hotCancelledVu) {
            bookingClicked("hot_canceled");
        }
        else if (v == binding.bookingsVu) {
            bookingClicked("all_booked");
        }
        else if (v == binding.hoursVu) {
            bookingClicked("hours");
        }
        else if (v == binding.callBookingsVu) {
            bookingClicked("call_booked");
        }
        else if (v == binding.appBookingsVu) {
            bookingClicked("app_booked");
        }
        else if (v == binding.upcomingBookingsVu) {
            bookingClicked("upcoming");
        }
        else if (v == binding.btnYes) {
            if (clubId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR, Toast.LENGTH_SHORT);
                return;
            }
            allowContinuous = "1";
            yesClicked();
            updateContinuousBookingAPI(true, olePlayerStat.getId(), "1");
        }
        else if (v == binding.btnNo) {
            if (clubId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR, Toast.LENGTH_SHORT);
                return;
            }
            allowContinuous = "0";
            noClicked();
            updateContinuousBookingAPI(true, olePlayerStat.getId(), "0");
        }
    }

    private void noClicked() {
        binding.btnYes.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        binding.btnYes.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.btnNo.setBackgroundColor(getResources().getColor(R.color.blueColorNew));
        binding.btnNo.setTextColor(getResources().getColor(R.color.whiteColor));
    }

    private void yesClicked() {
        binding.btnYes.setBackgroundColor(getResources().getColor(R.color.blueColorNew));
        binding.btnYes.setTextColor(getResources().getColor(R.color.whiteColor));
        binding.btnNo.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        binding.btnNo.setTextColor(getResources().getColor(R.color.darkTextColor));
    }

    private void payClicked(OlePlayerBalance balance) {
        if (clubId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR, Toast.LENGTH_SHORT);
            return;
        }
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.payment), getResources().getString(R.string.make_discount))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("PayBalanceDialogFragment");
                        if (prev != null) {
                            fragmentTransaction.remove(prev);
                        }
                        fragmentTransaction.addToBackStack(null);
                        dialogFragment = new OlePayBalanceDialogFragment();
                        if (index == 0) {
                            dialogFragment.setType("payment");
                        }
                        else {
                            dialogFragment.setType("discount");
                        }
                        dialogFragment.setFragmentCallback(new OlePayBalanceDialogFragment.PayBalanceDialogFragmentCallback() {
                            @Override
                            public void enteredValue(DialogFragment dialogFragment, String value, String type, String filePath) {
                                dialogFragment.dismiss();
                                payBalanceAPI(true, balance.getBookingId(), value, type, balance, filePath);
                            }
                        });
                        dialogFragment.show(getSupportFragmentManager(), "PayBalanceDialogFragment");
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialogFragment.isVisible()) {
            dialogFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void blockClicked() {
        if (olePlayerStat == null) {
            return;
        }
        if (olePlayerStat.getIsBlocked().equalsIgnoreCase("1")) {
            unblockUser();
        }
        else {
            blockUser();
        }
    }

    private void unblockUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.unblock_user))
                .setMessage(getResources().getString(R.string.do_you_want_to_unblock_user))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (olePlayerStat.getId().equalsIgnoreCase("")) {
                            blockUnblockUserAPI(true, "0", "", "", playerPhone);
                        }
                        else {
                            blockUnblockUserAPI(true, "0", olePlayerStat.getId(), "", "");
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
                                if (olePlayerStat.getId().equalsIgnoreCase("")) {
                                    blockUnblockUserAPI(true, "1", "", reason, playerPhone);
                                }
                                else {
                                    blockUnblockUserAPI(true, "1", olePlayerStat.getId(), reason, "");
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

    private void bookingClicked(String type) {
        if (olePlayerStat != null) {
            Intent intent = new Intent(getContext(), OleCancelledBookingsActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("player_id", olePlayerStat.getId());
            intent.putExtra("player_phone", olePlayerStat.getPhone());
            intent.putExtra("is_player_stat", true);
            intent.putExtra("type", type);
            startActivity(intent);
        }
    }

    private void populateData() {
        if (olePlayerStat == null) { return; }
        if (olePlayerStat.getId().equalsIgnoreCase("")) {
            // call booking
            binding.tvName.setText(playerName);
            binding.tvAge.setText(playerPhone);
        }
        else {
            if (olePlayerStat.getNickName().isEmpty()) {
                binding.tvName.setText(olePlayerStat.getName());
            }
            else {
                binding.tvName.setText(String.format("%s (%s)", olePlayerStat.getName(), olePlayerStat.getNickName()));
            }
            if (olePlayerStat.getAge().equalsIgnoreCase("") || olePlayerStat.getAge().equalsIgnoreCase("0")) {
                binding.tvAge.setVisibility(View.GONE);
            }
            else {
                binding.tvAge.setVisibility(View.VISIBLE);
                binding.tvAge.setText(String.format("%s: %s", getString(R.string.age), olePlayerStat.getAge()));
            }
        }
        Glide.with(getContext()).load(olePlayerStat.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.playerImage);
        if (olePlayerStat.getLevel() != null && !olePlayerStat.getLevel().isEmpty() && !olePlayerStat.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvRank.setVisibility(View.VISIBLE);
            binding.tvRank.setText(String.format("LV: %s", olePlayerStat.getLevel().getValue()));
        }
        else {
            binding.tvRank.setVisibility(View.INVISIBLE);
        }

        if (olePlayerStat.getCardDiscountValue().isEmpty()) {
            binding.discountCardVu.setVisibility(View.GONE);
        }
        else {
            binding.discountCardVu.setVisibility(View.VISIBLE);
            if (olePlayerStat.getCardDiscountRemaining().equalsIgnoreCase("0")) {
                binding.tvLeftBooking.setText(getResources().getString(R.string.place_discount_on_next_booking, olePlayerStat.getCardDiscountValue()));
            }
            else {
                binding.tvLeftBooking.setText(getResources().getString(R.string.place_booking_remaining_discount, olePlayerStat.getCardDiscountRemaining(), olePlayerStat.getCardDiscountValue()));
            }
            binding.tvPerc.setText(olePlayerStat.getCardDiscountValue());
            int target = Integer.parseInt(olePlayerStat.getCardDiscountTarget());
            int remain = Integer.parseInt(olePlayerStat.getCardDiscountRemaining());
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

        if (olePlayerStat.getId().equalsIgnoreCase("")) {
            binding.dateLimitVu.setVisibility(View.GONE);
            binding.paymentTypeVu.setVisibility(View.GONE);
            binding.continuousBookingVu.setVisibility(View.GONE);
            binding.matchWonPointsVu.setVisibility(View.GONE);
            binding.friendlyGameVu.setVisibility(View.GONE);
        }
        else {
            binding.dateLimitVu.setVisibility(View.VISIBLE);
            binding.paymentTypeVu.setVisibility(View.VISIBLE);
            binding.continuousBookingVu.setVisibility(View.VISIBLE);
            binding.matchWonPointsVu.setVisibility(View.VISIBLE);
            if (clubType == null || clubType.equalsIgnoreCase(Constants.kPadelModule)) {
                binding.friendlyGameVu.setVisibility(View.GONE);
            }
            else {
                binding.friendlyGameVu.setVisibility(View.VISIBLE);
            }
        }
        // temporary
        binding.continuousBookingVu.setVisibility(View.GONE);

        binding.tvCancelled.setText(olePlayerStat.getTotalCanceled());
        binding.tvCancelTime.setText(getString(R.string.cancellation_last_place_hours, olePlayerStat.getCancellationHours()));
        binding.tvTimeCancelled.setText(olePlayerStat.getCanceledTwelve());

        binding.tvMatches.setText(olePlayerStat.getMatchPlayed());
        binding.tvLostMatches.setText(olePlayerStat.getMatchLoss());
        binding.tvWon.setText(olePlayerStat.getMatchWon());
        binding.tvDrawMatches.setText(olePlayerStat.getMatchDrawn());
        binding.tvPoints.setText(olePlayerStat.getPoints());
        binding.tvWinPerc.setText(String.format("%s%%", olePlayerStat.getWinPercentage()));

        binding.tvFriendly.setText(olePlayerStat.getFriendlyGames());
        binding.tvFriendlyPerc.setText(String.format("%s%%", olePlayerStat.getGamesRanking()));
        binding.friendlyProgressBar.setProgress(Float.parseFloat(olePlayerStat.getGamesRanking()));
        binding.tvRate.setText(olePlayerStat.getReviews());

        binding.tvTotalBookings.setText(olePlayerStat.getTotalBookings());
        binding.tvTotalHours.setText(olePlayerStat.getTotalHours());
        binding.tvCallBookings.setText(olePlayerStat.getCallBookings());
        binding.tvAppBookings.setText(olePlayerStat.getAppBookings());
        binding.tvUpcomingBookings.setText(olePlayerStat.getFutureBookings());
        binding.tvBookingDate.setText(olePlayerStat.getLastBookingDate());

        binding.tvCash.setText(String.format("%s %s", olePlayerStat.getTotalCashPaid(), olePlayerStat.getCurrency()));
        binding.tvCard.setText(String.format("%s %s", olePlayerStat.getTotalOnlinePaid(), olePlayerStat.getCurrency()));

        paymentType = olePlayerStat.getRestrictedPayment();
        paymentAdapter.setSelectedDay(paymentType);

        days = olePlayerStat.getRestrictedDays();
        dayLimitAdapter.setSelectedDay(days);

        bookingLimit = olePlayerStat.getBookingsRestriction();
        bookingLimitAdapter.setSelectedDay(bookingLimit);

        allowContinuous = olePlayerStat.getContinuousAllowed();
        if (allowContinuous.equalsIgnoreCase("1")) {
            yesClicked();
        }
        else {
            noClicked();
        }

        balanceList.clear();
        balanceList.addAll(olePlayerStat.getPendingBalance());
        populateBalance();

        if (olePlayerStat.getIsBlocked().equalsIgnoreCase("1")) {
            binding.btnBlock.setText(R.string.unblock_this_user);
            binding.blockVu.setVisibility(View.VISIBLE);
            binding.tvBlockedBy.setText(olePlayerStat.getBlockedBy());
            binding.tvBlockedDate.setText(olePlayerStat.getBlockedDate());
            binding.tvReason.setText(olePlayerStat.getBlockedReason());
        }
        else {
            binding.btnBlock.setText(R.string.block_this_user);
            binding.blockVu.setVisibility(View.GONE);
        }
    }

    private void populateBalance() {
        if (balanceList.isEmpty()) {
            binding.balanceVu.setVisibility(View.GONE);
        }
        else {
            binding.balanceVu.setVisibility(View.VISIBLE);
            balanceAdapter.notifyDataSetChanged();
            double total = 0;
            for (OlePlayerBalance balance : balanceList) {
                total += Double.parseDouble(balance.getAmount());
            }
            if (total > 0) {
                binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s: %.2f %s", getString(R.string.total_amount), total, olePlayerStat.getCurrency()));
            }
            else {
                binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s: 0 %s", getString(R.string.total_amount), olePlayerStat.getCurrency()));
            }
        }
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String pId = "", pPhone = "";
        if (playerId.isEmpty()) {
            pId = "";
            pPhone = playerPhone;
        }
        else {
            pId = playerId;
            pPhone = "";
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.profileForOwner(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, pId, pPhone);
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
                            olePlayerStat = gson.fromJson(obj.toString(), OlePlayerStat.class);
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

    private void updatePlayerMethodAPI(boolean isLoader, String paymentMethod, String days, String id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePlayerPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, id, days, paymentMethod);
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

    private void updateBookingLimitAPI(boolean isLoader, String limit, String id, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.manageBookingsRestriction(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, id, phone, limit);
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

    private void updateContinuousBookingAPI(boolean isLoader, String id, String flag) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.managePlayerContinuous(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, id, flag);
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

    private void blockUnblockUserAPI(boolean isLoader, String status, String userId, String reason, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.blockUnblockUser(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), userId, status, reason, phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            olePlayerStat.setIsBlocked(status);
                            if (olePlayerStat.getIsBlocked().equalsIgnoreCase("1")) {
                                binding.btnBlock.setText(R.string.unblock_this_user);
                            }
                            else {
                                binding.btnBlock.setText(R.string.block_this_user);
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

    private void payBalanceAPI(boolean isLoader, String bookingId, String amount, String type, OlePlayerBalance balance, String filePath) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String isDiscount = "0";
        if (type.equalsIgnoreCase("discount")) {
            isDiscount = "1";
        }
        MultipartBody.Part filePart = null;
        if (!filePath.isEmpty()) {
            File file = new File(filePath);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("receipt", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeBalance(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), bookingId),
                RequestBody.create(MediaType.parse("text/plain"), amount),
                RequestBody.create(MediaType.parse("text/plain"), isDiscount));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            OlePlayerBalanceDetail detail = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), OlePlayerBalanceDetail.class);
                            balance.getBalanceDetails().add(detail);
                            double remaining = Double.parseDouble(balance.getAmount()) - Double.parseDouble(amount);
                            if (remaining > 0) {
                                balance.setAmount(String.format(Locale.ENGLISH, "%.2f", remaining));
                            }
                            else {
                                balance.setAmount("0");
                            }
                            populateBalance();
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