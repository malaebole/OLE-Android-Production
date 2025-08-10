package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.databinding.OlefragmentBookingPriceDetailDialogBinding;

public class OleBookingPriceDetailDialogFragment extends DialogFragment {

    private OlefragmentBookingPriceDetailDialogBinding binding;
    private String bookingPrice = "", matchFee = "", currency = "";

    public OleBookingPriceDetailDialogFragment() {
        // Required empty public constructor
    }

    public OleBookingPriceDetailDialogFragment(String bookingPrice, String matchFee, String currency) {
        this.bookingPrice = bookingPrice;
        this.matchFee = matchFee;
        this.currency = currency;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentBookingPriceDetailDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvBookingPrice.setText(String.format("%s %s", bookingPrice, currency));
        binding.tvMatchFee.setText(String.format("%s %s", matchFee, currency));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}