package ae.oleapp.player;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

import ae.oleapp.R;
import ae.oleapp.adapters.OleProfileMatchHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityProfileMatchHistoryDetailsBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleProfileMatchHistoryDetailsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityProfileMatchHistoryDetailsBinding binding;
    private OleProfileMatchHistoryAdapter adapter;
    private final List<OleMatchResults> oleMatchResults = new ArrayList<>();
    private List<Date> dateList = new ArrayList<>();
    private String fromDate = "";
    private String toDate = "";
    private String playerId = "";
    private final String kDateFormat = "dd/MM/yyyy";
    private OleMatchResults matchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityProfileMatchHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchResult = new Gson().fromJson(bundle.getString("match", ""), OleMatchResults.class);
            playerId = bundle.getString("player_id", "");
            populateData();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleProfileMatchHistoryAdapter(getContext(), oleMatchResults);
        binding.recyclerVu.setAdapter(adapter);

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
        binding.tvFilterDate.setText(String.format("%s %s", dateFormat.format(dateList.get(dateList.size()-1)), y));

        binding.backBtn.setOnClickListener(this);
        binding.myProfileVu.setOnClickListener(this);
        binding.opponentProfileVu.setOnClickListener(this);
        binding.myProfileVu1.setOnClickListener(this);
        binding.opponentProfileVu1.setOnClickListener(this);
        binding.dateVu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backBtn) {
            finish();
        }
        else if (view == binding.myProfileVu || view == binding.myProfileVu1) {
            gotoProfile(matchResult.getPlayerOne().getId());
        }
        else if (view == binding.opponentProfileVu || view == binding.opponentProfileVu1) {
            gotoProfile(matchResult.getPlayerTwo().getId());
        }
        else if (view == binding.dateVu) {
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
                    binding.tvFilterDate.setText(selectedItem.getValue());
                    int pos = Integer.parseInt(selectedItem.getId());
                    if (pos == 0) {
                        fromDate = "";
                        toDate = "";
                        getMatchHistoryAPI(true);
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

    private void setDates(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kDateFormat, Locale.ENGLISH);
        fromDate = dateFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date toD = calendar.getTime();
        toDate = dateFormat.format(toD);
        getMatchHistoryAPI(true);
    }

    private void gotoProfile(String playerId) {
        if (!playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", playerId);
            startActivity(intent);
        }
    }

    private void populateData() {
        ///////////////  HEADER ////////////////
        binding.myProfileVu.populateData(matchResult.getPlayerOne().getNickName(), matchResult.getPlayerOne().getPhotoUrl(), matchResult.getPlayerOne().getLevel(), true);
        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
            binding.tvPoints.setText(String.format("%s:%s", matchResult.getPlayerTwo().getGoals(), matchResult.getPlayerOne().getGoals()));
        }
        else {
            binding.tvPoints.setText(String.format("%s:%s", matchResult.getPlayerOne().getGoals(), matchResult.getPlayerTwo().getGoals()));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(matchResult.getMatchDate());
            dateFormat.applyPattern("dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            binding.tvDate.setText("");
        }

        binding.tvTime.setText(matchResult.getMatchTime());
        binding.tvClubName.setText(matchResult.getClubName());
        binding.tvAddress.setText(String.format("%s-%s", matchResult.getDistance(), matchResult.getClubCity()));
        if (matchResult.getPlayerOne().getMatchStatus().equalsIgnoreCase("win")) {
            binding.winnerBadge1.setVisibility(View.VISIBLE);
        }
        else {
            binding.winnerBadge1.setVisibility(View.GONE);
        }

        OlePlayerInfo player2 = matchResult.getPlayerTwo();
        // check object null or not
        if (!player2.isEmpty()) {
            binding.opponentProfileVu.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
            if (matchResult.getPlayerTwo().getMatchStatus().equalsIgnoreCase("win")) {
                binding.winnerBadge2.setVisibility(View.VISIBLE);
            }
            else {
                binding.winnerBadge2.setVisibility(View.GONE);
            }
        }
        else {
            binding.opponentProfileVu.populateData("", "", null, false);
        }
        ///////////////  HEADER END ////////////////

        binding.myProfileVu1.populateData(matchResult.getPlayerOne().getNickName(), matchResult.getPlayerOne().getPhotoUrl(), matchResult.getPlayerOne().getLevel(), true);
        binding.tvP1Win.setText(matchResult.getPlayerOneWin());

        binding.tvDraw.setText(matchResult.getDrawMatches());
        binding.tvPlayed.setText(getResources().getString(R.string.total_played_place, matchResult.getTotalPlayed()));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date date = df.parse(matchResult.getLastPlayed());
            df.applyPattern("EEE, dd/MM/yyyy");
            binding.tvLastPlayed.setText(getResources().getString(R.string.last_played_place, df.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
            binding.tvLastPlayed.setText("");
        }

        binding.opponentProfileVu1.populateData(matchResult.getPlayerTwo().getNickName(), matchResult.getPlayerTwo().getPhotoUrl(), matchResult.getPlayerTwo().getLevel(), true);
        binding.tvP2Win.setText(matchResult.getPlayerTwoWin());
    }

    private void getMatchHistoryAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.profileMatchHistroy(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), playerId, fromDate, toDate, Constants.kFootballModule);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            oleMatchResults.clear();
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleMatchResults.add(gson.fromJson(arr.get(i).toString(), OleMatchResults.class));
                            }
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