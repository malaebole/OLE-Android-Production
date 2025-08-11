package ae.oleapp.player;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleHistoryDetailAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityHistoryDetailBinding;
import ae.oleapp.models.OleHistoryDetail;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleHistoryDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityHistoryDetailBinding binding;
    private String playerId1 = "";
    private String playerId2 = "";
    private final List<OleHistoryDetail> oleHistoryDetailList = new ArrayList<>();
    private OleHistoryDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityHistoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.match_history);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerId1 = bundle.getString("playerId_1", "");
            playerId2 = bundle.getString("playerId_2", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleHistoryDetailAdapter(getContext(), oleHistoryDetailList);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.profileVu1.setOnClickListener(this);
        binding.profileVu2.setOnClickListener(this);

        matchHistoryDeatilAPI(true);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.profileVu1) {
            imgVuClicked();
        }
        else if (v == binding.profileVu2) {
            imgVu2Clicked();
        }
    }

    private void imgVuClicked() {
        if (playerId1.isEmpty()) {
            return;
        }
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", playerId1);
        startActivity(intent);
    }

    private void imgVu2Clicked() {
        if (playerId2.isEmpty()) {
            return;
        }
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", playerId2);
        startActivity(intent);
    }

    private void populateData(JSONObject object) throws JSONException {
        Gson gson = new Gson();
        OlePlayerInfo playerOne = gson.fromJson(object.getJSONObject("player_one").toString(), OlePlayerInfo.class);
        OlePlayerInfo playerTwo = gson.fromJson(object.getJSONObject("player_two").toString(), OlePlayerInfo.class);
        String totalMatch = object.getString("total_matches");
        String drawMatch = object.getString("draw_matches");
        String lastMatch = object.getString("last_match");

        oleHistoryDetailList.clear();
        JSONArray arr = object.getJSONArray(Constants.kData);
        for (int i = 0; i < arr.length(); i++) {
            oleHistoryDetailList.add(gson.fromJson(arr.get(i).toString(), OleHistoryDetail.class));
        }
        adapter.notifyDataSetChanged();

        binding.profileVu1.populateData(playerOne.getNickName(), playerOne.getPhotoUrl(), playerOne.getLevel(), true);
        binding.tvP1Win.setText(playerOne.getMatchWon());
        Glide.with(getApplicationContext()).load(playerOne.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1SmallImg);

        binding.tvDraw.setText(drawMatch);
        binding.tvPlayed.setText(getResources().getString(R.string.total_played_place, totalMatch));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(lastMatch);
            dateFormat.applyPattern("EEE, dd/MM/yyyy");
            binding.tvDate.setText(getResources().getString(R.string.last_played_place, dateFormat.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
            binding.tvDate.setText("");
        }

        binding.profileVu2.populateData(playerTwo.getNickName(), playerTwo.getPhotoUrl(), playerTwo.getLevel(), true);
        binding.tvP2Win.setText(playerTwo.getMatchWon());
        Glide.with(getApplicationContext()).load(playerTwo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p2SmallImg);
    }

    private void matchHistoryDeatilAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.matchHistoryDetail(Functions.getAppLang(getContext()), playerId1, playerId2);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            populateData(object);
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
