package ae.oleapp.owner;

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
import ae.oleapp.databinding.OleactivitySmsDetailsBinding;
import ae.oleapp.models.OleSmsData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleSmsDetailsActivity extends BaseActivity {

    private OleactivitySmsDetailsBinding binding;
    private OleSmsData oleSmsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivitySmsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            oleSmsData = gson.fromJson(bundle.getString("data", ""), OleSmsData.class);
        }

//        populateData();

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    private void populateData() {
//        binding.tvTotalSms.setText(smsData.getTotalSms());
//        binding.tvRemainSms.setText(smsData.getRemainingSms());
//        if (smsData.getForOwner().equalsIgnoreCase("1")) {
//            binding.ownerSwitch.setChecked(true);
//        }
//        if (smsData.getForPlayer().equalsIgnoreCase("1")) {
//            binding.playerSwitch.setChecked(true);
//        }
//        if (smsData.getForContinuous().equalsIgnoreCase("1")) {
//            binding.bookingSwitch.setChecked(true);
//        }
//        binding.ownerSwitch.setOnCheckedChangeListener(changeListener);
//        binding.playerSwitch.setOnCheckedChangeListener(changeListener);
//        binding.bookingSwitch.setOnCheckedChangeListener(changeListener);
//    }

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.owner_switch) {
                if (binding.ownerSwitch.isChecked()) {
                    updateSmsAPI(true, oleSmsData.getClubId(), "1", "", "", "");
                }
                else {
                    updateSmsAPI(true, oleSmsData.getClubId(), "0", "", "", "");
                }
            }
            else if (buttonView.getId() == R.id.player_switch) {
                if (binding.playerSwitch.isChecked()) {
                    updateSmsAPI(true, oleSmsData.getClubId(), "", "1", "", "");
                }
                else {
                    updateSmsAPI(true, oleSmsData.getClubId(), "", "0", "", "");
                }
            }
            else if (buttonView.getId() == R.id.booking_switch) {
                if (binding.bookingSwitch.isChecked()) {
                    updateSmsAPI(true, oleSmsData.getClubId(), "", "", "", "1");
                }
                else {
                    updateSmsAPI(true, oleSmsData.getClubId(), "", "", "", "0");
                }
            }
        }
    };

    private void updateSmsAPI(boolean isLoader, String clubId, String fieldOwner, String player, String gap, String booking) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateSMS(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, oleSmsData.getSmsId(), "", "", "", fieldOwner, player, gap, booking);
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