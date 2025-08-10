package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.MyGroupsAdapter;
import ae.oleapp.databinding.FragmentGroupsDialogBinding;
import ae.oleapp.models.GroupData;

public class GroupsDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentGroupsDialogBinding binding;
    private MyGroupsAdapter adapter;
    private List<GroupData> groupList = new ArrayList<>();
    private GroupsDialogFragmentCallback dialogCallback;

    public GroupsDialogFragment() {
        // Required empty public constructor
    }

    public GroupsDialogFragment(List<GroupData> groupList) {
        this.groupList = groupList;
    }

    public void setDialogCallback(GroupsDialogFragmentCallback dialogCallback) {
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
        binding = FragmentGroupsDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new MyGroupsAdapter(getContext(), groupList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            dismiss();
        }
    }

    MyGroupsAdapter.ItemClickListener itemClickListener = new MyGroupsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            dialogCallback.didSelectGroup(GroupsDialogFragment.this, groupList.get(pos));
        }
    };

    public interface GroupsDialogFragmentCallback {
        void didSelectGroup(DialogFragment dialogFragment,  GroupData groupData);
    }
}