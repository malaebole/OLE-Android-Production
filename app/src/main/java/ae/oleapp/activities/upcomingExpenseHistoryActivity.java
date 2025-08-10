package ae.oleapp.activities;

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
import ae.oleapp.adapters.OleUpcomingExpenseHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityUpcomingExpenseHistoryBinding;
import ae.oleapp.fragments.ExpenseHistoryBottomSheetDialogFragment;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.UpcomingExpenseHistoryBottomSheetDialogFragment;
import ae.oleapp.models.UpcomingExpense;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class upcomingExpenseHistoryActivity extends BaseActivity implements View.OnClickListener  {

    private ActivityUpcomingExpenseHistoryBinding binding;

    private OleUpcomingExpenseHistoryAdapter oleUpcomingExpenseHistoryAdapter;
    private final List<UpcomingExpense> upcomingExpenseList = new ArrayList<>();
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpcomingExpenseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        Intent intent = getIntent();
        clubId  = intent.getStringExtra("club_id");
        if (!clubId.isEmpty()){
            getUpcomingExpenseHistory(true,clubId,"","","","");
        }
        LinearLayoutManager oleExpenseHistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.upcomingExpenseHistoryRecyclerVu.setLayoutManager(oleExpenseHistoryLayoutManager);
        oleUpcomingExpenseHistoryAdapter = new OleUpcomingExpenseHistoryAdapter(getContext(), upcomingExpenseList);
        oleUpcomingExpenseHistoryAdapter.setItemClickListener(itemClickListener);
        binding.upcomingExpenseHistoryRecyclerVu.setAdapter(oleUpcomingExpenseHistoryAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    OleUpcomingExpenseHistoryAdapter.ItemClickListener itemClickListener = new OleUpcomingExpenseHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            showUpcomingExpenseDetail(upcomingExpenseList.get(pos).getId(), clubId, upcomingExpenseList.get(pos).getExpenseId());

        }
    };


    private void getUpcomingExpenseHistory(Boolean isLoader, String clubId, String from, String to, String filterBy, String bankId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.upcomingExpenseHistory(Functions.getAppLang(getContext()), clubId, bankId,filterBy,"", from, to,"");
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
                                upcomingExpenseList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    upcomingExpenseList.add(gson.fromJson(data.get(i).toString(), UpcomingExpense.class));
                                }
                                binding.tvTotalAmount.setText(object.getString("total_amount"));
                                binding.tvIncomeCur.setText(object.getString("currency"));
                                oleUpcomingExpenseHistoryAdapter.notifyDataSetChanged();
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

    protected void showUpcomingExpenseDetail(String upcomingExpenseId, String clubId, String expenseId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("UpcomingExpenseHistoryBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        UpcomingExpenseHistoryBottomSheetDialogFragment dialogFragment = new UpcomingExpenseHistoryBottomSheetDialogFragment(upcomingExpenseId, clubId, expenseId);
        dialogFragment.setDialogCallback((df, isEditClicked, upcomingExpenseDetailsModel) -> {
            df.dismiss();

            if (isEditClicked) {
                Intent intent = new Intent(upcomingExpenseHistoryActivity.this, AddUpcomingExpenseActivity.class);
                intent.putExtra("is_update",true);
                intent.putExtra("result", new Gson().toJson(upcomingExpenseDetailsModel));
                intent.putExtra("club_id", clubId);
                intent.putExtra("expense_id", expenseId);
                intent.putExtra("record_id", upcomingExpenseId);
                startActivity(intent);
                //finish();

            } else {
                Intent intent = new Intent(upcomingExpenseHistoryActivity.this, MarkPaidActivity.class);
                intent.putExtra("result", new Gson().toJson(upcomingExpenseDetailsModel));
                intent.putExtra("club_id", clubId);
                intent.putExtra("upcoming_expense_id", upcomingExpenseId);
                startActivity(intent);

            }

        });
        dialogFragment.show(fragmentTransaction, "UpcomingExpenseHistoryBottomSheetDialogFragment");
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

            getUpcomingExpenseHistory(true, clubId,from,to,filterBy,bankId);


        });
        dialogFragment.show(fragmentTransaction, "FilterFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}