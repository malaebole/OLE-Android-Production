package ae.oleapp.shop;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleProductListAdapter;
import ae.oleapp.adapters.OleProductListCatAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityProductListBinding;
import ae.oleapp.dialogs.OleShopFilterDialogFragment;
import ae.oleapp.dialogs.OleShopSortingDialogFragment;
import ae.oleapp.models.Product;
import ae.oleapp.models.OleProductBrand;
import ae.oleapp.models.OleProductCategory;
import ae.oleapp.models.OleShopFilter;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleEndlessRecyclerViewScrollListener;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityProductListBinding binding;
    private OleProductListAdapter adapter;
    private final List<Product> productList = new ArrayList<>();
    private final List<OleShopFilter> filterList = new ArrayList<>();
    private String typeId = "";
    private String type = "";
    private String keyword = "";
    private String title = "";
    private int pageNo = 1;
    private String sortType = "";
    private List<OleShopFilter> selectedFilter = new ArrayList<>();
    private String minPrice = "";
    private String maxPrice = "";
    private String subCatId = "";
    private OleEndlessRecyclerViewScrollListener scrollListener;
    private OleProductListCatAdapter catAdapter;
    private final List<OleProductCategory> categoryList = new ArrayList<>();
    private final List<OleProductBrand> brandList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeId = bundle.getString("id", "");
            type = bundle.getString("type", "");
            keyword = bundle.getString("keyword", "");
            title = bundle.getString("title", "");
        }

        binding.titleBar.toolbarTitle.setText(title);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                getProductsAPI(false);
            }
        });

        if (type.equalsIgnoreCase("brand")) {
            getBrandsAPI(false);
        }
        else {
            getSubCategoriesAPI(false);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(gridLayoutManager);
        adapter = new OleProductListAdapter(getContext(), productList, false);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.catRecyclerVu.setLayoutManager(layoutManager);
        catAdapter = new OleProductListCatAdapter(getContext(), Arrays.asList(categoryList.toArray()));
        catAdapter.setItemClickListener(catClicked);
        binding.catRecyclerVu.setAdapter(catAdapter);

        scrollListener = new OleEndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 50) {
                    pageNo = page + 1;
                    getProductsAPI(false);
                }
            }
        };
        binding.recyclerVu.addOnScrollListener(scrollListener);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.relFilter.setOnClickListener(this);
        binding.relSort.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductsAPI(productList.isEmpty());
    }

    OleProductListCatAdapter.ItemClickListener catClicked = new OleProductListCatAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (catAdapter.getSelectedIndex() == pos) {
                if (!type.equalsIgnoreCase("brand")) {
                    subCatId = "";
                    catAdapter.setSelectedIndex(-1);
                }
            }
            else {
                catAdapter.setSelectedIndex(pos);
                if (type.equalsIgnoreCase("brand")) {
                    typeId = brandList.get(pos).getId();
                }
                else {
                    subCatId = categoryList.get(pos).getId();
                }
            }
            pageNo = 1;
            getProductsAPI(true);
        }
    };

    OleProductListAdapter.ItemClickListener clickListener = new OleProductListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("prod_id", productList.get(pos).getId());
            startActivity(intent);
        }

        @Override
        public void favClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Product product = productList.get(pos);
            if (product.getIsFavorite().equalsIgnoreCase("1")) {
                removeFromWishlist(true, product.getId(), pos);
            }
            else {
                addToWishlist(true, product.getId(), pos);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.relFilter) {
            filterClicked();
        }
        else if (v == binding.relSort) {
            sortClicked();
        }
    }

    private void filterClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ShopFilterDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleShopFilterDialogFragment dialogFragment = new OleShopFilterDialogFragment(filterList, selectedFilter, minPrice, maxPrice);
        dialogFragment.setDialogCallback(new OleShopFilterDialogFragment.ShopFilterDialogCallback() {
            @Override
            public void filters(DialogFragment df, List<OleShopFilter> filter, String minPrice, String maxPrice) {
                df.dismiss();
                selectedFilter = filter;
                ProductListActivity.this.minPrice = minPrice;
                ProductListActivity.this.maxPrice = maxPrice;
                pageNo = 1;
                getProductsAPI(true);
            }
        });
        dialogFragment.show(fragmentTransaction, "ShopFilterDialogFragment");
    }

    private void sortClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ShopSortingDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleShopSortingDialogFragment dialogFragment = new OleShopSortingDialogFragment();
        dialogFragment.setDialogCallback(new OleShopSortingDialogFragment.ShopSortingDialogCallback() {
            @Override
            public void sorting(DialogFragment df, String sorting) {
                df.dismiss();
                sortType = sorting;
                pageNo = 1;
                getProductsAPI(true);
            }
        });
        dialogFragment.show(fragmentTransaction, "ShopSortingDialogFragment");
    }

    private void getProductsAPI(boolean isLoader) {
        String filterStr = "";
        try {
            JSONArray array = new JSONArray();
            for (OleShopFilter filter : selectedFilter) {
                JSONObject object = new JSONObject();
                object.put("title", filter.getTitle());
                object.put("value", filter.getValue());
                array.put(object);
            }
            filterStr = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        KProgressHUD hud = pageNo == 1 && isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getProducts(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), typeId, keyword, type, sortType, filterStr, minPrice, maxPrice, pageNo, subCatId);
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
                            JSONArray arrF = object.getJSONArray("filters");
                            if (type.equalsIgnoreCase("category")) {
                                binding.titleBar.toolbarTitle.setText(object.getJSONObject("category").getString("name"));
                            }
                            else {
                                binding.titleBar.toolbarTitle.setText(object.getJSONObject("brand").getString("name"));
                            }
                            filterList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arrF.length(); i++) {
                                filterList.add(gson.fromJson(arrF.get(i).toString(), OleShopFilter.class));
                            }
                            if (pageNo == 1) {
                                productList.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    productList.add(gson.fromJson(arr.get(i).toString(), Product.class));
                                }
                            }
                            else {
                                List<Product> more = new ArrayList<>();
                                for (int i = 0; i < arr.length(); i++) {
                                    more.add(gson.fromJson(arr.get(i).toString(), Product.class));
                                }

                                if (more.size() > 0) {
                                    productList.addAll(more);
                                }
                                else {
                                    pageNo = pageNo-1;
                                }
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

    private void getSubCategoriesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getSubCategories(Functions.getAppLang(getContext()), typeId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrC = object.getJSONArray(Constants.kData);
                            categoryList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arrC.length(); i++) {
                                categoryList.add(gson.fromJson(arrC.get(i).toString(), OleProductCategory.class));
                            }
                        }
                        else {
                            categoryList.clear();
                        }
                        catAdapter.setDataSource(Arrays.asList(categoryList.toArray()));
                        if (categoryList.size() == 0) {
                            binding.catRecyclerVu.setVisibility(View.GONE);
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

    private void getBrandsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getBrands(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            brandList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                brandList.add(gson.fromJson(arr.get(i).toString(), OleProductBrand.class));
                            }
                            catAdapter.setDataSource(Arrays.asList(brandList.toArray()));
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

    private void addToWishlist(boolean isLoader, String prodId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addToWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    productList.get(pos).setIsFavorite("1");
                    adapter.notifyItemChanged(pos);
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void removeFromWishlist(boolean isLoader, String prodId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        removeFromWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    productList.get(pos).setIsFavorite("0");
                    adapter.notifyItemChanged(pos);
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }
}
