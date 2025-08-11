package ae.oleapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubDetailFacAdapter;
import ae.oleapp.adapters.OleOwnerEarningAdapter;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentOwnerEarningBinding;
import ae.oleapp.dialogs.OleEarningTransferDialogFragment;
import ae.oleapp.dialogs.OleExportDialogFragment;
import ae.oleapp.dialogs.OleOwnerEarningDialogFragment;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.Earning;
import ae.oleapp.models.OleEarningData;
import ae.oleapp.owner.OleEarningBalanceDetailActivity;
import ae.oleapp.owner.OleOwnerEarningDetailsActivity;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleOwnerEarningFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentOwnerEarningBinding binding;
    private OleOwnerEarningAdapter adapter;
    private final List<OleEarningData> earningList = new ArrayList<>();
    private OleOleBookingCountFilterFragment filterFragment;
    private String fromDate = "", toDate = "", clubId = "", paymentType = "";
    private OleClubDetailFacAdapter facilityAdapter;
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();

    public OleOwnerEarningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentOwnerEarningBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleOwnerEarningAdapter(getContext(), earningList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        GridLayoutManager facLayoutManager  = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        facilityAdapter = new OleClubDetailFacAdapter(getContext(), clubFacilities, true);
        binding.facRecyclerVu.setAdapter(facilityAdapter);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEarningsAPI(false);
            }
        });

        binding.filterBg.setOnClickListener(this);
        binding.onlinePaymentVu.setOnClickListener(this);
        binding.btnExport.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.relFilter.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.latePaymentVu.setOnClickListener(this);
        binding.paidBalanceVu.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBadgeValue();
        clubId = Functions.getPrefValue(getContext(), Constants.kOwnerEarningSelectedClub);
        fromDate = Functions.getPrefValue(getContext(), Constants.kOwnerEarningFromDate);
        toDate = Functions.getPrefValue(getContext(), Constants.kOwnerEarningToDate);
        populateDate();
        getEarningsAPI(earningList.isEmpty());
    }

    private void populateDate() {
        if (!fromDate.equalsIgnoreCase("") && !toDate.equalsIgnoreCase("")) {
            binding.tvDate.setText(String.format("%s %s %s %s", fromDate, getString(R.string.to), toDate, getString(R.string.earnings)));
        }
        else if (!fromDate.equalsIgnoreCase("")) {
            binding.tvDate.setText(String.format("%s %s", fromDate, getString(R.string.earnings)));
        }
        else {
            binding.tvDate.setText(getString(R.string.today_earning));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    private void openEarningDialog(String bookingId, String type) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("OwnerEarningDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleOwnerEarningDialogFragment dialogFragment = new OleOwnerEarningDialogFragment(bookingId, type);
        dialogFragment.show(fragmentTransaction, "OwnerEarningDialogFragment");
    }

    private void openTransferDialog(String bookingId, String type) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("EarningTransferDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleEarningTransferDialogFragment dialogFragment = new OleEarningTransferDialogFragment();
        dialogFragment.show(fragmentTransaction, "EarningTransferDialogFragment");
    }

    public void filterClicked() {
        if (filterFragment != null && filterFragment.isVisible()) {
            removeFilterFrag();
        }
        else {
            binding.filterBg.setVisibility(View.VISIBLE);
            binding.filterBg.bringToFront();
            binding.filterContainer.bringToFront();
            filterFragment = new OleOleBookingCountFilterFragment(fromDate, toDate, clubId, true, paymentType);
            filterFragment.setFragmentCallBack(fragmentCallBack);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.filter_container, filterFragment).commit();
        }
    }

    private void removeFilterFrag() {
        getChildFragmentManager().beginTransaction().remove(filterFragment).commit();
        filterFragment = null;
        binding.filterBg.setVisibility(View.GONE);
    }

    OleOleBookingCountFilterFragment.BookingCountFilterFragmentCallBack fragmentCallBack = new OleOleBookingCountFilterFragment.BookingCountFilterFragmentCallBack() {
        @Override
        public void getFilters(String from, String to, String clubId, String paymentType) {
            fromDate = from;
            toDate = to;
            OleOwnerEarningFragment.this.clubId = clubId;
            OleOwnerEarningFragment.this.paymentType = paymentType;
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (clubId.equalsIgnoreCase("")) {
                editor.remove(Constants.kOwnerEarningSelectedClub);
            }
            else {
                editor.putString(Constants.kOwnerEarningSelectedClub, clubId);
            }
            if (fromDate.equalsIgnoreCase("")) {
                editor.remove(Constants.kOwnerEarningFromDate);
            }
            else {
                editor.putString(Constants.kOwnerEarningFromDate, fromDate);
            }
            if (toDate.equalsIgnoreCase("")) {
                editor.remove(Constants.kOwnerEarningToDate);
            }
            else {
                editor.putString(Constants.kOwnerEarningToDate, toDate);
            }
            populateDate();
            editor.apply();
            getEarningsAPI(true);
            removeFilterFrag();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.filterBg) {
            filterBgClicked();
        }
        else if (v == binding.onlinePaymentVu) {
            onlineClicked();
        }
        else if (v == binding.btnExport) {
            exportClicked();
        }
        else if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relFilter) {
            filterClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.latePaymentVu) {
            latePaymentClicked(false);
        }
        else if (v == binding.paidBalanceVu) {
            latePaymentClicked(true);
        }
    }

    private void latePaymentClicked(boolean isPaid) {
        Intent intent = new Intent(getContext(), OleEarningBalanceDetailActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("from_date", fromDate);
        intent.putExtra("to_date", toDate);
        intent.putExtra("is_paid", isPaid);
        startActivity(intent);
    }

    private void notifClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    private void menuClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void filterBgClicked() {
        removeFilterFrag();
    }

    private void onlineClicked() {
        Intent intent = new Intent(getContext(), OleOwnerEarningDetailsActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("from_date", fromDate);
        intent.putExtra("to_date", toDate);
        startActivity(intent);
    }

    private void exportClicked() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("ExportDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleExportDialogFragment dialogFragment = new OleExportDialogFragment();
        dialogFragment.setDialogCallback(new OleExportDialogFragment.ExportDialogFragmentCallback() {
            @Override
            public void export(String email) {
                exportEarningsAPI(true, email);
            }
        });
        dialogFragment.show(getChildFragmentManager(), "ExportDialogFragment");
    }

    private void getEarningsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOwnerEarnings(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID),  clubId, "", fromDate, toDate);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONObject(Constants.kData).getJSONArray("earnings");
                            JSONArray facArr = object.getJSONObject(Constants.kData).getJSONArray("facilities");
                            String facAmount = object.getJSONObject(Constants.kData).getString("facilities_total");
                            String cash = object.getJSONObject(Constants.kData).getString("cash");
                            String card = object.getJSONObject(Constants.kData).getString("card");
                            String pos = object.getJSONObject(Constants.kData).getString("pos");
                            String currency = object.getJSONObject(Constants.kData).getString("currency");
                            String customers = object.getJSONObject(Constants.kData).getString("customers");
                            String totalEarning = object.getJSONObject(Constants.kData).getString("total_earning");
                            String totalBooking = object.getJSONObject(Constants.kData).getString("total_booking");
                            String totalHours = object.getJSONObject(Constants.kData).getString("total_hours");
                            String totalCanceled = object.getJSONObject(Constants.kData).getString("total_canceled");
                            String paidBalance = object.getJSONObject(Constants.kData).getString("paid_balance");
                            String latePayment = object.getJSONObject(Constants.kData).getString("total_balance");
                            String discount = object.getJSONObject(Constants.kData).getString("total_dicount");
                            String matchFee = object.getJSONObject(Constants.kData).getString("total_match_fee");
                            String expense = object.getJSONObject(Constants.kData).getString("expenses");
                            binding.tvFacTotal.setText(String.format("%s: %s %s", getString(R.string.total_amount), facAmount, currency));
                            binding.tvTotalEarning.setText(String.format("%s %s", totalEarning, currency));
                            binding.tvNewCustomers.setText(getString(R.string.new_customers, customers));
                            binding.tvCash.setText(String.format("%s %s", cash, currency));
                            binding.tvOnline.setText(String.format("%s %s", card, currency));
                            binding.tvPos.setText(String.format("%s %s", pos, currency));
                            binding.tvPaidBalance.setText(String.format("%s %s", paidBalance, currency));
                            binding.tvLatePayment.setText(String.format("%s %s", latePayment, currency));
                            binding.tvDiscount.setText(String.format("%s %s", discount, currency));
                            binding.tvMatchFee.setText(String.format("%s %s", matchFee, currency));
                            binding.tvExpense.setText(String.format("%s %s", expense, currency));
                            binding.tvBookings.setText(totalBooking);
                            binding.tvHours.setText(totalHours);
                            binding.tvCancelled.setText(totalCanceled);
                            earningList.clear();
                            Gson gson = new Gson();
                            clubFacilities.clear();
                            for (int i = 0; i < facArr.length(); i++) {
                                clubFacilities.add(gson.fromJson(facArr.get(i).toString(), OleClubFacility.class));
                            }
                            for (int i = 0; i < arr.length(); i++) {
                                if (paymentType.equalsIgnoreCase("")) {
                                    earningList.add(gson.fromJson(arr.get(i).toString(), OleEarningData.class));
                                }
                                else {
                                    // apply payemnt type filter
                                    OleEarningData oleEarningData = gson.fromJson(arr.get(i).toString(), OleEarningData.class);
                                    List<Earning> filterData = new ArrayList<>();
                                    for (Earning earning : oleEarningData.getEarnings()) {
                                        if (paymentType.equalsIgnoreCase("card")) {
                                            if (!earning.getPaymentMethod().equalsIgnoreCase("cash")) {
                                                filterData.add(earning);
                                            }
                                        }
                                        else {
                                            if (earning.getPaymentMethod().equalsIgnoreCase("cash")) {
                                                filterData.add(earning);
                                            }
                                        }
                                    }
                                    oleEarningData.setEarnings(filterData);
                                    earningList.add(oleEarningData);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            if (clubFacilities.isEmpty()) {
                                binding.facVu.setVisibility(View.GONE);
                            }
                            else {
                                binding.facVu.setVisibility(View.VISIBLE);
                                facilityAdapter.notifyDataSetChanged();
                            }

                            if (earningList.size() == 0) {
                                Functions.showToast(getContext(), getString(R.string.data_not_found), FancyToast.ERROR);
                            }
                        }
                        else {
                            earningList.clear();
                            adapter.notifyDataSetChanged();
                            binding.facVu.setVisibility(View.GONE);
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

    private void exportEarningsAPI(boolean isLoader, String email) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.exportEarnings(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), "", "", fromDate, toDate, email);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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