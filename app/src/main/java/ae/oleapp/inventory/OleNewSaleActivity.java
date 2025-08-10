package ae.oleapp.inventory;

import androidx.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleNewSaleAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityNewSaleBinding;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleNewSaleActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityNewSaleBinding binding;
    private String clubId = "";
    private final List<OleInventoryItem> itemList = new ArrayList<>();
    private OleNewSaleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityNewSaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.sell);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleNewSaleAdapter(getContext(), itemList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnTotal.setAlpha(0.5f);
        binding.tvTotal.setText(String.format("%s 0 %s", getString(R.string.pay), getString(R.string.aed)));
        getInventoryAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnTotal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnTotal) {
            List<OleInventoryItem> selectedList = new ArrayList<>();
            for (OleInventoryItem item : itemList) {
                if (item.getQty() > 0) {
                    selectedList.add(item);
                }
            }
            if (selectedList.size() > 0) {
                Intent intent = new Intent(getContext(), OleInventoryCheckoutActivity.class);
                intent.putExtra("data", new Gson().toJson(selectedList));
                intent.putExtra("club_id", clubId);
                startActivityForResult(intent, 1313);
            }
        }
    }

    OleNewSaleAdapter.ItemClickListener itemClickListener = new OleNewSaleAdapter.ItemClickListener() {
        @Override
        public void plusClicked(View view, int pos) {
            int qty = itemList.get(pos).getQty();
            int stock = Integer.parseInt(itemList.get(pos).getCurrentStock());
            if (qty < stock) {
                itemList.get(pos).setQty(qty+1);
                adapter.notifyDataSetChanged();
                calculatePrice();
            }
        }

        @Override
        public void minusClicked(View view, int pos) {
            int qty = itemList.get(pos).getQty();
            if (qty > 0) {
                itemList.get(pos).setQty(qty-1);
                adapter.notifyDataSetChanged();
                calculatePrice();
            }
        }
    };

    private void calculatePrice() {
        double total = 0;
        String currency = "";
        for (OleInventoryItem item : itemList) {
            int qty = item.getQty();
            double price = Double.parseDouble(item.getSalePrice());
            total = total + (qty * price);
            if (currency.isEmpty()) {
                currency = item.getCurrency();
            }
        }
        if (total > 0) {
            binding.btnTotal.setAlpha(1.0f);
            binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s %.2f %s", getString(R.string.pay), total, currency));
        }
        else {
            binding.btnTotal.setAlpha(0.5f);
            binding.tvTotal.setText(String.format("%s 0 %s", getString(R.string.pay), currency));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1313) {
            this.finish();
        }
    }

    private void getInventoryAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getInventory(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            itemList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < array.length(); i++) {
                                itemList.add(gson.fromJson(array.get(i).toString(), OleInventoryItem.class));
                            }
                        }
                        else {
                            itemList.clear();
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