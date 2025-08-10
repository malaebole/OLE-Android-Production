package ae.oleapp.owner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

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
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubPricingAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityClubPricingBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.dialogs.OleMembershipPaymentDialogFragment;
import ae.oleapp.models.OleMembershipPlan;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleClubPricingActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityClubPricingBinding binding;
    private final List<OleMembershipPlan> oleMembershipPlans = new ArrayList<>();
    private OleClubPricingAdapter adapter;
    private String clubId = "";
    private String selectedAmount = "";
    private boolean canChoose = false;
    private boolean includedSMS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityClubPricingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.membership_plan);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            canChoose = bundle.getBoolean("can_choose", false);
        }

        GridLayoutManager facLayoutManager  = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(facLayoutManager);
        adapter = new OleClubPricingAdapter(getContext(), oleMembershipPlans, canChoose);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        getPlansAPI(true);

        binding.btmVu.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.smsVu.setOnClickListener(this);
    }

    OleClubPricingAdapter.ItemClickListener itemClickListener = new OleClubPricingAdapter.ItemClickListener() {
        @Override
        public void choosePlanClicked(View view, int pos) {
            adapter.setSelectedPos(pos);
            if (canChoose) {
                binding.btmVu.setVisibility(View.VISIBLE);
                populateData();
            }
            else {
                binding.btmVu.setVisibility(View.GONE);
            }
        }
    };

    private void populateData() {
        OleMembershipPlan plan = oleMembershipPlans.get(adapter.getSelectedPos());
        if (includedSMS) {
            selectedAmount = plan.getPrice();
            binding.tvPrice.setText(String.format("%s %s", selectedAmount, plan.getCurrency()));
            binding.tvDesc.setText(R.string.sms_will_be_sent_to_players);
        }
        else {
            double smsCharges = Double.parseDouble(plan.getSmsCharges());
            double amount = Double.parseDouble(plan.getPrice()) - smsCharges;
            selectedAmount = String.format(Locale.ENGLISH, "%.1f", amount);
            binding.tvPrice.setText(String.format("%s %s", selectedAmount, plan.getCurrency()));
            binding.tvDesc.setText(R.string.sms_will_not_be_sent);
        }
    }

    private void showPayment(OleMembershipPlan plan) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("MembershipPaymentDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleMembershipPaymentDialogFragment dialogFragment = new OleMembershipPaymentDialogFragment(clubId, selectedAmount, plan.getCurrency(), plan.getTitle());
        dialogFragment.setDialogCallback(new OleMembershipPaymentDialogFragment.MembershipPaymentDialogCallback() {
            @Override
            public void didConfirm(boolean status, String paymentMethod, String orderRef, String couponId, String discount) {
                paymentPlansAPI(true, plan.getId(), paymentMethod, orderRef, couponId, discount);
            }
        });
        dialogFragment.show(fragmentTransaction, "MembershipPaymentDialogFragment");
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnConfirm) {
            if (adapter.getSelectedPos() != -1) {
                showPayment(oleMembershipPlans.get(adapter.getSelectedPos()));
            }
        }
        else if (v == binding.smsVu) {
            if (includedSMS) {
                includedSMS = false;
                binding.imgCheck.setImageResource(R.drawable.uncheck);
            }
            else {
                includedSMS = true;
                binding.imgCheck.setImageResource(R.drawable.blue_check);
            }
            populateData();
        }
    }

    private void getPlansAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.olePlans(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleMembershipPlans.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleMembershipPlans.add(gson.fromJson(arr.get(i).toString(), OleMembershipPlan.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            oleMembershipPlans.clear();
                            adapter.notifyDataSetChanged();
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

    private void paymentPlansAPI(boolean isLoader, String planId, String paymentMethod, String orderRef, String couponId, String discount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.planPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, planId, paymentMethod, Functions.getIPAddress(), orderRef, couponId, discount);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showAlert(getContext(), getString(R.string.success), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    finish();
                                }
                            });
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