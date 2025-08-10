package ae.oleapp.player;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import ae.oleapp.R;
import ae.oleapp.adapters.OleProfileLevelDetailAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityProfileMissionDetailsBinding;
import ae.oleapp.models.OleLevelsTarget;
import ae.oleapp.models.OleProfileMission;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleProfileMissionDetailsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityProfileMissionDetailsBinding binding;
    private String module = "";
    private String playerId = "";
    private OleProfileMission oleProfileMission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityProfileMissionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            module = bundle.getString("module", "");
            playerId = bundle.getString("player_id", "");
            oleProfileMission = new Gson().fromJson(bundle.getString("mission", ""), OleProfileMission.class);
        }

        if (module.equalsIgnoreCase(Constants.kPadelModule)) {
            binding.headerImgVu.setImageResource(R.drawable.padel_profile_header);
            binding.btnImg.setVisibility(View.INVISIBLE);
            binding.btnCollect.setCardBackgroundColor(getResources().getColor(R.color.yellowColor));
            binding.tvCollect.setTextColor(Color.parseColor("#845700"));
        }
        else {
            binding.headerImgVu.setImageResource(R.drawable.profile_header);
            binding.btnImg.setVisibility(View.VISIBLE);
            binding.btnCollect.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tvCollect.setTextColor(Color.WHITE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        populateData();

        binding.backBtn.setOnClickListener(this);
        binding.btnCollect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backBtn) {
            finish();
        }
        else if (view == binding.btnCollect) {
            Functions.showAlert(getContext(), getString(R.string.rewards), oleProfileMission.getCollectionMsg(), null);
        }
    }

    private void populateData() {
        binding.toolbarTitle.setText(oleProfileMission.getTitle());
        binding.tvRewardsName.setText(oleProfileMission.getRewardName());
        binding.tvRewardsDesc.setText(oleProfileMission.getRewardDesc());
        Glide.with(getContext()).load(oleProfileMission.getRewardPhoto()).into(binding.rewardsImgVu);

        boolean isCompleted = true;
        for (OleLevelsTarget target : oleProfileMission.getTargets()) {
            if (target.getRemaining() != 0) {
                isCompleted = false;
                break;
            }
        }

        if (isCompleted && playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (oleProfileMission.getRewardCollected().equalsIgnoreCase("1")) {
                binding.btnCollect.setVisibility(View.GONE);
            }
            else {
                binding.btnCollect.setVisibility(View.VISIBLE);
            }
        }
        else {
            binding.btnCollect.setVisibility(View.GONE);
        }

        OleProfileLevelDetailAdapter adapter = new OleProfileLevelDetailAdapter(getContext(), oleProfileMission.getTargets(), module);
        binding.recyclerVu.setAdapter(adapter);
    }
}