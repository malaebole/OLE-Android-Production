package ae.oleapp.owner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.AddCashDepositFileActivity;
import ae.oleapp.activities.OleFullImageActivity;
import ae.oleapp.adapters.OleExpenseListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCashDepositBinding;
import ae.oleapp.dialogs.OleAddExpenseDialogFragment;
import ae.oleapp.dialogs.OleDepositReceiptDialogFragment;
import ae.oleapp.models.OleCashDeposit;
import ae.oleapp.models.OleClubExpense;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCashDepositActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCashDepositBinding binding;
    private String clubId = "", fromDate = "";
    private OleCashDeposit deposit;
    private final List<OleClubExpense> expenseList = new ArrayList<>();
    private final List<OleClubExpense> addedExpenseList = new ArrayList<>();
    private OleExpenseListAdapter adapter;
    private OleDepositReceiptDialogFragment dialogFragment;
    private OleAddExpenseDialogFragment expenseDialogFragment;
    private Boolean selection;
    private double grandTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCashDepositBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.cash_deposit);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        fromDate = dateFormat.format(new Date());
        binding.tvDate.setText(fromDate);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleExpenseListAdapter(getContext(), addedExpenseList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        getExpensesAPI(false);

        binding.btnAdd.setVisibility(View.GONE);
        binding.depositVu.setVisibility(View.GONE);
        getEarningAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnCalendar.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.btnView.setOnClickListener(this);
        binding.btnAddExpense.setOnClickListener(this);
        binding.btnUploadDoc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnCalendar) {
            calendarClicked();
        }
        else if (v == binding.btnAddExpense) {
            if (expenseList.isEmpty()) {
                return;
            }
            selection = false;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("AddExpenseDialogFragment");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.addToBackStack(null);
            expenseDialogFragment = new OleAddExpenseDialogFragment(clubId, expenseList, fromDate);
            expenseDialogFragment.setFragmentCallback(new OleAddExpenseDialogFragment.AddExpenseDialogFragmentCallback() {
                @Override
                public void didAddExpense(DialogFragment df, OleClubExpense expense) {
                    df.dismiss();
                    addedExpenseList.add(expense);
                    adapter.notifyDataSetChanged();
                    calculateTotal();
                }
            });
            expenseDialogFragment.show(fragmentTransaction, "AddExpenseDialogFragment");
        }
        else if (v == binding.btnAdd) {
            if (deposit == null) {
                return;
            }
            selection = true;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("DepositReceiptDialogFragment");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.addToBackStack(null);
            dialogFragment = new OleDepositReceiptDialogFragment(clubId, deposit.getCashInHand(), deposit.getInventoryCash(), fromDate,grandTotal);
            dialogFragment.setFragmentCallback(new OleDepositReceiptDialogFragment.DepositReceiptDialogFragmentCallback() {
                @Override
                public void didUploadReceipt(DialogFragment df) {
                    df.dismiss();
                    getEarningAPI(false);
                }
            });
            dialogFragment.show(fragmentTransaction, "DepositReceiptDialogFragment");
        }
        else if (v == binding.btnView) {
            if (deposit != null && !deposit.getReceiptPhoto().isEmpty()) {
                Intent intent = new Intent(getContext(), OleFullImageActivity.class);
                intent.putExtra("URL", deposit.getReceiptPhoto());
                startActivity(intent);
            }
        }
        else if (v == binding.btnUploadDoc) {
            uploadDocClicked();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (selection){
            if (dialogFragment.isVisible()) {
                dialogFragment.onActivityResult(requestCode, resultCode, data);
            }
        }else{
            if (expenseDialogFragment.isVisible()){
                expenseDialogFragment.onActivityResult(requestCode, resultCode, data);

            }
        }


    }

    private void uploadDocClicked() {
        Intent intent = new Intent(OleCashDepositActivity.this, AddCashDepositFileActivity.class);
        intent.putExtra("club",clubId);
        intent.putExtra("date",fromDate);
        intent.putExtra("reportFileAdded",deposit.getReportFileAdded());
        intent.putExtra("update",deposit.getCanUpdate());
        startActivity(intent);

    }


    OleExpenseListAdapter.OnItemClickListener itemClickListener = new OleExpenseListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (deposit.getReceiptPhoto().isEmpty()){
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
                                deleteExpenseAPI(true, addedExpenseList.get(pos).getId(), pos);
                            }
                        }
                    }).show();
            }

        }

        @Override
        public void OnAttachmentClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OleFullImageActivity.class);
            intent.putExtra("URL", addedExpenseList.get(pos).getReceiptPhoto());
            startActivity(intent);
        }
    };

    private void calendarClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                fromDate = formatter.format(calendar.getTime());
                binding.tvDate.setText(fromDate);
                getEarningAPI(true);
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void populateData() {
        binding.tvEarningCash.setText(String.format("%s %s", deposit.getCashInHand(), deposit.getCurrency()));
        binding.tvEarningOnline.setText(String.format("%s %s", deposit.getCardAmount(), deposit.getCurrency()));
        binding.tvEarningPos.setText(String.format("%s %s", deposit.getPosAmount(), deposit.getCurrency()));
        binding.tvMatchFee.setText(String.format("%s %s", deposit.getMatchFees(), deposit.getCurrency()));
        binding.tvFacility.setText(String.format("%s %s", deposit.getFacilities(), deposit.getCurrency()));
        binding.tvEarningDiscount.setText(String.format("%s %s", deposit.getDiscounts(), deposit.getCurrency()));
        binding.tvInventoryCash.setText(String.format("%s %s", deposit.getInventoryCash(), deposit.getCurrency()));
        binding.tvInventoryPos.setText(String.format("%s %s", deposit.getInventoryPos(), deposit.getCurrency()));
        binding.tvInventoryDiscount.setText(String.format("%s %s", deposit.getInventoryDiscount(), deposit.getCurrency()));

        addedExpenseList.clear();
        addedExpenseList.addAll(deposit.getExpenseList());
        adapter.notifyDataSetChanged();

        calculateTotal();

        if (deposit.getReceiptPhoto().isEmpty()) {
            binding.btnAdd.setVisibility(View.VISIBLE);
            binding.depositVu.setVisibility(View.GONE);
        }
        else {
            binding.btnAdd.setVisibility(View.GONE);
            binding.depositVu.setVisibility(View.VISIBLE);
            binding.tvBy.setText(String.format("%s: %s", getString(R.string.deposit_by), deposit.getAddedBy()));
            binding.tvDepositDate.setText(String.format("%s: %s", getString(R.string.deposit_date), deposit.getAddedDate()));
            binding.tvDepositAmount.setText(String.format("%s: %s %s", getString(R.string.deposit_amount), deposit.getDepositedAmount(), deposit.getCurrency()));
            binding.tvNote.setText(deposit.getDepositNote());
        }
        if (deposit.getReportFileAdded()){
            binding.tvDoc.setText(getString(R.string.view_report));
        }else{
            binding.tvDoc.setText(getString(R.string.upload_report));
        }
    }

    private void calculateTotal() {
        double total = Double.parseDouble(deposit.getCashInHand()) + Double.parseDouble(deposit.getInventoryCash());
        if (addedExpenseList.isEmpty()) {
            binding.expenseVu.setVisibility(View.GONE);
        }
        else {
            binding.expenseVu.setVisibility(View.VISIBLE);
            for (OleClubExpense expense : addedExpenseList) {
                double price = Double.parseDouble(expense.getAmount());
                total -= price;
            }
        }
        grandTotal = total + Double.parseDouble(deposit.getRemainings());
        double remainingAmt = Double.parseDouble(deposit.getRemainings());
        double yesterDayAmt = Double.parseDouble(deposit.getYesterdayRemainings());
        double totalExpense = Double.parseDouble(deposit.getTotalExpense());
        double afterDepositTotal = grandTotal-remainingAmt;
        double depositedAmt = Double.parseDouble(deposit.getDepositedAmount());

        binding.tvTodayEarning.setText(String.format(Locale.ENGLISH, "%s: %.2f %s",getString(R.string.today_earning), total, deposit.getCurrency()));
        binding.tvRemainingAmount.setText(String.format(Locale.ENGLISH, "%s: %.2f %s", getString(R.string.remaining_amount), remainingAmt, deposit.getCurrency()));
        binding.tvYesterdayRemAmt.setText(String.format(Locale.ENGLISH, "%s: %.2f %s",getString(R.string.yesterday_remamt), yesterDayAmt, deposit.getCurrency()));
        binding.tvTotalExpense.setText(String.format(Locale.ENGLISH, "%s: %.2f %s",getString(R.string.total_expenses), totalExpense, deposit.getCurrency()));

        if (!deposit.getDepositedAmount().equalsIgnoreCase("0") && !deposit.getReceiptPhoto().isEmpty()){
            binding.tvRemainingAmount.setVisibility(View.VISIBLE);
            binding.tvTotalExpense.setVisibility(View.VISIBLE);
            binding.tvMsg.setText(String.format(Locale.ENGLISH, "%s: %.2f %s", getString(R.string.deposit_amount), depositedAmt, deposit.getCurrency()));
        }else{
            binding.tvRemainingAmount.setVisibility(View.GONE);
            binding.tvTotalExpense.setVisibility(View.GONE);
            binding.tvMsg.setText(String.format(Locale.ENGLISH, "%s: %.2f %s", getString(R.string.amount_deposit), grandTotal, deposit.getCurrency()));
        }

    }

    private void getEarningAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEarningInventory(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fromDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            deposit = new Gson().fromJson(obj.toString(), OleCashDeposit.class);
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

    private void getExpensesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getExpenses(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            expenseList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                expenseList.add(gson.fromJson(arr.get(i).toString(), OleClubExpense.class));
                            }
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

    private void deleteExpenseAPI(boolean isLoader, String expId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteExpense(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), expId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            addedExpenseList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, addedExpenseList.size());
                            calculateTotal();
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