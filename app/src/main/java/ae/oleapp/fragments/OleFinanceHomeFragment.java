package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import ae.oleapp.activities.BankHistoryActivity;
import ae.oleapp.activities.EmployeeSalaryHistoryActivity;
import ae.oleapp.activities.ExpenseHistoryActivity;
import ae.oleapp.activities.IncomeHistoryActivity;
import ae.oleapp.activities.OleFinanceActivity;
import ae.oleapp.activities.PartnerHistoryActivity;
import ae.oleapp.activities.upcomingExpenseHistoryActivity;
import ae.oleapp.adapters.EmployeeSalaryAdapter;
import ae.oleapp.adapters.OleBanksEarningAdapter;
import ae.oleapp.adapters.OleShareHolderEarningAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.FragmentOleFinanceHomeBinding;
import ae.oleapp.models.BankEarning;
import ae.oleapp.models.EmployeeSalary;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.PartnerEarning;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleFinanceHomeFragment extends BaseFragment implements View.OnClickListener {

    private FragmentOleFinanceHomeBinding binding;
    private OleBanksEarningAdapter oleBanksEarningAdapter;
    private OleShareHolderEarningAdapter oleShareHolderEarningAdapter;
    private EmployeeSalaryAdapter employeeSalaryAdapter;
    private FinanceHome financeHomeData;

    private String clubId = "";

    private final List<BankEarning> bankEarnings = new ArrayList<>();
    private final List<PartnerEarning> partnerEarnings = new ArrayList<>();
    private final List<EmployeeSalary> employeeSalaries = new ArrayList<>();


    public OleFinanceHomeFragment() {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOleFinanceHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        OleFinanceActivity activity = (OleFinanceActivity)getActivity();
        Bundle results = activity.getClubId();
        if (results !=null){
            clubId = results.getString("club_id");

        }
        getFinanceHomeApi(true,clubId);


        LinearLayoutManager oleBanksEarningLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.banksRecyclerVu.setLayoutManager(oleBanksEarningLayoutManager);
        oleBanksEarningAdapter = new OleBanksEarningAdapter(getContext(), bankEarnings);
        oleBanksEarningAdapter.setItemClickListener(itemClickListener);
        binding.banksRecyclerVu.setAdapter(oleBanksEarningAdapter);

        LinearLayoutManager oleShareHolderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shareholderRecyclerVu.setLayoutManager(oleShareHolderLayoutManager);
        oleShareHolderEarningAdapter = new OleShareHolderEarningAdapter(getContext(), partnerEarnings);
        oleShareHolderEarningAdapter.setItemClickListener(ShareHolderItemClickListener);
        binding.shareholderRecyclerVu.setAdapter(oleShareHolderEarningAdapter);

        LinearLayoutManager oleEmployeeEarningLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.employeeRecyclerVu.setLayoutManager(oleEmployeeEarningLayoutManager);
        employeeSalaryAdapter = new EmployeeSalaryAdapter(getContext(), employeeSalaries,false);
        employeeSalaryAdapter.setItemClickListener(EmployeeSalaryItemClickListener);
        binding.employeeRecyclerVu.setAdapter(employeeSalaryAdapter);


        binding.backBtn.setOnClickListener(this);
        binding.relIncome.setOnClickListener(this);
        binding.relExpense.setOnClickListener(this);
        binding.relUpcomingExpense.setOnClickListener(this);



        return view;
    }


    OleBanksEarningAdapter.ItemClickListener itemClickListener = new OleBanksEarningAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
                String bankId = financeHomeData.getBankEarnings().get(pos).getId();
                bankClicked(bankId);

        }
    };
    OleShareHolderEarningAdapter.ItemClickListener ShareHolderItemClickListener = new OleShareHolderEarningAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            String partnerId = financeHomeData.getPartnerEarnings().get(pos).getId();
            partnerClicked(partnerId);
        }
    };
    EmployeeSalaryAdapter.ItemClickListener EmployeeSalaryItemClickListener = new EmployeeSalaryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            String employeeId = financeHomeData.getEmployeeSalaries().get(pos).getId();
            employeeClicked(employeeId);

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
            getFinanceHomeApi(false,clubId);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            getActivity().finish();
        }
        else if (v == binding.relIncome) {
            incomeClicked();
        }
        else if (v == binding.relExpense) {
            expenseClicked();
        }
        else if (v == binding.relUpcomingExpense) {
            upcomingExpenseClicked();
        }
    }
    private void getFinanceHomeApi(Boolean isLoader,  String club_id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.financeHome(Functions.getAppLang(getContext()), club_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                    JSONObject obj = object.getJSONObject(Constants.kData);
                                    Gson gson = new Gson();
                                    financeHomeData = gson.fromJson(obj.toString(), FinanceHome.class);
                                    popuplatedata();
                                    JSONArray bank_earnings = obj.getJSONArray("bank_earnings");
                                    JSONArray partner_earnings = obj.getJSONArray("partner_earnings");
                                    JSONArray employee_salaries = obj.getJSONArray("employee_salaries");
                                    bankEarnings.clear();
                                    partnerEarnings.clear();
                                    employeeSalaries.clear();
                                    Gson gson1 = new Gson();
                                for (int i = 0; i < bank_earnings.length(); i++) {
                                    bankEarnings.add(gson1.fromJson(bank_earnings.get(i).toString(), BankEarning.class));
                                }
                                for (int i = 0; i < partner_earnings.length(); i++) {
                                    partnerEarnings.add(gson1.fromJson(partner_earnings.get(i).toString(), PartnerEarning.class));
                                }
                                for (int i = 0; i < employee_salaries.length(); i++) {
                                    employeeSalaries.add(gson1.fromJson(employee_salaries.get(i).toString(), EmployeeSalary.class));
                                }

                                oleBanksEarningAdapter.notifyDataSetChanged();
                                oleShareHolderEarningAdapter.notifyDataSetChanged();
                                employeeSalaryAdapter.notifyDataSetChanged();
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
                    }
                    else {
                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
            });
        }
    }

    private void popuplatedata(){

            binding.tvCurrency.setText(financeHomeData.getCurrency());
            binding.tvTotalEarning.setText(financeHomeData.getAvailableEarnings());
            binding.incomeTv.setText(financeHomeData.getTotalIncomes());
            binding.expenseTv.setText(financeHomeData.getTotalExpenses());
            binding.upcomingExpenseTv.setText(financeHomeData.getUpcomingExpenses());

    }

    private void incomeClicked(){
        Intent intent = new Intent(getActivity(), IncomeHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        startActivity(intent);
    }

    private void expenseClicked(){
        Intent intent = new Intent(getActivity(), ExpenseHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        startActivity(intent);
    }

    private void upcomingExpenseClicked(){
        Intent intent = new Intent(getActivity(), upcomingExpenseHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        startActivity(intent);
    }

    private void bankClicked(String bankId){
        Intent intent = new Intent(getActivity(), BankHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("bank_id", bankId);
        startActivity(intent);
    }

    private void partnerClicked(String partnerId){
        Intent intent = new Intent(getActivity(), PartnerHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("partner_id", partnerId);
        startActivity(intent);
    }

    private void employeeClicked(String employeeId){
        Intent intent = new Intent(getActivity(), EmployeeSalaryHistoryActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("employee_id", employeeId);
        startActivity(intent);
    }



}