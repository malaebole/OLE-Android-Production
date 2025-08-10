package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleMatchListAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentMatchesBinding;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.padel.OlePadelMatchBookingDetailActivity;
import ae.oleapp.padel.OlePadelMatchDetailActivity;
import ae.oleapp.player.OleGameBookingDetailActivity;
import ae.oleapp.player.OleGameDetailActivity;
import ae.oleapp.player.OleMatchBookingDetailActivity;
import ae.oleapp.player.OleMatchDetailActivity;
import ae.oleapp.player.OleNormalBookingDetailActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
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
public class OleMatchesFragment extends BaseFragment implements View.OnClickListener {

    private boolean isPlayed = false;
    private final List<OlePlayerMatch> upcomingList = new ArrayList<>();
    private final List<OlePlayerMatch> playedList = new ArrayList<>();
    private OleMatchListAdapter adapter;
    private OlefragmentMatchesBinding binding;

    public OleMatchesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentMatchesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
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
        adapter = new OleMatchListAdapter(getContext(), upcomingList);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        upcomingClicked();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    upcomingClicked();
                }
                else {
                    playedClicked();
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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final OleMatchListAdapter.ItemClickListener clickListener = new OleMatchListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OlePlayerMatch olePlayerMatch;
            if (isPlayed) {
                olePlayerMatch = playedList.get(pos);
            }
            else {
                olePlayerMatch = upcomingList.get(pos);
            }
            if (olePlayerMatch.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                if (olePlayerMatch.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                    Intent intent = new Intent(getContext(), OleNormalBookingDetailActivity.class);
                    intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                    startActivity(intent);
                }
                else if (olePlayerMatch.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                    Intent intent = new Intent(getContext(), OleGameBookingDetailActivity.class);
                    intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                    startActivity(intent);
                }
                else {
                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                        Intent intent = new Intent(getContext(), OlePadelMatchBookingDetailActivity.class);
                        intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getContext(), OleMatchBookingDetailActivity.class);
                        intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                        startActivity(intent);
                    }
                }
            }
            else {
                if (olePlayerMatch.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                    Intent intent = new Intent(getContext(), OleGameDetailActivity.class);
                    intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                    startActivity(intent);
                }
                else {
                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                        Intent intent = new Intent(getContext(), OlePadelMatchDetailActivity.class);
                        intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getContext(), OleMatchDetailActivity.class);
                        intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                        startActivity(intent);
                    }
                }
            }
        }

        @Override
        public void joinClicked(View view, int pos) {

        }

        @Override
        public void acceptClicked(View view, int pos) {

        }

        @Override
        public void challengeClicked(View view, int pos) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setBadgeValue();
        if (isPlayed) {
            getMatchListAPI(playedList.isEmpty());
        }
        else {
            getMatchListAPI(upcomingList.isEmpty());
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

    @Override
    public void onClick(View v) {
        if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
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

    private void upcomingClicked() {
        isPlayed = false;
        adapter.setDataSource(upcomingList);
    }

    private void playedClicked() {
        isPlayed = true;
        adapter.setDataSource(playedList);
    }

    private void getMatchListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getMatchesList(Functions.getAppLang(getActivity()),Functions.getPrefValue(getActivity(), Constants.kUserID), Functions.getPrefValue(getActivity(), Constants.kAppModule));
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
                            upcomingList.clear();
                            playedList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                OlePlayerMatch match = gson.fromJson(arr.get(i).toString(), OlePlayerMatch.class);
                                if (match.getMatchType().equalsIgnoreCase("upcoming")) {
                                    upcomingList.add(match);
                                }
                                else {
                                    playedList.add(match);
                                }
                            }
                            if (isPlayed) {
                                adapter.setDataSource(playedList);
                            }
                            else {
                                adapter.setDataSource(upcomingList);
                            }
                        }
                        else {
                            upcomingList.clear();
                            playedList.clear();
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
