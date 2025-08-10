package ae.oleapp.owner;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
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
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCreateOfferBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleOfferData;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreateOfferActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreateOfferBinding binding;
    private OleBookingFieldAdapter fieldAdapter;
    private final List<Field> fieldList = new ArrayList<>();
    private String clubId = "";
    private String dayIds = "";
    private boolean isPerc = false;
    private OleOfferData oleOfferData;
    private List<OleKeyValuePair> days;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreateOfferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        amountClicked();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            clubId = bundle.getString("club_id", "");
            oleOfferData = gson.fromJson(bundle.getString("offer", ""), OleOfferData.class);
        }

        binding.tvCurrency.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        days = Functions.getDays(getContext());

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        if (oleOfferData == null) {
            // new
            binding.titleBar.toolbarTitle.setText(R.string.add_offer);
            binding.btnDelete.setVisibility(View.GONE);
            binding.btnAdd.setVisibility(View.VISIBLE);
            binding.overlayVu.setVisibility(View.GONE);
            if (clubId.isEmpty() && AppManager.getInstance().clubs.size() > 0) {
                Club club = AppManager.getInstance().clubs.get(0);
                clubId = club.getId();
            }
            else {
                for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
                    if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                        oleRankClubAdapter.setSelectedIndex(i);
                        break;
                    }
                }
            }
            getAllFieldsAPI(true, clubId);
        }
        else {
            // update
            binding.titleBar.toolbarTitle.setText(R.string.offer);
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.overlayVu.setVisibility(View.VISIBLE);
            binding.btnAdd.setVisibility(View.GONE);
            try {
                populateData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            getAllFieldsAPI(true, oleOfferData.getClubId());
        }

        binding.fieldRecyclerVu.setVisibility(View.GONE);
        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleBookingFieldAdapter(getContext(), fieldList, true);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.etDays.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.etStartTime.setOnClickListener(this);
        binding.percVu.setOnClickListener(this);
        binding.amountVu.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    private void populateData() throws ParseException {
        clubId = oleOfferData.getClubId();
        for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
            if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                oleRankClubAdapter.setSelectedIndex(i);
                break;
            }
        }
        binding.etTitle.setText(oleOfferData.getOfferName());
        binding.etFromDate.setText(oleOfferData.getOfferStart());
        binding.etToDate.setText(oleOfferData.getOfferExpiry());
        dayIds = oleOfferData.getDayId();
        for (OleKeyValuePair pair : days) {
            String[] arr = dayIds.split(",");
            for (String str : arr) {
                if (pair.getKey().equalsIgnoreCase(str)) {
                    if (binding.etDays.getText().toString().isEmpty()) {
                        binding.etDays.setText(pair.getValue());
                    }
                    else {
                        binding.etDays.setText(String.format("%s, %s", binding.etDays.getText().toString(), pair.getValue()));
                    }
                    break;
                }
            }
        }

        String startTime = oleOfferData.getTimimgStart().split(" ")[1];
        binding.etStartTime.setText(startTime);

        String dur = Functions.getTimeDifference(oleOfferData.getTimimgStart().split(" ")[1], oleOfferData.getTimingEnd().split(" ")[1]);
        int h = Integer.parseInt(dur) / 60;
        binding.etHour.setText(String.valueOf(h));

        if (oleOfferData.getDiscountType().equalsIgnoreCase("amount")) {
            amountClicked();
            binding.etAmount.setText(oleOfferData.getDiscount());
        }
        else {
            percClicked();
            binding.etPerc.setText(oleOfferData.getDiscount());
        }
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getAllFieldsAPI(true, clubId);
        }
    };

    OleBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            fieldAdapter.setSelectedPosition(pos);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDelete) {
            deleteClicked();
        }
        else if (v == binding.etDays) {
            daysClicked();
        }
        else if (v == binding.etFromDate) {
            fromDateClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
        else if (v == binding.etStartTime) {
            timeClicked();
        }
        else if (v == binding.percVu) {
            percClicked();
        }
        else if (v == binding.amountVu) {
            amountClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void deleteClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteOfferAPI();
                        }
                    }
                }).show();
    }

    private void timeClicked() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
                String str = formatter.format(calendar.getTime());
                binding.etStartTime.setText(str);
            }
        }, hour, minute, false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.setTimeInterval(1, 30);
        timePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private void daysClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleKeyValuePair pair: days) {
            oleSelectionList.add(new OleSelectionList(pair.getKey(), pair.getValue()));
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_day), true);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                dayIds = "";
                binding.etDays.setText("");
                for (OleSelectionList selectedItem: selectedItems) {
                    if (binding.etDays.getText().toString().isEmpty()) {
                        dayIds = selectedItem.getId();
                        binding.etDays.setText(selectedItem.getValue());
                    }
                    else {
                        dayIds = String.format("%s,%s",  dayIds, selectedItem.getId());
                        binding.etDays.setText(String.format("%s, %s",  binding.etDays.getText().toString(), selectedItem.getValue()));
                    }
                }
            }
        });
        dialog.show();
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

    private void percClicked() {
        isPerc = true;
        binding.imgPerc.setImageResource(R.drawable.check);
        binding.imgAmount.setImageResource(R.drawable.uncheck);
        binding.etAmount.setText("");
        binding.etPerc.setEnabled(true);
        binding.relPerc.setAlpha(1.0f);
        binding.etAmount.setEnabled(false);
        binding.relAmount.setAlpha(0.5f);
    }

    private void amountClicked() {
        isPerc = false;
        binding.imgPerc.setImageResource(R.drawable.uncheck);
        binding.imgAmount.setImageResource(R.drawable.check);
        binding.etPerc.setText("");
        binding.etPerc.setEnabled(false);
        binding.relPerc.setAlpha(0.5f);
        binding.etAmount.setEnabled(true);
        binding.relAmount.setAlpha(1.0f);
    }

    private void addClicked() {
        if (clubId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
            return;
        }
        if (fieldAdapter.selectedFields.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
            return;
        }
        if (binding.etTitle.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_title), FancyToast.ERROR);
            return;
        }
        if (dayIds.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_days), FancyToast.ERROR);
            return;
        }
        if (binding.etFromDate.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_from_date), FancyToast.ERROR);
            return;
        }
        if (binding.etToDate.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_to_date), FancyToast.ERROR);
            return;
        }
        if (binding.etStartTime.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_time), FancyToast.ERROR);
            return;
        }
        if (binding.etHour.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_hours), FancyToast.ERROR);
            return;
        }
        if (Integer.parseInt(binding.etHour.getText().toString()) > 24) {
            Functions.showToast(getContext(), getString(R.string.hours_one_to_24), FancyToast.ERROR);
            return;
        }
        if (binding.etPerc.getText().toString().isEmpty() && binding.etAmount.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_discount_value), FancyToast.ERROR);
            return;
        }

        String fieldsId = "";
        for (Field field: fieldAdapter.selectedFields) {
            if (fieldsId.isEmpty()) {
                fieldsId = field.getId();
            }
            else {
                fieldsId = String.format("%s,%s", fieldsId, field.getId());
            }
        }

        String startTime = String.format("%s %s", binding.etFromDate.getText().toString(), binding.etStartTime.getText().toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma", Locale.ENGLISH);
        String endTime = "";
        try {
            Date dt = dateFormat.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.HOUR, Integer.parseInt(binding.etHour.getText().toString()));
            endTime = dateFormat.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!endTime.isEmpty()) {
            if (isPerc) {
                addOfferAPI(binding.etTitle.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), startTime, endTime, binding.etPerc.getText().toString(), "percent", fieldsId);
            }
            else {
                addOfferAPI(binding.etTitle.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), startTime, endTime, binding.etAmount.getText().toString(), "amount", fieldsId);
            }
        }
    }

    private void getAllFieldsAPI(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldList.clear();
                            Gson gson = new Gson();
                            fieldAdapter.selectedFields.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                                if (oleOfferData != null) {
                                    String[] arrIds = oleOfferData.getFieldId().split(",");
                                    for (String str : arrIds) {
                                        if (field.getId().equalsIgnoreCase(str)) {
                                            fieldAdapter.selectedFields.add(field);
                                            break;
                                        }
                                    }
                                }
                            }
                            binding.fieldRecyclerVu.setVisibility(View.VISIBLE);
                            fieldAdapter.notifyDataSetChanged();
                        }
                        else {
                            fieldList.clear();
                            fieldAdapter.selectedFields.clear();
                            fieldAdapter.notifyDataSetChanged();
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

    private void addOfferAPI(String title, String from, String to, String startTime, String endTime, String value, String type, String fieldIds) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addOffer(Functions.getAppLang(getContext()), title, Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fieldIds, startTime, endTime, dayIds, type, value, from, to);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void deleteOfferAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteOffer(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), oleOfferData.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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