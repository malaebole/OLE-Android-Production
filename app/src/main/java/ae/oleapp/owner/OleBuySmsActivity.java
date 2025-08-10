package ae.oleapp.owner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityBuySmsBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OleSmsData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBuySmsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBuySmsBinding binding;
    private String clubId = "";
    private boolean isUpdate = false;
    private OleSmsData oleSmsData;
    private double smsPrice = 0;
    private int arSmsLength = 0;
    private int enSmsLength = 0;
    private int allPlayers = 0;
    private int callBookedPlayers = 0;
    private int onlineBookedPlayers = 0;
    private int topTenPlayers = 0;
    private int msgCount = 0;
    private String smsFor = "";
    private String currency = "";
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBuySmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.sms);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isUpdate = bundle.getBoolean("is_update", false);
            clubId = bundle.getString("club_id", "");
            if (isUpdate) {
                Gson gson = new Gson();
                oleSmsData = gson.fromJson(bundle.getString("data", ""), OleSmsData.class);
            }
        }

        if (isUpdate) {
            binding.btnSendReq.setVisibility(View.GONE);
            binding.etMsg.setText(oleSmsData.getSmsText());
            binding.etDate.setText(oleSmsData.getSendingDate());
            binding.etTime.setText(oleSmsData.getSendingTime());
            binding.tvNote.setText(oleSmsData.getNote());
            binding.tvPrice.setText(String.format("%s %s", oleSmsData.getPaidPrice(), oleSmsData.getCurrency()));
            binding.tvSmsCount.setText(String.format(Locale.ENGLISH, "%s %s", oleSmsData.getTotalSms(), getString(R.string.sms)));
            binding.etMsg.setEnabled(false);
        }
        else {
            binding.tvNote.setVisibility(View.GONE);
            binding.etDate.setOnClickListener(this);
            binding.etTime.setOnClickListener(this);
            binding.btnSendReq.setOnClickListener(this);
            binding.vuTopTen.setOnClickListener(this);
            binding.vuAllP.setOnClickListener(this);
            binding.vuCall.setOnClickListener(this);
            binding.vuOnline.setOnClickListener(this);
            binding.etMsg.addTextChangedListener(textWatcher);
        }

        binding.btnSendReq.setAlpha(0.5f);

        customerCountAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int wordCount = binding.etMsg.getText().toString().length();
            setCount(wordCount);
        }
    };

    private void setCount(int wordCount) {
        msgCount = 0;
        smsFor = "";
        binding.btnSendReq.setAlpha(0.5f);
        binding.imgAllPlayer.setImageResource(R.drawable.uncheck);
        binding.imgCallPlayer.setImageResource(R.drawable.uncheck);
        binding.imgOnlinePlayer.setImageResource(R.drawable.uncheck);
        binding.imgTopTenPlayer.setImageResource(R.drawable.uncheck);
        if (Functions.isArabic(binding.etMsg.getText().toString())) {
            if (wordCount == 0) {
                msgCount = 0;
            }
            else if (wordCount <= arSmsLength) {
                msgCount = 1;
            }
            else if (wordCount % arSmsLength == 0) {
                msgCount = wordCount / arSmsLength;
            }
            else {
                msgCount = (wordCount / arSmsLength) + 1;
            }
        }
        else {
            if (wordCount == 0) {
                msgCount = 0;
            }
            else if (wordCount <= enSmsLength) {
                msgCount = 1;
            }
            else if (wordCount % enSmsLength == 0) {
                msgCount = wordCount / enSmsLength;
            }
            else {
                msgCount = (wordCount / enSmsLength) + 1;
            }
        }
        binding.tvWordCount.setText(String.format(Locale.ENGLISH, "%d (%d %s)", wordCount, msgCount, getString(R.string.sms)));
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.etDate) {
            dateClicked();
        }
        else if (v == binding.etTime) {
            timeClicked();
        }
        else if (v == binding.btnSendReq) {
            sendClicked();
        }
        else if (v == binding.vuAllP) {
            allPlayerClicked();
        }
        else if (v == binding.vuCall) {
            callClicked();
        }
        else if (v == binding.vuOnline) {
            onlineClicked();
        }
        else if (v == binding.vuTopTen) {
            topTenClicked();
        }
    }

    private void allPlayerClicked() {
        if (binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.write_msg), FancyToast.ERROR);
            return;
        }
        smsFor = "all";
        binding.btnSendReq.setAlpha(1.0f);
        binding.imgAllPlayer.setImageResource(R.drawable.check);
        binding.imgCallPlayer.setImageResource(R.drawable.uncheck);
        binding.imgOnlinePlayer.setImageResource(R.drawable.uncheck);
        binding.imgTopTenPlayer.setImageResource(R.drawable.uncheck);
        int sms = msgCount * allPlayers;
        totalAmount = sms * smsPrice;
        binding.tvSmsCount.setText(String.format(Locale.ENGLISH, "%d %s", sms, getString(R.string.sms)));
        binding.tvPrice.setText(String.format(Locale.ENGLISH, "%.2f %s", totalAmount, currency));
    }

    private void callClicked() {
        if (binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.write_msg), FancyToast.ERROR);
            return;
        }
        smsFor = "call";
        binding.btnSendReq.setAlpha(1.0f);
        binding.imgAllPlayer.setImageResource(R.drawable.uncheck);
        binding.imgCallPlayer.setImageResource(R.drawable.check);
        binding.imgOnlinePlayer.setImageResource(R.drawable.uncheck);
        binding.imgTopTenPlayer.setImageResource(R.drawable.uncheck);
        int sms = msgCount * callBookedPlayers;
        totalAmount = sms * smsPrice;
        binding.tvSmsCount.setText(String.format(Locale.ENGLISH, "%d %s", sms, getString(R.string.sms)));
        binding.tvPrice.setText(String.format(Locale.ENGLISH, "%.2f %s", totalAmount, currency));
    }

    private void onlineClicked() {
        if (binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.write_msg), FancyToast.ERROR);
            return;
        }
        smsFor = "app";
        binding.btnSendReq.setAlpha(1.0f);
        binding.imgAllPlayer.setImageResource(R.drawable.uncheck);
        binding.imgCallPlayer.setImageResource(R.drawable.uncheck);
        binding.imgOnlinePlayer.setImageResource(R.drawable.check);
        binding.imgTopTenPlayer.setImageResource(R.drawable.uncheck);
        int sms = msgCount * onlineBookedPlayers;
        totalAmount = sms * smsPrice;
        binding.tvSmsCount.setText(String.format(Locale.ENGLISH, "%d %s", sms, getString(R.string.sms)));
        binding.tvPrice.setText(String.format(Locale.ENGLISH, "%.2f %s", totalAmount, currency));
    }

    private void topTenClicked() {
        if (binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.write_msg), FancyToast.ERROR);
            return;
        }
        smsFor = "top_ten";
        binding.btnSendReq.setAlpha(1.0f);
        binding.imgAllPlayer.setImageResource(R.drawable.uncheck);
        binding.imgCallPlayer.setImageResource(R.drawable.uncheck);
        binding.imgOnlinePlayer.setImageResource(R.drawable.uncheck);
        binding.imgTopTenPlayer.setImageResource(R.drawable.check);
        int sms = msgCount * topTenPlayers;
        totalAmount = sms * smsPrice;
        binding.tvSmsCount.setText(String.format(Locale.ENGLISH, "%d %s", sms, getString(R.string.sms)));
        binding.tvPrice.setText(String.format(Locale.ENGLISH, "%.2f %s", totalAmount, currency));
    }

    private void dateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
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
                binding.etTime.setText(formatter.format(calendar.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private void populateData() {
        binding.tvAllPlayer.setText(String.valueOf(allPlayers));
        binding.tvCallPlayer.setText(String.valueOf(callBookedPlayers));
        binding.tvOnlinePlayer.setText(String.valueOf(onlineBookedPlayers));
        binding.tvTopTenPlayer.setText(String.valueOf(topTenPlayers));
        binding.imgAllPlayer.setImageResource(R.drawable.uncheck);
        binding.imgCallPlayer.setImageResource(R.drawable.uncheck);
        binding.imgOnlinePlayer.setImageResource(R.drawable.uncheck);
        binding.imgTopTenPlayer.setImageResource(R.drawable.uncheck);
        if (isUpdate) {
            setCount(oleSmsData.getSmsText().length());
            if (oleSmsData.getSmsFor().equalsIgnoreCase("top_ten")) {
                binding.imgTopTenPlayer.setImageResource(R.drawable.check);
            }
            else if (oleSmsData.getSmsFor().equalsIgnoreCase("call")) {
                binding.imgCallPlayer.setImageResource(R.drawable.check);
            }
            else if (oleSmsData.getSmsFor().equalsIgnoreCase("app")) {
                binding.imgOnlinePlayer.setImageResource(R.drawable.check);
            }
            else {
                binding.imgAllPlayer.setImageResource(R.drawable.check);
            }
        }
    }

    private void sendClicked() {
        if (smsFor.equalsIgnoreCase("")) {
            return;
        }
        if (binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.write_msg), FancyToast.ERROR);
            return;
        }
        if (binding.etDate.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_date), FancyToast.ERROR);
            return;
        }
        if (binding.etTime.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_time), FancyToast.ERROR);
            return;
        }
        openPaymentDialog(String.format(Locale.ENGLISH, "%.2f", totalAmount), currency, "", "", "", true, true, "", new OlePaymentDialogFragment.PaymentDialogCallback() {
            @Override
            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                sendSmsAPI(true, binding.etMsg.getText().toString(), binding.etDate.getText().toString(), binding.etTime.getText().toString(), String.format(Locale.ENGLISH, "%.2f", totalAmount), paymentMethod, orderRef);
            }
        });
    }

    private void customerCountAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.customerCountForSms(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            callBookedPlayers = object.getJSONObject(Constants.kData).getInt("call_booked_players");
                            onlineBookedPlayers = object.getJSONObject(Constants.kData).getInt("app_booked_players");
                            topTenPlayers = object.getJSONObject(Constants.kData).getInt("top_ten_players");
                            smsPrice = object.getJSONObject(Constants.kData).getDouble("per_sms_price");
                            arSmsLength = object.getJSONObject(Constants.kData).getInt("one_sms_length_ar");
                            enSmsLength = object.getJSONObject(Constants.kData).getInt("one_sms_length_en");
                            currency = object.getJSONObject(Constants.kData).getString("currency");
                            allPlayers = callBookedPlayers + onlineBookedPlayers;
                            populateData();
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

    private void sendSmsAPI(boolean isLoader, String msg, String date, String time, String price, String method, String orderRef) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendSMS(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, smsFor, msg, String.valueOf(msgCount), date, time, price, method, Functions.getIPAddress(), orderRef);
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