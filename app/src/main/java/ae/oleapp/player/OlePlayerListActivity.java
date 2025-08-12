package ae.oleapp.player;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePlayerListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPlayerListBinding;
import ae.oleapp.fragments.OleHomeFragment;
import ae.oleapp.fragments.PlayerListFiltersFragemnt;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.LocationHelperFragment;
import ae.oleapp.util.OleEndlessRecyclerViewScrollListener;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPlayerListBinding binding;
    private boolean isForSelection = false;
    private boolean isSingleSelection = false;
    private boolean isThreeSelection = false;
    private boolean isSelectPartner = false;
    private LinearLayoutManager layoutManager;
    private int pageNo = 1;
    private Location location;
    private OleEndlessRecyclerViewScrollListener scrollListener;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private OlePlayerListAdapter adapter;
    PlayerListFiltersFragemnt filterFragment;

    // filter
    private String name = "";
    private String cityId = "";
    private String countryId = "";
    private String age = "";
    private final String highMatch = "";
    private String point = "";
    private String topPlayer = "";
    private String playedOverAll = "";
    private String playedMonth = "";
    private final int playersLoaded = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForSelection = bundle.getBoolean("is_selection", false);
            isSingleSelection = bundle.getBoolean("is_single_selection", false);
            if (bundle.containsKey("is_select_partner")) {
                isSelectPartner = bundle.getBoolean("is_select_partner");
            }
            if (bundle.containsKey("is_three_selection")) {
                isThreeSelection = bundle.getBoolean("is_three_selection");
            }
        }

        if (isForSelection) {
            binding.titleBar.toolbarTitle.setText(R.string.players);
            binding.tvInvite.setText(R.string.done);
        }
        else {
            binding.titleBar.toolbarTitle.setText(R.string.invite_friends);
        }

        if (isSelectPartner) {
            binding.titleBar.toolbarTitle.setText(R.string.select_your_partner);
        }

        binding.filterBg.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);


        scrollListener = new OleEndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                int newPlayersCount = totalItemsCount - playersLoaded;
//                playersLoaded = totalItemsCount;
                if (totalItemsCount >= 50) {
                    pageNo = page + 1;
                    getPlayerListAPI(true);
                }
            }
        };
        binding.recyclerVu.addOnScrollListener(scrollListener);
        adapter = new OlePlayerListAdapter(getContext(), playerList, true);
        adapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.searchVu.setVisibility(View.GONE);
        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    name = query;
                    pageNo = 1;
                    getPlayerListAPI(true);
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
                pageNo = 1;
                getPlayerListAPI(false);
                return true;
            }
        });

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColor));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                getLocationAndCallAPI();
            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnInvite.setOnClickListener(this);
        binding.filterBg.setOnClickListener(this);
        binding.relFilter.setOnClickListener(this);
        binding.relSearch.setOnClickListener(this);

        getLocationAndCallAPI();
    }

    private final OlePlayerListAdapter.OnItemClickListener clickListener = new OlePlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            adapter.selectItem(playerList.get(pos));
        }

        @Override
        public void OnImageClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", playerList.get(pos).getId());
            startActivity(intent);
        }
    };

