package ae.oleapp.player;

import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.special.ResideMenu.ResideMenu;

import org.json.JSONObject;

import java.util.List;
import ae.oleapp.R;
import ae.oleapp.activities.OleNotificationsActivity;
import ae.oleapp.adapters.OleMyPagerAdapter;
import ae.oleapp.base.BaseTabActivity;
import ae.oleapp.databinding.OleactivityPlayerMainTabsBinding;
import ae.oleapp.dialogs.OleLoyaltyCardDialogFragment;
import ae.oleapp.dialogs.OleWinMatchDialogFragment;
import ae.oleapp.fragments.OleFavoriteFragment;
import ae.oleapp.fragments.OleHomeFragment;
import ae.oleapp.fragments.OleMatchesFragment;
import ae.oleapp.fragments.OlePBookingListFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OlePlayerLevel;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleBookingActivity;
import ae.oleapp.shop.ShopTabFragment;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleCustomTabView;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerMainTabsActivity extends BaseTabActivity {

    private OleactivityPlayerMainTabsBinding binding;
    private OleMyPagerAdapter adapter;
    private final OleHomeFragment oleHomeFragment = new OleHomeFragment();
    private final OlePBookingListFragment bookingListFragment = new OlePBookingListFragment();
    private final OleMatchesFragment oleMatchesFragment = new OleMatchesFragment();
    private final OleFavoriteFragment oleFavoriteFragment = new OleFavoriteFragment();
    private final ShopTabFragment shopTabFragment = new ShopTabFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerMainTabsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewPager(binding.content.contentMain.viewPager);
        setupTabLayout();
        setupMenu();

//        inAppUpdates();


        binding.content.contentMain.viewPager.disableScroll(!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1"));

        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            LinearLayout tabStrip = ((LinearLayout) binding.content.contentMain.tabLayout.getChildAt(0));
            tabStrip.getChildAt(1).setOnTouchListener(onTouchListener);
            tabStrip.getChildAt(2).setOnTouchListener(onTouchListener);
            tabStrip.getChildAt(4).setOnTouchListener(onTouchListener);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int index = bundle.getInt("tab_position", 0);
            binding.content.contentMain.tabLayout.setScrollPosition(index, 0, true);
            binding.content.contentMain.viewPager.setCurrentItem(index);
        }
        getCountriesAPI(new CountriesCallback() {
            @Override
            public void getCountries(List<OleCountry> countries) {
                AppManager.getInstance().countries = countries;
            }
        });

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            paymentVu.setVisibility(View.GONE);
            addPriceVu.setVisibility(View.GONE);
            oleCreditVu.setVisibility(View.GONE);
            savedCardVu.setVisibility(View.GONE);
            wishlistVu.setVisibility(View.GONE);
            shopOrderVu.setVisibility(View.GONE);
            //switchVu.setVisibility(View.GONE);
            globalRankVu.setVisibility(View.GONE);
        } else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            addPriceVu.setVisibility(View.GONE);
            scheduleVu.setVisibility(View.GONE);
            shareVu.setVisibility(View.GONE);
            playerSearchVu.setVisibility(View.GONE);
            membershipPlansVu.setVisibility(View.GONE);
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                globalRankVu.setVisibility(View.VISIBLE);
            }
            else {
                globalRankVu.setVisibility(View.GONE);
            }
        } else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kRefereeType)) {
            paymentVu.setVisibility(View.GONE);
            settingVu.setVisibility(View.GONE);
            scheduleVu.setVisibility(View.GONE);
            shareVu.setVisibility(View.GONE);
            playerSearchVu.setVisibility(View.GONE);
            membershipPlansVu.setVisibility(View.GONE);
            savedCardVu.setVisibility(View.GONE);
            oleCreditVu.setVisibility(View.GONE);
            rankVu.setVisibility(View.GONE);
            wishlistVu.setVisibility(View.GONE);
            shopOrderVu.setVisibility(View.GONE);
           // switchVu.setVisibility(View.GONE);
            globalRankVu.setVisibility(View.GONE);
        }

        // temporary
        savedCardVu.setVisibility(View.GONE);
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    return false;
                } else {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                    return true;
                }
            }
            return true;
        }
    };

    private void callUnreadNotifAPI() {
        getUnreadNotificationAPI(new UnreadCountCallback() {
            @Override
            public void unreadNotificationCount(int count) {
                AppManager.getInstance().notificationCount = count;
                setBadgeValue();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Functions.getPrefValue(getContext(),Constants.kIsSignIn).equalsIgnoreCase("1")){
            callUnreadNotifAPI();
            checkLoyaltyAPI();
        }
        //callUnreadNotifAPI();
        populateSideMenuData();
       // checkLoyaltyAPI();
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive_push"));
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            AppManager.getInstance().notificationCount += 1;
            setBadgeValue();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        if (resideMenu.isOpened()) {
            resideMenu.closeMenu();
        }
        else {
            super.onBackPressed();
        }
    }

    private void populateSideMenuData() {
        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo != null) {
            tvName.setText(userInfo.getName());
            Glide.with(getApplicationContext()).load(userInfo.getBibUrl()).placeholder(R.drawable.bibl).into(shirtImgVu);  //(ImageView) menuVu.findViewById(R.id.shirt_img_vu));
            Glide.with(getApplicationContext()).load(userInfo.getEmojiUrl()).into(emojiImgVu);
            if (userInfo.getLevel() != null && !userInfo.getLevel().isEmpty() && !userInfo.getLevel().getValue().equalsIgnoreCase("")) {
                tvRank.setVisibility(View.VISIBLE);
                tvRank.setText(String.format("LV: %s", userInfo.getLevel().getValue()));
            }
            else {
                tvRank.setVisibility(View.INVISIBLE);
            }
        }
        else {
            tvName.setText(R.string.guest);
            userImageVu.setImageResource(R.drawable.player_active);
            tvRank.setVisibility(View.INVISIBLE);
        }
        setBadgeValue();

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            imgVuSide.setImageResource(R.drawable.sidemenu_padel);
            // imgVuFootball.setVisibility(View.GONE);
            // imgVuPadel.setVisibility(View.VISIBLE);
           //  tvFootball.setTextColor(getResources().getColor(R.color.greenColor));
           //  tvPadel.setTextColor(getResources().getColor(R.color.whiteColor));
        }
        else {
            imgVuSide.setImageResource(R.drawable.sidemenu_football);
            //  imgVuFootball.setVisibility(View.VISIBLE);
            //  imgVuPadel.setVisibility(View.GONE);
            //  tvFootball.setTextColor(getResources().getColor(R.color.whiteColor));
            //  tvPadel.setTextColor(getResources().getColor(R.color.greenColor));
        }
    }

    private void setBadgeValue() {
        if (oleHomeFragment.isVisible()) {
            oleHomeFragment.setBadgeValue();
        }
        else if (bookingListFragment.isVisible()) {
            bookingListFragment.setBadgeValue();
        }
        else if (oleMatchesFragment.isVisible()) {
            oleMatchesFragment.setBadgeValue();
        }
        else if (oleFavoriteFragment.isVisible()) {
            oleFavoriteFragment.setBadgeValue();
        }
        else if (shopTabFragment.isVisible()) {
            shopTabFragment.setBadgeValue();
        }
    }

    private void setupTabLayout() {
        binding.content.contentMain.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.content.contentMain.tabLayout.setupWithViewPager(binding.content.contentMain.viewPager);
        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.content.contentMain.tabLayout.setBackgroundColor(getResources().getColor(R.color.blueColorNew));
        }
        else {
            binding.content.contentMain.tabLayout.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        }
        for (int i = 0; i < binding.content.contentMain.tabLayout.getTabCount(); i++) {
            OleCustomTabView tabItemVu = adapter.getTabView(i);
            binding.content.contentMain.tabLayout.getTabAt(i).setCustomView(tabItemVu);
            if(i == 0) {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                    tabItemVu.iconVu.getDrawable().setColorFilter(getResources().getColor(R.color.yellowColor), PorterDuff.Mode.SRC_IN);
                    tabItemVu.tvTitle.setTextColor(getResources().getColor(R.color.yellowColor));
                }
                else {
                    tabItemVu.iconVu.getDrawable().setColorFilter(getResources().getColor(R.color.blueColorNew), PorterDuff.Mode.SRC_IN);
                    tabItemVu.tvTitle.setTextColor(getResources().getColor(R.color.blueColorNew));
                }
            }
            else {
                tabItemVu.iconVu.getDrawable().setColorFilter(getResources().getColor(R.color.separatorColor), PorterDuff.Mode.SRC_IN);
                tabItemVu.tvTitle.setTextColor(getResources().getColor(R.color.separatorColor));
            }
        }
        binding.content.contentMain.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                    ((OleCustomTabView)tab.getCustomView()).iconVu.getDrawable().setColorFilter(getResources().getColor(R.color.yellowColor), PorterDuff.Mode.SRC_IN);
                    ((OleCustomTabView)tab.getCustomView()).tvTitle.setTextColor(getResources().getColor(R.color.yellowColor));
                }
                else {
                    ((OleCustomTabView)tab.getCustomView()).iconVu.getDrawable().setColorFilter(getResources().getColor(R.color.blueColorNew), PorterDuff.Mode.SRC_IN);
                    ((OleCustomTabView)tab.getCustomView()).tvTitle.setTextColor(getResources().getColor(R.color.blueColorNew));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((OleCustomTabView)tab.getCustomView()).iconVu.getDrawable().setColorFilter(getResources().getColor(R.color.separatorColor), PorterDuff.Mode.SRC_IN);
                ((OleCustomTabView)tab.getCustomView()).tvTitle.setTextColor(getResources().getColor(R.color.separatorColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new OleMyPagerAdapter(getContext(), getSupportFragmentManager());
        adapter.addFrag(oleHomeFragment, "");
        adapter.addFrag(bookingListFragment, "");
        adapter.addFrag(oleMatchesFragment, "");
        adapter.addFrag(shopTabFragment, "");
        adapter.addFrag(oleFavoriteFragment, "");
        viewPager.setAdapter(adapter);
    }

    public void notificationsClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        startActivity(new Intent(getContext(), OleNotificationsActivity.class));
    }

    public void menuClicked() {
        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
            resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
        }
        else {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        }
    }

    private void checkLevelAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.levelsTargetStatus(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            OlePlayerLevel olePlayerLevel = gson.fromJson(obj.toString(), OlePlayerLevel.class);
                            DialogFragment dialogFragment = gotoLevelDialog(olePlayerLevel);
                            if (dialogFragment.getDialog() != null) {
                                dialogFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        checkRestrictionAPI();
                                    }
                                });
                            }
                        }
                        else {
                            checkRestrictionAPI();
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

    private void checkLoyaltyAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.loyaltyTargetStatus(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            String discount = obj.getString("card_discount_value");
                            String target = obj.getString("discount_booking_target");
                            String expiry = obj.getString("card_discount_expiry");
                            String playerBookings = obj.getString("player_bookings");
                            String remainBookings = obj.getString("remaining_bookings");
                            String popupId = obj.getString("popup_id");
                            String popupType = obj.getString("popup_type");
                            Club club = gson.fromJson(obj.getJSONObject("club").toString(), Club.class);
                            gotoLoyaltyCardDialog(club, discount, target, expiry, playerBookings, remainBookings, popupId, popupType, new OleLoyaltyCardDialogFragment.LoyaltyCardDialogCallback() {
                                @Override
                                public void bookClicked(DialogFragment dialogFragment) {
                                    dialogFragment.dismiss();
                                    Intent intent = new Intent(getContext(), OleBookingActivity.class);
                                    intent.putExtra("field_id", "");
                                    Gson gson = new Gson();
                                    intent.putExtra("club", gson.toJson(club));
                                    startActivity(intent);
                                }

                                @Override
                                public void onDismiss(DialogFragment dialogFragment) {
                                    dialogFragment.dismiss();
                                    checkWinMatchAPI();
                                }
                            });
                        }
                        else {
                            checkWinMatchAPI();
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

    private void checkRestrictionAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showRestrictionPopup(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String title = obj.getString("title");
                            String msg = obj.getString("message");
                            String popId = obj.getString("id");
                            gotoRestrictDialog(popId, title, msg);
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

    private void checkWinMatchAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showWinMatchPopup(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), "", Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String msg = obj.getString("message");
                            String popId = obj.getString("id");
                            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                OlePadelMatchResults matchResult = new Gson().fromJson(obj.getJSONObject("result").toString(), OlePadelMatchResults.class);
                                gotoWinPadelMatchDialog(popId, msg, matchResult, new OleWinMatchDialogFragment.WinMatchDialogFragmentCallback() {
                                    @Override
                                    public void shareClicked(DialogFragment df) {
                                        df.dismiss();
                                        Intent intent = new Intent(getContext(), OlePadelResultShareActivity.class);
                                        intent.putExtra("result", new Gson().toJson(matchResult));
                                        intent.putExtra("club_name", matchResult.getClubName());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onDismiss(DialogFragment df) {
                                        df.dismiss();
                                        checkEmpRateAPI();
                                    }
                                });
                            }
                            else {
                                OleMatchResults matchResult = new Gson().fromJson(obj.getJSONObject("result").toString(), OleMatchResults.class);
                                gotoWinFootballMatchDialog(popId, msg, matchResult, new OleWinMatchDialogFragment.WinMatchDialogFragmentCallback() {
                                    @Override
                                    public void shareClicked(DialogFragment df) {
                                        df.dismiss();
                                        Intent intent = new Intent(getContext(), OleFootballResultShareActivity.class);
                                        intent.putExtra("result", new Gson().toJson(matchResult));
                                        intent.putExtra("club_name", matchResult.getClubName());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onDismiss(DialogFragment df) {
                                        df.dismiss();
                                        checkEmpRateAPI();
                                    }
                                });
                            }
                        }
                        else {
                            checkEmpRateAPI();
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

    private void checkEmpRateAPI() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.showEmpRatePopup(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String bookingId = obj.getString("booking_id");
                            String clubId = obj.getString("club_id");
                            String isRated = obj.getString("is_rated");
                            gotoEmpRate(bookingId, clubId, isRated);
                        }
                        else {
                            checkLevelAPI();
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
