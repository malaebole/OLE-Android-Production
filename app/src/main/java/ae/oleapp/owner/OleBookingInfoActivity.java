package ae.oleapp.owner;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import com.hbb20.CCPCountry;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingInfoDayAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBookingInfoBinding;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingInfoActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingInfoBinding binding;
    private String price = "";
    private String currency = "";
    private String duration = "";
    private String size = "";
    private String clubName = "";
    private String fieldName = "";
    private String clubId = "";
    private String fieldId = "";
    private String date = "";
    private String time = "";
    private String name = "";
    private String phone = "";
    private String userId = "";
    private List<OleKeyValuePair> dayList = new ArrayList<>();
    private boolean isSchedule = false;
    private OleBookingInfoDayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.booking_info);

        isSchedule = false;
        binding.scheduleVu.setVisibility(View.GONE);

        dayList = Functions.getDays(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleBookingInfoDayAdapter(getContext(), dayList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        populateData();

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.etAddPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addPriceTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString();
                int desiredLength = 9;
                if (phoneNumber.length() == desiredLength) {
                    findPhoneDetails(clubId, binding.etPhone.getText().toString());
                }

            }
        });
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discountTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.scheduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                scheduleSwitchChanged();
            }
        });
    }

    OleBookingInfoDayAdapter.OnItemClickListener itemClickListener = new OleBookingInfoDayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            adapter.selectDay(dayList.get(pos));
        }
    };

    private void populateData() {
//        if (binding.etPhone.getText().toString().length() == 9){
//            findPhoneDetails(clubId, binding.etPhone.getText().toString());
//        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            price = bundle.getString("price", "");
            currency = bundle.getString("currency", "");
            duration = bundle.getString("duration", "");
            size = bundle.getString("size", "");
            clubName = bundle.getString("club_name", "");
            fieldName = bundle.getString("field_name", "");
            clubId = bundle.getString("club_id", "");
            fieldId = bundle.getString("field_id", "");
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
            name = bundle.getString("name", "");
            phone = bundle.getString("phone", "");
        }

        if (!phone.isEmpty()){
            binding.etName.setText(name);
            if (phone.startsWith("0")) {
                phone = phone.substring(1);
            }
            binding.etPhone.setText(phone);
        }

        binding.tvPrice.setText(String.format("%s %s", price, currency));
        binding.tvDuration.setText(String.format("%s %s", duration, getResources().getString(R.string.hour)));
        if (size.isEmpty()) {
            binding.tvFieldName.setText(fieldName);
            binding.relSchedule.setVisibility(View.GONE);
            binding.btnConfirm.setBackgroundImage(true);
        }
        else {
            binding.tvFieldName.setText(String.format("%s (%s)", fieldName, size));
            binding.btnConfirm.setBackgroundImage(false);
        }
        binding.tvClubName.setText(clubName);
        binding.tvTime.setText(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(date);
            dateFormat.applyPattern("EEEE, dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(dt));
            dateFormat.applyPattern("dd/MM/yyyy");
            binding.etFromDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnConfirm) {
            confirmClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
    }

    private void findPhoneDetails(String clubId, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.findPhoneDetails(phone, clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);

                            if (data.length() > 0) {

                                userId = data.getString("id");
                                String name = data.getString("name"); //data.optString("name", "");
                                String completed = data.getString("completed");
                                String cancelled = data.getString("canceled");
                                binding.etName.setText(name);
                                binding.tvCompletedBookings.setText("("+getString(R.string.completed)+": "+completed+")");
                                binding.tvCancelledBookings.setText("("+getString(R.string.cancelled)+": "+cancelled+")");
                                binding.tvNewUser.setVisibility(View.GONE);
                                binding.tvCompletedBookings.setVisibility(View.VISIBLE);
                                binding.tvCancelledBookings.setVisibility(View.VISIBLE);

                            } else {

                                binding.tvNewUser.setVisibility(View.VISIBLE);
                                binding.tvCompletedBookings.setVisibility(View.GONE);
                                binding.tvCancelledBookings.setVisibility(View.GONE);
                                binding.etName.setText("");
                                binding.tvCompletedBookings.setText("");
                                binding.tvCancelledBookings.setText("");

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void confirmClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        String countryCode = binding.ccp.getSelectedCountryCodeWithPlus();
        if (countryCode.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().startsWith("0")) {
            Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
            return;
        }
        if (isSchedule) {
            if (binding.etFromDate.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_from_date), FancyToast.ERROR);
                return;
            }
            if (binding.etToDate.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_to_date), FancyToast.ERROR);
                return;
            }
            String days = "", dayIds = "";
            for (OleKeyValuePair day: adapter.getSelectedDays()) {
                if (days.isEmpty()) {
                    dayIds = day.getKey();
                    days = day.getValue();
                }
                else {
                    dayIds = String.format("%s,%s",  dayIds, day.getKey());
                    days = String.format("%s, %s",  days, day.getValue());
                }
            }
            if (adapter.getSelectedDays().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_days), FancyToast.ERROR);
                return;
            }

            String msg = getResources().getString(R.string.do_you_want_schedule_book_at_place, binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), days);
            showAlert(msg, dayIds, String.format("%s%s", countryCode, binding.etPhone.getText().toString()));
        }
        else {
            String msg= getResources().getString(R.string.do_you_want_book_at_place, time, binding.tvDate.getText().toString());
            showAlert(msg, "", String.format("%s%s", countryCode, binding.etPhone.getText().toString()));
        }
    }

    private void showAlert(String msg, String dayIds, String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.confirmation))
                .setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPhoneBlockAPI(true, dayIds, phone);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void moveToIntent(String dayIds, String phone) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("user_id" ,userId);
        returnIntent.putExtra("name",binding.etName.getText().toString());
        returnIntent.putExtra("phone",phone);
        returnIntent.putExtra("discount",binding.etDiscount.getText().toString());
        returnIntent.putExtra("add_price",binding.etAddPrice.getText().toString());
        if (isSchedule) {
            returnIntent.putExtra("days", dayIds);
            returnIntent.putExtra("from_date",binding.etFromDate.getText().toString());
            returnIntent.putExtra("to_date",binding.etToDate.getText().toString());
        } else {
            returnIntent.putExtra("days", "");
            returnIntent.putExtra("from_date", "");
            returnIntent.putExtra("to_date", "");
        }
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
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

    private void addPriceTextChanged() {
        binding.etDiscount.getText().clear();
        if (binding.etAddPrice.getText().toString().isEmpty()) {
            binding.tvPrice.setText(String.format("%s %s", price, currency));
            binding.relDiscount.setVisibility(View.VISIBLE);
        }
        else {
            binding.relDiscount.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etAddPrice.getText().toString());
            double total = Double.parseDouble(price) + val;
            binding.tvPrice.setText(String.format("%s %s", total, currency));
        }
    }

    private void discountTextChanged() {
        binding.etAddPrice.getText().clear();
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(String.format("%s %s", price, currency));
            binding.relAddPrice.setVisibility(View.VISIBLE);
        }
        else {
            binding.relAddPrice.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(price) - val;
            if (val > Double.parseDouble(price)) {
                binding.etDiscount.setText(price);
                binding.tvPrice.setText(String.format("%s %s", "0", currency));
            }
            else {
                binding.tvPrice.setText(String.format("%s %s", total, currency));
            }
        }
    }

    private void scheduleSwitchChanged() {
        if (binding.scheduleSwitch.isChecked()) {
            isSchedule = true;
            binding.scheduleVu.setVisibility(View.VISIBLE);
        }
        else {
            isSchedule = false;
            binding.scheduleVu.setVisibility(View.GONE);
        }
    }

    private void checkPhoneBlockAPI(boolean isLoader, String dayIds, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.checkPhoneBlock(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String isBlock = object.getJSONObject(Constants.kData).getString("is_blocked");
                            if (isBlock.equalsIgnoreCase("1")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle(getResources().getString(R.string.blocked))
                                        .setMessage(getResources().getString(R.string.phone_blocked_desc))
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                moveToIntent(dayIds, phone);
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).create();
                                builder.show();
                            }
                            else {
                                moveToIntent(dayIds, phone);
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}
