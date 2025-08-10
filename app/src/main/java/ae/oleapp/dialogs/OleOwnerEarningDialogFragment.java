package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentOwnerEarningDialogBinding;
import ae.oleapp.models.Earning;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleOwnerEarningDialogFragment extends DialogFragment {

    private String bookingId = "", type = "";
    private Earning earning;
    private OlefragmentOwnerEarningDialogBinding binding;

    public OleOwnerEarningDialogFragment() {
        // Required empty public constructor
    }

    public OleOwnerEarningDialogFragment(String bookingId, String type) {
        this.bookingId = bookingId;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentOwnerEarningDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        getEarningsDetailAPI(true);
        binding.relCard.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateData() {
        binding.tvAmount.setText(String.format("%s %s", earning.getAmount(), earning.getCurrency()));
        binding.tvName.setText(earning.getPlayerName());
        if (earning.getFieldSize() != null && !earning.getFieldSize().isEmpty()) {
            binding.tvField.setText(String.format("%s (%s)", earning.getFieldName(), earning.getFieldSize()));
        }
        else {
            binding.tvField.setText(earning.getFieldName());
        }
        binding.tvClub.setText(earning.getClubName());
        binding.tvTime.setText(earning.getTime());
        binding.tvDate.setText(earning.getDate());
        if (earning.getCardNumber() == null || earning.getCardNumber().isEmpty()) {
            binding.relCard.setVisibility(View.GONE);
        }
        else {
            binding.relCard.setVisibility(View.VISIBLE);
            binding.tvCardName.setText(earning.getCardType());
            binding.tvCardNum.setText(earning.getCardNumber());
        }
        if ( earning.getType().equalsIgnoreCase("booking")) {
            binding.imgVuType.setImageResource(R.drawable.credit_received_ic);
            binding.tvAmount.setTextColor(Color.parseColor("#49D483"));
            binding.tvDesc.setTextColor(Color.parseColor("#49D483"));
        }
        else {
            binding.imgVuType.setImageResource(R.drawable.credit_spent_ic);
            binding.tvAmount.setTextColor(Color.parseColor("#F02301"));
            binding.tvDesc.setTextColor(Color.parseColor("#F02301"));
        }

        if (earning.getPaymentMethod().equalsIgnoreCase("cash")) {
            binding.tvPaymentMethod.setText(R.string.cash);
        }
        else if (earning.getPaymentMethod().equalsIgnoreCase("card")) {
            binding.tvPaymentMethod.setText(R.string.card);
        }
        else if (earning.getPaymentMethod().equalsIgnoreCase("wallet")) {
            binding.tvPaymentMethod.setText(R.string.wallet);
        }
        else if (earning.getPaymentMethod().equalsIgnoreCase("apple")) {
            binding.tvPaymentMethod.setText(R.string.apple_pay);
        }
        else if (earning.getPaymentMethod().equalsIgnoreCase("samsung")) {
            binding.tvPaymentMethod.setText(R.string.samsung_pay);
        }
        else {
            binding.relPaymentMethod.setVisibility(View.GONE);
        }
    }

    private void getEarningsDetailAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEarningsDetails(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            earning = gson.fromJson(obj.toString(), Earning.class);
                            populateData();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
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