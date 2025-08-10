package ae.oleapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleEarnFilterAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentEarnFilterBinding;


public class OleEarnFilterFragment extends BaseFragment implements View.OnClickListener {

    private String[] arrFilter;
    private OleEarnFilterAdapter filterAdapter;
    private int selectedIndex = -1;
    private String fromDate = "";
    private String toDate = "";
    private EarnFilterFragmentCallBack fragmentCallBack;
    private OlefragmentEarnFilterBinding binding;

    public OleEarnFilterFragment() {

    }

    public OleEarnFilterFragment(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void setFragmentCallBack(EarnFilterFragmentCallBack fragmentCallBack) {
        this.fragmentCallBack = fragmentCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentEarnFilterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        arrFilter = new String[]{getString(R.string.lifetime), getString(R.string.today), getString(R.string.last_week), getString(R.string.last_month), getString(R.string.last_2_month), getString(R.string.last_6_month), getString(R.string.last_year)};

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        filterAdapter = new OleEarnFilterAdapter(getContext(), arrFilter, selectedIndex);
        filterAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(filterAdapter);

        binding.etFromDate.setText(fromDate);
        binding.etToDate.setText(toDate);

        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    OleEarnFilterAdapter.OnItemClickListener itemClickListener = new OleEarnFilterAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedIndex = pos;
            filterAdapter.setSelectedIndex(selectedIndex);
            binding.etFromDate.setText("");
            binding.etToDate.setText("");
            fromDate = "";
            toDate = "";
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnApply) {
            applyClicked();
        }
        else if (v == binding.btnReset) {
            resetClicked();
        }
        else if (v == binding.etFromDate) {
            fromClicked();
        }
        else if (v == binding.etToDate) {
            toClicked();
        }
    }

    private void applyClicked() {
        if (selectedIndex != -1) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            switch (selectedIndex) {
                case 0:
                    fromDate = "";
                    toDate = "";
                    break;
                case 1:
                    fromDate = dateFormat.format(new Date());
                    toDate = dateFormat.format(new Date());
                    break;
                case 2:
                    toDate = dateFormat.format(new Date());
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    fromDate = dateFormat.format(calendar.getTime());
                    break;
                case 3:
                    toDate = dateFormat.format(new Date());
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MONTH, -1);
                    fromDate = dateFormat.format(calendar.getTime());
                    break;
                case 4:
                    toDate = dateFormat.format(new Date());
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MONTH, -2);
                    fromDate = dateFormat.format(calendar.getTime());
                    break;
                case 5:
                    toDate = dateFormat.format(new Date());
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MONTH, -6);
                    fromDate = dateFormat.format(calendar.getTime());
                    break;
                case 6:
                    toDate = dateFormat.format(new Date());
                    calendar.setTime(new Date());
                    calendar.add(Calendar.YEAR, -1);
                    fromDate = dateFormat.format(calendar.getTime());
                    break;
            }
        }

        fragmentCallBack.getFilters(fromDate, toDate);
    }

    private void resetClicked() {
        fromDate = toDate = "";
        fragmentCallBack.getFilters(fromDate, toDate);
    }

    private void fromClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                fromDate = formatter.format(calendar.getTime());
                binding.etFromDate.setText(fromDate);
                selectedIndex = -1;
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void toClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                toDate = formatter.format(calendar.getTime());
                binding.etToDate.setText(toDate);
                selectedIndex = -1;
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public interface EarnFilterFragmentCallBack {
        void getFilters(String fromDate, String toDate);
    }
}
