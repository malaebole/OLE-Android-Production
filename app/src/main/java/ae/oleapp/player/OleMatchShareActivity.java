package ae.oleapp.player;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityMatchShareBinding;
import ae.oleapp.models.OleBookingList;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.Club;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleMatchShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityMatchShareBinding binding;
    private Club club;
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private final List<OleBookingList> oleBookingList = new ArrayList<>();
    private List<Club> clubList = new ArrayList<>();
    private int selectedDateIndex = 0;
    private OleBookingDateAdapter daysAdapter;
    private String selectedDate = "";
    private OleBookingListAdapter oleBookingListAdapter;
    private boolean isMatch = false;
    private final String kBookDateFormat = "yyyy-MM-dd";
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMatchShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isMatch = bundle.getBoolean("is_match", false);
        }

        if (isMatch) {
            binding.bar.toolbarTitle.setText(R.string.matches);
            clubList = AppManager.getInstance().clubs;
        }
        else {
            binding.bar.toolbarTitle.setText(R.string.friendly_game);
            // friendly game is only in football
            for (Club c : AppManager.getInstance().clubs) {
                if (c.getClubType().equalsIgnoreCase(Constants.kFootballModule)) {
                    clubList.add(c);
                }
            }
        }

        if (clubList.size() > 0) {
            club = clubList.get(0);
        }

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), clubList, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        selectedDateIndex = 0;
        selectedDate = getDateStr(new Date());

        LinearLayoutManager horizLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(horizLayoutManager);
        daysAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
        daysAdapter.setOnItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(daysAdapter);

        LinearLayoutManager vertLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(vertLayoutManager);
        oleBookingListAdapter = new OleBookingListAdapter(getContext(), oleBookingList);
        oleBookingListAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(oleBookingListAdapter);

        getBookingsAPI(true, "1", getDateStr(new Date()));

        binding.bar.backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            club = clubList.get(pos);
            getBookingsAPI(true, "1", getDateStr(new Date()));
        }
    };

    OleBookingListAdapter.ItemClickListener itemClickListener = new OleBookingListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleBookingList booking = oleBookingList.get(pos);
            getBookingDetail(true, booking.getId());
        }

        @Override
        public void OnItemLongClick(View v, int pos) {

        }

        @Override
        public void callClicked(View view, int pos) {

        }
    };

    private String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookDateFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    OleBookingDateAdapter.OnItemClickListener daysClickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDateIndex = pos;
            daysAdapter.setSelectedDateIndex(pos);
            selectedDate = arrDate.get(pos).getDate();
            getBookingsAPI(true, "0", "");
        }
    };

    private void getBookingsAPI(boolean isLoader, String isDateNeeded, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String type = "";
        if (isMatch) {
            type = "match";
        }
        else {
            type = "friendly_game";
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.userBookings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), selectedDate, "", club.getId(), "", isDateNeeded, date, type);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleBookingList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingList booking = gson.fromJson(arr.get(i).toString(), OleBookingList.class);
                                if (booking.getStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking) ||
                                        booking.getStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking) ||
                                        booking.getStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
                                    // no need to add
                                }
                                else {
                                    oleBookingList.add(booking);
                                }
                            }
                            oleBookingListAdapter.setDataSource(oleBookingList);
                            if (isDateNeeded.equalsIgnoreCase("1"))  {
                                arrDate.clear();
                                JSONArray arrD = object.getJSONArray("dates");
                                for (int i = 0; i < arrD.length(); i++) {
                                    arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
                                }
                                daysAdapter.setDataSource(arrDate.toArray());
                            }
                        }
                        else {
                            oleBookingList.clear();
                            oleBookingListAdapter.setDataSource(new ArrayList<>());
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

    private void getBookingDetail(boolean isLoader, String bookingId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getBookingDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            OleBookingList bookingDetail = gson.fromJson(obj.toString(), OleBookingList.class);
                            moveNext(bookingDetail);
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

    private void moveNext(OleBookingList booking) {
        if (booking.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
            Intent intent = new Intent(getContext(), OleFriendlyGameShareActivity.class);
            intent.putExtra("club_name", booking.getClubName());
            intent.putExtra("date", booking.getBookingDate());
            intent.putExtra("time", booking.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(booking.getUser()));
            intent.putExtra("player_list", gson.toJson(booking.getJoinedPlayers()));
            int totalPlayers = 0;
            if (booking.getTotalPlayers() == null || booking.getTotalPlayers().isEmpty()) {
                totalPlayers = 0;
            }
            else {
                totalPlayers = Integer.parseInt(booking.getTotalPlayers());
            }
            intent.putExtra("req_players", totalPlayers);
            startActivity(intent);
        }
        else {
            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                Intent intent = new Intent(getContext(), OlePadelMatchShareActivity.class);
                intent.putExtra("club_name", booking.getClubName());
                intent.putExtra("date", booking.getBookingDate());
                intent.putExtra("time", booking.getBookingTime());
                Gson gson = new Gson();
                intent.putExtra("player_one", gson.toJson(booking.getUser()));
                intent.putExtra("player_one_partner", gson.toJson(booking.getUserPartner()));
                if (booking.getPlayerTwo() != null && !booking.getPlayerTwo().isEmpty()) {
                    intent.putExtra("player_two", gson.toJson(booking.getPlayerTwo()));
                }
                if (booking.getPlayerTwoPartner() != null && !booking.getPlayerTwoPartner().isEmpty()) {
                    intent.putExtra("player_two_partner", gson.toJson(booking.getPlayerTwoPartner()));
                }
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getContext(), OleFootballMatchShareActivity.class);
                intent.putExtra("club_name", booking.getClubName());
                intent.putExtra("date", booking.getBookingDate());
                intent.putExtra("time", booking.getBookingTime());
                Gson gson = new Gson();
                intent.putExtra("player_one", gson.toJson(booking.getUser()));
                if (booking.getPlayerTwo() != null && !booking.getPlayerTwo().isEmpty()) {
                    intent.putExtra("player_two", gson.toJson(booking.getPlayerTwo()));
                }
                startActivity(intent);
            }
        }
    }
}