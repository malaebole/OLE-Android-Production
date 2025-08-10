package ae.oleapp.inventory;

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

import ae.oleapp.R;
import ae.oleapp.adapters.OlePurchaseListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPurchaseListBinding;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePurchaseListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPurchaseListBinding binding;
    private OlePurchaseListAdapter adapter;
    private String clubId = "";
    private final List<OleInventoryItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPurchaseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.purchase);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePurchaseListAdapter(getContext(), itemList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInventoryAPI(itemList.isEmpty());
    }

    OlePurchaseListAdapter.ItemClickListener itemClickListener = new OlePurchaseListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleItemDetailActivity.class);
            intent.putExtra("item_id", itemList.get(pos).getId());
            startActivity(intent);
        }

        @Override
        public void editClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleAddItemActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("item", new Gson().toJson(itemList.get(pos)));
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnAdd) {
            Intent intent = new Intent(getContext(), OleAddItemActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
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