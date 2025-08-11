package ae.oleapp.shop;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.FragmentAddAddressBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.OleShopAddress;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressFragment extends DialogFragment implements View.OnClickListener {

    private FragmentAddAddressBinding binding;
    private AddAddressFragmentCallback fragmentCallback;
    private boolean isUpdate = false;
    private OleShopAddress oleShopAddress;
    private String isHome = "";
    private String cityId = "";
    private double lat = 0;
    private double lng = 0;
    private List<OleCountry> oleCountryList;

    public AddAddressFragment(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public AddAddressFragment(OleShopAddress oleShopAddress) {
        isUpdate = true;
        this.oleShopAddress = oleShopAddress;
    }

    public void setFragmentCallback(AddAddressFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddAddressBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ((BaseActivity)getActivity()).getCountriesAPI(new BaseActivity.CountriesCallback() {
            @Override
            public void getCountries(List<OleCountry> countries) {
                oleCountryList = countries;
            }
        });

        if (isUpdate) {
            binding.tvBtnTitle.setText(R.string.update);
            binding.etFullname.setText(oleShopAddress.getName());
            binding.etPhone.setText(oleShopAddress.getPhone());
            binding.etCity.setText(oleShopAddress.getCity());
            cityId = oleShopAddress.getCityId();
            binding.etArea.setText(oleShopAddress.getArea());
            binding.etAddress.setText(oleShopAddress.getAddress());
            binding.etInstruction.setText(oleShopAddress.getDeliveryNote());
            if (oleShopAddress.getIsHome().equalsIgnoreCase("1")) {
                binding.etFlatVilla.setHint(R.string.flat_villa_no);
                binding.etFlatVilla.setText(oleShopAddress.getHouseNo());
                homeClicked();
                isHome = "1";
            }
            else {
                binding.etFlatVilla.setHint(R.string.office_no);
                binding.etFlatVilla.setText(oleShopAddress.getOfficeNo());
                officeClicked();
                isHome = "0";
            }
        }
        else {
            binding.tvBtnTitle.setText(R.string.add_now);
            homeClicked();
        }

        binding.btnClose.setOnClickListener(this);
        binding.relHome.setOnClickListener(this);
        binding.relOffice.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.relHome) {
            homeClicked();
        }
        else if (v == binding.relOffice) {
            officeClicked();
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void homeClicked() {
        binding.imgVuHome.setImageResource(R.drawable.check);
        binding.imgVuOffice.setImageResource(R.drawable.uncheck);
        binding.tvHome.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.tvOffice.setTextColor(getResources().getColor(R.color.subTextColor));
        binding.etFlatVilla.setHint(R.string.flat_villa_no);
        isHome = "1";
    }

    private void officeClicked() {
        binding.imgVuHome.setImageResource(R.drawable.uncheck);
        binding.imgVuOffice.setImageResource(R.drawable.check);
        binding.tvHome.setTextColor(getResources().getColor(R.color.subTextColor));
        binding.tvOffice.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.etFlatVilla.setHint(R.string.office_no);
        isHome = "0";
    }

    private void cityClicked() {
        if (oleCountryList != null && oleCountryList.size() > 0) {
            List<OleSelectionList> oleSelectionList = new ArrayList<>();
            String uaeId = "1";
            for (OleCountry oleCountry : oleCountryList) {
                if (oleCountry.getId().equalsIgnoreCase(uaeId)) {
                    for (OleCountry city : oleCountry.getCities()) {
                        oleSelectionList.add(new OleSelectionList(city.getId(), city.getName()));
                    }
                    break;
                }
            }
            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), false);
            dialog.setLists(oleSelectionList);
            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<OleSelectionList> selectedItems) {
                    OleSelectionList selectedItem = selectedItems.get(0);
                    cityId = selectedItem.getId();
                    binding.etCity.setText(selectedItem.getValue());
                }
            });
            dialog.show();
        }
    }

    private void addClicked() {
        if (binding.etFullname.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_full_name), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_mobile_no), FancyToast.ERROR);
            return;
        }
        if (binding.etCity.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_city), FancyToast.ERROR);
            return;
        }
        if (binding.etArea.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_area), FancyToast.ERROR);
            return;
        }
        if (binding.etAddress.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_address), FancyToast.ERROR);
            return;
        }
        if (binding.etFlatVilla.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_flat_villa_no), FancyToast.ERROR);
            return;
        }

        if (isUpdate) {
            updateAddressAPI(true, binding.etFullname.getText().toString(), binding.etPhone.getText().toString(), binding.etAddress.getText().toString(), cityId, binding.etArea.getText().toString(), binding.etFlatVilla.getText().toString(), binding.etInstruction.getText().toString(), oleShopAddress.getId());
        }
        else {
            addAddressAPI(true, binding.etFullname.getText().toString(), binding.etPhone.getText().toString(), binding.etAddress.getText().toString(), cityId, binding.etArea.getText().toString(), binding.etFlatVilla.getText().toString(), binding.etInstruction.getText().toString());
        }
    }

    private void addAddressAPI(boolean isLoader, String name, String phone, String address, String city, String area, String number, String note) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addAddress(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), name, phone, lat, lng, address, city, area, number, isHome, note);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fragmentCallback.addressAdded(AddAddressFragment.this);
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

    private void updateAddressAPI(boolean isLoader, String name, String phone, String address, String city, String area, String number, String note, String addressId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateAddress(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), addressId, name, phone, address, city, area, number, isHome, note);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fragmentCallback.addressAdded(AddAddressFragment.this);
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

    public interface AddAddressFragmentCallback {
        void addressAdded(DialogFragment dialogFragment);
    }
}