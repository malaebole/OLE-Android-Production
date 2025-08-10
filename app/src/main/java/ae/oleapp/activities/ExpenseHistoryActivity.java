package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleExpenseHistoryAdapter;
import ae.oleapp.adapters.OleIncomeHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityExpenseHistoryBinding;
import ae.oleapp.databinding.ActivityIncomeHistoryBinding;
import ae.oleapp.fragments.ExpenseHistoryBottomSheetDialogFragment;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.models.ExpenseHistory;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseHistoryActivity extends BaseActivity implements View.OnClickListener  {

    private ActivityExpenseHistoryBinding binding;

    private OleExpenseHistoryAdapter oleExpenseHistoryAdapter;
    private final List<ExpenseHistory> expenseHistoryList = new ArrayList<>();
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        Intent intent = getIntent();
        clubId  = intent.getStringExtra("club_id");
        if (!clubId.isEmpty()){
            getExpenseHistory(true,clubId,"","","","");
        }
        LinearLayoutManager oleExpenseHistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.expenseHistoryRecyclerVu.setLayoutManager(oleExpenseHistoryLayoutManager);
        oleExpenseHistoryAdapter = new OleExpenseHistoryAdapter(getContext(), expenseHistoryList);
        oleExpenseHistoryAdapter.setItemClickListener(itemClickListener);
        binding.expenseHistoryRecyclerVu.setAdapter(oleExpenseHistoryAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    OleExpenseHistoryAdapter.ItemClickListener itemClickListener = new OleExpenseHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            showExpenseDetail(expenseHistoryList.get(pos).getId(), expenseHistoryList.get(pos).getExpenseId());

        }
    };


    private void getExpenseHistory(Boolean isLoader, String clubId, String from, String to, String filterBy, String bankId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.expenseHistory(Functions.getAppLang(getContext()), clubId,filterBy,bankId,"",from,to);
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
                                expenseHistoryList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    expenseHistoryList.add(gson.fromJson(data.get(i).toString(), ExpenseHistory.class));
                                }
                                binding.tvTotalAmount.setText(object.getString("total_amount"));
                                binding.tvIncomeCur.setText(object.getString("currency"));
                                binding.expenseHistoryRecyclerVu.setAdapter(oleExpenseHistoryAdapter);
                            }
                            else {
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
        if (v == binding.backBtn){
            finish();
        }
        else if (v == binding.filterBtn) {
            showFilter();
        }
    }

    protected void showExpenseDetail(String recordId, String expenseId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ExpenseHistoryBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        ExpenseHistoryBottomSheetDialogFragment dialogFragment = new ExpenseHistoryBottomSheetDialogFragment(recordId,expenseId);
        dialogFragment.setDialogCallback((df, isEditClicked, expenseDetailsModel) -> {
            df.dismiss();
            if (isEditClicked) {
                Intent intent = new Intent(ExpenseHistoryActivity.this, AddExpenseActivity.class);
                intent.putExtra("is_update",true);
                intent.putExtra("result", new Gson().toJson(expenseDetailsModel));
                intent.putExtra("record_id", recordId);
                intent.putExtra("expense_id", expenseId);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
                //finish();
            }

        });
        dialogFragment.show(fragmentTransaction, "IncomeHistoryBottomSheetDialogFragment");
    }

    protected void showFilter() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FilterFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        FilterFragment dialogFragment = new FilterFragment(clubId);
        dialogFragment.setDialogCallback((df, filterBy, bankId, from, to) -> {
            df.dismiss();

            getExpenseHistory(true, clubId,from,to,filterBy,bankId);


        });
        dialogFragment.show(fragmentTransaction, "FilterFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}