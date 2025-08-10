package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleHiddenFieldTypeAdapter;

import ae.oleapp.databinding.OlefragmentHideFieldDialogBinding;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.Functions;

public class OleHideFieldDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentHideFieldDialogBinding binding;
    private String type = "";
    private HideFieldDialogCallback dialogCallback;
    private List<OleKeyValuePair> selectionList;
    private OleHiddenFieldTypeAdapter adapter;

    public OleHideFieldDialogFragment() {

    }

    public void setDialogCallback(HideFieldDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentHideFieldDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        selectionList = Arrays.asList(new OleKeyValuePair("maintenance", getString(R.string.maintenance)),
                new OleKeyValuePair("tournament", getString(R.string.tournament)),
                new OleKeyValuePair("holiday", getString(R.string.holidays)),
                new OleKeyValuePair("other", getString(R.string.other)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleHiddenFieldTypeAdapter(getContext(), selectionList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.relPrice.setVisibility(View.GONE);
        binding.btnConfirm.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    OleHiddenFieldTypeAdapter.OnItemClickListener itemClickListener = new OleHiddenFieldTypeAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            type = selectionList.get(pos).getKey();
            adapter.setSelectedType(type);
            if (type.equalsIgnoreCase("tournament")) {
                binding.relPrice.setVisibility(View.VISIBLE);
            }
            else {
                binding.relPrice.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnConfirm) {
            confirmClicked();
        }
    }

    private void confirmClicked() {
        if (type.equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.select_type), FancyToast.ERROR);
            return;
        }
        if (type.equalsIgnoreCase("tournament") && binding.etPrice.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
            return;
        }
        if (binding.etReason.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.enter_reason), FancyToast.ERROR);
            return;
        }
        dialogCallback.didConfirm(type, binding.etPrice.getText().toString(), binding.etReason.getText().toString());
        dismiss();
    }

    public interface HideFieldDialogCallback {
        void didConfirm(String type, String price, String reason);
    }
}
