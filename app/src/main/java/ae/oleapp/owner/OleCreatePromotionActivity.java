package ae.oleapp.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCreatePromotionBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreatePromotionActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreatePromotionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreatePromotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.create_promotion);

        binding.bar.backBtn.setOnClickListener(this);
        binding.offerVu.setOnClickListener(this);
        binding.promoVu.setOnClickListener(this);
        binding.discountVu.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPromotionsAPI(false);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.offerVu) {
            offerClicked();
        }
        else if (v == binding.promoVu) {
            promoClicked();
        }
        else if (v == binding.discountVu) {
            discountClicked();
        }
    }

    private void offerClicked() {
        startActivity(new Intent(getContext(), OleOfferListActivity.class));
    }

    private void promoClicked() {
        startActivity(new Intent(getContext(), OlePromoCodeListActivity.class));
    }

    private void discountClicked() {
        startActivity(new Intent(getContext(), OleDiscountCardsActivity.class));
    }

    private void getPromotionsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.ownerPromotionsList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String offer = object.getJSONObject(Constants.kData).getString("offers_count");
                            String promo = object.getJSONObject(Constants.kData).getString("promo_codes_count");
                            String cards = object.getJSONObject(Constants.kData).getString("card_discount_count");
                            binding.offerCount.setText(String.format("%s - %s", getString(R.string.active), offer));
                            binding.promoCodeCount.setText(String.format("%s - %s", getString(R.string.active), promo));
                            binding.discountCardCount.setText(String.format("%s - %s", getString(R.string.active), cards));
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