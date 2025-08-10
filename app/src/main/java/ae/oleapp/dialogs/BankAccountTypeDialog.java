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

import ae.oleapp.adapters.BankAccountTypeAdapter;
import ae.oleapp.adapters.BankListSelectionListAdapter;
import ae.oleapp.adapters.ExpenseTypeListAdapter;
import ae.oleapp.databinding.BankAccountTypeDialogBinding;
import ae.oleapp.databinding.ClubBankSelectionListDialogBinding;
import ae.oleapp.databinding.ExpenseTypeDialogBinding;
import ae.oleapp.models.AccountTypeModel;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.ExpenseTypeModel;

public class BankAccountTypeDialog  extends Dialog {

    private BankAccountTypeDialogBinding binding;

    private final Context context;
    private OnItemSelected onItemSelected;
    private List<AccountTypeModel> lists;
    private String title = "";
    private BankAccountTypeAdapter adapter;
    private Boolean isShowSearch = false;
    private final List<AccountTypeModel> filtered = new ArrayList<>();
    private boolean isSearchActive = false;

    public BankAccountTypeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public BankAccountTypeDialog(Context context, String title) {
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

    public void setLists(List<AccountTypeModel> lists) {
        this.lists = lists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = BankAccountTypeDialogBinding.inflate(getLayoutInflater());
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

        adapter = new BankAccountTypeAdapter(context, lists);
        adapter.setOnItemClickListener(new BankAccountTypeAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                AccountTypeModel item;
                if (isSearchActive) {
                    item = filtered.get(position);
                } else {
                    item = lists.get(position);
                }

                List<AccountTypeModel> accountTypeModels = new ArrayList<>();
                accountTypeModels.add(item);
                onItemSelected.selectedItem(accountTypeModels,position);
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
        void selectedItem(List<AccountTypeModel> accountTypeModels, int pos);
    }
}