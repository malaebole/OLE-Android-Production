package ae.oleapp.padel;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleProfilePadelMatchHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityProfilePadelMatchHistoryDetailsBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleProfilePadelMatchHistoryDetailsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityProfilePadelMatchHistoryDetailsBinding binding;
    private OleProfilePadelMatchHistoryAdapter adapter;
    private final List<OlePadelMatchResults> matchResults = new ArrayList<>();
    private List<Date> dateList = new ArrayList<>();
    private String fromDate = "";
    private String toDate = "";
    private String playerId = "";
    private final String kDateFormat = "dd/MM/yyyy";
    private OlePadelMatchResults matchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityProfilePadelMatchHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchResult = new Gson().fromJson(bundle.getString("match"), OlePadelMatchResults.class);
            playerId = bundle.getString("player_id", "");
            populateData();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleProfilePadelMatchHistoryAdapter(getContext(), matchResults);
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
        binding.myProfileVu.setOnClickListener(this);
        binding.myPartnerProfileVu.setOnClickListener(this);
        binding.opponentProfileVu.setOnClickListener(this);
        binding.opponentPartnerProfileVu.setOnClickListener(this);
        binding.dateVu.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.backBtn) {
            finish();
        }
        else if (view == binding.myProfileVu) {
            gotoProfile(matchResult.getCreatedBy().getId());
        }
        else if (view == binding.myPartnerProfileVu) {
            gotoProfile(matchResult.getCreatorPartner().getId());
        }
        else if (view == binding.opponentProfileVu) {
            gotoProfile(matchResult.getPlayerTwo().getId());
        }
        else if (view == binding.opponentPartnerProfileVu) {
            gotoProfile(matchResult.getPlayerTwoPartner().getId());
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

    private void populateData() {
        binding.myProfileVu.populateData(matchResult.getCreatedBy().getNickName(), matchResult.getCreatedBy().getPhotoUrl(), matchResult.getCreatedBy().getLevel(), true);
        binding.myPartnerProfileVu.populateData(matchResult.getCreatorPartner().getNickName(), matchResult.getCreatorPartner().getPhotoUrl(), matchResult.getCreatorPartner().getLevel(), true);
        binding.tvDate.setText(matchResult.getMatchDate());
        binding.tvTime.setText(matchResult.getMatchTime());
        if (matchResult.getCreatorWin().equalsIgnoreCase("1")) {
            binding.winnerBadge1.setVisibility(View.VISIBLE);
        }
        else {
            binding.winnerBadge1.setVisibility(View.INVISIBLE);
        }
        if (matchResult.getPlayerTwoWin().equalsIgnoreCase("1")) {
            binding.winnerBadge2.setVisibility(View.VISIBLE);
        }
        else {
            binding.winnerBadge2.setVisibility(View.INVISIBLE);
        }
        binding.opponentProfileVu.populateData(matchResult.getPlayerTwo().getNickName(), matchResult.getPlayerTwo().getPhotoUrl(), matchResult.getPlayerTwo().getLevel(), true);
        binding.opponentPartnerProfileVu.populateData(matchResult.getPlayerTwoPartner().getNickName(), matchResult.getPlayerTwoPartner().getPhotoUrl(), matchResult.getPlayerTwoPartner().getLevel(), true);
        if (matchResult.getCreatorScore().getSetOne() == 1) {
            binding.imgTeamASet1.setImageResource(R.drawable.tick_green);
        }
        else {
            binding.imgTeamASet1.setImageResource(R.drawable.red_cross);
        }
        if (matchResult.getCreatorScore().getSetTwo() == 1) {
            binding.imgTeamASet2.setImageResource(R.drawable.tick_green);
        }
        else {
            binding.imgTeamASet2.setImageResource(R.drawable.red_cross);
        }
        if (matchResult.getCreatorScore().getSetThree() == 1) {
            binding.imgTeamASet3.setImageResource(R.drawable.tick_green);
        }
        else {
            binding.imgTeamASet3.setImageResource(R.drawable.red_cross);
        }

        if (matchResult.getPlayerTwoScore().getSetOne() == 1) {
            binding.imgTeamBSet1.setImageResource(R.drawable.tick_green);
        }
        else {
            binding.imgTeamBSet1.setImageResource(R.drawable.red_cross);
        }
        if (matchResult.getPlayerTwoScore().getSetTwo() == 1) {
            binding.imgTeamBSet2.setImageResource(R.drawable.tick_green);
        }
        else {
            binding.imgTeamBSet2.setImageResource(R.drawable.red_cross);
        }
        if (matchResult.getPlayerTwoScore().getSetThree() == 1) {
            binding.imgTeamBSet3.setImageResource(R.drawable.tick_green);
        }
        else {
            binding.imgTeamBSet3.setImageResource(R.drawable.red_cross);
        }
//        binding.tvTeamASet1.setText(String.valueOf(matchResult.getCreatorScore().getSetOne()));
//        binding.tvTeamASet2.setText(String.valueOf(matchResult.getCreatorScore().getSetTwo()));
//        binding.tvTeamASet3.setText(String.valueOf(matchResult.getCreatorScore().getSetThree()));
//        binding.tvTeamBSet1.setText(String.valueOf(matchResult.getPlayerTwoScore().getSetOne()));
//        binding.tvTeamBSet2.setText(String.valueOf(matchResult.getPlayerTwoScore().getSetTwo()));
//        binding.tvTeamBSet3.setText(String.valueOf(matchResult.getPlayerTwoScore().getSetThree()));
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

    private void getMatchHistoryAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.profileMatchHistroy(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), playerId, fromDate, toDate, Constants.kPadelModule);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            matchResults.clear();
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                matchResults.add(gson.fromJson(arr.get(i).toString(), OlePadelMatchResults.class));
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