package ae.oleapp.shop;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
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
import ae.oleapp.adapters.OleAddressListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddressListBinding;
import ae.oleapp.models.OleShopAddress;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddressListBinding binding;
    private OleAddressListAdapter adapter;
    private final List<OleShopAddress> addressList = new ArrayList<>();
    private final List<OleShopAddress> pickupList = new ArrayList<>();
    private boolean isPickup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.checkout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            double price = bundle.getDouble("price", 0);
            String currency = bundle.getString("currency", "");
            binding.tvReview.setText(getString(R.string.review_pay_place, price, currency));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleAddressListAdapter(this, addressList, true);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnReview.setOnClickListener(this);
        binding.btnAddAddress.setOnClickListener(this);

        isPickup = true;
        binding.btnAddAddress.setVisibility(View.GONE);
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0), true);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    isPickup = true;
                    adapter.setDatasource(pickupList, true);
                    binding.btnAddAddress.setVisibility(View.GONE);
                }
                else {
                    isPickup = false;
                    adapter.setDatasource(addressList, false);
                    binding.btnAddAddress.setVisibility(View.VISIBLE);
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

    @Override
    protected void onResume() {
        super.onResume();
        getAddressListAPI(addressList.isEmpty());
    }

    OleAddressListAdapter.ItemClickListener itemClickListener = new OleAddressListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.setSelectedIndex(pos);
        }

        @Override
        public void deleteClicked(View view, int pos) {
            OleShopAddress address = addressList.get(pos);
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.delete))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                deleteAddressAPI(true, address.getId(), pos);
                            }
                        }
                    }).show();
        }

        @Override
        public void editClicked(View view, int pos) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("AddAddressFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            AddAddressFragment addressFragment = new AddAddressFragment(addressList.get(pos));
            addressFragment.setFragmentCallback(new AddAddressFragment.AddAddressFragmentCallback() {
                @Override
                public void addressAdded(DialogFragment dialogFragment) {
                    dialogFragment.dismiss();
                    getAddressListAPI(false);
                }
            });
            addressFragment.show(fragmentTransaction, "AddAddressFragment");
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnReview) {
            reviewClicked();
        }
        else if (v == binding.btnAddAddress) {
            addClicked();
        }
    }

    private void reviewClicked() {
        if (adapter.getSelectedIndex() == -1) {
            Functions.showToast(getContext(), getString(R.string.select_address), FancyToast.ERROR);
            return;
        }
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("is_pickup", isPickup);
        Gson gson = new Gson();
        if (isPickup) {
            intent.putExtra("address", gson.toJson(pickupList.get(adapter.getSelectedIndex())));
        }
        else {
            intent.putExtra("address", gson.toJson(addressList.get(adapter.getSelectedIndex())));
        }
        startActivity(intent);
    }

    private void addClicked() {
        Intent intent = new Intent(this, ShopMapActivity.class);
        startActivity(intent);
    }

    private void getAddressListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAddress(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            JSONArray arrP = object.getJSONArray("pickup");
                            Gson gson = new Gson();
                            addressList.clear();
                            pickupList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                addressList.add(gson.fromJson(arr.get(i).toString(), OleShopAddress.class));
                            }
                            for (int i = 0; i < arrP.length(); i++) {
                                pickupList.add(gson.fromJson(arrP.get(i).toString(), OleShopAddress.class));
                            }
                            if (isPickup) {
                                adapter.setDatasource(pickupList, true);
                            }
                            else {
                                adapter.setDatasource(addressList, false);
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

    private void deleteAddressAPI(boolean isLoader, String addressId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteAddress(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), addressId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            addressList.remove(pos);
                            adapter.setSelectedIndex(-1);
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