package ae.oleapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


import ae.oleapp.R;
import ae.oleapp.adapters.OlePadelPlayerListAdapter;
import ae.oleapp.adapters.OlePlayerClubListAdapter;
import ae.oleapp.adapters.OlePlayerListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentFavoriteBinding;
import ae.oleapp.dialogs.OleClubRateDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.player.OlePlayerClubDetailActivity;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleFavoriteFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentFavoriteBinding binding;
    private OlePlayerClubListAdapter clubAdapter;
    private OlePlayerListAdapter playerAdapter;
    private OlePadelPlayerListAdapter padelPlayerAdapter;
    private final List<Club> clubList = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private boolean isClub = true;

    public OleFavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentFavoriteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (Functions.getPrefValue(requireContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.cardVu.setCardBackgroundColor(getResources().getColor(R.color.blueColorNew));
            binding.imgVuMenu.setImageResource(R.drawable.p_menu_ic_white);
            binding.tvTitle.setTextColor(getResources().getColor(R.color.whiteColor));
            binding.imgVuNotif.setImageResource(R.drawable.p_notification_ic_white);
        }
        else {
            binding.cardVu.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
            binding.imgVuMenu.setImageResource(R.drawable.p_menu_ic);
            binding.tvTitle.setTextColor(getResources().getColor(R.color.darkTextColor));
            binding.imgVuNotif.setImageResource(R.drawable.p_notification_ic);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        clubClicked();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    clubClicked();
                }
                else {
                    playerClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.relMenu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBadgeValue();
        if (isClub) {
            getFavListAPI(clubList.isEmpty());
        }
        else {
            getFavListAPI(playerList.isEmpty());
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void clubClicked() {
        isClub = true;
        clubAdapter = new OlePlayerClubListAdapter(getActivity(), clubList);
        clubAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(clubAdapter);
        binding.btnAdd.setVisibility(View.GONE);
    }

    private void playerClicked() {
        isClub = false;
        if (Functions.getPrefValue(requireContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            padelPlayerAdapter = new OlePadelPlayerListAdapter(getActivity(), playerList);
            padelPlayerAdapter.setOnItemClickListener(padelItemClickListener);
            binding.recyclerVu.setAdapter(padelPlayerAdapter);
        }
        else {
            playerAdapter = new OlePlayerListAdapter(getActivity(), playerList, false);
            playerAdapter.setFromFav(true);
            playerAdapter.setOnItemClickListener(clickListener);
            binding.recyclerVu.setAdapter(playerAdapter);
        }
        binding.btnAdd.setVisibility(View.VISIBLE);
    }

    private final OlePlayerListAdapter.OnItemClickListener clickListener = new OlePlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            gotoProfile(playerList.get(pos).getId());
        }

        @Override
        public void OnImageClick(View v, int pos) {
            OnItemClick(v, pos);
        }
    };

    private final OlePadelPlayerListAdapter.OnItemClickListener padelItemClickListener = new OlePadelPlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            gotoProfile(playerList.get(pos).getId());
        }

        @Override
        public void OnDeleteClick(View v, int pos) {

        }
    };

    private void gotoProfile(String playerId) {
        Intent intent = new Intent(getActivity(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", playerId);
        startActivity(intent);
    }

    private final OlePlayerClubListAdapter.ItemClickListener itemClickListener = new OlePlayerClubListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getActivity(), OlePlayerClubDetailActivity.class);
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(clubList.get(pos)));
            startActivity(intent);
        }

        @Override
        public void favClicked(View view, int pos) {
            Club club = clubList.get(pos);
            if (club.getFavorite().equalsIgnoreCase("1")) {
                addRemoveFavClub(true, club.getId(), "0", pos);
            }
        }

        @Override
        public void rateVuClicked(View view, int pos) {
            OleClubRateDialog rateDialog = new OleClubRateDialog(getActivity(), clubList.get(pos).getId());
            rateDialog.show();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void menuClicked() {
        if (getActivity() instanceof OlePlayerMainTabsActivity) {
            ((OlePlayerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OlePlayerMainTabsActivity) {
            ((OlePlayerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    private void addClicked() {
        Intent intent = new Intent(getActivity(), OlePlayerListActivity.class);
        intent.putExtra("is_selection", true);
        this.startActivityForResult(intent, 106);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == Activity.RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            String ids = "";
            for (OlePlayerInfo info: list) {
                if (ids.isEmpty()) {
                    ids = info.getId();
                }
                else {
                    ids = String.format("%s,%s", ids, info.getId());
                }
            }
            addFav(ids);
        }
    }

    private void addRemoveFavClub(boolean isLoader, String clubId, String status, int pos) {
        KProgressHUD hud = Functions.showLoader(getActivity(), "Image processing");
        ((OlePlayerMainTabsActivity)requireContext()).addRemoveFavAPI(clubId, status, "club", (success, msg) -> {
            Functions.hideLoader(hud);
            if (success) {
                Functions.showToast(getActivity(), msg, FancyToast.SUCCESS);
                clubList.remove(pos);
                clubAdapter.notifyItemRemoved(pos);
                clubAdapter.notifyItemRangeChanged(pos, clubList.size());
            }
            else {
                Functions.showToast(getActivity(), msg, FancyToast.ERROR);
            }
        });
    }

    private void getFavListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFavList(Functions.getAppLang(getActivity()),Functions.getPrefValue(requireContext(), Constants.kUserID), Functions.getPrefValue(getActivity(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arrC = object.getJSONObject(Constants.kData).getJSONArray("clubs");
                            JSONArray arrP = object.getJSONObject(Constants.kData).getJSONArray("players");
                            Gson gson = new Gson();
                            clubList.clear();
                            playerList.clear();
                            for (int i = 0; i < arrC.length(); i++) {
                                clubList.add(gson.fromJson(arrC.get(i).toString(), Club.class));
                            }
                            for (int i = 0; i < arrP.length(); i++) {
                                playerList.add(gson.fromJson(arrP.get(i).toString(), OlePlayerInfo.class));
                            }
                        }
                        else {
                            clubList.clear();
                            playerList.clear();
                        }
                        if (isClub) {
                            clubAdapter.notifyDataSetChanged();
                        }
                        else {
                            if (Functions.getPrefValue(requireContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                padelPlayerAdapter.notifyDataSetChanged();
                            }
                            else {
                                playerAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getActivity(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getActivity(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getActivity(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getActivity(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void addFav(String playerId) {
        KProgressHUD hud = Functions.showLoader(getActivity(), "Image processing");
        ((BaseActivity) requireContext()).addRemoveFavAPI(playerId, "1", "player", (success, msg) -> {
            Functions.hideLoader(hud);
            if (success) {
                Functions.showToast(getActivity(), msg, FancyToast.SUCCESS);
                getFavListAPI(false);
            }
            else {
                Functions.showToast(getActivity(), msg, FancyToast.ERROR);
            }
        });
    }
}
