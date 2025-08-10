package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import ae.oleapp.adapters.OleColorListAdapter;
import ae.oleapp.databinding.OlefragmentColorDialogBinding;
import ae.oleapp.models.OleColorModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleColorDialogFragment extends DialogFragment {

    private OlefragmentColorDialogBinding binding;
    private List<OleColorModel> colorList;
    private ColorDialogCallback dialogCallback;
    private OleColorListAdapter adapter;

    public OleColorDialogFragment() {
        // Required empty public constructor
    }

    public OleColorDialogFragment(List<OleColorModel> colorList) {
        this.colorList = colorList;
    }

    public void setDialogCallback(ColorDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentColorDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.recyclerVu.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new OleColorListAdapter(getContext(), colorList);
        adapter.setOnItemClickListener(new OleColorListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int pos) {
                dialogCallback.colorPicked(colorList.get(pos));
                dismiss();
            }
        });
        binding.recyclerVu.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface ColorDialogCallback {
        void colorPicked(OleColorModel oleColorModel);
    }
}
