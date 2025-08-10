package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubListDialogAdapter;

import ae.oleapp.databinding.OlefragmentClubListDialogBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleClubListDialogFragment extends DialogFragment {

    private OlefragmentClubListDialogBinding binding;
    private List<Club> clubList;
    private List<Field> fieldList;
    private boolean isField;
    private ClubListDialogFragmentCallback fragmentCallback;

    public OleClubListDialogFragment() {
        // Required empty public constructor
    }

    public OleClubListDialogFragment(List<Club> clubList, List<Field> fieldList, boolean isField) {
        this.clubList = clubList;
        this.fieldList = fieldList;
        this.isField = isField;
    }

    public void setFragmentCallback(ClubListDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentClubListDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (isField) {
            binding.tvTitle.setText(getString(R.string.select_field));
        }
        else {
            binding.tvTitle.setText(getString(R.string.select_club));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        OleClubListDialogAdapter adapter = new OleClubListDialogAdapter(getContext(), clubList, fieldList, isField);
        adapter.setItemClickListener(new OleClubListDialogAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int pos) {
                if (isField) {
                    fragmentCallback.didSelectField(fieldList.get(pos));
                }
                else {
                    fragmentCallback.didSelectClub(clubList.get(pos));
                }
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

    public interface ClubListDialogFragmentCallback {
        void didSelectClub(Club club);
        void didSelectField(Field field);
    }
}
