package ae.oleapp.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentFinanceAddBinding;
import ae.oleapp.models.IncomeDetailsModel;

public class FinanceAddFragment extends DialogFragment implements View.OnClickListener {

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
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


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

        if (v == binding.btnClose) {
            dismiss();

        } else if (v == binding.btnAddIncome) {
            dialogCallback.addincome(this);

        } else if (v == binding.btnAddExpense) {
            dialogCallback.addExpense(this);

        } else if (v == binding.btnAddUpcomingExpense) {
            dialogCallback.addUpcomingExpense(this);

        } else if (v == binding.btnPaySalary) {
            dialogCallback.paySalary(this);

        } else if (v == binding.btnPayPartner) {
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