package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
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
import ae.oleapp.adapters.OleIncomeHistoryAdapter;
import ae.oleapp.adapters.PartnerHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityBankHistoryBinding;
import ae.oleapp.databinding.ActivityPartnerHistoryBinding;
import ae.oleapp.dialogs.PartnerHistoryBottomSheetDialogFragment;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.models.PartnerData;
import ae.oleapp.models.PartnerHistoryModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerHistoryActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPartnerHistoryBinding binding;
    private PartnerHistoryAdapter partnerHistoryAdapter;

    private PartnerData partnerData;

    private final List<PartnerHistoryModel> partnerHistoryModelList =  new ArrayList<>();
    private String clubId = "", partnerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartnerHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Intent intent = getIntent();
        clubId = intent.getStringExtra("club_id");
        partnerId = intent.getStringExtra("partner_id");
        if (!clubId.isEmpty()) {
            getPartnerPaidHistory(true, clubId, partnerId, "", "", "","");

        }
        LinearLayoutManager partnerhistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.partnerHistoryRecyclerVu.setLayoutManager(partnerhistoryLayoutManager);
        partnerHistoryAdapter = new PartnerHistoryAdapter(getContext(), partnerHistoryModelList);
        partnerHistoryAdapter.setItemClickListener(itemClickListener);
        binding.partnerHistoryRecyclerVu.setAdapter(partnerHistoryAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);

    }

    PartnerHistoryAdapter.ItemClickListener itemClickListener = new PartnerHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            showPartnerDetail(partnerHistoryModelList.get(pos).getId());

        }
    };

    private void getPartnerPaidHistory(Boolean isLoader, String clubId ,String partnerId, String filterBy, String bankId, String from, String to) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.partnerPaidHistory(Functions.getAppLang(getContext()), clubId, partnerId, filterBy, bankId, from, to);
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
                                partnerHistoryModelList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    partnerHistoryModelList.add(gson.fromJson(data.get(i).toString(), PartnerHistoryModel.class));
                                }
                                JSONObject obj = object.getJSONObject("partner_data");
                                Gson gson1 = new Gson();
                                partnerData = gson1.fromJson(obj.toString(), PartnerData.class);
                                populateData();
                                binding.partnerHistoryRecyclerVu.setAdapter(partnerHistoryAdapter);
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


    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        } else if (v == binding.filterBtn) {
            showFilter();
        }
    }

    private void populateData(){
        String text =  partnerData.getShares();
        String formattedText = text + "%";
        SpannableString spannableString = new SpannableString(formattedText);
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.5f);
        spannableString.setSpan(relativeSizeSpan, text.length(), formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlignmentSpan.Standard alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
        spannableString.setSpan(alignmentSpan, text.length(), formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvSharePercentage.setText(spannableString);
        binding.tvTotalAmount.setText(partnerData.getPaidAmount());
        binding.tvName.setText(partnerData.getName());
        binding.tvIncomeCur.setText(partnerData.getCurrency());
        if (!partnerData.getPhotoUrl().isEmpty()){
            Glide.with(getContext()).load(partnerData.getPhotoUrl()).into(binding.imgVu);
        }
    }

    protected void showPartnerDetail(String partnerId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("PartnerHistoryBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        PartnerHistoryBottomSheetDialogFragment dialogFragment = new PartnerHistoryBottomSheetDialogFragment(partnerId,partnerHistoryModelList);
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();

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

            getPartnerPaidHistory(true, clubId, partnerId, filterBy, bankId, from, to);


        });
        dialogFragment.show(fragmentTransaction, "FilterFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}