package ae.oleapp.activities;

import androidx.appcompat.widget.SearchView;
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

import ae.oleapp.adapters.OleBlockedListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBlockedUsersBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.owner.OleBookingCountDetailActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBlockedUsersActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBlockedUsersBinding binding;
    private OleBlockedListAdapter adapter;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private final List<OlePlayerInfo> filterList = new ArrayList<>();
    private boolean isSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBlockedUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.blocked_users);

        binding.noDataVu.setVisibility(View.GONE);
        binding.btnUnblock.setAlpha(0.5f);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleBlockedListAdapter(getContext(), playerList, true);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

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
                    filterList.clear();
                    for (OlePlayerInfo info : playerList) {
                        if (info.getName().toLowerCase().contains(str) || info.getPhone().toLowerCase().contains(str)) {
                            filterList.add(info);
                        }
                    }
                    adapter.setDatasource(filterList);
                }
                return true;
            }
        });
        
        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnUnblock.setOnClickListener(this);
    }

    OleBlockedListAdapter.OnItemClickListener itemClickListener = new OleBlockedListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OlePlayerInfo info = null;
            if (isSearch) {
                info = filterList.get(pos);
            }
            else {
                info = playerList.get(pos);
            }
            adapter.selectItem(info);
            if (adapter.selectedList.isEmpty()) {
                binding.btnUnblock.setAlpha(0.5f);
            }
            else {
                binding.btnUnblock.setAlpha(1.0f);
            }
        }

        @Override
        public void OnImageClick(View v, int pos) {
            OlePlayerInfo info = null;
            if (isSearch) {
                info = filterList.get(pos);
            }
            else {
                info = playerList.get(pos);
            }
            Intent intent = new Intent(getContext(), OleBookingCountDetailActivity.class);
            intent.putExtra("player_id", info.getId());
            intent.putExtra("player_phone", info.getPhone());
            intent.putExtra("player_name", info.getName());
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getPlayerListAPI(playerList.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnUnblock) {
            unblockClicked();
        }
    }

    private void unblockClicked() {
        if (adapter.selectedList.isEmpty()) {
            return;
        }
        String ids = "";
        String phones = "";
        for (OlePlayerInfo data : adapter.selectedList) {
            if (ids.isEmpty()) {
                ids = data.getId();
                phones = data.getPhone();
            }
            else {
                ids = String.format("%s,%s", ids, data.getId());
                phones = String.format("%s,%s", phones, data.getPhone());
            }
        }
        blockUnblockUserAPI(true, "0", ids, phones);
    }

    private void getPlayerListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.blockedUser(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
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
                            adapter.setDatasource(playerList);
                            if (playerList.isEmpty()) {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.GONE);
                            }
                        }
                        else {
                            playerList.clear();
                            adapter.setDatasource(playerList);
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
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void blockUnblockUserAPI(boolean isLoader, String status, String userId, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.blockUnblockUser(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), userId, status, "", phone);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            adapter.selectedList.clear();
                            binding.btnUnblock.setAlpha(0.5f);
                            getPlayerListAPI(false);
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