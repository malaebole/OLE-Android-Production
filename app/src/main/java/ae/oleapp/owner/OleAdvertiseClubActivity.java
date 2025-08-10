package ae.oleapp.owner;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAdvertiseClubBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleAdvertiseClub;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleAdvertiseClubActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAdvertiseClubBinding binding;
    private String clubId = "", cityIds = "", countryId = "", featuredPrice = "", totalPrice = "", featuredId = "";
    private OleAdvertiseClub oleAdvertiseClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAdvertiseClubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.advertise_club);

        binding.tvStatus.setText("");
        binding.tvDays.setText("");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            featuredPrice = bundle.getString("featured_price", "");
            countryId = bundle.getString("country_id", "");
            if (bundle.containsKey("data")) {
                Gson gson = new Gson();
                oleAdvertiseClub = gson.fromJson(bundle.getString("data", ""), OleAdvertiseClub.class);
                populateData();
            }
        }

        if (AppManager.getInstance().countries.isEmpty()) {
            getCountriesAPI(new CountriesCallback() {
                @Override
                public void getCountries(List<OleCountry> countries) {
                    AppManager.getInstance().countries = countries;
                }
            });
        }

        binding.tvDayPrice.setText(getString(R.string.aed_place_day, featuredPrice, Functions.getPrefValue(getContext(), Constants.kCurrency)));
        binding.bar.backBtn.setOnClickListener(this);
        binding.btnFeature.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
    }

    private void populateData() {
        featuredId = oleAdvertiseClub.getFeaturedId();
        cityIds = oleAdvertiseClub.getCityIds();
        binding.etCity.setText(oleAdvertiseClub.getCities());
        binding.etToDate.setText(oleAdvertiseClub.getTo());
        binding.etFromDate.setText(oleAdvertiseClub.getFrom());
        totalPrice = oleAdvertiseClub.getPrice();
        binding.tvPrice.setText(String.format("%s %s", totalPrice, oleAdvertiseClub.getCurrency()));
        if (oleAdvertiseClub.getStatus().equalsIgnoreCase("Active")) {
            binding.tvStatus.setText(R.string.active);
            binding.tvStatus.setTextColor(Color.parseColor("#49D483"));
        }
        else if (oleAdvertiseClub.getStatus().equalsIgnoreCase("Expired soon")) {
            binding.tvStatus.setText(R.string.expired_soon);
            binding.tvStatus.setTextColor(Color.parseColor("#F02301"));
        }
        else if (oleAdvertiseClub.getStatus().equalsIgnoreCase("Expired")) {
            binding.tvStatus.setText(R.string.expired);
            binding.tvStatus.setTextColor(Color.parseColor("#F02301"));
        }

        if (oleAdvertiseClub.getStatus().equalsIgnoreCase("Expired")) {
            binding.btnFeature.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnFeature.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnFeature) {
            featureClicked();
        }
        else if (v == binding.etFromDate) {
            dateClicked(v);
        }
        else if (v == binding.etToDate) {
            if (binding.etFromDate.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_from_date), FancyToast.ERROR);
                return;
            }
            dateClicked(v);
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
    }

    private void featureClicked() {
        if (cityIds.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_city), FancyToast.ERROR);
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
        if (Float.parseFloat(totalPrice) > 0) {
            addFeatureAPI(true, binding.etFromDate.getText().toString(), binding.etToDate.getText().toString());
        }
    }

    private void dateClicked(View vu) {
        if (oleAdvertiseClub != null && !oleAdvertiseClub.getStatus().equalsIgnoreCase("Expired")) {
            return;
        }
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                if (vu.getId() == R.id.et_from_date) {
                    binding.etFromDate.setText(formatter.format(calendar.getTime()));
                }
                else {
                    binding.etToDate.setText(formatter.format(calendar.getTime()));
                }
                if (!binding.etFromDate.getText().toString().isEmpty() && !binding.etToDate.getText().toString().isEmpty()) {
                    try {
                        long diff = formatter.parse(binding.etToDate.getText().toString()).getTime() - formatter.parse(binding.etFromDate.getText().toString()).getTime();
                        long diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
                        if (diffInDays > 0) {
                            binding.tvDays.setText(getString(R.string.You_have_selected_place_days, diffInDays));
                            float price = Float.parseFloat(featuredPrice) * diffInDays;
                            totalPrice = String.format(Locale.ENGLISH, "%.2f", price);
                            binding.tvPrice.setText(String.format("%s %s", totalPrice, Functions.getPrefValue(getContext(), Constants.kCurrency)));
                        }
                        else {
                            binding.tvDays.setText(getString(R.string.You_have_selected_place_days, 0));
                            totalPrice = "0";
                            binding.tvPrice.setText(String.format("%s %s", totalPrice, Functions.getPrefValue(getContext(), Constants.kCurrency)));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void cityClicked() {
        if (oleAdvertiseClub != null && !oleAdvertiseClub.getStatus().equalsIgnoreCase("Expired")) {
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
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), true);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                cityIds = "";
                binding.etCity.setText("");
                for (OleSelectionList selectedItem: selectedItems) {
                    if (binding.etCity.getText().toString().isEmpty()) {
                        cityIds = selectedItem.getId();
                        binding.etCity.setText(selectedItem.getValue());
                    }
                    else {
                        cityIds = String.format("%s,%s",  cityIds, selectedItem.getId());
                        binding.etCity.setText(String.format("%s, %s",  binding.etCity.getText().toString(), selectedItem.getValue()));
                    }
                }
            }
        });
        dialog.show();
    }

    private void addFeatureAPI(boolean isLoader, String fromDate, String toDate) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addFeaturedClub(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fromDate, toDate, totalPrice, cityIds, featuredId);
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