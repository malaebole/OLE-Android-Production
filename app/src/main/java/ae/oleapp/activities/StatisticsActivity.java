package ae.oleapp.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.DaysAdapter;
import ae.oleapp.adapters.MonthsAdapter;
import ae.oleapp.adapters.YearsAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityStatisticsBinding;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.models.ClubStatsModel;
import ae.oleapp.models.GraphData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity  extends BaseActivity implements View.OnClickListener {


    private ActivityStatisticsBinding binding;
    private ClubStatsModel clubStatsModel;
    private final List<GraphData> graphDataList = new ArrayList<>();
    private YearsAdapter yearsAdapter;
    private MonthsAdapter monthsAdapter;
    private DaysAdapter daysAdapter;
    private String clubId = "", selectedYearsString ="", selectedMonthsString="",selectedDaysString="", from ="", to="", selectedData="";
    private  List<String> yearsList;
    private List<String> monthsList;
    private List<String> daysList;
    private List<String> selectedYears;
    private List<String> selectedMonths;
    private final List<Integer> selectedYearIndices = new ArrayList<>();
    private final List<Integer> selectedMonthsIndices = new ArrayList<>();
    private final List<Integer> selectedDaysIndices = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

        yearsList = generateYearsList();
        monthsList = generateMonthsList();
        daysList = generateDaysList();


        LinearLayoutManager oleYearslayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.yearsRecyclerVu.setLayoutManager(oleYearslayoutManager);
        yearsAdapter = new YearsAdapter(getContext(), yearsList);
        yearsAdapter.setItemClickListener(yearsClickListener);
        yearsAdapter.setSelectedIndices(selectedYearIndices);
        toggleYearsSelection(0);
        binding.yearsRecyclerVu.setAdapter(yearsAdapter);

        LinearLayoutManager oleMonthsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.monthsRecyclerVu.setLayoutManager(oleMonthsLayoutManager);
        monthsAdapter = new MonthsAdapter(getContext(), monthsList);
        monthsAdapter.setItemClickListener(monthsClickListener);
        binding.monthsRecyclerVu.setAdapter(monthsAdapter);

        LinearLayoutManager oleDaysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(oleDaysLayoutManager);
        daysAdapter = new DaysAdapter(getContext(), daysList);
        daysAdapter.setItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(daysAdapter);

        clubStatistics(true, clubId,"","","","","");

        binding.totalMatchRel.setOnClickListener(this);
        binding.friendlyGameRel.setOnClickListener(this);
        binding.facilityPriceRel.setOnClickListener(this);
        binding.bookingsRel.setOnClickListener(this);
        binding.cancelledRel.setOnClickListener(this);
        binding.inventoryRel.setOnClickListener(this);
        binding.hoursRel.setOnClickListener(this);
        binding.earningsRel.setOnClickListener(this);
        binding.expenseRel.setOnClickListener(this);
        binding.newUsersRel.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    YearsAdapter.ItemClickListener yearsClickListener = new YearsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

                yearsAdapter.setSelectedIndices(selectedYearIndices);
                toggleYearsSelection(pos);
                selectedYears = getSelectedYears();
                selectedYearsString = TextUtils.join(",", selectedYears);

            if (selectedYears != null && selectedYears.size() != 1) {
                binding.daysRecyclerVu.setVisibility(View.GONE);
                daysAdapter.setSelected(-1);
                selectedDaysString = "";
            } else {
                binding.daysRecyclerVu.setVisibility(View.VISIBLE);
            }

            clubStatistics(true, clubId, selectedYearsString,selectedMonthsString,selectedDaysString,"","");


        }
    };


    MonthsAdapter.ItemClickListener monthsClickListener = new MonthsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            monthsAdapter.setSelectedIndices(selectedMonthsIndices);
            toggleMonthsSelection(pos);
            selectedMonths = getSelectedMonths();
            selectedMonthsString = TextUtils.join(",", selectedMonths);

            if (selectedYears != null && selectedYears.size() != 1 || selectedMonths != null && selectedMonths.isEmpty()) {
                binding.daysRecyclerVu.setVisibility(View.GONE);
                daysAdapter.setSelected(-1);
                selectedDaysString = "";
            } else {
                binding.daysRecyclerVu.setVisibility(View.VISIBLE);
            }

            clubStatistics(true, clubId, selectedYearsString, selectedMonthsString, selectedDaysString,"","");


        }
    };

    DaysAdapter.ItemClickListener daysClickListener = new DaysAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            daysAdapter.setSelected(pos);
            String selectedDay = daysList.get(pos);
            if (selectedDaysString != null && selectedDaysString.equals(selectedDay)) {
                daysAdapter.setSelected(-1);
                selectedDaysString = null;
            } else {
                selectedDaysString = selectedDay;
            }

            clubStatistics(true, clubId, selectedYearsString, selectedMonthsString, selectedDaysString,"","");

        }
    };

    public void toggleYearsSelection(int position) {
        if (selectedYearIndices.contains(position)) {
            selectedYearIndices.remove((Integer) position);
        } else {
            selectedYearIndices.add(position);
        }
        yearsAdapter.notifyDataSetChanged();
    }


    public List<String> getSelectedYears() {
        List<String> selectedYears = new ArrayList<>();
        for (int index : selectedYearIndices) {
            if (index >= 0 && index < yearsList.size()) {
                selectedYears.add(yearsList.get(index));
            }
        }
        return selectedYears;
    }

    public void toggleMonthsSelection(int position) {
        if (selectedMonthsIndices.contains(position)) {
            selectedMonthsIndices.remove((Integer) position);
        } else {
            selectedMonthsIndices.add(position);
        }
        monthsAdapter.notifyDataSetChanged();
    }


    public List<String> getSelectedMonths() {
        List<String> selectedMonths= new ArrayList<>();
        for (int index : selectedMonthsIndices) {
            if (index >= 0 && index < monthsList.size()) {
                selectedMonths.add(monthsList.get(index));
            }
        }
        return selectedMonths;
    }


