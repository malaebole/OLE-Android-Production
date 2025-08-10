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
import ae.oleapp.adapters.OleEmployeeReviewAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmpMyRatingsBinding;
import ae.oleapp.models.OleEmployeeReview;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmpMyRatingsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmpMyRatingsBinding binding;
    private OleEmployeeReviewAdapter adapter;
    private final List<OleEmployeeReview> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmpMyRatingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleEmployeeReviewAdapter(getContext(), reviewList, true);
        binding.recyclerVu.setAdapter(adapter);

        binding.backBtn.setOnClickListener(this);
        binding.btnSeeAll.setOnClickListener(this);

        getEmployeeRatingAPI(true);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnSeeAll) {
            Intent intent = new Intent(getContext(), OleEmpTipPaymentActivity.class);
            startActivity(intent);
        }
    }

    private void getEmployeeRatingAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployeeRatings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), Functions.getPrefValue(getContext(), Constants.kUserID), "", "", "");
        call.enqueue(new Callback<ResponseBody>() {
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
                                reviewList.add(gson.fromJson(arr.get(i).toString(), OleEmployeeReview.class));
                            }
                            binding.tvReceivableTip.setText(String.format("%s %s", object.getString("pending_tip"), object.getString("currency")));
                        }
                        else {
                            reviewList.clear();
                            binding.tvReceivableTip.setText("0 AED");
                        }
                        adapter.notifyDataSetChanged();
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