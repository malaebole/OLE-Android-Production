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

import ae.oleapp.adapters.ExpenseSelectionListAdapter;
import ae.oleapp.adapters.SelectionListAdapter;
import ae.oleapp.databinding.ExpenseSelectionListDialogBinding;
import ae.oleapp.databinding.SelectionListDialogBinding;
import ae.oleapp.models.ExpenseList;
import ae.oleapp.models.SelectionList;

public class ExpenseSelectionListDialog extends Dialog {

    private ExpenseSelectionListDialogBinding binding;

    private final Context context;
    private OnItemSelected onItemSelected;
    private List<ExpenseList> lists;
    private String title = "";
    private ExpenseSelectionListAdapter adapter;
    private Boolean isShowSearch = false;
    private final List<ExpenseList> filtered = new ArrayList<>();
    private boolean isSearchActive = false;

    public ExpenseSelectionListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ExpenseSelectionListDialog(Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;

    }

    public void setShowSearch(Boolean showSearch) {
        isShowSearch = showSearch;
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setLists(List<ExpenseList> lists) {
        this.lists = lists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ExpenseSelectionListDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

//        binding.tvTitle.setText(title);

//        if (isMultiSelection) {
//            binding.btnDone.setVisibility(View.VISIBLE);
//        }
//        else {
//            binding.btnDone.setVisibility(View.GONE);
//        }

        if (isShowSearch) {
            binding.searchVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.searchVu.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        adapter = new ExpenseSelectionListAdapter(context, lists);
        adapter.setOnItemClickListener(new ExpenseSelectionListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                ExpenseList item;
                if (isSearchActive) {
                    item = filtered.get(position);
                }
                else {
                    item = lists.get(position);
                }

                List<ExpenseList> expenseListList = new ArrayList<>();
                expenseListList.add(item);
                onItemSelected.selectedItem(expenseListList);
                dismiss();

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
                        if (lists.get(i).getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(lists.get(i));
                        }
                    }
                    isSearchActive = true;
                    adapter.setDatasource(filtered);
                }
                return true;
            }
        });

    }


    public interface OnItemSelected {
        void selectedItem(List<ExpenseList> expenseLists);
    }
}
