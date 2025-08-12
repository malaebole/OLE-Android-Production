package ae.oleapp.owner;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.OleFullImageActivity;
import ae.oleapp.adapters.OleEarningBalanceDetailAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEarningBalanceDetailBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OlePlayerBalance;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEarningBalanceDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEarningBalanceDetailBinding binding;
    private String fromDate = "", toDate = "", clubId = "";
    private boolean isPaid = false;
    private OleEarningBalanceDetailAdapter adapter;
    private final List<OlePlayerBalance> pendingBalanceList = new ArrayList<>();
    private final List<OlePlayerBalance> paidBalanceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEarningBalanceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.balance);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromDate = bundle.getString("from_date", "");
            toDate = bundle.getString("to_date", "");
            clubId = bundle.getString("club_id", "");
            isPaid = bundle.getBoolean("is_paid", false);
        }

        if (isPaid) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1), true);
        }
        else {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0), true);
        }

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OleEarningBalanceDetailAdapter(getContext(), pendingBalanceList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBalanceDetailAPI(false);
            }
        });

        getBalanceDetailAPI(true);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.relCalendar.setOnClickListener(this);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    isPaid = false;
                    adapter.setDatasource(pendingBalanceList, false);
                }
                else {
                    isPaid = true;
                    adapter.setDatasource(paidBalanceList, true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.relCalendar) {
            calendarClicked();
        }
    }

    OleEarningBalanceDetailAdapter.ItemClickListener itemClickListener = new OleEarningBalanceDetailAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OlePlayerBalance player;
            if (isPaid) {
                player = paidBalanceList.get(pos);
            }
            else {
                player = pendingBalanceList.get(pos);
            }
            Intent intent = new Intent(getContext(), OleBookingCountDetailActivity.class);
            intent.putExtra("player_id", player.getPlayerId());
            intent.putExtra("player_phone", player.getPlayerPhone());
            intent.putExtra("player_name", player.getPlayerName());
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }

        @Override
        public void receiptClicked(View view, int pos) {
            if (isPaid) {
                OlePlayerBalance player = paidBalanceList.get(pos);
                if (!player.getReceipt().equalsIgnoreCase("")) {
                    Intent intent = new Intent(getContext(), OleFullImageActivity.class);
                    intent.putExtra("URL", player.getReceipt());
                    startActivity(intent);
                }
            }
        }
    };

    private void calendarClicked() {
        showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
            @Override
            public void filterData(DialogFragment df, String from, String to) {
                df.dismiss();
                fromDate = from;
                toDate = to;
                getBalanceDetailAPI(true);
            }
        });
    }

    private void getBalanceDetailAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.earningBalanceDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fromDate, toDate);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrPending = object.getJSONObject(Constants.kData).getJSONArray("added_balance");
                            JSONArray arrPaid = object.getJSONObject(Constants.kData).getJSONArray("paid_balance");
                            pendingBalanceList.clear();
                            paidBalanceList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arrPending.length(); i++) {
                                OlePlayerBalance balance = gson.fromJson(arrPending.get(i).toString(), OlePlayerBalance.class);
                                pendingBalanceList.add(balance);
                            }
                            for (int i = 0; i < arrPaid.length(); i++) {
                                OlePlayerBalance balance = gson.fromJson(arrPaid.get(i).toString(), OlePlayerBalance.class);
                                paidBalanceList.add(balance);
                            }
                            if (isPaid) {
                                adapter.setDatasource(paidBalanceList, true);
                            }
                            else {
                                adapter.setDatasource(pendingBalanceList, false);
                            }
                        }
                        else {
                            pendingBalanceList.clear();
                            paidBalanceList.clear();
                            adapter.setDatasource(new ArrayList<>(), false);
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
}