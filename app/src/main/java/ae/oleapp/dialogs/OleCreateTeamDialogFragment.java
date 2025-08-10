package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFieldColorListAdapter;

import ae.oleapp.databinding.OlefragmentCreateTeamDialogBinding;
import ae.oleapp.models.OleColorModel;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreateTeamDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentCreateTeamDialogBinding binding;
    private List<OleColorModel> colorList;
    private OleFieldColorListAdapter teamAAdapter;
    private OleFieldColorListAdapter teamBAdapter;
    private String teamAColor = "";
    private String teamBColor = "";
    private OleGameTeam oleGameTeam;
    private String bookingId = "";
    private CreateTeamDialogCallback dialogCallback;

    public OleCreateTeamDialogFragment() {
        // Required empty public constructor
    }

    public OleCreateTeamDialogFragment(OleGameTeam oleGameTeam, String bookingId) {
        this.oleGameTeam = oleGameTeam;
        this.bookingId = bookingId;
    }

    public void setDialogCallback(CreateTeamDialogCallback dialogCallback) {
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
        binding = OlefragmentCreateTeamDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        colorList = Arrays.asList(
                new OleColorModel(getString(R.string.black), "#000000"),
                new OleColorModel(getString(R.string.gray), "#C4C4C4"),
                new OleColorModel(getString(R.string.blue), "#1E75C9"),
                new OleColorModel(getString(R.string.yellow), "#FFBA00"),
                new OleColorModel(getString(R.string.pink), "#FD6C9E"),
                new OleColorModel(getString(R.string.red), "#FE2717"),
                new OleColorModel(getString(R.string.purple), "#800080"),
                new OleColorModel(getString(R.string.aqua), "#0CFFEC"));

        LinearLayoutManager teamALayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.teamARecyclerVu.setLayoutManager(teamALayoutManager);
        teamAAdapter = new OleFieldColorListAdapter(getContext(), colorList);
        teamAAdapter.setOnItemClickListener(teamAClickListener);
        binding.teamARecyclerVu.setAdapter(teamAAdapter);

        LinearLayoutManager teamBLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.teamBRecyclerVu.setLayoutManager(teamBLayoutManager);
        teamBAdapter = new OleFieldColorListAdapter(getContext(), colorList);
        teamBAdapter.setOnItemClickListener(teamBClickListener);
        binding.teamBRecyclerVu.setAdapter(teamBAdapter);

        if (oleGameTeam == null) {
            binding.tvCreate.setText(R.string.create_team);
        }
        else {
            binding.tvCreate.setText(R.string.update_team);
            populateData();
        }

        binding.btnCreate.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnCreate) {
            createClicked();
        }
    }

    OleFieldColorListAdapter.OnItemClickListener teamAClickListener = new OleFieldColorListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            teamAAdapter.setSelectedIndex(pos);
            OleColorModel oleColorModel = colorList.get(pos);
            teamAColor = oleColorModel.getColor();
        }
    };

    OleFieldColorListAdapter.OnItemClickListener teamBClickListener = new OleFieldColorListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            teamBAdapter.setSelectedIndex(pos);
            OleColorModel oleColorModel = colorList.get(pos);
            teamBColor = oleColorModel.getColor();
        }
    };

    private void populateData() {
        binding.etTeamA.setText(oleGameTeam.getTeamAName());
        binding.etTeamB.setText(oleGameTeam.getTeamBName());
        teamAColor = oleGameTeam.getTeamAColor();
        teamBColor = oleGameTeam.getTeamBColor();
        for (int i = 0; i < colorList.size(); i++) {
            OleColorModel oleColorModel = colorList.get(i);
            if (oleColorModel.getColor().equalsIgnoreCase(teamAColor)) {
                teamAAdapter.setSelectedIndex(i);
            }
            if (oleColorModel.getColor().equalsIgnoreCase(teamBColor)) {
                teamBAdapter.setSelectedIndex(i);
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
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.createTeam(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, teamAName, teamAColor, teamBName, teamBColor, "normal");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            dialogCallback.teamCreated(OleCreateTeamDialogFragment.this, new Gson().fromJson(obj.toString(), OleGameTeam.class));
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
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            dialogCallback.teamCreated(OleCreateTeamDialogFragment.this, new Gson().fromJson(obj.toString(), OleGameTeam.class));
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

    public interface CreateTeamDialogCallback {
        void teamCreated(DialogFragment dialogFragment, OleGameTeam oleGameTeam);
    }
}