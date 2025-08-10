package ae.oleapp.owner;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityClubSettingsBinding;
import ae.oleapp.models.Club;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleClubSettingsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityClubSettingsBinding binding;
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityClubSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.club_settings);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            club = new Gson().fromJson(bundle.getString("club", ""), Club.class);
        }

        binding.bar.backBtn.setOnClickListener(this);
        binding.relSms.setOnClickListener(this);
        binding.relAdvertise.setOnClickListener(this);
        binding.relPayment.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);
        binding.relEditClub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.relSms) {
            Intent smsIntent = new Intent(getContext(), OleSmsServiceActivity.class);
            smsIntent.putExtra("club_id", club.getId());
            startActivity(smsIntent);
        }
        else if (v == binding.relAdvertise) {
            Intent adIntent = new Intent(getContext(), OleAdvertiseListActivity.class);
            adIntent.putExtra("club_id", club.getId());
            adIntent.putExtra("country_id", club.getCountry().getId());
            startActivity(adIntent);
        }
        else if (v == binding.relPayment) {
            Intent paymentIntent = new Intent(getContext(), OlePaymentSettingsActivity.class);
            paymentIntent.putExtra("club_id", club.getId());
            startActivity(paymentIntent);
        }
        else if (v == binding.btnDel) {
            deleteClub();
        }
        else if (v == binding.relEditClub) {
            Intent detailIntent = new Intent(getContext(), OleClubDetailActivity.class);
            detailIntent.putExtra("club_id", club.getId());
            startActivity(detailIntent);
        }
    }

    private void deleteClub() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_club))
                .setMessage(getResources().getString(R.string.do_you_want_to_delete_club))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteClubAPI(true, club.getId());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void deleteClubAPI(boolean isLoader, String clubId) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.deleteClub(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
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