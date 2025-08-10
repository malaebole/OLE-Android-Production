package ae.oleapp.player;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleManualListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityManualPlayersBinding;
import ae.oleapp.dialogs.OleAddPlayerDialogFragment;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleManualPlayersActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityManualPlayersBinding binding;
    private OleManualListAdapter adapter;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private final List<OlePlayerInfo> filterPlayerList = new ArrayList<>();
    private boolean isSearch = false;
    private String bookingId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityManualPlayersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.players);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleManualListAdapter(getContext(), playerList);
        adapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.noDataVu.setVisibility(View.GONE);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnAddPlayer.setOnClickListener(this);
        binding.btnAddGame.setOnClickListener(this);

        getPlayerListAPI(true);

        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String str = binding.searchVu.getQuery().toString();
                if (str.isEmpty()) {
                    isSearch = false;
                    adapter.setDatasource(playerList);
                }
                else {
                    isSearch = true;
                    filterPlayerList.clear();
                    for (OlePlayerInfo info : playerList) {
                        if (info.getName().toLowerCase().contains(str.toLowerCase())) {
                            filterPlayerList.add(info);
                        }
                    }
                    adapter.setDatasource(filterPlayerList);
                }
                return true;
            }
        });

    }

    OleManualListAdapter.OnItemClickListener clickListener = new OleManualListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            adapter.binderHelper.closeLayout(String.valueOf(pos));
            if (isSearch) {
                adapter.selectItem(filterPlayerList.get(pos));
            }
            else {
                adapter.selectItem(playerList.get(pos));
            }
        }

        @Override
        public void OnEditClick(View v, int pos) {
            adapter.binderHelper.closeLayout(String.valueOf(pos));
            addUpdatePlayerClicked(playerList.get(pos));
        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.delete))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                            adapter.binderHelper.closeLayout(String.valueOf(pos));
                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                if (isSearch) {
                                    deletePlayerAPI(true, filterPlayerList.get(pos).getId(), pos);
                                }
                                else {
                                    deletePlayerAPI(true, playerList.get(pos).getId(), pos);
                                }
                            }
                        }
                    }).show();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnAddPlayer) {
            addUpdatePlayerClicked(null);
        }
        else if (v == binding.btnAddGame) {
            addClicked();
        }
    }

    private void addUpdatePlayerClicked(OlePlayerInfo olePlayerInfo) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("AddPlayerDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleAddPlayerDialogFragment dialogFragment = new OleAddPlayerDialogFragment(olePlayerInfo);
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogCallback(new OleAddPlayerDialogFragment.AddPlayerDialogCallback() {
            @Override
            public void didAddPlayer(OlePlayerInfo olePlayerInfo) {
                playerList.add(olePlayerInfo);
                adapter.notifyDataSetChanged();
                binding.noDataVu.setVisibility(View.GONE);
            }
            @Override
            public void didUpdatePlayer(OlePlayerInfo olePlayerInfo) {
                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(i).getId().equalsIgnoreCase(olePlayerInfo.getId())) {
                        playerList.get(i).setName(olePlayerInfo.getName());
                        playerList.get(i).setPlayerPosition(olePlayerInfo.getPlayerPosition());
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        dialogFragment.show(fragmentTransaction, "AddPlayerDialogFragment");
    }

    private void addClicked() {
        List<OlePlayerInfo> list = adapter.selectedList;
        if (list.size() > 0) {
            String ids = "";
            for (OlePlayerInfo info : list) {
                if (ids.isEmpty()) {
                    ids = info.getId();
                }
                else {
                    ids = String.format("%s,%s", ids, info.getId());
                }
            }
            addPlayerInGameAPI(true, ids);
        }
    }

    private void getPlayerListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getManualPlayerList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            playerList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                playerList.add(gson.fromJson(arr.get(i).toString(), OlePlayerInfo.class));
                            }
                            adapter.notifyDataSetChanged();
                            if (playerList.size() > 0) {
                                binding.noDataVu.setVisibility(View.GONE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            binding.noDataVu.setVisibility(View.VISIBLE);
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
                Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
            }
        });
    }

    private void deletePlayerAPI(boolean isLoader, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.manageManualPlayerList(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerId, "", "", "remove");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (isSearch) {
                                filterPlayerList.remove(pos);
                            }
                            else {
                                playerList.remove(pos);
                            }
                            adapter.selectedList.clear();
                            adapter.notifyDataSetChanged();
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
                Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
            }
        });
    }

    private void addPlayerInGameAPI(boolean isLoader, String playerIds) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPlayerInGame(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerIds);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent();
                            intent.putExtra("add_to_game", true);
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
                Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
            }
        });
    }
}
