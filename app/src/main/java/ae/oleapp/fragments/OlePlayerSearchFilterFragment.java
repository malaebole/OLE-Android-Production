package ae.oleapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePlayerSearchFilterAdapter;

import ae.oleapp.databinding.OlefragmentPlayerSearchFilterBinding;
import ae.oleapp.models.OleKeyValuePair;

public class OlePlayerSearchFilterFragment extends Fragment implements View.OnClickListener {

    private OlefragmentPlayerSearchFilterBinding binding;
    private String from = "", to = "", days = "", filter = "";
    private PlayerSearchFilterFragmentCallback fragmentCallback;
    private final List<OleKeyValuePair> daysList = new ArrayList<>();
    private final List<OleKeyValuePair> filterList = new ArrayList<>();
    private OlePlayerSearchFilterAdapter dayLimitAdapter;
    private OlePlayerSearchFilterAdapter filterAdapter;

    public OlePlayerSearchFilterFragment() {
        // Required empty public constructor
    }

    public OlePlayerSearchFilterFragment(String from, String to, String days, String filter) {
        this.from = from;
        this.to = to;
        this.days = days;
        this.filter = filter;
    }

    public void setFragmentCallback(PlayerSearchFilterFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPlayerSearchFilterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.etFromDate.setText(from);
        binding.etToDate.setText(to);

        getFilters();
        LinearLayoutManager filterLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.filterRecyclerVu.setLayoutManager(filterLayoutManager);
        filterAdapter = new OlePlayerSearchFilterAdapter(getContext(), filterList, filter);
        filterAdapter.setItemClickListener(filterClickListener);
        binding.filterRecyclerVu.setAdapter(filterAdapter);

        getDaysData();
        binding.daysVu.setVisibility(View.GONE);
        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        dayLimitAdapter = new OlePlayerSearchFilterAdapter(getContext(), daysList, days);
        dayLimitAdapter.setItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(dayLimitAdapter);

        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnApply) {
            fragmentCallback.getFilters(binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), days, filter);
        }
        else if (v == binding.btnReset) {
            fragmentCallback.getFilters("", "", "", "");
        }
        else if (v == binding.etFromDate) {
            fromClicked();
        }
        else if (v == binding.etToDate) {
            toClicked();
        }
    }

    private void fromClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                from = formatter.format(calendar.getTime());
                binding.etFromDate.setText(from);
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
                to = formatter.format(calendar.getTime());
                binding.etToDate.setText(to);
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    OlePlayerSearchFilterAdapter.OnItemClickListener daysClickListener = new OlePlayerSearchFilterAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            days = daysList.get(pos).getKey();
            dayLimitAdapter.setSelectedValue(days);
        }
    };

    OlePlayerSearchFilterAdapter.OnItemClickListener filterClickListener = new OlePlayerSearchFilterAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            filter = filterList.get(pos).getKey();
            filterAdapter.setSelectedValue(filter);
            if (filter.equalsIgnoreCase("booking_dates_limit")) {
                binding.daysVu.setVisibility(View.VISIBLE);
            }
            else {
                binding.daysVu.setVisibility(View.GONE);
                days = "";
                dayLimitAdapter.setSelectedValue(days);
            }
        }
    };

    private void getDaysData() {
        daysList.add(new OleKeyValuePair("1", getString(R.string.one_day)));
        daysList.add(new OleKeyValuePair("2", getString(R.string.two_days)));
        daysList.add(new OleKeyValuePair("4", getString(R.string.four_days)));
        daysList.add(new OleKeyValuePair("7", getString(R.string.one_week)));
        daysList.add(new OleKeyValuePair("15", getString(R.string.two_weeks)));
        daysList.add(new OleKeyValuePair("30", getString(R.string.one_month)));
        daysList.add(new OleKeyValuePair("60", getString(R.string.two_months)));
        daysList.add(new OleKeyValuePair("90", getString(R.string.three_months)));
        daysList.add(new OleKeyValuePair("180", getString(R.string.six_months)));
        daysList.add(new OleKeyValuePair("270", getString(R.string.nine_months)));
        daysList.add(new OleKeyValuePair("365", getString(R.string.one_year)));
    }

    private void getFilters() {
        filterList.add(new OleKeyValuePair("booking_dates_limit", getString(R.string.booking_dates_limit)));
        filterList.add(new OleKeyValuePair("pending_balance", getString(R.string.pending_balance)));
        filterList.add(new OleKeyValuePair("most_app_bookings", getString(R.string.most_app_bookings)));
        filterList.add(new OleKeyValuePair("most_call_bookings", getString(R.string.most_call_bookings)));
        filterList.add(new OleKeyValuePair("most_cancellation", getString(R.string.most_cancellation)));
        filterList.add(new OleKeyValuePair("loyalty_cards", getString(R.string.loyalty_cards)));
    }


    public interface PlayerSearchFilterFragmentCallback {
        void getFilters(String from, String to, String days, String filter);
    }
}