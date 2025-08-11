package ae.oleapp.owner;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDurationAdapter;
import ae.oleapp.adapters.OleBookingSlotAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityChangeBookingTimeBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleChangeBookingTimeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityChangeBookingTimeBinding binding;
    private String bookingId = "", bookingTime = "", slot60 = "", slot90 = "", slot120 = "", duration = "", clubType = "", date = "", selectedSlotDuration = "";
    private OleBookingDurationAdapter durationAdapter;
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private final List<OleBookingSlot> arrSlot = new ArrayList<>();
    private int selectedSlotIndex = -1;
    private OleBookingSlotAdapter slotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityChangeBookingTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.change_time);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            bookingTime = bundle.getString("booking_time", "");
            slot60 = bundle.getString("slot60", "");
            slot90 = bundle.getString("slot90", "");
            slot120 = bundle.getString("slot120", "");
            duration = bundle.getString("duration", "");
            clubType = bundle.getString("club_type", "");
            date = bundle.getString("date", "");
        }

        if (slot60.equalsIgnoreCase("1")) {
            durationList.add(new OleKeyValuePair("1", getString(R.string.one_hour)));
        }
        if (slot90.equalsIgnoreCase("1")) {
            durationList.add(new OleKeyValuePair("1.5", getString(R.string.one_half_hour)));
        }
        if (slot120.equalsIgnoreCase("1")) {
            durationList.add(new OleKeyValuePair("2", getString(R.string.two_hour)));
        }

        binding.btnUpdate.setBackgroundImage(clubType.equalsIgnoreCase(Constants.kPadelModule));

        LinearLayoutManager durationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.durRecyclerVu.setLayoutManager(durationLayoutManager);
        durationAdapter = new OleBookingDurationAdapter(getContext(), durationList, duration);
        durationAdapter.setOnItemClickListener(durClickListener);
        binding.durRecyclerVu.setAdapter(durationAdapter);

        GridLayoutManager slotLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(slotLayoutManager);
        boolean isPadel = clubType.equalsIgnoreCase(Constants.kPadelModule);
        slotAdapter = new OleBookingSlotAdapter(getContext(), arrSlot, isPadel);
        slotAdapter.setSetManualWidth(true);
        slotAdapter.setOnItemClickListener(slotClickListener);
        binding.recyclerVu.setAdapter(slotAdapter);

        getSlotsAPI();

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnUpdate) {
            if (selectedSlotIndex == -1) {
                Functions.showToast(getContext(), getResources().getString(R.string.select_time), FancyToast.ERROR);
                return;
            }
            if (selectedSlotDuration.isEmpty()) {
                selectedSlotDuration = duration;
            }
            String dateStr = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date dt = dateFormat.parse(date);
                dateFormat.applyPattern("dd/MM/yyyy");
                dateStr = dateFormat.format(dt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            OleBookingSlot slot = arrSlot.get(selectedSlotIndex);
            String msg = getString(R.string.do_you_want_change_booking_time_from_on_place, bookingTime, slot.getSlot(), dateStr);
            showAlert(msg, slot);
        }
    }

    private void showAlert(String msg, OleBookingSlot slot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.confirmation))
                .setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateTimeAPI(slot.getStart(), slot.getEnd(), slot.getShift());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    OleBookingDurationAdapter.OnItemClickListener durClickListener = new OleBookingDurationAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            duration = durationList.get(pos).getKey();
            durationAdapter.setSelectedDuration(duration);
            getSlotsAPI();
        }
    };

    OleBookingSlotAdapter.OnItemClickListener slotClickListener = new OleBookingSlotAdapter.OnItemClickListener() {
        @Override
        public void OnItemLongClick(OleBookingSlotAdapter.ViewHolder v, int pos) {

        }

        @Override
        public void OnItemClick(OleBookingSlotAdapter.ViewHolder viewHolder, int pos) {
            OleBookingSlot slot = arrSlot.get(pos);
            if (slot.getStatus().equalsIgnoreCase("hidden") || slot.getStatus().equalsIgnoreCase("booked")) {
                return;
            }
            if (duration.equalsIgnoreCase(viewHolder.slotDuration)) {
                selectedSlotDuration = viewHolder.slotDuration;
                selectedSlotIndex = pos;
                slotAdapter.setSelectedSlotIndex(pos, date);
            }
            else {
                String msg = getResources().getString(R.string.slot_selected_dur_msg, duration, viewHolder.slotDuration, viewHolder.slotDuration);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.confirmation))
                        .setMessage(msg)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedSlotDuration = viewHolder.slotDuration;
                                selectedSlotIndex = pos;
                                slotAdapter.setSelectedSlotIndex(pos, date);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                builder.show();
            }
        }
    };

    private void getSlotsAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getSlotsForChangeTime(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, duration);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            arrSlot.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                arrSlot.add(gson.fromJson(arr.get(i).toString(), OleBookingSlot.class));
                            }
                            selectedSlotIndex = -1;
                            slotAdapter.setSelectedSlotIndex(selectedSlotIndex, date);// it will reload data
                        }
                        else {
                            arrSlot.clear();
                            slotAdapter.notifyDataSetChanged();
                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        arrSlot.clear();
                        slotAdapter.notifyDataSetChanged();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    arrSlot.clear();
                    slotAdapter.notifyDataSetChanged();
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                arrSlot.clear();
                slotAdapter.notifyDataSetChanged();
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void updateTimeAPI(String timeStart, String timeEnd, String shift) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateBookingTime(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, timeStart, timeEnd, selectedSlotDuration, shift);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showAlert(getContext(), getString(R.string.success), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    finish();
                                }
                            });
                        }
                        else {
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