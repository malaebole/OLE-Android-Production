package ae.oleapp.dialogs;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentIncomeHistoryBottomSheetDialogBinding;
import ae.oleapp.databinding.FragmentPartnerHistoryBottomSheetDialogBinding;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.PartnerHistoryModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerHistoryBottomSheetDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentPartnerHistoryBottomSheetDialogBinding binding;
    private String partnerId = "";
    private ResultDialogCallback dialogCallback;
    private  List<PartnerHistoryModel> partnerHistoryModelList =  new ArrayList<>();


    public PartnerHistoryBottomSheetDialogFragment(String partnerId, List<PartnerHistoryModel> partnerHistoryModelList) {
        this.partnerId = partnerId;
        this.partnerHistoryModelList = partnerHistoryModelList;
    }


    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public PartnerHistoryBottomSheetDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPartnerHistoryBottomSheetDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        populateData();

        binding.btnClose.setOnClickListener(this);

        return view;
    }

    private void populateData(){
        for (int i =0; i<partnerHistoryModelList.size(); i++){
            if (partnerId.equalsIgnoreCase(partnerHistoryModelList.get(i).getId())){
                binding.dateTv.setText(partnerHistoryModelList.get(i).getDate());
                binding.paymentMethodTv.setText(partnerHistoryModelList.get(i).getBankName());
                binding.addedTv.setText(partnerHistoryModelList.get(i).getAddedDate());
                binding.amountTv.setText(partnerHistoryModelList.get(i).getAmount());
            }
        }

    }




    @Override
    public void onClick(View v) {

        if (v == binding.btnClose) {
            dismiss();

        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }

}
