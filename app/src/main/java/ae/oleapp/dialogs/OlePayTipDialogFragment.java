package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentPayTipDialogBinding;


public class OlePayTipDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentPayTipDialogBinding binding;
    private PayTipDialogFragmentCallback fragmentCallback;
    private String amount = "";

    public OlePayTipDialogFragment() {
        // Required empty public constructor
    }

    public OlePayTipDialogFragment(String amount) {
        this.amount = amount;
    }

    public void setFragmentCallback(PayTipDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPayTipDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.etAmount.setText(amount);

        binding.btnClose.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnSubmit) {
            if (!binding.etAmount.getText().toString().isEmpty()) {
                fragmentCallback.submitClicked(this, binding.etAmount.getText().toString());
            }
        }
    }

    public interface PayTipDialogFragmentCallback {
        void submitClicked(DialogFragment df, String amount);
    }
}