package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentAddBankAccountDialogBinding;
import ae.oleapp.models.OlePaymentSetting;
import ae.oleapp.util.Functions;

public class OleAddBankAccountDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentAddBankAccountDialogBinding binding;
    private AddBankAccountDialogCallback dialogCallback;
    private OlePaymentSetting olePaymentSetting;
    private boolean isUpdate = false;

    public OleAddBankAccountDialogFragment() {
        // Required empty public constructor
    }

    public OleAddBankAccountDialogFragment(OlePaymentSetting olePaymentSetting, boolean isUpdate) {
        this.olePaymentSetting = olePaymentSetting;
        this.isUpdate = isUpdate;
    }

    public void setDialogCallback(AddBankAccountDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddBankAccountDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (isUpdate) {
            binding.etAccountNo.setText(olePaymentSetting.getAccountNumber());
            binding.etBankName.setText(olePaymentSetting.getBankName());
            binding.etIbanNo.setText(olePaymentSetting.getIbanNumber());
            binding.etAccountTitle.setText(olePaymentSetting.getAccountName());
            binding.etBranch.setText(olePaymentSetting.getBranchName());
            binding.tvBtnTitle.setText(R.string.update);
        }
        else {
            binding.tvBtnTitle.setText(R.string.add_now);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void addClicked() {
        if (binding.etBankName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_bank_name), FancyToast.ERROR);
            return;
        }
        if (binding.etAccountTitle.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_account_title), FancyToast.ERROR);
            return;
        }
        if (binding.etAccountNo.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_account_no), FancyToast.ERROR);
            return;
        }

        dialogCallback.accountDetails(binding.etBankName.getText().toString(), binding.etAccountTitle.getText().toString(), binding.etIbanNo.getText().toString(), binding.etAccountNo.getText().toString(), binding.etBranch.getText().toString());
        dismiss();
    }

    public interface AddBankAccountDialogCallback {
        void accountDetails(String bankName, String accountTitle, String ibanNumber, String accountNo, String branch);
    }
}