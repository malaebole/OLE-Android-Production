package ae.oleapp.player;

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
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleOutstandingBalanceListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPlayerOutstandingBalanceBinding;
import ae.oleapp.models.OlePlayerBalance;
import ae.oleapp.owner.OleBookingDetailActivity;
import ae.oleapp.owner.OlePadelBookingDetailActivity;
import ae.oleapp.padel.OlePadelMatchBookingDetailActivity;
import ae.oleapp.padel.OlePadelNormalBookingDetailActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerOutstandingBalanceActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPlayerOutstandingBalanceBinding binding;
    private String clubId = "", playerId = "";
    private OleRankClubAdapter oleRankClubAdapter;
    private final List<OlePlayerBalance> balanceList = new ArrayList<>();
    private OleOutstandingBalanceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerOutstandingBalanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.unpaid_amount);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerId = bundle.getString("player_id", "");
        }

        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, -1, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleOutstandingBalanceListAdapter(getContext(), balanceList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);


        getBalanceAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
    }

    OleOutstandingBalanceListAdapter.ItemClickListener itemClickListener = new OleOutstandingBalanceListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OlePlayerBalance balance = balanceList.get(pos);
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                if (balance.getClubType().equalsIgnoreCase("football")) {
                    Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                    intent.putExtra("booking_id", balance.getBookingId());
                    startActivity(intent);
                }
                else {
                    // padel
                    Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                    intent.putExtra("booking_id", balance.getBookingId());
                    startActivity(intent);
                }
            }
            else {
                if (balance.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                        Intent intent = new Intent(getContext(), OlePadelNormalBookingDetailActivity.class);
                        intent.putExtra("booking_id", balance.getBookingId());
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getContext(), OleNormalBookingDetailActivity.class);
                        intent.putExtra("booking_id", balance.getBookingId());
                        startActivity(intent);
                    }
                }
                else if (balance.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                    Intent intent = new Intent(getContext(), OleGameBookingDetailActivity.class);
                    intent.putExtra("booking_id", balance.getBookingId());
                    startActivity(intent);
                }
                else {
                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                        Intent intent = new Intent(getContext(), OlePadelMatchBookingDetailActivity.class);
                        intent.putExtra("booking_id", balance.getBookingId());
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getContext(), OleMatchBookingDetailActivity.class);
                        intent.putExtra("booking_id", balance.getBookingId());
                        startActivity(intent);
                    }
                }
            }
        }
    };

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getBalanceAPI(true);
        }
    };

    private void calculateTotal() {
        double total = 0;
        String currency = getString(R.string.aed);
        for (OlePlayerBalance balance : balanceList) {
            total += Double.parseDouble(balance.getAmount());
            currency = balance.getCurrency();
        }
        if (total > 0) {
            binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s: %.2f %s", getString(R.string.unpaid_amount), total, currency));
        }
        else {
            binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s: 0 %s", getString(R.string.unpaid_amount), currency));
        }
    }

    private void getBalanceAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.playerBalance(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            balanceList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < array.length(); i++) {
                                balanceList.add(gson.fromJson(array.get(i).toString(), OlePlayerBalance.class));
                            }
                            adapter.notifyDataSetChanged();
                            calculateTotal();
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