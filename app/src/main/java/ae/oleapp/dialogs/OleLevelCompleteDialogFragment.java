package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.adapters.OleLevelAttributeAdapter;
import ae.oleapp.databinding.OlefragmentLevelCompleteDialogBinding;
import ae.oleapp.models.OlePlayerLevel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleLevelCompleteDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentLevelCompleteDialogBinding binding;
    private OlePlayerLevel olePlayerLevel;

    public OleLevelCompleteDialogFragment() {
        // Required empty public constructor
    }

    public OleLevelCompleteDialogFragment(OlePlayerLevel olePlayerLevel) {
        this.olePlayerLevel = olePlayerLevel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentLevelCompleteDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.rel.setBackgroundColor(Color.parseColor("#26C167"));
            binding.btnImg.setVisibility(View.INVISIBLE);
            binding.btnDismiss.setCardBackgroundColor(getResources().getColor(R.color.yellowColor));
            binding.tvDismiss.setTextColor(Color.parseColor("#845700"));
        }
        else {
            binding.rel.setBackgroundColor(Color.parseColor("#0084FF"));
            binding.btnImg.setVisibility(View.VISIBLE);
            binding.btnDismiss.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tvDismiss.setTextColor(Color.WHITE);
        }

        binding.tvMsg.setText(olePlayerLevel.getMessage());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        OleLevelAttributeAdapter adapter = new OleLevelAttributeAdapter(getContext(), olePlayerLevel.getTargets(), Functions.getPrefValue(getContext(), Constants.kAppModule), false);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnDismiss.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnDismiss) {
            dismissLevelAPI(true);
        }
    }

    private void dismissLevelAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.dismissTargetStatus(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), olePlayerLevel.getDismissId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                        dismiss();
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