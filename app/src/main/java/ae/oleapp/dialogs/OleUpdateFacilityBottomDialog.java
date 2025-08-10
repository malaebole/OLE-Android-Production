package ae.oleapp.dialogs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFacilityListAdapter;
import ae.oleapp.databinding.OleupdateFacilityDialogBinding;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleUpdateFacilityBottomDialog extends DialogFragment {

    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private final List<OleClubFacility> selectedFacilities = new ArrayList<>();
    private final String bookingId;
    private UpdateFacilityBottomDialogCallback dialogCallback;
    private OleFacilityListAdapter facilityAdapter;
    private OleupdateFacilityDialogBinding binding;

    public OleUpdateFacilityBottomDialog(List<OleClubFacility> clubFacilities, List<OleClubFacility> selectedFacilities, String bookingId) {
        this.clubFacilities.addAll(clubFacilities);
        this.selectedFacilities.addAll(selectedFacilities);
        this.bookingId = bookingId;
    }

    public void setDialogCallback(UpdateFacilityBottomDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OleupdateFacilityDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(facLayoutManager);
        facilityAdapter = new OleFacilityListAdapter(getContext(), clubFacilities);
        facilityAdapter.setOnItemClickListener(facClickListener);
        for (OleClubFacility facility: selectedFacilities) {
            if (facility.getMaxQuantity().equalsIgnoreCase("")) {
                facility.setQty(0);
            }
            else {
                facility.setQty(Integer.parseInt(facility.getMaxQuantity()));
                setQtyInClubFac(Integer.parseInt(facility.getMaxQuantity()), facility.getId());
            }
        }
        facilityAdapter.selectedFacility = selectedFacilities;
        binding.recyclerVu.setAdapter(facilityAdapter);

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFacilityAPI(true);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setQtyInClubFac(int qty, String facId) {
        for (OleClubFacility facility: clubFacilities) {
            if (facility.getId().equalsIgnoreCase(facId)) {
                facility.setQty(qty);
                break;
            }
        }
    }

    OleFacilityListAdapter.OnItemClickListener facClickListener = new OleFacilityListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleClubFacility facility = clubFacilities.get(pos);
            facilityAdapter.setSelectedFacility(facility);
        }

        @Override
        public void OnPlusClick(View v, int pos) {
            OleClubFacility facility = clubFacilities.get(pos);
            int maxQty = Integer.parseInt(facility.getMaxQuantity());
            if (facility.getQty() < maxQty) {
                facility.setQty(facility.getQty()+1);
                for (OleClubFacility selectedFac: facilityAdapter.selectedFacility) {
                    if (selectedFac.getId().equalsIgnoreCase(facility.getId())) {
                        selectedFac.setQty(facility.getQty());
                        break;
                    }
                }
                facilityAdapter.notifyDataSetChanged();
            }
            else {
                Functions.showToast(getContext(), String.format(getResources().getString(R.string.you_select_max_qty_place), maxQty), FancyToast.ERROR);
            }
        }

        @Override
        public void OnMinusClick(View v, int pos) {
            OleClubFacility facility = clubFacilities.get(pos);
            facility.setQty(facility.getQty()-1);
            for (OleClubFacility selectedFac: facilityAdapter.selectedFacility) {
                if (selectedFac.getId().equalsIgnoreCase(facility.getId())) {
                    selectedFac.setQty(facility.getQty());
                    break;
                }
            }
            if (facility.getQty() == 0) {
                int index = facilityAdapter.isExistInSelected(facility.getId());
                if (index != -1) {
                    facilityAdapter.selectedFacility.remove(index);
                }
            }
            facilityAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // make custom veriable qty 0 to avoid issue
        for (OleClubFacility facility : clubFacilities) {
            facility.setQty(0);
        }
    }

    private void updateFacilityAPI(boolean isLoader) {
        String facilities = "";
        double facPrice = 0;
        try {
            JSONArray array = new JSONArray();
            for (OleClubFacility facility : facilityAdapter.selectedFacility) {
                JSONObject object = new JSONObject();
                object.put("fac_id", facility.getId());
                if (facility.getQty() > 0) {
                    object.put("qty", String.valueOf(facility.getQty()));
                }
                else {
                    object.put("qty", "");
                }
                array.put(object);
                // calculate price
                if (!facility.getPrice().isEmpty()) {
                    if (facility.getType().equalsIgnoreCase("countable")) {
                        facPrice = facPrice + (Double.parseDouble(facility.getPrice()) * facility.getQty());
                    }
                    else {
                        facPrice = facPrice + Double.parseDouble(facility.getPrice());
                    }
                }
            }
            facilities = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateFacility(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, facilities, facPrice);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dialogCallback.didUpdateFacilities();
                            dismiss();
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

    public interface UpdateFacilityBottomDialogCallback {
        void didUpdateFacilities();
    }
}
