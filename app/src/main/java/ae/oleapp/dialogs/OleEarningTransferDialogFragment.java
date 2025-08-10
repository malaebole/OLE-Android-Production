package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;
import java.net.UnknownHostException;
import ae.oleapp.R;

import ae.oleapp.databinding.OlefragmentEarningTransferDialogBinding;
import ae.oleapp.models.OleEarningTransfer;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEarningTransferDialogFragment extends DialogFragment {

    private OlefragmentEarningTransferDialogBinding binding;
    private OleEarningTransfer earning;

    public OleEarningTransferDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentEarningTransferDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.imgVu.setVisibility(View.GONE);
        getEarningsDetailAPI(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateData() {
        binding.tvPaymentDate.setText(earning.getFromToDates());
        binding.tvTotalBookings.setText(earning.getTotalBookings());
        binding.tvTotalAmount.setText(String.format("%s %s", earning.getAmount(), earning.getCurrency()));
        binding.tvCharges.setText(String.format("-%s %s", earning.getCharges(), earning.getCurrency()));
        binding.tvName.setText(earning.getAccountName());
        binding.tvBankName.setText(earning.getBankName());
        binding.tvAccountNo.setText(getString(R.string.account_no_place, earning.getAccountNumber()));
        binding.tvBranch.setText(getString(R.string.branch_place, earning.getBranchName()));
        binding.tvDateTime.setText(earning.getTransactionDate());
        if (!earning.getInvoiceSlip().isEmpty()) {
            binding.imgVu.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(earning.getInvoiceSlip()).into(binding.imgVu);
        }
        else {
            binding.imgVu.setVisibility(View.GONE);
        }
    }

    private void getEarningsDetailAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEarningsDetails(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), "", "bank_transfer");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            earning = gson.fromJson(obj.toString(), OleEarningTransfer.class);
                            populateData();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
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