package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;
import java.net.UnknownHostException;
import ae.oleapp.R;
import ae.oleapp.databinding.OleupdatePassDialogBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleUpdatePassDialog extends Dialog implements View.OnClickListener {

    private final Context context;
    private boolean isOldPassVisible = false;
    private boolean isNewPassVisible = false;
    private boolean isConfirmPassVisible = false;
    private OleupdatePassDialogBinding binding;

    public OleUpdatePassDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleupdatePassDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnUpdate.setOnClickListener(this);
        binding.btnOldpassEye.setOnClickListener(this);
        binding.btnNewpassEye.setOnClickListener(this);
        binding.btnConfirmpassEye.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnUpdate) {
            updateClicked();
        }
        else if (v == binding.btnOldpassEye) {
            oldPassEyeClicked();
        }
        else if (v == binding.btnNewpassEye) {
            newPassEyeClicked();
        }
        else if (v == binding.btnConfirmpassEye) {
            cnfrmPassEyeClicked();
        }
    }

    private void updateClicked() {
        if (binding.etOldPass.getText().toString().length() < 6) {
            Functions.showToast(context, context.getResources().getString(R.string.invalid_old_pass), FancyToast.ERROR);
            return;
        }
        if (binding.etNewPass.getText().toString().length() < 6) {
            Functions.showToast(context, context.getResources().getString(R.string.invalid_new_pass), FancyToast.ERROR);
            return;
        }
        if (!binding.etNewPass.getText().toString().equals(binding.etConfirmPass.getText().toString())) {
            Functions.showToast(context, context.getResources().getString(R.string.pass_not_matched), FancyToast.ERROR);
            return;
        }

        updatePassAPI(binding.etOldPass.getText().toString(), binding.etNewPass.getText().toString());

    }

    private void oldPassEyeClicked() {
        if (isOldPassVisible) {
            binding.etOldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.btnOldpassEye.setImageResource(R.drawable.view_ic);
            isOldPassVisible = false;
        }
        else {
            binding.etOldPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.btnOldpassEye.setImageResource(R.drawable.hide_ic);
            isOldPassVisible = true;
        }
    }

    private void newPassEyeClicked() {
        if (isNewPassVisible) {
            binding.etNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.btnNewpassEye.setImageResource(R.drawable.view_ic);
            isNewPassVisible = false;
        }
        else {
            binding.etNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.btnNewpassEye.setImageResource(R.drawable.hide_ic);
            isNewPassVisible = true;
        }
    }

    private void cnfrmPassEyeClicked() {
        if (isConfirmPassVisible) {
            binding.etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.btnConfirmpassEye.setImageResource(R.drawable.view_ic);
            isConfirmPassVisible = false;
        }
        else {
            binding.etConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.btnConfirmpassEye.setImageResource(R.drawable.hide_ic);
            isConfirmPassVisible = true;
        }
    }

    private void updatePassAPI(String oldPass, String newPass) {
        KProgressHUD hud = Functions.showLoader(context, "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePass(Functions.getAppLang(context),Functions.getPrefValue(context, Constants.kUserID), oldPass, newPass);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dismiss();
                        }
                        else {
                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(context, e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(context, context.getResources().getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), context.getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}
