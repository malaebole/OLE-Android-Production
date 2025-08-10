package ae.oleapp.activities;

import android.os.Bundle;
import android.view.View;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;
import java.net.UnknownHostException;
import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityContactUsBinding;
import ae.oleapp.models.OleContactDetail;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleContactUsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.contact_us);

        getContactDetailsAPI(true);
        binding.bar.backBtn.setOnClickListener(this);
        binding.relPhone.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.relPhone) {
            phoneClicked();
        }
        else if (v == binding.btnSubmit) {
            submitClicked();
        }
    }

    private void phoneClicked() {
        if (!binding.tvPhone.getText().toString().isEmpty()) {
            makeCall(binding.tvPhone.getText().toString());
        }
    }

    private void submitClicked() {
        if (binding.etFullname.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.enter_full_name), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getResources().getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        if (binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.write_msg), FancyToast.ERROR);
            return;
        }

        postContactAPI(true, binding.etFullname.getText().toString(), binding.etPhone.getText().toString(), binding.etEmail.getText().toString(), binding.etMsg.getText().toString());
    }

    private void getContactDetailsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getContactDetails(Functions.getAppLang(getContext()));
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
                            OleContactDetail detail = gson.fromJson(obj.toString(), OleContactDetail.class);
                            binding.tvEmail.setText(detail.getEmail());
                            binding.tvPhone.setText(detail.getPhone());
                            binding.tvLoc.setText(detail.getAddress());
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

    private void postContactAPI(boolean isLoader, String name, String phone, String email, String msg) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addContactUs(Functions.getAppLang(getContext()), name, email, phone, msg);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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
