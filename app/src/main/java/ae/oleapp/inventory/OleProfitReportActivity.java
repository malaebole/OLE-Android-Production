package ae.oleapp.inventory;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
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

import ae.oleapp.R;
import ae.oleapp.adapters.OleProfitReportAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityProfitReportBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleProfitReportActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityProfitReportBinding binding;
    private String fromDate = "", toDate = "", clubId = "";
    private OleProfitReportAdapter adapter;
    private final List<OleInventoryItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityProfitReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.profit_report);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleProfitReportAdapter(getContext(), itemList);
        binding.recyclerVu.setAdapter(adapter);

        binding.tvDate.setVisibility(View.GONE);
        fromDate = Functions.getPrefValue(getContext(), Constants.kInventoryProfitReportFromDate);
        toDate = Functions.getPrefValue(getContext(), Constants.kInventoryProfitReportToDate);
        populateDate();

        getProfitReportAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnCalendar.setOnClickListener(this);
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
                    populateDate();
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (fromDate.equalsIgnoreCase("")) {
                        editor.remove(Constants.kInventoryProfitReportFromDate);
                    }
                    else {
                        editor.putString(Constants.kInventoryProfitReportFromDate, fromDate);
                    }
                    if (toDate.equalsIgnoreCase("")) {
                        editor.remove(Constants.kInventoryProfitReportToDate);
                    }
                    else {
                        editor.putString(Constants.kInventoryProfitReportToDate, toDate);
                    }
                    editor.apply();
                    getProfitReportAPI(true);
                }
            });
        }
    }

    private void populateDate() {
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
    }

    private void getProfitReportAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getProfitReport(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fromDate, toDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            itemList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                itemList.add(gson.fromJson(array.get(i).toString(), OleInventoryItem.class));
                            }
                            String currency = object.getString("currency");
                            binding.tvSale.setText(String.format("%s %s", object.getString("sales_total"), currency));
                            binding.tvDiscount.setText(String.format("%s %s", object.getString("discount"), currency));
                            binding.tvProfit.setText(String.format("%s %s", object.getString("total_profit"), currency));
                        }
                        else {
                            itemList.clear();
                            binding.tvSale.setText("0 AED");
                            binding.tvDiscount.setText("0 AED");
                            binding.tvProfit.setText("0 AED");
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