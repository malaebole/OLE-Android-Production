package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.adapters.OleSelectionListAdapter;
import ae.oleapp.databinding.SelectionListDialogBinding;
import ae.oleapp.models.OleSelectionList;

public class OleSelectionListDialog extends Dialog {

    private SelectionListDialogBinding binding;
    private final Context context;
    private OnItemSelected onItemSelected;
    private List<OleSelectionList> lists;
    private String title = "";
    private Boolean isMultiSelection = false;
    private OleSelectionListAdapter adapter;

    public OleSelectionListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public OleSelectionListDialog(Context context, String title, Boolean isMultiSelection) {
        super(context);
        this.context = context;
        this.title = title;
        this.isMultiSelection = isMultiSelection;
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setLists(List<OleSelectionList> lists) {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        adapter = new OleSelectionListAdapter(context, lists);
        adapter.setOnItemClickListener(new OleSelectionListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (isMultiSelection) {
                    adapter.selectItem(lists.get(position));
                }
                else {
                    List<OleSelectionList> selectedList = new ArrayList<>();
                    selectedList.add(lists.get(position));
                    onItemSelected.selectedItem(selectedList);
                    dismiss();
                }
            }
        });
        binding.recyclerVu.setAdapter(adapter);

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
        void selectedItem(List<OleSelectionList> selectedItems);
    }
}
