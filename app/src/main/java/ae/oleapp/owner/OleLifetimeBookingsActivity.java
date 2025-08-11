package ae.oleapp.owner;

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
import ae.oleapp.databinding.OleactivityLifetimeBookingsBinding;
import ae.oleapp.models.OleBookingList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleLifetimeBookingsActivity extends BaseActivity {

    private OleactivityLifetimeBookingsBinding binding;
    private String clubId = "", playerId = "", isCall = "0", thisMonth = "0", callBookingPhone = "", appModule = "";
    private OleBookingCountDetailAdapter adapter;
    private final List<OleBookingList> oleBookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityLifetimeBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            playerId = bundle.getString("player_id", "");
            isCall = bundle.getString("is_call", "");
            thisMonth = bundle.getString("this_month", "");
            callBookingPhone = bundle.getString("phone", "");
            appModule = bundle.getString("app_module", "");
        }

        if (thisMonth.equalsIgnoreCase("1")) {
            binding.bar.toolbarTitle.setText(R.string.this_month);
        }
        else {
            binding.bar.toolbarTitle.setText(R.string.lifetime);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleBookingCountDetailAdapter(getContext(), oleBookingList);
        adapter.setOnItemClick(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        getBookingListAPI(true);

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    OleBookingCountDetailAdapter.OnItemClickListener itemClickListener = new OleBookingCountDetailAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int position) {
            if (oleBookingList.get(position).getBookingFieldType().equalsIgnoreCase("football")) {
                Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                intent.putExtra("booking_id", oleBookingList.get(position).getId());
                intent.putExtra("isBookingVuHidden", true);
                startActivity(intent);
            }
            else {
                // padel
                Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                intent.putExtra("booking_id", oleBookingList.get(position).getId());
                intent.putExtra("isBookingVuHidden", true);
                startActivity(intent);
            }
        }
    };

    private void getBookingListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.totalCompleteBookings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, playerId, isCall, thisMonth, callBookingPhone, appModule);
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