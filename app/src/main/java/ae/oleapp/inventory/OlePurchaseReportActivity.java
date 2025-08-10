package ae.oleapp.inventory;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePurchaseReportAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPurchaseReportBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OlePurchaseHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePurchaseReportActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPurchaseReportBinding binding;
    private String clubId = "", fromDate = "", toDate = "", searchText = "";
    private OlePurchaseReportAdapter adapter;
    private final List<OlePurchaseHistory> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPurchaseReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.purchase_report);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePurchaseReportAdapter(getContext(), historyList);
        binding.recyclerVu.setAdapter(adapter);

        binding.tvDate.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnCalendar.setOnClickListener(this);
        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    searchText = query;
                    getPurchaseReportAPI(false);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.text_should_3_charachters), FancyToast.ERROR);
                }
                binding.searchVu.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = binding.searchVu.getQuery().toString();
                getPurchaseReportAPI(false);
                return true;
            }
        });

        getPurchaseReportAPI(true);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnCalendar) {
            showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
                @Override
                public void filterData(DialogFragment df, String from, String to) {
                    df.dismiss();
                    fromDate = from;
                    toDate = to;
                    binding.tvDate.setVisibility(View.VISIBLE);
                    if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                        binding.tvDate.setText(String.format("%s %s %s", fromDate, getString(R.string.to), toDate));
                    }
                    else if (!fromDate.isEmpty()) {
                        binding.tvDate.setText(fromDate);
                    }
                    else if (!toDate.isEmpty()) {
                        binding.tvDate.setText(toDate);
                    }
                    else {
                        binding.tvDate.setText("");
                        binding.tvDate.setVisibility(View.GONE);
                    }
                    getPurchaseReportAPI(true);
                }
            });
        }
    }

    private void calculateTotal(String currency) {
        double total = 0;
        for (OlePurchaseHistory history : historyList) {
            double price = Double.parseDouble(history.getPurchasePrice());
            double qty = Double.parseDouble(history.getPurchaseNewStock());
            total = total + (price * qty);
        }
        if (total > 0) {
            binding.tvTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", total, currency));
        }
        else {
            binding.tvTotal.setText(String.format("0 %s", currency));
        }
    }

    private void getPurchaseReportAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPurchaseHistory(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fromDate, toDate, searchText);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        String currency = "AED";
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            historyList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                OlePurchaseHistory history = gson.fromJson(array.get(i).toString(), OlePurchaseHistory.class);
                                historyList.add(history);
                                if (currency.isEmpty()) {
                                    currency = history.getCurrency();
                                }
                            }
                        }
                        else {
                            historyList.clear();
                            if (searchText.isEmpty()) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        }
                        calculateTotal(currency);
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