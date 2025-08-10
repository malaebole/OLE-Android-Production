package ae.oleapp.owner;

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
import ae.oleapp.adapters.OleAdvertiseListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAdvertiseListBinding;
import ae.oleapp.models.OleAdvertiseClub;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleAdvertiseListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAdvertiseListBinding binding;
    private OleAdvertiseListAdapter adapter;
    private final List<OleAdvertiseClub> oleAdvertiseClubs = new ArrayList<>();
    private String clubId = "", featuredPrice = "", countryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAdvertiseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.advertise_club);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            countryId = bundle.getString("country_id", "");
        }

        binding.noDataVu.setVisibility(View.GONE);
        binding.btnAdvertise.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleAdvertiseListAdapter(getContext(), oleAdvertiseClubs);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdvertise.setOnClickListener(this);
        binding.btnAdvertise2.setOnClickListener(this);

    }

    OleAdvertiseListAdapter.ItemClickListener itemClickListener = new OleAdvertiseListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleAdvertiseClubActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("featured_price", featuredPrice);
            intent.putExtra("country_id", countryId);
            Gson gson = new Gson();
            intent.putExtra("data", gson.toJson(oleAdvertiseClubs.get(pos)));
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getAdvertiseListAPI(oleAdvertiseClubs.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnAdvertise || v == binding.btnAdvertise2) {
            advertiseClicked();
        }
    }

    private void advertiseClicked() {
        Intent intent = new Intent(getContext(), OleAdvertiseClubActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("featured_price", featuredPrice);
        intent.putExtra("country_id", countryId);
        startActivity(intent);
    }

    private void getAdvertiseListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFeaturedClub(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            featuredPrice = object.getString("featured_price_per_day");
                            oleAdvertiseClubs.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleAdvertiseClubs.add(gson.fromJson(arr.get(i).toString(), OleAdvertiseClub.class));
                            }
                            adapter.notifyDataSetChanged();
                            if (oleAdvertiseClubs.isEmpty()) {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                                binding.btnAdvertise.setVisibility(View.GONE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.GONE);
                                binding.btnAdvertise.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            oleAdvertiseClubs.clear();
                            adapter.notifyDataSetChanged();
                            binding.noDataVu.setVisibility(View.VISIBLE);
                            binding.btnAdvertise.setVisibility(View.GONE);
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