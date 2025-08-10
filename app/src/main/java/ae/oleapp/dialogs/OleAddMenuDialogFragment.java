package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentAddMenuDialogBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleAddMenuDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private OlefragmentAddMenuDialogBinding binding;
    private AddMenuDialogFragmentCallback fragmentCallback;

    public OleAddMenuDialogFragment() {
        // Required empty public constructor
    }

    public void setFragmentCallback(AddMenuDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddMenuDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.relClub.setOnClickListener(this);
        binding.relField.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relClub) {
            addClubClicked();
        }
        else if (v == binding.relField) {
            addFieldClicked();
        }
    }

    private void addClubClicked() {
        dismiss();
        fragmentCallback.addClubClicked();
    }

    private void addFieldClicked() {
        dismiss();
        fragmentCallback.addFieldClicked();
    }

    public interface AddMenuDialogFragmentCallback {
        void addClubClicked();
        void addFieldClicked();
    }
}
