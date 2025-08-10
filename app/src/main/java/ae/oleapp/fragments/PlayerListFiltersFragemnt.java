package ae.oleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentPlayerListFiltersBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Functions;

public class PlayerListFiltersFragemnt extends BaseFragment implements View.OnClickListener {

    private String cityId = "";
    private String countryId = "";
    private String age = "";
    private String point = "";
    private String topPlayer = "";
    private String playedOverAll = "";
    private String playedMonth = "";

    private PlayerListFiltersCallBack filtersCallBack;
    private OlefragmentPlayerListFiltersBinding binding;

    public PlayerListFiltersFragemnt() {
        // Required empty public constructor
    }

    public PlayerListFiltersFragemnt(String cityId, String countryId, String age, String point, String topPlayer, String playedOverAll, String playedMonth) {
        this.cityId = cityId;
        this.countryId = countryId;
        this.age = age;
        this.point = point;
        this.topPlayer = topPlayer;
        this.playedOverAll = playedOverAll;
        this.playedMonth = playedMonth;
    }

    public void setFiltersCallBack(PlayerListFiltersCallBack filtersCallBack) {
        this.filtersCallBack = filtersCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPlayerListFiltersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        populateData();

        binding.etCountry.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.btnCityClear.setOnClickListener(this);
        binding.btnCountryClear.setOnClickListener(this);
        binding.relTop100.setOnClickListener(this);
        binding.relHighPoints.setOnClickListener(this);
        binding.relMostPlayed.setOnClickListener(this);
        binding.relMostPlayedOverall.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateData() {
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

        binding.etAge.setText(age);
        if (topPlayer.equalsIgnoreCase("1")) {
            binding.imgTop100.setImageResource(R.drawable.check);
        }
        if (point.equalsIgnoreCase("1")) {
            binding.imgHighPoints.setImageResource(R.drawable.check);
        }
        if (playedMonth.equalsIgnoreCase("1")) {
            binding.imgMostPlayed.setImageResource(R.drawable.check);
        }
        if (playedOverAll.equalsIgnoreCase("1")) {
            binding.imgMostPlayedOverall.setImageResource(R.drawable.check);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.etCountry) {
            countryClicked();
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
        else if (v == binding.btnReset) {
            resetClicked();
        }
        else if (v == binding.btnCityClear || v == binding.btnCountryClear) {
            clearClicked();
        }
        else if (v == binding.relTop100) {
            topPlayerClicked();
        }
        else if (v == binding.relHighPoints) {
            highPointsClicked();
        }
        else if (v == binding.relMostPlayed) {
            mostPlayedClicked();
        }
        else if (v == binding.relMostPlayedOverall) {
            playedOverallClicked();
        }
        else if (v == binding.btnApply) {
            applyClicked();
        }
    }

    private void countryClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleCountry oleCountry : AppManager.getInstance().countries) {
            oleSelectionList.add(new OleSelectionList(oleCountry.getId(), oleCountry.getName()));
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                cityId = "";
                binding.etCity.setText("");
                OleSelectionList item = selectedItems.get(0);
                binding.etCountry.setText(item.getValue());
                countryId = item.getId();
            }
        });
        dialog.show();
    }

    private void cityClicked() {
        if (countryId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
            return;
        }
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
                for (OleSelectionList selectedItem : selectedItems) {
                    if (binding.etCity.getText().toString().isEmpty()) {
                        cityId = selectedItem.getId();
                        binding.etCity.setText(selectedItem.getValue());
                    } else {
                        cityId = String.format("%s,%s", cityId, selectedItem.getId());
                        binding.etCity.setText(String.format("%s, %s", binding.etCity.getText().toString(), selectedItem.getValue()));
                    }
                }
            }
        });
        dialog.show();
    }

    private void resetClicked() {
        countryId = "";
        cityId = "";
        topPlayer = "";
        point = "";
        playedMonth = "";
        playedOverAll = "";
        age = "";
        binding.etAge.setText("");
        binding.etCity.setText("");
        binding.etCountry.setText("");
        binding.imgTop100.setImageResource(R.drawable.uncheck);
        binding.imgHighPoints.setImageResource(R.drawable.uncheck);
        binding.imgMostPlayedOverall.setImageResource(R.drawable.uncheck);
        binding.imgMostPlayed.setImageResource(R.drawable.uncheck);

        if (filtersCallBack != null) {
            filtersCallBack.getFilters(countryId, cityId, topPlayer, point, playedMonth, playedOverAll, age);
        }
    }

    private void clearClicked() {
        countryId = "";
        cityId = "";
        binding.etCity.setText("");
        binding.etCountry.setText("");
    }

    private void topPlayerClicked() {
        if (topPlayer.isEmpty()) {
            topPlayer = "1";
            binding.imgTop100.setImageResource(R.drawable.check);
        }
        else {
            topPlayer = "";
            binding.imgTop100.setImageResource(R.drawable.uncheck);
        }
    }

    private void highPointsClicked() {
        if (point.isEmpty()) {
            point = "1";
            binding.imgHighPoints.setImageResource(R.drawable.check);
        }
        else {
            point = "";
            binding.imgHighPoints.setImageResource(R.drawable.uncheck);
        }
    }

    private void mostPlayedClicked() {
        if (playedMonth.isEmpty()) {
            playedMonth = "1";
            binding.imgMostPlayed.setImageResource(R.drawable.check);
        }
        else {
            playedMonth = "";
            binding.imgMostPlayed.setImageResource(R.drawable.uncheck);
        }
    }

    private void playedOverallClicked() {
        if (playedOverAll.isEmpty()) {
            playedOverAll = "1";
            binding.imgMostPlayedOverall.setImageResource(R.drawable.check);
        }
        else {
            playedOverAll = "";
            binding.imgMostPlayedOverall.setImageResource(R.drawable.uncheck);
        }
    }

    private void applyClicked() {
        if (filtersCallBack != null) {
            filtersCallBack.getFilters(countryId, cityId, topPlayer, point, playedMonth, playedOverAll, binding.etAge.getText().toString());
        }
    }

    public interface PlayerListFiltersCallBack {
        void getFilters(String countryId, String cityId, String topPlayer, String point, String playedMonth, String playedOverAll, String age);
    }
}
