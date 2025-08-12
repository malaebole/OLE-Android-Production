package ae.oleapp.signup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.MyApp;
import ae.oleapp.R;
import ae.oleapp.activities.MainActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityVerifyPhoneBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityVerifyPhoneBinding binding;
    private boolean isForUpdate = false;
    String userModule = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();
        binding.tvPhone.setText("");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForUpdate = bundle.getBoolean("is_update", false);
            String phone = bundle.getString("phone", "");
            binding.tvPhone.setText(phone);
        }
//       tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); // checkx
//       countryCode = tm.getNetworkCountryIso();

        userModule = Functions.getPrefValue(getContext(), Constants.kUserModule);
        binding.pinVu.setItemBackgroundColor(Color.WHITE);
        binding.pinVu.requestFocus();
        startCountDown();

        binding.pinVu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.pinVu.getText().toString().length() == 4) {
                    verifyClicked();
                }
            }
        });

        binding.btnBack.setOnClickListener(this);
        binding.btnVerify.setOnClickListener(this);
        binding.btnResend.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.getHelp.setOnClickListener(this);
    }

    private void startCountDown() {
        binding.countVu.start(60000);
        binding.btnResend.setVisibility(View.INVISIBLE);
        binding.countVu.setVisibility(View.VISIBLE);
        binding.countVu.setOnCountdownEndListener(endListener);
    }

    private final CountdownView.OnCountdownEndListener endListener = new CountdownView.OnCountdownEndListener() {
        @Override
        public void onEnd(CountdownView cv) {
            cv.stop();
            binding.btnResend.setVisibility(View.VISIBLE);
            binding.countVu.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnEdit) {
            backClicked();
        }
        else if (v == binding.btnVerify) {
            verifyClicked();
        }
        else if (v == binding.btnResend) {
            resendClicked();
        }
        else if (v == binding.getHelp) {
            getHelpClicked();
        }
    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }

    private void verifyClicked() {
        if (binding.pinVu.getText().toString().length() < 4) {
            Functions.showToast(this, getResources().getString(R.string.invalid_code), FancyToast.ERROR);
            return;
        }
        verifyCodeAPI(binding.pinVu.getText().toString());
    }
    private void getHelpClicked(){
        String url = "https://api.whatsapp.com/send?phone=+971547215551";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void resendClicked() {
        resendCodeAPI();
    }

    private void gotoLoginActivity() {
        binding.countVu.stop();
        Intent intent = new Intent(this, IntroSliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void resendCodeAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.resendCodeV2(Functions.getAppLang(getContext()), userId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.countVu.stop();
                            startCountDown();
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

    private void verifyCodeAPI(String code) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.verifyCodeV2(Functions.getAppLang(getContext()), userId, code);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.countVu.stop();
                            String isSignupRequired = object.getString("is_signup_required");
                            SharedPreferences.Editor editor1 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor1.putString(Constants.kaccessToken, object.getString("access_token"));
                            editor1.apply();

                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            if (isSignupRequired.equalsIgnoreCase("1")) {
                                if (userInfo != null && !userInfo.isEmpty()) {
                                    if (userInfo.getUserRole().equalsIgnoreCase(Constants.kPlayerType)) {
                                        Functions.saveUserinfo(getContext(), userInfo);
                                        Intent intent = new Intent(getContext(), PlayerSignupActivity.class);
                                        intent.putExtra("is_referee", false);
                                        startActivity(intent);
                                    }
                                    else {
                                        Intent intent = new Intent(getContext(), UserTypeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                                else {
                                    Intent intent = new Intent(getContext(), UserTypeActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else {
                                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                editor.putString(Constants.kUserID, userInfo.getId());
                                editor.putString(Constants.kIsSignIn, "1");
                                editor.putString(Constants.kUserType, userInfo.getUserRole());
                                editor.putString(Constants.kCurrency, userInfo.getCurrency());
                                editor.apply();

                                userInfo.setPhoneVerified("1");
                                Functions.saveUserinfo(getContext(), userInfo);

                                String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                                if (!fcmToken.isEmpty()) {
                                    sendFcmTokenApi(fcmToken);
                                }

                                if (userInfo.getUserRole().equalsIgnoreCase(Constants.kPlayerType)) {
                                    if (!userModule.equalsIgnoreCase("all")){
                                        SharedPreferences.Editor editor2 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                        editor2.putString(Constants.kAppModule, Constants.kLineupModule);
                                        editor2.apply();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }else {
                                        SharedPreferences.Editor editor3 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                        editor3.putString(Constants.kAppModule, Constants.kFootballModule);
                                        editor3.apply();
                                        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
//                                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase("")) {
//                                        Intent intent = new Intent(getContext(), ModuleOptionsActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                    }

                                    finish();
                                }
                                else  if (userInfo.getUserRole().equalsIgnoreCase(Constants.kOwnerType)) {
                                    Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Functions.showToast(getContext(), "Referee module is coming soon", FancyToast.SUCCESS);
                                }
                            }
                        }
                        else if (object.getInt(Constants.kStatus) == 423) {
                            // when field owner account is not activated by admin
                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    gotoLoginActivity();
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
