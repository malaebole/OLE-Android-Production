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

import ae.oleapp.adapters.BankListSelectionListAdapter;
import ae.oleapp.adapters.ExpenseTypeListAdapter;
import ae.oleapp.databinding.ClubBankSelectionListDialogBinding;
import ae.oleapp.databinding.ExpenseTypeDialogBinding;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.ExpenseTypeModel;

public class ExpenseTypeDialog extends Dialog {

    private ExpenseTypeDialogBinding binding;

    private final Context context;
    private OnItemSelected onItemSelected;
    private List<ExpenseTypeModel> lists;
    private String title = "";
    private ExpenseTypeListAdapter adapter;
    private Boolean isShowSearch = false;
    private final List<ExpenseTypeModel> filtered = new ArrayList<>();
    private boolean isSearchActive = false;

    public ExpenseTypeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ExpenseTypeDialog(Context context, String title) {
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

    public void setLists(List<ExpenseTypeModel> lists) {
        this.lists = lists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ExpenseTypeDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        if (isShowSearch) {
            binding.searchVu.setVisibility(View.VISIBLE);
        } else {
            binding.searchVu.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        adapter = new ExpenseTypeListAdapter(context, lists);
        adapter.setOnItemClickListener(new ExpenseTypeListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                ExpenseTypeModel item;
                if (isSearchActive) {
                    item = filtered.get(position);
                } else {
                    item = lists.get(position);
                }

                List<ExpenseTypeModel> expenseTypeModels = new ArrayList<>();
                expenseTypeModels.add(item);
                onItemSelected.selectedItem(expenseTypeModels);
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
                } else {
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

    }


    public interface OnItemSelected {
        void selectedItem(List<ExpenseTypeModel> expenseTypeModels);
    }
}