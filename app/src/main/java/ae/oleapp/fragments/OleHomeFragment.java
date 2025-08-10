package ae.oleapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ae.oleapp.R;
import ae.oleapp.activities.MainActivity;
import ae.oleapp.activities.OleNotificationsActivity;
import ae.oleapp.adapters.OleHomeNotifAdapter;
import ae.oleapp.adapters.OleMatchListAdapter;
import ae.oleapp.adapters.OlePlayerClubListAdapter;
import ae.oleapp.adapters.OleProfilePadelMatchHistoryAdapter;
import ae.oleapp.adapters.OleResultListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentHomeBinding;
import ae.oleapp.dialogs.OleClubRateDialog;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OlePositionDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleHomeNotification;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.models.OlePlayerPosition;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Product;
import ae.oleapp.models.UserInfo;
import ae.oleapp.padel.OlePadelChallengeActivity;
import ae.oleapp.padel.OlePadelMatchBookingDetailActivity;
import ae.oleapp.padel.OlePadelMatchDetailActivity;
import ae.oleapp.padel.OlePadelNormalBookingDetailActivity;
import ae.oleapp.padel.OleProfilePadelMatchHistoryDetailsActivity;
import ae.oleapp.player.OleChatActivity;
import ae.oleapp.player.OleGameBookingDetailActivity;
import ae.oleapp.player.OleGameDetailActivity;
import ae.oleapp.player.OleGameHistoryActivity;
import ae.oleapp.player.OleHistoryDetailActivity;
import ae.oleapp.player.OleMatchBookingDetailActivity;
import ae.oleapp.player.OleMatchDetailActivity;
import ae.oleapp.player.OleMatchHistoryActivity;
import ae.oleapp.player.OleMatchRequestsActivity;
import ae.oleapp.player.OleNormalBookingDetailActivity;
import ae.oleapp.player.OlePClubListActivity;
import ae.oleapp.player.OlePlayerClubDetailActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.shop.ProductDetailActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleEndlessRecyclerViewScrollListener;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleScrollingLinearLayoutManager;
import io.github.hyuwah.draggableviewlib.DraggableListener;
import io.github.hyuwah.draggableviewlib.DraggableView;
import mumayank.com.airlocationlibrary.AirLocation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleHomeFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentHomeBinding binding;
    private OlePlayerClubListAdapter clubListAdapter;
    private OleMatchListAdapter oleMatchListAdapter;
    private OleHomeNotifAdapter oleHomeNotifAdapter;
    private int pageNo = 1;
    private final List<Club> clubList = new ArrayList<>();
    private final List<OlePlayerMatch> matchList = new ArrayList<>();
    private final List<OleHomeNotification> notificationList = new ArrayList<>();
    private final List<OleHomeNotification> challengeReqList = new ArrayList<>();
    private final List<Object> matchResults = new ArrayList<>();
    private final List<Product> productList = new ArrayList<>();
    private Location location;
    private OleEndlessRecyclerViewScrollListener scrollListener;
    private OleClubFilterFragment filterFragment;
    private Timer timer;
    private TimerTask timerTask;
    private OleScrollingLinearLayoutManager notifLayoutManager;
    private final List<OleMatchResults> resultList = new ArrayList<>();
    private final List<OlePadelMatchResults> olePadelMatchResults = new ArrayList<>();
    private OleResultListAdapter oleResultListAdapter;
    private OleProfilePadelMatchHistoryAdapter padelMatchAdapter;
    private DraggableView<MaterialCardView> draggableView;
    private  UserInfo userInfo;


    //filter
    private String date = "";
    private String openTime = "";
    private String closeTime = "";
    private String cityId = "";
    private String countryId = "";
    private String offer = "";
    private final String nearby = "";
    private final String name = "";
    private String fieldSize = "";
    private String fieldType = "";
    private String grassType = "";

    public OleHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.filterBg.setVisibility(View.GONE);
        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.pPadel.setEnabled(false);
            binding.pClubs.setEnabled(true);
            binding.pLineup.setEnabled(true);
            binding.cardVu.setCardBackgroundColor(getResources().getColor(R.color.transparent));
            //binding.rel_menu.setImageResource(R.drawable.p_menu_ic_white);
           //binding.imgVuSearch.setImageResource(R.drawable.search_ic_new_white);
           //binding.tvSearch.setTextColor(getResources().getColor(R.color.whiteColor));
            binding.imgVuFilter.setImageResource(R.drawable.p_filter);
            binding.imgVuNotif.setImageResource(R.drawable.p_bell_icon);
           // binding.switchIcon.setImageResource(R.drawable.football_ball);
            binding.pPadel.setImageResource(R.drawable.p_active_padel);
            binding.pPadelBg.setImageResource(R.drawable.p_padel_bg);
            binding.pClubBg.setImageResource(android.R.color.transparent);

        }
        else if( Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kFootballModule)) {
            binding.pClubs.setImageResource(R.drawable.p_active_clubs);
            binding.pPadel.setEnabled(true);
            binding.pClubs.setEnabled(false);
            binding.pLineup.setEnabled(true);
            binding.cardVu.setCardBackgroundColor(getResources().getColor(R.color.transparent));
           // binding.imgVuMenu.setImageResource(R.drawable.pmenu_ic);
           // binding.imgVuSearch.setImageResource(R.drawable.search_ic_new);
           // binding.tvSearch.setTextColor(Color.parseColor("#979FB3"));
            binding.imgVuFilter.setImageResource(R.drawable.p_filter);
            binding.imgVuNotif.setImageResource(R.drawable.p_bell_icon);
           // binding.switchIcon.setImageResource(R.drawable.padel_ball);
            binding.pClubBg.setImageResource(R.drawable.p_club_bg);
            binding.pPadelBg.setImageResource(android.R.color.transparent);

        }
        else if(Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)){
            binding.pLineup.setImageResource(R.drawable.p_active_lineup);
            binding.pPadel.setEnabled(true);
            binding.pClubs.setEnabled(true);
            binding.pLineup.setEnabled(false);
        }

