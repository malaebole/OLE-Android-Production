package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
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
import ae.oleapp.adapters.OleBankIncomeHistoryAdapter;
import ae.oleapp.adapters.OleIncomeHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityBankHistoryBinding;
import ae.oleapp.databinding.ActivityIncomeHistoryBinding;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.models.BankHistory;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankHistoryActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBankHistoryBinding binding;
    private OleBankIncomeHistoryAdapter oleBankIncomeHistoryAdapter;

    private final List<BankHistory> bankHistoryList = new ArrayList<>();
    private String clubId = "", bankID= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBankHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Intent intent = getIntent();
        clubId = intent.getStringExtra("club_id");
        bankID = intent.getStringExtra("bank_id");

        if (!clubId.isEmpty()) {
            getIncomeHistory(true, clubId, "", "", "", bankID);
        }

        LinearLayoutManager oleBankIncomehistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.incomeHistoryRecyclerVu.setLayoutManager(oleBankIncomehistoryLayoutManager);
        oleBankIncomeHistoryAdapter = new OleBankIncomeHistoryAdapter(getContext(), bankHistoryList);
        oleBankIncomeHistoryAdapter.setItemClickListener(itemClickListener);
        binding.incomeHistoryRecyclerVu.setAdapter(oleBankIncomeHistoryAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    OleBankIncomeHistoryAdapter.ItemClickListener itemClickListener = new OleBankIncomeHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            //getincomeDetails(true, bankHistoryList.get(pos).getId());
            String IncomeId = bankHistoryList.get(pos).getId();
            String incomeFrom = bankHistoryList.get(pos).getSource();
            String Amount = bankHistoryList.get(pos).getAmount();
            String Note =bankHistoryList.get(pos).getNotes();
            String ReceiptUrl = bankHistoryList.get(pos).getReceipt();
            Boolean paymentmethod = false;
            showIncomeDetail(IncomeId, incomeFrom, Amount, Note, ReceiptUrl,paymentmethod);


        }
    };

    private void getIncomeHistory(Boolean isLoader, String clubId, String from, String to, String filterBy, String bankId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.bankDepositReport(Functions.getAppLang(getContext()), clubId, filterBy, bankId, from, to);
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
                                bankHistoryList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    bankHistoryList.add(gson.fromJson(data.get(i).toString(), BankHistory.class));
                                }

                                binding.bankTitle.setText(object.getString("bank_name"));
                                binding.tvTotalEarning.setText(object.getString("total_amount"));
                                binding.expectedAmt.setText(object.getString("expected_amount")+" "+object.getString("currency"));
                                binding.remainingAmt.setText(object.getString("remaining_amount")+" "+object.getString("currency"));
                                binding.tvCurrency.setText(object.getString("currency"));

                                oleBankIncomeHistoryAdapter.notifyDataSetChanged();
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
//    private void getincomeDetails(Boolean isLoader, String income_id) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
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
        if (v == binding.backBtn) {
            finish();
        } else if (v == binding.filterBtn) {
            showFilter();
        }
    }

    protected void showIncomeDetail(String IncomeId, String incomeFrom, String Amount, String Note, String ReceiptUrl,Boolean paymentmethod) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("IncomeHistoryBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        IncomeHistoryBottomSheetDialogFragment dialogFragment = new IncomeHistoryBottomSheetDialogFragment(IncomeId, incomeFrom, Amount, Note, ReceiptUrl, paymentmethod,"");
        dialogFragment.setDialogCallback((df, isEditClicked) -> {
            df.dismiss();
            if (isEditClicked) {
                Intent intent = new Intent(getContext(), AddIncomeActivity.class);
                intent.putExtra("is_update", true);
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
        FilterFragment dialogFragment = new FilterFragment(clubId,true);
        dialogFragment.setDialogCallback((df, filterBy,  bankId,  from,  to) -> {
            df.dismiss();
                //if (!filterBy.equalsIgnoreCase("") || !from.equalsIgnoreCase("") || !to.equalsIgnoreCase("")){
                //    getIncomeHistory(true, clubId, from, to, filterBy, bankID,true);
               // }else{
                    getIncomeHistory(true, clubId, from, to, filterBy, bankID);
                //}

        });
        dialogFragment.show(fragmentTransaction, "FilterFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}