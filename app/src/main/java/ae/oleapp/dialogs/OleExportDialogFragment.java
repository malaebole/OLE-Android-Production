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

import ae.oleapp.databinding.OlefragmentExportDialogBinding;
import ae.oleapp.util.Functions;

public class OleExportDialogFragment extends DialogFragment {

    private OlefragmentExportDialogBinding binding;
    private ExportDialogFragmentCallback dialogCallback;

    public OleExportDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(ExportDialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
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
        binding = OlefragmentExportDialogBinding.inflate(inflater, container, false);
        View  view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.etEmail.setText(Functions.getUserinfo(getContext()).getEmail());

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitClicked();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void submitClicked() {
        dialogCallback.export(binding.etEmail.getText().toString());
        dismiss();
    }

    public interface ExportDialogFragmentCallback {
        void export(String email);
    }
}