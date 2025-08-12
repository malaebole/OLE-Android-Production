package ae.oleapp.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.scottyab.aescrypt.AESCrypt;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmpLoginBinding;
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

public class EmpLoginActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmpLoginBinding binding;
    private boolean isPassVisible = false;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmpLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        checkKeyboardListener();

        binding.btnLogin.setOnClickListener(this);
        binding.btnEye.setOnClickListener(this);
        binding.biometricVu.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);

        if (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS &&
                !Functions.getPrefValue(getContext(), Constants.kUserName).equalsIgnoreCase("")) {
            binding.biometricVu.setVisibility(View.VISIBLE);
            setupBiometric();
        }
        else {
            binding.biometricVu.setVisibility(View.INVISIBLE);
        }
    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    setHeight(0.8f);
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
                                    setHeight(0.4f);
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
            finish();
        }
        else if (v == binding.btnLogin) {
            btnLogin_clicked();
        }
        else if (v == binding.btnEye) {
            btnEye_clicked();
        }
        else if (v == binding.biometricVu) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void setupBiometric() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String username = Functions.getPrefValue(getContext(), Constants.kUserName);
                String encPass = Functions.getPrefValue(getContext(), Constants.kEncPass);
                try {
                    String pass = AESCrypt.decrypt(username, encPass);
                    loginApi(username, pass);
                }catch (GeneralSecurityException e){
                    //handle error - could be due to incorrect password or tampered encryptedMsg
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

    }

    private void btnLogin_clicked() {
        if (binding.etEmpId.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_employee_id), FancyToast.ERROR);
            return;
        }
        if (binding.etPass.getText().toString().length()<6) {
            Functions.showToast(getContext(), getString(R.string.invalid_pass), FancyToast.ERROR);
            return;
        }
        loginApi(binding.etEmpId.getText().toString(), binding.etPass.getText().toString());
    }

    private void btnEye_clicked() {
        if (isPassVisible) {
            binding.etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.btnEye.setImageResource(R.drawable.view_ic);
            isPassVisible = false;
        }
        else {
            binding.etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.btnEye.setImageResource(R.drawable.hide_ic);
            isPassVisible = true;
        }
    }

    private void loginApi(String username, String pass) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.login(Functions.getAppLang(getContext()), username, pass, "1");
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
                            UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kUserID, userInfo.getId());
                            editor.putString(Constants.kIsSignIn, "1");
                            editor.putString(Constants.kUserType, userInfo.getUserRole());
                            editor.putString(Constants.kCurrency, userInfo.getCurrency());
                            editor.putString(Constants.kUserName, username);
                            editor.putString(Constants.kIsEmp, "1");
                            SharedPreferences.Editor editor1 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor1.putString(Constants.kaccessToken, object.getString("access_token"));
                            editor1.apply();

                            try {
                                String encryptedPass = AESCrypt.encrypt(username, pass);
                                editor.putString(Constants.kEncPass, encryptedPass);
                            }catch (GeneralSecurityException e){
                                //handle error
                            }

                            editor.apply();
                            userInfo.setIsEmployee("1");
                            Functions.saveUserinfo(getContext(), userInfo);

                            String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                            if (!fcmToken.isEmpty()) {
                                sendFcmTokenApi(fcmToken);
                            }

                            if (userInfo.getUserRole().equalsIgnoreCase(Constants.kOwnerType)) {
                                Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                                startActivity(intent);
                                finish();
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
}