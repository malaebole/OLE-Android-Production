package ae.oleapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OlePlayerBookingListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentPBookingListBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.padel.OleCreatePadelMatchActivity;
import ae.oleapp.padel.OlePadelMatchBookingDetailActivity;
import ae.oleapp.padel.OlePadelNormalBookingDetailActivity;
import ae.oleapp.player.OleCreateMatchActivity;
import ae.oleapp.player.OleGameBookingDetailActivity;
import ae.oleapp.player.OleMatchBookingDetailActivity;
import ae.oleapp.player.OleNormalBookingDetailActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OlePBookingListFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentPBookingListBinding binding;
    private final String kBookDateFormat = "dd/MM/yyyy";
    private String fromDate = "";
    private String toDate = "";
    private int selectedDateIndex = 0;
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private final List<OlePlayerBookingList> bookingList = new ArrayList<>();
    private OleBookingDateAdapter oleBookingDateAdapter;
    private OlePlayerBookingListAdapter bookingListAdapter;
    private String isAllDates = "0";

    public OlePBookingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPBookingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.cardVu.setCardBackgroundColor(getResources().getColor(R.color.blueColorNew));
            binding.imgVuMenu.setImageResource(R.drawable.p_menu_ic_white);
            binding.tvTitle.setTextColor(getResources().getColor(R.color.whiteColor));
            binding.imgVuCalendar.setImageResource(R.drawable.calendar_ic_new_white);
            binding.imgVuNotif.setImageResource(R.drawable.p_notification_ic_white);
        }
        else {
            binding.cardVu.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.imgVuMenu.setImageResource(R.drawable.p_menu_ic);
            binding.tvTitle.setTextColor(getResources().getColor(R.color.darkTextColor));
            binding.imgVuCalendar.setImageResource(R.drawable.calendar_ic_new);
            binding.imgVuNotif.setImageResource(R.drawable.p_notification_ic);
        }

        LinearLayoutManager horizLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(horizLayoutManager);
        oleBookingDateAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
        oleBookingDateAdapter.setOnItemClickListener(clickListener);
        binding.dateRecyclerVu.setAdapter(oleBookingDateAdapter);

        LinearLayoutManager vertLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.listRecyclerVu.setLayoutManager(vertLayoutManager);
        bookingListAdapter = new OlePlayerBookingListAdapter(getActivity(), bookingList, true);
        bookingListAdapter.setItemClickListener(itemClickListener);
        binding.listRecyclerVu.setAdapter(bookingListAdapter);

        selectedDateIndex = 0;
        fromDate = "";

        if (Functions.getPrefValue(getActivity(), Constants.kBookingDateIndex).equalsIgnoreCase("0")) {
            isAllDates = "1";
            fromDate = getDateStr(new Date());
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0), true);
        }
        else {
            isAllDates = "0";
            fromDate = "";
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1), true);
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                if (tab.getPosition() == 0) {
                    isAllDates = "1";
                    selectedDateIndex = 0;
                    fromDate = getDateStr(new Date());
                    editor.putString(Constants.kBookingDateIndex, "0");
                    getBookingList(true, "1", getDateStr(new Date()));
                }
                else {
                    isAllDates = "0";
                    selectedDateIndex = 0;
                    fromDate = "";
                    editor.putString(Constants.kBookingDateIndex, "1");
                    getBookingList(true, "1", getDateStr(new Date()));
                }
                editor.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.relCalendar.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBadgeValue();
        getBookingList(true, "1", getDateStr(new Date()));
        if (oleBookingDateAdapter != null) {
            oleBookingDateAdapter.setDataSource(arrDate.toArray());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.relCalendar) {
            calendarClicked();
        }
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

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    OlePlayerBookingListAdapter.ItemClickListener itemClickListener = new OlePlayerBookingListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OlePlayerBookingList booking = bookingList.get(pos);
            if (booking.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                    Intent intent = new Intent(getContext(), OlePadelNormalBookingDetailActivity.class);
                    intent.putExtra("booking_id", booking.getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleNormalBookingDetailActivity.class);
                    intent.putExtra("booking_id", booking.getBookingId());
                    startActivity(intent);
                }
            }
            else if (booking.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                Intent intent = new Intent(getContext(), OleGameBookingDetailActivity.class);
                intent.putExtra("booking_id", booking.getBookingId());
                startActivity(intent);
            }
            else {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                    Intent intent = new Intent(getContext(), OlePadelMatchBookingDetailActivity.class);
                    intent.putExtra("booking_id", booking.getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleMatchBookingDetailActivity.class);
                    intent.putExtra("booking_id", booking.getBookingId());
                    startActivity(intent);
                }
            }
        }

        @Override
        public void createMatchClicked(View view, int pos) {
            OlePlayerBookingList booking = bookingList.get(pos);
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                if (!booking.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    Functions.showToast(getContext(), getResources().getString(R.string.booking_holder_can_create_match), FancyToast.ERROR);
                    return;
                }
                if (booking.getMatchAllowed().equalsIgnoreCase("1")) {
                    if (booking.getDuration().equalsIgnoreCase("60 min")) {
                        Functions.showToast(getContext(), getResources().getString(R.string.padel_match_duration_error), FancyToast.ERROR);
                        return;
                    }
                    if (Functions.getUserinfo(getContext()).getPhotoUrl().equalsIgnoreCase("")) {
                        Functions.showToast(getContext(), getResources().getString(R.string.upload_your_profile_photo_first), FancyToast.ERROR);
                        return;
                    }
                    if (booking.getPartnerCount() > 1) {
                        Functions.showToast(getContext(), getResources().getString(R.string.padel_match_partner_count_error), FancyToast.ERROR);
                        return;
                    }
                    // create match
                    Intent intent = new Intent(getContext(), OleCreatePadelMatchActivity.class);
                    intent.putExtra("booking_id", booking.getBookingId());
                    intent.putExtra("club_id", booking.getClubId());
                    intent.putExtra("payment_method", booking.getPaymentMethod());
                    intent.putExtra("match_allow", booking.getMatchAllowed());
                    if (booking.getJoinedPlayers().size() > 0) {
                        intent.putExtra("partner", new Gson().toJson(booking.getJoinedPlayers().get(0)));
                    }
                    startActivity(intent);
                }
                else {
                    Functions.showToast(getContext(), getResources().getString(R.string.club_not_allow_match), FancyToast.ERROR);
                }
            }
            else {
                if (booking.getMatchAllowed().equalsIgnoreCase("1") || booking.getGamesAllowed().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(getContext(), OleCreateMatchActivity.class);
                    intent.putExtra("booking_id", booking.getBookingId());
                    intent.putExtra("club_id", booking.getClubId());
                    intent.putExtra("payment_method", booking.getPaymentMethod());
                    intent.putExtra("is_update", false);
                    intent.putExtra("match_allow", booking.getMatchAllowed());
                    intent.putExtra("game_allow", booking.getGamesAllowed());
                    startActivity(intent);
                } else {
                    Functions.showToast(getContext(), getResources().getString(R.string.club_not_allow_match), FancyToast.ERROR);
                }
            }
        }
    };

    OleBookingDateAdapter.OnItemClickListener clickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDateIndex = pos;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getDefault());
            try {
                Date dt = dateFormat.parse(arrDate.get(pos).getDate());
                dateFormat.applyPattern(kBookDateFormat);
                fromDate = dateFormat.format(dt);
            } catch (ParseException e) {
                e.printStackTrace();
                fromDate = "";
            }
            oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
            toDate = "";
            getBookingList(true, "0", "");
        }
    };

    private String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookDateFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    private void calendarClicked() {
        ((BaseActivity)getActivity()).showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
            @Override
            public void filterData(DialogFragment df, String from, String to) {
                df.dismiss();
                if (from.isEmpty() && to.isEmpty()) {
                    fromDate = getDateStr(new Date());
                    toDate = "";
                }
                else {
                    fromDate = from;
                    toDate = to;
                }
                getBookingList(true, "0", "");
                selectedDateIndex = -1;
                oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
            }
        });
    }

    private void getBookingList(boolean isLoader, String isDateNeeded, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerBookings(Functions.getAppLang(getActivity()), Functions.getPrefValue(getActivity(), Constants.kUserID),
                fromDate, toDate, isDateNeeded, date, isAllDates, "", "", Functions.getPrefValue(getActivity(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            bookingList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                bookingList.add(gson.fromJson(arr.get(i).toString(), OlePlayerBookingList.class));
                            }
                            bookingListAdapter.notifyDataSetChanged();
                            if (isDateNeeded.equalsIgnoreCase("1"))  {
                                arrDate.clear();
                                JSONArray arrD = object.getJSONArray("dates");
                                for (int i = 0; i < arrD.length(); i++) {
                                    arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                                }
                                oleBookingDateAdapter.setDataSource(arrDate.toArray());
                            }
                            if (bookingList.size() == 0) {
//                                Functions.showToast(getContext(), getString(R.string.booking_not_found), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                            }
                        }
                        else {
                            bookingList.clear();
                            bookingListAdapter.notifyDataSetChanged();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
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