//        draggableView = new DraggableView.Builder<>(binding.switchVu)
//                .setStickyMode(DraggableView.Mode.NON_STICKY)
//                .setAnimated(true)
//                .setListener(draggableListener)
//                .build();

        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        clubListAdapter = new OlePlayerClubListAdapter(getActivity(), clubList, binding.clubRecyclerVu);
        clubListAdapter.setItemClickListener(itemClickListener);
        binding.clubRecyclerVu.setAdapter(clubListAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.clubRecyclerVu);

        LinearLayoutManager matchLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.matchRecyclerVu.setLayoutManager(matchLayoutManager);
        oleMatchListAdapter = new OleMatchListAdapter(getActivity(), matchList, binding.matchRecyclerVu);
        oleMatchListAdapter.setItemClickListener(matchItemClickListener);
        binding.matchRecyclerVu.setAdapter(oleMatchListAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.matchRecyclerVu);

//        LinearLayoutManager notifLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        binding.notifRecyclerVu.setLayoutManager(notifLayoutManager);
        notifLayoutManager = new OleScrollingLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false, 100);
        binding.notifRecyclerVu.setLayoutManager(notifLayoutManager);
        oleHomeNotifAdapter = new OleHomeNotifAdapter(getActivity(), notificationList, challengeReqList);
        oleHomeNotifAdapter.setItemClickListener(notifItemClickListener);
        binding.notifRecyclerVu.setAdapter(oleHomeNotifAdapter);

        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.resultRecyclerVu.setLayoutManager(resultLayoutManager);
        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            padelMatchAdapter = new OleProfilePadelMatchHistoryAdapter(getContext(), olePadelMatchResults);
            padelMatchAdapter.setItemClickListener(padelResultItemClickListener);
            binding.resultRecyclerVu.setAdapter(padelMatchAdapter);
        }
        else {
            oleResultListAdapter = new OleResultListAdapter(getContext(), resultList);
            oleResultListAdapter.setItemClickListener(footballResultItemClickListener);
            binding.resultRecyclerVu.setAdapter(oleResultListAdapter);
        }
        PagerSnapHelper resultSnapHelper = new PagerSnapHelper();
        resultSnapHelper.attachToRecyclerView(binding.resultRecyclerVu);

        timer = new Timer();
        timerTask = new AutoScrollTask();
        timer.schedule(timerTask, 2000, 3000);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColor));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocationAndCallAPI();
                getPromotionsAPI(false);
                getResultListAPI(false);
            }
        });

        binding.relMenu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        //binding.searchVu.setOnClickListener(this);
        binding.btnClubSeeAll.setOnClickListener(this);
        binding.btnMatchSeeAll.setOnClickListener(this);
        binding.btnResultSeeAll.setOnClickListener(this);
        binding.relFilter.setOnClickListener(this);
        binding.filterBg.setOnClickListener(this);
       // binding.switchVu.setOnClickListener(this);
        binding.pClubs.setOnClickListener(this);
        binding.pLineup.setOnClickListener(this);
        binding.pPadel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        binding = null;
    }

    protected void sendAppLangApi() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);

        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendAppLang(userId,Functions.getAppLang(getContext()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    DraggableListener draggableListener = new DraggableListener() {
        @Override
        public void onPositionChanged(@NonNull View view) {
            if (view.getY() < getResources().getDimension(R.dimen._75sdp)) {
                draggableView.setViewPosition(view.getX(), getResources().getDimension(R.dimen._75sdp));
            }
        }
    };

    OleProfilePadelMatchHistoryAdapter.ItemClickListener padelResultItemClickListener = new OleProfilePadelMatchHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(getContext(), OleProfilePadelMatchHistoryDetailsActivity.class);
            intent.putExtra("match", new Gson().toJson(olePadelMatchResults.get(pos)));
            intent.putExtra("player_id", olePadelMatchResults.get(pos).getCreatedBy().getId());
            startActivity(intent);
        }
    };

    OleResultListAdapter.ItemClickListener footballResultItemClickListener = new OleResultListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(getContext(), OleHistoryDetailActivity.class);
            intent.putExtra("playerId_1", resultList.get(pos).getPlayerOne().getId());
            intent.putExtra("playerId_2", resultList.get(pos).getPlayerTwo().getId());
            startActivity(intent);
        }
    };

    private class AutoScrollTask extends TimerTask {
        int position = 0;
        boolean end = false;
        @Override
        public void run() {
            if (notificationList.isEmpty()) {
                return;
            }
            binding.notifRecyclerVu.post(new Runnable() {
                @Override
                public void run() {
                    if (oleHomeNotifAdapter.getItemCount() > 0) {
                        int nextPage = (notifLayoutManager.findLastVisibleItemPosition() + 1) % oleHomeNotifAdapter.getItemCount();
                        if (binding != null && binding.notifRecyclerVu != null) {
                            binding.notifRecyclerVu.smoothScrollToPosition(nextPage);
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationAndCallAPI();
        getPromotionsAPI(false);
        getResultListAPI(false);
        setBadgeValue();
        userInfo = Functions.getUserinfo(getContext());
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            return;
        }else{
            Glide.with(getContext()).load(userInfo.getEmojiUrl()).into(binding.emojiImgVu);
        }
        Glide.with(getContext()).load(userInfo.getBibUrl()).placeholder(R.drawable.bibl).into(binding.shirtImgVu);

    }

    private void getLocationAndCallAPI() {


        getClubList(clubList.isEmpty());
        new AirLocation(getActivity(), true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location loc) {
                location = loc;
                pageNo = 1;
                getClubList(clubList.isEmpty());
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                binding.pullRefresh.setRefreshing(false);
                pageNo = 1;
                getClubList(clubList.isEmpty());

            }
        });
    }

    private final OleHomeNotifAdapter.ItemClickListener notifItemClickListener = new OleHomeNotifAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleHomeNotification notification = notificationList.get(pos);
            if (notification.getType().equalsIgnoreCase("friendly_game_request")) {
                Intent intent = new Intent(getContext(), OleGameHistoryActivity.class);
                intent.putExtra("match_type", notification.getBookingType());
                intent.putExtra("booking_id", notification.getBookingId());
                intent.putExtra("req_status", notification.getRequestStatus());
                intent.putExtra("player_id", notification.getBy().getId());
                startActivity(intent);
            }
            else if (notification.getType().equalsIgnoreCase("new_challenge")) {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kFootballModule)) {
                    Intent intent = new Intent(getContext(), OleMatchHistoryActivity.class);
                    intent.putExtra("match_type", notification.getBookingType());
                    intent.putExtra("booking_id", notification.getBookingId());
                    intent.putExtra("req_status", notification.getRequestStatus());
                    intent.putExtra("player_id", notification.getBy().getId());
                    startActivity(intent);
                }
            }
            else if (notification.getType().equalsIgnoreCase("next_booking")) {
                if (notification.getBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    if (notification.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                            Intent intent = new Intent(getContext(), OlePadelNormalBookingDetailActivity.class);
                            intent.putExtra("booking_id", notification.getBookingId());
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(getContext(), OleNormalBookingDetailActivity.class);
                            intent.putExtra("booking_id", notification.getBookingId());
                            startActivity(intent);
                        }
                    }
                    else if (notification.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                        Intent intent = new Intent(getContext(), OleGameBookingDetailActivity.class);
                        intent.putExtra("booking_id", notification.getBookingId());
                        startActivity(intent);
                    }
                    else {
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                            Intent intent = new Intent(getContext(), OlePadelMatchBookingDetailActivity.class);
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
                else {
                    if (notification.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                        Intent intent = new Intent(getContext(), OleGameDetailActivity.class);
                        intent.putExtra("booking_id", notification.getBookingId());
                        startActivity(intent);
                    }
                    else {
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                            Intent intent = new Intent(getContext(), OlePadelMatchDetailActivity.class);
                            intent.putExtra("booking_id", notification.getBookingId());
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(getContext(), OleMatchDetailActivity.class);
                            intent.putExtra("booking_id", notification.getBookingId());
                            startActivity(intent);
                        }
                    }
                }
            }
        }

        @Override
        public void replyClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleChatActivity.class);
            intent.putExtra("from_notif", true);
            intent.putExtra("booking_id", notificationList.get(pos).getBookingId());
            startActivity(intent);
        }

        @Override
        public void bookClicked(View view, int pos) {
            Club club = null;
            for (Club c : clubList) {
                if (c.getId().equalsIgnoreCase(notificationList.get(pos).getClubId())) {
                    club = c;
                    break;
                }
            }
            if (club != null) {
                Intent intent = new Intent(getContext(), OlePlayerClubDetailActivity.class);
                Gson gson = new Gson();
                intent.putExtra("club", gson.toJson(club));
                startActivity(intent);
            }
        }

        @Override
        public void acceptClicked(View view, int pos) {
            OleHomeNotification notification = notificationList.get(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.match))
                    .setMessage(R.string.do_you_want_to_accept_request)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                acceptPadelChallengeAPI(true, notification.getBookingId(), notification.getBy().getId(), pos);
                            }
                            else {
                                acceptRequestAPI(true, notification.getBookingId(), notification.getBy().getId(), notification.getBookingType(), notification.getRequestStatus(), "accept", pos);
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

        @Override
        public void sliderItemClicked(View view, int pos) {
            OleHomeNotification notification = challengeReqList.get(pos);
            if (notification.getType().equalsIgnoreCase("friendly_game_request")) {
                Intent intent = new Intent(getContext(), OleGameHistoryActivity.class);
                intent.putExtra("match_type", notification.getBookingType());
                intent.putExtra("booking_id", notification.getBookingId());
                intent.putExtra("req_status", notification.getRequestStatus());
                intent.putExtra("player_id", notification.getBy().getId());
                startActivity(intent);
            }
            else if (notification.getType().equalsIgnoreCase("new_challenge")) {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kFootballModule)) {
                    Intent intent = new Intent(getContext(), OleMatchHistoryActivity.class);
                    intent.putExtra("match_type", notification.getBookingType());
                    intent.putExtra("booking_id", notification.getBookingId());
                    intent.putExtra("req_status", notification.getRequestStatus());
                    intent.putExtra("player_id", notification.getBy().getId());
                    startActivity(intent);
                }
            }
        }

        @Override
        public void sliderAcceptClicked(View view, int pos) {
            OleHomeNotification notification = challengeReqList.get(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.match))
                    .setMessage(R.string.do_you_want_to_accept_request)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                acceptPadelChallengeAPI(true, notification.getBookingId(), notification.getBy().getId(), pos);
                            }
                            else {
                                acceptRequestAPI(true, notification.getBookingId(), notification.getBy().getId(), notification.getBookingType(), notification.getRequestStatus(), "accept", pos);
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

        @Override
        public void productFavClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Product product = productList.get(pos);
            if (product.getIsFavorite().equalsIgnoreCase("1")) {
                removeFromWishlist(true, product.getId(), pos);
            }
            else {
                addToWishlist(true, product.getId(), pos);
            }
        }

        @Override
        public void productItemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("prod_id", productList.get(pos).getId());
            startActivity(intent);
        }
    };

    private final OlePlayerClubListAdapter.ItemClickListener itemClickListener = new OlePlayerClubListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OlePlayerClubDetailActivity.class);
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(clubList.get(pos)));
            startActivity(intent);
        }

        @Override
        public void favClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Club club = clubList.get(pos);
            if (club.getFavorite().equalsIgnoreCase("1")) {
                addRemoveFavClub(true, club.getId(), "0", pos);
            }
            else {
                addRemoveFavClub(true, club.getId(), "1", pos);
            }
        }

        @Override
        public void rateVuClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OleClubRateDialog rateDialog = new OleClubRateDialog(getContext(), clubList.get(pos).getId());
            rateDialog.show();
        }
    };

    private final OleMatchListAdapter.ItemClickListener matchItemClickListener = new OleMatchListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OlePlayerMatch olePlayerMatch = matchList.get(pos);
            if (olePlayerMatch.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                Intent intent = new Intent(getContext(), OleGameDetailActivity.class);
                intent.putExtra("booking_id", matchList.get(pos).getBookingId());
                startActivity(intent);
            }
            else {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                    Intent intent = new Intent(getContext(), OlePadelMatchDetailActivity.class);
                    intent.putExtra("booking_id", matchList.get(pos).getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleMatchDetailActivity.class);
                    intent.putExtra("booking_id", matchList.get(pos).getBookingId());
                    startActivity(intent);
                }
            }
        }

        @Override
        public void joinClicked(View view, int pos) {
            // football challenge
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OlePlayerMatch match = matchList.get(pos);
            if (match.getRemainingPlayers().isEmpty() || match.getRemainingPlayers().equalsIgnoreCase("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.alert))
                        .setMessage(getResources().getString(R.string.player_joined_you_want_notification))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((BaseActivity)getActivity()).gameAvailRequestAPI(match.getBookingId());
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                builder.show();
            }
            else {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                Fragment prev = getChildFragmentManager().findFragmentByTag("PositionDialogFragment");
                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                fragmentTransaction.addToBackStack(null);
                OlePositionDialogFragment dialogFragment = new OlePositionDialogFragment(match.getJoiningFee(), match.getCurrency());
                dialogFragment.setDialogCallback(new OlePositionDialogFragment.PositionDialogCallback() {
                    @Override
                    public void confirmClicked(OlePlayerPosition olePlayerPosition) {
                        ((BaseActivity)getActivity()).openPaymentDialog(match.getJoiningFee(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", true, false, match.getClubId(), new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                joinGameAPI(true, match.getBookingId(), olePlayerPosition.getPositionId(), match.getJoiningFee(), pos, paymentMethod, orderRef, cardPaid, walletPaid);
                            }
                        });
                    }
                });
                dialogFragment.show(fragmentTransaction, "PositionDialogFragment");
            }
        }

        @Override
        public void acceptClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OlePlayerMatch olePlayerMatch = matchList.get(pos);
            if (olePlayerMatch.getRequestStatus().equalsIgnoreCase("request_received")) {
                acceptChallenge(olePlayerMatch.getBookingId(), olePlayerMatch.getBookingType(), olePlayerMatch.getRequestStatus(), olePlayerMatch.getJoiningFee(), olePlayerMatch.getClubId(), pos);
            }
            else {
                joinChallenge(olePlayerMatch.getBookingId(), olePlayerMatch.getBookingType(), olePlayerMatch.getJoiningFee(), olePlayerMatch.getClubId(), pos);
            }
        }

        @Override
        public void challengeClicked(View view, int pos) {
            // padel challenge
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(getContext(), OlePadelChallengeActivity.class);
            intent.putExtra("match", new Gson().toJson(matchList.get(pos)));
            startActivity(intent);
        }
    };

    private void acceptChallenge(String bookingId, String bookingType, String requestStatus, String price, String clubId, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(R.string.do_you_want_to_accept_request)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((BaseActivity)getActivity()).openPaymentDialog(price, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                acceptChallengeAPI(true, bookingId, bookingType, requestStatus, "accept", pos, paymentMethod, orderRef, price, cardPaid, walletPaid);
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

    private void joinChallenge(String bookingId, String bookingType, String price, String clubId, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(R.string.want_to_send_challenge)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((BaseActivity)getActivity()).openPaymentDialog(price, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                joinChallengeAPI(true, bookingId, bookingType, pos, paymentMethod, orderRef, price, cardPaid, walletPaid);
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

    @Override
    public void onClick(View v) {
        if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.btnClubSeeAll) {
            searchClicked(v);
        }
        else if (v == binding.btnMatchSeeAll) {
            matchSeeAllClicked(v);
        }
        else if (v == binding.btnResultSeeAll) {
            resultSeeAllClicked(v);
        }
        else if (v == binding.relFilter) {
            filterClicked();
        }
        else if (v == binding.filterBg) {
            filterBgClicked();
        }
//        else if (v == binding.switchVu) {
//
//        }
        else if (v == binding.pClubs){
            switchToClubs();
            binding.pPadel.setImageResource(R.drawable.p_padel);
            binding.pClubs.setImageResource(R.drawable.p_active_clubs);
            binding.pLineup.setImageResource(R.drawable.p_lineup);
        }
        else if (v == binding.pLineup){
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_to_use_lineup), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            }else{
                Functions.setAppLang(getContext(), "en");
                Functions.changeLanguage(getContext(),"en");
                sendAppLangApi();
                switchToLineup();
                binding.pPadel.setImageResource(R.drawable.p_padel);
                binding.pClubs.setImageResource(R.drawable.p_clubs);
                binding.pLineup.setImageResource(R.drawable.p_active_lineup);
            }

        }
        else if (v == binding.pPadel){
            switchToPadel();
            binding.pPadel.setImageResource(R.drawable.p_active_padel);
            binding.pClubs.setImageResource(R.drawable.p_clubs);
            binding.pLineup.setImageResource(R.drawable.p_lineup);
        }
    }

    private void resultSeeAllClicked(View v) {
        Intent intent = new Intent(getContext(), OleMatchRequestsActivity.class);
        intent.putExtra("is_result", true);
        startActivity(intent);
    }

    private void switchToPadel() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kAppModule, Constants.kPadelModule);
        editor.apply();
        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();

    }
    private void switchToClubs() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kAppModule, Constants.kFootballModule);

        editor.apply();
        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();

    }
    private void switchToLineup() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kAppModule, Constants.kLineupModule);
        editor.apply();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void menuClicked() {
        if (getActivity() instanceof OlePlayerMainTabsActivity) {
            ((OlePlayerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OlePlayerMainTabsActivity) {
            ((OlePlayerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    private void searchClicked(View view) {
        Intent intent = new Intent(getContext(), OlePClubListActivity.class);
        intent.putExtra("is_search", view == binding.btnClubSeeAll);
        startActivity(intent);
    }

    private void matchSeeAllClicked(View view) {
        Intent intent = new Intent(getContext(), OleMatchRequestsActivity.class);
        startActivity(intent);
    }

    private void filterClicked() {
        if (filterFragment != null && filterFragment.isVisible()) {
            removeFilterFrag();
        }
        else {
            binding.filterBg.setVisibility(View.VISIBLE);
            //binding.switchVu.setVisibility(View.GONE);
            filterFragment = new OleClubFilterFragment();
            filterFragment.date = date;
            filterFragment.countryId = countryId;
            filterFragment.cityId = cityId;
            filterFragment.offer = offer;
            filterFragment.openTime = openTime;
            filterFragment.closeTime = closeTime;
            filterFragment.fieldSize = fieldSize;
            filterFragment.fieldType = fieldType;
            filterFragment.grassType = grassType;
            filterFragment.setFragmentCallBack(callBack);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.filter_container, filterFragment).commit();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void filterBgClicked() {
        removeFilterFrag();
    }

    private void removeFilterFrag() {
        getChildFragmentManager().beginTransaction().remove(filterFragment).commit();
        filterFragment = null;
        binding.filterBg.setVisibility(View.GONE);
        //binding.switchVu.setVisibility(View.VISIBLE);
    }

    OleClubFilterFragment.ClubFilterFragmentCallBack  callBack = new OleClubFilterFragment.ClubFilterFragmentCallBack() {
        @Override
        public void getFilters(String date, String countryId, String cityId, String offer, String openTime, String closeTime, String fieldSize, String fieldType, String grassType) {
            OleHomeFragment.this.date = date;
            OleHomeFragment.this.countryId = countryId;
            OleHomeFragment.this.cityId = cityId;
            OleHomeFragment.this.offer = offer;
            OleHomeFragment.this.openTime = openTime;
            OleHomeFragment.this.closeTime = closeTime;
            OleHomeFragment.this.fieldSize = fieldSize;
            OleHomeFragment.this.fieldType = fieldType;
            OleHomeFragment.this.grassType = grassType;

            pageNo = 1;
            OleHomeFragment.this.getClubList(true);

            removeFilterFrag();
        }
    };

    private void getClubList(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        if (location == null) {
            call = AppManager.getInstance().apiInterface.getClubList(Functions.getAppLang(getActivity()), Functions.getPrefValue(getActivity(), Constants.kUserID),   0, 0, pageNo, offer, name, date, openTime, closeTime, cityId, grassType, fieldType, fieldSize, nearby, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        else {
            call = AppManager.getInstance().apiInterface.getClubList(Functions.getAppLang(getActivity()), Functions.getPrefValue(getActivity(), Constants.kUserID),   location.getLatitude(), location.getLongitude(), pageNo, offer, name, date, openTime, closeTime, cityId, grassType, fieldType, fieldSize, nearby, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (binding != null) {
                    binding.pullRefresh.setRefreshing(false);
                }
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            clubList.clear();
                            matchList.clear();
                            JSONArray arrM = object.getJSONArray("matches");
                            for (int i = 0; i < arrM.length(); i++) {
                                OlePlayerMatch match = gson.fromJson(arrM.get(i).toString(), OlePlayerMatch.class);
                                if (!match.getMyStatus().equalsIgnoreCase("canceled")) {
                                    matchList.add(match);
                                }
                            }
                            for (int i = 0; i < arr.length(); i++) {
                                clubList.add(gson.fromJson(arr.get(i).toString(), Club.class));
                            }
                            AppManager.getInstance().clubs = clubList;
                            clubListAdapter.notifyDataSetChanged();
                            oleMatchListAdapter.notifyDataSetChanged();
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
                if (binding != null) {
                    binding.pullRefresh.setRefreshing(false);
                }
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void getPromotionsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPromotions(Functions.getAppLang(getActivity()),Functions.getPrefValue(getActivity(), Constants.kUserID), Functions.getPrefValue(getActivity(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (binding != null) {
                    binding.pullRefresh.setRefreshing(false);
                }
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            JSONArray arrExtra = object.getJSONArray("extras");
                            JSONArray arrProduct = object.getJSONArray("products");
                            JSONArray arrR;
                            if (Functions.getPrefValue(getActivity(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                arrR = object.getJSONArray("padel_matches_result");
                            }
                            else {
                                arrR = object.getJSONArray("matches_result");
                            }
                            Gson gson = new Gson();
                            notificationList.clear();
                            challengeReqList.clear();
                            productList.clear();
                            matchResults.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                notificationList.add(gson.fromJson(arr.get(i).toString(), OleHomeNotification.class));
                            }
                            for (int i = 0; i < arrExtra.length(); i++) {
                                challengeReqList.add(gson.fromJson(arrExtra.get(i).toString(), OleHomeNotification.class));
                            }
                            for (int i = 0; i < arrProduct.length(); i++) {
                                productList.add(gson.fromJson(arrProduct.get(i).toString(), Product.class));
                            }
                            for (int i = 0; i < arrR.length(); i++) {
                                if (Functions.getPrefValue(getActivity(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                    matchResults.add(gson.fromJson(arrR.get(i).toString(), OlePadelMatchResults.class));
                                }
                                else {
                                    matchResults.add(gson.fromJson(arrR.get(i).toString(), OleMatchResults.class));
                                }
                            }
                            if (productList.size() > 0) {
                                OleHomeNotification oleHomeNotification = new OleHomeNotification();
                                oleHomeNotification.setType("product");
                                notificationList.add(0, oleHomeNotification);
                                oleHomeNotifAdapter.setProductList(productList);
                            }
                            if (challengeReqList.size() > 0) {
                                OleHomeNotification oleHomeNotification = new OleHomeNotification();
                                oleHomeNotification.setType("challenge");
                                notificationList.add(0, oleHomeNotification);
                                oleHomeNotifAdapter.setChallengeList(challengeReqList);
                            }
                            if (matchResults.size() > 0) {
                                OleHomeNotification oleHomeNotification = new OleHomeNotification();
                                oleHomeNotification.setType("match_result");
                                notificationList.add(0, oleHomeNotification);
                                oleHomeNotifAdapter.setResultList(matchResults);
                            }
                            oleHomeNotifAdapter.notifyDataSetChanged();
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
                if (binding != null) {
                    binding.pullRefresh.setRefreshing(false);
                }
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void getResultListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.allMatches(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, "", "", "", "", "1", Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrR = object.getJSONArray("matches_result");
                            JSONArray arrPR = object.getJSONArray("padel_matches_result");
                            Gson gson = new Gson();
                            resultList.clear();
                            olePadelMatchResults.clear();
                            for (int i = 0; i < arrR.length(); i++) {
                                resultList.add(gson.fromJson(arrR.get(i).toString(), OleMatchResults.class));
                            }
                            for (int i = 0; i < arrPR.length(); i++) {
                                olePadelMatchResults.add(gson.fromJson(arrPR.get(i).toString(), OlePadelMatchResults.class));
                            }
//                            Glide.with(getContext()).load(userInfo.getEmojiUrl()).into(binding.emojiImgVu);
//                            Glide.with(getContext()).load(userInfo.getBibUrl()).placeholder(R.drawable.bibl).into(binding.shirtImgVu);
                        }
                        else {
                            resultList.clear();
                            olePadelMatchResults.clear();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                            padelMatchAdapter.notifyDataSetChanged();
                        }
                        else {
                            oleResultListAdapter.notifyDataSetChanged();
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

    private void addRemoveFavClub(boolean isLoader, String clubId, String status, int pos) {
        KProgressHUD hud = Functions.showLoader(getActivity(), "Image processing");
        ((OlePlayerMainTabsActivity)getActivity()).addRemoveFavAPI(clubId, status, "club", new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(getContext(), msg, FancyToast.SUCCESS);
                    clubList.get(pos).setFavorite(status);
                    int favCount = Integer.parseInt(clubList.get(pos).getFavoriteCount());
                    if (status.equalsIgnoreCase("0")) {
                        if (favCount>0) {
                            favCount -= 1;
                        }
                    }
                    else {
                        favCount += 1;
                    }
                    clubList.get(pos).setFavoriteCount(String.valueOf(favCount));
                    clubListAdapter.notifyItemChanged(pos);
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void acceptChallengeAPI(boolean isLoader, String bookingId, String matchType, String requestStatus, String flag, int pos, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptRejectChallenge(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID),  bookingId, "", matchType, requestStatus, flag, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress(), joinFee);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchList.get(pos).setMyStatus("accepted");
                            oleMatchListAdapter.notifyDataSetChanged();
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

    private void acceptRequestAPI(boolean isLoader, String bookingId, String playerId, String matchType, String requestStatus, String flag, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptRejectChallenge(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId, matchType, requestStatus, flag);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            notificationList.remove(pos);
                            oleHomeNotifAdapter.notifyDataSetChanged();
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

    private void acceptPadelChallengeAPI(boolean isLoader, String bookingId, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptChallenge(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            challengeReqList.remove(pos);
                            oleHomeNotifAdapter.notifyDataSetChanged();
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

    private void joinChallengeAPI(boolean isLoader, String bookingId, String matchType, int pos, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.joinChallenge(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, matchType, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress(), joinFee);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchList.get(pos).setMyStatus("pending");
                            oleMatchListAdapter.notifyDataSetChanged();
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

    private void joinGameAPI(boolean isLoader, String bookingId, String position, String fee, int pos, String paymentMethod, String orderRef, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.joinGame(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, position, fee, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchList.get(pos).setMyStatus("pending");
                            oleMatchListAdapter.notifyDataSetChanged();
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

    private void addToWishlist(boolean isLoader, String prodId, int pos) {
        KProgressHUD hud = Functions.showLoader(getActivity(), "Image processing");
        ((BaseActivity)getActivity()).addToWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    productList.get(pos).setIsFavorite("1");
                    oleHomeNotifAdapter.notifyDataSetChanged();
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void removeFromWishlist(boolean isLoader, String prodId, int pos) {
        KProgressHUD hud = Functions.showLoader(getActivity(), "Image processing");
        ((BaseActivity)getActivity()).removeFromWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    productList.get(pos).setIsFavorite("0");
                    oleHomeNotifAdapter.notifyDataSetChanged();
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }
}