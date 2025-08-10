package ae.oleapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.andrewjapar.rangedatepicker.CalendarPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityDateRangeBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;

public class OleDateRangeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityDateRangeBinding binding;
    private Date fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityDateRangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.select_date_range);

        Calendar startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.ENGLISH);
        Calendar endDate = Calendar.getInstance(TimeZone.getDefault(), Locale.ENGLISH);
        startDate.add(Calendar.YEAR, -2);
        endDate.add(Calendar.YEAR, 2);

        binding.calenderPicker.setRangeDate(startDate.getTime(), endDate.getTime());
        Calendar scrollDate = Calendar.getInstance();
        scrollDate.setTime(new Date());
        scrollDate.add(Calendar.MONTH, -1);
        binding.calenderPicker.scrollToDate(scrollDate.getTime());
        binding.calenderPicker.setMode(CalendarPicker.SelectionMode.RANGE);

        Calendar startSelectionDate = Calendar.getInstance(TimeZone.getDefault(), Locale.ENGLISH);
        Calendar endSelectionDate = Calendar.getInstance(TimeZone.getDefault(), Locale.ENGLISH);
        fromDate = startSelectionDate.getTime();
        endSelectionDate.add(Calendar.DAY_OF_MONTH, 2);
        toDate = endSelectionDate.getTime();
        binding.calenderPicker.setSelectionDate(fromDate, toDate);

        binding.calenderPicker.setOnRangeSelectedListener(new Function4<Date, Date, String, String, Unit>() {
            @Override
            public Unit invoke(Date startDate, Date endDate, String startLabel, String endLabel) {
                fromDate = startDate;
                toDate = endDate;
                return null;
            }
        });

        binding.calenderPicker.setOnStartSelectedListener(new Function2<Date, String, Unit>() {
            @Override
            public Unit invoke(Date date, String s) {
                fromDate = date;
                toDate = null;
                return null;
            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDone) {
            doneClicked();
        }
    }

    private void doneClicked() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        Intent intent = new Intent();
        if (fromDate != null && toDate != null) {
            intent.putExtra("from_date", dateFormat.format(fromDate));
            intent.putExtra("to_date", dateFormat.format(toDate));
        }
        else if (toDate == null) {
            intent.putExtra("from_date", dateFormat.format(fromDate));
            intent.putExtra("to_date", "");
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
