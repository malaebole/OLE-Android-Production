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
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePlayerReviewAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPlayerReviewsBinding;
import ae.oleapp.models.OlePlayerReview;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerReviewsActivity extends BaseActivity {

    private OleactivityPlayerReviewsBinding binding;
    private String playerId = "";
    private OlePlayerReviewAdapter adapter;
    private final List<OlePlayerReview> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.playing_reviews);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerId = bundle.getString("player_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePlayerReviewAdapter(getContext(), reviewList);
        adapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getReviewsAPI(true);

    }

    OlePlayerReviewAdapter.OnItemClickListener clickListener = new OlePlayerReviewAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", reviewList.get(pos).getId());
            startActivity(intent);
        }
    };

    private void getReviewsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getReviews(Functions.getAppLang(getContext()), playerId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            reviewList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                reviewList.add(gson.fromJson(arr.get(i).toString(), OlePlayerReview.class));
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
