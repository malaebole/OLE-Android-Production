package ae.oleapp.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleNotificationListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityNotificationsBinding;
import ae.oleapp.dialogs.OleRatingPagerDialogFragment;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OleNotificationList;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.owner.OleBookingActivity;
import ae.oleapp.owner.OleBookingDetailActivity;
import ae.oleapp.owner.OlePadelBookingDetailActivity;
import ae.oleapp.padel.OlePadelMatchBookingDetailActivity;
import ae.oleapp.padel.OlePadelMatchDetailActivity;
import ae.oleapp.padel.OlePadelNormalBookingDetailActivity;
import ae.oleapp.player.OleEmployeeRateActivity;
import ae.oleapp.player.OleGameBookingDetailActivity;
import ae.oleapp.player.OleGameDetailActivity;
import ae.oleapp.player.OleMatchBookingDetailActivity;
import ae.oleapp.player.OleMatchDetailActivity;
import ae.oleapp.player.OleNormalBookingDetailActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.shop.ShopOrderDetailActivity;
import ae.oleapp.player.OleFootballResultShareActivity;
import ae.oleapp.player.OlePadelResultShareActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleNotificationsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityNotificationsBinding binding;
    private final List<OleNotificationList> oleNotificationList = new ArrayList<>();
    private OleNotificationListAdapter adapter;
    private String clubId = "";
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.notifications);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleNotificationListAdapter(getContext(), oleNotificationList);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            binding.clubRecyclerVu.setVisibility(View.VISIBLE);
            if (AppManager.getInstance().clubs.size() > 0) {
                clubId = AppManager.getInstance().clubs.get(0).getId();
                getNotifications(true);
            }
        }
        else {
            binding.clubRecyclerVu.setVisibility(View.GONE);
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            getNotifications(oleNotificationList.isEmpty());
        }
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getNotifications(true);
        }
    };

    OleNotificationListAdapter.OnItemClickListener clickListener = new OleNotificationListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleNotificationList notification = oleNotificationList.get(pos);
            if (!notification.getIsRead().equalsIgnoreCase("1")) {
                readNotificationAPI(notification.getId());
                notification.setIsRead("1");
                adapter.notifyItemChanged(pos);
            }
            clickedItem(notification);
        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            deleteItem(pos);
        }
    };

    private void deleteItem(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete_notification))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                        adapter.binderHelper.closeLayout(String.valueOf(pos));
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteNotificationAPI(true, oleNotificationList.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    private void clickedItem(OleNotificationList notification) {
        if (notification.getType().equalsIgnoreCase(Constants.kNewBookingNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kConfirmBookingByPlayerNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kCancelBookingByPlayerNotification)) {
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                if (notification.getBookingField().equalsIgnoreCase("padel")) {
                    Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kConfirmBookingByOwnerNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kCancelBookingByOwnerNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kBookingReminderNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kAcceptInvitationNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kRejectInvitationNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kCancelInvitationByReceiverNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kPublicChallengeCanceledByPlayerNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kNewGameRequestNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kInviteMorePlayersNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kPlayerCanceledAcceptedMatchNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kNewChallengeRequestNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kPrivateChallengeAcceptedNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kPrivateChallengeCanceledNotification)){
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                if (notification.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                    Intent intent = new Intent(getContext(), OleNormalBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
                else if (notification.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                    Intent intent = new Intent(getContext(), OleGameBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleMatchBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kBookingAvailableNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kNewOfferNotification)) {
            Intent intent = new Intent(getContext(), OleBookingActivity.class);
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                intent.putExtra("field_id", notification.getFieldId());
            }
            else {
                intent.putExtra("field_id", "");
            }
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(notification.getClub()));
            startActivity(intent);
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kBookingCompleteNotification)) {
            if (notification.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("RatingPagerDialogFragment");
                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                fragmentTransaction.addToBackStack(null);
                OleRatingPagerDialogFragment dialogFragment = new OleRatingPagerDialogFragment(notification.getBookingId());
                dialogFragment.show(fragmentTransaction, "RatingPagerDialogFragment");
            }
            else {
                Intent rateIntent = new Intent(getContext(), OleEmployeeRateActivity.class);
                rateIntent.putExtra("booking_id", notification.getBookingId());
                rateIntent.putExtra("club_id", notification.getClub().getId());
                rateIntent.putExtra("is_rated", notification.getIsRated());
                startActivity(rateIntent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kAppUpdateNotification)) {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kCreatorCanceledAcceptedMatchNotification)) {
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                if (notification.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                    Intent intent = new Intent(getContext(), OleGameDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), OleMatchDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kPublicChallengeAcceptedNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kPublicChallengeCanceledNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kNewInvitationRequestNotification)) {
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                Intent intent = new Intent(getContext(), OleMatchDetailActivity.class);
                intent.putExtra("booking_id", notification.getBookingId());
                startActivity(intent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kGameRequestAcceptedNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kGameRequestCanceledNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kJoinFriendlyGameNotification) ||
                notification.getType().equalsIgnoreCase(Constants.kNewCaptainGameNotification)) {
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                Intent intent = new Intent(getContext(), OleGameDetailActivity.class);
                intent.putExtra("booking_id", notification.getBookingId());
                startActivity(intent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kShoppingNotification)) {
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                Intent intent = new Intent(getContext(), ShopOrderDetailActivity.class);
                intent.putExtra("order_id", notification.getOrderId());
                intent.putExtra("is_from_checkout", false);
                startActivity(intent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kInvitationPadel) ||
                notification.getType().equalsIgnoreCase(Constants.kInvitationRejectedByPlayer) ||
                notification.getType().equalsIgnoreCase(Constants.kInvitationAcceptedByPlayer)) {
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                Intent intent = new Intent(getContext(), OlePadelNormalBookingDetailActivity.class);
                intent.putExtra("booking_id", notification.getBookingId());
                startActivity(intent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kPadelChallengeRejected) ||
                notification.getType().equalsIgnoreCase(Constants.kAcceptedChallengeCanceledByCreator) ||
                notification.getType().equalsIgnoreCase(Constants.kPadelChallengeAccepted) ||
                notification.getType().equalsIgnoreCase(Constants.kPartnerForChallenge)) {
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                Intent intent = new Intent(getContext(), OlePadelMatchDetailActivity.class);
                intent.putExtra("booking_id", notification.getBookingId());
                startActivity(intent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kChallengePadel) ||
                notification.getType().equalsIgnoreCase(Constants.kPadelChallengeCanceled) ||
                notification.getType().equalsIgnoreCase(Constants.kAcceptedChallengeCanceledByPlayer)) {
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                Intent intent = new Intent(getContext(), OlePadelMatchBookingDetailActivity.class);
                intent.putExtra("booking_id", notification.getBookingId());
                startActivity(intent);
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kLevelCompleted)) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", notification.getSender().getId());
            startActivity(intent);
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kCompletePayment)) {
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                if (notification.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                    Intent intent = new Intent(getContext(), OlePadelNormalBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OlePadelMatchBookingDetailActivity.class);
                    intent.putExtra("booking_id", notification.getBookingId());
                    startActivity(intent);
                }
            }
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kYouWonTheMatch)) {
            getMatchResultAPI(false, notification.getBookingId());
        }
        else if (notification.getType().equalsIgnoreCase(Constants.kYouWonTheMatchPadel)) {
            getMatchResultAPI(true, notification.getBookingId());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDelete) {
            deleteClicked();
        }
    }

    private void deleteClicked() {
        if (oleNotificationList.size() == 0) {
            return;
        }
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.read_all_notifications), getResources().getString(R.string.delete_all_notifications))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            readAllNotificationAPI(true);
                        }
                        else if (index == 1) {
                            deleteAllNotificationAPI(true);
                        }
                    }
                }).show();
    }

    private void getNotifications(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.notificationList(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID),clubId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleNotificationList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleNotificationList.add(gson.fromJson(arr.get(i).toString(), OleNotificationList.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            oleNotificationList.clear();
                            adapter.notifyDataSetChanged();
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

    private void deleteNotificationAPI(boolean isLoader, String notId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteNotification(Functions.getAppLang(getContext()), notId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            adapter.binderHelper.closeLayout(String.valueOf(pos));
                            oleNotificationList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, oleNotificationList.size());
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

    private void deleteAllNotificationAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteAllNotification(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            AppManager.getInstance().notificationCount = 0;
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

    private void readAllNotificationAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.readAllNotification(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            AppManager.getInstance().notificationCount = 0;
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

    private void readNotificationAPI(String notId) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.readNotification(Functions.getAppLang(getContext()), notId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (AppManager.getInstance().notificationCount > 0) {
                                AppManager.getInstance().notificationCount -= 1;
                            }
                        }
                        else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getMatchResultAPI(boolean isPadel, String bookingId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showWinMatchPopup(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            if (isPadel) {
                                OlePadelMatchResults matchResult = new Gson().fromJson(obj.getJSONObject("result").toString(), OlePadelMatchResults.class);
                                Intent intent = new Intent(getContext(), OlePadelResultShareActivity.class);
                                intent.putExtra("result", new Gson().toJson(matchResult));
                                intent.putExtra("club_name", matchResult.getClubName());
                                startActivity(intent);
                            }
                            else {
                                OleMatchResults matchResult = new Gson().fromJson(obj.getJSONObject("result").toString(), OleMatchResults.class);
                                Intent intent = new Intent(getContext(), OleFootballResultShareActivity.class);
                                intent.putExtra("result", new Gson().toJson(matchResult));
                                intent.putExtra("club_name", matchResult.getClubName());
                                startActivity(intent);
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
}
