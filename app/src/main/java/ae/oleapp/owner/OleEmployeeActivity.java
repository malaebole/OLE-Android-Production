package ae.oleapp.owner;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleTipPaymentAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmployeeBinding;
import ae.oleapp.dialogs.OlePayTipDialogFragment;
import ae.oleapp.models.Employee;
import ae.oleapp.models.OleTipPayment;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmployeeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmployeeBinding binding;
    private Employee employee;
    private String empId = "", clubId;
    private OleTipPaymentAdapter adapter;
    private final List<OleTipPayment> paymentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empId = bundle.getString("emp_id", "");
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleTipPaymentAdapter(getContext(), paymentList);
        binding.recyclerVu.setAdapter(adapter);

        binding.backBtn.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.ratingVu.setOnClickListener(this);
        binding.tvSeeAll.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEmployeeAPI(employee == null);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnDel) {
            if (employee != null) {
                delClicked();
            }
        }
        else if (v == binding.ratingVu) {
            if (employee != null) {
                Intent intent = new Intent(getContext(), OleEmployeeReviewsActivity.class);
                intent.putExtra("emp_id", employee.getId());
                startActivity(intent);
            }
        }
        else if (v == binding.tvSeeAll) {
            if (employee != null) {
                Intent intent = new Intent(getContext(), OleTipPaymentActivity.class);
                intent.putExtra("emp_id", employee.getId());
                startActivity(intent);
            }
        }
        else if (v == binding.btnEdit) {
            if (employee != null) {
                Intent intent = new Intent(getContext(), OleAddEmployeeActivity.class);
                Gson gson = new Gson();
                intent.putExtra("data", gson.toJson(employee));
                intent.putExtra("status", "old");
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }
        }
        else if (v == binding.btnPay) {
            if (employee != null) {
                if (Double.parseDouble(employee.getPayable()) > 0) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("PayTipDialogFragment");
                    if (fragment != null) {
                        fragmentTransaction.remove(fragment);
                    }
                    fragmentTransaction.addToBackStack(null);
                    OlePayTipDialogFragment dialogFragment = new OlePayTipDialogFragment(employee.getPayable());
                    dialogFragment.setFragmentCallback(new OlePayTipDialogFragment.PayTipDialogFragmentCallback() {
                        @Override
                        public void submitClicked(DialogFragment df, String amount) {
                            df.dismiss();
                            payTipPaymentsAPI(true, amount);
                        }
                    });
                    dialogFragment.show(fragmentTransaction, "PayTipDialogFragment");
                }
            }
        }
    }

    private void populateData() {
        Glide.with(getApplicationContext()).load(employee.getEmployeePhoto()).placeholder(R.drawable.emp_ic).into(binding.imgVu);
        binding.tvClubName.setText(employee.getClubName());
        binding.tvName.setText(employee.getName());
        binding.tvPhone.setText(employee.getPhone());
        binding.tvEmpId.setText(employee.getEmployeeId());
        binding.tvEmail.setText(employee.getEmail());
        binding.tvRole.setText(employee.getRoleName());
        binding.tvRate.setText(String.format("%s (%s)", employee.getRating(), employee.getReviewsCount()));
        binding.ratingBar.setRating(Float.parseFloat(employee.getRating()));


        binding.tvPayableTip.setText(String.format("%s %s", employee.getPayable(), employee.getCurrency()));
        binding.tvTodayTip.setText(String.format("%s %s", employee.getTodayTip(), employee.getCurrency()));
        binding.tvWeekTip.setText(String.format("%s %s", employee.getLastWeekTip(), employee.getCurrency()));
        binding.tvLifetimeTip.setText(String.format("%s %s", employee.getLifetimeTip(), employee.getCurrency()));
        binding.tvMonthTip.setText(String.format("%s %s", employee.getLastMonthTip(), employee.getCurrency()));
        binding.tvMonth.setText(employee.getLastMonthTitle());

        paymentList.clear();
        paymentList.addAll(employee.getPaidTips());
        if (paymentList.isEmpty()) {
            binding.paymentVu.setVisibility(View.GONE);
        }
        else {
            binding.paymentVu.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void delClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.employee))
                .setMessage(getResources().getString(R.string.do_you_want_to_delete_employee))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEmployeeAPI(true, employee.getId());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void getEmployeeAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployee(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), empId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            employee = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), Employee.class);
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

    private void deleteEmployeeAPI(boolean isLoader, String empId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteEmployee(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), empId);
        call.enqueue(new Callback<>() {
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

    private void payTipPaymentsAPI(boolean isLoader, String amount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.employeeTipPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), empId, amount);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            getEmployeeAPI(false);
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