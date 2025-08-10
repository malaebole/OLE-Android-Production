package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import ae.oleapp.databinding.FragmentAddedCartDialogBinding;


public class AddedCartDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentAddedCartDialogBinding binding;
    private AddedCartDialogCallback dialogCallback;

    public AddedCartDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(AddedCartDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddedCartDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.btnCart.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnContinue) {
            dialogCallback.addedCart(this, false);
        }
        else if (v == binding.btnCart) {
            dialogCallback.addedCart(this, true);
        }
    }

    public interface AddedCartDialogCallback {
        void addedCart(DialogFragment df, boolean isViewCart);
    }

}