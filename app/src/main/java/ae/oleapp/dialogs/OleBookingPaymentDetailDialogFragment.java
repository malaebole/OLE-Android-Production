package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentBookingPaymentDetailDialogBinding;

public class OleBookingPaymentDetailDialogFragment extends DialogFragment {

    private OlefragmentBookingPaymentDetailDialogBinding binding;
    private String posPaid = "", cashPaid = "", cardPaid = "", wallet = "", currency = "", totalPaid = "", totalBalance = "", paidBalance = "";

    public OleBookingPaymentDetailDialogFragment() {
        // Required empty public constructor
    }

    public OleBookingPaymentDetailDialogFragment(String posPaid, String cashPaid, String cardPaid, String wallet, String currency, String totalPaid, String totalBalance, String paidBalance) {
        this.posPaid = posPaid;
        this.cashPaid = cashPaid;
        this.cardPaid = cardPaid;
        this.wallet = wallet;
        this.currency = currency;
        this.totalPaid = totalPaid;
        this.totalBalance = totalBalance;
        this.paidBalance = paidBalance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentBookingPaymentDetailDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvCard.setText(getString(R.string.currency_place, cardPaid, currency));
        binding.tvPos.setText(getString(R.string.currency_place, posPaid, currency));
        binding.tvCash.setText(getString(R.string.currency_place, cashPaid, currency));
        binding.tvWallet.setText(getString(R.string.currency_place, wallet, currency));
        binding.tvTotal.setText(getString(R.string.currency_place, totalPaid, currency));
        binding.tvBalanceAmount.setText(getString(R.string.currency_place, totalBalance, currency));
        binding.tvPaidBalance.setText(getString(R.string.currency_place, paidBalance, currency));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}