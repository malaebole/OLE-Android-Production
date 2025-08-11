package ae.oleapp.owner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.activities.MainActivity;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingDurationAdapter;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleBookingSlotAdapter;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBookingBinding;
import ae.oleapp.dialogs.OleBookingAlertDialogFragment;
import ae.oleapp.dialogs.OleBookingDoneDialog;
import ae.oleapp.dialogs.OleClubListDialogFragment;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.fragments.AddWaitingUserDialogFragment;
import ae.oleapp.fragments.showWaitingUsersListDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleFieldPrice;
import ae.oleapp.models.OleImageData;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleOfferData;
import ae.oleapp.padel.OleCreatePadelMatchActivity;
import ae.oleapp.player.OleCreateMatchActivity;
import ae.oleapp.player.OlePBookingInfoActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingBinding binding;
    private Club club;
    private String fieldId;
    private final List<Field> fieldList = new ArrayList<>();
    private List<OleClubFacility> facilityList = new ArrayList<>();
    private Field fieldDetail;
    private String selectedDuration = "";
    private String selectedSlotDuration = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private String selectedSlotTime = "";
    private String selectedShift = "";
    private String selectedDate = "";
    private String price = "";
    private String offerDiscountType = "";
    private String isWaitingUser = "0";
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private int selectedDateIndex = 0;
    private int selectedSlotIndex = -1;
    private final List<OleBookingSlot> arrSlot = new ArrayList<>();
    private final String kBookingFormat = "yyyy-MM-dd";
    private double offerDiscount = 0;
    private double selectedFacilityPrice = 0;
    private OleFacilityAdapter oleFacilityAdapter;
    private OleBookingFieldAdapter fieldAdapter;
    private OleBookingDurationAdapter durationAdapter;
    private OleBookingDateAdapter daysAdapter;
    private OleBookingSlotAdapter slotAdapter;
    private String bookingPaymentMethod = "";
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private String padelPlayers = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            setTheme(R.style.AppThemePlayer);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(getString(R.string.booking));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fieldId = bundle.getString("field_id", "");
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
        }

        binding.btnBook.setBackgroundImage(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));
        facilityList = club.getFacilities();

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), facilityList, false);
        oleFacilityAdapter.setOnItemClickListener(facClickListener);

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            binding.relLoc.setVisibility(View.GONE);
            binding.relClub.setVisibility(View.VISIBLE);
            binding.tvClub.setText(club.getName());
            Iterator<OleClubFacility> iterator = facilityList.iterator();
            while (iterator.hasNext()) {
                OleClubFacility facility = iterator.next();
                if (facility.getPrice().equalsIgnoreCase("")) {
                    oleFacilityAdapter.selectedFacility.add(facility);
                    iterator.remove();
                }
            }
            OleImageData oleImageData = new OleImageData();
            oleImageData.setPhotoPath(club.getCoverPath());
            setupSlider(Collections.singletonList(oleImageData));
            if (!fieldId.isEmpty()) {
                getFieldAPI(true);
            }
        } else {
            binding.relLoc.setVisibility(View.VISIBLE);
            binding.relClub.setVisibility(View.GONE);
            if (!fieldId.isEmpty()) {
                getFieldAPI(true);
            }

            for (OleClubFacility facility : facilityList) {
                if (facility.getPrice().equalsIgnoreCase("")) {
                    oleFacilityAdapter.selectedFacility.add(facility);
                }
            }
        }
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
        selectedDate = dateFormat.format(new Date());

        binding.offerVu.setVisibility(View.GONE);

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

        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        daysAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
        daysAdapter.setOnItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(daysAdapter);

        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.slotsRecyclerVu.setLayoutManager(slotLayoutManager);
        boolean isPadel = club.getClubType().equalsIgnoreCase(Constants.kPadelModule);
        slotAdapter = new OleBookingSlotAdapter(getContext(), arrSlot, isPadel);
        slotAdapter.setOnItemClickListener(slotClickListener);
        binding.slotsRecyclerVu.setAdapter(slotAdapter);

        getAllFieldsAPI(false);

        binding.bar.backBtn.setOnClickListener(this);
        binding.relLoc.setOnClickListener(this);
        binding.relClub.setOnClickListener(this);
        binding.btnBook.setOnClickListener(this);

    }

    OleFacilityAdapter.OnItemClickListener facClickListener = new OleFacilityAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            if (facility.getPrice().equalsIgnoreCase("")) {
                return;
            }
            oleFacilityAdapter.setSelectedFacility(facility);
            if (selectedSlotDuration.isEmpty()) {
                setButtonText(selectedDuration);
            } else {
                setButtonText(selectedSlotDuration);
            }
        }

        @Override
        public void OnPlusClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            int maxQty = Integer.parseInt(facility.getMaxQuantity());
            if (facility.getQty() < maxQty) {
                facility.setQty(facility.getQty() + 1);
                if (selectedSlotDuration.isEmpty()) {
                    setButtonText(selectedDuration);
                } else {
                    setButtonText(selectedSlotDuration);
                }
                oleFacilityAdapter.notifyDataSetChanged();
            } else {
                Functions.showToast(getContext(), String.format(getResources().getString(R.string.you_select_max_qty_place), maxQty), FancyToast.ERROR);
            }
        }

        @Override
        public void OnMinusClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            facility.setQty(facility.getQty() - 1);
            if (facility.getQty() == 0) {
                int index = oleFacilityAdapter.isExistInSelected(facility.getId());
                if (index != -1) {
                    oleFacilityAdapter.selectedFacility.remove(index);
                }
            }
            if (selectedSlotDuration.isEmpty()) {
                setButtonText(selectedDuration);
            } else {
                setButtonText(selectedSlotDuration);
            }
            oleFacilityAdapter.notifyDataSetChanged();
        }
    };

    OleBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            fieldId = fieldList.get(pos).getId();
            fieldAdapter.setSelectedFieldId(fieldId);
            getFieldAPI(true);
        }
    };

    OleBookingDurationAdapter.OnItemClickListener durClickListener = new OleBookingDurationAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDuration = durationList.get(pos).getKey();
            durationAdapter.setSelectedDuration(selectedDuration);
            setButtonText(selectedDuration);
            selectedStartTime = "";
            getSlotsAPI(arrDate.get(selectedDateIndex).getDate());
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
                setFieldPriceByDay(date);
                selectedStartTime = "";
                offerDiscount = 0;
                setButtonText(selectedDuration);
                selectedDate = date;
                getSlotsAPI(selectedDate);
            } else {
                Functions.showToast(getContext(), getString(R.string.select_duration), FancyToast.ERROR);
            }
        }
    };

    OleBookingSlotAdapter.OnItemClickListener slotClickListener = new OleBookingSlotAdapter.OnItemClickListener() {
        @Override
        public void OnItemLongClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
            if (fieldId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
                return;
            }
            OleBookingSlot slot = arrSlot.get(pos);
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                if (!slot.getStatus().equalsIgnoreCase("available")) {
                    //showBookingAlert(slot.getStart(), slot.getEnd());
                    showWaitingUsersList(slot);
                } else if (slot.getStatus().equalsIgnoreCase("available") && !slot.getWaitingList().isEmpty()) {
                    showWaitingUsersList(slot);
                }
            } else {
                if (!slot.getUserName().equalsIgnoreCase("")) {
                    String msg = getResources().getString(R.string.has_booked_this_time, slot.getUserName());
                    ViewTooltip.on(v.relBorder)
                            .autoHide(true, 3000).clickToHide(true)
                            .corner(30).position(ViewTooltip.Position.TOP)
                            .text(msg).textColor(Color.WHITE).color(getResources().getColor(R.color.blueColorNew))
                            .show();
                }
            }

            if (selectedDuration.equalsIgnoreCase(v.slotDuration)) {
                selectedSlotDuration = v.slotDuration;
                selectedSlotIndex = pos;
                selectedStartTime = slot.getStart();
                selectedEndTime = slot.getEnd();
                selectedSlotTime = slot.getSlot();
                selectedShift = slot.getShift();
                offerDiscount = 0;
                try {
                    offerDiscount = checkOfferForSlot(slot.getStart(), slot.getEnd());
                    if (selectedSlotDuration.isEmpty()) {
                        setButtonText(selectedDuration);
                    } else {
                        setButtonText(selectedSlotDuration);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                slotAdapter.setSelectedSlotIndex(pos, selectedDate);
            } else {
                String msg = getResources().getString(R.string.slot_selected_dur_msg, selectedDuration, v.slotDuration, v.slotDuration);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.confirmation))
                        .setMessage(msg)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setButtonText(v.slotDuration);
                                selectedSlotDuration = v.slotDuration;
                                selectedSlotIndex = pos;
                                selectedStartTime = slot.getStart();
                                selectedEndTime = slot.getEnd();
                                selectedSlotTime = slot.getSlot();
                                selectedShift = slot.getShift();
                                offerDiscount = 0;
                                try {
                                    offerDiscount = checkOfferForSlot(slot.getStart(), slot.getEnd());
                                    if (selectedSlotDuration.isEmpty()) {
                                        setButtonText(selectedDuration);
                                    } else {
                                        setButtonText(selectedSlotDuration);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                slotAdapter.setSelectedSlotIndex(pos, selectedDate);
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

        @Override
        public void OnItemClick(OleBookingSlotAdapter.ViewHolder viewHolder, int pos) {
            if (fieldId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
                return;
            }
            OleBookingSlot slot = arrSlot.get(pos);
            if (slot.getStatus().equalsIgnoreCase("booked")) {
                if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                    confirmCancelBookingAlert(slot.getBookingId(), pos, slot.getBookingStatus());
                } else {
                    showBookingAlert(slot.getStart(), slot.getEnd());
                }
                return;
            }
            if (slot.getStatus().equalsIgnoreCase("hidden")) {
                if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                    Functions.showToast(getContext(), getString(R.string.you_hide_this_slot), FancyToast.SUCCESS);
                } else {
                    showBookingAlert(slot.getStart(), slot.getEnd());
                }
                return;
            }

            if (selectedDuration.equalsIgnoreCase(viewHolder.slotDuration)) {
                selectedSlotDuration = viewHolder.slotDuration;
                selectedSlotIndex = pos;
                selectedStartTime = slot.getStart();
                selectedEndTime = slot.getEnd();
                selectedSlotTime = slot.getSlot();
                selectedShift = slot.getShift();
                offerDiscount = 0;
                try {
                    offerDiscount = checkOfferForSlot(slot.getStart(), slot.getEnd());
                    if (selectedSlotDuration.isEmpty()) {
                        setButtonText(selectedDuration);
                    } else {
                        setButtonText(selectedSlotDuration);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                slotAdapter.setSelectedSlotIndex(pos, selectedDate);
            } else {
                String msg = getResources().getString(R.string.slot_selected_dur_msg, selectedDuration, viewHolder.slotDuration, viewHolder.slotDuration);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.confirmation))
                        .setMessage(msg)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setButtonText(viewHolder.slotDuration);
                                selectedSlotDuration = viewHolder.slotDuration;
                                selectedSlotIndex = pos;
                                selectedStartTime = slot.getStart();
                                selectedEndTime = slot.getEnd();
                                selectedSlotTime = slot.getSlot();
                                selectedShift = slot.getShift();
                                offerDiscount = 0;
                                try {
                                    offerDiscount = checkOfferForSlot(slot.getStart(), slot.getEnd());
                                    if (selectedSlotDuration.isEmpty()) {
                                        setButtonText(selectedDuration);
                                    } else {
                                        setButtonText(selectedSlotDuration);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                slotAdapter.setSelectedSlotIndex(pos, selectedDate);
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

    private void showBookingAlert(String startTime, String endTime) {
        if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("BookingAlertDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleBookingAlertDialogFragment dialogFragment = new OleBookingAlertDialogFragment();
            dialogFragment.setFragmentCallback(new OleBookingAlertDialogFragment.BookingAlertDialogFragmentCallback() {
                @Override
                public void didSubmit(String phone) {
                    notificationAPI(startTime, endTime, phone);
                }
            });
            dialogFragment.show(fragmentTransaction, "BookingAlertDialogFragment");
        }
    }

    private void confirmCancelBookingAlert(String bookingId, int position, String bookingStatus) {
        String[] arr;
        if (!bookingStatus.equalsIgnoreCase("1")) {
            arr = new String[]{getResources().getString(R.string.add_waiting_user), getResources().getString(R.string.confirm_booking), getResources().getString(R.string.cancel_booking)};
        } else {
            arr = new String[]{getResources().getString(R.string.add_waiting_user), getResources().getString(R.string.cancel_booking)};
        }
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.dismiss))
                .setOtherButtonTitles(arr)
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (arr.length == 3) {
                            if (index == 0) {
                                actionSheet.dismiss();
                                new Handler().postDelayed(() -> showWaitingUserSheet(bookingId), 200); // Adjust delay as needed
                            } else if (index == 1) {
                                cancelConfirmBookingAPI(true, "confirm", bookingId, position);
                            } else {
                                cancelConfirmBookingAPI(true, "cancel", bookingId, position);
                            }
                        } else {
                            if (index == 0) {
                                actionSheet.dismiss();
                                new Handler().postDelayed(() -> showWaitingUserSheet(bookingId), 200); // Adjust delay as needed
                            } else if (index == 1) {
                                cancelConfirmBookingAPI(true, "cancel", bookingId, position);
                            }

                        }
                    }
                }).show();
    }

    protected void showWaitingUserSheet(String bookingId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag("AddWaitingUserDialogFragment");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }
        fragmentTransaction.addToBackStack(null);
        AddWaitingUserDialogFragment dialogFragment = new AddWaitingUserDialogFragment(bookingId);
        dialogFragment.setDialogCallback((df, name, phone) -> {
            df.dismiss();
            addUserToWaitingList(bookingId, club.getId(), name, phone);
        });
        dialogFragment.show(fragmentTransaction, "AddWaitingUserDialogFragment");
    }

    protected void showWaitingUsersList(OleBookingSlot slot) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag("showWaitingUsersListDialogFragment");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }
        fragmentTransaction.addToBackStack(null);
        showWaitingUsersListDialogFragment dialogFragment = new showWaitingUsersListDialogFragment(slot);
        dialogFragment.setDialogCallback((df, type, id, name, phone) -> {
            df.dismiss();
            if (type.equalsIgnoreCase("booking")) {
                if (!phone.isEmpty()) {
                    isWaitingUser = "1";
                    bookClicked(name, phone);
                }
            } else {
                if (id != null) {
                    removeWaitingUser(id);
                }
            }


        });
        dialogFragment.show(fragmentTransaction, "showWaitingUsersListDialogFragment");
    }

    private void removeWaitingUser(String id) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeWaitingUser(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
                            getSlotsAPI(selectedDate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void addUserToWaitingList(String bookingId, String clubId, String name, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addUserToWaitingList(bookingId, clubId, name, phone);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
                            getSlotsAPI(selectedDate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        } else if (v == binding.relLoc) {
            locationClicked();
        } else if (v == binding.relClub) {
            clubClicked();
        } else if (v == binding.btnBook) {
            isWaitingUser = "0";
            bookClicked("", "");

        }
    }

    private void locationClicked() {
        String uri = "http://maps.google.com/maps?daddr=" + club.getLatitude() + "," + club.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void clubClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ClubListDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        List<Club> clubs = new ArrayList<>();
        clubs.addAll(AppManager.getInstance().clubs);
        OleClubListDialogFragment dialogFragment = new OleClubListDialogFragment(clubs, null, false);
        dialogFragment.setFragmentCallback(new OleClubListDialogFragment.ClubListDialogFragmentCallback() {
            @Override
            public void didSelectClub(Club club) {
                binding.tvClub.setText(club.getName());
                OleBookingActivity.this.club = club;
                fieldId = "";
                getAllFieldsAPI(true);
                selectedSlotIndex = -1;
                slotAdapter.setSelectedSlotIndex(selectedSlotIndex, selectedDate);
                Iterator<OleClubFacility> iterator = OleBookingActivity.this.club.getFacilities().iterator();
                oleFacilityAdapter.selectedFacility.clear();
                while (iterator.hasNext()) {
                    OleClubFacility facility = iterator.next();
                    if (facility.getPrice().equalsIgnoreCase("")) {
                        oleFacilityAdapter.selectedFacility.add(facility);
                        iterator.remove();
                    }
                }
                oleFacilityAdapter.setDataSource(club.getFacilities());

                OleImageData oleImageData = new OleImageData();
                oleImageData.setPhotoPath(club.getCoverPath());
                setupSlider(Collections.singletonList(oleImageData));
            }

            @Override
            public void didSelectField(Field field) {

            }
        });
        dialogFragment.show(fragmentTransaction, "ClubListDialogFragment");
    }

    private void bookClicked(String name, String phone) {

        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (club.getIsExpired() != null && club.getIsExpired().equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.renew_membership_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (fieldId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
            return;
        }
        if (fieldDetail == null) {
            return;
        }
        if (fieldDetail.getIsBlock().equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), "انت محظور من قبل الملعب", FancyToast.ERROR);
//            Functions.showToast(getContext(), getResources().getString(R.string.you_are_blocked_by_field_owner), FancyToast.ERROR);
            return;
        }
        if (selectedDuration.isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_duration), FancyToast.ERROR);
            return;
        }
        if (selectedDate.isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_date), FancyToast.ERROR);
            return;
        }
        if (selectedStartTime.isEmpty() || selectedSlotIndex == -1) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_time), FancyToast.ERROR);
            return;
        }
        if (arrSlot.size() == 0) {
            return;
        }
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            Intent intent = new Intent(getContext(), OlePBookingInfoActivity.class);
            if (offerDiscountType.equalsIgnoreCase("") || offerDiscountType.equalsIgnoreCase("amount")) {
                intent.putExtra("price", String.valueOf(Double.parseDouble(price) - offerDiscount + selectedFacilityPrice));
            } else {
                double p = Double.parseDouble(price);
                p = p - ((offerDiscount / 100) * p);
                intent.putExtra("price", String.valueOf(p + selectedFacilityPrice));
            }

            intent.putExtra("booking_price", price);
            intent.putExtra("duration", selectedSlotDuration);
            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                intent.putExtra("size", "");
            } else {
                intent.putExtra("size", fieldDetail.getFieldSize().getName());
            }
            intent.putExtra("club_name", club.getName());
            intent.putExtra("field_name", fieldDetail.getName());
            intent.putExtra("currency", fieldDetail.getCurrency());
            intent.putExtra("continuous_allowed", fieldDetail.getContinuousAllowed());
            intent.putExtra("club_id", club.getId());
            intent.putExtra("field_id", fieldId);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", arrSlot.get(selectedSlotIndex).getSlot());
            intent.putExtra("fac_price", selectedFacilityPrice);
            intent.putExtra("is_offer", offerDiscount > 0);
            startActivityForResult(intent, 100);
        } else {
            Intent intent = new Intent(getContext(), OleBookingInfoActivity.class);
            intent.putExtra("price", String.valueOf(Double.parseDouble(price) - offerDiscount + selectedFacilityPrice));
            intent.putExtra("duration", selectedSlotDuration);
            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                intent.putExtra("size", "");
            } else {
                intent.putExtra("size", fieldDetail.getFieldSize().getName());
            }
            intent.putExtra("club_name", club.getName());
            intent.putExtra("field_name", fieldDetail.getName());
            intent.putExtra("currency", fieldDetail.getCurrency());
            intent.putExtra("club_id", club.getId());
            intent.putExtra("field_id", fieldId);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", arrSlot.get(selectedSlotIndex).getSlot());
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            startActivityForResult(intent, 1001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // from player booking info
            Bundle bundle = data.getExtras();
            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            String paymentType = bundle.getString("paymentType");
            int promoDiscount = bundle.getInt("promoDiscount");
            String promoId = bundle.getString("promoId");
            String cardDiscount = bundle.getString("cardDiscount");
            padelPlayers = bundle.getString("padel_players");
            String padelPlayersPayment = bundle.getString("padel_players_payment");
            bookingPaymentMethod = paymentType;
            if (cardDiscount.isEmpty()) {
                addBookingAPI("", name, phone, "", paymentType, "", "", "", "", promoDiscount, promoId, offerDiscount, padelPlayers, padelPlayersPayment);
            } else {
                addBookingAPI("", name, phone, cardDiscount, paymentType, "", "", "", "", promoDiscount, promoId, 0, padelPlayers, padelPlayersPayment);
            }
        } else if (requestCode == 1001 && resultCode == RESULT_OK) {
            // from owner booking info
            Bundle bundle = data.getExtras();
            String userId = bundle.getString("user_id");
            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            String discount = bundle.getString("discount");
            String days = bundle.getString("days");
            String fromDate = bundle.getString("from_date");
            String toDate = bundle.getString("to_date");
            String addPrice = bundle.getString("add_price");
            addBookingAPI(userId, name, phone, discount, "cash", days, fromDate, toDate, addPrice, 0, "", offerDiscount, "", "");
        }
    }

    private void populateData(Field field) {
        setupSlider(field.getImages());
        binding.tvLoc.setText(club.getCity().getName());

        if (club.getSlots60().equalsIgnoreCase("1")) {
            selectedDuration = "1";
            price = field.getOneHour();
        } else if (club.getSlots90().equalsIgnoreCase("1")) {
            selectedDuration = "1.5";
            price = field.getOneHalfHours();
        } else if (club.getSlots120().equalsIgnoreCase("1")) {
            selectedDuration = "2";
            price = field.getTwoHours();
        }

        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
            if (club.getSlots90().equalsIgnoreCase("1")) {
                selectedDuration = "1.5";
                price = field.getOneHalfHours();
            } else if (club.getSlots120().equalsIgnoreCase("1")) {
                selectedDuration = "2";
                price = field.getTwoHours();
            }
        }
        populateDuration();

        if (arrDate.size() > 0) {
            setFieldPriceByDay(arrDate.get(selectedDateIndex).getDate());
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
            setFieldPriceByDay(dateFormat.format(new Date()));
        }
        if (Functions.getAppLang(getContext()).equalsIgnoreCase(Constants.kArLang)) {
            binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), price, fieldDetail.getCurrency()));
        } else {
            binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), price));
        }
        checkOfferAvailable();
        getSlotsAPI(selectedDate);
    }

    private void populateDuration() {
        if (fieldId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
            return;
        }
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
        durationAdapter.setSelectedDuration(selectedDuration);
    }

    private void setFieldPriceByDay(String date) {
        if (fieldDetail != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
            Date dt = null;
            try {
                dt = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFormat.applyPattern("EEEE");
            String dayName = dateFormat.format(dt);
            String dayId = Functions.getDayIdByName(dayName.toLowerCase());
            for (OleFieldPrice dayPrice : fieldDetail.getDaysPrice()) {
                if (dayPrice.getDayId().equalsIgnoreCase(dayId)) {
                    fieldDetail.setOneHour(dayPrice.getOneHour());
                    fieldDetail.setOneHalfHours(dayPrice.getOneHalfHour());
                    fieldDetail.setTwoHours(dayPrice.getTwoHour());
                    break;
                }
            }
        }
    }

    private void checkOfferAvailable() {
        if (fieldDetail != null) {
            if (fieldDetail.getOffers().size() == 1) {
                OleOfferData oleOfferData = fieldDetail.getOffers().get(0);
                binding.offerVu.setVisibility(View.VISIBLE);
                binding.tvOfferName.setText(oleOfferData.getOfferName());
                String days = "";
                String[] dayIdArr = oleOfferData.getDayId().split(",");
                for (String id : dayIdArr) {
                    if (days.isEmpty()) {
                        days = Functions.getDayById(getContext(), id);
                    } else {
                        days = String.format("%s, %s", days, Functions.getDayById(getContext(), id));
                    }
                }
                String startTime = oleOfferData.getTimimgStart().split(" ")[1];
                String endTime = oleOfferData.getTimingEnd().split(" ")[1];
                String str = "";
                if (oleOfferData.getDiscountType().equalsIgnoreCase("amount")) {
                    str = getResources().getString(R.string.offer_desc_place, days, startTime, endTime, oleOfferData.getDiscount(), oleOfferData.getCurrency());
                } else {
                    str = getResources().getString(R.string.offer_desc_place, days, startTime, endTime, oleOfferData.getDiscount(), "%");
                }

                binding.tvOfferDetail.setText(str);
            } else if (fieldDetail.getOffers().size() > 1) {
                binding.offerVu.setVisibility(View.VISIBLE);
                binding.tvOfferName.setText(getString(R.string.offer));
                binding.tvOfferDetail.setText(getString(R.string.offer_desc));
            } else {
                binding.offerVu.setVisibility(View.GONE);
            }
        } else {
            binding.offerVu.setVisibility(View.GONE);
        }
    }

    public double checkOfferForSlot(String time1, String time2) throws ParseException {
        double result = 0;
        if (binding.offerVu.getVisibility() == View.VISIBLE && fieldDetail != null && fieldDetail.getOffers().size() > 0) {
            for (OleOfferData oleOfferData : fieldDetail.getOffers()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getDefault());
                Date slotTime1 = dateFormat.parse(time1);
                Date slotTime2 = dateFormat.parse(time2);
                dateFormat.applyPattern("dd/MM/yyyy hh:mma");
                Date slot1DT = dateFormat.parse(dateFormat.format(slotTime1));
                Date slot2DT = dateFormat.parse(dateFormat.format(slotTime2));
                dateFormat.applyPattern(kBookingFormat);
                Date date = dateFormat.parse(selectedDate);
                Date offerExpiryDate = dateFormat.parse(oleOfferData.getOfferExpiry());
                dateFormat.applyPattern("dd/MM/yyyy");
                String todayDate = dateFormat.format(date);

                // check offer expired date if selected date is greater than offer end date then return false
                boolean isExpired = date.compareTo(offerExpiryDate) > 0;

                // check day
                String dayName = Functions.getDayName(date);
                String[] dayIdArr = oleOfferData.getDayId().split(",");
                boolean isFound = false;
                for (String id : dayIdArr) {
                    if (dayName.equalsIgnoreCase(Functions.getEngDayById(getContext(), id))) {
                        isFound = true;
                        break;
                    }
                }

                dateFormat.applyPattern("dd/MM/yyyy");
                Date offerStartDate = dateFormat.parse(oleOfferData.getTimimgStart().split(" ")[0]);
                Date offerEndDate = dateFormat.parse(oleOfferData.getTimingEnd().split(" ")[0]);
                if (offerEndDate.compareTo(offerStartDate) > 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    String nextDate = dateFormat.format(calendar.getTime());
                    dateFormat.applyPattern("dd/MM/yyyy hh:mma");
                    offerStartDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimimgStart().split(" ")[1]));
                    offerEndDate = dateFormat.parse(String.format("%s %s", nextDate, oleOfferData.getTimingEnd().split(" ")[1]));
                } else {
                    dateFormat.applyPattern("dd/MM/yyyy hh:mma");
                    offerStartDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimimgStart().split(" ")[1]));
                    offerEndDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimingEnd().split(" ")[1]));
                }

                if (((offerStartDate.compareTo(slot1DT) < 0 || offerStartDate.compareTo(slot1DT) == 0)
                        && (slot1DT.compareTo(offerEndDate) < 0 || slot1DT.compareTo(offerEndDate) == 0))
                        && (offerStartDate.compareTo(slot2DT) < 0 || offerStartDate.compareTo(slot2DT) == 0)
                        && (slot2DT.compareTo(offerEndDate) < 0 || slot2DT.compareTo(offerEndDate) == 0)
                        && isFound && !isExpired) {
                    if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                        result = 0;
                        offerDiscountType = "";
                    } else {
                        result = Double.parseDouble(oleOfferData.getDiscount());
                        offerDiscountType = oleOfferData.getDiscountType();
                    }
                }
            }
        }
        return result;
    }

    private void setButtonText(String duration) {
        if (fieldDetail != null) {
            selectedFacilityPrice = calculateFacPrice();
            if (duration.equalsIgnoreCase("1")) {
                price = fieldDetail.getOneHour();
            } else if (duration.equalsIgnoreCase("1.5")) {
                price = fieldDetail.getOneHalfHours();
            } else if (duration.equalsIgnoreCase("2")) {
                price = fieldDetail.getTwoHours();
            } else {
                price = "";
            }

            if (price.isEmpty()) {
                binding.btnBook.setTitle(getString(R.string.book_now));
            } else {
                double p = 0;
                if (offerDiscountType.isEmpty() || offerDiscountType.equalsIgnoreCase("amount")) {
                    p = Double.parseDouble(price) - offerDiscount;
                    p = p + selectedFacilityPrice;
                } else {
                    p = Double.parseDouble(price);
                    p = p - ((offerDiscount / 100) * p);
                    p = p + selectedFacilityPrice;
                }
                if (Functions.getAppLang(getContext()).equalsIgnoreCase(Constants.kArLang)) {
                    binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), p, fieldDetail.getCurrency()));
                } else {
                    binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), p));
                }
            }
        }
    }

    private double calculateFacPrice() {
        double p = 0;
        for (OleClubFacility facility : oleFacilityAdapter.selectedFacility) {
            if (!facility.getPrice().isEmpty()) {
                if (facility.getType().equalsIgnoreCase("countable")) {
                    p = p + (Double.parseDouble(facility.getPrice()) * facility.getQty());
                } else {
                    p = p + Double.parseDouble(facility.getPrice());
                }
            }
        }
        return p;
    }

    private void setupSlider(List<OleImageData> oleImageDataList) {
        List<SlideModel> imageList = new ArrayList<>();
        for (OleImageData oleImageData : oleImageDataList) {
            imageList.add(new SlideModel(oleImageData.getPhotoPath(),  ScaleTypes.FIT));

        }
        binding.slider.setImageList(imageList,ScaleTypes.FIT);
        binding.slider.setItemClickListener(new ItemClickListener() {
            @Override
            public void doubleClick(int i) {

            }

            @Override
            public void onItemSelected(int i) {

                new StfalconImageViewer.Builder<>(getContext(), oleImageDataList, (imageView, oleImageData) -> {
                    // Use Glide to load the image from the OleImageData
                    Glide.with(getApplicationContext())
                            .load(oleImageData.getPhotoPath())
                            .into(imageView);
                }).withStartPosition(i).show();
            }
        });
    }

    private void getAllFieldsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
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
                            int selectedPos = -1;
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                                if (field.getId().equalsIgnoreCase(fieldId)) {
                                    selectedPos = i;
                                }
                            }
                            if (fieldId.equalsIgnoreCase("") && fieldList.size() > 0) {
                                fieldId = fieldList.get(0).getId();
                                getFieldAPI(true);
                            }
                            fieldAdapter.setSelectedFieldId(fieldId);
                            if (selectedPos != -1) {
                                binding.fieldRecyclerVu.scrollToPosition(selectedPos);
                            }
                        } else {
                            fieldList.clear();
                            fieldAdapter.notifyDataSetChanged();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void getFieldAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOneField(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fieldId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            fieldDetail = gson.fromJson(obj.toString(), Field.class);
                            populateData(fieldDetail);
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
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
                                arrSlot.add(gson.fromJson(arr.get(i).toString(), OleBookingSlot.class));
                            }
                            selectedSlotIndex = -1;
                            slotAdapter.setSelectedSlotIndex(selectedSlotIndex, date);// it will reload data
                            for (int i = 0; i < arrD.length(); i++) {
                                arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                            }
                            daysAdapter.setDataSource(arrDate.toArray());
                        } else {
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
                } else {
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
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void notificationAPI(String start, String end, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.notifyAvailability(Functions.getAppLang(getContext()), fieldId, club.getId(), start, end, phone, Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void addBookingAPI(String registeredUserId, String userName, String phone, String discount, String paymentType, String days, String fromDate, String toDate, String addPrice, int promoDiscount, String promoId, double offerDis, String padelPlayers, String padelPlayersForPayment) {
        double totalP = 0;
        if (!addPrice.isEmpty()) {
            totalP = Double.parseDouble(price) + Double.parseDouble(addPrice);
        } else {
            totalP = Double.parseDouble(price);
        }

        ///// promo code priority //////
        if (promoDiscount > 0) {
            offerDis = 0;
            discount = "";
        } else {
            if (offerDiscountType.equalsIgnoreCase("percent")) {
                double p = Double.parseDouble(price);
                offerDis = (offerDis / 100) * p;
            }
        }
        ///// end //////

        if (selectedSlotDuration.isEmpty()) {
            selectedSlotDuration = selectedDuration;
        }
        String fieldType = club.getClubType();
        String facilities = "";
        try {
            JSONArray array = new JSONArray();
            for (OleClubFacility facility : oleFacilityAdapter.selectedFacility) {
                JSONObject object = new JSONObject();
                object.put("fac_id", facility.getId());
                if (facility.getQty() > 0) {
                    object.put("qty", String.valueOf(facility.getQty()));
                } else {
                    object.put("qty", "");
                }
                array.put(object);
            }

            facilities = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String ladySlot = arrSlot.get(selectedSlotIndex).getLadySlot();

        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addBooking(Functions.getAppLang(getContext()),
                Functions.getPrefValue(getContext(), Constants.kUserID),
                fieldId, club.getId(), selectedSlotDuration, selectedDate, totalP, discount,
                selectedStartTime, selectedEndTime, selectedShift, paymentType, userName, phone,
                facilities, selectedFacilityPrice, offerDis, "0", promoDiscount, promoId,
                days, fromDate, toDate, Functions.getIPAddress(), fieldType, padelPlayers, padelPlayersForPayment, ladySlot, isWaitingUser, registeredUserId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                finish();
                            } else if (paymentType.equalsIgnoreCase("cash")) {
                                if (!club.getMatchAllowed().equalsIgnoreCase("1") && !club.getGamesAllowed().equalsIgnoreCase("1")) {
                                    Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                    finish();
                                } else {
                                    // go to booking done dialog
                                    String bookingId = object.getJSONObject(Constants.kData).getString("id");
                                    OleBookingDoneDialog dialog = new OleBookingDoneDialog(getContext(), club.getName(), selectedDate, selectedSlotTime, bookingId);
                                    dialog.setDialogCallback(callback);
                                    dialog.show();
                                }
                            } else if (paymentType.equalsIgnoreCase("wallet")) {
                                String bookingId = object.getJSONObject(Constants.kData).getString("id");
                                String price = object.getJSONObject(Constants.kData).getString("price");
                                String currency = object.getJSONObject(Constants.kData).getString("currency");
                                if (price.equalsIgnoreCase("")) {
                                    if (!club.getMatchAllowed().equalsIgnoreCase("1") && !club.getGamesAllowed().equalsIgnoreCase("1")) {
                                        Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                        finish();
                                    } else {
                                        // go to booking done dialog
                                        OleBookingDoneDialog dialog = new OleBookingDoneDialog(getContext(), club.getName(), selectedDate, selectedSlotTime, bookingId);
                                        dialog.setDialogCallback(callback);
                                        dialog.show();
                                    }
                                } else {
                                    makePayment(bookingId, "card", price, currency, new PaymentCallback() {
                                        @Override
                                        public void paymentStatus(boolean status, String orderRef) {
                                            checkPaymentStatusApi(bookingId, orderRef);
                                            if (status) {
                                                if (!club.getMatchAllowed().equalsIgnoreCase("1") && !club.getGamesAllowed().equalsIgnoreCase("1")) {
                                                    finish();
                                                } else {
                                                    // go to booking done dialog
                                                    OleBookingDoneDialog dialog = new OleBookingDoneDialog(getContext(), club.getName(), selectedDate, selectedSlotTime, bookingId);
                                                    dialog.setDialogCallback(callback);
                                                    dialog.show();
                                                }
                                            } else {
                                                Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                                            }
                                        }
                                    });
                                }
                            } else {
                                String bookingId = object.getJSONObject(Constants.kData).getString("id");
                                String price = object.getJSONObject(Constants.kData).getString("price");
                                String currency = object.getJSONObject(Constants.kData).getString("currency");
                                makePayment(bookingId, paymentType, price, currency, new PaymentCallback() {
                                    @Override
                                    public void paymentStatus(boolean status, String orderRef) {
                                        checkPaymentStatusApi(bookingId, orderRef);
                                        if (status) {
                                            if (!club.getMatchAllowed().equalsIgnoreCase("1") && !club.getGamesAllowed().equalsIgnoreCase("1")) {
                                                finish();
                                            } else {
                                                // go to booking done dialog
                                                OleBookingDoneDialog dialog = new OleBookingDoneDialog(getContext(), club.getName(), selectedDate, selectedSlotTime, bookingId);
                                                dialog.setDialogCallback(callback);
                                                dialog.show();
                                            }
                                        } else {
                                            Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                                        }
                                    }
                                });
                            }
                        } else if (object.getInt(Constants.kStatus) == 409 && Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                            Functions.showAlert(getContext(), getString(R.string.success), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    finish();
                                }
                            });
                        } else {
                            arrSlot.clear();
                            slotAdapter.setSelectedSlotIndex(-1, selectedDate);
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void checkPaymentStatusApi(String bookingId, String paymentOrderRef) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.checkPaymentStatus(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, paymentOrderRef);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void cancelConfirmBookingAPI(boolean isLoader, String status, String bookingId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelConfirmBooking(Functions.getAppLang(getContext()), bookingId, status, "", "", "", "", "", Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (status.equalsIgnoreCase("cancel")) {
                                arrSlot.get(pos).setStatus("available");
                                arrSlot.get(pos).setUserName("");
                                slotAdapter.notifyItemChanged(pos);
                            }
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    OleBookingDoneDialog.BookingDialogCallback callback = new OleBookingDoneDialog.BookingDialogCallback() {
        @Override
        public void onDismiss() {
            if (!isFinishing()) {
                getSlotsAPI(selectedDate);
            }
        }

        @Override
        public void createMatchClicked(String bookingId) {
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                if (selectedSlotDuration.equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getResources().getString(R.string.padel_match_duration_error), FancyToast.ERROR);
                    return;
                }
                if (Functions.getUserinfo(getContext()).getPhotoUrl().equalsIgnoreCase("")) {
                    Functions.showToast(getContext(), getResources().getString(R.string.upload_your_profile_photo_first), FancyToast.ERROR);
                    return;
                }
                if (!padelPlayers.equalsIgnoreCase("")) {
                    Functions.showToast(getContext(), getResources().getString(R.string.padel_match_partner_count_error), FancyToast.ERROR);
                    return;
                }
                Intent intent = new Intent(getContext(), OleCreatePadelMatchActivity.class);
                intent.putExtra("booking_id", bookingId);
                intent.putExtra("club_id", club.getId());
                intent.putExtra("payment_method", bookingPaymentMethod);
                intent.putExtra("match_allow", club.getMatchAllowed());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), OleCreateMatchActivity.class);
                intent.putExtra("booking_id", bookingId);
                intent.putExtra("payment_method", bookingPaymentMethod);
                intent.putExtra("club_id", club.getId());
                intent.putExtra("is_update", false);
                intent.putExtra("match_allow", club.getMatchAllowed());
                intent.putExtra("game_allow", club.getGamesAllowed());
                startActivity(intent);
            }
        }

        @Override
        public void bookingListClicked() {
            Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
            intent.putExtra("tab_position", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        @Override
        public void formationClicked(String bookingId) {
//            Intent intent = new Intent(getContext(), OleBookingFormationActivity.class);
//            intent.putExtra("booking_id", bookingId);
//            intent.putExtra("booking_status", "");
//            startActivity(intent);
//            finish();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };


}
