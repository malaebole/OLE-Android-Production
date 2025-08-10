package ae.oleapp.owner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPaymentSettingsBinding;
import ae.oleapp.dialogs.OleAddBankAccountDialogFragment;
import ae.oleapp.models.OlePaymentSetting;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePaymentSettingsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPaymentSettingsBinding binding;
    private String clubId = "";
    private OlePaymentSetting olePaymentSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPaymentSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.payment_settings);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        binding.accountVu.setVisibility(View.GONE);
        binding.btnAdd.setVisibility(View.GONE);

        getPaymentSettingsAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.accountVu.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == binding.cashSwitch) {
                if (binding.cashSwitch.isChecked()) {
                    setPaymentSettingsAPI(true, "1", "", "", "", "", "", "");
                }
                else {
                    setPaymentSettingsAPI(true, "0", "", "", "", "", "", "");
                }
            }
            else {
                if (binding.cardSwitch.isChecked()) {
                    setPaymentSettingsAPI(true, "", "1", "", "", "", "", "");
                }
                else {
                    setPaymentSettingsAPI(true, "", "0", "", "", "", "", "");
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.accountVu) {
            accountVuClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void accountVuClicked() {
        openDialog(true);
    }

    private void addClicked() {
        openDialog(false);
    }

    private void openDialog(boolean isUpdate) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("AddBankAccountDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleAddBankAccountDialogFragment dialogFragment = new OleAddBankAccountDialogFragment(olePaymentSetting, isUpdate);
        dialogFragment.setDialogCallback(new OleAddBankAccountDialogFragment.AddBankAccountDialogCallback() {
            @Override
            public void accountDetails(String bankName, String accountTitle, String ibanNumber, String accountNo, String branch) {
                setPaymentSettingsAPI(true, "", "", bankName, accountNo, accountTitle, ibanNumber, branch);
                binding.accountVu.setVisibility(View.VISIBLE);
                binding.btnAdd.setVisibility(View.GONE);
                binding.tvAccountTitle.setText(accountTitle);
                binding.tvAccountNo.setText(getString(R.string.account_no_place, accountNo));
                binding.tvBankName.setText(bankName);
                binding.tvBranch.setText(branch);
                binding.tvIbanNo.setText(getString(R.string.iban_no_place, ibanNumber));
            }
        });
        dialogFragment.show(fragmentTransaction, "AddBankAccountDialogFragment");
    }

    private void populateData() {
        if (olePaymentSetting.getCashPayment().equalsIgnoreCase("1")) {
            binding.cashSwitch.setChecked(true);
        }
        if (olePaymentSetting.getCardPayment().equalsIgnoreCase("1")) {
            binding.cardSwitch.setChecked(true);
        }
        if (olePaymentSetting.getBankName().isEmpty()) {
            binding.accountVu.setVisibility(View.GONE);
            binding.btnAdd.setVisibility(View.VISIBLE);
        }
        else {
            binding.accountVu.setVisibility(View.VISIBLE);
            binding.btnAdd.setVisibility(View.GONE);
            binding.tvAccountTitle.setText(olePaymentSetting.getAccountName());
            binding.tvAccountNo.setText(getString(R.string.account_no_place, olePaymentSetting.getAccountNumber()));
            binding.tvBankName.setText(olePaymentSetting.getBankName());
            binding.tvBranch.setText(olePaymentSetting.getBranchName());
            binding.tvIbanNo.setText(getString(R.string.iban_no_place, olePaymentSetting.getIbanNumber()));
        }
        binding.cashSwitch.setOnCheckedChangeListener(changeListener);
        binding.cardSwitch.setOnCheckedChangeListener(changeListener);
    }

    private void getPaymentSettingsAPI(boolean isLoader)  {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPaymentDetails(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
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
                            olePaymentSetting = gson.fromJson(obj.toString(), OlePaymentSetting.class);
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

    private void setPaymentSettingsAPI(boolean isLoader, String cash, String card, String bankName, String accountNo, String accountTitle, String iban, String branch)  {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePaymentSettings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, cash, card, bankName, accountTitle, iban, accountNo, branch);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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