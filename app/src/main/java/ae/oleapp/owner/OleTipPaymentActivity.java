package ae.oleapp.owner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import ae.oleapp.databinding.OleactivityTipPaymentBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.dialogs.OlePayTipDialogFragment;
import ae.oleapp.models.OleTipPayment;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleTipPaymentActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityTipPaymentBinding binding;
    private String fromDate = "", toDate = "", empId = "", payableTip = "0", currency = "";
    private OleTipPaymentAdapter adapter;
    private final List<OleTipPayment> paymentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityTipPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empId = bundle.getString("emp_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleTipPaymentAdapter(getContext(), paymentList);
        binding.recyclerVu.setAdapter(adapter);

        binding.tvDate.setVisibility(View.GONE);

        binding.backBtn.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
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
        else if (v == binding.btnPay) {
            if (Double.parseDouble(payableTip) > 0) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("PayTipDialogFragment");
                if (fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
                fragmentTransaction.addToBackStack(null);
                OlePayTipDialogFragment dialogFragment = new OlePayTipDialogFragment(payableTip);
                dialogFragment.setFragmentCallback(new OlePayTipDialogFragment.PayTipDialogFragmentCallback() {
                    @Override
                    public void submitClicked(DialogFragment df, String amount) {
                        df.dismiss();
                        payTipPaymentsAPI(true, amount);
                    }
                });
                dialogFragment.show(fragmentTransaction, "PayTipDialogFragment");
            }
        }
    }

    private void getTipPaymentsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployeeTipPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), empId, fromDate, toDate);
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
                            payableTip = object.getString("payable_tip");
                            currency = object.getString("currency");
                            binding.tvPayableTip.setText(String.format("%s %s", payableTip, currency));
                        }
                        else {
                            paymentList.clear();
                            payableTip = "0";
                            binding.tvPayableTip.setText("0 AED");
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

    private void payTipPaymentsAPI(boolean isLoader, String amount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.employeeTipPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), empId, amount);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            getTipPaymentsAPI(false);
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