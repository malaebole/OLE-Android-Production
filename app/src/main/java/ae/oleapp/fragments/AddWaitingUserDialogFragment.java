package ae.oleapp.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentAddWaitingUserDialogBinding;
import ae.oleapp.util.Functions;


public class AddWaitingUserDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentAddWaitingUserDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private final String bookingId;


    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public AddWaitingUserDialogFragment(String bookingId) {
        this.bookingId = bookingId;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddWaitingUserDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {

        if (v == binding.btnClose) {
            dismiss();

        } else if (v == binding.btnDone) {
            if (binding.etPhone.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), "Phone number cannot be empty!", FancyToast.ERROR);
                return;
            }
            dialogCallback.didSubmitResult(AddWaitingUserDialogFragment.this, binding.etName.getText().toString(), binding.etPhone.getText().toString());
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String name, String phone);
    }
}