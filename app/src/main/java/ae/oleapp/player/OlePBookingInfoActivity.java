package ae.oleapp.player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import ae.oleapp.adapters.OleBookingInfoDayAdapter;
import ae.oleapp.adapters.OlePadelPlayerListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPBookingInfoBinding;
import ae.oleapp.dialogs.OleAddCardDialogFragment;
import ae.oleapp.dialogs.OleCardListDialogFragment;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OleUserCard;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePBookingInfoActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPBookingInfoBinding binding;
    private String price = "";
    private String currency = "";
    private String bookingPrice = "";
    private String duration = "";
    private String size = "";
    private String clubName = "";
    private String fieldName = "";
    private String continuousAllowed = "";
    private String clubId = "";
    private String fieldId = "";
    private String date = "";
    private String time = "";
    private int paymentType = 1;
    private boolean isOffer = false;
    private int promoDiscount = 0;
    private String promoId = "";
    private String ownerDiscount = "";
    private double facPrice = 0;
    private final int CASH = 1;
    private final int CARD = 2;
    private final int WALLET = 3;
    private final int SPAY = 4;
    private final List<OleUserCard> oleUserCards = new ArrayList<>();
    private String selectedCardId = "";
    private String finalPrice = "";
    private double walletAmount = 0;
    private String partnerPrice = "";
    private final List<OlePlayerInfo> partnerList = new ArrayList<>();
    private OlePadelPlayerListAdapter adapter;
    private List<OleKeyValuePair> dayList = new ArrayList<>();
    private boolean isSchedule = false;
    private OleBookingInfoDayAdapter dayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPBookingInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.booking_info);

        populateData();

        cashClicked();

        getWalletDataAPI(clubId, new WalletDataCallback() {
            @Override
            public void getWalletData(String amount, String paymentMethod, String currency, String shopPaymentMethod) {
                if (!amount.isEmpty()) {
                    walletAmount = Double.parseDouble(amount);
                    binding.tvCredit.setText(String.format("%s %s", amount, currency));
                }
                else {
                    binding.tvCredit.setText(String.format("0 %s", currency));
                }
                if (!paymentMethod.isEmpty()) {
                    setPaymentMethod(paymentMethod);
                }
            }
        });

        getOwnerDiscountAPI();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePadelPlayerListAdapter(getContext(), partnerList);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCurrency(currency);
        binding.recyclerVu.setAdapter(adapter);

        isSchedule = false;
        binding.scheduleVu.setVisibility(View.GONE);

        dayList = Functions.getDays(getContext());
        LinearLayoutManager dayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(dayLayoutManager);
        dayAdapter = new OleBookingInfoDayAdapter(getContext(), dayList);
        dayAdapter.setOnItemClickListener(dayClickListener);
        binding.daysRecyclerVu.setAdapter(dayAdapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);
        binding.relCard.setOnClickListener(this);
        binding.relSpay.setOnClickListener(this);
        binding.relCash.setOnClickListener(this);
        binding.relWallet.setOnClickListener(this);
        binding.relAddCard.setOnClickListener(this);
        binding.imgDrop.setOnClickListener(this);
        binding.selectPartnerVu.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);

        binding.scheduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                scheduleSwitchChanged();
            }
        });
    }

    private void setPaymentMethod(String method) {
        if (method.equalsIgnoreCase("cash")) {
            binding.relWallet.setVisibility(View.GONE);
            binding.relAddCard.setVisibility(View.GONE);
            binding.relCard.setVisibility(View.GONE);
            binding.relSpay.setVisibility(View.GONE);
            binding.relCvv.setVisibility(View.GONE);
            cashClicked();
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                binding.selectPartnerVu.setVisibility(View.GONE);
            }
        }
        if (method.equalsIgnoreCase("card")) {
            binding.relCash.setVisibility(View.GONE);
            cardClicked();
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                binding.selectPartnerVu.setVisibility(View.VISIBLE);
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

    OleBookingInfoDayAdapter.OnItemClickListener dayClickListener = new OleBookingInfoDayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            dayAdapter.selectDay(dayList.get(pos));
        }
    };

    OlePadelPlayerListAdapter.OnItemClickListener onItemClickListener = new OlePadelPlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            adapter.selectItem(partnerList.get(pos));
            setBtnPrice();
        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.remove))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                OlePlayerInfo info = partnerList.get(pos);
                                for (int i = 0; i < adapter.selectedList.size(); i++) {
                                    if (adapter.selectedList.get(i).getId().equalsIgnoreCase(info.getId())) {
                                        adapter.selectedList.remove(i);
                                        break;
                                    }
                                }
                                partnerList.remove(pos);
                                if (partnerList.isEmpty()) {
                                    binding.partnerVu.setVisibility(View.GONE);
                                }
                                else {
                                    binding.partnerVu.setVisibility(View.VISIBLE);
                                }
                                setBtnPrice();
                            }
                        }
                    }).show();
        }
    };

    private void populateData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            price = bundle.getString("price", "");
            bookingPrice = bundle.getString("booking_price", "");
            currency = bundle.getString("currency", "");
            duration = bundle.getString("duration", "");
            size = bundle.getString("size", "");
            clubName = bundle.getString("club_name", "");
            fieldName = bundle.getString("field_name", "");
            continuousAllowed = bundle.getString("continuous_allowed", "");
            clubId = bundle.getString("club_id", "");
            fieldId = bundle.getString("field_id", "");
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
            isOffer = bundle.getBoolean("is_offer", false);
            facPrice = bundle.getDouble("fac_price", 0);
        }
        finalPrice = price;
        binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.confirm), finalPrice, currency));
        partnerPrice = String.valueOf(Double.parseDouble(finalPrice) / 4);
        binding.tvDuration.setText(String.format("%s %s", duration, getResources().getString(R.string.hour)));
        if (size.equalsIgnoreCase("")) {
            binding.tvFieldName.setText(fieldName);
        }
        else {
            binding.tvFieldName.setText(String.format("%s (%s)", fieldName, size));
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

        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo !=null) {
            binding.etName.setText(userInfo.getName());
            binding.etMobile.setText(userInfo.getPhone());
        }

        binding.btnDel.setVisibility(View.GONE);
        binding.tvPromoDis.setVisibility(View.GONE);

        if (continuousAllowed.equalsIgnoreCase("1")) {
            binding.continuousBookingVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.continuousBookingVu.setVisibility(View.GONE);
        }

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.selectPartnerVu.setVisibility(View.VISIBLE);
            binding.partnerVu.setVisibility(View.GONE);
        }
        else {
            binding.selectPartnerVu.setVisibility(View.GONE);
            binding.partnerVu.setVisibility(View.GONE);
        }

    }

    private void applyDiscount() {
        binding.btnApply.setVisibility(View.GONE);
        binding.btnDel.setVisibility(View.VISIBLE);
        binding.etPromo.setEnabled(false);
        binding.tvPromoDis.setVisibility(View.VISIBLE);
        binding.tvPromoDis.setText(getString(R.string.promo_place, String.valueOf(promoDiscount), currency));
        if (isOffer) { // promo code priority
            finalPrice = String.valueOf(Double.parseDouble(bookingPrice) - promoDiscount);
        }
        else {
            finalPrice = String.valueOf(Double.parseDouble(price) - promoDiscount);
        }
        setBtnPrice();
    }

    private void applyCardDiscount(String p, String discount) {
        finalPrice = String.valueOf(Double.parseDouble(p) + facPrice);
        setBtnPrice();
        ownerDiscount = discount;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnConfirm) {
            btnConfirmClicked();
        }
        else if (v == binding.btnApply) {
            btnApplyClicked();
        }
        else if (v == binding.btnDel) {
            btnDelClicked();
        }
        else if (v == binding.relCard) {
            cardClicked();
        }
        else if (v == binding.relSpay) {
            spayClicked();
        }
        else if (v == binding.relCash) {
            cashClicked();
        }
        else if (v == binding.relWallet) {
            walletClicked();
        }
        else if (v == binding.relAddCard) {
            addCardClicked();
        }
        else if (v == binding.imgDrop) {
            imgDropdownClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
        else if (v == binding.selectPartnerVu) {
            Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
            intent.putExtra("is_selection", true);
            intent.putExtra("is_three_selection", true);
            startActivityForResult(intent, 106);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            for (OlePlayerInfo info : list) {
                if (!checkPartnerExist(info.getId())) {
                    partnerList.add(info);
                }
            }
            binding.partnerVu.setVisibility(View.VISIBLE);
            setBtnPrice();
        }
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

    private void setBtnPrice() {
        if (partnerList.size() > 0) {
            partnerPrice = String.valueOf(Double.parseDouble(finalPrice) / 4);
            adapter.setDatasource(partnerList, partnerPrice, currency);
            if (adapter.selectedList.size() > 0) {
                double p = Double.parseDouble(partnerPrice);
                binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.confirm), p * (adapter.selectedList.size()+1), currency));
            }
            else {
                binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.confirm), partnerPrice, currency));
            }
        }
        else {
            binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.confirm), finalPrice, currency));
        }
    }

    private boolean checkPartnerExist(String id) {
        boolean result = false;
        for (OlePlayerInfo info: partnerList) {
            if (info.getId().equalsIgnoreCase(id)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void btnConfirmClicked() {
        if (partnerList.size() > 0 && partnerList.size() != 3) {
            Functions.showToast(getContext(), getString(R.string.must_select_three_partner), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (partnerList.size() > 0 && paymentType == CASH) {
            Functions.showToast(getContext(), getString(R.string.must_pay_online_partner), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (paymentType == CARD) {
            showAlert("card");
        }
        else if (paymentType == SPAY) {
            showAlert("samsung");
        }
        else if (paymentType == WALLET) {
            selectedCardId = "";
            if (!finalPrice.isEmpty()) {
                if (walletAmount > 0) {
                    showAlert("wallet");
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.insufficient_balance_wallet), FancyToast.ERROR);
                }
            }
        }
        else {
            selectedCardId = "";
            showAlert("cash");
        }
    }

    private void showAlert(String method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.confirmation))
                .setMessage(getResources().getString(R.string.do_you_want_book_at_place, time, binding.tvDate.getText().toString()))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String padelPlayers = "";
                        String padelPlayersForPaymnet = "";
                        if (partnerList.size() > 0) {
                            for (OlePlayerInfo info : partnerList) {
                                if (padelPlayers.isEmpty()) {
                                    padelPlayers = info.getId();
                                }
                                else {
                                    padelPlayers = String.format("%s,%s", padelPlayers, info.getId());
                                }
                            }
                            if (adapter.selectedList.size() > 0) {
                                for (OlePlayerInfo info : adapter.selectedList) {
                                    if (padelPlayersForPaymnet.isEmpty()) {
                                        padelPlayersForPaymnet = info.getId();
                                    }
                                    else {
                                        padelPlayersForPaymnet = String.format("%s,%s", padelPlayersForPaymnet, info.getId());
                                    }
                                }
                            }
                        }

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("name",binding.etName.getText().toString());
                        returnIntent.putExtra("phone",binding.etMobile.getText().toString());
                        returnIntent.putExtra("paymentType",method);
                        returnIntent.putExtra("promoDiscount",promoDiscount);
                        returnIntent.putExtra("promoId",promoId);
                        returnIntent.putExtra("cardDiscount",ownerDiscount);
                        returnIntent.putExtra("cardId",selectedCardId);
                        returnIntent.putExtra("cvv",binding.etCvv.getRawText());
                        returnIntent.putExtra("padel_players",padelPlayers);
                        returnIntent.putExtra("padel_players_payment",padelPlayersForPaymnet);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void btnApplyClicked() {
        if (binding.etPromo.getText().toString().isEmpty()) {
            return;
        }
        getPromoDiscountAPI(binding.etPromo.getText().toString());
    }

    private void btnDelClicked() {
        binding.btnApply.setVisibility(View.VISIBLE);
        binding.btnDel.setVisibility(View.GONE);
        binding.etPromo.setEnabled(true);
        binding.etPromo.setText("");
        promoDiscount = 0;
        promoId = "";
        binding.tvPromoDis.setVisibility(View.GONE);
        finalPrice = price;
        setBtnPrice();
    }

    private void cardClicked() {
        paymentType = CARD;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.check);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
    }

    private void spayClicked() {
        paymentType = SPAY;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.check);
    }

    private void cashClicked() {
        paymentType = CASH;
        binding.imgVuCash.setImageResource(R.drawable.check);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.tvCard.setText(R.string.card);
        binding.relAddCard.setVisibility(View.GONE);
        binding.relCvv.setVisibility(View.GONE);
    }

    private void walletClicked() {
        paymentType = WALLET;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.check);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.tvCard.setText(R.string.card);
        binding.relAddCard.setVisibility(View.GONE);
        binding.relCvv.setVisibility(View.GONE);
    }

    private void addCardClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("AddCardDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleAddCardDialogFragment dialogFragment = new OleAddCardDialogFragment();
        dialogFragment.setDialogCallback(new OleAddCardDialogFragment.AddCardDialogCallback() {
            @Override
            public void didAddCard() {
                getCardsAPI();
            }
        });
        dialogFragment.show(fragmentTransaction, "AddCardDialogFragment");
    }

    private void imgDropdownClicked() {
        if (paymentType == CARD && oleUserCards.size() > 0) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("CardListDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleCardListDialogFragment dialogFragment = new OleCardListDialogFragment(oleUserCards);
            dialogFragment.setCardListDialogCallback(new OleCardListDialogFragment.CardListDialogCallback() {
                @Override
                public void didSelectCard(OleUserCard oleUserCard) {
                    binding.tvCard.setText(oleUserCard.getCardNumber());
                    selectedCardId = oleUserCard.getCardId();
                }
            });
            dialogFragment.show(fragmentTransaction, "CardListDialogFragment");
        }
    }

    private void getPromoDiscountAPI(String promoCode) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.promoDiscount(Functions.getAppLang(getContext()), fieldId, clubId, bookingPrice, date, promoCode, duration, Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            promoDiscount = object.getJSONObject(Constants.kData).getInt("discounted_amount");
                            promoId = object.getJSONObject(Constants.kData).getString("id");
                            applyDiscount();
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

    private void getOwnerDiscountAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cardDiscount(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, bookingPrice, date, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String price = object.getJSONObject(Constants.kData).getString("price");
                            String discount = object.getJSONObject(Constants.kData).getString("discount");
                            applyCardDiscount(price, discount);
                        }
                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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

    private void getCardsAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.userCards(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleUserCards.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleUserCards.add(gson.fromJson(arr.get(i).toString(), OleUserCard.class));
                            }
                            if (oleUserCards.size()>0) {
                                binding.tvCard.setText(oleUserCards.get(0).getCardNumber());
                                selectedCardId = oleUserCards.get(0).getCardId();
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
