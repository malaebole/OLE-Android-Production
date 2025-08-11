package ae.oleapp.shop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
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
import ae.oleapp.adapters.BestPicksAdapter;
import ae.oleapp.adapters.FlashDealsAdapter;
import ae.oleapp.adapters.HotTrendingAdapter;
import ae.oleapp.adapters.ProductBrandAdapter;
import ae.oleapp.adapters.ProductListAdapter;
import ae.oleapp.adapters.ShopBannerAdapter;
import ae.oleapp.adapters.ShopCatAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityShopBinding;
import ae.oleapp.models.Product;
import ae.oleapp.models.ProductBrand;
import ae.oleapp.models.ProductCategory;
import ae.oleapp.models.ShopBanner;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends BaseActivity implements View.OnClickListener {

    private ActivityShopBinding binding;
    private final List<ShopBanner> bannerList = new ArrayList<>();
    private final List<ProductCategory> categoryList = new ArrayList<>();
    private final List<Product> trendingList = new ArrayList<>();
    private final List<Product> dealsList = new ArrayList<>();
    private final List<Product> bestPickList = new ArrayList<>();
    private final List<Product> hotTrendingList = new ArrayList<>();
    private final List<ProductBrand> brandList = new ArrayList<>();
    private ShopBannerAdapter bannerAdapter;
    private ShopCatAdapter catAdapter;
    private ProductListAdapter trendingAdapter;
    private FlashDealsAdapter flashDealsAdapter;
    private BestPicksAdapter bestPicksAdapter;
    private HotTrendingAdapter hotTrendingAdapter;
    private ProductBrandAdapter productBrandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cartBadge.setVisibility(View.GONE);
        binding.bannerCard.setVisibility(View.GONE);
        binding.catVu.setVisibility(View.GONE);
        binding.trendingVu.setVisibility(View.GONE);
        binding.flashDealsVu.setVisibility(View.GONE);
        binding.hotTrendVu.setVisibility(View.GONE);
        binding.bestPicksVu.setVisibility(View.GONE);
        binding.brandsVu.setVisibility(View.GONE);

        bannerAdapter = new ShopBannerAdapter(getContext(), bannerList);
        bannerAdapter.setItemClickListener(bannerClicked);
        binding.bannerVu.setAdapter(bannerAdapter);

        LinearLayoutManager catLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.catRecyclerVu.setLayoutManager(catLayoutManager);
        catAdapter = new ShopCatAdapter(getContext(), categoryList);
        catAdapter.setItemClickListener(categoryClicked);
        binding.catRecyclerVu.setAdapter(catAdapter);

        LinearLayoutManager trendLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.trendingRecyclerVu.setLayoutManager(trendLayoutManager);
        trendingAdapter = new ProductListAdapter(getContext(), trendingList, true);
        trendingAdapter.setItemClickListener(trendingClicked);
        binding.trendingRecyclerVu.setAdapter(trendingAdapter);

        LinearLayoutManager dealLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.flashDealsRecyclerVu.setLayoutManager(dealLayoutManager);
        flashDealsAdapter = new FlashDealsAdapter(getContext(), dealsList);
        flashDealsAdapter.setItemClickListener(dealClicked);
        binding.flashDealsRecyclerVu.setAdapter(flashDealsAdapter);

        LinearLayoutManager bestPickLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.bestPicksRecyclerVu.setLayoutManager(bestPickLayoutManager);
        bestPicksAdapter = new BestPicksAdapter(getContext(), bestPickList);
        bestPicksAdapter.setItemClickListener(bestPickClicked);
        binding.bestPicksRecyclerVu.setAdapter(bestPicksAdapter);

        LinearLayoutManager hotTrendLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.hotTrendRecyclerVu.setLayoutManager(hotTrendLayoutManager);
        hotTrendingAdapter = new HotTrendingAdapter(getContext(), hotTrendingList);
        hotTrendingAdapter.setItemClickListener(hotTrendingClicked);
        binding.hotTrendRecyclerVu.setAdapter(hotTrendingAdapter);

        LinearLayoutManager brandLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.brandsRecyclerVu.setLayoutManager(brandLayoutManager);
        productBrandAdapter = new ProductBrandAdapter(getContext(), brandList);
        productBrandAdapter.setItemClickListener(brandClicked);
        binding.brandsRecyclerVu.setAdapter(productBrandAdapter);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeDataAPI(false);
            }
        });

        binding.relCart.setOnClickListener(this);
        binding.searchVu.setOnClickListener(this);
        binding.whatsappVu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBadgeValue();
        getHomeDataAPI(categoryList.isEmpty());
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void setCartBadge(String count) {
        int cartCount = 0;
        if (!count.isEmpty()) {
            cartCount = Integer.parseInt(count);
        }
        if (cartCount > 0) {
            binding.cartBadge.setVisibility(View.VISIBLE);
            binding.cartBadge.setNumber(cartCount);
        }
        else  {
            binding.cartBadge.setVisibility(View.GONE);
        }
    }

    ShopBannerAdapter.ItemClickListener bannerClicked = new ShopBannerAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            ShopBanner banner = bannerList.get(pos);
            if (banner.getType().equalsIgnoreCase("product")) {
                openProductDetail(banner.getTargetId());
            }
            else if (banner.getType().equalsIgnoreCase("category")) {
                openProductList(banner.getTargetId(), "", "category");
            }
            else if (banner.getType().equalsIgnoreCase("brand")) {
                openProductList(banner.getTargetId(), "", "brand");
            }
        }
    };

    ShopCatAdapter.ItemClickListener categoryClicked = new ShopCatAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!categoryList.get(pos).getProductCount().equalsIgnoreCase("0")) {
                openProductList(categoryList.get(pos).getId(), categoryList.get(pos).getName(), "category");
            }
            else {
                Functions.showToast(getContext(), getString(R.string.coming_soon), FancyToast.SUCCESS, FancyToast.LENGTH_SHORT);
            }
        }
    };

    ProductListAdapter.ItemClickListener trendingClicked = new ProductListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            openProductDetail(trendingList.get(pos).getId());
        }

        @Override
        public void favClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Product product = trendingList.get(pos);
            if (product.getIsFavorite().equalsIgnoreCase("1")) {
                removeFromWishlist(true, product.getId(), pos, binding.trendingRecyclerVu);
            }
            else {
                addToWishlist(true, product.getId(), pos, binding.trendingRecyclerVu);
            }
        }
    };

    FlashDealsAdapter.ItemClickListener dealClicked = new FlashDealsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            openProductDetail(dealsList.get(pos).getId());
        }
    };

    BestPicksAdapter.ItemClickListener bestPickClicked = new BestPicksAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            openProductDetail(bestPickList.get(pos).getId());
        }

        @Override
        public void favClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Product product = bestPickList.get(pos);
            if (product.getIsFavorite().equalsIgnoreCase("1")) {
                removeFromWishlist(true, product.getId(), pos, binding.bestPicksRecyclerVu);
            }
            else {
                addToWishlist(true, product.getId(), pos, binding.bestPicksRecyclerVu);
            }
        }
    };

    HotTrendingAdapter.ItemClickListener hotTrendingClicked = new HotTrendingAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            openProductDetail(hotTrendingList.get(pos).getId());
        }
    };

    ProductBrandAdapter.ItemClickListener brandClicked = new ProductBrandAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            openProductList(brandList.get(pos).getId(), brandList.get(pos).getName(), "brand");
        }
    };

    private void openProductList(String id, String name, String type) {
        Intent intent = new Intent(getContext(), ProductListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("title", name);
        intent.putExtra("keyword", "");
        startActivity(intent);
    }

    private void openProductDetail(String id) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra("prod_id", id);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.relCart) {
            cartClicked();
        }
        else if (v == binding.searchVu) {
            searchVuClicked();
        }
        else if (v == binding.whatsappVu) {
            whatsappClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
    }

    private void cartClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        Intent intent = new Intent(getContext(), CartActivity.class);
        startActivity(intent);
    }

    private void searchVuClicked() {
        Intent intent = new Intent(getContext(), ShopSearchActivity.class);
        startActivity(intent);
    }

    private void notifClicked() {
//        startActivity(new Intent(getContext(), NotificationsActivity.class));
    }

    private void whatsappClicked() {
        String url = "https://api.whatsapp.com/send?phone=+971547215551";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void getHomeDataAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getShopHome(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrB = object.getJSONObject(Constants.kData).getJSONArray("banners");
                            JSONArray arrC = object.getJSONObject(Constants.kData).getJSONArray("categories");
                            JSONArray arrT = object.getJSONObject(Constants.kData).getJSONArray("trending");
                            JSONArray arrF = object.getJSONObject(Constants.kData).getJSONArray("flash_deals");
                            String count = object.getString("cart_items");
                            setCartBadge(count);
                            bannerList.clear();
                            categoryList.clear();
                            trendingList.clear();
                            dealsList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arrB.length(); i++) {
                                bannerList.add(gson.fromJson(arrB.get(i).toString(), ShopBanner.class));
                            }
                            for (int i = 0; i < arrC.length(); i++) {
                                categoryList.add(gson.fromJson(arrC.get(i).toString(), ProductCategory.class));
                            }
                            for (int i = 0; i < arrT.length(); i++) {
                                trendingList.add(gson.fromJson(arrT.get(i).toString(), Product.class));
                            }
                            for (int i = 0; i < arrF.length(); i++) {
                                dealsList.add(gson.fromJson(arrF.get(i).toString(), Product.class));
                            }
                            if (bannerList.size() > 0) {
                                binding.bannerCard.setVisibility(View.VISIBLE);
                                bannerAdapter.notifyDataSetChanged();
                            }
                            else {
                                binding.bannerCard.setVisibility(View.GONE);
                            }
                            binding.catVu.setVisibility(View.VISIBLE);
                            catAdapter.notifyDataSetChanged();
                            binding.trendingVu.setVisibility(View.VISIBLE);
                            trendingAdapter.notifyDataSetChanged();
                            if (dealsList.size() > 0) {
                                binding.flashDealsVu.setVisibility(View.VISIBLE);
                                flashDealsAdapter.notifyDataSetChanged();
                            }
                            else {
                                binding.flashDealsVu.setVisibility(View.GONE);
                            }
                            getDealsAPI(false);
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

    private void getDealsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getShopDeals(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrB = object.getJSONObject(Constants.kData).getJSONArray("brands");
                            JSONArray arrH = object.getJSONObject(Constants.kData).getJSONArray("hot_trending");
                            JSONArray arrP = object.getJSONObject(Constants.kData).getJSONArray("best_picks");
                            brandList.clear();
                            hotTrendingList.clear();
                            bestPickList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arrB.length(); i++) {
                                brandList.add(gson.fromJson(arrB.get(i).toString(), ProductBrand.class));
                            }
                            for (int i = 0; i < arrH.length(); i++) {
                                hotTrendingList.add(gson.fromJson(arrH.get(i).toString(), Product.class));
                            }
                            for (int i = 0; i < arrP.length(); i++) {
                                bestPickList.add(gson.fromJson(arrP.get(i).toString(), Product.class));
                            }
                            if (brandList.size() > 0) {
                                binding.brandsVu.setVisibility(View.VISIBLE);
                                productBrandAdapter.notifyDataSetChanged();
                            }
                            else {
                                binding.brandsVu.setVisibility(View.GONE);
                            }
                            if (hotTrendingList.size() > 0) {
                                binding.hotTrendVu.setVisibility(View.VISIBLE);
                                hotTrendingAdapter.notifyDataSetChanged();
                            }
                            else {
                                binding.hotTrendVu.setVisibility(View.GONE);
                            }
                            if (bestPickList.size() > 0) {
                                binding.bestPicksVu.setVisibility(View.VISIBLE);
                                bestPicksAdapter.notifyDataSetChanged();
                            }
                            else {
                                binding.bestPicksVu.setVisibility(View.GONE);
                            }
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

    private void addToWishlist(boolean isLoader, String prodId, int pos, RecyclerView recyclerView) {
        KProgressHUD hud = Functions.showLoader(getContext());
        addToWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    if (recyclerView == binding.trendingRecyclerVu) {
                        trendingList.get(pos).setIsFavorite("1");
                        trendingAdapter.notifyItemChanged(pos);
                    }
                    else {
                        bestPickList.get(pos).setIsFavorite("1");
                        bestPicksAdapter.notifyItemChanged(pos);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void removeFromWishlist(boolean isLoader, String prodId, int pos, RecyclerView recyclerView) {
        KProgressHUD hud = Functions.showLoader(getContext());
        removeFromWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    if (recyclerView == binding.trendingRecyclerVu) {
                        trendingList.get(pos).setIsFavorite("0");
                        trendingAdapter.notifyItemChanged(pos);
                    }
                    else {
                        bestPickList.get(pos).setIsFavorite("0");
                        bestPicksAdapter.notifyItemChanged(pos);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }
}