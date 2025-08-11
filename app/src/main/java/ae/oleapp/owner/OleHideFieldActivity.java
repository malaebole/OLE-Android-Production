package ae.oleapp.owner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingDurationAdapter;
import ae.oleapp.adapters.OleHideSlotAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityHideFieldBinding;
import ae.oleapp.dialogs.OleHideFieldDialogFragment;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleHideFieldActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityHideFieldBinding binding;
    private int selectedDateIndex = 0;
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private OleBookingDateAdapter oleBookingDateAdapter;
    private OleHideSlotAdapter slotAdapter;
    private String clubId = "", fieldId = "", date = "", slotsIds = "", hideType = "";
    private String selectedDuration = "1";
    private final List<OleBookingSlot> arrSlot = new ArrayList<>();
    private boolean isUpdate = false;
    private OleBookingDurationAdapter durationAdapter;
    private final List<OleKeyValuePair> durationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityHideFieldBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.hide_field);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            fieldId = bundle.getString("field_id", "");
            isUpdate = bundle.getBoolean("is_update", false);
            date = bundle.getString("date");
            if (bundle.containsKey("slot_ids")) {
                slotsIds = bundle.getString("slot_ids", "");
            }
            if (bundle.containsKey("type")) {
                hideType = bundle.getString("type", "");
            }
        }

        if (isUpdate) {
            binding.dateRecyclerVu.setVisibility(View.GONE);
            binding.relSelect.setVisibility(View.GONE);
            binding.btnUnhide.setVisibility(View.VISIBLE);
            binding.durRecyclerVu.setVisibility(View.GONE);
            hiddenSlotsDetailAPI();
            if (hideType.equalsIgnoreCase("tournament")) {
                binding.titleBar.toolbarTitle.setText(R.string.tournament);
            }
            else if (hideType.equalsIgnoreCase("maintenance")) {
                binding.titleBar.toolbarTitle.setText(R.string.maintenance);
            }
            else if (hideType.equalsIgnoreCase("holiday")) {
                binding.titleBar.toolbarTitle.setText(R.string.holidays);
            }
            else {
                binding.titleBar.toolbarTitle.setText(R.string.other);
            }
        }
        else {
            binding.dateRecyclerVu.setVisibility(View.VISIBLE);
            binding.relSelect.setVisibility(View.VISIBLE);
            binding.btnUnhide.setVisibility(View.GONE);
            selectedDateIndex = 0;
            date = getDateStr(new Date());
            binding.durRecyclerVu.setVisibility(View.VISIBLE);
            getSlotsAPI(date);
        }

        durationList.add(new OleKeyValuePair("1", getString(R.string.one_hour)));
        durationList.add(new OleKeyValuePair("1.5", getString(R.string.one_half_hour)));
        durationList.add(new OleKeyValuePair("2", getString(R.string.two_hour)));
        LinearLayoutManager durationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.durRecyclerVu.setLayoutManager(durationLayoutManager);
        durationAdapter = new OleBookingDurationAdapter(getContext(), durationList, selectedDuration);
        durationAdapter.setOnItemClickListener(durClickListener);
        binding.durRecyclerVu.setAdapter(durationAdapter);

        LinearLayoutManager horizLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(horizLayoutManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.listRecyclerVu.setLayoutManager(layoutManager);
        slotAdapter = new OleHideSlotAdapter(getContext(), arrSlot);
        slotAdapter.setItemClickListener(itemClickListener);
        binding.listRecyclerVu.setAdapter(slotAdapter);
        
        binding.titleBar.backBtn.setOnClickListener(this);
        binding.tvSelectAll.setOnClickListener(this);
        binding.btnUnhide.setOnClickListener(this);
        binding.btnHide.setOnClickListener(this);

    }

    OleBookingDurationAdapter.OnItemClickListener durClickListener = new OleBookingDurationAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDuration = durationList.get(pos).getKey();
            getSlotsAPI(date);
            durationAdapter.setSelectedDuration(selectedDuration);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.tvSelectAll) {
            selectAllClicked();
        }
        else if (v == binding.btnUnhide) {
            unhideClicked();
        }
        else if (v == binding.btnHide) {
            hideClicked();
        }
    }

    private void selectAllClicked() {
        slotAdapter.selectedSlots.clear();
        slotAdapter.selectedSlots.addAll(arrSlot);
        slotAdapter.notifyDataSetChanged();
        binding.tvSelected.setText(getString(R.string.place_selected, slotAdapter.selectedSlots.size()));
    }

    private void unhideClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.field))
                .setMessage(getResources().getString(R.string.do_you_want_to_unhide_all_slots))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unhideAllSlotsAPI(date);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void hideClicked() {
        if (slotAdapter.selectedSlots.size() == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("HideFieldDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleHideFieldDialogFragment dialogFragment = new OleHideFieldDialogFragment();
        dialogFragment.setDialogCallback(new OleHideFieldDialogFragment.HideFieldDialogCallback() {
            @Override
            public void didConfirm(String type, String price, String reason) {
                hideSlotsAPI(date, type, price, reason);
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "HideFieldDialogFragment");
    }

    OleHideSlotAdapter.ItemClickListener itemClickListener = new OleHideSlotAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleBookingSlot slot = arrSlot.get(pos);
            if (slot.getStatus().equalsIgnoreCase("available")) {
                slotAdapter.selectItem(slot);
                binding.tvSelected.setText(getString(R.string.place_selected, slotAdapter.selectedSlots.size()));
            }
            else {
                unhideSlot(pos);
            }
        }
    };

    private void unhideSlot(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.unhide))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            unhideSingleSlotAPI(arrSlot.get(pos).getBookingId(), pos);
                        }
                    }
                }).show();
    }

    OleBookingDateAdapter.OnItemClickListener clickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDateIndex = pos;
            oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
            date = arrDate.get(pos).getDate();
            getSlotsAPI(date);
        }
    };

    private String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    private void getSlotsAPI(String date) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getSlots(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fieldId, clubId, selectedDuration, date, "1", Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            JSONArray arrD = object.getJSONArray("dates");
                            if (!isUpdate) { // for update need to append available slots in array
                                arrSlot.clear();
                            }
                            arrDate.clear();
                            Gson gson = new Gson();
                            slotAdapter.selectedSlots.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingSlot slot = gson.fromJson(arr.get(i).toString(), OleBookingSlot.class);
                                slot.setSlotId(String.valueOf(i+1));
                                if (isUpdate) {
                                    if (slot.getStatus().equalsIgnoreCase("available")) {
                                        arrSlot.add(slot);
                                    }
                                }
                                else {
                                    if (slot.getStatus().equalsIgnoreCase("hidden") || slot.getStatus().equalsIgnoreCase("available")) {
                                        arrSlot.add(slot);
                                        if (slot.getStatus().equalsIgnoreCase("hidden")) {
                                            slotAdapter.selectedSlots.add(slot);
                                        }
                                    }
                                }
                            }
                            slotAdapter.notifyDataSetChanged();
                            binding.tvSelected.setText(getString(R.string.place_selected, slotAdapter.selectedSlots.size()));
                            if (!isUpdate) {
                                for (int i = 0; i < arrD.length(); i++) {
                                    arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                                }
                                oleBookingDateAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
                                oleBookingDateAdapter.setOnItemClickListener(clickListener);
                                binding.dateRecyclerVu.setAdapter(oleBookingDateAdapter);
                            }
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

    private void hiddenSlotsDetailAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.hiddenSlotDetails(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), slotsIds);
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
                                OleBookingSlot slot = gson.fromJson(arr.get(i).toString(), OleBookingSlot.class);
                                slot.setSlotId(String.valueOf(i+1));
                                arrSlot.add(slot);
                            }
                            slotAdapter.notifyDataSetChanged();
                            getSlotsAPI(date);
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

    private void hideSlotsAPI(String date, String type, String price, String reason) {
        String slots = "";
        try {
            JSONArray array = new JSONArray();
            for (OleBookingSlot slot : slotAdapter.selectedSlots) {
                JSONObject object = new JSONObject();
                object.put("time_start", slot.getStart());
                object.put("time_end", slot.getEnd());
                object.put("shift", slot.getShift());
                object.put("slot_status", slot.getStatus());
                array.put(object);
            }
            slots = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.hideSlots(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fieldId, slots, date, type, price, reason);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            for (OleBookingSlot slot : slotAdapter.selectedSlots) {
                                slot.setStatus("hidden");
                            }
                            slotAdapter.notifyDataSetChanged();
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

    private void unhideAllSlotsAPI(String date) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.unhideAllSlots(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fieldId, date, hideType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            for (OleBookingSlot slot : arrSlot) {
                                slot.setStatus("available");
                            }
                            slotAdapter.selectedSlots.clear();
                            binding.tvSelected.setText(getString(R.string.place_selected, slotAdapter.selectedSlots.size()));
                            slotAdapter.notifyDataSetChanged();
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

    private void unhideSingleSlotAPI(String bookingId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteBooking(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            arrSlot.get(pos).setStatus("available");
                            for (int i = 0; i < slotAdapter.selectedSlots.size(); i++) {
                                if (slotAdapter.selectedSlots.get(i).getSlotId().equalsIgnoreCase(arrSlot.get(pos).getSlotId())) {
                                    slotAdapter.selectedSlots.remove(i);
                                    break;
                                }
                            }
                            binding.tvSelected.setText(getString(R.string.place_selected, slotAdapter.selectedSlots.size()));
                            slotAdapter.notifyDataSetChanged();
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
