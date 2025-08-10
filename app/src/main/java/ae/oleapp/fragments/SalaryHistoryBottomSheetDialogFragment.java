package ae.oleapp.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentSalaryHistoryBottomSheetDialogBinding;
import ae.oleapp.models.EmployeeData;
import ae.oleapp.models.EmployeeSalaryHistoryModel;
import ae.oleapp.models.SalaryDetailModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalaryHistoryBottomSheetDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentSalaryHistoryBottomSheetDialogBinding binding;
    private String employeeId = "";
    private ResultDialogCallback dialogCallback;
    private SalaryDetailModel salaryDetailModel;
    private  EmployeeData employeeData;
    private List<EmployeeSalaryHistoryModel> employeeSalaryHistoryModelList = new ArrayList<>();



    public SalaryHistoryBottomSheetDialogFragment(String employeeId, List<EmployeeSalaryHistoryModel> employeeSalaryHistoryModelList, EmployeeData employeeData) {
        this.employeeId = employeeId;
        this.employeeSalaryHistoryModelList = employeeSalaryHistoryModelList;
        this.employeeData = employeeData;
    }


    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public SalaryHistoryBottomSheetDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSalaryHistoryBottomSheetDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getExpenseDetails(employeeId);
        populateData();

        binding.btnClose.setOnClickListener(this);

        return view;
    }

    private void populateData(){
        for (int i =0; i<employeeSalaryHistoryModelList.size(); i++){
            if (employeeId.equalsIgnoreCase(employeeSalaryHistoryModelList.get(i).getId())){
                binding.tvName.setText(employeeData.getName());
                binding.tvMonth.setText(employeeSalaryHistoryModelList.get(i).getMonth());
                binding.tvSalary.setText(employeeData.getSalary());
                binding.tvRole.setText(employeeData.getRoleName());
                binding.tvDate.setText(employeeSalaryHistoryModelList.get(i).getAddedDate());

                if (!employeeSalaryHistoryModelList.get(i).getReceipt().isEmpty()){
                    Glide.with(getActivity()).load(employeeSalaryHistoryModelList.get(i).getReceipt()).into(binding.invoiceImgVu);
                }
            }
        }

    }

    private void getExpenseDetails(String employeeId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.expenseDetails(Functions.getAppLang(getContext()), employeeId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                salaryDetailModel = gson.fromJson(obj.toString(), SalaryDetailModel.class);
                                binding.tvNote.setText(salaryDetailModel.getNotes());

                            }
                        }  catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
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


    @Override
    public void onClick(View v) {

        if (v == binding.btnClose) {
            dismiss();

        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }

}
