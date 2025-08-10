package ae.oleapp.dialogs;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentEmpReviewFilterDialogBinding;

public class OleDateRangeFilterDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentEmpReviewFilterDialogBinding binding;
    private String fromDate = "", toDate = "";
    private DateRangeFilterDialogFragmentCallback fragmentCallback;

    public OleDateRangeFilterDialogFragment() {
        // Required empty public constructor
    }

    public OleDateRangeFilterDialogFragment(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void setFragmentCallback(DateRangeFilterDialogFragmentCallback fragmentCallback) {
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
        binding = FragmentEmpReviewFilterDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        binding.etFromDate.setText(fromDate);
        binding.etToDate.setText(toDate);

        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnApply) {
            fragmentCallback.filterData(this, binding.etFromDate.getText().toString(), binding.etToDate.getText().toString());
        }
        else if (v == binding.btnReset) {
            fragmentCallback.filterData(this, "", "");
        }
        else if (v == binding.etFromDate) {
            fromDateClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
    }

    private void fromDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etFromDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void toDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etToDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public interface DateRangeFilterDialogFragmentCallback {
        void filterData(DialogFragment df, String from, String to);
    }
}