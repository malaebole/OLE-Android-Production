package ae.oleapp.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleCheckoutAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityShopOrderDetailBinding;
import ae.oleapp.models.Order;
import ae.oleapp.models.Product;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopOrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityShopOrderDetailBinding binding;
    private String orderId = "";
    private boolean isFromCheckout = false;
    private final List<Object> productList = new ArrayList<>();
    private Order orderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.tracking);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getString("order_id", "");
            isFromCheckout = bundle.getBoolean("is_from_checkout", false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        binding.scrollVu.setVisibility(View.INVISIBLE);
        getOrderAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnReview.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            onBackPressed();
        }
        else if (v == binding.btnReview) {
            reviewClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromCheckout) {
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
        else {
            finish();
        }
    }

    private void reviewClicked() {
        if (orderDetail != null) {
            Intent intent = new Intent(getContext(), OrdersProductReviewActivity.class);
            Gson gson = new Gson();
            String str = gson.toJson(orderDetail.getProducts());
            intent.putExtra("products", str);
            startActivity(intent);
        }
    }

    private void cancelClicked() {
        if (orderDetail != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.cancel_order))
                    .setMessage(getResources().getString(R.string.do_you_want_to_cancel_order))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelOrderAPI(true);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }

    private void populateData() {
        if (orderDetail == null) {
            return;
        }
        binding.scrollVu.setVisibility(View.VISIBLE);
        binding.tvOrderId.setText(getString(R.string.order_no_place, orderDetail.getOrderNumber()));
        binding.tvDate.setText(getString(R.string.placed_on_place, orderDetail.getOrderDate()));
        binding.btnCancel.setVisibility(View.GONE);
        if (orderDetail.getDeliveryStatus().equalsIgnoreCase("pending")) {
            binding.placeVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.deliveredVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.btnCancel.setVisibility(View.VISIBLE);
        }
        else if (orderDetail.getDeliveryStatus().equalsIgnoreCase("confirmed")) {
            binding.placeVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.deliveredVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.btnCancel.setVisibility(View.VISIBLE);
        }
        else if (orderDetail.getDeliveryStatus().equalsIgnoreCase("shipped")) {
            binding.placeVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.deliveredVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        }
        else if (orderDetail.getDeliveryStatus().equalsIgnoreCase("delivered")) {
            binding.placeVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.deliveredVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
        }
        else if (orderDetail.getDeliveryStatus().equalsIgnoreCase("canceled")) {
            binding.placeVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.greenColor));
            binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.deliveredVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            if (orderDetail.getIsConfirmed().equalsIgnoreCase("1")) {
                binding.tvShipped.setText(R.string.cancelled);
                binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.redColor));
                binding.tvDelivered.setVisibility(View.GONE);
                binding.deliveredVu.setVisibility(View.GONE);
            }
            else {
                binding.tvConfirmed.setText(R.string.cancelled);
                binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.redColor));
                binding.tvShipped.setVisibility(View.GONE);
                binding.shippedVu.setVisibility(View.GONE);
                binding.tvDelivered.setVisibility(View.GONE);
                binding.deliveredVu.setVisibility(View.GONE);
            }
        }
        else {
            binding.placeVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.confirmedVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.shippedVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.deliveredVu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        }

        if (orderDetail.getPickupAddress().equalsIgnoreCase("1")) {
            binding.tvPickup.setText(R.string.collection_point);
        }
        else {
            binding.tvPickup.setText(R.string.delivery_to);
        }
        binding.tvName.setText(orderDetail.getUserAddress().getName());
        binding.tvPhone.setText(orderDetail.getUserAddress().getPhone());
        binding.tvAddress.setText(String.format("%s - %s - %s", orderDetail.getUserAddress().getAddress(), orderDetail.getUserAddress().getArea(), orderDetail.getUserAddress().getCity()));
        if (orderDetail.getUserAddress().getIsHome().equalsIgnoreCase("1")) {
            binding.tvFlatNo.setText(getString(R.string.flat_no_place, orderDetail.getUserAddress().getHouseNo()));
        }
        else {
            binding.tvFlatNo.setText(getString(R.string.office_no_place, orderDetail.getUserAddress().getOfficeNo()));
        }

        if (orderDetail.getPaymentType().equalsIgnoreCase("cash")) {
            binding.tvPaymentMethod.setText(R.string.cash);
        }
        else if (orderDetail.getPaymentType().equalsIgnoreCase("card")) {
            binding.tvPaymentMethod.setText(orderDetail.getCardNumber());
        }
        else if (orderDetail.getPaymentType().equalsIgnoreCase("samsung")) {
            binding.tvPaymentMethod.setText(R.string.samsung_pay);
        }
        else if (orderDetail.getPaymentType().equalsIgnoreCase("apple")) {
            binding.tvPaymentMethod.setText(R.string.apple_pay);
        }
        else if (orderDetail.getPaymentType().equalsIgnoreCase("wallet")) {
            binding.tvPaymentMethod.setText(R.string.wallet);
        }
        else {
            binding.tvPaymentMethod.setText("");
        }

        productList.clear();
        productList.addAll(orderDetail.getProducts());
        OleCheckoutAdapter adapter = new OleCheckoutAdapter(getContext(), productList);
        binding.recyclerVu.setAdapter(adapter);

        double totalPrice = 0;
        for (Object obj : productList) {
            Product product = (Product) obj;
            double price = Double.parseDouble(product.getSalePrice());
            totalPrice = totalPrice + price;
        }
        binding.tvItemTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", totalPrice, orderDetail.getCurrency()));
        if (!orderDetail.getShippingCost().equalsIgnoreCase("")) {
            binding.tvShippingFee.setText(String.format("%s %s", orderDetail.getShippingCost(), orderDetail.getCurrency()));
        }
        else {
            binding.tvShippingFee.setText(R.string.free);
        }
        if (Double.parseDouble(orderDetail.getCod()) > 0) {
            binding.codFeeVu.setVisibility(View.VISIBLE);
            binding.tvCodFee.setText(String.format("%s %s", orderDetail.getCod(), orderDetail.getCurrency()));
        }
        else {
            binding.codFeeVu.setVisibility(View.GONE);
        }

        if (Double.parseDouble(orderDetail.getCouponDiscount()) > 0) {
            binding.discountVu.setVisibility(View.VISIBLE);
            binding.tvCouponDiscount.setText(String.format("-%s %s", orderDetail.getCouponDiscount(), orderDetail.getCurrency()));
        }
        else {
            binding.discountVu.setVisibility(View.GONE);
        }
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%s %s", orderDetail.getGrandTotal(), orderDetail.getCurrency()));

        if (orderDetail.getDeliveryStatus().equalsIgnoreCase("delivered") && orderDetail.getIsReviewed().equalsIgnoreCase("0")) {
            binding.btnReview.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnReview.setVisibility(View.GONE);
        }
    }

    private void getOrderAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOrderDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), orderId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            orderDetail = gson.fromJson(obj.toString(), Order.class);
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

    private void cancelOrderAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelOrder(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), orderId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (orderDetail.getDeliveryStatus().equalsIgnoreCase("confirmed")) {
                                orderDetail.setIsConfirmed("1");
                            }
                            else {
                                orderDetail.setIsConfirmed("0");
                            }
                            orderDetail.setDeliveryStatus("canceled");
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