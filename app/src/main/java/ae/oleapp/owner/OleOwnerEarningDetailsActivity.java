package ae.oleapp.owner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleOwnerEarningAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityOwnerEarningDetailsBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.dialogs.OleEarningTransferDialogFragment;
import ae.oleapp.dialogs.OleOwnerEarningDialogFragment;
import ae.oleapp.models.Earning;
import ae.oleapp.models.OleEarningData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleOwnerEarningDetailsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityOwnerEarningDetailsBinding binding;
    private OleOwnerEarningAdapter adapter;
    private final List<OleEarningData> earningList = new ArrayList<>();
    private String fromDate = "", toDate = "", clubId = "";
    private boolean isReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityOwnerEarningDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.earnings);
        
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromDate = bundle.getString("from_date", "");
            toDate = bundle.getString("to_date", "");
            clubId = bundle.getString("club_id", "");
        }

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OleOwnerEarningAdapter(getContext(), earningList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColor));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fromDate = toDate = "";
                if (isReceived) {
                    getEarningsAPI(false, "received");
                }
                else {
                    getEarningsAPI(false, "transfer");
                }
            }
        });

        isReceived = true;
        getEarningsAPI(true, "received");

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.relCalendar.setOnClickListener(this);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    isReceived = true;
                    getEarningsAPI(true, "received");
                }
                else {
                    isReceived = false;
                    getEarningsAPI(true, "transfer");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    OleOwnerEarningAdapter.ItemClickListener itemClickListener = new OleOwnerEarningAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int section, int pos) {
            Earning earning = earningList.get(section).getEarnings().get(pos);
            if (earning.getType().equalsIgnoreCase("bank_transfer")) {
                openTransferDialog(earning.getBookingId(), earning.getType());
            }
            else {
                openEarningDialog(earning.getBookingId(), earning.getType());
            }
        }
    };

    private void openTransferDialog(String bookingId, String type) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EarningTransferDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleEarningTransferDialogFragment dialogFragment = new OleEarningTransferDialogFragment();
        dialogFragment.show(fragmentTransaction, "EarningTransferDialogFragment");
    }

    private void openEarningDialog(String bookingId, String type) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("OwnerEarningDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleOwnerEarningDialogFragment dialogFragment = new OleOwnerEarningDialogFragment(bookingId, type);
        dialogFragment.show(fragmentTransaction, "OwnerEarningDialogFragment");
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.relCalendar) {
            calendarClicked();
        }
    }

    private void calendarClicked() {
        showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
            @Override
            public void filterData(DialogFragment df, String from, String to) {
                df.dismiss();
                fromDate = from;
                toDate = to;
                if (isReceived) {
                    getEarningsAPI(true, "received");
                }
                else {
                    getEarningsAPI(true, "transfer");
                }
            }
        });
    }

    private void getEarningsAPI(boolean isLoader, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOwnerCardEarnings( Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fromDate, toDate, clubId, "", type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            earningList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                earningList.add(gson.fromJson(arr.get(i).toString(), OleEarningData.class));
                            }
                            adapter.notifyDataSetChanged();

                            if (earningList.size() == 0) {
                                Functions.showToast(getContext(), getString(R.string.data_not_found), FancyToast.ERROR);
                            }
                        }
                        else {
                            earningList.clear();
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
}