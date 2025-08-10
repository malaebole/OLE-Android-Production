package ae.oleapp.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.EmployeeSalaryHistoryAdapter;
import ae.oleapp.adapters.PartnerHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityEmployeeSalaryHistoryBinding;
import ae.oleapp.dialogs.PartnerHistoryBottomSheetDialogFragment;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.SalaryHistoryBottomSheetDialogFragment;
import ae.oleapp.models.EmployeeData;
import ae.oleapp.models.EmployeeSalaryHistoryModel;
import ae.oleapp.models.ExpenseDetailsModel;
import ae.oleapp.models.PartnerData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeSalaryHistoryActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEmployeeSalaryHistoryBinding binding;
    private EmployeeSalaryHistoryAdapter employeeSalaryHistoryAdapter;

    private final List<EmployeeSalaryHistoryModel> employeeSalaryHistoryModelList = new ArrayList<>();
    private EmployeeData employeeData;

    private String clubId = "", employeeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeSalaryHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Intent intent = getIntent();
        clubId = intent.getStringExtra("club_id");
        employeeId = intent.getStringExtra("employee_id");
        if (!clubId.isEmpty()) {
            getEmployeeHistory(true, clubId, employeeId, "", "", "");

        }
        LinearLayoutManager partnerhistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.employeeSalaryHistoryRecyclerVu.setLayoutManager(partnerhistoryLayoutManager);
        employeeSalaryHistoryAdapter = new EmployeeSalaryHistoryAdapter(getContext(), employeeSalaryHistoryModelList);
        employeeSalaryHistoryAdapter.setItemClickListener(itemClickListener);
        binding.employeeSalaryHistoryRecyclerVu.setAdapter(employeeSalaryHistoryAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    EmployeeSalaryHistoryAdapter.ItemClickListener itemClickListener = new EmployeeSalaryHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            showSalaryDetail(employeeSalaryHistoryModelList.get(pos).getId());

        }
    };

    private void getEmployeeHistory(Boolean isLoader, String clubId, String employeeId, String filterBy , String from, String to) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.employeeSalaryHistory(Functions.getAppLang(getContext()), clubId, employeeId, filterBy, from, to);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                employeeSalaryHistoryModelList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    employeeSalaryHistoryModelList.add(gson.fromJson(data.get(i).toString(), EmployeeSalaryHistoryModel.class));
                                }
                                JSONObject obj = object.getJSONObject("employee");
                                Gson gson1 = new Gson();
                                employeeData = gson1.fromJson(obj.toString(), EmployeeData.class);
                                populateData();
                                binding.employeeSalaryHistoryRecyclerVu.setAdapter(employeeSalaryHistoryAdapter);
                            } else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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


    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        } else if (v == binding.filterBtn) {
            showFilter();
        }
    }

    private void populateData() {
        //binding.tvTotalAmount.setText(employeeData.getPaidAmount());
        binding.tvName.setText(employeeData.getName());
        binding.tvDesignation.setText(employeeData.getRoleName());
        binding.tvSalary.setText(employeeData.getSalary());
        binding.tvIncomeCur.setText(employeeData.getCurrency());
        if (!employeeData.getPhotoUrl().isEmpty()) {
            Glide.with(getContext()).load(employeeData.getPhotoUrl()).into(binding.imgVu);
        }
    }

    protected void showSalaryDetail(String employeeId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("SalaryHistoryBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        SalaryHistoryBottomSheetDialogFragment dialogFragment = new SalaryHistoryBottomSheetDialogFragment(employeeId, employeeSalaryHistoryModelList,employeeData);
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();

        });
        dialogFragment.show(fragmentTransaction, "SalaryHistoryBottomSheetDialogFragment");
    }


    protected void showFilter() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FilterFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        FilterFragment dialogFragment = new FilterFragment(clubId,true);
        dialogFragment.setDialogCallback((df, filterBy, bankId, from, to) -> {
            df.dismiss();

             getEmployeeHistory(true, clubId, employeeId, filterBy, from, to);


        });
        dialogFragment.show(fragmentTransaction, "FilterFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
