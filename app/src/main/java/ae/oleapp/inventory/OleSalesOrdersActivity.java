package ae.oleapp.inventory;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
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
import ae.oleapp.adapters.OleSalesOrderAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivitySalesOrdersBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleInventoryOrder;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleSalesOrdersActivity extends BaseActivity implements View.OnClickListener {

    private OleactivitySalesOrdersBinding binding;
    private String fromDate = "", toDate = "", clubId = "", searchText = "";
    private final List<OleInventoryOrder> orderList = new ArrayList<>();
    private OleSalesOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivitySalesOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.sales_orders);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleSalesOrderAdapter(getContext(), orderList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.tvDate.setVisibility(View.GONE);

        fromDate = Functions.getPrefValue(getContext(), Constants.kInventoryOrderFromDate);
        toDate = Functions.getPrefValue(getContext(), Constants.kInventoryOrderToDate);
        populateDate();
        getSalesOrderAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnCalendar.setOnClickListener(this);
        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    searchText = query;
                    getSalesOrderAPI(false);
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
                getSalesOrderAPI(false);
                return true;
            }
        });
    }

    OleSalesOrderAdapter.ItemClickListener itemClickListener = new OleSalesOrderAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleSalesOrderDetailActivity.class);
            intent.putExtra("order_id", orderList.get(pos).getId());
            startActivity(intent);
        }
    };

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
                        editor.remove(Constants.kInventoryOrderFromDate);
                    }
                    else {
                        editor.putString(Constants.kInventoryOrderFromDate, fromDate);
                    }
                    if (toDate.equalsIgnoreCase("")) {
                        editor.remove(Constants.kInventoryOrderToDate);
                    }
                    else {
                        editor.putString(Constants.kInventoryOrderToDate, toDate);
                    }
                    editor.apply();
                    getSalesOrderAPI(true);
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

    private void getSalesOrderAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOrderItems(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fromDate, toDate, searchText);
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
                            orderList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                orderList.add(gson.fromJson(array.get(i).toString(), OleInventoryOrder.class));
                            }
                        }
                        else {
                            orderList.clear();
                            if (searchText.isEmpty()) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
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