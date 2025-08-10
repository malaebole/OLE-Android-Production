package ae.oleapp.fragments;

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

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentFinanceAddBinding;
import ae.oleapp.databinding.FragmentIncomeHistoryBottomSheetDialogBinding;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinanceAddFragment extends DialogFragment implements View.OnClickListener{

    private FragmentFinanceAddBinding binding;
    private String incomeId = "";
    private ResultDialogCallback dialogCallback;
    private IncomeDetailsModel incomeDetailsModel;



    public FinanceAddFragment(String incomeId) {
        this.incomeId = incomeId;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public FinanceAddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFinanceAddBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        binding.btnClose.setOnClickListener(this);
        binding.btnAddIncome.setOnClickListener(this);
        binding.btnAddExpense.setOnClickListener(this);
        binding.btnAddUpcomingExpense.setOnClickListener(this);
        binding.btnPaySalary.setOnClickListener(this);
        binding.btnPayPartner.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        if (v == binding.btnClose){
            dismiss();

        }
        else if (v == binding.btnAddIncome){
            dialogCallback.addincome(this );

        }
        else if (v == binding.btnAddExpense){
            dialogCallback.addExpense(this );

        }
        else if (v == binding.btnAddUpcomingExpense){
            dialogCallback.addUpcomingExpense(this );

        }
        else if (v == binding.btnPaySalary){
            dialogCallback.paySalary(this);

        }
        else if (v == binding.btnPayPartner){
            dialogCallback.payToPartner(this);

        }


    }


    public interface ResultDialogCallback {
        void addincome(DialogFragment df);
        void addExpense(DialogFragment df);
        void addUpcomingExpense(DialogFragment df);
        void paySalary(DialogFragment df);
        void payToPartner(DialogFragment df);


    }
}