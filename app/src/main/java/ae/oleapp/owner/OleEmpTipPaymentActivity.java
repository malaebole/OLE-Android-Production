package ae.oleapp.owner;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import ae.oleapp.adapters.OleTipPaymentAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmpTipPaymentBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleTipPayment;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmpTipPaymentActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmpTipPaymentBinding binding;
    private OleTipPaymentAdapter adapter;
    private final List<OleTipPayment> paymentList = new ArrayList<>();
    private String fromDate = "", toDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmpTipPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleTipPaymentAdapter(getContext(), paymentList);
        binding.recyclerVu.setAdapter(adapter);

        binding.tvDate.setVisibility(View.GONE);

        binding.backBtn.setOnClickListener(this);
        binding.btnCalendar.setOnClickListener(this);

        getTipPaymentsAPI(true);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnCalendar) {
            showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
                @Override
                public void filterData(DialogFragment df, String from, String to) {
                    df.dismiss();
                    fromDate = from;
                    toDate = to;
                    binding.tvDate.setVisibility(View.VISIBLE);
                    if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                        binding.tvDate.setText(String.format("%s %s %s", fromDate, getString(R.string.to), toDate));
                    }
                    else if (!fromDate.isEmpty()) {
                        binding.tvDate.setText(fromDate);
                    }
                    else if (!toDate.isEmpty()) {
                        binding.tvDate.setText(toDate);
                    }
                    else {
                        binding.tvDate.setText("");
                        binding.tvDate.setVisibility(View.GONE);
                    }
                    getTipPaymentsAPI(true);
                }
            });
        }
    }

    private void getTipPaymentsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployeeTipPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kUserID), fromDate, toDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            paymentList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                paymentList.add(gson.fromJson(arr.get(i).toString(), OleTipPayment.class));
                            }
                            binding.tvReceivedTip.setText(String.format("%s %s", object.getString("paid_total"), object.getString("currency")));
                        }
                        else {
                            paymentList.clear();
                            binding.tvReceivedTip.setText("0 AED");
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