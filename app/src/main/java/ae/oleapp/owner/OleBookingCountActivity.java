package ae.oleapp.owner;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import ae.oleapp.adapters.OleBookingCountAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityBookingCountBinding;
import ae.oleapp.fragments.OlePlayerSearchFilterFragment;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleEndlessRecyclerViewScrollListener;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingCountActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingCountBinding binding;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private OleBookingCountAdapter adapter;
    private String from = "", to = "", days = "", filter = "", name = "", clubId = "";
    private int pageNo = 1;
    private LinearLayoutManager layoutManager;
    private OleRankClubAdapter oleRankClubAdapter;
    private OlePlayerSearchFilterFragment filterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingCountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.player_search);

        if (AppManager.getInstance().clubs.size() > 0) {
            clubId = AppManager.getInstance().clubs.get(0).getId();
        }
        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        binding.recyclerVu.addOnScrollListener(new OleEndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 50) {
                    pageNo = page + 1;
                    getPlayerListAPI(false);
                }
            }
        });
        adapter = new OleBookingCountAdapter(getContext(), playerList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

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

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                from = "";
                to = "";
                getPlayerListAPI(false);
            }
        });
        binding.titleBar.backBtn.setOnClickListener(this);
        binding.relFilter.setOnClickListener(this);
        binding.filterBg.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlayerListAPI(playerList.isEmpty());
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            pageNo = 1;
            getPlayerListAPI(true);
        }
    };

    OleBookingCountAdapter.OnItemClickListener itemClickListener = new OleBookingCountAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OlePlayerInfo info = playerList.get(pos);
            Intent bookingIntent = new Intent(getContext(), OleBookingCountDetailActivity.class);
            bookingIntent.putExtra("player_id", info.getId());
            bookingIntent.putExtra("player_phone", info.getPhone());
            bookingIntent.putExtra("player_name", info.getName());
            bookingIntent.putExtra("club_id", clubId);
            startActivity(bookingIntent);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.relFilter) {
            filterClicked();
        }
        else if (v == binding.filterBg) {
            filterBgClicked();
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

    private void filterClicked() {
        if (filterFragment != null && filterFragment.isVisible()) {
            removeFilterFrag();
        }
        else {
            binding.filterBg.setVisibility(View.VISIBLE);
            filterFragment = new OlePlayerSearchFilterFragment(from, to, days, filter);
            filterFragment.setFragmentCallback(fragmentCallback);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.filter_container, filterFragment).commit();
        }
    }

    OlePlayerSearchFilterFragment.PlayerSearchFilterFragmentCallback fragmentCallback = new OlePlayerSearchFilterFragment.PlayerSearchFilterFragmentCallback() {
        @Override
        public void getFilters(String from, String to, String days, String filter) {
            removeFilterFrag();
            OleBookingCountActivity.this.from = from;
            OleBookingCountActivity.this.to = to;
            OleBookingCountActivity.this.days = days;
            OleBookingCountActivity.this.filter = filter;
            adapter.setFilter(filter); // for show balance in list
            pageNo = 1;
            getPlayerListAPI(true);
        }
    };

    private void getPlayerListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getBookingsCount(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), from, to, filter, days, name, clubId, pageNo);
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
                        }
                        else {
                            if (pageNo == 1) {
                                playerList.clear();
                            }
                        }
                        adapter.notifyDataSetChanged();
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