package ae.oleapp.dialogs;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleShopFilterAdapter;
import ae.oleapp.databinding.FragmentShopFilterDialogBinding;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.OleShopFilter;

public class OleShopFilterDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentShopFilterDialogBinding binding;
    private List<OleShopFilter> filterList = new ArrayList<>();
    private List<OleShopFilter> selectedFilter = new ArrayList<>();
    private String minPrice = "";
    private String maxPrice = "";
    private ShopFilterDialogCallback dialogCallback;
    private OleShopFilterAdapter filterAdapter;

    public OleShopFilterDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(ShopFilterDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public OleShopFilterDialogFragment(List<OleShopFilter> filterList, List<OleShopFilter> selectedFilter, String minPrice, String maxPrice) {
        this.filterList = filterList;
        this.selectedFilter = selectedFilter;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShopFilterDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.toolbarTitle.setText(R.string.filter);

        binding.etMinPrice.setText(minPrice);
        binding.etMaxPrice.setText(maxPrice);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        filterAdapter = new OleShopFilterAdapter(getContext(), filterList);
        filterAdapter.setSelectedFilter(selectedFilter);
        filterAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(filterAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    OleShopFilterAdapter.ItemClickListener itemClickListener = new OleShopFilterAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleShopFilter filter = filterList.get(pos);
            List<OleSelectionList> oleSelectionList = new ArrayList<>();
            for (String str: filter.getValues()) {
                oleSelectionList.add(new OleSelectionList(filter.getTitle(), str));
            }
            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), filter.getTitle(), false);
            dialog.setLists(oleSelectionList);
            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<OleSelectionList> selectedItems) {
                    OleSelectionList item = selectedItems.get(0);
                    int index = -1;
                    for (int i = 0; i < selectedFilter.size(); i++) {
                        if (selectedFilter.get(i).getTitle().equalsIgnoreCase(item.getId())) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        OleShopFilter oleShopFilter = new OleShopFilter();
                        oleShopFilter.setTitle(item.getId());
                        oleShopFilter.setValue(item.getValue());
                        selectedFilter.add(oleShopFilter);
                    }
                    else {
                        selectedFilter.get(index).setValue(item.getValue());
                    }
                    filterAdapter.setSelectedFilter(selectedFilter);
                    filterAdapter.notifyDataSetChanged();
                }
            });
            dialog.show();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            dismiss();
        }
        else if (v == binding.btnApply) {
            dialogCallback.filters(this, selectedFilter, binding.etMinPrice.getText().toString(), binding.etMaxPrice.getText().toString());
        }
        else if (v == binding.btnReset) {
            dialogCallback.filters(this, new ArrayList<>(), "", "");
        }
    }

    public interface ShopFilterDialogCallback {
        void filters(DialogFragment df, List<OleShopFilter> filter, String minPrice, String maxPrice);
    }
}