package ae.oleapp.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingDurationAdapter;
import ae.oleapp.adapters.OleBookingSlotAdapter;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.adapters.OleFastBookingFieldAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityFastBookingBinding;
import ae.oleapp.dialogs.OleBookingAlertDialogFragment;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.fragments.AddWaitingUserDialogFragment;
import ae.oleapp.fragments.showWaitingUsersListDialogFragment;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleFieldPrice;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleOfferData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleFastBookingActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityFastBookingBinding binding;
    private Club club;
    private boolean isPadel;
    private String fieldId = "";
    private Field fieldDetail;
    private final List<Club> clubList = new ArrayList<>();
    private final List<Field> fieldList = new ArrayList<>();
    private List<OleClubFacility> facilityList = new ArrayList<>();
    private String selectedDuration = "";
    private String isWaitingUser = "0";
    private String selectedSlotDuration = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private String selectedSlotTime = "";
    private String selectedShift = "";
    private String selectedDate = "";
    private String price = "";
    private String offerDiscountType = "";
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private int selectedDateIndex = 0;
    private final String kBookingFormat = "yyyy-MM-dd";
    private double offerDiscount = 0;
    private double selectedFacilityPrice = 0;
    private OleFacilityAdapter oleFacilityAdapter;
    private OleBookingDurationAdapter durationAdapter;
    private OleBookingDateAdapter daysAdapter;
    private OleFastBookingFieldAdapter fieldAdapter;
    private OleRankClubAdapter clubAdapter;
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private int selectedSlotIndex = -1;
    private int selectedFieldIndex = -1;
    private int clubIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFastBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(getString(R.string.fast_booking));

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            isPadel = bundle.getBoolean("is_padel", false);
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
            //id = bundle.getString("id");
        }

        clubList.addAll(AppManager.getInstance().clubs);
        for (int i = 0; i < clubList.size(); i++) {
            if (club.getId().equalsIgnoreCase(clubList.get(i).getId())) {
                clubIndex = i;
            }



//            if (isPadel && c.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
////                clubList.add(c);
//                if (club.getId().equalsIgnoreCase(c.getId())) {
//                    clubIndex = i;
//                }
//            }
//            else if (!isPadel && c.getClubType().equalsIgnoreCase(Constants.kFootballModule)) {
////                clubList.add(c);
//                if (club.getId().equalsIgnoreCase(c.getId())) {
//                    clubIndex = i;
//                }
//            }
        }

        binding.btnBook.setBackgroundImage(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));

        facilityList = club.getFacilities();
        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), facilityList, false);
        oleFacilityAdapter.setOnItemClickListener(facClickListener);
        Iterator<OleClubFacility> iterator = facilityList.iterator();
        while (iterator.hasNext()) {
            OleClubFacility facility = iterator.next();
            if (facility.getPrice().equalsIgnoreCase("")) {
                oleFacilityAdapter.selectedFacility.add(facility);
                iterator.remove();
            }
        }
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
        selectedDate = dateFormat.format(new Date());

        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        clubAdapter = new OleRankClubAdapter(getContext(), clubList, clubIndex, false);
        clubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(clubAdapter);
        if (clubIndex != -1){
            binding.clubRecyclerVu.scrollToPosition(clubIndex);
        }


        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleFastBookingFieldAdapter(getContext(), fieldList, selectedDate);
        fieldAdapter.setPadel(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        populateDuration(false);
        selectedDuration = durationList.get(0).getKey();
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

        getAllFieldsAPI(true, selectedDate);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnBook.setOnClickListener(this);

        //doItBitch(); //12/07/2023, If (issue){remove code}

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnBook) {
            bookClicked("","");
        }
    }

