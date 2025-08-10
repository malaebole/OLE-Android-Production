package ae.oleapp.dialogs;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.ShopFilterAdapter;
import ae.oleapp.databinding.FragmentShopFilterDialogBinding;
import ae.oleapp.models.SelectionList;
import ae.oleapp.models.ShopFilter;

public class ShopFilterDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentShopFilterDialogBinding binding;
    private List<ShopFilter> filterList = new ArrayList<>();
    private List<ShopFilter> selectedFilter = new ArrayList<>();
    private String minPrice = "";
    private String maxPrice = "";
    private ShopFilterDialogCallback dialogCallback;
    private ShopFilterAdapter filterAdapter;

    public ShopFilterDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(ShopFilterDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public ShopFilterDialogFragment(List<ShopFilter> filterList, List<ShopFilter> selectedFilter, String minPrice, String maxPrice) {
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
        filterAdapter = new ShopFilterAdapter(getContext(), filterList);
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

    ShopFilterAdapter.ItemClickListener itemClickListener = new ShopFilterAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            ShopFilter filter = filterList.get(pos);
            List<SelectionList> selectionList = new ArrayList<>();
            for (String str: filter.getValues()) {
                selectionList.add(new SelectionList(filter.getTitle(), str));
            }
            SelectionListDialog dialog = new SelectionListDialog(getContext(), filter.getTitle(), false);
            dialog.setLists(selectionList);
            dialog.setOnItemSelected(new SelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<SelectionList> selectedItems) {
                    SelectionList item = selectedItems.get(0);
                    int index = -1;
                    for (int i = 0; i < selectedFilter.size(); i++) {
                        if (selectedFilter.get(i).getTitle().equalsIgnoreCase(item.getId())) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        ShopFilter shopFilter = new ShopFilter();
                        shopFilter.setTitle(item.getId());
                        shopFilter.setValue(item.getValue());
                        selectedFilter.add(shopFilter);
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
        void filters(DialogFragment df, List<ShopFilter> filter, String minPrice, String maxPrice);
    }
}