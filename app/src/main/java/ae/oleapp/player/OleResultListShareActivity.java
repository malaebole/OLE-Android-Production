package ae.oleapp.player;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import ae.oleapp.adapters.OleMatchDateAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.OleResultListShareAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityResultListShareBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleResultListShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityResultListShareBinding binding;
    private Club club;
    private final List<Object> resultList = new ArrayList<>();
    private OleResultListShareAdapter adapter;
    private final String kDateFormat = "dd/MM/yyyy";
    private List<Date> dateList = new ArrayList<>();
    private int selectedDateIndex = 0;
    private String fromDate = "";
    private String toDate = "";
    private OleMatchDateAdapter rankDateAdapter;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityResultListShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.match_result);

        if (AppManager.getInstance().clubs.size() > 0) {
            club = AppManager.getInstance().clubs.get(0);
        }

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String year = dateFormat.format(new Date());
        dateFormat.applyPattern(kDateFormat);
        String todayDate = dateFormat.format(new Date());
        dateList = Functions.getMonthAndYearBetween("01/01/"+year, todayDate, kDateFormat);
        dateList.add(0, null);
        selectedDateIndex = dateList.size()-1;
        setDates(dateList.get(selectedDateIndex));
        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(daysLayoutManager);
        rankDateAdapter = new OleMatchDateAdapter(getContext(), dateList, selectedDateIndex);
        rankDateAdapter.setOnItemClickListener(dateClickListener);
        binding.dateRecyclerVu.setAdapter(rankDateAdapter);
        if (selectedDateIndex != -1) {
            binding.dateRecyclerVu.scrollToPosition(selectedDateIndex);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleResultListShareAdapter(getContext(), resultList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            club = AppManager.getInstance().clubs.get(pos);
            getResultListAPI(true);
        }
    };

    OleMatchDateAdapter.OnItemClickListener dateClickListener = new OleMatchDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            rankDateAdapter.setSelectedDateIndex(pos);
            setDates(dateList.get(pos));
        }
    };

    private void setDates(Date date) {
        if (date == null) {
            fromDate = "";
            toDate = "";
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(kDateFormat, Locale.ENGLISH);
            fromDate = dateFormat.format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date toD = calendar.getTime();
            toDate = dateFormat.format(toD);
        }
        getResultListAPI(true);
    }

    OleResultListShareAdapter.ItemClickListener itemClickListener = new OleResultListShareAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                Intent intent = new Intent(getContext(), OlePadelResultShareActivity.class);
                intent.putExtra("result", new Gson().toJson(resultList.get(pos)));
                intent.putExtra("club_name", club.getName());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getContext(), OleFootballResultShareActivity.class);
                intent.putExtra("result", new Gson().toJson(resultList.get(pos)));
                intent.putExtra("club_name", club.getName());
                startActivity(intent);
            }
        }
    };

    private void getResultListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.allMatches(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, "", fromDate, toDate, club.getId(), club.getClubType());
        call.enqueue(new Callback<>() {
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
                            for (int i = 0; i < arrR.length(); i++) {
                                resultList.add(gson.fromJson(arrR.get(i).toString(), OleMatchResults.class));
                            }
                            for (int i = 0; i < arrPR.length(); i++) {
                                resultList.add(gson.fromJson(arrPR.get(i).toString(), OlePadelMatchResults.class));
                            }
                        }
                        else {
                            resultList.clear();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                        adapter.notifyDataSetChanged();

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