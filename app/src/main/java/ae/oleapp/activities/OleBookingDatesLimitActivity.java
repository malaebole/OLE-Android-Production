package ae.oleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateLimitPlayerAdapter;
import ae.oleapp.adapters.OleBookingDayLimitAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBookingDatesLimitBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingDatesLimitActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingDatesLimitBinding binding;
    private String clubId = "";
    private String days = "";
    private final List<OleKeyValuePair> daysList = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private final List<OlePlayerInfo> filterList = new ArrayList<>();
    private final List<Club> clubList = new ArrayList<>();
    private OleBookingDayLimitAdapter dayLimitAdapter;
    private OleBookingDateLimitPlayerAdapter playerAdapter;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingDatesLimitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.booking_dates_limit);

        int pos = 0;
        if (AppManager.getInstance().clubs.size() > 0) {
            clubId = AppManager.getInstance().clubs.get(0).getId();
            pos = 1;
        }

        Club club = new Club();
        club.setId("");
        club.setName(getString(R.string.all));
        clubList.add(club);
        clubList.addAll(AppManager.getInstance().clubs);
        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), clubList, pos, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        getDaysData();
        days = daysList.get(0).getKey();
        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        dayLimitAdapter = new OleBookingDayLimitAdapter(getContext(), daysList, days);
        dayLimitAdapter.setOnItemClickListener(onItemClickListener);
        binding.daysRecyclerVu.setAdapter(dayLimitAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.playersRecyclerVu.setLayoutManager(layoutManager);
        playerAdapter = new OleBookingDateLimitPlayerAdapter(getContext(), filterList);
        playerAdapter.setOnItemClickListener(itemClickListener);
        binding.playersRecyclerVu.setAdapter(playerAdapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

        binding.pullRefresh.setColorSchemeResources(R.color.blueColorNew);
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!clubId.equalsIgnoreCase("")) {
                    getListAPI(false);
                }
                else {
                    binding.pullRefresh.setRefreshing(false);
                }
            }
        });

        getListAPI(true);
    }

    private void getDaysData() {
        daysList.add(new OleKeyValuePair("1", getString(R.string.one_day)));
        daysList.add(new OleKeyValuePair("2", getString(R.string.two_days)));
        daysList.add(new OleKeyValuePair("4", getString(R.string.four_days)));
        daysList.add(new OleKeyValuePair("7", getString(R.string.one_week)));
        daysList.add(new OleKeyValuePair("15", getString(R.string.two_weeks)));
        daysList.add(new OleKeyValuePair("30", getString(R.string.one_month)));
        daysList.add(new OleKeyValuePair("60", getString(R.string.two_months)));
        daysList.add(new OleKeyValuePair("90", getString(R.string.three_months)));
        daysList.add(new OleKeyValuePair("180", getString(R.string.six_months)));
        daysList.add(new OleKeyValuePair("270", getString(R.string.nine_months)));
        daysList.add(new OleKeyValuePair("365", getString(R.string.one_year)));
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = clubList.get(pos).getId();
            if (!clubId.equalsIgnoreCase("")) {
                getListAPI(true);
            }
        }
    };

    OleBookingDayLimitAdapter.OnItemClickListener onItemClickListener = new OleBookingDayLimitAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            days = daysList.get(pos).getKey();
            dayLimitAdapter.setSelectedDay(days);
            filterData();
        }
    };

    OleBookingDateLimitPlayerAdapter.OnItemClickListener itemClickListener = new OleBookingDateLimitPlayerAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (clubId.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.remove), getResources().getString(R.string.payment_method))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                removePlayerAPI(true, filterList.get(pos).getId());
                            }
                            else {
                                showPaymentMethod(Collections.singletonList(filterList.get(pos)), true, pos);
                            }
                        }
                    }).show();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnAdd) {
            Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
            intent.putExtra("is_selection", true);
            startActivityForResult(intent, 106);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            showPaymentMethod(list, false, 0);
        }
    }

    private void showPaymentMethod(List<OlePlayerInfo> list, boolean isUpdate, int pos) {
        List<OleSelectionList> oleSelectionList;
        oleSelectionList = Arrays.asList(new OleSelectionList("both", getString(R.string.cash_card)), new OleSelectionList("cash", getString(R.string.cash)), new OleSelectionList("card", getString(R.string.card)));
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.payment_method), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                if (isUpdate) {
                    updatePlayerMethodAPI(true, list.get(0).getId(), selectedItem.getId(), pos);
                }
                else {
                    addPlayerAPI(true, selectedItem.getId(), list);
                }
            }
        });
        dialog.show();
    }

    private boolean isExist(String id) {
        boolean result = false;
        for (int i = 0; i < filterList.size(); i++) {
            if (filterList.get(i).getId().equalsIgnoreCase(id)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void filterData() {
        filterList.clear();
        for (OlePlayerInfo info: playerList) {
            if (info.getDays().equalsIgnoreCase(days)) {
                filterList.add(info);
            }
        }
        playerAdapter.notifyDataSetChanged();
    }

    private void addPlayerAPI(boolean isLoader, String paymentMethod, List<OlePlayerInfo> list) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String pIds = "";
        for (OlePlayerInfo info : list) {
            if (pIds.isEmpty()) {
                pIds = info.getId();
            }
            else {
                pIds = String.format("%s,%s", pIds, info.getId());
            }
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.slotAndPayment(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, pIds, days, paymentMethod);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            for (OlePlayerInfo player: list) {
                                if (!isExist(player.getId())) {
                                    player.setPaymentMethod(paymentMethod);
                                    filterList.add(player);
                                }
                            }
                            playerAdapter.notifyDataSetChanged();
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

    private void getListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.slotAndPaymentList(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            playerList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                playerList.add(gson.fromJson(arr.get(i).toString(), OlePlayerInfo.class));
                            }
                            filterData();
                        }
                        else {
                            playerList.clear();
                            filterData();
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
                binding.pullRefresh.setRefreshing(false);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void removePlayerAPI(boolean isLoader, String playerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeFromRestriction(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, playerId, days);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            for (int i = 0; i < playerList.size(); i++) {
                                if (playerList.get(i).getId().equalsIgnoreCase(playerId)) {
                                    playerList.remove(i);
                                    break;
                                }
                            }
                            filterData();
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

    private void updatePlayerMethodAPI(boolean isLoader, String playerId, String paymentMethod, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePlayerPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, playerId, paymentMethod);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            filterList.get(pos).setPaymentMethod(paymentMethod);
                            playerAdapter.notifyItemChanged(pos);
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