//    private void getLocationAndCallAPI() {
//        new AirLocation(getContext(), true, false, new AirLocation.Callbacks() {
//            @Override
//            public void onSuccess(Location loc) {
//                // do something
//                location = loc;
//                getPlayerListAPI(playerList.isEmpty());
//            }
//
//            @Override
//            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
//                // do something
//                binding.pullRefresh.setRefreshing(false);
//                getPlayerListAPI(playerList.isEmpty());
//            }
//        });
//    }
    private void getLocationAndCallAPI() {
        LocationHelperFragment helper = LocationHelperFragment.getInstance(getSupportFragmentManager());
        helper.startLocationRequest(new LocationHelperFragment.LocationCallback() {
                @Override
                public void onLocationRetrieved(Location location) {
                    OlePlayerListActivity.this.location = location;
                    getPlayerListAPI(playerList.isEmpty());
                }

                @Override
                public void onLocationError(String message) {
                    binding.pullRefresh.setRefreshing(false);
                    getPlayerListAPI(playerList.isEmpty());
                }
            });
    }


    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnInvite) {
            inviteClicked();
        }
        else if (v == binding.filterBg) {
            filterBgClicked();
        }
        else if (v == binding.relFilter) {
            filtertClicked();
        }
        else if (v == binding.relSearch) {
            searchClicked();
        }
    }

    private void inviteClicked() {
        if (adapter.selectedList.size() == 0) {
            Functions.showToast(getContext(), getString(R.string.select_one_player), FancyToast.ERROR);
            return;
        }
        if (isSingleSelection && adapter.selectedList.size() > 1) {
            Functions.showToast(getContext(), getString(R.string.select_one_player_only), FancyToast.ERROR);
            return;
        }
        if (isThreeSelection && adapter.selectedList.size() > 3) {
            Functions.showToast(getContext(), getString(R.string.select_three_player), FancyToast.ERROR);
            return;
        }
        if (isForSelection) {
            Intent intent = new Intent();
            Gson gson = new Gson();
            intent.putExtra("players", gson.toJson(adapter.selectedList));
            setResult(RESULT_OK, intent);
            finish();
        }
        else {

        }
    }

    private void filterBgClicked() {
        removeFilterFrag();
    }

    private void removeFilterFrag() {
        getSupportFragmentManager().beginTransaction().remove(filterFragment).commit();
        filterFragment = null;
        binding.filterBg.setVisibility(View.GONE);
    }

    private void filtertClicked() {
        binding.searchVu.setVisibility(View.GONE);
        if (filterFragment != null && filterFragment.isVisible()) {
            removeFilterFrag();
        }
        else {
            binding.filterBg.setVisibility(View.VISIBLE);
            filterFragment = new PlayerListFiltersFragemnt(cityId, countryId, age, point, topPlayer, playedOverAll, playedMonth);
            filterFragment.setFiltersCallBack(callBack);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.filter_container, filterFragment).commit();
        }
    }

    PlayerListFiltersFragemnt.PlayerListFiltersCallBack  callBack = new PlayerListFiltersFragemnt.PlayerListFiltersCallBack() {
        @Override
        public void getFilters(String countryId, String cityId, String topPlayer, String point, String playedMonth, String playedOverAll, String age) {
            OlePlayerListActivity.this.countryId = countryId;
            OlePlayerListActivity.this.cityId = cityId;
            OlePlayerListActivity.this.topPlayer = topPlayer;
            OlePlayerListActivity.this.point = point;
            OlePlayerListActivity.this.playedMonth = playedMonth;
            OlePlayerListActivity.this.playedOverAll = playedOverAll;
            OlePlayerListActivity.this.age = age;
            getPlayerListAPI(true);

            removeFilterFrag();
        }
    };

    private void searchClicked() {
        if (binding.searchVu.getVisibility() == View.GONE) {
            binding.searchVu.setVisibility(View.VISIBLE);
            binding.searchVu.requestFocus();
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                    toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        else {
            binding.searchVu.setVisibility(View.GONE);
            name = "";
            pageNo = 1;
            getPlayerListAPI(true);
        }
    }

    private void getPlayerListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call;
        if (location == null) {
            call = AppManager.getInstance().apiInterface.getPlayerList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, pageNo, name, cityId, age, highMatch, point, topPlayer, playedMonth, playedOverAll, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        else {
            call = AppManager.getInstance().apiInterface.getPlayerList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), location.getLatitude(), location.getLongitude(), pageNo, name, cityId, age, highMatch, point, topPlayer, playedMonth, playedOverAll, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            if (pageNo == 1) {
                                playerList.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    playerList.add(gson.fromJson(arr.get(i).toString(), OlePlayerInfo.class));
                                }
                            }
                            else {
                                List<OlePlayerInfo> more = new ArrayList<>();
                                for (int i = 0; i < arr.length(); i++) {
                                    more.add(gson.fromJson(arr.get(i).toString(), OlePlayerInfo.class));
                                }

                                if (more.size() > 0) {
                                    playerList.addAll(more);
                                }
                                else {
                                    pageNo = pageNo-1;
                                }
                            }
                            if (playerList.size() == 0) {
                                Functions.showToast(getContext(), getString(R.string.player_not_found), FancyToast.ERROR);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            playerList.clear();
                            adapter.notifyDataSetChanged();
                            if (binding.searchVu.getVisibility() == View.GONE) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
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
                binding.pullRefresh.setRefreshing(false);
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
