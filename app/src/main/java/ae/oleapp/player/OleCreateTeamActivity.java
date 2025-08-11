package ae.oleapp.player;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCreateTeamBinding;
import ae.oleapp.dialogs.OleColorDialogFragment;
import ae.oleapp.models.OleColorModel;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreateTeamActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreateTeamBinding binding;
    private String bookingId = "";
    private String teamAColor = "";
    private String teamBColor = "";
    private OleGameTeam oleGameTeam;
    private List<OleColorModel> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreateTeamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        colorList = Arrays.asList(
                new OleColorModel(getString(R.string.black), "#000000"),
                new OleColorModel(getString(R.string.gray), "#C4C4C4"),
                new OleColorModel(getString(R.string.blue), "#1E75C9"),
                new OleColorModel(getString(R.string.yellow), "#FFBA00"),
                new OleColorModel(getString(R.string.pink), "#FD6C9E"),
                new OleColorModel(getString(R.string.red), "#FE2717"),
                new OleColorModel(getString(R.string.purple), "#800080"),
                new OleColorModel(getString(R.string.aqua), "#0CFFEC"));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            String team = bundle.getString("team", "");
            if (!team.isEmpty()) {
                Gson gson = new Gson();
                oleGameTeam = gson.fromJson(team, OleGameTeam.class);
            }
        }

        if (oleGameTeam == null) {
            binding.bar.toolbarTitle.setText(R.string.create_team);
            binding.tvCreate.setText(R.string.create_team);
        }
        else {
            binding.bar.toolbarTitle.setText(R.string.update_team);
            binding.tvCreate.setText(R.string.update_team);
            populateData();
        }

        binding.bar.backBtn.setOnClickListener(this);
        binding.etColorA.setOnClickListener(this);
        binding.etColorB.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.etColorA) {
            teamAColorClicked();
        }
        else if (v == binding.etColorB) {
            teamBColorClicked();
        }
        else if (v == binding.btnCreate) {
            createClicked();
        }
    }

    private void teamAColorClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ColorDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleColorDialogFragment dialogFragment = new OleColorDialogFragment(colorList);
        dialogFragment.setDialogCallback(new OleColorDialogFragment.ColorDialogCallback() {
            @Override
            public void colorPicked(OleColorModel oleColorModel) {
                binding.etColorA.setText(oleColorModel.getName());
                teamAColor = oleColorModel.getColor();
                binding.vuColorA.setCardBackgroundColor(Color.parseColor(teamAColor));
            }
        });
        dialogFragment.show(fragmentTransaction, "ColorDialogFragment");
    }

    private void teamBColorClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ColorDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleColorDialogFragment dialogFragment = new OleColorDialogFragment(colorList);
        dialogFragment.setDialogCallback(new OleColorDialogFragment.ColorDialogCallback() {
            @Override
            public void colorPicked(OleColorModel oleColorModel) {
                binding.etColorB.setText(oleColorModel.getName());
                teamBColor = oleColorModel.getColor();
                binding.vuColorB.setCardBackgroundColor(Color.parseColor(teamBColor));
            }
        });
        dialogFragment.show(fragmentTransaction, "ColorDialogFragment");
    }

    private void populateData() {
        binding.etTeamA.setText(oleGameTeam.getTeamAName());
        binding.etTeamB.setText(oleGameTeam.getTeamBName());
        teamAColor = oleGameTeam.getTeamAColor();
        teamBColor = oleGameTeam.getTeamBColor();
        binding.vuColorA.setCardBackgroundColor(Color.parseColor(teamAColor));
        binding.vuColorB.setCardBackgroundColor(Color.parseColor(teamBColor));
        for (OleColorModel oleColorModel : colorList) {
            if (oleColorModel.getColor().equalsIgnoreCase(teamAColor)) {
                binding.etColorA.setText(oleColorModel.getName());
            }
            if (oleColorModel.getColor().equalsIgnoreCase(teamBColor)) {
                binding.etColorB.setText(oleColorModel.getName());
            }
        }
    }

    private void createClicked() {
        if (binding.etTeamA.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_team_name), FancyToast.ERROR);
            return;
        }
        if (teamAColor.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.pick_color), FancyToast.ERROR);
            return;
        }
        if (binding.etTeamB.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_team_name), FancyToast.ERROR);
            return;
        }
        if (teamBColor.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.pick_color), FancyToast.ERROR);
            return;
        }
        if (oleGameTeam != null) {
            updateTeamAPI(oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), binding.etTeamA.getText().toString(), binding.etTeamB.getText().toString());
        }
        else {
            createTeamAPI(binding.etTeamA.getText().toString(), binding.etTeamB.getText().toString());
        }
    }

    private void createTeamAPI(String teamAName, String teamBName) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.createTeam(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, teamAName, teamAColor, teamBName, teamBColor, "friendly_game");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Intent intent = new Intent();
                            intent.putExtra("team", obj.toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void updateTeamAPI(String teamAId, String teamBId, String teamAName, String teamBName) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateTeam(Functions.getAppLang(getContext()), teamAId, teamBId, teamAName, teamAColor, teamBName, teamBColor);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Intent intent = new Intent();
                            intent.putExtra("team", obj.toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}
