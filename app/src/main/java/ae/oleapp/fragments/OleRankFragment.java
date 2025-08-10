package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.OleNotificationsActivity;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.OleRankDateAdapter;
import ae.oleapp.adapters.OleRankListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentRankBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OlePlayerRank;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
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
public class OleRankFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentRankBinding binding;
    private int selectedMinDateIndex = -1;
    private int selectedMaxDateIndex = -1;
    private OleRankDateAdapter oleRankDateAdapter;
    private OleRankListAdapter oleRankListAdapter;
    private OleRankClubAdapter oleRankClubAdapter;
    private final String kDateFormat = "dd/MM/yyyy";
    private List<Date> dateList = new ArrayList<>();
    private final List<OlePlayerRank> rankList = new ArrayList<>();
    private final List<Club> clubList = new ArrayList<>();
    private String fromDate = "";
    private String toDate = "";
    private final String minAge = "";
    private final String maxAge = "";
    private String clubId = "";
    private boolean isMostBooking = true;

    public OleRankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentRankBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String year = dateFormat.format(new Date());
        dateFormat.applyPattern(kDateFormat);
        String todayDate = dateFormat.format(new Date());
        dateList = Functions.getMonthAndYearBetween("01/01/"+year, todayDate, kDateFormat);
        dateList.add(0, null);
        selectedMinDateIndex = dateList.size()-1;
        setDates(dateList.get(selectedMinDateIndex), null);

        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(daysLayoutManager);
        oleRankDateAdapter = new OleRankDateAdapter(getContext(), dateList, selectedMinDateIndex);
        oleRankDateAdapter.setOnItemClickListener(dateClickListener);
        binding.dateRecyclerVu.setAdapter(oleRankDateAdapter);
        binding.dateRecyclerVu.scrollToPosition(selectedMinDateIndex);

//        ageList = Arrays.asList(getString(R.string.all_age), "<18", "18-35", "35+");

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), clubList, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);
        binding.clubRecyclerVu.scrollToPosition(0);

        LinearLayoutManager listLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(listLayoutManager);
        oleRankListAdapter = new OleRankListAdapter(getContext(), rankList, isMostBooking);
        oleRankListAdapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(oleRankListAdapter);

        getClubList(false);

        winningClicked();
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    bookingClicked();
                }
                else {
                    winningClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.cardVu.setVisibility(View.GONE);

        binding.relMenu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.relCalendar.setOnClickListener(this);

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
        else {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            startActivity(new Intent(getContext(), OleNotificationsActivity.class));
        }
    }

    OleRankListAdapter.OnItemClickListener clickListener = new OleRankListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            openPlayerInfo(rankList.get(pos).getId());
        }
    };

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = clubList.get(pos).getId();
            callAPI();
        }
    };

    OleRankDateAdapter.OnItemClickListener dateClickListener = new OleRankDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (pos == 0) {
                selectedMinDateIndex = 0;
                selectedMaxDateIndex = -1;
                fromDate = "";
                toDate = "";
                oleRankDateAdapter.setSelectedDateIndex(selectedMinDateIndex, selectedMaxDateIndex);
                callAPI();
            }
            else {
                if (selectedMinDateIndex == 0 || selectedMinDateIndex == -1) {
                    selectedMinDateIndex = pos;
                }
                else if (selectedMaxDateIndex == -1) {
                    if (selectedMinDateIndex < pos) {
                        selectedMaxDateIndex = pos;
                    } else {
                        selectedMinDateIndex = pos;
                    }
                }
                else {
                    selectedMinDateIndex = pos;
                    selectedMaxDateIndex = -1;
                }
                oleRankDateAdapter.setSelectedDateIndex(selectedMinDateIndex, selectedMaxDateIndex);
                Date sDate = null, eDate = null;
                if (selectedMinDateIndex != -1) {
                    sDate = dateList.get(selectedMinDateIndex);
                }
                if (selectedMaxDateIndex != -1) {
                    eDate = dateList.get(selectedMaxDateIndex);
                }
                setDates(sDate, eDate);
            }
        }
    };

    private void openPlayerInfo(String playerId) {
        if (Functions.getPrefValue(getContext(),Constants.kIsSignIn).equalsIgnoreCase("1")){
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", playerId);
            startActivity(intent);
        }else{
            Functions.showToast(getContext(),getString(R.string.please_login_first), FancyToast.ERROR);
        }
    }

    private void bookingClicked() {
        isMostBooking = true;
        binding.tvPoints.setText(R.string.booking_hrs);
        binding.tvPerc.setVisibility(View.GONE);
        callAPI();
        rankList.clear();
        oleRankListAdapter.notifyDataSetChanged();
    }

    private void winningClicked() {
        isMostBooking = false;
        binding.tvPoints.setText(R.string.points);
        binding.tvPerc.setVisibility(View.VISIBLE);
        callAPI();
        rankList.clear();
        oleRankListAdapter.notifyDataSetChanged();
    }

    private void setDates(Date sDate, Date eDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kDateFormat, Locale.ENGLISH);
        if (sDate != null) {
            fromDate = dateFormat.format(sDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sDate);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date toD = calendar.getTime();
            toDate = dateFormat.format(toD);
        }
        if (eDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eDate);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date toD = calendar.getTime();
            toDate = dateFormat.format(toD);
        }
        callAPI();
    }

    private void callAPI() {
        if (isMostBooking) {
            getRanksAPI(true, "most_bookings");
        }
        else {
            getRanksAPI(true, "most_winning");
        }
    }

    public void calendarClicked() {
        ((BaseActivity)getActivity()).showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
            @Override
            public void filterData(DialogFragment df, String from, String to) {
                df.dismiss();
                fromDate = from;
                toDate = to;
                callAPI();
                selectedMinDateIndex = -1;
                selectedMaxDateIndex = -1;
                oleRankDateAdapter.setSelectedDateIndex(selectedMinDateIndex, selectedMaxDateIndex);
            }
        });
    }

    private void getRanksAPI(boolean isLoader, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getRanking(Functions.getAppLang(getActivity()),Functions.getPrefValue(getActivity(), Constants.kUserID), type, "1", fromDate, toDate, minAge, maxAge, clubId, "", Constants.kFootballModule);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            rankList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                OlePlayerRank olePlayerRank = gson.fromJson(arr.get(i).toString(), OlePlayerRank.class);
                                olePlayerRank.setRank(i+1);
                                rankList.add(olePlayerRank);
                            }
                            oleRankListAdapter.setMostBooking(isMostBooking);
                            oleRankListAdapter.notifyDataSetChanged();
                        }
                        else {
                            rankList.clear();
                            oleRankListAdapter.setMostBooking(isMostBooking);
                            oleRankListAdapter.notifyDataSetChanged();
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

    private void getClubList(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getMyClubs(Functions.getAppLang(getActivity()),Functions.getPrefValue(getActivity(), Constants.kUserID), Functions.getPrefValue(getActivity(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            clubList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Club club = gson.fromJson(arr.get(i).toString(), Club.class);
                                clubList.add(club);
                            }
                            Club club = new Club();
                            club.setId("");
                            club.setName(getString(R.string.all));
                            clubList.add(0, club);
                            oleRankClubAdapter.notifyDataSetChanged();
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
