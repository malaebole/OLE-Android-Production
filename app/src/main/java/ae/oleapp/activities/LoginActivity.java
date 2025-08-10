package ae.oleapp.activities;

import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;
import com.hbb20.CCPCountry;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityLoginBinding;
import ae.oleapp.dialogs.PopUpClass;
import ae.oleapp.models.OleAppIntro;
import ae.oleapp.models.PublicIPGetter;
import ae.oleapp.models.SubscriptionCheck;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.signup.ForgotPassActivity;
import ae.oleapp.signup.PlayerSignupActivity;
import ae.oleapp.signup.UserTypeActivity;
import ae.oleapp.signup.VerifyPhoneActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityLoginBinding binding;
    String SelectedcountryCode, userIpDetails, userModule;
    private KProgressHUD hudd;

//    private AppUpdateManager appUpdateManager;
//    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        checkKeyboardListener();
        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        binding.btnSkip.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.infoIcon.setOnClickListener(this);
        binding.resetPassword.setOnClickListener(this);

        userModule = Functions.getPrefValue(getContext(), Constants.kUserModule);
        userIpDetails = Functions.getPrefValue(getContext(), Constants.kLoginType);

//        inAppUpdates();


//        activityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartIntentSenderForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        // Handle the result
//                        if (result.getResultCode() != RESULT_OK) {
//                            // Handle failure
//                            Functions.showToast(getContext(), getString(R.string.major_changes_in_app), FancyToast.ERROR);
//                            inAppUpdates();
//                        }
//                    }
//                });
    }


//    private void inAppUpdates(){
//        appUpdateManager = AppUpdateManagerFactory.create(this);
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                appUpdateManager.startUpdateFlowForResult(
//                        appUpdateInfo,
//                        activityResultLauncher,
//                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).setAllowAssetPackDeletion(true).build());
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        ipdetails(true,"");
    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    setHeight(0.85f); //0.65f
                    setHeightRel(0.40f);
                    binding.logo.setVisibility(View.GONE);
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
                                    setHeightRel(0.65f);
                                    binding.logo.setVisibility(View.VISIBLE);
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
    private void setHeightRel(float height) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.rel.getLayoutParams();
        lp.matchConstraintPercentHeight = height;
        binding.rel.setLayoutParams(lp);
    }
    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.btnContinue) {
            btnContinueClicked();

        }
        else if (v == binding.btnSkip) {
            skipClicked();
        }
        else if (v == binding.infoIcon){
            PopUpClass popUpClass = new PopUpClass();
            popUpClass.showPopupWindow(v,true,"");
        }
        else if (v == binding.resetPassword){
            Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
            startActivity(intent);
        }
    }
    private void btnContinueClicked() {
        SelectedcountryCode = binding.ccp.getSelectedCountryCodeWithPlus();
        if (SelectedcountryCode.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().startsWith("0")) {
            Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
            return;
        }

        if (!userIpDetails.equalsIgnoreCase("otp")){
            binding.passwordVu.setVisibility(View.VISIBLE);
            binding.resetPassword.setVisibility(View.VISIBLE);
            if (binding.etPassword.getText().toString().isEmpty()){
                Functions.showToast(getContext(), getString(R.string.enter_password), FancyToast.ERROR);
                return;
            }else if (binding.etPassword.getText().length() < 4){
                Functions.showToast(getContext(), getString(R.string.pass_must), FancyToast.ERROR);
                return;
            }
            loginApi(String.format("%s%s", SelectedcountryCode,  binding.etPhone.getText().toString()), binding.etPassword.getText().toString());
        }else {
            binding.passwordVu.setVisibility(View.GONE);
            binding.resetPassword.setVisibility(View.GONE);
            loginApi(String.format("%s%s", SelectedcountryCode, binding.etPhone.getText().toString()),"");
        }

    }


    private void skipClicked() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kUserType, Constants.kPlayerType);
        editor.putString(Constants.kCurrency, getString(R.string.aed));
        editor.putString(Constants.kAppModule, Constants.kFootballModule);
        editor.remove(Constants.kUserID);
        editor.remove(Constants.kIsSignIn);
        editor.remove(Constants.kUserInfo);
        editor.apply();
        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loginApi(String phone, String password) {  //
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call;
        if (!userIpDetails.equalsIgnoreCase("otp")){
            call = AppManager.getInstance().apiInterface.withPasswordlogin(Functions.getAppLang(getContext()), phone, password, Functions.getPrefValue(getContext(), Constants.kFCMToken), "android"); //, password
        }else{
            call = AppManager.getInstance().apiInterface.loginWithPhone(Functions.getAppLang(getContext()), phone, Functions.getPrefValue(getContext(), Constants.kFCMToken), "android"); //, OTP
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String userId = obj.getString("id");
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kUserID, userId);
                            editor.apply();
                            if (!userIpDetails.equalsIgnoreCase("otp")){
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                String isSignupRequired = object.getString("is_signup_required");
                                SharedPreferences.Editor editor1 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                editor1.putString(Constants.kaccessToken, object.getString("access_token"));
                                editor1.apply();

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
                                        SharedPreferences.Editor editor2 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                        editor2.putString(Constants.kUserID, userInfo.getId());
                                        editor2.putString(Constants.kIsSignIn, "1");
                                        editor2.putString(Constants.kUserType, userInfo.getUserRole());
                                        editor2.putString(Constants.kCurrency, userInfo.getCurrency());
                                        editor2.apply();

                                        //userInfo.setPhoneVerified("0");
                                        Functions.saveUserinfo(getContext(), userInfo);

                                        String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                                        if (!fcmToken.isEmpty()) {
                                            sendFcmTokenApi(fcmToken);
                                        }

                                        if (userInfo.getUserRole().equalsIgnoreCase(Constants.kPlayerType)) {
                                            if (!userModule.equalsIgnoreCase("all")){
                                                SharedPreferences.Editor editor3 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                                editor3.putString(Constants.kAppModule, Constants.kLineupModule);
                                                editor3.apply();
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }else {
                                                SharedPreferences.Editor editor4 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                                editor4.putString(Constants.kAppModule, Constants.kFootballModule);
                                                editor4.apply();
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
                            else {
                                Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
                                intent.putExtra("phone", phone);
                                startActivity(intent);
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

    private void ipdetails(Boolean isLoader, String ip) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(),"Image processing") : null;
        Call<ResponseBody>  call = AppManager.getInstance().apiInterfaceNode.getIpDetails(ip,Functions.getAppLang(getContext()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            userIpDetails = data.getString("login_type");
                            userModule = data.getString("allow_module");
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kLoginType, userIpDetails);
                            editor.putString(Constants.kUserModule, userModule);
                            editor.apply();
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

    private void populateData(){
        if (!userIpDetails.equalsIgnoreCase("otp")){
            binding.passwordVu.setVisibility(View.VISIBLE);
            binding.resetPassword.setVisibility(View.VISIBLE);
            binding.btnSkip.setVisibility(View.GONE);
        }else {
            binding.passwordVu.setVisibility(View.GONE);
            binding.resetPassword.setVisibility(View.GONE);
            binding.btnSkip.setVisibility(View.VISIBLE);
        }
    }

}
