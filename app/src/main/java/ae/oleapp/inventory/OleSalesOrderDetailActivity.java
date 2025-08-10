package ae.oleapp.inventory;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
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
import ae.oleapp.activities.OleFullImageActivity;
import ae.oleapp.adapters.OleInventoryCheckoutAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivitySalesOrderDetailBinding;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.models.OleInventoryOrder;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleSalesOrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivitySalesOrderDetailBinding binding;
    private String orderId = "";
    private OleInventoryOrder orderDetail;
    private final List<OleInventoryItem> itemList = new ArrayList<>();
    private OleInventoryCheckoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivitySalesOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getString("order_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleInventoryCheckoutAdapter(getContext(), itemList, true);
        binding.recyclerVu.setAdapter(adapter);

        binding.scrollVu.setVisibility(View.INVISIBLE);
        getOrderDetailAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.imgVuReceipt) {
            if (orderDetail != null) {
                Intent intent = new Intent(getContext(), OleFullImageActivity.class);
                intent.putExtra("URL", orderDetail.getReceiptPhoto());
                startActivity(intent);
            }
        }
    }

    private void populateData() {
        binding.scrollVu.setVisibility(View.VISIBLE);
        binding.tvOrderNo.setText(getString(R.string.order_no_place, orderDetail.getCode()));
        binding.tvDate.setText(orderDetail.getAddedDate());
        binding.tvAddedBy.setText(String.format("%s: %s", getString(R.string.sold_by), orderDetail.getSoldBy()));
        if (orderDetail.getEmployeeName().isEmpty()) {
            binding.tvEmp.setVisibility(View.GONE);
        }
        else {
            binding.tvEmp.setVisibility(View.VISIBLE);
            binding.tvEmp.setText(String.format("%s: %s", getString(R.string.employee), orderDetail.getEmployeeName()));
        }
        itemList.clear();
        itemList.addAll(orderDetail.getOrderItems());
        adapter.notifyDataSetChanged();

        binding.tvItemTotal.setText(String.format("%s %s", orderDetail.getSubTotal(), orderDetail.getCurrency()));
        binding.tvDiscount.setText(String.format("%s %s", orderDetail.getDiscount(), orderDetail.getCurrency()));
        binding.tvGrandTotal.setText(String.format("%s %s", orderDetail.getGrandTotal(), orderDetail.getCurrency()));

        if (orderDetail.getReceiptPhoto().isEmpty()) {
            binding.imgVuReceipt.setVisibility(View.GONE);
        }
        else {
            binding.imgVuReceipt.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(orderDetail.getReceiptPhoto()).into(binding.imgVuReceipt);
        }
        if (orderDetail.getNote().isEmpty()) {
            binding.tvNote.setVisibility(View.GONE);
            binding.tvNoteTitle.setVisibility(View.GONE);
        }
        else {
            binding.tvNote.setVisibility(View.VISIBLE);
            binding.tvNoteTitle.setVisibility(View.VISIBLE);
            binding.tvNote.setText(orderDetail.getNote());
        }
    }

    private void getOrderDetailAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getInventoryOrderDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), orderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            orderDetail = new Gson().fromJson(obj.toString(), OleInventoryOrder.class);
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