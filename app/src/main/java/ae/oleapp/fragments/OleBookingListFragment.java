package ae.oleapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleBookingListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.base.BaseTabActivity;
import ae.oleapp.databinding.OlefragmentBookingListBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleBookingList;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.owner.OleBookingDetailActivity;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.owner.OlePadelBookingDetailActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class OleBookingListFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentBookingListBinding binding;
    private final String kBookDateFormat = "dd/MM/yyyy";
    private String fromDate = "";
    private String prevNextDate = "";
    private String type = "";
    private String toDate = "";
    private int selectedDateIndex = 0;
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private final List<Field> fieldList = new ArrayList<>();
    private String bookingType = "";
    private OleBookingDateAdapter oleBookingDateAdapter;
    private OleBookingListAdapter oleBookingListAdapter;
    private String clubId = "";
    private String fieldId = "";
    private final List<OleBookingList> oleBookingList = new ArrayList<>();
    private final List<OleBookingList> cancelledList = new ArrayList<>();
    private final List<OleBookingList> previousBookingsList = new ArrayList<>();
    private OleRankClubAdapter clubAdapter;
    private final List<Club> clubList = new ArrayList<>();
    private OleBookingFieldAdapter fieldAdapter;
    private Animation hitAnimation, hitAnimation2;
    private String lastWeekDate = "";

    public OleBookingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentBookingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager horizLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(horizLayoutManager);
        oleBookingDateAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
        oleBookingDateAdapter.setOnItemClickListener(clickListener);
        binding.dateRecyclerVu.setAdapter(oleBookingDateAdapter);

        binding.fieldsRecyclerVu.setVisibility(View.GONE);

        if (AppManager.getInstance().clubs.size() == 1) {
            binding.clubRecyclerVu.setVisibility(View.GONE);
            clubId = AppManager.getInstance().clubs.get(0).getId();
            getAllFieldsAPI(true);
        }
        else {
            Club club = new Club();
            club.setId("");
            club.setName(getString(R.string.all));
            clubList.clear();
            clubList.add(club);
            clubList.addAll(AppManager.getInstance().clubs);
            String cId = Functions.getPrefValue(getContext(), Constants.kOwnerBookingSelectedClub);
            int pos = findClub(cId);
            if (pos != -1) {
                clubId = clubList.get(pos).getId();
            }
            else {
                pos = 0;
            }
            LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
            binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
            clubAdapter = new OleRankClubAdapter(getActivity(), clubList, pos, false);
            clubAdapter.setOnItemClickListener(clubClickListener);
            binding.clubRecyclerVu.setAdapter(clubAdapter);
        }

        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        binding.fieldsRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleBookingFieldAdapter(getActivity(), fieldList, true);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldsRecyclerVu.setAdapter(fieldAdapter);

        LinearLayoutManager vertLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.listRecyclerVu.setLayoutManager(vertLayoutManager);
        oleBookingListAdapter = new OleBookingListAdapter(getActivity(), oleBookingList);
        oleBookingListAdapter.setItemClickListener(itemClickListener);
        binding.listRecyclerVu.setAdapter(oleBookingListAdapter);

        selectedDateIndex = 0;
        fromDate = getDateStr(new Date());

        bookingClicked();

        binding.relMenu.setOnClickListener(this);
        binding.relCalendar.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.forward.setOnClickListener(this);
        binding.back.setOnClickListener(this);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    bookingClicked();
                }
                else if (tab.getPosition() == 1) {
                    cancelledClicked();
                } else{
                    if (!prevNextDate.isEmpty() && !type.isEmpty()){
                        previousBookings(prevNextDate, type);
                    }else {
                        previousBookings(fromDate, "prev");
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
        if (clubAdapter != null) {
            clubList.clear();
            Club club = new Club();
            club.setId("");
            club.setName(getString(R.string.all));
            clubList.add(club);
            clubList.addAll(AppManager.getInstance().clubs);
            clubAdapter.notifyDataSetChanged();
        }
        getBookingsAPI(oleBookingList.isEmpty(), "1", getDateStr(new Date()));
    }

    private int findClub(String id) {
        int pos = -1;
        for (int i = 0; i < clubList.size(); i++) {
            if (clubList.get(i).getId().equalsIgnoreCase(id)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    private String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookDateFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            clubAdapter.setSelectedIndex(pos);
            Club club = clubList.get(pos);
            clubId = club.getId();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.kOwnerBookingSelectedClub, clubId);
            editor.apply();
            fieldId = "";
            prevNextDate = "";
            type = "";
            binding.fieldsRecyclerVu.setVisibility(View.GONE);
            if (!clubId.equalsIgnoreCase("")) {
                getAllFieldsAPI(false);
            }
            getBookingsAPI(true, "1", getDateStr(new Date()));
        }
    };

    OleBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (fieldId.equalsIgnoreCase(fieldList.get(pos).getId())) {
                fieldId = "";
            }
            else {
                fieldId = fieldList.get(pos).getId();
            }
            fieldAdapter.setSelectedFieldId(fieldId);
            prevNextDate = "";
            type = "";
            getBookingsAPI(true, "1", getDateStr(new Date()));
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
            }
            catch (ParseException e) {
                e.printStackTrace();
                fromDate = "";
            }
            oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
            toDate = "";
            prevNextDate = "";
            type = "";
            getBookingsAPI(true, "0", "");
        }
    };

    OleBookingListAdapter.ItemClickListener itemClickListener = new OleBookingListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            OleBookingList booking;
            if (bookingType.equalsIgnoreCase("booking")) {
                booking = oleBookingList.get(pos);
            } else if (bookingType.equalsIgnoreCase("cancelled")){
                booking = cancelledList.get(pos);
            }else{
                booking = previousBookingsList.get(pos);
            }
            if (booking.getBookingFieldType().equalsIgnoreCase("football")) {
                Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            }
            else {
                // padel
                Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            }
        }

        @Override
        public void OnItemLongClick(View v, int pos) {
            OleBookingList booking;
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                if (bookingType.equalsIgnoreCase("booking")) {
                    booking = oleBookingList.get(pos);
                    if (booking.getStatus().equalsIgnoreCase("0") || booking.getStatus().equalsIgnoreCase("1") || booking.getStatus().equalsIgnoreCase("2")) {
                        showWaitingUserSheet(booking.getId(), booking.getClubId());
                    }
                }

            }
        }

        @Override
        public void callClicked(View view, int pos) {
            if (bookingType.equalsIgnoreCase("booking")) {
                ((BaseTabActivity)getActivity()).makeCall(oleBookingList.get(pos).getUser().getPhone());
            } else if (bookingType.equalsIgnoreCase("cancelled")){
                ((BaseTabActivity)getActivity()).makeCall(cancelledList.get(pos).getUser().getPhone());
            }else{
                ((BaseTabActivity)getActivity()).makeCall(previousBookingsList.get(pos).getUser().getPhone());
            }
//            if (isBooking) {
//                ((BaseTabActivity)getActivity()).makeCall(oleBookingList.get(pos).getUser().getPhone());
//            }
//            else {
//                ((BaseTabActivity)getActivity()).makeCall(cancelledList.get(pos).getUser().getPhone());
//            }
        }
    };

    protected void showWaitingUserSheet(String bookingId, String clubId) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Remove existing fragment with the same tag, if any
        Fragment existingFragment = fragmentManager.findFragmentByTag("AddWaitingUserDialogFragment");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }

        // Add transaction to backstack if necessary
        fragmentTransaction.addToBackStack(null);

        // Create and show the new dialog fragment
        AddWaitingUserDialogFragment dialogFragment = new AddWaitingUserDialogFragment(bookingId);
        dialogFragment.setDialogCallback((df, name, phone) -> {
            df.dismiss();
            addUserToWaitingList(bookingId, clubId, name, phone);
        });
        dialogFragment.show(fragmentTransaction, "AddWaitingUserDialogFragment");
    }


    private void addUserToWaitingList(String bookingId, String clubId, String name, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addUserToWaitingList(bookingId,clubId, name, phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
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

    @Override
    public void onClick(View v) {
        if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relCalendar) {
            calendarClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.forward) {
            prevNextDate = lastWeekDate;
            type = "next";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getDefault());
            try {
                Date dt = dateFormat.parse(prevNextDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt);
                calendar.add(Calendar.DAY_OF_YEAR, 0);
                Date newDate = calendar.getTime();
                prevNextDate = dateFormat.format(newDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
                prevNextDate = "";
            }
            getPastAndFutureBookingsAPI(false, prevNextDate, type);
            hitAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.hit_animation_rtl);
            binding.imgVu.startAnimation(hitAnimation2);
            binding.tvDate.startAnimation(hitAnimation2);
            binding.day.startAnimation(hitAnimation2);
            binding.month.startAnimation(hitAnimation2);
            hitAnimation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.imgVu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
        else if (v == binding.back) {
            prevNextDate = lastWeekDate;
            type = "prev";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getDefault());
            try {
                Date dt = dateFormat.parse(prevNextDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt);
                calendar.add(Calendar.DAY_OF_YEAR, 0);
                Date newDate = calendar.getTime();
                prevNextDate = dateFormat.format(newDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
                prevNextDate = "";
            }
            getPastAndFutureBookingsAPI(false, prevNextDate, type);
            hitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.hit_animation);
            binding.imgVu.startAnimation(hitAnimation);
            binding.tvDate.startAnimation(hitAnimation);
            binding.day.startAnimation(hitAnimation);
            binding.month.startAnimation(hitAnimation);
            hitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.imgVu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }


    private void menuClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).notificationsClicked();
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

    private void bookingClicked() {
        binding.linear.setVisibility(View.VISIBLE);
        binding.dateRecyclerVu.setVisibility(View.VISIBLE);
        binding.main.setVisibility(View.GONE);
        bookingType = "booking";
        oleBookingListAdapter.setDataSource(oleBookingList);
    }

    private void cancelledClicked() {
        binding.linear.setVisibility(View.VISIBLE);
        binding.dateRecyclerVu.setVisibility(View.VISIBLE);
        binding.main.setVisibility(View.GONE);
        bookingType = "cancelled";
        oleBookingListAdapter.setDataSource(cancelledList);
    }
    private void previousBookings(String date, String type) {
        binding.linear.setVisibility(View.GONE);
        binding.dateRecyclerVu.setVisibility(View.GONE);
        binding.main.setVisibility(View.VISIBLE);
        bookingType = "past_booking";
        getPastAndFutureBookingsAPI(true, date, type);
    }

    private void calendarClicked() {
        ((BaseActivity) getActivity()).showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
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
                getBookingsAPI(true, "1", getDateStr(new Date()));
                selectedDateIndex = -1;
                oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
            }
        });
    }

    private void setLabelCount() {
        binding.tabLayout.getTabAt(0).setText(getString(R.string.bookings_place, oleBookingList.size()));
        binding.tabLayout.getTabAt(1).setText(getString(R.string.cancelled_place, cancelledList.size()));
        binding.tabLayout.getTabAt(2).setText(getString(R.string.past_place, previousBookingsList.size()));
    }

    private void getAllFieldsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),  clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                            }
                            if (fieldList.size() > 0) {
                                binding.fieldsRecyclerVu.setVisibility(View.VISIBLE);
                                fieldAdapter.notifyDataSetChanged();
                            }
                            else {
                                Functions.showToast(getContext(), getString(R.string.field_not_found), FancyToast.ERROR);
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

    private void getBookingsAPI(boolean isLoader, String isDateNeeded, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.userBookings(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), fromDate, toDate, clubId, fieldId, isDateNeeded, date, "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleBookingList.clear();
                            cancelledList.clear();

                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingList booking = gson.fromJson(arr.get(i).toString(), OleBookingList.class);
                                if (booking.getStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking) ||
                                        booking.getStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking) ||
                                        booking.getStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
                                    cancelledList.add(booking);
                                }
                                else {
                                    oleBookingList.add(booking);
                                }
                            }

                            if (bookingType.equalsIgnoreCase("booking") || bookingType.isEmpty()) {
                                oleBookingListAdapter.setDataSource(oleBookingList);
                            }
                            else if (bookingType.equalsIgnoreCase("cancelled")){
                                oleBookingListAdapter.setDataSource(cancelledList);
                            }

                            setLabelCount();

                            if (isDateNeeded.equalsIgnoreCase("1"))  {
                                arrDate.clear();
                                JSONArray arrD = object.getJSONArray("dates");
                                for (int i = 0; i < arrD.length(); i++) {
                                    arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                                }
                                oleBookingDateAdapter.setDataSource(arrDate.toArray());
                            }

                        }
                        else {
                            oleBookingList.clear();
                            cancelledList.clear();
                            oleBookingListAdapter.setDataSource(new ArrayList<>());
                            setLabelCount();
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

    private void getPastAndFutureBookingsAPI(boolean isLoader, String date, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPastAndFutureBookingsAPI(Functions.getAppLang(getContext()), clubId, fieldId, date, type); //prev/next
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            previousBookingsList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingList booking = gson.fromJson(arr.get(i).toString(), OleBookingList.class);
                                previousBookingsList.add(booking);
                            }
                            lastWeekDate = object.getString("last_week_date");

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            dateFormat.setTimeZone(TimeZone.getDefault());
                            try {
                                Date dt = dateFormat.parse(lastWeekDate);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(dt);
                                SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
                                String dayOfWeek = dayFormat.format(dt);
                                binding.day.setText(dayOfWeek);
                                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
                                String month = monthFormat.format(dt);
                                binding.month.setText(month);
                                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                                String formattedDayOfMonth = String.format(Locale.ENGLISH, "%02d", dayOfMonth); // Ensure two-digit format
                                binding.tvDate.setText(formattedDayOfMonth);

                            }
                            catch (ParseException e) {
                                e.printStackTrace();
                            }
                            oleBookingListAdapter.setDataSource(previousBookingsList);
                            setLabelCount();
                        }
                        else {
                            previousBookingsList.clear();
                            oleBookingListAdapter.setDataSource(new ArrayList<>());
                            setLabelCount();
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
