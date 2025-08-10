package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ae.oleapp.adapters.OlePadelSkillsLevelAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.OleRankListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentPadelRankBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.OlePadelSkillLevel;
import ae.oleapp.models.OlePlayerRank;
import ae.oleapp.models.OleSelectionList;
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
public class OlePadelRankFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentPadelRankBinding binding;
    private OleRankListAdapter oleRankListAdapter;
    private OleRankClubAdapter oleRankClubAdapter;
    private final String kDateFormat = "dd/MM/yyyy";
    private List<Date> dateList = new ArrayList<>();
    private final List<OlePlayerRank> rankList = new ArrayList<>();
    private final List<Club> clubList = new ArrayList<>();
    private final List<OlePadelSkillLevel> skillLevels = new ArrayList<>();
    private String fromDate = "";
    private String toDate = "";
    private final String minAge = "";
    private final String maxAge = "";
    private String clubId = "";
    private String levelId = "";
    private boolean isMostBooking = true;
    private OlePadelSkillsLevelAdapter adapter;

    public OlePadelRankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPadelRankBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        isMostBooking = false;
        binding.tvPoints.setText(R.string.points);
        binding.levelRecyclerVu.setVisibility(View.VISIBLE);
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String year = dateFormat.format(new Date());
        dateFormat.applyPattern(kDateFormat);
        String todayDate = dateFormat.format(new Date());
        dateList = Functions.getMonthAndYearBetween("01/01/"+year, todayDate, kDateFormat);
        dateList.add(0, null);
        setDates(dateList.get(dateList.size()-1));
        dateFormat.applyPattern("yyyy");
        String y = dateFormat.format(dateList.get(dateList.size()-1));
        dateFormat.applyPattern("MMM");
        binding.tvDate.setText(String.format("%s %s", dateFormat.format(dateList.get(dateList.size()-1)), y));

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), clubList, 0, true);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);
        binding.clubRecyclerVu.scrollToPosition(0);

        LinearLayoutManager listLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(listLayoutManager);
        oleRankListAdapter = new OleRankListAdapter(getContext(), rankList, isMostBooking);
        oleRankListAdapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(oleRankListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.levelRecyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePadelSkillsLevelAdapter(getContext(), skillLevels);
        adapter.setOnItemClickListener(itemClickListener);
        binding.levelRecyclerVu.setAdapter(adapter);

        getClubList(false);
        getLevelsAPi(false);

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

        binding.topVu.setVisibility(View.GONE);
        binding.backBtn.setOnClickListener(this);
        binding.calendarBtn.setOnClickListener(this);
        binding.dateVu.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            getActivity().finish();
        }
        else if (v == binding.calendarBtn) {
            calendarClicked();
        }
        else if (v == binding.dateVu) {
            List<OleSelectionList> oleSelectionList = new ArrayList<>();
            for (int i = 0; i < dateList.size(); i++) {
                if (dateList.get(i) == null) {
                    oleSelectionList.add(new OleSelectionList(String.valueOf(i), getString(R.string.all)));
                }
                else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
                    String y = dateFormat.format(dateList.get(i));
                    dateFormat.applyPattern("MMM");
                    oleSelectionList.add(new OleSelectionList(String.valueOf(i), String.format("%s %s", dateFormat.format(dateList.get(i)), y)));
                }
            }
            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_date), false);
            dialog.setLists(oleSelectionList);
            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<OleSelectionList> selectedItems) {
                    OleSelectionList selectedItem = selectedItems.get(0);
                    binding.tvDate.setText(selectedItem.getValue());
                    int pos = Integer.parseInt(selectedItem.getId());
                    if (pos == 0) {
                        fromDate = "";
                        toDate = "";
                        callAPI();
                    }
                    else {
                        Date date = dateList.get(pos);
                        setDates(date);
                    }
                }
            });
            dialog.show();
        }
    }

    OlePadelSkillsLevelAdapter.OnItemClickListener itemClickListener = new OlePadelSkillsLevelAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            levelId = skillLevels.get(pos).getId();
            adapter.setSelectedLevelId(levelId);
            callAPI();
        }
    };

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

    private void openPlayerInfo(String playerId) {
        if (Functions.getPrefValue(getContext(),Constants.kIsSignIn).equalsIgnoreCase("1")){
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", playerId);
            startActivity(intent);
        }else{
            Functions.showToast(getContext(),getString(R.string.please_login_first), FancyToast.ERROR);
        }


//            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
//            intent.putExtra("player_id", playerId);
//            startActivity(intent);
    }

    private void bookingClicked() {
        isMostBooking = true;
        binding.tvPoints.setText(R.string.booking_hrs);
        binding.levelRecyclerVu.setVisibility(View.INVISIBLE);
        callAPI();
        rankList.clear();
        oleRankListAdapter.notifyDataSetChanged();
    }

    private void winningClicked() {
        isMostBooking = false;
        binding.tvPoints.setText(R.string.points);
        binding.levelRecyclerVu.setVisibility(View.VISIBLE);
        callAPI();
        rankList.clear();
        oleRankListAdapter.notifyDataSetChanged();
    }

    private void setDates(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kDateFormat, Locale.ENGLISH);
        fromDate = dateFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date toD = calendar.getTime();
        toDate = dateFormat.format(toD);
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
            }
        });
    }

    private void populateData() {
        binding.topVu.setVisibility(View.VISIBLE);
        if (rankList.size() > 2) {
            populatePlayerOne(rankList.get(0));
            populatePlayerTwo(rankList.get(1));
            populatePlayerThree(rankList.get(2));
            rankList.subList(0, 3).clear();
        }
        else if (rankList.size() > 1) {
            populatePlayerOne(rankList.get(0));
            populatePlayerTwo(rankList.get(1));
            rankList.subList(0, 2).clear();
            binding.profileVuThree.setVisibility(View.INVISIBLE);
        }
        else if (rankList.size() > 0) {
            populatePlayerOne(rankList.get(0));
            rankList.remove(0);
            binding.profileVuTwo.setVisibility(View.INVISIBLE);
            binding.profileVuThree.setVisibility(View.INVISIBLE);
        }
        else {
            binding.topVu.setVisibility(View.GONE);
        }

        oleRankListAdapter.setMostBooking(isMostBooking);
        oleRankListAdapter.notifyDataSetChanged();
    }

    private void populatePlayerOne(OlePlayerRank olePlayerRank) {
        binding.profileVuOne.setVisibility(View.VISIBLE);
        binding.imgVuRankOne.setImageResource(R.drawable.rank_badge_one);
        binding.profileOne.populateData(olePlayerRank.getNickName(), olePlayerRank.getPhotoUrl(), olePlayerRank.getLevel(), true);
        binding.profileOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayerInfo(olePlayerRank.getId());
            }
        });
        if (isMostBooking) {
            binding.tvPointsOne.setText(olePlayerRank.getTotalHours());
        }
        else {
            binding.tvPointsOne.setText(olePlayerRank.getPoints());
        }
    }

    private void populatePlayerTwo(OlePlayerRank olePlayerRank) {
        binding.profileVuTwo.setVisibility(View.VISIBLE);
        binding.imgVuRankTwo.setImageResource(R.drawable.rank_badge_two);
        binding.profileTwo.populateData(olePlayerRank.getNickName(), olePlayerRank.getPhotoUrl(), olePlayerRank.getLevel(), true);
        binding.profileTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayerInfo(olePlayerRank.getId());
            }
        });
        if (isMostBooking){
            binding.tvPointsTwo.setText(olePlayerRank.getTotalHours());
        }
        else {
            binding.tvPointsTwo.setText(olePlayerRank.getPoints());
        }
    }

    private void populatePlayerThree(OlePlayerRank olePlayerRank) {
        binding.profileVuThree.setVisibility(View.VISIBLE);
        binding.imgVuRankThree.setImageResource(R.drawable.rank_badge_three);
        binding.profileThree.populateData(olePlayerRank.getNickName(), olePlayerRank.getPhotoUrl(), olePlayerRank.getLevel(), true);
        binding.profileThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayerInfo(olePlayerRank.getId());
            }
        });
        if (isMostBooking){
            binding.tvPointsThree.setText(olePlayerRank.getTotalHours());
        }
        else {
            binding.tvPointsThree.setText(olePlayerRank.getPoints());
        }
    }

    private void getRanksAPI(boolean isLoader, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getRanking(Functions.getAppLang(getActivity()), Functions.getPrefValue(getActivity(), Constants.kUserID), type, "1", fromDate, toDate, minAge, maxAge, clubId, levelId, Constants.kPadelModule);
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
                            populateData();
                        }
                        else {
                            rankList.clear();
                            populateData();
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

    private void getLevelsAPi(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getLevels(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
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
                            skillLevels.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                skillLevels.add(gson.fromJson(arr.get(i).toString(), OlePadelSkillLevel.class));
                            }
                            OlePadelSkillLevel level = new OlePadelSkillLevel();
                            level.setId("");
                            level.setName(getString(R.string.all));
                            skillLevels.add(0, level);
                            adapter.notifyDataSetChanged();
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
