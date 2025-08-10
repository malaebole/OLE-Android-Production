package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import ae.oleapp.databinding.FragmentOrderDoneDialogBinding;

public class OrderDoneDialogFragment extends DialogFragment implements View.OnClickListener {

    private OrderDoneDialogCallback dialogCallback;
    private FragmentOrderDoneDialogBinding binding;

    public OrderDoneDialogFragment(String orderId) {
        // Required empty public constructor
    }

    public void setDialogCallback(OrderDoneDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderDoneDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnTrack.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);

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
            dialogCallback.orderDone(this, false);
        }
        else if (v == binding.btnTrack) {
            dialogCallback.orderDone(this, true);
        }
        else if (v == binding.btnContinue) {
            dialogCallback.orderDone(this, false);
        }
    }

    public interface OrderDoneDialogCallback {
        void orderDone(DialogFragment dialogFragment, boolean isTrack);
    }
}