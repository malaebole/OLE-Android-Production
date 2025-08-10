package ae.oleapp.dialogs;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.activities.MakeCaptainActivity;
import ae.oleapp.databinding.FragmentChooseTeamDialogBinding;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.Functions;

public class ChooseTeamDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentChooseTeamDialogBinding binding;
    private PlayerInfo player;
    private GameTeam gameTeam;
    private FormationTeams teamA,teamB;
    private String selectedTeamId = "",gameId="",userId="";
    private ChooseTeamDialogFragmentCallback dialogFragmentCallback;

    public ChooseTeamDialogFragment() {
        // Required empty public constructor
    }

    public ChooseTeamDialogFragment(PlayerInfo player, String gameId, String userId, GameTeam gameTeam, FormationTeams teamA, FormationTeams teamB) {
        this.player = player;
        this.gameTeam = gameTeam;
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameId = gameId;
        this.userId = userId;
    }

    public void setDialogFragmentCallback(ChooseTeamDialogFragmentCallback dialogFragmentCallback) {
        this.dialogFragmentCallback = dialogFragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChooseTeamDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setDimAmount(0.5f);
        }

        binding.tvTeamA.setText(teamA.getTeamName()); // Check Sometimes Null
        binding.tvTeamB.setText(teamB.getTeamName());
        binding.teamAVu.setStrokeColor(getResources().getColor(R.color.transparent));
        binding.teamBVu.setStrokeColor(getResources().getColor(R.color.transparent));

        binding.btnClose.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.teamAVu.setOnClickListener(this);
        binding.teamBVu.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            dismiss();
        }
        else if (view == binding.btnContinue) {
            if (selectedTeamId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_team), FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent(getContext(), MakeCaptainActivity.class);
            intent.putExtra("game", new Gson().toJson(gameTeam));
            intent.putExtra("player", new Gson().toJson(player));
            intent.putExtra("team_id", selectedTeamId);
            intent.putExtra("game_id", gameId);
            intent.putExtra("user_id", userId);
            intent.putExtra("team_a", new Gson().toJson(teamA));
            intent.putExtra("team_b", new Gson().toJson(teamB));
            captainResultLauncher.launch(intent);

        }
        else if (view == binding.teamAVu) {
            selectedTeamId = teamA.getId();
            binding.teamAVu.setStrokeColor(getResources().getColor(R.color.yellowColor));
            binding.teamBVu.setStrokeColor(getResources().getColor(R.color.transparent));
        }
        else if (view == binding.teamBVu) {
            selectedTeamId = teamB.getId();
            binding.teamAVu.setStrokeColor(getResources().getColor(R.color.transparent));
            binding.teamBVu.setStrokeColor(getResources().getColor(R.color.yellowColor));
        }
    }

    ActivityResultLauncher<Intent> captainResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                boolean isAdded = result.getData().getExtras().getBoolean("is_added");
                String teamACaptainName = result.getData().getExtras().getString("teamA_captain_name");
                String teamBCaptainName = result.getData().getExtras().getString("teamB_captain_name");

                if (isAdded) {
                    dialogFragmentCallback.captainAdded(ChooseTeamDialogFragment.this, teamACaptainName, teamBCaptainName);
                }
                dismiss();
            }
        }
    });

    public interface ChooseTeamDialogFragmentCallback {
        void captainAdded(DialogFragment df, String teamACaptainName, String teamBCaptainName);
    }
}