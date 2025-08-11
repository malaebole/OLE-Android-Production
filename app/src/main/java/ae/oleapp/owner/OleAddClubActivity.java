package ae.oleapp.owner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubDayAdapter;
import ae.oleapp.adapters.OleClubFacilityListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAddClubBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.OleShiftTime;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import mumayank.com.airlocationlibrary.AirLocation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OleAddClubActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private OleactivityAddClubBinding binding;
    private EasyImage easyImage;
    private ImageType imageType;
    private File logoImage, coverImage;
    private List<OleKeyValuePair> daysList = new ArrayList<>();
    private String countryId = "";
    private String cityId = "";
    private String currentDayId = "";
    private String slotOneHour = "1";
    private String slotOneHalfHour = "1";
    private String slotTwoHour = "1";
    private String clubType = "";
    private OleClubDayAdapter dayAdapter;
    private double latitude = 0, longitude = 0;
    private OleClubFacilityListAdapter facilityAdapter;
    private boolean isForUpdate = false;
    private Club club;

    public enum ImageType {
        logo,
        cover
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddClubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForUpdate = bundle.getBoolean("is_edit", false);
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
        }

        daysList = Functions.getDays(getContext());
        binding.vuShift2.setVisibility(View.GONE);

        if (AppManager.getInstance().clubFacilities.size() == 0) {
            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
            getFacilityAPI(new FacilityCallback() {
                @Override
                public void facilities(List<OleClubFacility> facilities) {
                    Functions.hideLoader(hud);
                    AppManager.getInstance().clubFacilities.clear();
                    AppManager.getInstance().clubFacilities.addAll(facilities);
                    facilityAdapter.notifyDataSetChanged();
                }
            });
        }

        LinearLayoutManager dayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(dayLayoutManager);
        dayAdapter = new OleClubDayAdapter(getContext(), daysList);
        dayAdapter.setOnItemClickListener(dayClickListener);
        binding.daysRecyclerVu.setAdapter(dayAdapter);

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        facilityAdapter = new OleClubFacilityListAdapter(getContext(), AppManager.getInstance().clubFacilities);
        facilityAdapter.setOnItemClickListener(facClickListener);
        binding.facRecyclerVu.setAdapter(facilityAdapter);

        if (isForUpdate) {
            binding.bar.toolbarTitle.setText(R.string.club_details);
            binding.tvBtntitle.setText(R.string.update);
            populateData();
        }
        else {
            binding.bar.toolbarTitle.setText(R.string.add_club);
            binding.tvBtntitle.setText(R.string.add_now);

            new AirLocation(getContext(), true, true, new AirLocation.Callbacks() {
                @Override
                public void onSuccess(@NotNull Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Functions.getAddressFromLocation(location, getContext(), new GeocoderHandler());
                }

                @Override
                public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {

                }
            });
        }

        basicVuClicked();

        checkKeyboardListener();

        binding.bar.backBtn.setOnClickListener(this);
        binding.imgVuLogo.setOnClickListener(this);
        binding.imgVuBanner.setOnClickListener(this);
        binding.relOneHour.setOnClickListener(this);
        binding.relOneHalfHour.setOnClickListener(this);
        binding.relTwoHour.setOnClickListener(this);
        binding.relApplyEveryday.setOnClickListener(this);
        binding.btnAddMore.setOnClickListener(this);
        binding.btnRemove.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
        binding.etCountry.setOnClickListener(this);
        binding.etLoc.setOnClickListener(this);
        binding.etOpenTime1.setOnClickListener(this);
        binding.etOpenTime2.setOnClickListener(this);
        binding.etCloseTime1.setOnClickListener(this);
        binding.etCloseTime2.setOnClickListener(this);
        binding.hourSwitch.setOnCheckedChangeListener(this);
        binding.everydaySwitch.setOnCheckedChangeListener(this);
        binding.basicVu.setOnClickListener(this);
        binding.timeVu.setOnClickListener(this);
        binding.footballVu.setOnClickListener(this);
        binding.padelVu.setOnClickListener(this);
    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    binding.btnAdd.setVisibility(View.GONE);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //your code here
                                    binding.btnAdd.setVisibility(View.VISIBLE);
                                }
                            }, 50);
                        }
                    });
                }
            }
        });
    }

    private void populateData() {
        if (!club.getLogoPath().isEmpty()) {
            Glide.with(getApplicationContext()).load(club.getLogoPath()).into(binding.imgVuLogo);
        }
        if (!club.getCoverPath().isEmpty()) {
            Glide.with(getApplicationContext()).load(club.getCoverPath()).into(binding.imgVuBanner);
        }
        binding.etClubName.setText(club.getName());
        countryId = club.getCountry().getId();
        binding.etCountry.setText(club.getCountry().getName());
        cityId = club.getCity().getId();
        binding.etCity.setText(club.getCity().getName());
        binding.etPhone.setText(club.getContact());
        if (!club.getLatitude().equalsIgnoreCase("") && !club.getLongitude().equalsIgnoreCase("")) {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            latitude = Double.parseDouble(club.getLatitude());
            longitude = Double.parseDouble(club.getLongitude());
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            Functions.getAddressFromLocation(location, getContext(), new GeocoderHandler());
        }
        else {
            binding.etLoc.setText("");
        }
        dayAdapter.selectedDays.clear();
        dayAdapter.selectedDays.addAll(club.getTimings());
        dayAdapter.notifyDataSetChanged();
        if (club.getSlots60().equalsIgnoreCase("1")) {
            slotOneHour = "1";
            binding.imgVuOneHour.setImageResource(R.drawable.check);
        }
        else {
            slotOneHour = "0";
            binding.imgVuOneHour.setImageResource(R.drawable.uncheck);
        }
        if (club.getSlots90().equalsIgnoreCase("1")) {
            slotOneHalfHour = "1";
            binding.imgVuOneHalfHour.setImageResource(R.drawable.check);
        }
        else {
            slotOneHalfHour = "0";
            binding.imgVuOneHalfHour.setImageResource(R.drawable.uncheck);
        }
        if (club.getSlots120().equalsIgnoreCase("1")) {
            slotTwoHour = "1";
            binding.imgVuTwoHour.setImageResource(R.drawable.check);
        }
        else {
            slotTwoHour = "0";
            binding.imgVuTwoHour.setImageResource(R.drawable.uncheck);
        }
        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
            padelClicked();
        }
        else {
            footballClicked();
        }
        facilityAdapter.selectedFacility.clear();
        facilityAdapter.selectedFacility.addAll(club.getFacilities());
        facilityAdapter.notifyDataSetChanged();
    }

    OleClubFacilityListAdapter.OnItemClickListener facClickListener = new OleClubFacilityListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleClubFacility facility = AppManager.getInstance().clubFacilities.get(pos);
            int index = facilityAdapter.isExistInSelected(facility.getId());
            if (index == -1) {
                facilityAdapter.setCurrentSelectedPosition(pos);
            }
            else {
                facilityAdapter.selectedFacility.remove(index);
                facilityAdapter.notifyDataSetChanged();
            }
        }
    };

    OleClubDayAdapter.OnItemClickListener dayClickListener = new OleClubDayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {

            int dayIndex = dayAdapter.checkDayExist(currentDayId);
            if (dayIndex != -1) {
                Day day = dayAdapter.selectedDays.get(dayIndex);
                // first check empty fields for shift before move to next day
                if (day.getShifting().size() == 2) {
                    OleShiftTime oleShiftTime = day.getShifting().get(1);
                    if (oleShiftTime.getOpening() == null || oleShiftTime.getClosing() == null) {
                        Functions.showToast(getContext(), getString(R.string.select_2nd_shift), FancyToast.ERROR);
                        return;
                    }
                }
                if (day.getShifting().size() == 1) {
                    OleShiftTime oleShiftTime = day.getShifting().get(0);
                    if (oleShiftTime.getOpening() == null || oleShiftTime.getClosing() == null) {
                        Functions.showToast(getContext(), getString(R.string.select_1st_shift), FancyToast.ERROR);
                        return;
                    }
                }
            }
                /////////////////////////// end ///////////////////////////

            OleKeyValuePair dayPair = daysList.get(pos);
            currentDayId = dayPair.getKey();
            dayAdapter.setCurrentDayId(currentDayId);
            dayIndex = dayAdapter.checkDayExist(currentDayId);
            if (dayIndex != -1) {
                Day day = dayAdapter.selectedDays.get(dayIndex);

                if (day.is24Hours()) {
                    binding.hourSwitch.setChecked(true);
                    binding.vuShift1.setVisibility(View.GONE);
                    binding.vuShift2.setVisibility(View.GONE);
                    binding.addRemoveShiftVu.setVisibility(View.GONE);
                }
                else if (day.getShifting().size() == 1) {
                    binding.hourSwitch.setChecked(false);
                    OleShiftTime oleShiftTime = day.getShifting().get(0);
                    binding.etOpenTime1.setText(oleShiftTime.getOpening());
                    binding.etCloseTime1.setText(oleShiftTime.getClosing());
                    binding.etOpenTime2.setText("");
                    binding.etCloseTime2.setText("");
                    binding.vuShift1.setVisibility(View.VISIBLE);
                    binding.vuShift2.setVisibility(View.GONE);
                }
                else if (day.getShifting().size() == 2) {
                    binding.hourSwitch.setChecked(false);
                    OleShiftTime oleShiftTime = day.getShifting().get(0);
                    binding.etOpenTime1.setText(oleShiftTime.getOpening());
                    binding.etCloseTime1.setText(oleShiftTime.getClosing());
                    OleShiftTime oleShiftTime2 = day.getShifting().get(1);
                    binding.etOpenTime2.setText(oleShiftTime2.getOpening());
                    binding.etCloseTime2.setText(oleShiftTime2.getClosing());
                    binding.vuShift1.setVisibility(View.VISIBLE);
                    binding.vuShift2.setVisibility(View.VISIBLE);
                }
                else {
                    binding.hourSwitch.setChecked(false);
                    binding.etOpenTime1.setText("");
                    binding.etCloseTime1.setText("");
                    binding.etOpenTime2.setText("");
                    binding.etCloseTime2.setText("");
                    binding.vuShift1.setVisibility(View.VISIBLE);
                    binding.vuShift2.setVisibility(View.GONE);
                    binding.addRemoveShiftVu.setVisibility(View.VISIBLE);
                }
            }
            else {
                Day day = new Day();
                day.setDayId(dayPair.getKey());
                day.setIs24Hours(false);
                binding.hourSwitch.setChecked(false);
                day.setShifting(new ArrayList<>());
                dayAdapter.selectedDays.add(day);
                binding.etOpenTime1.setText("");
                binding.etCloseTime1.setText("");
                binding.etOpenTime2.setText("");
                binding.etCloseTime2.setText("");
                binding.vuShift1.setVisibility(View.VISIBLE);
                binding.addRemoveShiftVu.setVisibility(View.VISIBLE);
                binding.vuShift2.setVisibility(View.GONE);
            }
            dayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.imgVuLogo) {
            logoClicked();
        }
        else if (v == binding.imgVuBanner) {
            bannerClicked();
        }
        else if (v == binding.relOneHour) {
            relOneHourClicked();
        }
        else if (v == binding.relOneHalfHour) {
            relOneHalfHourClicked();
        }
        else if (v == binding.relTwoHour) {
            relTwoClicked();
        }
        else if (v == binding.relApplyEveryday) {
            appEverydayClicked();
        }
        else if (v == binding.btnAddMore) {
            addMoreClicked();
        }
        else if (v == binding.btnRemove) {
            removeClicked();
        }
        else if (v == binding.btnAdd) {
            addClubClicked();
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
        else if (v == binding.etCountry) {
            countryClicked();
        }
        else if (v == binding.etLoc) {
            locationClicked();
        }
        else if (v == binding.etOpenTime1 || v == binding.etOpenTime2 || v == binding.etCloseTime1 || v == binding.etCloseTime2) {
            timeClicked(v);
        }
        else if (v == binding.basicVu) {
            if (binding.timeDetailVu.getVisibility() == View.VISIBLE || binding.facRecyclerVu.getVisibility() == View.VISIBLE) {
                basicVuClicked();
            }
        }
        else if (v == binding.timeVu) {
            if (binding.facRecyclerVu.getVisibility() == View.VISIBLE) {
                timeVuClicked();
            }
        }
        else if (v == binding.footballVu) {
            footballClicked();
        }
        else if (v == binding.padelVu) {
            padelClicked();
        }
    }

    private void footballClicked() {
        clubType = Constants.kFootballModule;
        binding.footballVu.setStrokeColor(getResources().getColor(R.color.blueColorNew));
        binding.padelVu.setStrokeColor(getResources().getColor(R.color.separatorColor));
        binding.tvFootball.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.tvPadel.setTextColor(getResources().getColor(R.color.subTextColor));
        binding.imgFootball.setImageResource(R.drawable.football_ac);
        binding.imgPadel.setImageResource(R.drawable.padel_de);
        binding.footballVu.invalidate();
        binding.padelVu.invalidate();
    }

    private void padelClicked() {
        clubType = Constants.kPadelModule;
        binding.footballVu.setStrokeColor(getResources().getColor(R.color.separatorColor));
        binding.padelVu.setStrokeColor(getResources().getColor(R.color.blueColorNew));
        binding.tvFootball.setTextColor(getResources().getColor(R.color.subTextColor));
        binding.tvPadel.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.imgFootball.setImageResource(R.drawable.football_de);
        binding.imgPadel.setImageResource(R.drawable.padel_ac);
        binding.footballVu.invalidate();
        binding.padelVu.invalidate();
    }

    private void basicVuClicked() {
        binding.tvBtntitle.setText(R.string.next);
        binding.basicDetailVu.setVisibility(View.VISIBLE);
        binding.timeDetailVu.setVisibility(View.GONE);
        binding.facRecyclerVu.setVisibility(View.GONE);
        binding.basicImgVu.setImageResource(R.drawable.selected_basic);
        binding.timeImgVu.setImageResource(R.drawable.unselected_club_time);
        binding.facImgVu.setImageResource(R.drawable.unselected_club_fac);
    }

    private void timeVuClicked() {
        binding.tvBtntitle.setText(R.string.next);
        binding.basicDetailVu.setVisibility(View.GONE);
        binding.timeDetailVu.setVisibility(View.VISIBLE);
        binding.facRecyclerVu.setVisibility(View.GONE);
        binding.basicImgVu.setImageResource(R.drawable.selected_basic);
        binding.timeImgVu.setImageResource(R.drawable.selected_club_time);
        binding.facImgVu.setImageResource(R.drawable.unselected_club_fac);
    }

    private void facVuClicked() {
        if (isForUpdate) {
            binding.tvBtntitle.setText(R.string.update);
        }
        else {
            binding.tvBtntitle.setText(R.string.add_now);
        }
        binding.basicDetailVu.setVisibility(View.GONE);
        binding.timeDetailVu.setVisibility(View.GONE);
        binding.facRecyclerVu.setVisibility(View.VISIBLE);
        binding.basicImgVu.setImageResource(R.drawable.selected_basic);
        binding.timeImgVu.setImageResource(R.drawable.selected_club_time);
        binding.facImgVu.setImageResource(R.drawable.selected_club_fac);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == binding.everydaySwitch) {
            appEverydayClicked();
        }
        else if (buttonView == binding.hourSwitch) {
            hourSwitchClicked(binding.hourSwitch);
        }
    }

    private void logoClicked() {
        imageType = ImageType.logo;
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            pickImage();
                        }
                        else if (index == 1) {
                            logoImage = null;
                            binding.imgVuLogo.setImageResource(R.drawable.add_image);
                        }
                    }
                }).show();
    }

    private void bannerClicked() {
        imageType = ImageType.cover;
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            pickImage();
                        }
                        else if (index == 1) {
                            coverImage = null;
                            binding.imgVuBanner.setImageDrawable(null);
                        }
                    }
                }).show();
    }

    private void relOneHourClicked() {
        if (slotOneHour.equalsIgnoreCase("1")) {
            slotOneHour = "0";
            binding.imgVuOneHour.setImageResource(R.drawable.uncheck);
        }
        else {
            slotOneHour = "1";
            binding.imgVuOneHour.setImageResource(R.drawable.check);
        }
    }

    private void relOneHalfHourClicked() {
        if (slotOneHalfHour.equalsIgnoreCase("1")) {
            slotOneHalfHour = "0";
            binding.imgVuOneHalfHour.setImageResource(R.drawable.uncheck);
        }
        else {
            slotOneHalfHour = "1";
            binding.imgVuOneHalfHour.setImageResource(R.drawable.check);
        }
    }

    private void relTwoClicked() {
        if (slotTwoHour.equalsIgnoreCase("1")) {
            slotTwoHour = "0";
            binding.imgVuTwoHour.setImageResource(R.drawable.uncheck);
        }
        else {
            slotTwoHour = "1";
            binding.imgVuTwoHour.setImageResource(R.drawable.check);
        }
    }

    private void hourSwitchClicked(SwitchCompat aSwitch) {
        if (currentDayId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_day), FancyToast.ERROR);
            aSwitch.setChecked(!aSwitch.isChecked());
            return;
        }
        int index = dayAdapter.checkDayExist(currentDayId);
        if (index == -1) {
            return;
        }
        binding.everydaySwitch.setChecked(false);
        Day day = dayAdapter.selectedDays.get(index);
        if (aSwitch.isChecked()) {
            day.setIs24Hours(true);
            day.getShifting().clear();
            OleShiftTime oleShiftTime = new OleShiftTime();
            oleShiftTime.setOpening("12:00AM");
            oleShiftTime.setClosing("12:00PM");
            day.getShifting().add(oleShiftTime);
            oleShiftTime = new OleShiftTime();
            oleShiftTime.setOpening("12:00PM");
            oleShiftTime.setClosing("12:00AM");
            day.getShifting().add(oleShiftTime);
            binding.vuShift1.setVisibility(View.GONE);
            binding.vuShift2.setVisibility(View.GONE);
            binding.addRemoveShiftVu.setVisibility(View.GONE);
        }
        else {
            day.setIs24Hours(false);
            day.getShifting().clear();
            binding.etOpenTime1.setText("");
            binding.etCloseTime1.setText("");
            binding.etOpenTime2.setText("");
            binding.etCloseTime2.setText("");
            binding.vuShift1.setVisibility(View.VISIBLE);
            binding.addRemoveShiftVu.setVisibility(View.VISIBLE);
            binding.vuShift2.setVisibility(View.GONE);
        }
    }

    private void appEverydayClicked() {
        if (currentDayId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_day), FancyToast.ERROR);
            binding.everydaySwitch.setChecked(!binding.everydaySwitch.isChecked());
            return;
        }
        if (binding.everydaySwitch.isChecked()) {
            if (binding.hourSwitch.isChecked()) {
                dayAdapter.selectedDays.clear();
                for (int i = 0; i < 7; i++) {
                    Day day = new Day();
                    day.setDayId(String.valueOf(i+1));
                    day.setIs24Hours(true);
                    OleShiftTime oleShiftTime = new OleShiftTime();
                    oleShiftTime.setOpening("12:00AM");
                    oleShiftTime.setClosing("12:00PM");
                    day.getShifting().add(oleShiftTime);
                    oleShiftTime = new OleShiftTime();
                    oleShiftTime.setOpening("12:00PM");
                    oleShiftTime.setClosing("12:00AM");
                    day.getShifting().add(oleShiftTime);
                    dayAdapter.selectedDays.add(day);
                }
            }
            else {
                int index = dayAdapter.checkDayExist(currentDayId);
                Day dayData = dayAdapter.selectedDays.get(index);
                if (dayData.getShifting().size() == 0) {
                    Functions.showToast(getContext(), getString(R.string.select_time), FancyToast.ERROR);
                    binding.everydaySwitch.setChecked(false);
                    return;
                }
                dayAdapter.selectedDays.clear();
                for (int i = 0; i < 7; i++) {
                    Day day = new Day();
                    day.setDayId(String.valueOf(i+1));
                    day.setIs24Hours(false);
                    if (dayData.getShifting().size() == 2) {
                        OleShiftTime oleShiftTime = new OleShiftTime();
                        oleShiftTime.setOpening(dayData.getShifting().get(0).getOpening());
                        oleShiftTime.setClosing(dayData.getShifting().get(0).getClosing());
                        day.getShifting().add(oleShiftTime);
                        oleShiftTime = new OleShiftTime();
                        oleShiftTime.setOpening(dayData.getShifting().get(1).getOpening());
                        oleShiftTime.setClosing(dayData.getShifting().get(1).getClosing());
                        day.getShifting().add(oleShiftTime);
                        dayAdapter.selectedDays.add(day);
                    }
                    else if (dayData.getShifting().size() == 1) {
                        OleShiftTime oleShiftTime = new OleShiftTime();
                        oleShiftTime.setOpening(dayData.getShifting().get(0).getOpening());
                        oleShiftTime.setClosing(dayData.getShifting().get(0).getClosing());
                        day.getShifting().add(oleShiftTime);
                        dayAdapter.selectedDays.add(day);
                    }
                }
            }
            dayAdapter.notifyDataSetChanged();
        }
    }

    private void addMoreClicked() {
        int index = dayAdapter.checkDayExist(currentDayId);
        if (index != -1) {
            Day day = dayAdapter.selectedDays.get(index);
            if (day.getShifting().size() == 2) {
                OleShiftTime oleShiftTime = day.getShifting().get(1);
                if (oleShiftTime.getOpening() == null || oleShiftTime.getClosing() == null) {
                    Functions.showToast(getContext(), getString(R.string.select_2nd_shift), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.add_max_two_shift), FancyToast.ERROR);
                }
            }
            else if (day.getShifting().size() == 1) {
                OleShiftTime oleShiftTime = day.getShifting().get(0);
                if (oleShiftTime.getOpening() == null || oleShiftTime.getClosing() == null) {
                    Functions.showToast(getContext(), getString(R.string.select_1st_shift), FancyToast.ERROR);
                }
                else {
                    binding.vuShift2.setVisibility(View.VISIBLE);
                }
            }
            else if (day.getShifting().size() == 0) {
                Functions.showToast(getContext(), getString(R.string.select_1st_shift), FancyToast.ERROR);
            }
        }
        else {
            Functions.showToast(getContext(), getString(R.string.select_day), FancyToast.ERROR);
        }
    }

    private void removeClicked() {
        int index = dayAdapter.checkDayExist(currentDayId);
        if (index != -1) {
            Day day = dayAdapter.selectedDays.get(index);
            if (day.getShifting().size() == 2) {
                binding.etOpenTime2.setText("");
                binding.etCloseTime2.setText("");
                day.getShifting().remove(1);
                binding.vuShift2.setVisibility(View.GONE);
            }
            else if (day.getShifting().size() == 1) {
                if (binding.vuShift2.getVisibility() == View.VISIBLE) {
                    binding.vuShift2.setVisibility(View.GONE);
                }
                else {
                    binding.etOpenTime1.setText("");
                    binding.etCloseTime1.setText("");
                    day.getShifting().remove(0);
                    binding.vuShift2.setVisibility(View.GONE);
                }
            }
            else if (day.getShifting().size() == 0) {
                binding.etOpenTime1.setText("");
                binding.etCloseTime1.setText("");
                binding.vuShift2.setVisibility(View.GONE);
            }
        }
        else {
            binding.etOpenTime1.setText("");
            binding.etCloseTime1.setText("");
            binding.etOpenTime2.setText("");
            binding.etCloseTime2.setText("");
        }
    }

    private void addClubClicked() {
        if (binding.basicDetailVu.getVisibility() == View.VISIBLE) {
            if (!isForUpdate && coverImage == null) {
                Functions.showToast(getContext(), getString(R.string.add_cover_image), FancyToast.ERROR);
                return;
            }
            if (clubType.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_club_type), FancyToast.ERROR);
                return;
            }
            if (binding.etClubName.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_club_name), FancyToast.ERROR);
                return;
            }
            if (Functions.isArabic(binding.etClubName.getText().toString())) {
                Functions.showToast(getContext(), getString(R.string.enter_club_name_english), FancyToast.ERROR);
                return;
            }
            if (countryId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
                return;
            }
            if (cityId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_city), FancyToast.ERROR);
                return;
            }
            if (latitude == 0 || longitude == 0) {
                Functions.showToast(getContext(), getString(R.string.select_location), FancyToast.ERROR);
                return;
            }
            binding.basicCheckVu.setImageResource(R.drawable.blue_check);
            timeVuClicked();
        }
        else if (binding.timeDetailVu.getVisibility() == View.VISIBLE) {
            if (dayAdapter.selectedDays.size() == 0) {
                Functions.showToast(getContext(), getString(R.string.select_open_close_time), FancyToast.ERROR);
                return;
            }
            if (slotOneHour.equalsIgnoreCase("0") && slotOneHalfHour.equalsIgnoreCase("0") && slotTwoHour.equalsIgnoreCase("0")) {
                Functions.showToast(getContext(), getString(R.string.select_booking_time_dur), FancyToast.ERROR);
                return;
            }
            binding.timeCheckVu.setImageResource(R.drawable.blue_check);
            facVuClicked();
        }
        else if (binding.facRecyclerVu.getVisibility() == View.VISIBLE) {
            binding.facCheckVu.setImageResource(R.drawable.blue_check);
            String facilities = "";
            try {
                JSONArray array = new JSONArray();
                for (OleClubFacility facility : facilityAdapter.selectedFacility) {
                    JSONObject object = new JSONObject();
                    object.put("fac_id", facility.getId());
                    object.put("price", facility.getPrice());
                    object.put("unit", facility.getUnit());
                    object.put("type", facility.getType());
                    object.put("max_quantity", facility.getMaxQuantity());
                    array.put(object);
                }
                facilities = array.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String timing = getTimeJson();

            addClubApi(binding.etClubName.getText().toString(), facilities, timing, binding.etPhone.getText().toString());
        }
    }

    private String getTimeJson() {
        String timing = "";
        try {
            JSONArray array = new JSONArray();
            for (Day day : dayAdapter.selectedDays) {
                if (day.getShifting().size()>0) {
                    JSONObject object = new JSONObject();
                    object.put("day_id", day.getDayId());
                    object.put("day_name", "");
                    JSONArray timeArray = new JSONArray();
                    for (OleShiftTime oleShiftTime : day.getShifting()) {
                        JSONObject obj = new JSONObject();
                        obj.put("opening", oleShiftTime.getOpening());
                        obj.put("closing", oleShiftTime.getClosing());
                        timeArray.put(obj);
                    }
                    object.put("shifting", timeArray);
                    array.put(object);
                }
            }
            timing = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return timing;
    }

    private void countryClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        if (AppManager.getInstance().countries.size() == 0) {
            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
            getCountriesAPI(new CountriesCallback() {
                @Override
                public void getCountries(List<OleCountry> countries) {
                    hud.dismiss();
                    AppManager.getInstance().countries = countries;
                    countryClicked();
                }
            });
        }
        else {
            for (OleCountry oleCountry : AppManager.getInstance().countries) {
                oleSelectionList.add(new OleSelectionList(oleCountry.getId(), oleCountry.getName()));
            }
            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_country), false);
            dialog.setLists(oleSelectionList);

            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<OleSelectionList> selectedItems) {
                    OleSelectionList selectedItem = selectedItems.get(0);
                    countryId = selectedItem.getId();
                    binding.etCountry.setText(selectedItem.getValue());
                    cityId = "";
                    binding.etCity.setText("");
                }
            });
            dialog.show();
        }
    }

    private void cityClicked() {
        if (countryId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
            return;
        }
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleCountry oleCountry : AppManager.getInstance().countries) {
            if (oleCountry.getId().equalsIgnoreCase(countryId)) {
                for (OleCountry city : oleCountry.getCities()) {
                    oleSelectionList.add(new OleSelectionList(city.getId(), city.getName()));
                }
                break;
            }
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                cityId = selectedItem.getId();
                binding.etCity.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    private void locationClicked() {
        Intent intent = new Intent(getContext(), OleMapActivity.class);
        startActivityForResult(intent, 112);
    }

    private void timeClicked(View clickedView) {
        if (dayAdapter.selectedDays.size() == 0 || currentDayId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_day), FancyToast.ERROR);
            return;
        }
        binding.everydaySwitch.setChecked(false);
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
                int dayIndex = dayAdapter.checkDayExist(currentDayId);
                if (dayIndex != -1) {
                    Day day = dayAdapter.selectedDays.get(dayIndex);
                    if (clickedView.getId() == R.id.et_open_time_1) {
                        String str = formatter.format(calendar.getTime());
                        binding.etOpenTime1.setText(str);
                        if (day.getShifting().size() == 0) {
                            OleShiftTime oleShiftTime = new OleShiftTime();
                            oleShiftTime.setOpening(str);
                            day.getShifting().add(oleShiftTime);
                        }
                        else {
                            day.getShifting().get(0).setOpening(str);
                        }
                    }
                    else if (clickedView.getId() == R.id.et_close_time_1) {
                        String str = formatter.format(calendar.getTime());
                        binding.etCloseTime1.setText(str);
                        if (day.getShifting().size() == 0) {
                            OleShiftTime oleShiftTime = new OleShiftTime();
                            oleShiftTime.setClosing(str);
                            day.getShifting().add(oleShiftTime);
                        }
                        else {
                            day.getShifting().get(0).setClosing(str);
                        }
                    }
                    else if (clickedView.getId() == R.id.et_open_time_2) {
                        String str = formatter.format(calendar.getTime());
                        binding.etOpenTime2.setText(str);
                        if (day.getShifting().size() == 1) {
                            OleShiftTime oleShiftTime = new OleShiftTime();
                            oleShiftTime.setOpening(str);
                            day.getShifting().add(oleShiftTime);
                        }
                        else {
                            day.getShifting().get(1).setOpening(str);
                        }
                    }
                    else if (clickedView.getId() == R.id.et_close_time_2) {
                        String str = formatter.format(calendar.getTime());
                        binding.etCloseTime2.setText(str);
                        if (day.getShifting().size() == 1) {
                            OleShiftTime oleShiftTime = new OleShiftTime();
                            oleShiftTime.setClosing(str);
                            day.getShifting().add(oleShiftTime);
                        }
                        else {
                            day.getShifting().get(1).setClosing(str);
                        }
                    }
                }
            }
        }, hour, minute, false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.setTimeInterval(1, 30);
        timePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private void pickImage() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                easyImage = new EasyImage.Builder(getContext())
                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                        .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false).build();
                easyImage.openChooser(OleAddClubActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(resultUri.getPath());
                if (imageType == ImageType.logo) {
                    logoImage = file;
                    Glide.with(getApplicationContext()).load(file).into(binding.imgVuLogo);
                }
                else {
                    coverImage = file;
                    Glide.with(getApplicationContext()).load(file).into(binding.imgVuBanner);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else if (requestCode == 112 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            latitude = bundle.getDouble("lat");
            longitude = bundle.getDouble("lng");
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            Functions.getAddressFromLocation(location, getContext(), new GeocoderHandler());
        }
        else {
            if (easyImage == null) {
                return;
            }
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.ActivityBuilder builder = CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
                        if (imageType == ImageType.logo) {
                            builder.setAspectRatio(1,1);
                        }
                        else {
                            builder.setAspectRatio(4,2);
                        }
                        builder.start(OleAddClubActivity.this);
                    }
                }

                @Override
                public void onImagePickerError(Throwable error, MediaSource source) {
                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                }

                @Override
                public void onCanceled(@NonNull MediaSource mediaSource) {

                }
            });
        }
    }

    private void addClubApi(String name, String facIds, String timings, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part coverPart = null;
        if (coverImage != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
            coverPart = MultipartBody.Part.createFormData("cover", coverImage.getName(), fileReqBody);
        }
        MultipartBody.Part logoPart = null;
        if (logoImage != null) {
            RequestBody logoReqBody = RequestBody.create(MediaType.parse("image/*"), logoImage);
            logoPart = MultipartBody.Part.createFormData("logo", logoImage.getName(), logoReqBody);
        }
        String url = "add_club";
        String clubId = "";
        if (isForUpdate) {
            url = "update_club";
            clubId = club.getId();
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addClub(url, coverPart, logoPart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), ""),
                RequestBody.create(MediaType.parse("text/plain"), countryId),
                RequestBody.create(MediaType.parse("text/plain"), cityId),
                RequestBody.create(MediaType.parse("text/plain"), facIds),
                RequestBody.create(MediaType.parse("text/plain"), timings),
                RequestBody.create(MediaType.parse("text/plain"), phone),
                RequestBody.create(MediaType.parse("text/plain"), String.format(Locale.ENGLISH, "%f,%f", latitude, longitude)),
                RequestBody.create(MediaType.parse("text/plain"), "1"),
                RequestBody.create(MediaType.parse("text/plain"), slotOneHour),
                RequestBody.create(MediaType.parse("text/plain"), slotOneHalfHour),
                RequestBody.create(MediaType.parse("text/plain"), slotTwoHour),
                RequestBody.create(MediaType.parse("text/plain"), "1"),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), clubType));
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

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result = "";
            if (message.what == 1) {
                Bundle bundle = message.getData();
                result = bundle.getString("address");
            } else {
                result = "";
            }
            // replace by what you need to do
            binding.etLoc.setText(result);
        }
    }
}
