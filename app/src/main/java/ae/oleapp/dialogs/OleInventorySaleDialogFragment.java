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
import ae.oleapp.databinding.OlefragmentInventorySaleDialogBinding;

public class OleInventorySaleDialogFragment extends DialogFragment {

    private OlefragmentInventorySaleDialogBinding binding;
    private InventorySaleDialogFragmentCallback fragmentCallback;

    public OleInventorySaleDialogFragment() {
        // Required empty public constructor
    }

    public void setFragmentCallback(InventorySaleDialogFragmentCallback fragmentCallback) {
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
        binding = OlefragmentInventorySaleDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback.doneClicked(OleInventorySaleDialogFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public interface InventorySaleDialogFragmentCallback {
        void doneClicked(DialogFragment df);
    }
}