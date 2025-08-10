package ae.oleapp.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OlefragmentMembershipPaymentDialogBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleMembershipPaymentDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentMembershipPaymentDialogBinding binding;
    private final int CARD = 2;
    private final int SPAY = 4;
    private int paymentType = CARD;
    private MembershipPaymentDialogCallback dialogCallback;
    private String clubId = "";
    private String price = "";
    private String currency = "";
    private String title = "";
    private int couponDiscount = 0;
    private String couponId = "";

    public OleMembershipPaymentDialogFragment() {
        // Required empty public constructor
    }

    public OleMembershipPaymentDialogFragment(String clubId, String price, String currency, String title) {
        this.clubId = clubId;
        this.price = price;
        this.currency = currency;
        this.title = title;
    }

    public void setDialogCallback(MembershipPaymentDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentMembershipPaymentDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.bar.toolbarTitle.setText(R.string.payment);

        cardClicked();

        binding.tvPrice.setText(String.format("%s %s", price, currency));
        binding.tvTitle.setText(title);

        binding.btnDel.setVisibility(View.GONE);
        binding.tvCouponDis.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.relCard.setOnClickListener(this);
        binding.relSpay.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            dismiss();
        }
        else if (v == binding.relCard) {
            cardClicked();
        }
        else if (v == binding.relSpay) {
            spayClicked();
        }
        else if (v == binding.btnConfirm) {
            btnConfirmClicked();
        }
        else if (v == binding.btnApply) {
            btnApplyClicked();
        }
        else if (v == binding.btnDel) {
            btnDelClicked();
        }
    }

    private void cardClicked() {
        paymentType = CARD;
        binding.imgVuCard.setImageResource(R.drawable.check);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
    }

    private void spayClicked() {
        paymentType = SPAY;
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.check);
    }

    private void btnApplyClicked() {
        if (binding.etCoupon.getText().toString().isEmpty()) {
            return;
        }
        getCouponDiscountAPI(binding.etCoupon.getText().toString());
    }

    private void btnDelClicked() {
        binding.btnApply.setVisibility(View.VISIBLE);
        binding.btnDel.setVisibility(View.GONE);
        binding.etCoupon.setEnabled(true);
        binding.etCoupon.setText("");
        couponDiscount = 0;
        couponId = "";
        binding.tvCouponDis.setVisibility(View.GONE);
        binding.tvPrice.setText(String.format("%s %s", price, currency));
    }

    private void applyDiscount() {
        binding.btnApply.setVisibility(View.INVISIBLE);
        binding.btnDel.setVisibility(View.VISIBLE);
        binding.etCoupon.setEnabled(false);
        binding.tvCouponDis.setVisibility(View.VISIBLE);
        binding.tvCouponDis.setText(getString(R.string.promo_place, String.valueOf(couponDiscount), currency));
        String finalPrice = String.valueOf(Double.parseDouble(price) - couponDiscount);
        binding.tvPrice.setText(String.format("%s %s", finalPrice, currency));
    }

    private void btnConfirmClicked() {
        String finalPrice = String.valueOf(Double.parseDouble(price) - couponDiscount);
        if (paymentType == CARD) {
            ((BaseActivity)getActivity()).makePayment("", "card", finalPrice, currency, new BaseActivity.PaymentCallback() {
                @Override
                public void paymentStatus(boolean status, String orderRef) {
                    dialogCallback.didConfirm(status,"card", orderRef, couponId, String.valueOf(couponDiscount));
                    dismiss();
                }
            });
        }
        else if (paymentType == SPAY) {
            ((BaseActivity)getActivity()).makePayment("", "samsung", finalPrice, currency, new BaseActivity.PaymentCallback() {
                @Override
                public void paymentStatus(boolean status, String orderRef) {
                    dialogCallback.didConfirm(status,"samsung", orderRef, couponId, String.valueOf(couponDiscount));
                    dismiss();
                }
            });
        }
    }

    private void getCouponDiscountAPI(String promoCode) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.planCouponDiscount(Functions.getAppLang(getContext()), clubId, price, promoCode, Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            couponDiscount = object.getJSONObject(Constants.kData).getInt("plan_discount");
                            couponId = object.getJSONObject(Constants.kData).getString("id");
                            applyDiscount();
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

    public interface MembershipPaymentDialogCallback {
        void didConfirm(boolean status, String paymentMethod, String orderRef, String couponId, String discount);
    }
}
