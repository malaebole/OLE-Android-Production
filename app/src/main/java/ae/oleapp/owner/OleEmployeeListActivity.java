package ae.oleapp.owner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.AssignCountryActivity;
import ae.oleapp.adapters.OleEmployeeListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmployeeListBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Employee;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmployeeListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmployeeListBinding binding;
    private OleEmployeeListAdapter adapter;
    private final List<Employee> employees = new ArrayList<>();
    private OleRankClubAdapter oleRankClubAdapter;
    private String clubId = "", sorting = "";
    private final List<Club> clubList = new ArrayList<>();
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmployeeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.employee_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleEmployeeListAdapter(getContext(), employees);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.noDataVu.setVisibility(View.GONE);
        binding.btnAdd.setVisibility(View.GONE);

//        Club club = new Club();
//        club.setId("");
//        club.setName(getString(R.string.all));
//        clubList.add(club);
//        clubList.addAll(AppManager.getInstance().clubs);
//        AppManager.getInstance().clubs.add(club);
        if (clubId.isEmpty()){
            clubId = "2";
        }

        for (int i =0; i<AppManager.getInstance().clubs.size(); i++){
            if (clubId.equalsIgnoreCase(AppManager.getInstance().clubs.get(i).getId())){
                index = i;
            }
        }
        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, index, false); // clublist
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.btnAddEmp.setOnClickListener(this);
        binding.btnSort.setOnClickListener(this);
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            //clubId = clubList.get(pos).getId();
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getEmployeesAPI(true);

        }
    };

    OleEmployeeListAdapter.ItemClickListener itemClickListener = new OleEmployeeListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            if (employees.get(pos).getEmployeeType().equalsIgnoreCase("lineup")){
                Intent intent = new Intent(getContext(), AssignCountryActivity.class);
                intent.putExtra("country_ids", employees.get(pos).getAssignedCountries());
                intent.putExtra("emp_id", employees.get(pos).getId());
                intent.putExtra("club_id", clubId);
                intent.putExtra("update", true);
                startActivity(intent);

            }else{
                Intent intent = new Intent(getContext(), OleEmployeeActivity.class);
                intent.putExtra("emp_id", employees.get(pos).getId());
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getEmployeesAPI(employees.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnAdd || v == binding.btnAddEmp) {
            addClicked();
        }
        else if (v == binding.btnSort) {
            sortClicked();
        }
    }

    private void sortClicked() {
        if (employees.isEmpty()) {
            return;
        }
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.rating_high_to_low), getResources().getString(R.string.rating_low_to_high))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            sorting = "high_to_low";
                        }
                        else if (index == 1) {
                            sorting = "low_to_high";
                        }
                        getEmployeesAPI(true);
                    }
                }).show();
    }

    private void addClicked() {
        Intent intent = new Intent(getContext(), OleAddEmployeeActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("status", "new");
        startActivity(intent);
    }

    private void getEmployeesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployees(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, sorting);
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
                            adapter.notifyDataSetChanged();
                            if (employees.isEmpty()) {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                                binding.btnAdd.setVisibility(View.GONE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.GONE);
                                binding.btnAdd.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            employees.clear();
                            adapter.notifyDataSetChanged();
                            binding.noDataVu.setVisibility(View.VISIBLE);
                            binding.btnAdd.setVisibility(View.GONE);
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