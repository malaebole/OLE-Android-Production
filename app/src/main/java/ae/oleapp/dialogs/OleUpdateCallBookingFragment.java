package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentUpdateCallBookingBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleUpdateCallBookingFragment extends DialogFragment {

    private String name = "", phone = "", bookingId = "", callBooking = "", price = "", currency = "", duration = "";
    private UpdateCallBookingFragmentCallback fragmentCallback;
    private OlefragmentUpdateCallBookingBinding binding;

    public OleUpdateCallBookingFragment() {
        // Required empty public constructor
    }

    public OleUpdateCallBookingFragment(String name, String phone, String bookingId, String callBooking, String price, String currency, String duration) {
        this.name = name;
        this.phone = phone;
        this.bookingId = bookingId;
        this.callBooking = callBooking;
        this.price = price;
        this.currency = currency;
        this.duration = duration;
    }

    public void setFragmentCallback(UpdateCallBookingFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentUpdateCallBookingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvPrice.setText(String.format("%s %s", price, currency));
        binding.tvDuration.setText(duration);
        binding.etName.setText(name);
        binding.etMobile.setText(phone);

        binding.etAddPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addPriceTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discountTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClicked();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addPriceTextChanged() {
        binding.etDiscount.getText().clear();
        if (binding.etAddPrice.getText().toString().isEmpty()) {
            binding.tvPrice.setText(String.format("%s %s", price, currency));
            binding.relDiscount.setVisibility(View.VISIBLE);
        }
        else {
            binding.relDiscount.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etAddPrice.getText().toString());
            double total = Double.parseDouble(price) + val;
            binding.tvPrice.setText(String.format("%s %s", total, currency));
        }
    }

    private void discountTextChanged() {
        binding.etAddPrice.getText().clear();
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(String.format("%s %s", price, currency));
            binding.relAddPrice.setVisibility(View.VISIBLE);
        }
        else {
            binding.relAddPrice.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(price) - val;
            if (val > Double.parseDouble(price)) {
                binding.etDiscount.setText(price);
                binding.tvPrice.setText(String.format("%s %s", "0", currency));
            }
            else {
                binding.tvPrice.setText(String.format("%s %s", total, currency));
            }
        }
    }

    private void updateClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (binding.etMobile.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        updateBookingAPI(true, binding.etName.getText().toString(), binding.etMobile.getText().toString(), binding.etAddPrice.getText().toString(), binding.etDiscount.getText().toString());
    }

    private void updateBookingAPI(boolean isLoader, String name, String phone, String pricePlus, String priceMinus) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateCallBookingDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, name, phone, pricePlus, priceMinus);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dismiss();
                            fragmentCallback.didUpdateDetails(name, phone);
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

    public interface UpdateCallBookingFragmentCallback {
        void didUpdateDetails(String name, String phone);
    }
}