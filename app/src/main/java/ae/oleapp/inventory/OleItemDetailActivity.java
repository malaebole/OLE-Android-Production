package ae.oleapp.inventory;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleItemDetailAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityItemDetailBinding;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.models.OleStockHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleItemDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityItemDetailBinding binding;
    private String itemId = "";
    private OleInventoryItem item;
    private final List<OleStockHistory> historyList = new ArrayList<>();
    private OleItemDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemId = bundle.getString("item_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleItemDetailAdapter(getContext(), historyList);
        binding.recyclerVu.setAdapter(adapter);

        binding.vu.setVisibility(View.INVISIBLE);
        getItemDetailAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
    }

    private void populateData() {
        binding.vu.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(item.getPhoto()).into(binding.imgVu);
        binding.tvItemName.setText(item.getName());
        binding.tvStock.setText(String.format("%s: %s", getString(R.string.current_stock), item.getCurrentStock()));
        historyList.clear();
        historyList.addAll(item.getStockHistory());
        adapter.notifyDataSetChanged();
    }

    private void getItemDetailAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getStockHistory(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), itemId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            item = new Gson().fromJson(obj.toString(), OleInventoryItem.class);
                            populateData();
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