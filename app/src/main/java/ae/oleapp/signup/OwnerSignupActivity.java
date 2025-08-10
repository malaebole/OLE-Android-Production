package ae.oleapp.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.activities.OleWebVuActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityOwnerSignupBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerSignupActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityOwnerSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityOwnerSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        checkKeyboardListener();

        binding.btnBack.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);
        binding.tvTerms.setOnClickListener(this);

    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    setHeight(0.88f);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //your code here
                                    setHeight(0.5f);
                                }
                            }, 50);
                        }
                    });
                }
            }
        });
    }

    private void setHeight(float height) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.bottomContainer.getLayoutParams();
        lp.matchConstraintPercentHeight = height;
        binding.bottomContainer.setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            backClicked();
        }
        else if (v == binding.btnSignup) {
            signupClicked();
        }
        else if (v == binding.tvTerms) {
            termsClicked();
        }
    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }

    private void signupClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (Functions.isArabic(binding.etName.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.enter_name_english), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
        signUpApi(binding.etName.getText().toString(), binding.etEmail.getText().toString(), Constants.kOwnerType);
    }

    private void termsClicked() {
        Intent intent = new Intent(getContext(), OleWebVuActivity.class);
        startActivity(intent);
    }

    private void signUpApi(String fullName, String email, String type) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.register(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),  fullName, email, type, "android"); // Functions.getPrefValue(getContext(), Constants.kUserID),
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            UserInfo userInfo = new Gson().fromJson(obj.toString(), UserInfo.class);
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kUserID, userInfo.getId());
                            editor.putString(Constants.kIsSignIn, "1");
                            editor.putString(Constants.kUserType, userInfo.getUserRole());
                            editor.putString(Constants.kCurrency, userInfo.getCurrency());
                            editor.apply();

                            Functions.saveUserinfo(getContext(), userInfo);

                            String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                            if (!fcmToken.isEmpty()) {
                                sendFcmTokenApi(fcmToken);
                            }

                            Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else if (object.getInt(Constants.kStatus) == 423) {
                            // when field owner account is not activiated by admin
                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    Intent intent = new Intent(getContext(), IntroSliderActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
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
