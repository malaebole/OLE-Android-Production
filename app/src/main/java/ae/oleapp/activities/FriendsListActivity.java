package ae.oleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.PlayerGridAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityFriendsListBinding;
import ae.oleapp.models.GameHistory;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.socket.SocketManager;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFriendsListBinding binding;
    private String name = "", gameId = "", type = "",  date="",time="",stadiumName ="", teamACaptainName="", teamBCaptainName="",
            cityName="", FriendId = "", captainAId="", captainBId="", teamAid = "", teamBid = "";
    private int playersLimit = 0;
    private boolean isForSubstitute = false, isTeam = false;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private final List<PlayerInfo> captainsList = new ArrayList<>();
    private final List<PlayerInfo> filteredPlayerList = new ArrayList<>();
    private PlayerGridAdapter adapter;
    private SocketManager socketManager;
    private  Socket socket;
    private JSONArray selectedPlayersArray;
    private Boolean vsMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        socketManager = SocketManager.getInstance();
        socket = socketManager.getSocket();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForSubstitute = bundle.getBoolean("substitute", false);
            gameId = bundle.getString("game_id", "");
            type = bundle.getString("type", "");
            isTeam = bundle.getBoolean("is_team", false);
            playersLimit = Integer.parseInt(bundle.getString("players", ""));
            date = bundle.getString("game_date", "");
            time = bundle.getString("game_time", "");
            //players = bundle.getString("game_capacity", "");
            stadiumName = bundle.getString("club_name", "");
            cityName = bundle.getString("city_name", "");
            FriendId = bundle.getString("friend_id", "");
            vsMode = bundle.getBoolean("vs_mode");
            captainAId = bundle.getString("captain_a_id");
            teamAid = bundle.getString("team_a_id");
            captainBId = bundle.getString("captain_b_id");
            teamBid = bundle.getString("team_b_id");
            teamACaptainName = bundle.getString("teamA_captain_name");
            teamBCaptainName = bundle.getString("teamB_captain_name");

        }

        if (isForSubstitute) {
            binding.tvTitle.setText(R.string.replace_player);
            binding.tvDesc.setText(R.string.select_player_for_substitute);
        }
        else {
            if (type.equalsIgnoreCase("remove_player")) {
                binding.tvTitle.setText(R.string.remove_from_game);
                binding.tvDesc.setText(R.string.select_players_remove_this_game);
            }
            else {
                binding.tvTitle.setText(R.string.add_to_game);
                binding.tvDesc.setText(R.string.select_players_play_this_game);
            }
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(gridLayoutManager);
        adapter = new PlayerGridAdapter(getContext(), playerList, false, playersLimit, vsMode);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);


        getFriends(true);

        binding.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    name = query;
                    //filterPlayers(name);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.text_should_3_charachters), FancyToast.ERROR);
                }
                binding.searchVu.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                name = binding.searchVu.getQuery().toString();
                filterPlayers(name);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnAdd) {
            if (vsMode){
                sendVsModePlayers();
            }else{
                normalMode();
            }


        }
    }

    private void sendVsModePlayers() {
        List<PlayerInfo> list = adapter.getSelectedList();
        if (isForSubstitute) {
            if (list.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_player), FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("player", new Gson().toJson(list.get(0)));
            if (isTeam) {
                setResult(123, intent);
            } else {
                setResult(RESULT_OK, intent);
            }
            finish();
        }
        else {
            if (type.equalsIgnoreCase("remove_player")) {
                if (list.size() != playersLimit) {
                    Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
                    return;
                }
                String ids = "";
                for (PlayerInfo info : list) {
                    if (ids.isEmpty()) {
                        ids = info.getId();
                    } else {
                        ids = String.format("%s,%s", ids, info.getId());
                    }
                }
                removeFromGameAPI(true, ids);
            }
            else if (type.equalsIgnoreCase("add_player")) {

                if (list.size() % 2 == 1) {
                    Functions.showToast(getContext(), "Please select even number of players, like 6, 8 or 10.", FancyToast.ERROR);
                    return;
                }
                playersLimit = list.size()+2;

//                if (list.size() != playersLimit) {
//                    Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
//                    return;
//                }
                selectedPlayersArray = new JSONArray(); // Create a JSON array to hold selected players
                // Add captains from captainsList
                for (PlayerInfo captain : captainsList) {
                    JSONObject captainObject = new JSONObject(); // Create a JSON object for each captain
                    try {
                        captainObject.put("friend_id", captain.getId());
                        captainObject.put("friendship_id", captain.getFriendShipId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    selectedPlayersArray.put(captainObject); // Add the captain object to the JSON array
                }

                for (PlayerInfo info : list) {
                    JSONObject playerObject = new JSONObject(); // Create a JSON object for each player
                    try {
                        playerObject.put("friend_id", info.getId());
                        playerObject.put("friendship_id", info.getFriendShipId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    selectedPlayersArray.put(playerObject); // Add the player object to the JSON array
                }
                addToVsMode(selectedPlayersArray);
            } else {

                if (list.size() % 2 == 1) {
                    Functions.showToast(getContext(), "Please select even number of players, like 6, 8 or 10.", FancyToast.ERROR);
                    return;
                }

                playersLimit = list.size()+2;


//                if (list.size() != playersLimit) {
//                    Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
//                    return;
//                }
                selectedPlayersArray = new JSONArray(); // Create a JSON array to hold selected players
                // Add captains from captainsList
                for (PlayerInfo captain : captainsList) {
                    JSONObject captainObject = new JSONObject(); // Create a JSON object for each captain
                    try {
                        captainObject.put("friend_id", captain.getId());
                        captainObject.put("friendship_id", captain.getFriendShipId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    selectedPlayersArray.put(captainObject); // Add the captain object to the JSON array
                }

                for (PlayerInfo info : list) {
                    JSONObject playerObject = new JSONObject(); // Create a JSON object for each player
                    try {
                        playerObject.put("friend_id", info.getId());
                        playerObject.put("friendship_id", info.getFriendShipId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    selectedPlayersArray.put(playerObject); // Add the player object to the JSON array
                }
                addToVsMode(selectedPlayersArray);
            }
        }
    }

    private void normalMode(){

        List<PlayerInfo> list = adapter.getSelectedList();
        if (isForSubstitute) {
            if (list.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_player), FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("player", new Gson().toJson(list.get(0)));
            if (isTeam) {
                setResult(123, intent);
            } else {
                setResult(RESULT_OK, intent);
            }
            finish();
        } else {
            if (type.equalsIgnoreCase("remove_player")) {
                if (list.size() != playersLimit) {
                    Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
                    return;
                }
                String ids = "";
                for (PlayerInfo info : list) {
                    if (ids.isEmpty()) {
                        ids = info.getId();
                    } else {
                        ids = String.format("%s,%s", ids, info.getId());
                    }
                }
                removeFromGameAPI(true, ids);
            } else if (type.equalsIgnoreCase("add_player")) {
                if (list.size() != playersLimit) {
                    Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
                    return;
                }
                selectedPlayersArray = new JSONArray(); // Create a JSON array to hold selected players

                for (PlayerInfo info : list) {
                    JSONObject playerObject = new JSONObject(); // Create a JSON object for each player
                    try {
                        playerObject.put("friend_id", info.getId());
                        playerObject.put("friendship_id", info.getFriendShipId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    selectedPlayersArray.put(playerObject); // Add the player object to the JSON array
                }

////                    if (list.size() != playersLimit) {
////                        Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
////                        return;
////                    }
////                    String ids = "";
////                    String friendShipIds = "";
////
////                    for (PlayerInfo info : list) {
////                        if (ids.isEmpty() && friendShipIds.isEmpty()) {
////                            ids = info.getId();
////                            friendShipIds = info.getFriendShipId();
////
////                        } else {
////                            ids = String.format("%s,%s", ids, info.getId());
////                            friendShipIds = String.format("%s,%s", friendShipIds, info.getFriendShipId());
////                        }
////                    }
                addToGameAPI(selectedPlayersArray);
            } else {
                if (list.size() != playersLimit) {
                    Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
                    return;
                }
                selectedPlayersArray = new JSONArray(); // Create a JSON array to hold selected players
                for (PlayerInfo info : list) {
                    JSONObject playerObject = new JSONObject(); // Create a JSON object for each player
                    try {
                        playerObject.put("friend_id", info.getId());
                        playerObject.put("friendship_id", info.getFriendShipId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    selectedPlayersArray.put(playerObject); // Add the player object to the JSON array
                }
                addToGameAPI(selectedPlayersArray);
            }
        }

    }
    PlayerGridAdapter.ItemClickListener itemClickListener = new PlayerGridAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.selectPos(pos);
        }
    };

    private void addToVsMode(JSONArray selectedPlayers) {
        if (playersLimit < 8){
            Functions.showToast(getContext(), getString(R.string.select_player_place, playersLimit), FancyToast.ERROR);
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("game_id", gameId);
            data.put("selected_players", selectedPlayers);
            socket.emit("game:switch-lineup", data);
            Functions.showToast(getContext(), "Success", FancyToast.SUCCESS);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject captainsData = new JSONObject();
        try {
            captainsData.put("game_id", gameId);
            captainsData.put("selection_type", "captainsCreated");
            socket.emit("game:temp-captain-broadcast", captainsData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.putExtra("is_added", true);
        intent.putExtra("game_date", date);
        intent.putExtra("game_time", time);
        intent.putExtra("game_capacity", playersLimit);
        intent.putExtra("club_name", stadiumName);
        intent.putExtra("city_name", cityName);
        intent.putExtra("vs_mode", true);
        intent.putExtra("teamA_captain_name", teamACaptainName);
        intent.putExtra("teamB_captain_name", teamBCaptainName);
        setResult(RESULT_OK, intent);
        finish();

    }
    private void addToGameAPI(JSONArray selectedPlayers) {
        JSONObject data = new JSONObject();
        try {
            data.put("game_date", date);
            data.put("game_time", time);
            data.put("game_capacity", playersLimit);
            data.put("club_name", stadiumName);
            data.put("city_name", cityName);
            data.put("selected_players", selectedPlayers);
            socket.emit("game:normal-lineup", data);
            Functions.showToast(getContext(), "Success", FancyToast.SUCCESS);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.putExtra("is_added", true);
        intent.putExtra("game_date", date);
        intent.putExtra("game_time", time);
        intent.putExtra("game_players", playersLimit);
        intent.putExtra("club_name", stadiumName);
        intent.putExtra("city_name", cityName);
        setResult(RESULT_OK, intent);
        finish();

//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addToGame(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), ids,friendShipIds, gameId);
//        call.enqueue(new Callback<>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            Intent intent = new Intent();
//                            intent.putExtra("is_added", true);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                            //notifyGame(ids);
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
    }
    private void removeFromGameAPI(boolean isLoader, String ids) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeFromGame(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), ids, gameId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent();
                            intent.putExtra("is_added", true);
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
    private void getFriends(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getFriendsNodeApi(Functions.getPrefValue(getContext(), Constants.kUserID));
        // Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getFriendsNodeApi(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), name);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            playerList.clear();
                            captainsList.clear();
                            Gson gson = new Gson();
                            if (vsMode){
                                for (int i = 0; i < array.length(); i++) {
                                    PlayerInfo info = gson.fromJson(array.get(i).toString(), PlayerInfo.class);
                                    if (!info.getId().equalsIgnoreCase(captainAId) && !info.getId().equalsIgnoreCase(captainBId)) {
                                        playerList.add(info);
                                    }else{
                                        captainsList.add(info);
                                    }
                                }
                            }else{
                                for (int i = 0; i < array.length(); i++) {
                                    PlayerInfo info = gson.fromJson(array.get(i).toString(), PlayerInfo.class);
                                    if (isForSubstitute) {
                                        // remove player who's in game
                                        if (!info.getInGame().equalsIgnoreCase("1")) {
                                            playerList.add(info);
                                        }
                                    }
                                    else {
                                        if (type.equalsIgnoreCase("add_player")) {
                                            // only show player who's not in game
                                            if (!info.getInGame().equalsIgnoreCase("1")) {
                                                playerList.add(info);
                                            }
                                        }
                                        else if (type.equalsIgnoreCase("remove_player")) {
                                            // only show player who's in game
                                            if (info.getInGame().equalsIgnoreCase("1")) {
                                                playerList.add(info);
                                            }
                                        }
                                        else {
                                            playerList.add(info);
                                        }
                                    }
                                }
                            }


                            adapter.notifyDataSetChanged();
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
    private void filterPlayers(String query) {
        List<PlayerInfo> filteredList = new ArrayList<>();

        if (query.equalsIgnoreCase("")) {
            getFriends(false);
        } else {
            for (PlayerInfo player : playerList) {
                if (player.getNickName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(player);
                }
            }
        }

        adapter.updateData(filteredList);
    }





}