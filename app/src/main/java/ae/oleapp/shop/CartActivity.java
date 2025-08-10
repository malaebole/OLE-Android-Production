package ae.oleapp.shop;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleCartListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityCartBinding;
import ae.oleapp.models.Cart;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCartBinding binding;
    private OleCartListAdapter oleCartListAdapter;
    private final List<Cart> cartList = new ArrayList<>();
    private double totalPrice = 0.0;
    private String currency = "";
    private boolean isStockAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.cart);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        oleCartListAdapter = new OleCartListAdapter(this, cartList);
        oleCartListAdapter.setItemClickListener(itemClicked);
        binding.recyclerVu.setAdapter(oleCartListAdapter);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCartAPI(false);
            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnCheckout.setOnClickListener(this);
        binding.btnShop.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartAPI(cartList.isEmpty());
    }

    OleCartListAdapter.ItemClickListener itemClicked = new OleCartListAdapter.ItemClickListener() {
        @Override
        public void deleteClicked(View view, int pos) {
            Cart cart = cartList.get(pos);
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.delete))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                deleteCartAPI(true, cart.getProductId(), cart.getId(), pos);
                            }
                        }
                    }).show();
        }

        @Override
        public void plusClicked(View view, int pos) {
            Cart cart = cartList.get(pos);
            int qty = Integer.parseInt(cart.getQuantity());
            int stock = Integer.parseInt(cart.getCurrentStock());
            if (qty < stock) {
                qty += 1;
                updateCartAPI(true, cart.getProductId(), cart.getId(), qty, pos);
            }
        }

        @Override
        public void minusClicked(View view, int pos) {
            Cart cart = cartList.get(pos);
            int qty = Integer.parseInt(cart.getQuantity());
            if (qty > 1) {
                qty -= 1;
                updateCartAPI(true, cart.getProductId(), cart.getId(), qty, pos);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn || v == binding.btnShop) {
            finish();
        } else if (v == binding.btnCheckout) {
            checkoutClicked();
        }
    }

    private void checkoutClicked() {
        if (cartList.isEmpty()) {
            return;
        }
        if (isStockAvailable) {
            Intent intent = new Intent(this, AddressListActivity.class);
            intent.putExtra("price", totalPrice);
            intent.putExtra("currency", currency);
            startActivity(intent);
        }
        else {
            Functions.showToast(getContext(), getString(R.string.out_of_stock), FancyToast.ERROR);
        }
    }

    private void calculatePrice() {
        totalPrice = 0;
        for (Cart cart : cartList) {
            currency = cart.getCurrency();
            double price = Double.parseDouble(cart.getPrice());
            totalPrice = totalPrice + price;
        }
        if (currency.equalsIgnoreCase("")) {
            currency = getString(R.string.aed);
        }
        binding.tvCheckout.setText(getString(R.string.checkout_place, totalPrice, currency));
        checkStock();
    }

    private void checkStock() {
        isStockAvailable = true;
        for (Cart cart : cartList) {
            int qty = Integer.parseInt(cart.getQuantity());
            int stock = Integer.parseInt(cart.getCurrentStock());
            if (qty > stock) {
                isStockAvailable = false;
                break;
            }
        }
    }

    private void getCartAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getCart(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
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
                            Gson gson = new Gson();
                            cartList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                cartList.add(gson.fromJson(arr.get(i).toString(), Cart.class));
                            }
                            if (cartList.size() > 0) {
                                binding.pullRefresh.setVisibility(View.VISIBLE);
                                binding.btnCheckout.setVisibility(View.VISIBLE);
                                binding.emptyCartVu.setVisibility(View.GONE);
                                oleCartListAdapter.notifyDataSetChanged();
                                calculatePrice();
                            }
                            else {
                                binding.pullRefresh.setVisibility(View.GONE);
                                binding.btnCheckout.setVisibility(View.GONE);
                                binding.emptyCartVu.setVisibility(View.VISIBLE);
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

    private void deleteCartAPI(boolean isLoader, String prodId, String cartId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteCart(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), prodId, cartId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            cartList.remove(pos);
                            oleCartListAdapter.notifyItemRemoved(pos);
                            oleCartListAdapter.notifyItemRangeRemoved(pos, cartList.size());
                            calculatePrice();
                            if (cartList.size() == 0) {
                                binding.pullRefresh.setVisibility(View.GONE);
                                binding.btnCheckout.setVisibility(View.GONE);
                                binding.emptyCartVu.setVisibility(View.VISIBLE);
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

    private void updateCartAPI(boolean isLoader, String prodId, String cartId, int qty, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateCart(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), prodId, cartId, qty);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            cartList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                cartList.add(gson.fromJson(arr.get(i).toString(), Cart.class));
                            }
                            oleCartListAdapter.notifyDataSetChanged();
                            calculatePrice();
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