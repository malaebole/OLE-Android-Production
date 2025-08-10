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

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityCreateDiscountCardBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleDiscountCard;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreateDiscountCardActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreateDiscountCardBinding binding;
    private String clubId = "";
    private boolean isPerc = false;
    private OleDiscountCard oleDiscountCard = null;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreateDiscountCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.loyalty_card);

        amountClicked();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            clubId = bundle.getString("club_id", "");
            oleDiscountCard = gson.fromJson(bundle.getString("card", ""), OleDiscountCard.class);
        }

        binding.tvCurrency.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        if (oleDiscountCard == null) {
            // new
            binding.btnDelete.setVisibility(View.GONE);
            binding.tvBtntitle.setText(R.string.add_now);
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
        }
        else {
            // update
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.tvBtntitle.setText(R.string.update);
            populateData();
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.percVu.setOnClickListener(this);
        binding.amountVu.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
        }
    };

    private void populateData() {
        clubId = oleDiscountCard.getClubId();
        for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
            if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                oleRankClubAdapter.setSelectedIndex(i);
                break;
            }
        }
        binding.etTitle.setText(oleDiscountCard.getTitle());
        binding.etFromDate.setText(oleDiscountCard.getFromDate());
        binding.etToDate.setText(oleDiscountCard.getToDate());
        binding.etReqBooking.setText(oleDiscountCard.getTargetBooking());
        if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
            amountClicked();
            binding.etAmount.setText(oleDiscountCard.getDiscountValue());
        }
        else {
            percClicked();
            binding.etPerc.setText(oleDiscountCard.getDiscountValue());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDelete) {
            deleteClicked();
        }
        else if (v == binding.etFromDate) {
            fromDateClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
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
                            deleteDiscountCardAPI();
                        }
                    }
                }).show();
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
        binding.imgAmount.setImageResource(R.drawable.p_uncheck);
        binding.etAmount.setText("");
        binding.etPerc.setEnabled(true);
        binding.relPerc.setAlpha(1.0f);
        binding.etAmount.setEnabled(false);
        binding.relAmount.setAlpha(0.5f);
    }

    private void amountClicked() {
        isPerc = false;
        binding.imgPerc.setImageResource(R.drawable.p_uncheck);
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
        if (binding.etTitle.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_title), FancyToast.ERROR);
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
        if (binding.etPerc.getText().toString().isEmpty() && binding.etAmount.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_discount_value), FancyToast.ERROR);
            return;
        }
        if (binding.etReqBooking.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_req_booking), FancyToast.ERROR);
            return;
        }
        if (Integer.parseInt(binding.etReqBooking.getText().toString()) > 6) {
            Functions.showToast(getContext(), getString(R.string.req_booking_shouldnt_six), FancyToast.ERROR);
            return;
        }

        if (oleDiscountCard == null) {
            // new
            if (isPerc) {
                addDiscountCardAPI(binding.etTitle.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), binding.etReqBooking.getText().toString(), binding.etPerc.getText().toString(), "percent");
            }
            else {
                addDiscountCardAPI(binding.etTitle.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), binding.etReqBooking.getText().toString(), binding.etAmount.getText().toString(), "amount");
            }
        }
        else {
            // update
            if (isPerc) {
                updateDiscountCardAPI(binding.etTitle.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), binding.etReqBooking.getText().toString(), binding.etPerc.getText().toString(), "percent");
            }
            else {
                updateDiscountCardAPI(binding.etTitle.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), binding.etReqBooking.getText().toString(), binding.etAmount.getText().toString(), "amount");
            }
        }
    }

    private void addDiscountCardAPI(String title, String from, String to, String bookingReq, String value, String type) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addDiscountCard(Functions.getAppLang(getContext()), title, Functions.getPrefValue(getContext(), Constants.kUserID), clubId, bookingReq, type, value, from, to);
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

    private void updateDiscountCardAPI(String title, String from, String to, String bookingReq, String value, String type) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateDiscountCard(Functions.getAppLang(getContext()), title, Functions.getPrefValue(getContext(), Constants.kUserID), clubId, bookingReq, type, value, from, to, oleDiscountCard.getId());
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

    private void deleteDiscountCardAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteDiscountCard(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), oleDiscountCard.getId());
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