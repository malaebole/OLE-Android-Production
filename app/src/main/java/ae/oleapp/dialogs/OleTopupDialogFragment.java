package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.databinding.OlefragmentTopupDialogBinding;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;


/**
 * A simple {@link Fragment} subclass.
 */
public class OleTopupDialogFragment extends DialogFragment {

    private TopupDialogFragmentCallback fragmentCallback;
    private OlefragmentTopupDialogBinding binding;

    public OleTopupDialogFragment() {
        // Required empty public constructor
    }

    public void setFragmentCallback(TopupDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentTopupDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvCurrency.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        binding.btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback.walletAmount(OleTopupDialogFragment.this, binding.etPayment.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface TopupDialogFragmentCallback {
        void walletAmount(DialogFragment dialog, String amount);
    }
}
