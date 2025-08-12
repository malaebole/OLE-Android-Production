package ae.oleapp.activities;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePEarningListAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityPlayerPaymentBinding;
import ae.oleapp.fragments.OleEarnFilterFragment;
import ae.oleapp.models.Earning;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerPaymentActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPlayerPaymentBinding binding;
    private final List<Earning> earningList = new ArrayList<>();
    private OlePEarningListAdapter adapter;
    private OleEarnFilterFragment filterFragment;
    private String fromDate = "";
    private String toDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.my_payment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePEarningListAdapter(getContext(), earningList);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        getEarningsAPI(true);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fromDate = "";
                toDate = "";
                getEarningsAPI(false);
            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.filterBg.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.imgVu.setImageResource(R.drawable.sidemenu_padel);
        }
        else {
            binding.imgVu.setImageResource(R.drawable.payment_football);
        }

    }

    OlePEarningListAdapter.OnItemClickListener clickListener = new OlePEarningListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
//            Earning earning = earningList.get(pos);

        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.filterBg) {
            filterBgClicked();
        }
        else if (v == binding.btnFilter) {
            filterClicked();
        }
    }

    private void filterBgClicked() {
        removeFilterFrag();
    }

    private void removeFilterFrag() {
        getSupportFragmentManager().beginTransaction().remove(filterFragment).commit();
        filterFragment = null;
        binding.filterBg.setVisibility(View.GONE);
    }

    private void filterClicked() {
        if (filterFragment != null && filterFragment.isVisible()) {
            removeFilterFrag();
        }
        else {
            binding.filterBg.setVisibility(View.VISIBLE);
            filterFragment = new OleEarnFilterFragment(fromDate, toDate);
            filterFragment.setFragmentCallBack(fragmentCallBack);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.filter_container, filterFragment).commit();
        }
    }

    OleEarnFilterFragment.EarnFilterFragmentCallBack fragmentCallBack = new OleEarnFilterFragment.EarnFilterFragmentCallBack() {
        @Override
        public void getFilters(String fromDate, String toDate) {
            OlePlayerPaymentActivity.this.fromDate = fromDate;
            OlePlayerPaymentActivity.this.toDate = toDate;
            getEarningsAPI(true);
            removeFilterFrag();
        }
    };

    private void getEarningsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEarnings(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), fromDate, toDate, "player", Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONObject(Constants.kData).getJSONArray("payments");
                            String wallet = object.getJSONObject(Constants.kData).getString("wallet");
                            String currency = object.getJSONObject(Constants.kData).getString("currency");
                            String totalPayments = object.getJSONObject(Constants.kData).getString("total_payments");
                            binding.tvWallet.setText(String.format("%s %s", wallet, currency));
                            binding.tvSpent.setText(String.format("%s: %s %s", getString(R.string.total_spent), totalPayments, currency));
                            earningList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                earningList.add(gson.fromJson(arr.get(i).toString(), Earning.class));
                            }
                            adapter.notifyDataSetChanged();

                            if (earningList.size() == 0) {
                                Functions.showToast(getContext(), getString(R.string.data_not_found), FancyToast.ERROR);
                            }
                        }
                        else {
                            earningList.clear();
                            adapter.notifyDataSetChanged();
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
