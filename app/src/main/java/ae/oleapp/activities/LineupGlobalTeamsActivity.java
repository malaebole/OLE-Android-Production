package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.AssignedCountryAdapter;
import ae.oleapp.adapters.GlobalLineupPlayersAdapter;
import ae.oleapp.adapters.ShirtsTeamAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityLineupGlobalBinding;
import ae.oleapp.databinding.ActivityLineupGlobalTeamsBinding;
import ae.oleapp.models.AssignedCountries;
import ae.oleapp.models.Country;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.LineupGlobalPlayers;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.Team;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LineupGlobalTeamsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLineupGlobalTeamsBinding binding;
    private final List<LineupGlobalPlayers> lineupGlobalPlayersList = new ArrayList<>();
    private final List<Shirt> shirtList = new ArrayList<>();
    private ShirtsTeamAdapter teamAdapter;
    private GlobalLineupPlayersAdapter globalLineupPlayersAdapter;
    private String selectedCountryId = "";
    private String selectedCountryName="";
    private String selectedTeamId = "";
    private String playerId="";
    private final List<Team> teamList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLineupGlobalTeamsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedCountryId = bundle.getString("country_id", "");
            selectedCountryName = bundle.getString("country_name", "");
        }
        binding.toolbarTitle.setText(selectedCountryName);
        getTeamAndShirtDetails(true);

        GridLayoutManager playerLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        binding.playerRecyclerVu.setLayoutManager(playerLayoutManager);
        globalLineupPlayersAdapter = new GlobalLineupPlayersAdapter(getContext(), lineupGlobalPlayersList);
        globalLineupPlayersAdapter.setItemClickListener(itemClickListener);
        binding.playerRecyclerVu.setAdapter(globalLineupPlayersAdapter);


        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.teamRecyclerVu.setLayoutManager(teamLayoutManager);
        teamAdapter = new ShirtsTeamAdapter(getContext(), teamList);
        teamAdapter.setItemClickListener(teamClickListener);
        binding.teamRecyclerVu.setAdapter(teamAdapter);



        binding.backBtn.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        } else if (v == binding.btnContinue) {
            addNewPlayer();
        }

    }


    GlobalLineupPlayersAdapter.ItemClickListener itemClickListener = new GlobalLineupPlayersAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            LineupGlobalPlayers lineupGlobalPlayer = lineupGlobalPlayersList.get(pos);
            playerId = lineupGlobalPlayer.getId();
            updatePlayer(lineupGlobalPlayer);

        }
    };

    ShirtsTeamAdapter.ItemClickListener teamClickListener = new ShirtsTeamAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedTeamId = teamList.get(pos).getId();
            teamAdapter.setSelectedId(selectedTeamId);
            shirtList.clear();
            shirtList.addAll(teamList.get(pos).getShirts());
            lineupGlobalTeamPlayers(false, selectedTeamId);

        }
    };
    private void addNewPlayer() {
        if (selectedTeamId.isEmpty()){
            selectedTeamId = teamList.get(0).getId();
        }
        Intent intent = new Intent(getContext(), AddLineupGlobalPlayerActivity.class);
        intent.putExtra("is_update",false);
        intent.putExtra("country_id",selectedCountryId);
        intent.putExtra("team_id",selectedTeamId);
        intent.putExtra("shirts", new Gson().toJson(shirtList));
        startActivity(intent);

    }
    private void updatePlayer(LineupGlobalPlayers lineupGlobalPlayer){
        if (selectedTeamId.isEmpty()){
            selectedTeamId = teamList.get(0).getId();
        }
        Intent intent = new Intent(getContext(), AddLineupGlobalPlayerActivity.class);
        intent.putExtra("is_update",true);
        intent.putExtra("country_id",selectedCountryId);
        intent.putExtra("team_id", selectedTeamId);
        intent.putExtra("player_id", playerId);
        intent.putExtra("player", new Gson().toJson(lineupGlobalPlayer));
        intent.putExtra("shirts", new Gson().toJson(shirtList));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!selectedTeamId.isEmpty()){
            lineupGlobalTeamPlayers(false, selectedTeamId);
        }

    }

    private void getTeamAndShirtDetails(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getTeamAndShirtDetails(selectedCountryId,"android");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray dataArray = object.getJSONArray(Constants.kData);
                            teamList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject teamObject = dataArray.getJSONObject(i);
                                Team team = gson.fromJson(teamObject.toString(), Team.class);
                                teamList.add(team);
                            }
                            teamAdapter.setSelectedId(teamList.get(0).getId());
                            shirtList.addAll(teamList.get(0).getShirts());
                            lineupGlobalTeamPlayers(false, teamList.get(0).getId());
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
    private void lineupGlobalTeamPlayers(boolean isLoader, String teamId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.lineupGlobalTeamPlayers(teamId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray globalPlayers = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            lineupGlobalPlayersList.clear();
                            for (int i = 0; i < globalPlayers.length(); i++) {
                                lineupGlobalPlayersList.add(gson.fromJson(globalPlayers.get(i).toString(), LineupGlobalPlayers.class));
                            }
                            globalLineupPlayersAdapter.notifyDataSetChanged();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

}