//    private void doItBitch(){
//        for (int i =0; i<clubList.size(); i++){
//            if (id.equalsIgnoreCase(clubList.get(i).getId())){
//                this.club = null;
//                Club club = clubList.get(i);
//                clubAdapter.setSelectedId(id);
//                this.club = club;
//                populateDuration(true);
//                fieldAdapter.setPadel(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));
//                fieldId = "";
//                selectedStartTime = "";
//                getAllFieldsAPI(true, selectedDate);
//                Iterator<OleClubFacility> iterator = club.getFacilities().iterator();
//                oleFacilityAdapter.selectedFacility.clear();
//                while (iterator.hasNext()) {
//                    OleClubFacility facility = iterator.next();
//                    if (facility.getPrice().equalsIgnoreCase("")) {
//                        oleFacilityAdapter.selectedFacility.add(facility);
//                        iterator.remove();
//                    }
//                }
//                oleFacilityAdapter.setDataSource(club.getFacilities());
//                fieldAdapter.notifyDataSetChanged();
//                daysAdapter.notifyDataSetChanged();
//
//            }
//        }
//    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            //doItBitch(pos);
            clubAdapter.setSelectedIndex(pos);
            Club club = clubList.get(pos);
            //clubAdapter.setSelectedId(club.getId());
            OleFastBookingActivity.this.club = club;
            populateDuration(true);
            fieldAdapter.setPadel(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));
            fieldId = "";
            selectedStartTime = "";
            getAllFieldsAPI(true, selectedDate);
            Iterator<OleClubFacility> iterator = club.getFacilities().iterator();
            oleFacilityAdapter.selectedFacility.clear();
            while (iterator.hasNext()) {
                OleClubFacility facility = iterator.next();
                if (facility.getPrice().equalsIgnoreCase("")) {
                    oleFacilityAdapter.selectedFacility.add(facility);
                    iterator.remove();
                }
            }
            oleFacilityAdapter.setDataSource(club.getFacilities());
        }
    };

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
            }
            else {
                setButtonText(selectedSlotDuration);
            }
        }

        @Override
        public void OnPlusClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            int maxQty = Integer.parseInt(facility.getMaxQuantity());
            if (facility.getQty() < maxQty) {
                facility.setQty(facility.getQty()+1);
                if (selectedSlotDuration.isEmpty()) {
                    setButtonText(selectedDuration);
                }
                else {
                    setButtonText(selectedSlotDuration);
                }
                oleFacilityAdapter.notifyDataSetChanged();
            }
            else {
                Functions.showToast(getContext(), String.format(getResources().getString(R.string.you_select_max_qty_place), maxQty), FancyToast.ERROR);
            }
        }

        @Override
        public void OnMinusClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            facility.setQty(facility.getQty()-1);
            if (facility.getQty() == 0) {
                int index = oleFacilityAdapter.isExistInSelected(facility.getId());
                if (index != -1) {
                    oleFacilityAdapter.selectedFacility.remove(index);
                }
            }
            if (selectedSlotDuration.isEmpty()) {
                setButtonText(selectedDuration);
            }
            else {
                setButtonText(selectedSlotDuration);
            }
            oleFacilityAdapter.notifyDataSetChanged();
        }
    };

    OleFastBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleFastBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos) {
            if (selectedFieldIndex != -1) { // unselect previous slot
                OleFastBookingFieldAdapter.ViewHolder holder = (OleFastBookingFieldAdapter.ViewHolder) binding.fieldRecyclerVu.findViewHolderForAdapterPosition(selectedFieldIndex);
                if (holder != null && holder.slotAdapter != null) {
                    holder.slotAdapter.setSelectedSlotIndex(-1, "");
                }
                else {
                    return;
                }
            }
            OleBookingSlot slot = fieldList.get(fieldPos).getSlotList().get(slotPos);
            if (slot.getStatus().equalsIgnoreCase("booked")) {
                confirmCancelBookingAlert(slot.getBookingId(), slotPos, fieldPos, slot.getBookingStatus());
                return;
            }
            if (slot.getStatus().equalsIgnoreCase("hidden")) {
                Functions.showToast(getContext(), getString(R.string.you_hide_this_slot), FancyToast.SUCCESS);
                return;
            }

            selectedFieldIndex = fieldPos;
            selectedSlotIndex = slotPos;
            fieldDetail = fieldList.get(fieldPos);
            fieldId = fieldDetail.getId();
            populateData(fieldDetail);
            slotClicked(fieldHolder, v, slot);
        }

        @Override
        public void OnItemLongClick(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos) {
            OleBookingSlot slot = fieldList.get(fieldPos).getSlotList().get(slotPos);
//            if (!slot.getStatus().equalsIgnoreCase("available")) {
//                showBookingAlert(slot.getStart(), slot.getEnd());
//            }

            if (!slot.getStatus().equalsIgnoreCase("available")) {
                showWaitingUsersList(slot);
            } else if (slot.getStatus().equalsIgnoreCase("available") && !slot.getWaitingList().isEmpty()) {
                showWaitingUsersList(slot);
                selectedFieldIndex = fieldPos;
                selectedSlotIndex = slotPos;
                fieldDetail = fieldList.get(fieldPos);
                fieldId = fieldDetail.getId();
                slotClicked(fieldHolder, v, slot);
            }
        }
    };

    OleBookingDurationAdapter.OnItemClickListener durClickListener = new OleBookingDurationAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDuration = durationList.get(pos).getKey();
            durationAdapter.setSelectedDuration(selectedDuration);
            setButtonText(selectedDuration);
            selectedStartTime = "";
            getAllFieldsAPI(true, arrDate.get(selectedDateIndex).getDate());
        }
    };

    OleBookingDateAdapter.OnItemClickListener daysClickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (!selectedDuration.isEmpty()) {
                String date = arrDate.get(pos).getDate();
                selectedDateIndex = pos;
                daysAdapter.setSelectedDateIndex(pos);
                setFieldPriceByDay(date);
                selectedStartTime = "";
                offerDiscount = 0;
                setButtonText(selectedDuration);
                selectedDate = date;
                getAllFieldsAPI(true, selectedDate);
            }
            else {
                Functions.showToast(getContext(), getString(R.string.select_duration), FancyToast.ERROR);
            }
        }
    };

    private void slotClicked(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, OleBookingSlot slot) {
        if (selectedDuration.equalsIgnoreCase(v.slotDuration)) {
            selectedSlotDuration = v.slotDuration;
            selectedStartTime = slot.getStart();
            selectedEndTime = slot.getEnd();
            selectedSlotTime = slot.getSlot();
            selectedShift = slot.getShift();
            offerDiscount = 0;
            try {
                offerDiscount = checkOfferForSlot(selectedFieldIndex, slot.getStart(), slot.getEnd());
                if (selectedSlotDuration.isEmpty()) {
                    setButtonText(selectedDuration);
                }
                else {
                    setButtonText(selectedSlotDuration);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            fieldHolder.slotAdapter.setSelectedSlotIndex(selectedSlotIndex, selectedDate);
        }
        else {
            String msg = getResources().getString(R.string.slot_selected_dur_msg, selectedDuration, v.slotDuration, v.slotDuration);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.confirmation))
                    .setMessage(msg)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setButtonText(v.slotDuration);
                            selectedSlotDuration = v.slotDuration;
                            selectedStartTime = slot.getStart();
                            selectedEndTime = slot.getEnd();
                            selectedSlotTime = slot.getSlot();
                            selectedShift = slot.getShift();
                            offerDiscount = 0;
                            try {
                                offerDiscount = checkOfferForSlot(selectedFieldIndex, slot.getStart(), slot.getEnd());
                                if (selectedSlotDuration.isEmpty()) {
                                    setButtonText(selectedDuration);
                                }
                                else {
                                    setButtonText(selectedSlotDuration);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            fieldHolder.slotAdapter.setSelectedSlotIndex(selectedSlotIndex, selectedDate);
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

    private void confirmCancelBookingAlert(String bookingId, int position, int fieldPos, String bookingStatus) {
        String[] arr;
        if (!bookingStatus.equalsIgnoreCase("1")) {
            arr = new String[]{getResources().getString(R.string.add_waiting_user),getResources().getString(R.string.confirm_booking), getResources().getString(R.string.cancel_booking)};
        }
        else {
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
//                        if (arr.length == 2) {
//                            if (index == 0) {
//                                cancelConfirmBookingAPI(true, "confirm", bookingId, position, fieldPos);
//                            }
//                            else {
//                                cancelConfirmBookingAPI(true, "cancel", bookingId, position, fieldPos);
//                            }
//                        }
//                        else {
//                            cancelConfirmBookingAPI(true, "cancel", bookingId, position, fieldPos);
//                        }


                        if (arr.length == 3) {
                            if (index == 0) {
                                actionSheet.dismiss();
                                new Handler().postDelayed(() -> showWaitingUserSheet(bookingId), 200); // Adjust delay as needed
                            } else if (index == 1) {
                                cancelConfirmBookingAPI(true, "confirm", bookingId, position,fieldPos);
                            } else {
                                cancelConfirmBookingAPI(true, "cancel", bookingId, position,fieldPos);
                            }
                        }
                        else {
                            if (index == 0) {
                                actionSheet.dismiss();
                                new Handler().postDelayed(() -> showWaitingUserSheet(bookingId), 200); // Adjust delay as needed
                            } else if (index == 1) {
                                cancelConfirmBookingAPI(true, "cancel", bookingId, position,fieldPos);
                            }

                        }






                    }
                }).show();
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
        if (fieldDetail == null) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
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
        if (selectedStartTime.isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_time), FancyToast.ERROR);
            return;
        }
        if (fieldList.size() == 0) {
            return;
        }

        Intent intent = new Intent(getContext(), OleBookingInfoActivity.class);
        intent.putExtra("price",String.valueOf(Double.parseDouble(price)-offerDiscount+selectedFacilityPrice));
        intent.putExtra("duration",selectedSlotDuration);
        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
            intent.putExtra("size", "");
        }
        else {
            intent.putExtra("size",fieldDetail.getFieldSize().getName());
        }
        intent.putExtra("club_name",club.getName());
        intent.putExtra("field_name",fieldDetail.getName());
        intent.putExtra("currency",fieldDetail.getCurrency());
        intent.putExtra("club_id",club.getId());
        intent.putExtra("field_id",fieldId);
        intent.putExtra("date",selectedDate);
        intent.putExtra("time",selectedSlotTime);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
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
        if (club.getSlots60().equalsIgnoreCase("1")) {
            price = field.getOneHour();
        }
        else if (club.getSlots90().equalsIgnoreCase("1")) {
            price = field.getOneHalfHours();
        }
        else if (club.getSlots120().equalsIgnoreCase("1")) {
            price = field.getTwoHours();
        }

        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
            if (club.getSlots90().equalsIgnoreCase("1")) {
                price = field.getOneHalfHours();
            }
            else if (club.getSlots120().equalsIgnoreCase("1")) {
                price = field.getTwoHours();
            }
        }

        if (arrDate.size() > 0) {
            setFieldPriceByDay(arrDate.get(selectedDateIndex).getDate());
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
            setFieldPriceByDay(dateFormat.format(new Date()));
        }
        binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), price));
    }

    private void populateDuration(boolean isRefresh) {
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
        if (isRefresh) {
            durationAdapter.notifyDataSetChanged();
        }
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

    public double checkOfferForSlot(int fieldPos, String time1, String time2) throws ParseException {
        double result = 0;
        if (fieldList.get(fieldPos).getOffers().size() > 0) {
            for (OleOfferData oleOfferData : fieldList.get(fieldPos).getOffers()) {
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
                }
                else {
                    dateFormat.applyPattern("dd/MM/yyyy hh:mma");
                    offerStartDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimimgStart().split(" ")[1]));
                    offerEndDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimingEnd().split(" ")[1]));
                }

                if (((offerStartDate.compareTo(slot1DT)<0 || offerStartDate.compareTo(slot1DT) == 0)
                        && (slot1DT.compareTo(offerEndDate) < 0 || slot1DT.compareTo(offerEndDate) == 0))
                        && (offerStartDate.compareTo(slot2DT)<0 || offerStartDate.compareTo(slot2DT) == 0)
                        && (slot2DT.compareTo(offerEndDate) < 0 || slot2DT.compareTo(offerEndDate) == 0)
                        && isFound && !isExpired) {
                    if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                        result = 0;
                        offerDiscountType = "";
                    }
                    else {
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
            }
            else if (duration.equalsIgnoreCase("1.5")) {
                price = fieldDetail.getOneHalfHours();
            }
            else if (duration.equalsIgnoreCase("2")) {
                price = fieldDetail.getTwoHours();
            }
            else {
                price = "";
            }

            if (price.isEmpty()) {
                binding.btnBook.setTitle(getString(R.string.book_now));
            }
            else {
                if (offerDiscountType.isEmpty() || offerDiscountType.equalsIgnoreCase("amount")) {
                    double p = Double.parseDouble(price) - offerDiscount;
                    p = p + selectedFacilityPrice;
                    binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), p));
                }
                else {
                    double p = Double.parseDouble(price);
                    p = p - ((offerDiscount / 100) * p);
                    p = p + selectedFacilityPrice;
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
                }
                else {
                    p = p + Double.parseDouble(facility.getPrice());
                }
            }
        }
        return p;
    }

    private void getAllFieldsAPI(boolean isLoader, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), club.getId(), selectedDuration, date, "1", "1");
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
                            arrDate.clear();
                            fieldList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                            }
                            for (int i = 0; i < arrD.length(); i++) {
                                arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                            }
                            daysAdapter.setDataSource(arrDate.toArray());
                            fieldAdapter.setSelectedDate(selectedDate);
                            fieldAdapter.notifyDataSetChanged();
                        }
                        else {
                            fieldList.clear();
                            fieldAdapter.notifyDataSetChanged();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        fieldList.clear();
                        fieldAdapter.notifyDataSetChanged();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    fieldList.clear();
                    fieldAdapter.notifyDataSetChanged();
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                fieldList.clear();
                fieldAdapter.notifyDataSetChanged();
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
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

    private void addBookingAPI(String registeredUserId, String userName, String phone, String discount, String paymentType, String days, String fromDate, String toDate, String addPrice, int promoDiscount, String promoId, double offerDis, String padelPlayers, String padelPlayersForPayment) {
        double totalP = 0;
        if (!addPrice.isEmpty()) {
            totalP = Double.parseDouble(price) + Double.parseDouble(addPrice);
        }
        else {
            totalP = Double.parseDouble(price);
        }

        if (offerDiscountType.equalsIgnoreCase("percent")) {
            double p = Double.parseDouble(price);
            offerDis = (offerDis / 100) * p;
        }

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
                }
                else {
                    object.put("qty", "");
                }
                array.put(object);
            }

            facilities = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String ladySlot = fieldDetail.getSlotList().get(selectedSlotIndex).getLadySlot();

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
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
                        }
                        else if (object.getInt(Constants.kStatus) == 409) {
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

    private void cancelConfirmBookingAPI(boolean isLoader, String status, String bookingId, int pos, int fieldPos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
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
                                fieldList.get(fieldPos).getSlotList().get(pos).setStatus("available");
                                fieldList.get(fieldPos).getSlotList().get(pos).setUserName("");
                                OleFastBookingFieldAdapter.ViewHolder holder = (OleFastBookingFieldAdapter.ViewHolder) binding.fieldRecyclerVu.findViewHolderForAdapterPosition(fieldPos);
                                if (holder != null) {
                                    holder.slotAdapter.notifyItemChanged(pos);
                                }
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
    protected void showWaitingUserSheet(String bookingId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Remove existing fragment with the same tag, if any
        Fragment existingFragment = fragmentManager.findFragmentByTag("AddWaitingUserDialogFragment");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }

        // Add transaction to backstack if necessary
        fragmentTransaction.addToBackStack(null);

        // Create and show the new dialog fragment
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
        dialogFragment.setDialogCallback((df,type, id, name, phone) -> {
            df.dismiss();
            if (type.equalsIgnoreCase("booking")){
                if (!phone.isEmpty()){
                    isWaitingUser = "1";
                    bookClicked(name, phone);
                }
            }else{
                if (id !=null){
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
                            getAllFieldsAPI(true, selectedDate);
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


    private void addUserToWaitingList(String bookingId, String clubId, String name, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addUserToWaitingList(bookingId,clubId, name, phone);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);

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