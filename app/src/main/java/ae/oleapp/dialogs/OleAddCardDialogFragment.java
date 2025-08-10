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
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OlefragmentAddCardDialogBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import br.com.sapereaude.maskedEditText.MaskedEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleAddCardDialogFragment extends DialogFragment {

    private OlefragmentAddCardDialogBinding binding;
    private AddCardDialogCallback dialogCallback;

    public OleAddCardDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(AddCardDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =OlefragmentAddCardDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.etCardNumber.addTextChangedListener(new GenericTextWatcher(binding.etCardNumber));
        binding.etExpiry.addTextChangedListener(new GenericTextWatcher(binding.etExpiry));
        binding.etCvv.addTextChangedListener(new GenericTextWatcher(binding.etCvv));

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClicked();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addClicked() {
        if (binding.etCardNumber.getRawText().length() < 16 || (!Functions.getCreditCardType(binding.etCardNumber.getRawText()).equalsIgnoreCase("VISA") && !Functions.getCreditCardType(binding.etCardNumber.getRawText()).equalsIgnoreCase("MASTER"))) {
            Functions.showToast(getContext(), getString(R.string.invalid_card_number), FancyToast.ERROR);
            return;
        }
        if (binding.etExpiry.getRawText().length() < 4) {
            Functions.showToast(getContext(), getString(R.string.invalid_expiry), FancyToast.ERROR);
            return;
        }
        if (binding.etCvv.getRawText().length() < 3) {
            Functions.showToast(getContext(), getString(R.string.invalid_cvv), FancyToast.ERROR);
            return;
        }

        String m = binding.etExpiry.getRawText().substring(0, 2);
        String y = binding.etExpiry.getRawText().substring(2, 4);
        String exp = String.format("%s/%s", m, y);

        addCardAPI(binding.etCardNumber.getRawText(), exp, binding.etCvv.getRawText());

    }

    private void addCardAPI(String cardNumber, String expiry, String cvv) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addCard(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), cardNumber, expiry, cvv, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String url = object.getJSONObject(Constants.kData).getString("url");
                            ((BaseActivity)getActivity()).openWebDialog(url, new OlePaymentWebDialogFragment.PaymentWebCallback() {
                                @Override
                                public void didPaymentDone(String result) {
                                    if (result.equalsIgnoreCase("1")) {
                                        Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
                                        dialogCallback.didAddCard();
                                        dismiss();
                                    }
                                    else {
                                        Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                                    }
                                }
                            });
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

    private class GenericTextWatcher implements TextWatcher{

        private final MaskedEditText editText;
        private GenericTextWatcher(MaskedEditText view) {
            this.editText = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (binding.etCardNumber.equals(editText)) {
                if (editText.getText().toString().isEmpty()) {
                    binding.tvCard.setText("XXXX XXXX XXXX XXXX");
                    binding.imgVuType.setImageResource(0);
                } else {
                    binding.tvCard.setText(editText.getText().toString());
                    if (Functions.getCreditCardType(editText.getRawText()).equalsIgnoreCase("VISA")) {
                        binding.imgVuType.setImageResource(R.drawable.visa_ic);
                    } else if (Functions.getCreditCardType(editText.getRawText()).equalsIgnoreCase("MASTER")) {
                        binding.imgVuType.setImageResource(R.drawable.mastercard_ic);
                    } else {
                        binding.imgVuType.setImageResource(0);
                    }
                }
            } else if (binding.etExpiry.equals(editText)) {
                if (editText.getText().toString().isEmpty()) {
                    binding.tvExpiry.setText("_ _ /_ _");
                } else {
                    binding.tvExpiry.setText(editText.getText().toString());
                }
            } else if (binding.etCvv.equals(editText)) {
                if (editText.getText().toString().isEmpty()) {
                    binding.tvCvv.setText("_ _ _");
                } else {
                    binding.tvCvv.setText(editText.getText().toString());
                }
            }
        }

        public void afterTextChanged(Editable editable) { }
    }

    public interface AddCardDialogCallback {
        void didAddCard();
    }
}
