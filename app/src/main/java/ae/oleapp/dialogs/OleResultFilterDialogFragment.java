package ae.oleapp.dialogs;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OlefragmentResultFilterDialogBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class OleResultFilterDialogFragment extends DialogFragment implements View.OnClickListener {

    private DialogFragmentCallback dialogCallback;
    private OlefragmentResultFilterDialogBinding binding;

    public OleResultFilterDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(DialogFragmentCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OlefragmentResultFilterDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.etFromDate || v == binding.etToDate) {
            fromClicked(v);
        }
        else if (v == binding.btnApply) {
            applyClicked();
        }
    }

    private void fromClicked(View vu) {
        ((BaseActivity)getActivity()).hideKeyboard();
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                if (vu == binding.etFromDate) {
                    binding.etFromDate.setText(formatter.format(calendar.getTime()));
                }
                else {
                    binding.etToDate.setText(formatter.format(calendar.getTime()));
                }
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void applyClicked() {
        dialogCallback.applyFilter(binding.etName.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString());
        dismiss();
    }

    public interface DialogFragmentCallback {
        void applyFilter(String name, String fromDate, String toDate);
    }
}
