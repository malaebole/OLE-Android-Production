package ae.oleapp.signup;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import ae.oleapp.BuildConfig;
import ae.oleapp.R;
import ae.oleapp.activities.MainActivity;
import ae.oleapp.activities.OleUpdateAppActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivitySplashBinding;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.shop.ProductDetailActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private OleactivitySplashBinding binding;

    private Handler handler;
    private Uri deepLinkUri;
    TextView version_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();
        if (Functions.getAppLangStr(getContext()).isEmpty()) {
            Functions.setAppLang(getContext(), "en");
        }
        String ver = BuildConfig.VERSION_NAME;
        binding.versionText.setText("Version " + ver);
        deepLinkUri = getIntent().getData();

    }


    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        handler.postDelayed(runnable, 500);
    }

    private void handleIntent(Intent intent) {
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            String appLinkAction = intent.getAction();
            Uri appLinkData = intent.getData();
            if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
                String prodId = appLinkData.getLastPathSegment();
                Intent detailIntent = new Intent(getContext(), ProductDetailActivity.class);
                detailIntent.putExtra("prod_id", prodId);
                startActivity(detailIntent);
            }
        }
    }

    public void devicesLoginLimit() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.devicesLoginLimit(userId, uniqueID, "ole");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Intent i = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getContext(), IntroSliderActivity.class);
                            startActivity(i);
                            finish();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
                    if (deepLinkUri != null) {
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kFootballModule)) {
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kAppModule, Constants.kLineupModule);
                            editor.apply();
                        }
                        Functions.setAppLang(getContext(), "en");
                        Functions.changeLanguage(getContext(), "en");
                        sendAppLangApi();
                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.putExtra("invite_url", deepLinkUri);
                        startActivity(i);
                        finish();

                    } else {
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase("")) {
                            Intent intent = new Intent(getContext(), ModuleOptionsActivity.class);
                            startActivity(intent);
                        } else if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
                            Functions.setAppLang(getContext(), "en");
                            Functions.changeLanguage(getContext(), "en");
                            sendAppLangApi();
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            Intent i = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                            startActivity(i);
                            finish();

                        }
                        //share link intent
                        handleIntent(getIntent());
                    }

                } else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                    if (deepLinkUri != null) {
                        Functions.showToast(getContext(), "You are currently logged in as Owner\nPlease login as player first.", FancyToast.ERROR);
                    }
                    devicesLoginLimit();
                }

                getProfileAPI(false);
            } else {
                if (deepLinkUri != null) {
                    Functions.showToast(getContext(), "Please login first to join the game.", FancyToast.ERROR);
                }
                Intent i = new Intent(getContext(), IntroSliderActivity.class);
                startActivity(i);
                finish();
            }
            checkUpdatesApi();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }

    }

    protected void sendAppLangApi() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendAppLang(userId, Functions.getAppLang(getContext()));
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    private void checkUpdatesApi() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.checkUpdate("android");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String version = object.getJSONObject(Constants.kData).getString("version");
                            String update = object.getJSONObject(Constants.kData).getString("force_update");

                            if (!version.equalsIgnoreCase(BuildConfig.VERSION_NAME)) {
                                Intent intent = new Intent(getContext(), OleUpdateAppActivity.class);
                                intent.putExtra("version", version);
                                intent.putExtra("force_update", update);
                                startActivity(intent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), "", Functions.getPrefValue(getContext(), Constants.kAppModule));
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
                            editor.putString(Constants.kUserType, userInfo.getUserRole());
                            editor.putString(Constants.kCurrency, userInfo.getCurrency());
                            editor.apply();

                            Functions.saveUserinfo(getContext(), userInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
            }
        });
    }
}
