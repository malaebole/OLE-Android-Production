package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ae.oleapp.databinding.OlefragmentBookingAlertDialogBinding;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleBookingAlertDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentBookingAlertDialogBinding binding;
    private BookingAlertDialogFragmentCallback fragmentCallback;

    public OleBookingAlertDialogFragment() {
        // Required empty public constructor
    }

    public void setFragmentCallback(BookingAlertDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentBookingAlertDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            binding.relPhone.setVisibility(View.GONE);
        }
        else {
            binding.relPhone.setVisibility(View.VISIBLE);
        }

        binding.btnNo.setOnClickListener(this);
        binding.btnYes.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnYes) {
            yesClicked();
        }
        else if (v == binding.btnNo) {
            noClicked();
        }
    }

    private void yesClicked() {
        fragmentCallback.didSubmit(binding.etPhone.getText().toString());
        dismiss();
    }

    private void noClicked() {
        dismiss();
    }

    public interface BookingAlertDialogFragmentCallback {
        void didSubmit(String phone);
    }
}