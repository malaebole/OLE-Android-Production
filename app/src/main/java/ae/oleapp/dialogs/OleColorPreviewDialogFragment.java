package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFieldColorListAdapter;
import ae.oleapp.databinding.OlefragmentColorPreviewDialogBinding;
import ae.oleapp.models.OleColorModel;


public class OleColorPreviewDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentColorPreviewDialogBinding binding;
    private List<OleColorModel> colorList;
    private ColorPreviewDialogCallback dialogCallback;
    private OleFieldColorListAdapter colorListAdapter;
    private int selectedColorIndex = -1;

    public OleColorPreviewDialogFragment() {
        // Required empty public constructor
    }

    public OleColorPreviewDialogFragment(List<OleColorModel> colorList, int selectedColorIndex) {
        this.colorList = colorList;
        this.selectedColorIndex = selectedColorIndex;
    }

    public void setDialogCallback(ColorPreviewDialogCallback dialogCallback) {
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
        binding = OlefragmentColorPreviewDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        colorListAdapter = new OleFieldColorListAdapter(getContext(), colorList);
        colorListAdapter.setOnItemClickListener(colorClickListener);
        binding.recyclerVu.setAdapter(colorListAdapter);
        colorListAdapter.setSelectedIndex(selectedColorIndex);

        refreshView();

        binding.btnSave.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnSave) {
            if (selectedColorIndex != -1) {
                dialogCallback.colorPicked(selectedColorIndex);
                dismiss();
            }
        }
    }

    OleFieldColorListAdapter.OnItemClickListener colorClickListener = new OleFieldColorListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            colorListAdapter.setSelectedIndex(pos);
            selectedColorIndex = pos;
            refreshView();
        }
    };

    private void refreshView() {
        if (selectedColorIndex != -1) {
            binding.relMain.setStrokeColor(Color.parseColor(colorList.get(selectedColorIndex).getColor()));
            binding.cardvu.setCardBackgroundColor(Color.parseColor(colorList.get(selectedColorIndex).getColor()));
            GradientDrawable shapeDrawable = (GradientDrawable) binding.sizeVu.getBackground().getCurrent();
            shapeDrawable.setColor(Color.parseColor(colorList.get(selectedColorIndex).getColor()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public interface ColorPreviewDialogCallback {
        void colorPicked(int colorPos);
    }
}