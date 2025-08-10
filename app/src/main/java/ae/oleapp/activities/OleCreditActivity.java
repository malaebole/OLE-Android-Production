package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import ae.oleapp.adapters.OleWalletSection;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityOleCreditBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OleTopupDialogFragment;
import ae.oleapp.dialogs.OleWalletDetailDialogFragment;
import ae.oleapp.models.OleTransactionHistory;
import ae.oleapp.models.OleWalletTransaction;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreditActivity extends BaseActivity implements OleWalletSection.ClickListener, View.OnClickListener {

    private OleactivityOleCreditBinding binding;
    private SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;
    private final List<OleWalletTransaction> oleWalletTransactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityOleCreditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.ole_credit);

        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWalletAPI(false);
            }
        });

        getWalletAPI(true);
        binding.tvCurrency.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnTopup.setOnClickListener(this);

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.imgVu.setImageResource(R.drawable.sidemenu_padel);
        }
        else {
            binding.imgVu.setImageResource(R.drawable.wallet_ball);
        }
    }

    private void populateData() {
        sectionedRecyclerViewAdapter.removeAllSections();
        for (OleWalletTransaction transaction: oleWalletTransactions) {
            if (transaction.getHistory().size() > 0) {
                sectionedRecyclerViewAdapter.addSection(new OleWalletSection(transaction.getDate(), transaction.getHistory(), this));
            }
        }
        binding.recyclerVu.setAdapter(sectionedRecyclerViewAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnTopup) {
            topupClicked();
        }
    }

    private void topupClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("TopupDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleTopupDialogFragment dialogFragment = new OleTopupDialogFragment();
        dialogFragment.setFragmentCallback(new OleTopupDialogFragment.TopupDialogFragmentCallback() {
            @Override
            public void walletAmount(DialogFragment dialog, String amount) {
                dialog.dismiss();
                if (!amount.isEmpty()) {
                    openPaymentDialog(amount, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", true, true, "", new OlePaymentDialogFragment.PaymentDialogCallback() {
                        @Override
                        public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                            topupWalletAPI(true, amount, paymentMethod, orderRef);
                        }
                    });
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "TopupDialogFragment");
    }

    @Override
    public void onItemRootViewClicked(@NonNull OleWalletSection section, OleTransactionHistory history) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("WalletDetailDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleWalletDetailDialogFragment dialogFragment = new OleWalletDetailDialogFragment(history);
        dialogFragment.show(fragmentTransaction, "WalletDetailDialogFragment");
    }

    private void getWalletAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getWallet(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            binding.tvAmount.setText(object.getJSONObject(Constants.kData).getString("my_credit"));
                            JSONArray arr = object.getJSONObject(Constants.kData).getJSONArray("wallet_transactions");
                            oleWalletTransactions.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleWalletTransactions.add(gson.fromJson(arr.get(i).toString(), OleWalletTransaction.class));
                            }
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
                binding.pullRefresh.setRefreshing(false);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void topupWalletAPI(boolean isLoader, String amount, String paymentMethod, String orderRef) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addAmountInWallet(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), orderRef, paymentMethod, amount, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            getWalletAPI(true);
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
