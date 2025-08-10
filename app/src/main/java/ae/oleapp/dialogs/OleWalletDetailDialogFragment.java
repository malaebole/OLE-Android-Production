package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentWalletDetailDialogBinding;
import ae.oleapp.models.OleTransactionHistory;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleWalletDetailDialogFragment extends DialogFragment {

    private OleTransactionHistory history;
    private OlefragmentWalletDetailDialogBinding binding;

    public OleWalletDetailDialogFragment() {
        // Required empty public constructor
    }

    public OleWalletDetailDialogFragment(OleTransactionHistory history) {
        this.history = history;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentWalletDetailDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        populateData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateData() {
        binding.tvDesc.setText(history.getDescription());
        binding.tvCardName.setText(history.getCardName());
        binding.tvCardNum.setText(history.getCardNum());
        binding.tvDate.setText(history.getDatetime());
        binding.tvDeduction.setText(history.getDeduction());
        binding.deductionVu.setVisibility(View.GONE);
        if (history.getPaymentType().equalsIgnoreCase("topup") || history.getPaymentType().equalsIgnoreCase("refund")) {
            binding.tvDesc.setTextColor(Color.parseColor("#49D483"));
            binding.imgVuType.setImageResource(R.drawable.credit_received_ic);
            if (history.getPaymentType().equalsIgnoreCase("refund")) {
                binding.deductionVu.setVisibility(View.VISIBLE);
            }
        }
        else {
            binding.tvDesc.setTextColor(Color.parseColor("#F02301"));
            binding.imgVuType.setImageResource(R.drawable.credit_spent_ic);
        }
    }

}
