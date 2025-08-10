package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.databinding.FragmentShopSortingDialogBinding;

public class OleShopSortingDialogFragment extends DialogFragment implements View.OnClickListener {

    private ShopSortingDialogCallback dialogCallback;
    private FragmentShopSortingDialogBinding binding;

    public void setDialogCallback(ShopSortingDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public OleShopSortingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShopSortingDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnHigh.setOnClickListener(this);
        binding.btnLow.setOnClickListener(this);
        binding.btnPopular.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnPopular) {
            dialogCallback.sorting(this, "popular");
        }
        else if (v == binding.btnHigh) {
            dialogCallback.sorting(this, "price_high");
        }
        else if (v == binding.btnLow) {
            dialogCallback.sorting(this, "price_low");
        }
    }

    public interface ShopSortingDialogCallback {
        void sorting(DialogFragment df, String sorting);
    }
}