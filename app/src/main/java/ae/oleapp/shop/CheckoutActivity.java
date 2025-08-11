package ae.oleapp.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleCheckoutAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityCheckoutBinding;
import ae.oleapp.dialogs.OleOrderDoneDialogFragment;
import ae.oleapp.models.Cart;
import ae.oleapp.models.OleShopAddress;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends BaseActivity implements View.OnClickListener {

    private double walletAmount = 0;
    private final int CASH = 1;
    private final int CARD = 2;
    private final int WALLET = 3;
    private final int SPAY = 4;
    private int paymentType = CASH;
    private double couponDiscount = 0;
    private boolean isCouponApplied = false;
    private double totalPrice = 0;
    private double shippingFee = 0;
    private double codFee = 0;
    private String currency = "";
    private OleShopAddress oleShopAddress;
    private final List<Object> cartList = new ArrayList<>();
    private ActivityCheckoutBinding binding;
    private boolean isPickup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.checkout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            oleShopAddress = gson.fromJson(bundle.getString("address", ""), OleShopAddress.class);
            isPickup = bundle.getBoolean("is_pickup", false);
        }

        binding.btnDel.setVisibility(View.GONE);
        binding.tvCouponDis.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        binding.tvName.setText(oleShopAddress.getName());
        binding.tvPhone.setText(oleShopAddress.getPhone());
        binding.tvAddress.setText(String.format("%s - %s - %s", oleShopAddress.getAddress(), oleShopAddress.getArea(), oleShopAddress.getCity()));
        if (oleShopAddress.getIsHome().equalsIgnoreCase("1")) {
            binding.tvFlatNo.setText(getString(R.string.flat_no_place, oleShopAddress.getHouseNo()));
        }
        else {
            binding.tvFlatNo.setText(getString(R.string.office_no_place, oleShopAddress.getOfficeNo()));
        }

        cashClicked();

        getWalletDataAPI("", new WalletDataCallback() {
            @Override
            public void getWalletData(String amount, String paymentMethod, String currency, String shopPaymentMethod) {
                if (!amount.isEmpty()) {
                    walletAmount = Double.parseDouble(amount);
                    binding.tvCredit.setText(String.format("%s %s", amount, currency));
                }
                else {
                    binding.tvCredit.setText(String.format("0 %s", currency));
                }
                if (!shopPaymentMethod.isEmpty()) {
                    setPaymentMethod(shopPaymentMethod);
                }
            }
        });

        String code = Functions.getPrefValue(getContext(), Constants.kShopCoupon);
        if (!code.equalsIgnoreCase("")) {
            binding.etCoupon.setText(code);
            btnApplyClicked();
        }

        if (isPickup) {
            binding.tvShippingAddress.setText(R.string.collection_point);
        }
        else {
            binding.tvShippingAddress.setText(R.string.shipping_address);
        }

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);
        binding.relCard.setOnClickListener(this);
        binding.relSpay.setOnClickListener(this);
        binding.relCash.setOnClickListener(this);
        binding.relWallet.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
    }

    private void setPaymentMethod(String method) {
        if (method.equalsIgnoreCase("cash")) {
            binding.relWallet.setVisibility(View.GONE);
            binding.relCard.setVisibility(View.GONE);
            binding.relSpay.setVisibility(View.GONE);
            cashClicked();
        }
        if (method.equalsIgnoreCase("card")) {
            binding.relCash.setVisibility(View.GONE);
            cardClicked();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartAPI(cartList.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnApply) {
            btnApplyClicked();
        }
        else if (v == binding.btnDel) {
            btnDelClicked();
        }
        else if (v == binding.relCard) {
            cardClicked();
        }
        else if (v == binding.relSpay) {
            spayClicked();
        }
        else if (v == binding.relCash) {
            cashClicked();
        }
        else if (v == binding.relWallet) {
            walletClicked();
        }
        else if (v == binding.btnPay) {
            payClicked();
        }
    }

    private void btnApplyClicked() {
        if (binding.etCoupon.getText().toString().isEmpty()) {
            return;
        }
        getCouponDiscountAPI(true, binding.etCoupon.getText().toString());
    }

    private void applyDiscount() {
        binding.btnApply.setVisibility(View.GONE);
        binding.btnDel.setVisibility(View.VISIBLE);
        binding.etCoupon.setEnabled(false);
        binding.tvCouponDis.setVisibility(View.VISIBLE);
        isCouponApplied = true;

        binding.tvCouponDis.setText(getString(R.string.coupon_place, couponDiscount, currency));
        binding.tvCouponDiscount.setText(String.format(Locale.ENGLISH, "-%.2f %s", couponDiscount, currency));
        double price = totalPrice - couponDiscount + shippingFee;
        if (paymentType == CASH) {
            price = price + codFee;
        }
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
    }

    private void btnDelClicked() {
        binding.btnApply.setVisibility(View.VISIBLE);
        binding.btnDel.setVisibility(View.GONE);
        binding.etCoupon.setEnabled(true);
        binding.etCoupon.setText("");
        couponDiscount = 0;
        isCouponApplied = false;
        binding.tvCouponDis.setVisibility(View.GONE);
        binding.tvCouponDiscount.setText(String.format("0 %s", currency));
        double price = totalPrice + shippingFee;
        if (paymentType == CASH) {
            price = price + codFee;
        }
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.kShopCoupon);
        editor.apply();
    }

    private void cardClicked() {
        paymentType = CARD;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.check);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.codFeeVu.setVisibility(View.GONE);
        double price = totalPrice - couponDiscount + shippingFee;
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
    }

    private void spayClicked() {
        paymentType = SPAY;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.check);
        binding.codFeeVu.setVisibility(View.GONE);
        double price = totalPrice - couponDiscount + shippingFee;
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
    }

    private void cashClicked() {
        paymentType = CASH;
        binding.imgVuCash.setImageResource(R.drawable.check);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.codFeeVu.setVisibility(View.VISIBLE);
        double price = totalPrice - couponDiscount + shippingFee + codFee;
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
    }

    private void walletClicked() {
        paymentType = WALLET;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.check);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.codFeeVu.setVisibility(View.GONE);
        double price = totalPrice - couponDiscount + shippingFee;
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
    }

    private void payClicked() {
        String code = "";
        if (isCouponApplied) {
            code = binding.etCoupon.getText().toString();
        }
        double finalPrice = totalPrice + shippingFee - couponDiscount;
        if (finalPrice > 0) {
            if (paymentType == CASH) {
                placeOrderAPI(true, totalPrice - couponDiscount, shippingFee, codFee, "cash", "", code, couponDiscount, "", "");
            }
            else if (paymentType == CARD) {
                String finalCode = code;
                makePayment("", "card", String.valueOf(finalPrice), currency, new BaseActivity.PaymentCallback() {
                    @Override
                    public void paymentStatus(boolean status, String orderRef) {
                        placeOrderAPI(true, totalPrice - couponDiscount, shippingFee, 0, "card", orderRef, finalCode, couponDiscount, "", "");
                    }
                });
            }
            else if (paymentType == WALLET) {
                if (walletAmount < finalPrice) {
                    halfWalletHalfCard(finalPrice, code);
                }
                else {
                    placeOrderAPI(true, totalPrice - couponDiscount, shippingFee, 0, "wallet", "", code, couponDiscount, "", "");
                }
            }
            else if (paymentType == SPAY) {
                String finalCode = code;
                makePayment("", "samsung", String.valueOf(finalPrice), currency, new BaseActivity.PaymentCallback() {
                    @Override
                    public void paymentStatus(boolean status, String orderRef) {
                        placeOrderAPI(true, totalPrice - couponDiscount, shippingFee, 0, "samsung", orderRef, finalCode, couponDiscount, "", "");
                    }
                });
            }
        }
    }

    private void halfWalletHalfCard(double finalPrice, String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.payment))
                .setMessage(getResources().getString(R.string.insufficient_balance_wallet_remaining_amount_card))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double remainAmount = finalPrice - walletAmount;
                        makePayment("", "card", String.valueOf(remainAmount), currency, new BaseActivity.PaymentCallback() {
                            @Override
                            public void paymentStatus(boolean status, String orderRef) {
                                placeOrderAPI(true, totalPrice - couponDiscount, shippingFee, 0, "card", orderRef, code, couponDiscount, String.valueOf(walletAmount), String.valueOf(remainAmount));
                            }
                        });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void calculatePrice(String fee) {
        totalPrice = 0;
        if (!fee.equalsIgnoreCase("")) {
            shippingFee = Double.parseDouble(fee);
        }
        for (Object obj : cartList) {
            Cart cart = (Cart) obj;
            double price = Double.parseDouble(cart.getPrice());
            totalPrice = totalPrice + price;
        }
        binding.tvItemTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", totalPrice, currency));
        binding.tvCouponDiscount.setText(String.format(Locale.ENGLISH, "0 %s", currency));
        double price = 0;
        if (shippingFee > 0) {
            binding.tvShippingFee.setText(String.format(Locale.ENGLISH, "%.1f %s", shippingFee, currency));
            price = totalPrice + shippingFee;
        }
        else {
            binding.tvShippingFee.setText(R.string.free);
            price = totalPrice;
        }
        if (paymentType == CASH) {
            price = price + codFee;
        }
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", price, currency));
        binding.tvBtnTitle.setText(getString(R.string.pay_now_place, price, currency));
    }

    private void getCartAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getCart(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
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
                            OleCheckoutAdapter adapter = new OleCheckoutAdapter(getContext(), cartList);
                            binding.recyclerVu.setAdapter(adapter);
                            getShippingFeeAPI(false);
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

    private void getShippingFeeAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getShippingFee(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), oleShopAddress.getCityId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String shipFee = object.getJSONObject(Constants.kData).getString("amount");
                            currency = object.getJSONObject(Constants.kData).getString("currency");
                            String value = object.getJSONObject(Constants.kData).getString("cod");
                            if (!value.equalsIgnoreCase("")) {
                                codFee = Double.parseDouble(value);
                            }
                            binding.tvCodFee.setText(String.format(Locale.ENGLISH, "%.1f %s", codFee, currency));
                            if (isPickup) {
                                calculatePrice("");
                            }
                            else {
                                calculatePrice(shipFee);
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

    private void getCouponDiscountAPI(boolean isLoader, String code) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getCouponDis(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), code);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            couponDiscount = object.getJSONObject(Constants.kData).getDouble("discount");
                            SharedPreferences preferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.kShopCoupon, code);
                            editor.apply();
                            applyDiscount();
                        }
                        else {
                            binding.tvCouponDis.setVisibility(View.VISIBLE);
                            binding.tvCouponDis.setText(object.getString(Constants.kMsg));
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

    private void placeOrderAPI(boolean isLoader, double price, double shipFee, double cod, String paymentMethod, String orderRef, String couponCode, double couponDiscount, String walletPaid, String cardPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.placeOrder(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), oleShopAddress.getId(), price, shipFee, cod, paymentMethod, orderRef, couponCode, couponDiscount, walletPaid, cardPaid, Functions.getIPAddress());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            showPopup(object.getJSONObject(Constants.kData).getString("order_id"));
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

    private void showPopup(String orderId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prevFragment = getSupportFragmentManager().findFragmentByTag("OrderDoneDialogFragment");
        if (prevFragment != null) {
            fragmentTransaction.remove(prevFragment);
        }
        fragmentTransaction.addToBackStack(null);
        OleOrderDoneDialogFragment dialogFragment = new OleOrderDoneDialogFragment(orderId);
        dialogFragment.setDialogCallback(new OleOrderDoneDialogFragment.OrderDoneDialogCallback() {
            @Override
            public void orderDone(DialogFragment dialogFragment, boolean isTrack) {
                dialogFragment.dismiss();
                if (isTrack) {
                    Intent intent = new Intent(getContext(), ShopOrderDetailActivity.class);
                    intent.putExtra("order_id", orderId);
                    intent.putExtra("is_from_checkout", true);
                    startActivity(intent);
                }
                else {
                    if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "OrderDoneDialogFragment");
    }
}