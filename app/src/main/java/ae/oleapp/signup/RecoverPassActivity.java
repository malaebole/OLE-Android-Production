package ae.oleapp.signup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityRecoverPassBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoverPassActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityRecoverPassBinding binding;
    private boolean isPassVisible = false;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityRecoverPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        checkKeyboardListener();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username", "");
        }

        binding.pinVu.setItemBackgroundColor(Color.WHITE);
        binding.pinVu.requestFocus();
        startCountDown();

        binding.btnBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.btnResend.setOnClickListener(this);
        binding.btnEye.setOnClickListener(this);
    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    setHeight(0.85f);
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

    private void startCountDown() {
        binding.countVu.start(120000);
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
        if (v == binding.btnBack) {
            backClicked();
        }
        else if (v == binding.btnSubmit) {
            submitClicked();
        }
        else if (v == binding.btnResend) {
            resendClicked();
        }
        else if (v == binding.btnEye) {
            btnEye_clicked();
        }
    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }

    private void submitClicked() {
        if (binding.pinVu.getText().toString().length() < 4) {
            Functions.showToast(this, getResources().getString(R.string.invalid_code), FancyToast.ERROR);
            return;
        }
        if (binding.etPass.getText().toString().length()<6) {
            Functions.showToast(getContext(), getString(R.string.invalid_pass), FancyToast.ERROR);
            return;
        }
        recoverPassAPI(binding.pinVu.getText().toString(), binding.etPass.getText().toString());
    }

    private void resendClicked() {
        resendCodeAPI();
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

    private void gotoLoginActivity() {
        binding.countVu.stop();
        Intent intent = new Intent(this, IntroSliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void resendCodeAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.resetPass(Functions.getAppLang(getContext()), username);
        call.enqueue(new Callback<ResponseBody>() {
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

    private void recoverPassAPI(String code, String pass) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.recoverPass(Functions.getAppLang(getContext()), code, pass);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            gotoLoginActivity();
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
