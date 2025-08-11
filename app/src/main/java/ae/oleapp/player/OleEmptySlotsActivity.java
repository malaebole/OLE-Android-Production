package ae.oleapp.player;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingDurationAdapter;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleEmptySlotsAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmptySlotsBinding;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmptySlotsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmptySlotsBinding binding;
    private Club club;
    private String fieldId = "", selectedDuration = "", selectedDate = "";
    private final List<Field> fieldList = new ArrayList<>();
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private final List<OleBookingSlot> arrSlot = new ArrayList<>();
    private final String kBookingFormat = "yyyy-MM-dd";
    private OleBookingFieldAdapter fieldAdapter;
    private OleBookingDurationAdapter durationAdapter;
    private OleBookingDateAdapter daysAdapter;
    private OleEmptySlotsAdapter slotAdapter;
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private int selectedDateIndex = 0;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmptySlotsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.empty_slots);

        if (AppManager.getInstance().clubs.size() > 0) {
            club = AppManager.getInstance().clubs.get(0);
        }

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
        selectedDate = dateFormat.format(new Date());

        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleBookingFieldAdapter(getContext(), fieldList, fieldId);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        LinearLayoutManager durationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.durRecyclerVu.setLayoutManager(durationLayoutManager);
        durationAdapter = new OleBookingDurationAdapter(getContext(), durationList, selectedDuration);
        durationAdapter.setOnItemClickListener(durClickListener);
        binding.durRecyclerVu.setAdapter(durationAdapter);

        populateDuration();

        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        daysAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
        daysAdapter.setOnItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(daysAdapter);

        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.slotsRecyclerVu.setLayoutManager(slotLayoutManager);
        boolean isPadel = club.getClubType().equalsIgnoreCase(Constants.kPadelModule);
        slotAdapter = new OleEmptySlotsAdapter(getContext(), arrSlot, isPadel);
        slotAdapter.setOnItemClickListener(slotClickListener);
        binding.slotsRecyclerVu.setAdapter(slotAdapter);

        getAllFieldsAPI(false);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnNext) {
            nextClicked();
        }
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            club = AppManager.getInstance().clubs.get(pos);
            populateDuration();
            fieldId = "";
            arrSlot.clear();
            slotAdapter.setPadel(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));
            slotAdapter.clearSelectionAndReload(selectedDate);
            getAllFieldsAPI(true);
        }
    };

    private void nextClicked() {
        if (slotAdapter.getSelectedSlots().isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_time), FancyToast.ERROR);
            return;
        }
        if (slotAdapter.getSelectedSlots().size() > 6) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_max_six_slots), FancyToast.ERROR);
            return;
        }
        Intent intent = new Intent(getContext(), OleEmptySlotsShareActivity.class);
        intent.putExtra("club", new Gson().toJson(club));
        intent.putExtra("slots", new Gson().toJson(slotAdapter.getSelectedSlots()));
        intent.putExtra("date", selectedDate);
        startActivity(intent);
    }

    OleBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            fieldId = fieldList.get(pos).getId();
            fieldAdapter.setSelectedFieldId(fieldId);
            getSlotsAPI(selectedDate);
        }
    };

    OleBookingDurationAdapter.OnItemClickListener durClickListener = new OleBookingDurationAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDuration = durationList.get(pos).getKey();
            durationAdapter.setSelectedDuration(selectedDuration);
            getSlotsAPI(selectedDate);
        }
    };

    OleBookingDateAdapter.OnItemClickListener daysClickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (fieldId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
                return;
            }
            if (!selectedDuration.isEmpty()) {
                String date = arrDate.get(pos).getDate();
                selectedDateIndex = pos;
                daysAdapter.setSelectedDateIndex(pos);
                selectedDate = date;
                getSlotsAPI(selectedDate);
            }
            else {
                Functions.showToast(getContext(), getString(R.string.select_duration), FancyToast.ERROR);
            }
        }
    };

    OleEmptySlotsAdapter.OnItemClickListener slotClickListener = new OleEmptySlotsAdapter.OnItemClickListener() {

        @Override
        public void OnItemClick(OleEmptySlotsAdapter.ViewHolder v, int pos) {
            if (fieldId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
                return;
            }
            OleBookingSlot slot = arrSlot.get(pos);
            slotAdapter.selectSlot(slot, selectedDate);
        }
    };

    private void populateDuration() {
        durationList.clear();
        if (club.getSlots60().equalsIgnoreCase("1")) {
            durationList.add(new OleKeyValuePair("1", getString(R.string.one_hour)));
        }
        if (club.getSlots90().equalsIgnoreCase("1")) {
            durationList.add(new OleKeyValuePair("1.5", getString(R.string.one_half_hour)));
        }
        if (club.getSlots120().equalsIgnoreCase("1")) {
            durationList.add(new OleKeyValuePair("2", getString(R.string.two_hour)));
        }
        if (selectedDuration.equalsIgnoreCase("")) {
            selectedDuration = durationList.get(0).getKey();
        }
        durationAdapter.setSelectedDuration(selectedDuration);
    }

    private void getAllFieldsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), club.getId());
        call.enqueue(new Callback<>() {
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
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                            }
                            if (fieldId.equalsIgnoreCase("") && fieldList.size() > 0) {
                                fieldId = fieldList.get(0).getId();
                                getSlotsAPI(selectedDate);
                            }
                            fieldAdapter.setSelectedFieldId(fieldId);
                        }
                        else {
                            fieldList.clear();
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

    private void getSlotsAPI(String date) {
        if (fieldId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
            return;
        }
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getSlots(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fieldId, club.getId(), selectedDuration, date, "1", Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            JSONArray arrD = object.getJSONArray("booking_dates");
                            arrSlot.clear();
                            arrDate.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingSlot slot = gson.fromJson(arr.get(i).toString(), OleBookingSlot.class);
                                slot.setSlotId(String.valueOf(i+1));
                                if (slot.getStatus().equalsIgnoreCase("available")) {
                                    arrSlot.add(slot);
                                }
                            }
                            slotAdapter.clearSelectionAndReload(date);
                            for (int i = 0; i < arrD.length(); i++) {
                                arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                            }
                            daysAdapter.setDataSource(arrDate.toArray());
                        }
                        else {
                            arrSlot.clear();
                            slotAdapter.notifyDataSetChanged();
                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), null);
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