//    public void toggleDaysSelection(int position) {
//        if (selectedDaysIndices.contains(position)) {
//            selectedDaysIndices.remove((Integer) position);
//        } else {
//            selectedDaysIndices.add(position);
//        }
//        daysAdapter.notifyDataSetChanged();
//    }

//
//    public List<String> getSelectedDays() {
//        List<String> selectedDays = new ArrayList<>();
//        for (int index : selectedDaysIndices) {
//            if (index >= 0 && index < daysList.size()) {
//                selectedDays.add(daysList.get(index));
//            }
//        }
//        return selectedDays;
//    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        }
        else if (v == binding.totalMatchRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("matches");
            selectedData = "matches";
        }
        else if (v == binding.friendlyGameRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("games");
            selectedData = "games";



        }
        else if (v == binding.facilityPriceRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("facilities");
            selectedData = "facilities";



        }
        else if (v == binding.bookingsRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("completed");
            selectedData = "completed";


        }
        else if (v == binding.cancelledRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("canceled");
            selectedData = "canceled";

        }
        else if (v == binding.inventoryRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("inventory");
            selectedData = "inventory";

        }
        else if (v == binding.hoursRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("hours");
            selectedData = "hours";

        }
        else if (v == binding.earningsRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));

            populateGraph("earnings");
            selectedData = "earnings";

        }
        else if (v == binding.expenseRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            populateGraph("expenses");
            selectedData = "expenses";

        }
        else if (v == binding.newUsersRel){
            binding.totalMatchRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.friendlyGameRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.facilityPriceRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.bookingsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.cancelledRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.inventoryRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.hoursRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.earningsRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.expenseRel.setBackground(getResources().getDrawable(R.drawable.square_grey_border));
            binding.newUsersRel.setBackground(getResources().getDrawable(R.drawable.square_blue_border));
            populateGraph("users");
            selectedData = "users";

        }
        else if (v == binding.filterBtn){
            filterClicked();
        }

    }

    private void clubStatistics(Boolean isLoader,  String clubId, String byYear, String byMonth, String byDays, String from, String to) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.clubStatistics(Functions.getAppLang(getContext()), clubId,byYear,byMonth,byDays,from,to);
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
                                clubStatsModel = gson.fromJson(obj.toString(), ClubStatsModel.class);
                                popuplatedata();
                                if (!selectedData.isEmpty()){
                                    populateGraph(selectedData);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void statsGraphData(Boolean isLoader, String clubId, String filterType, String byYear, String byMonth, String byDays, String from, String to) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (byYear.isEmpty()){
            byYear = yearsList.get(0);
        }
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.statsGraphData(Functions.getAppLang(getContext()), clubId,filterType, byYear,byMonth,byDays,from,to);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray obj = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                graphDataList.clear();
                                for (int i = 0; i < obj.length(); i++) {
                                    graphDataList.add(gson.fromJson(obj.get(i).toString(), GraphData.class));
                                }
                                showGraph(filterType);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void popuplatedata(){

        binding.tvMatchCount.setText(clubStatsModel.getMatchesCount());
        binding.tvFriendlyCount.setText(clubStatsModel.getGamesCount());
        binding.tvFacilityCount.setText(clubStatsModel.getFacilitiesTotal());
        binding.tvBookingCount.setText(clubStatsModel.getCompletedCount());
        binding.tvCancelledCount.setText(clubStatsModel.getCanceledCount());
        binding.tvInventoryCount.setText(clubStatsModel.getProfitTotal());
        binding.tvHoursCount.setText(clubStatsModel.getTotalHours());
        binding.tvEarningCount.setText(clubStatsModel.getEarningsTotal());
        binding.tvExpenseCount.setText(clubStatsModel.getExpensesTotal());
        binding.tvNewUsersCount.setText(clubStatsModel.getNewUsers());

    }

    private void populateGraph(String filterType){
        statsGraphData(true, clubId, filterType,selectedYearsString,selectedMonthsString,selectedDaysString,from,to);

    }

    private void showGraph(String filterType){

        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setDrawValueAboveBar(false); // Disable drawing values above bars
        binding.barChart.setPinchZoom(false);
        binding.barChart.setDoubleTapToZoomEnabled(false); // Disable double-tap zoom
        binding.barChart.setDragEnabled(true); // Enable drag gesture
        binding.barChart.setScaleEnabled(false); // Disable scaling
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.getLegend().setEnabled(false);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setEnabled(true); // Enable X-axis values
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position X-axis at the bottom
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0); // Rotate labels by 45 degrees

        // Adjust label density
        if (graphDataList.size() > 6) {
            xAxis.setLabelCount(6); // Set the maximum number of labels to be displayed
            xAxis.setGranularity(1f); // Set the minimum interval between labels
        } else {
            xAxis.setLabelCount(graphDataList.size()); // Display all labels if there are fewer than 6
            xAxis.setGranularity(1f);
        }

        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = binding.barChart.getAxisRight();
        rightAxis.setEnabled(false);

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < graphDataList.size(); i++) {
            GraphData dataObject = graphDataList.get(i);
            float value = dataObject.getValue();
            String label = dataObject.getName();
            entries.add(new BarEntry(i, value, label));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Bar Data");
        dataSet.setValueTextSize(12f);
        dataSet.setColor(Color.GREEN);
        dataSet.setDrawValues(false); // Disable drawing values on each bar

        BarData barData = new BarData(dataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        binding.barChart.setData(barData);
        binding.barChart.setDrawMarkers(true); // Enable drawing markers on top of bars


        MyMarkerView markerView = new MyMarkerView(this, R.layout.custom_marker_view, filterType);
        markerView.setChartView(binding.barChart); // Set the chart view for proper positioning
        binding.barChart.setMarker(markerView);

        // Adjust visible range and bar width based on the number of entries
//        if (entries.size() < 6) {
//            binding.barChart.setVisibleXRangeMaximum(1);
//            binding.barChart.getBarData().setBarWidth(0.2f); // Adjust the bar width for a single entry
//        } else {
//            binding.barChart.setVisibleXRangeMaximum(6);
//        }

//        float fixedBarWidth = 0.2f; // Adjust the desired fixed bar width
//        binding.barChart.getBarData().setBarWidth(fixedBarWidth); // Set the fixed bar width
//
//// Set the maximum visible range of bars based on the number of entries
//        int maxVisibleEntries = 6; // Adjust this number based on your preference
//        if (entries.size() > maxVisibleEntries) {
//            binding.barChart.setVisibleXRangeMaximum(maxVisibleEntries);
//        } else {
//            binding.barChart.setVisibleXRangeMaximum(entries.size());
//        }

        binding.barChart.animateX(0); // Disable initial animation
        binding.barChart.setVisibleXRangeMaximum(6); // Set the maximum visible range of bars
        float fixedBarWidth = 0.2f; // Adjust the desired fixed bar width
        binding.barChart.getBarData().setBarWidth(fixedBarWidth);
        //binding.barChart.setVisibleXRangeMaximum(6); // Set the maximum visible range of bars
        binding.barChart.moveViewToX(entries.size() - 6); // Move the view to show the rightmost bars

        String[] xAxisValues = new String[graphDataList.size()];
        for (int i = 0; i < graphDataList.size(); i++) {
            xAxisValues[i] = graphDataList.get(i).getName();
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        binding.barChart.invalidate();


    }


    protected void filterClicked(){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("FilterFragment");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.addToBackStack(null);
            FilterFragment dialogFragment = new FilterFragment(true);
            dialogFragment.setDialogCallback((df,  filterBy,  bankId,  from,  to) -> {
                df.dismiss();

                clubStatistics(true, clubId,"","","", from, to);
                this.from = from;
                this.to = to;


            });
            dialogFragment.show(fragmentTransaction, "FilterFragment");


    }

    private List<String> generateYearsList() {
         List<String> yearsList = new ArrayList<>();


        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Add years from 2020 to 2023
//        for (int year = 2020; year <= 2023; year++) {
//            yearsList.add(String.valueOf(year));
//        }

        for (int year = currentYear; year >= 2019; year--) {
            yearsList.add(String.valueOf(year));
        }


        // Add years starting from 2024 until the current year
        for (int year = 2024; year <= currentYear; year++) {
            yearsList.add(String.valueOf(year));
        }

        return yearsList;
    }
//    private List<String> generateMonthsList() {
//        List<String> monthsList = new ArrayList<>();
//
//        // Get the current month
//        Calendar calendar = Calendar.getInstance();
//        int currentMonth = calendar.get(Calendar.MONTH);
//
//        // Add months from the current month to December
//        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
//        String[] monthNames = dateFormatSymbols.getMonths();
//        for (int month = currentMonth; month < 12; month++) {
//
//            String monthName = monthNames[month].substring(0, 3); // Get the first three letters
//            monthsList.add(monthName);
//            //monthsList.add(monthNames[month]);
//        }
//
//        // Add months from January to the previous month
//        for (int month = 0; month < currentMonth; month++) {
//            String monthName = monthNames[month].substring(0, 3);
//            monthsList.add(monthName);
//            //monthsList.add(monthNames[month]);
//        }
//
//        return monthsList;
//    }

    private List<String> generateMonthsList() {
        List<String> monthsList = new ArrayList<>();

        // Get the current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        // Define the English month names
        String[] englishMonthNames = new DateFormatSymbols(Locale.ENGLISH).getMonths();

        // Add months from the current month to December
        for (int month = currentMonth; month < 12; month++) {
            String monthName = englishMonthNames[month].substring(0, 3);
            monthsList.add(monthName);
        }

        // Add months from January to the previous month
        for (int month = 0; month < currentMonth; month++) {
            String monthName = englishMonthNames[month].substring(0, 3);
            monthsList.add(monthName);
        }

        return monthsList;
    }
    private List<String> generateDaysList() {
        List<String> daysList = new ArrayList<>();

        // Get the current day of the week
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Define the English month names
        String[] englishDayNames = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();

        // Add months from the current month to December
        for (int day = currentDayOfWeek; day <= Calendar.SATURDAY; day++) {
            String dayName = englishDayNames[day].substring(0, 3);
            daysList.add(dayName);
        }

        // Add months from January to the previous month
        for (int day = Calendar.SUNDAY; day < currentDayOfWeek; day++) {
            String dayName = englishDayNames[day].substring(0, 3);
            daysList.add(dayName);
        }

//
//        // Add days from the current day to Sunday
//        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
//        String[] dayNames = dateFormatSymbols.getWeekdays();
//        for (int day = currentDayOfWeek; day <= Calendar.SATURDAY; day++) {
//            daysList.add(dayNames[day].substring(0, 3));
//        }
//
//        // Add days from Monday to the previous day
//        for (int day = Calendar.SUNDAY + 1; day < currentDayOfWeek; day++) {
//            daysList.add(dayNames[day].substring(0, 3));
//        }

        return daysList;
    }


}