package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import ae.oleapp.R;
import ae.oleapp.adapters.OleLevelAttributeAdapter;
import ae.oleapp.databinding.OlefragmentMissionCompleteDialogBinding;
import ae.oleapp.models.OleProfileMission;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleMissionCompleteDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentMissionCompleteDialogBinding binding;
    private final OleProfileMission oleProfileMission;
    private String module = "";

    public OleMissionCompleteDialogFragment(OleProfileMission oleProfileMission, String module) {
        // Required empty public constructor
        this.module = module;
        this.oleProfileMission = oleProfileMission;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentMissionCompleteDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (module.equalsIgnoreCase(Constants.kPadelModule)) {
            binding.rel.setBackgroundColor(Color.parseColor("#26C167"));
            binding.bgImgVu.setImageResource(R.drawable.padel_reward_popup_bg);
            binding.btnImg.setVisibility(View.INVISIBLE);
            binding.btnCollect.setCardBackgroundColor(getResources().getColor(R.color.yellowColor));
            binding.tvCollect.setTextColor(Color.parseColor("#845700"));
        }
        else {
            binding.rel.setBackgroundColor(Color.parseColor("#0084FF"));
            binding.bgImgVu.setImageResource(R.drawable.reward_popup_bg);
            binding.btnImg.setVisibility(View.VISIBLE);
            binding.btnCollect.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tvCollect.setTextColor(Color.WHITE);
        }

        binding.tvRewardsName.setText(oleProfileMission.getRewardName());
        binding.tvRewardsDesc.setText(oleProfileMission.getRewardDesc());
        Glide.with(getContext()).load(oleProfileMission.getRewardPhoto()).into(binding.rewardsImgVu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.missionRecyclerVu.setLayoutManager(layoutManager);
        OleLevelAttributeAdapter adapter = new OleLevelAttributeAdapter(getContext(), oleProfileMission.getTargets(), module, false);
        binding.missionRecyclerVu.setAdapter(adapter);

        binding.btnCollect.setOnClickListener(this);
        binding.btnDismiss.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnCollect) {
            Functions.showAlert(getContext(), getString(R.string.rewards), oleProfileMission.getCollectionMsg(), null);
        }
        else if (v == binding.btnDismiss) {
            dismiss();
        }
    }
}