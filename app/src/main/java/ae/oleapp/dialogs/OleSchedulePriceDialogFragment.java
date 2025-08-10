package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
import ae.oleapp.databinding.OlefragmentSchedulePriceDialogBinding;
import ae.oleapp.models.OleScheduleList;
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
public class OleSchedulePriceDialogFragment extends DialogFragment {

    private OlefragmentSchedulePriceDialogBinding binding;
    private OleScheduleList schedule;
    private String bookingIds = "";
    private SchedulePriceDialogFragmentCallback fragmentCallback;

    public OleSchedulePriceDialogFragment() {
        // Required empty public constructor
    }

    public OleSchedulePriceDialogFragment(OleScheduleList oleScheduleList, String bookingIds) {
        this.schedule = oleScheduleList;
        this.bookingIds = bookingIds;
    }

    public void setFragmentCallback(SchedulePriceDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentSchedulePriceDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvPrice.setText(String.format("%s %s", schedule.getPrice(), schedule.getCurrency()));
        binding.tvDuration.setText(schedule.getDuration());
        binding.etName.setText(schedule.getName());
        binding.etMobile.setText(schedule.getPhone());

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
            binding.tvPrice.setText(String.format("%s %s", schedule.getPrice(), schedule.getCurrency()));
            binding.relDiscount.setVisibility(View.VISIBLE);
        }
        else {
            binding.relDiscount.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etAddPrice.getText().toString());
            double total = Double.parseDouble(schedule.getPrice()) + val;
            binding.tvPrice.setText(String.format("%s %s", total, schedule.getCurrency()));
        }
    }

    private void discountTextChanged() {
        binding.etAddPrice.getText().clear();
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(String.format("%s %s", schedule.getPrice(), schedule.getCurrency()));
            binding.relAddPrice.setVisibility(View.VISIBLE);
        }
        else {
            binding.relAddPrice.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(schedule.getPrice()) - val;
            if (val > Double.parseDouble(schedule.getPrice())) {
                binding.etDiscount.setText(schedule.getPrice());
                binding.tvPrice.setText(String.format("%s %s", "0", schedule.getCurrency()));
            }
            else {
                binding.tvPrice.setText(String.format("%s %s", total, schedule.getCurrency()));
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
        updateSchedulePriceAPI(true, binding.etName.getText().toString(), binding.etMobile.getText().toString(), binding.etAddPrice.getText().toString(), binding.etDiscount.getText().toString());
    }

    private void updateSchedulePriceAPI(boolean isLoader, String name, String phone, String pricePlus, String priceMinus) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateSchedulePrice(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingIds, name, phone, pricePlus, priceMinus);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fragmentCallback.didUpdatePrice();
                            dismiss();
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

    public interface SchedulePriceDialogFragmentCallback {
        void didUpdatePrice();
    }
}
