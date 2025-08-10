package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.adapters.SelectionListAdapter;
import ae.oleapp.databinding.SelectionListDialogBinding;
import ae.oleapp.models.SelectionList;

public class SelectionListDialog extends Dialog {

    private SelectionListDialogBinding binding;
    private final Context context;
    private OnItemSelected onItemSelected;
    private List<SelectionList> lists;
    private String title = "";
    private Boolean isMultiSelection = false;
    private SelectionListAdapter adapter;
    private Boolean isShowSearch = false;
    private final List<SelectionList> filtered = new ArrayList<>();
    private boolean isSearchActive = false;

    public SelectionListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public SelectionListDialog(Context context, String title, Boolean isMultiSelection) {
        super(context);
        this.context = context;
        this.title = title;
        this.isMultiSelection = isMultiSelection;
    }

    public void setShowSearch(Boolean showSearch) {
        isShowSearch = showSearch;
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setLists(List<SelectionList> lists) {
        this.lists = lists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = SelectionListDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvTitle.setText(title);

        if (isMultiSelection) {
            binding.btnDone.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnDone.setVisibility(View.GONE);
        }

        if (isShowSearch) {
            binding.searchVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.searchVu.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        adapter = new SelectionListAdapter(context, lists);
        adapter.setOnItemClickListener(new SelectionListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                SelectionList item;
                if (isSearchActive) {
                    item = filtered.get(position);
                }
                else {
                    item = lists.get(position);
                }
                if (isMultiSelection) {
                    adapter.selectItem(item);
                }
                else {
                    List<SelectionList> selectedList = new ArrayList<>();
                    selectedList.add(item);
                    onItemSelected.selectedItem(selectedList);
                    dismiss();
                }
            }
        });
        binding.recyclerVu.setAdapter(adapter);

        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = binding.searchVu.getQuery().toString();
                if (query.equalsIgnoreCase("")) {
                    isSearchActive = false;
                    adapter.setDatasource(lists);
                }
                else {
                    filtered.clear();
                    for (int i = 0; i < lists.size(); i++) {
                        if (lists.get(i).getValue().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(lists.get(i));
                        }
                    }
                    isSearchActive = true;
                    adapter.setDatasource(filtered);
                }
                return true;
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneClicked();
            }
        });
    }

    private void doneClicked() {
        if (adapter.getSelectedList().size() == 0) {
            dismiss();
        }
        else {
            onItemSelected.selectedItem(adapter.getSelectedList());
            dismiss();
        }
    }

    public interface OnItemSelected {
        void selectedItem(List<SelectionList> selectedItems);
    }
}
