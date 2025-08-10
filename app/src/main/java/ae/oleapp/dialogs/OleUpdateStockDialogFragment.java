package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;

import ae.oleapp.databinding.OlefragmentUpdateStockDialogBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleUpdateStockDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentUpdateStockDialogBinding binding;
    private String itemName = "", clubId = "", itemId = "";
    private UpdateStockDialogFragmentCallback fragmentCallback;

    public OleUpdateStockDialogFragment() {
        // Required empty public constructor
    }

    public OleUpdateStockDialogFragment(String itemName, String clubId, String itemId) {
        this.itemName = itemName;
        this.clubId = clubId;
        this.itemId = itemId;
    }

    public void setFragmentCallback(UpdateStockDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentUpdateStockDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvTitle.setText(itemName);

        binding.btnClose.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnSubmit) {
            if (binding.etQty.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.enter_qty), FancyToast.ERROR);
                return;
            }
            updateStockAPI(true, binding.etQty.getText().toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void updateStockAPI(boolean isLoader, String stock) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateStock(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, itemId, stock);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fragmentCallback.stockUpdated(OleUpdateStockDialogFragment.this);
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

    public interface UpdateStockDialogFragmentCallback {
        void stockUpdated(DialogFragment df);
    }
}