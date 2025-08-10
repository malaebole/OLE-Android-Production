package ae.oleapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import ae.oleapp.adapters.GiftsAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityGiftsBinding;
import ae.oleapp.models.ClubGifts;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGiftsBinding binding;

    private GiftsAdapter giftsAdapter;
    private final List<ClubGifts> clubGiftsList = new ArrayList<>();

    private String clubId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGiftsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Intent intent = getIntent();
        clubId = intent.getStringExtra("club_id");

        LinearLayoutManager oleGiftsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.giftsRecyclerVu.setLayoutManager(oleGiftsLayoutManager);
        giftsAdapter = new GiftsAdapter(getContext(), clubGiftsList);
        giftsAdapter.setItemClickListener(itemClickListener);
        binding.giftsRecyclerVu.setAdapter(giftsAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTab = tab.getPosition();
                giftsAdapter.setSelectedTab(selectedTab); // Update the selected tab in the adapter
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            // Other methods from TabLayout.OnTabSelectedListener
        });

    }


    GiftsAdapter.ItemClickListener itemClickListener = new GiftsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            String giftId = clubGiftsList.get(pos).getId();
            Intent intent = new Intent(getContext(), CreateGiftActivity.class);
            intent.putExtra("is_update",true);
            intent.putExtra("club_id", clubId);
            intent.putExtra("gift_id", giftId);
            intent.putExtra("target_id", clubGiftsList.get(pos).getTargetId());
            startActivity(intent);

        }
    };

    private void getGiftsList(Boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getGiftsList(Functions.getAppLang(getContext()), clubId, "");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                clubGiftsList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    clubGiftsList.add(gson.fromJson(data.get(i).toString(), ClubGifts.class));
                                }
                                giftsAdapter.setSelectedTab(0);
                                giftsAdapter.notifyDataSetChanged();
                            } else {
                                clubGiftsList.clear(); //17/07/2023
                                giftsAdapter.notifyDataSetChanged(); //17/07/2023
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Functions.hideLoader(hud);
                    if (t instanceof UnknownHostException) {
                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                    } else {
                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnCreate) {
            Intent intent = new Intent(getContext(), CreateGiftActivity.class);
            intent.putExtra("club_id",clubId);
            intent.putExtra("is_update", false);
            startActivity(intent);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGiftsList(clubGiftsList.isEmpty(), clubId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}