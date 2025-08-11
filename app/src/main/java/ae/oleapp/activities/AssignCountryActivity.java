package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.AssignCountryListAdapter;
import ae.oleapp.adapters.ClubsBanksAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAssignCountryBinding;
import ae.oleapp.databinding.OleactivityEmployeeBinding;
import ae.oleapp.models.Country;
import ae.oleapp.models.DocModel;
import ae.oleapp.models.DocumentModel;
import ae.oleapp.models.EmployeeDocModel;
import ae.oleapp.models.PartnerDocModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignCountryActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAssignCountryBinding binding;
    private final List<Country> countryList = new ArrayList<>();
    private AssignCountryListAdapter assignCountryListAdapter;
    private String empId = "", assignCountries="", clubId = "";
    private boolean isUpdate = false;
    private  String[] countryArray;
    private int[] intArray;
    private List<String> valList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignCountryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            assignCountries = bundle.getString("country_ids", "");
            empId = bundle.getString("emp_id", "");
            clubId = bundle.getString("club_id", "");
            isUpdate = bundle.getBoolean("update");
        }


        getAllCountries(true);


        LinearLayoutManager CountryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.banksRecyclerVu.setLayoutManager(CountryLayoutManager);
        assignCountryListAdapter = new AssignCountryListAdapter(getContext(), countryList, assignCountries);
        assignCountryListAdapter.setItemClickListener(itemClickListener);
        binding.banksRecyclerVu.setAdapter(assignCountryListAdapter);


        binding.btnClose.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.delete.setOnClickListener(this);

        if (isUpdate){
            valList = new ArrayList<>(Arrays.asList(assignCountries.split(",")));
            valList.removeAll(Collections.singleton(""));
            assignCountries = TextUtils.join(",", valList);
            assignCountryListAdapter.setAssignCountries(assignCountries);
            countryArray = assignCountries.split(",");
            if (!valList.isEmpty()){
                intArray = new int[countryArray.length];
                for (int i = 0; i < countryArray.length; i++) {
                    intArray[i] = Integer.parseInt(countryArray[i].trim());
                }
            }
            binding.delete.setVisibility(View.VISIBLE);
        }else{
            binding.delete.setVisibility(View.GONE);
        }


    }

    private void getAllCountries(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getAllCountries();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray country = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            countryList.clear();
                            for (int i = 0; i < country.length(); i++) {
                                countryList.add(gson.fromJson(country.get(i).toString(), Country.class));
                            }
                            assignCountryListAdapter.notifyDataSetChanged();
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

    AssignCountryListAdapter.ItemClickListener itemClickListener = new AssignCountryListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            String targetValue = countryList.get(pos).getId();
            valList = new ArrayList<>(Arrays.asList(assignCountries.split(",")));
            valList.removeAll(Collections.singleton(""));
            if (valList.contains(targetValue)) {
                valList.remove(targetValue);
            } else {
                valList.add(targetValue);
            }
            assignCountries = TextUtils.join(",", valList);
            assignCountryListAdapter.setAssignCountries(assignCountries);
            countryArray = assignCountries.split(",");
            if (!valList.isEmpty()){
                intArray = new int[countryArray.length];
                for (int i = 0; i < countryArray.length; i++) {
                    intArray[i] = Integer.parseInt(countryArray[i].trim());
                }
            }


        }
    };



    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            finish();
        } else if (v == binding.delete) {
            deleteClicked();
        } else if (v == binding.btnSubmit) {
            btnSubmitClicked();
        }

    }
    private void btnSubmitClicked() {
        if (valList == null || valList.isEmpty()){
            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
            return;
        }

        UpdatePlayerAsEmployee(true);

    }

    private void deleteClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            removePlayerAsEmployee(true);
                        }
                    }
                }).show();

    }


    private void UpdatePlayerAsEmployee(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.addUpdatePlayerAsEmployee(empId, clubId, countryArray);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isUpdate){
                                Functions.showToast(getContext(), getString(R.string.global_player_updated), FancyToast.SUCCESS);
                            }else{
                                Functions.showToast(getContext(), getString(R.string.global_player_added), FancyToast.SUCCESS);
                            }
                            finish();
                        }
                    }
                    catch (Exception e) {
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

    private void removePlayerAsEmployee(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.removePlayerAsEmployee(empId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.global_player_removed), FancyToast.SUCCESS);
                            finish();
                        }
                    }
                    catch (Exception e) {
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





}