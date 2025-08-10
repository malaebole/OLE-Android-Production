package ae.oleapp.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleIncomeHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityIncomeHistoryBinding;
import ae.oleapp.fragments.BestPlayerDialogFragment;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.fragments.OleOleBookingCountFilterFragment;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeHistoryActivity extends BaseActivity implements View.OnClickListener {

    private ActivityIncomeHistoryBinding binding;
    private OleIncomeHistoryAdapter oleIncomeHistoryAdapter;

    private final List<IncomeHistory> incomeHistoryList = new ArrayList<>();
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomeHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Intent intent = getIntent();
        clubId  = intent.getStringExtra("club_id");
        if (!clubId.isEmpty()){
            getIncomeHistory(true, clubId,"","","","");
        }
        LinearLayoutManager oleIncomehistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.incomeHistoryRecyclerVu.setLayoutManager(oleIncomehistoryLayoutManager);
        oleIncomeHistoryAdapter = new OleIncomeHistoryAdapter(getContext(), incomeHistoryList);
        oleIncomeHistoryAdapter.setItemClickListener(itemClickListener);
        binding.incomeHistoryRecyclerVu.setAdapter(oleIncomeHistoryAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    OleIncomeHistoryAdapter.ItemClickListener itemClickListener = new OleIncomeHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
//            getincomeDetails(true, incomeHistoryList.get(pos).getId());
            String IncomeId = incomeHistoryList.get(pos).getId();
            String incomeFrom = incomeHistoryList.get(pos).getSource();
            String Amount = incomeHistoryList.get(pos).getAmount();
            String Note =incomeHistoryList.get(pos).getNotes();
            String ReceiptUrl = incomeHistoryList.get(pos).getReceipt();
            String BankName = incomeHistoryList.get(pos).getBankName();
            Boolean paymentmethod = true;
            showIncomeDetail(IncomeId, incomeFrom, Amount, Note, ReceiptUrl, paymentmethod, BankName);

        }
    };

    private void getIncomeHistory(Boolean isLoader, String clubId, String from, String to, String filterBy, String bankId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.incomeHistory(Functions.getAppLang(getContext()), clubId, filterBy, bankId, from, to);
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
                                        incomeHistoryList.clear();
                                     for (int i = 0; i < data.length(); i++) {
                                         incomeHistoryList.add(gson.fromJson(data.get(i).toString(), IncomeHistory.class));
                                     }
                                     binding.tvTotalAmount.setText(object.getString("total_amount"));
                                     binding.tvIncomeCur.setText(object.getString("currency"));
                                    oleIncomeHistoryAdapter.notifyDataSetChanged();
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

//    private void getincomeDetails(Boolean isLoader, String income_id) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
//        if (userId!=null){
//            Call<ResponseBody> call = AppManager.getInstance().apiInterface.incomeDetails(Functions.getAppLang(getContext()), income_id);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Functions.hideLoader(hud);
//                    if (response.body() != null) {
//                        try {
//                            JSONObject object = new JSONObject(response.body().string());
//                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                                JSONObject obj = object.getJSONObject(Constants.kData);
//                                Gson gson = new Gson();
//                                incomeDetailsModel = gson.fromJson(obj.toString(), IncomeDetailsModel.class);
//
//                            }
//                        }  catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Functions.hideLoader(hud);
//                    if (t instanceof UnknownHostException) {
//                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                    }
//                    else {
//                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//            });
//        }
//    }



    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        }
        else if (v == binding.filterBtn) {
            showFilter();
        }
    }

    protected void showIncomeDetail(String IncomeId, String incomeFrom, String Amount, String Note, String ReceiptUrl, Boolean paymentmethod, String BankName) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("IncomeHistoryBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        IncomeHistoryBottomSheetDialogFragment dialogFragment = new IncomeHistoryBottomSheetDialogFragment(IncomeId, incomeFrom, Amount, Note, ReceiptUrl,paymentmethod, BankName);
        dialogFragment.setDialogCallback((df, isEditClicked) -> {
            df.dismiss();
            if (isEditClicked) {
                Intent intent = new Intent(IncomeHistoryActivity.this, AddIncomeActivity.class);
                intent.putExtra("is_update",true);
//                intent.putExtra("result", new Gson().toJson(incomeDetailsModel));
                intent.putExtra("income_id", IncomeId);
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
        dialogFragment.setDialogCallback((df,  filterBy,  bankId,  from,  to) -> {
            df.dismiss();

            getIncomeHistory(true, clubId,from,to,filterBy,bankId);




        });
        dialogFragment.show(fragmentTransaction, "FilterFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}