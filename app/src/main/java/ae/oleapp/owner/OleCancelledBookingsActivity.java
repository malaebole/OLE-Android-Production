package ae.oleapp.owner;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import ae.oleapp.adapters.OleBookingCountDetailAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityCancelledBookingsBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleBookingList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCancelledBookingsActivity extends BaseActivity {

    private OleactivityCancelledBookingsBinding binding;
    private OleBookingCountDetailAdapter adapter;
    private final List<OleBookingList> oleBookingList = new ArrayList<>();
    private String clubId = "", playerId = "", appModule = "", type = "", from = "", to = "", playerPhone = "";
    private boolean isPlayerStat = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCancelledBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            playerId = bundle.getString("player_id", "");
            isPlayerStat = bundle.getBoolean("is_player_stat", true);
            playerPhone = bundle.getString("player_phone", "");
            if (isPlayerStat) {
                type = bundle.getString("type", "");
            }
            else {
                appModule = bundle.getString("app_module", "");
            }
        }

        if (type.equalsIgnoreCase("all_booked")) {
            binding.titleBar.toolbarTitle.setText(R.string.completed_bookings);
        }
        else if (type.equalsIgnoreCase("hours")) {
            binding.titleBar.toolbarTitle.setText(R.string.completed_hours); // title
            type = "all_booked"; // for API
        }
        else if (type.equalsIgnoreCase("app_booked")) {
            binding.titleBar.toolbarTitle.setText(R.string.bookings_by_app);
        }
        else if (type.equalsIgnoreCase("call_booked")) {
            binding.titleBar.toolbarTitle.setText(R.string.call_bookings);
        }
        else if (type.equalsIgnoreCase("upcoming")) {
            binding.titleBar.toolbarTitle.setText(R.string.upcoming_bookings);
        }
        else if (type.equalsIgnoreCase("all_canceled")) {
            binding.titleBar.toolbarTitle.setText(R.string.cancelled_bookings);
        }
        else if (type.equalsIgnoreCase("hot_canceled")) {
            binding.titleBar.toolbarTitle.setText(R.string.cancelled_bookings);
        }
        else {
            binding.titleBar.toolbarTitle.setText(R.string.cancelled_bookings);
        }
        if (isPlayerStat) {
            binding.relCalendar.setVisibility(View.VISIBLE);
            if (playerId.equalsIgnoreCase("")) {
                getBookingListPlayerStatAPI(true, "", playerPhone);
            }
            else {
                getBookingListPlayerStatAPI(true, playerId, "");
            }
        }
        else {
            binding.relCalendar.setVisibility(View.INVISIBLE);
            if (playerId.equalsIgnoreCase("")) {
                getBookingListAPI(true, "", playerPhone);
            }
            else {
                getBookingListAPI(true, playerId, "");
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleBookingCountDetailAdapter(getContext(), oleBookingList);
        adapter.setOnItemClick(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.titleBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.relCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarClicked();
            }
        });
    }

    OleBookingCountDetailAdapter.OnItemClickListener itemClickListener = new OleBookingCountDetailAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int position) {
            if (isPlayerStat) {
                if (oleBookingList.get(position).getBookingFieldType().equalsIgnoreCase(Constants.kPadelModule)) {
                    Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                    intent.putExtra("booking_id", oleBookingList.get(position).getId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                    intent.putExtra("booking_id", oleBookingList.get(position).getId());
                    startActivity(intent);
                }
            }
        }
    };

    private void calendarClicked() {
        showDateRangeFilter(from, to, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
            @Override
            public void filterData(DialogFragment df, String from, String to) {
                df.dismiss();
                OleCancelledBookingsActivity.this.from = from;
                OleCancelledBookingsActivity.this.to = to;
                if (playerId.equalsIgnoreCase("")) {
                    getBookingListPlayerStatAPI(true, "", playerPhone);
                }
                else {
                    getBookingListPlayerStatAPI(true, playerId, "");
                }
            }
        });
    }

    private void getBookingListAPI(boolean isLoader, String id, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getBookingsCountDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, id, phone, "hot_canceled", "", "", appModule);
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
                            oleBookingList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                oleBookingList.add(gson.fromJson(arr.get(i).toString(), OleBookingList.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            oleBookingList.clear();
                            adapter.notifyDataSetChanged();
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

    private void getBookingListPlayerStatAPI(boolean isLoader, String id, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.bookingListPlayerStat(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, id, phone, type, from, to);
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
                            oleBookingList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                oleBookingList.add(gson.fromJson(arr.get(i).toString(), OleBookingList.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            oleBookingList.clear();
                            adapter.notifyDataSetChanged();
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