package ae.oleapp.shop;

import androidx.appcompat.widget.SearchView;
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
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleShopSearchAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityShopSearchBinding;
import ae.oleapp.models.OleSearchResult;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopSearchActivity extends BaseActivity {

    private ActivityShopSearchBinding binding;
    private String query = "";
    private final List<OleSearchResult> resultList = new ArrayList<>();
    private OleShopSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchVu.requestFocus();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleShopSearchAdapter(getContext(), resultList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (binding.searchVu.getQuery().toString().equalsIgnoreCase("")) {
                    resultList.clear();
                    adapter.notifyDataSetChanged();
                }
                else {
                    ShopSearchActivity.this.query = binding.searchVu.getQuery().toString();
                    searchAPI(false);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = binding.searchVu.getQuery().toString();
                if (query.equalsIgnoreCase("")) {
                    resultList.clear();
                    adapter.notifyDataSetChanged();
                }
                else {
                    searchAPI(false);
                }
                return true;
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    OleShopSearchAdapter.ItemClickListener itemClickListener = new OleShopSearchAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleSearchResult oleSearchResult = resultList.get(pos);
            if (oleSearchResult.getType().equalsIgnoreCase("product")) {
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("prod_id", oleSearchResult.getTargetId());
                startActivity(intent);
            }
            else {
                if (oleSearchResult.getType().equalsIgnoreCase("category") && oleSearchResult.getProductCount().equalsIgnoreCase("0")) {
                    Functions.showToast(getContext(), getString(R.string.coming_soon), FancyToast.SUCCESS, FancyToast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent(getContext(), ProductListActivity.class);
                intent.putExtra("id", oleSearchResult.getTargetId());
                intent.putExtra("type", oleSearchResult.getType());
                intent.putExtra("title", "");
                intent.putExtra("keyword", query);
                startActivity(intent);
            }
        }

        @Override
        public void arrowClicked(View view, int pos) {
            binding.searchVu.setQuery(resultList.get(pos).getName(), true);
        }
    };

    private void searchAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.search(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), query);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            resultList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                resultList.add(gson.fromJson(arr.get(i).toString(), OleSearchResult.class));
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