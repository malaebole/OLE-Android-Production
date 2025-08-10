package ae.oleapp.owner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubDayAdapter;
import ae.oleapp.adapters.OleHideSlotAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityGirlsTimeSlotsBinding;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleGirlsTimeSlotsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityGirlsTimeSlotsBinding binding;
    private String clubId = "", fieldId = "";
    private final List<OleBookingSlot> slotList = new ArrayList<>();
    private List<OleKeyValuePair> dayList = new ArrayList<>();
    private OleClubDayAdapter dayAdapter;
    private OleHideSlotAdapter slotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityGirlsTimeSlotsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.girls_time_slots);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            fieldId = bundle.getString("field_id", "");
        }

        dayList = Functions.getDays(getContext());

        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        dayAdapter = new OleClubDayAdapter(getContext(), dayList);
        dayAdapter.setCurrentDayId(dayList.get(0).getKey());
        dayAdapter.setOnItemClickListener(onItemClickListener);
        binding.daysRecyclerVu.setAdapter(dayAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        slotAdapter = new OleHideSlotAdapter(getContext(), slotList);
        slotAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(slotAdapter);

        getSlotsAPI(dayList.get(0).getKey());

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.bar.backBtn) {
            finish();
        }
        else if (view == binding.btnAdd) {
            addClicked();
        }
    }

    private void addClicked() {
        String slots = "";
        if (slotAdapter.selectedSlots.size() > 0) {
            try {
                JSONArray array = new JSONArray();
                for (OleBookingSlot slot : slotAdapter.selectedSlots) {
                    JSONObject object = new JSONObject();
                    object.put("time_start", slot.getStart());
                    object.put("time_end", slot.getEnd());
                    array.put(object);
                }
                slots = array.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        saveSlotsAPI(dayAdapter.getCurrentDayId(), slots);
    }

    OleClubDayAdapter.OnItemClickListener onItemClickListener = new OleClubDayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleKeyValuePair day = dayList.get(pos);
            dayAdapter.setCurrentDayId(day.getKey());
            dayAdapter.notifyDataSetChanged();
            getSlotsAPI(day.getKey());
        }
    };

    OleHideSlotAdapter.ItemClickListener itemClickListener = new OleHideSlotAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleBookingSlot slot = slotList.get(pos);
            slotAdapter.selectItem(slot);
        }
    };

    private void getSlotsAPI(String dayId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllSlots(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fieldId, clubId, dayId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            slotList.clear();
                            slotAdapter.selectedSlots.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingSlot slot = gson.fromJson(arr.get(i).toString(), OleBookingSlot.class);
                                slot.setSlotId(String.valueOf(i+1));
                                slotList.add(slot);
                                if (slot.getIsSelected().equalsIgnoreCase("1")) {
                                    slotAdapter.selectedSlots.add(slot);
                                }
                            }
                            if (slotAdapter.selectedSlots.size() > 0) {
                                binding.btnAdd.setTitle(R.string.update);
                            }
                            else {
                                binding.btnAdd.setTitle(R.string.add_now);
                            }
                            slotAdapter.notifyDataSetChanged();
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

    private void saveSlotsAPI(String days, String slots) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.saveLadiesSlots(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fieldId, clubId, days, slots);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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