package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.AddPartnerActivity;
import ae.oleapp.activities.EmployeeSalaryHistoryActivity;
import ae.oleapp.activities.OleFinanceActivity;
import ae.oleapp.adapters.OleEmployeeListAdapter;
import ae.oleapp.adapters.PartnerAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.FragmentOleEmployeeHomeBinding;
import ae.oleapp.databinding.FragmentOlePartnerHomeBinding;
import ae.oleapp.models.Employee;
import ae.oleapp.models.EmployeeData;
import ae.oleapp.models.PartnerData;
import ae.oleapp.owner.OleEmployeeActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmployeeHomeFragment extends BaseFragment implements View.OnClickListener {

    private FragmentOleEmployeeHomeBinding binding;
    private OleEmployeeListAdapter employeeAdapter;
    private final List<Employee> employees = new ArrayList<>();

    private String clubId = "";



    public OleEmployeeHomeFragment() {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOleEmployeeHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        OleFinanceActivity activity = (OleFinanceActivity)getActivity();
        Bundle results = activity.getClubId();
        if (results !=null){
            clubId = results.getString("club_id");
        }



//        LinearLayoutManager olePartnersLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        binding.partnerRecyclerVu.setLayoutManager(olePartnersLayoutManager);
//        partnerAdapter = new PartnerAdapter(getContext(), partnerDataList);
//        partnerAdapter.setItemClickListener(itemClickListener);
//        binding.partnerRecyclerVu.setAdapter(partnerAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.employeeRecyclerVu.setLayoutManager(layoutManager);
        employeeAdapter = new OleEmployeeListAdapter(getContext(), employees);
        employeeAdapter.setItemClickListener(itemClickListener);
        binding.employeeRecyclerVu.setAdapter(employeeAdapter);

        binding.backBtn.setOnClickListener(this);

        return view;
    }


    OleEmployeeListAdapter.ItemClickListener itemClickListener = new OleEmployeeListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
//            Intent intent = new Intent(getContext(), OleEmployeeActivity.class);
//            intent.putExtra("emp_id", employees.get(pos).getId());
//            startActivity(intent);
            String employeeId = employees.get(pos).getId();
            employeeClicked(employeeId);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getEmployeesAPI(employees.isEmpty());
    }

    private void getEmployeesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployees(Functions.getAppLang(getContext()), Functions.getPrefValue(getActivity(), Constants.kUserID), clubId, "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            employees.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                employees.add(gson.fromJson(arr.get(i).toString(), Employee.class));
                            }
                            employeeAdapter.notifyDataSetChanged();
                            if (employees.isEmpty()) {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.GONE);
                            }
                        }
                        else {
                            employees.clear();
                            employeeAdapter.notifyDataSetChanged();
                            binding.noDataVu.setVisibility(View.VISIBLE);
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

    private void employeeClicked(String employeeId){
        Intent intent = new Intent(getActivity(), EmployeeSalaryHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("employee_id", employeeId);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            getActivity().finish();
        }
    }


//    private void popuplatedata(){
//
//                binding.tvCurrency.setText(financeHomeData.getCurrency());
//                binding.tvTotalEarning.setText(financeHomeData.getAvailableEarnings() + ".00");
//                binding.incomeTv.setText(financeHomeData.getTotalIncomes());
//                binding.expenseTv.setText(financeHomeData.getTotalExpenses());
//                binding.upcomingExpenseTv.setText(financeHomeData.getUpcomingExpenses());
//
//        }


}