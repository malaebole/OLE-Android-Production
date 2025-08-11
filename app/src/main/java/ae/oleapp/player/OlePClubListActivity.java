package ae.oleapp.player;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.location.Location;
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
import ae.oleapp.adapters.OlePlayerClubListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPClubListBinding;
import ae.oleapp.dialogs.OleClubRateDialog;
import ae.oleapp.fragments.OleClubFilterFragment;
import ae.oleapp.models.Club;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleEndlessRecyclerViewScrollListener;
import ae.oleapp.util.Functions;
import mumayank.com.airlocationlibrary.AirLocation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePClubListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPClubListBinding binding;
    private OlePlayerClubListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int pageNo = 1;
    private final List<Club> clubList = new ArrayList<>();
    private Location location;
    private OleEndlessRecyclerViewScrollListener scrollListener;
    OleClubFilterFragment filterFragment;

    //filter
    private String date = "";
    private String openTime = "";
    private String closeTime = "";
    private String cityId = "";
    private String countryId = "";
    private String offer = "";
    private final String nearby = "";
    private String name = "";
    private String fieldSize = "";
    private String fieldType = "";
    private String grassType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPClubListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean("is_search", false)) {
                binding.searchVu.requestFocus();
            }
        }

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        scrollListener = new OleEndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 50) {
                    pageNo = page + 1;
                    getClubList(false);
                }
            }
        };
        binding.recyclerVu.addOnScrollListener(scrollListener);

        adapter = new OlePlayerClubListAdapter(getContext(), clubList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.filterBg.setVisibility(View.GONE);

        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                name = binding.searchVu.getQuery().toString();
                pageNo = 1;
                getClubList(false);
                return true;
            }
        });

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColorNew));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocationAndCallAPI();
            }
        });

        binding.backBtn.setOnClickListener(this);
        binding.filterBg.setOnClickListener(this);
        binding.filterBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationAndCallAPI();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.filterBg) {
            removeFilterFrag();
        }
        else if (v == binding.filterBtn) {
            filtertClicked();
        }
    }

    private void getLocationAndCallAPI() {
        new AirLocation(getContext(), true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location loc) {
                // do something
                location = loc;
                pageNo = 1;
                getClubList(clubList.isEmpty());
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                // do something
                binding.pullRefresh.setRefreshing(false);
                pageNo = 1;
                getClubList(clubList.isEmpty());
            }
        });
    }

    private final OlePlayerClubListAdapter.ItemClickListener itemClickListener = new OlePlayerClubListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OlePlayerClubDetailActivity.class);
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(clubList.get(pos)));
            startActivity(intent);
        }

        @Override
        public void favClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Club club = clubList.get(pos);
            if (club.getFavorite().equalsIgnoreCase("1")) {
                addRemoveFavClub(true, club.getId(), "0", pos);
            }
            else {
                addRemoveFavClub(true, club.getId(), "1", pos);
            }
        }

        @Override
        public void rateVuClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OleClubRateDialog rateDialog = new OleClubRateDialog(getContext(), clubList.get(pos).getId());
            rateDialog.show();
        }
    };

    private void filterBgClicked() {
        removeFilterFrag();
    }

    private void removeFilterFrag() {
        getSupportFragmentManager().beginTransaction().remove(filterFragment).commit();
        filterFragment = null;
        binding.filterBg.setVisibility(View.GONE);
    }

    private void filtertClicked() {
        if (filterFragment != null && filterFragment.isVisible()) {
            removeFilterFrag();
        }
        else {
            binding.filterBg.setVisibility(View.VISIBLE);
            filterFragment = new OleClubFilterFragment();
            filterFragment.date = date;
            filterFragment.countryId = countryId;
            filterFragment.cityId = cityId;
            filterFragment.offer = offer;
            filterFragment.openTime = openTime;
            filterFragment.closeTime = closeTime;
            filterFragment.fieldSize = fieldSize;
            filterFragment.fieldType = fieldType;
            filterFragment.grassType = grassType;
            filterFragment.setFragmentCallBack(callBack);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.filter_container, filterFragment).commit();
        }
    }

    OleClubFilterFragment.ClubFilterFragmentCallBack  callBack = new OleClubFilterFragment.ClubFilterFragmentCallBack() {
        @Override
        public void getFilters(String date, String countryId, String cityId, String offer, String openTime, String closeTime, String fieldSize, String fieldType, String grassType) {
            OlePClubListActivity.this.date = date;
            OlePClubListActivity.this.countryId = countryId;
            OlePClubListActivity.this.cityId = cityId;
            OlePClubListActivity.this.offer = offer;
            OlePClubListActivity.this.openTime = openTime;
            OlePClubListActivity.this.closeTime = closeTime;
            OlePClubListActivity.this.fieldSize = fieldSize;
            OlePClubListActivity.this.fieldType = fieldType;
            OlePClubListActivity.this.grassType = grassType;

            pageNo = 1;
            OlePClubListActivity.this.getClubList(true);

            removeFilterFrag();
        }
    };

    private void getClubList(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        if (location == null) {
            call = AppManager.getInstance().apiInterface.getClubList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, pageNo, offer, name, date, openTime, closeTime, cityId, grassType, fieldType, fieldSize, nearby, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        else {
            call = AppManager.getInstance().apiInterface.getClubList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), location.getLatitude(), location.getLongitude(), pageNo, offer, name, date, openTime, closeTime, cityId, grassType, fieldType, fieldSize, nearby, Functions.getPrefValue(getContext(), Constants.kAppModule));
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
                                clubList.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    clubList.add(gson.fromJson(arr.get(i).toString(), Club.class));
                                }
                                AppManager.getInstance().clubs = clubList;
                            }
                            else {
                                List<Club> more = new ArrayList<>();
                                for (int i = 0; i < arr.length(); i++) {
                                    more.add(gson.fromJson(arr.get(i).toString(), Club.class));
                                }

                                if (more.size() > 0) {
                                    AppManager.getInstance().clubs.addAll(more);
                                    clubList.addAll(more);
                                }
                                else {
                                    pageNo = pageNo-1;
                                }
                            }
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

    private void addRemoveFavClub(boolean isLoader, String clubId, String status, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addRemoveFavAPI(clubId, status, "club", new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(getContext(), msg, FancyToast.SUCCESS);
                    clubList.get(pos).setFavorite(status);
                    int favCount = Integer.parseInt(clubList.get(pos).getFavoriteCount());
                    if (status.equalsIgnoreCase("0")) {
                        if (favCount>0) {
                            favCount -= 1;
                        }
                    }
                    else {
                        favCount += 1;
                    }
                    clubList.get(pos).setFavoriteCount(String.valueOf(favCount));
                    adapter.notifyItemChanged(pos);
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }
}