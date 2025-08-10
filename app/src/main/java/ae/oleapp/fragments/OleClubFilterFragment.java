package ae.oleapp.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentClubFilterBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleFieldData;
import ae.oleapp.models.OleFieldDataChild;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OlePClubListActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleClubFilterFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentClubFilterBinding binding;
    public String date = "";
    public String countryId = "";
    public String cityId = "";
    public String offer = "";
    public String openTime = "";
    public String closeTime = "";
    public String fieldSize = "";
    public String fieldType = "";
    public String grassType = "";

    public ClubFilterFragmentCallBack fragmentCallBack;

    public OleClubFilterFragment() {
        // Required empty public constructor
    }

    public void setFragmentCallBack(ClubFilterFragmentCallBack fragmentCallBack) {
        this.fragmentCallBack = fragmentCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentClubFilterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (AppManager.getInstance().oleFieldData == null) {
            BaseActivity baseActivity = null;
            if (getParentFragment() != null) {
                baseActivity = ((BaseActivity)getParentFragment().getActivity());
            }
            if (baseActivity == null) {
                baseActivity = (BaseActivity)getActivity();
            }
            baseActivity.getFieldDataAPI(new BaseActivity.FieldDataCallback() {
                @Override
                public void getFieldData(OleFieldData oleFieldData) {
                    AppManager.getInstance().oleFieldData = oleFieldData;
                }
            });
        }

        populateData();

        binding.relOffer.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.etOpenTime.setOnClickListener(this);
        binding.etCloseTime.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
        binding.etSize.setOnClickListener(this);
        binding.etFieldType.setOnClickListener(this);
        binding.etGrassType.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.btnDateClear.setOnClickListener(this);
        binding.btnCityClear.setOnClickListener(this);
        binding.btnSizeClear.setOnClickListener(this);
        binding.btnFieldtypeClear.setOnClickListener(this);
        binding.btnGrasstypeClear.setOnClickListener(this);
        binding.btnOpentimeClear.setOnClickListener(this);
        binding.btnClosetimeClear.setOnClickListener(this);
        binding.searchVu.setOnClickListener(this);

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.sizeVu.setVisibility(View.GONE);
            binding.grassTypeVu.setVisibility(View.GONE);
        }
        else {
            binding.sizeVu.setVisibility(View.VISIBLE);
            binding.grassTypeVu.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateData() {
        UserInfo userInfo = Functions.getUserinfo(getActivity());
        if (userInfo !=null) {
            countryId = userInfo.getCountry();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date d = formatter.parse(date);
            formatter.applyPattern("dd/MM/yyyy");
            binding.etDate.setText(formatter.format(d));
        } catch (ParseException e) {
            binding.etDate.setText("");
            e.printStackTrace();
        }
        for (OleCountry oleCountry : AppManager.getInstance().countries) {
            if (oleCountry.getId().equalsIgnoreCase(countryId)) {
                String[] arr = cityId.split(",");
                for (String str: arr) {
                    for (OleCountry city : oleCountry.getCities()) {
                        if (city.getId().equalsIgnoreCase(str)) {
                            if (binding.etCity.getText().toString().isEmpty()) {
                                binding.etCity.setText(city.getName());
                            }
                            else {
                                binding.etCity.setText(String.format("%s, %s", binding.etCity.getText().toString(), city.getName()));
                            }
                            break;
                        }
                    }
                }
            }
        }

        // field data
        OleFieldData oleFieldData = AppManager.getInstance().oleFieldData;
        if (oleFieldData != null) {
            for (OleFieldDataChild item: oleFieldData.getFieldSizes()) {
                if (item.getId().equalsIgnoreCase(fieldSize)) {
                    binding.etSize.setText(item.getName());
                    break;
                }
            }
            for (OleFieldDataChild item: oleFieldData.getFiledTypes()) {
                if (item.getId().equalsIgnoreCase(fieldType)) {
                    binding.etFieldType.setText(item.getName());
                    break;
                }
            }
            for (OleFieldDataChild item: oleFieldData.getGrassType()) {
                if (item.getId().equalsIgnoreCase(grassType)) {
                    binding.etGrassType.setText(item.getName());
                    break;
                }
            }
        }

        if (offer.equalsIgnoreCase("1")) {
            binding.imgOffer.setImageResource(R.drawable.check);
        } else {
            binding.imgOffer.setImageResource(R.drawable.uncheck);
        }
        binding.etOpenTime.setText(openTime);
        binding.etCloseTime.setText(closeTime);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relOffer) {
            offerClicked();
        }
        else if (v == binding.etDate) {
            dateClicked();
        }
        else if (v == binding.etOpenTime || v == binding.etCloseTime) {
            timeClicked(v);
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
        else if (v == binding.etSize) {
            sizeClicked();
        }
        else if (v == binding.etFieldType) {
            fieldTypeClicked();
        }
        else if (v == binding.etGrassType) {
            grassTypeClicked();
        }
        else if (v == binding.btnApply) {
            applyClicked();
        }
        else if (v == binding.btnReset) {
            resetClicked();
        }
        else if (v == binding.btnDateClear || v == binding.btnCityClear || v == binding.btnSizeClear || v == binding.btnFieldtypeClear || v == binding.btnGrasstypeClear || v == binding.btnOpentimeClear || v == binding.btnClosetimeClear) {
            clearBtnsClicked(v);
        }
        else if (v == binding.searchVu)
        {
            searchClicked(v);
        }

    }

    private void searchClicked(View view) {
        Intent intent = new Intent(getContext(), OlePClubListActivity.class);
        intent.putExtra("is_search", view == binding.searchVu);
        startActivity(intent);
    }

    private void offerClicked() {
        if (offer.equalsIgnoreCase("")) {
            binding.imgOffer.setImageResource(R.drawable.check);
            offer = "1";
        }
        else {
            binding.imgOffer.setImageResource(R.drawable.uncheck);
            offer = "";
        }
    }

    private void dateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                date = formatter.format(calendar.getTime());
                formatter.applyPattern("dd/MM/yyyy");
                binding.etDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void timeClicked(View clickedView) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
                if (clickedView == binding.etOpenTime) {
                    openTime = formatter.format(calendar.getTime());
                    binding.etOpenTime.setText(openTime);
                }
                else {
                    closeTime = formatter.format(calendar.getTime());
                    binding.etCloseTime.setText(closeTime);
                }
            }
        }, hour, minute, false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.setTimeInterval(1, 30);
        timePickerDialog.show(getChildFragmentManager(), "Datepickerdialog");
    }

    private void cityClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleCountry oleCountry : AppManager.getInstance().countries) {
            if (oleCountry.getId().equalsIgnoreCase(countryId)) {
                for (OleCountry city : oleCountry.getCities()) {
                    oleSelectionList.add(new OleSelectionList(city.getId(), city.getName()));
                }
                break;
            }
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), true);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                cityId = "";
                binding.etCity.setText("");
                for (OleSelectionList selectedItem: selectedItems) {
                    if (binding.etCity.getText().toString().isEmpty()) {
                        cityId = selectedItem.getId();
                        binding.etCity.setText(selectedItem.getValue());
                    }
                    else {
                        cityId = String.format("%s,%s",  cityId, selectedItem.getId());
                        binding.etCity.setText(String.format("%s, %s",  binding.etCity.getText().toString(), selectedItem.getValue()));
                    }
                }
            }
        });
        dialog.show();
    }

    private void sizeClicked() {
        if (AppManager.getInstance().oleFieldData == null) {
            return;
        }
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleFieldDataChild item: AppManager.getInstance().oleFieldData.getFieldSizes()) {
            oleSelectionList.add(new OleSelectionList(item.getId(), item.getName()));
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_size), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                fieldSize = selectedItem.getId();
                binding.etSize.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    private void fieldTypeClicked() {
        if (AppManager.getInstance().oleFieldData == null) {
            return;
        }
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleFieldDataChild item: AppManager.getInstance().oleFieldData.getFiledTypes()) {
            oleSelectionList.add(new OleSelectionList(item.getId(), item.getName()));
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.field_type), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                fieldType = selectedItem.getId();
                binding.etFieldType.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    private void grassTypeClicked() {
        if (AppManager.getInstance().oleFieldData == null) {
            return;
        }
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleFieldDataChild item: AppManager.getInstance().oleFieldData.getGrassType()) {
            oleSelectionList.add(new OleSelectionList(item.getId(), item.getName()));
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.grass_type), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                grassType = selectedItem.getId();
                binding.etGrassType.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    private void applyClicked() {
        if (fragmentCallBack != null) {
            fragmentCallBack.getFilters(date, countryId, cityId, offer, openTime, closeTime, fieldSize, fieldType, grassType);
        }
    }

    private void resetClicked() {
        binding.etDate.setText("");
        date = "";
        binding.etCity.setText("");
        cityId = "";
        binding.etSize.setText("");
        binding.etFieldType.setText("");
        binding.etGrassType.setText("");
        fieldSize = "";
        fieldType = "";
        grassType = "";
        offer = "";
        binding.imgOffer.setImageResource(R.drawable.uncheck);
        binding.etOpenTime.setText("");
        openTime = "";
        binding.etCloseTime.setText("");
        closeTime = "";

        if (fragmentCallBack != null) {
            fragmentCallBack.getFilters(date, countryId, cityId, offer, openTime, closeTime, fieldSize, fieldType, grassType);
        }
    }

    private void clearBtnsClicked(View view) {
        if (binding.btnDateClear.equals(view)) {
            binding.etDate.setText("");
            date = "";
        } else if (binding.btnCityClear.equals(view)) {
            binding.etCity.setText("");
            cityId = "";
        } else if (binding.btnSizeClear.equals(view)) {
            binding.etSize.setText("");
            fieldSize = "";
        } else if (binding.btnFieldtypeClear.equals(view)) {
            binding.etFieldType.setText("");
            fieldType = "";
        } else if (binding.btnGrasstypeClear.equals(view)) {
            binding.etGrassType.setText("");
            grassType = "";
        } else if (binding.btnOpentimeClear.equals(view)) {
            binding.etOpenTime.setText("");
            openTime = "";
        } else if (binding.btnClosetimeClear.equals(view)) {
            binding.etCloseTime.setText("");
            closeTime = "";
        }
    }

    public interface ClubFilterFragmentCallBack {
        void getFilters(String date, String countryId, String cityId, String offer, String openTime, String closeTime, String fieldSize, String fieldType, String grassType);
    }
}
