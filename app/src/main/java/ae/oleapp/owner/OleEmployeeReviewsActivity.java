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
import ae.oleapp.adapters.OleEmpRatingFilterAdapter;
import ae.oleapp.adapters.OleEmployeeReviewAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmployeeReviewsBinding;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.models.OleEmployeeReview;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmployeeReviewsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmployeeReviewsBinding binding;
    private String empId = "";
    private OleEmployeeReviewAdapter adapter;
    private final List<OleEmployeeReview> reviewList = new ArrayList<>();
    private OleEmpRatingFilterAdapter filterAdapter;
    private final List<OleKeyValuePair> ratingList = new ArrayList<>();
    private String fromDate = "", toDate = "";
    private String rateStar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmployeeReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empId = bundle.getString("emp_id", "");
        }

        ratingList.add(new OleKeyValuePair("", getString(R.string.all)));
        ratingList.add(new OleKeyValuePair("5.0", "5.0"));
        ratingList.add(new OleKeyValuePair("4.0", "4.0"));
        ratingList.add(new OleKeyValuePair("3.0", "3.0"));
        ratingList.add(new OleKeyValuePair("2.0", "2.0"));
        ratingList.add(new OleKeyValuePair("1.0", "1.0"));
        LinearLayoutManager ratingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rateRecyclerVu.setLayoutManager(ratingLayoutManager);
        filterAdapter = new OleEmpRatingFilterAdapter(getContext(), ratingList, 0);
        filterAdapter.setOnItemClickListener(filterClickListener);
        binding.rateRecyclerVu.setAdapter(filterAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleEmployeeReviewAdapter(getContext(), reviewList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.tvDate.setVisibility(View.GONE);

        getEmployeeRatingAPI(true);

        binding.backBtn.setOnClickListener(this);
        binding.btnCalendar.setOnClickListener(this);
    }

    OleEmpRatingFilterAdapter.OnItemClickListener filterClickListener = new OleEmpRatingFilterAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            rateStar = ratingList.get(pos).getKey();
            filterAdapter.setSelectedIndex(pos);
            getEmployeeRatingAPI(true);
        }
    };

    OleEmployeeReviewAdapter.ItemClickListener itemClickListener = new OleEmployeeReviewAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleEmployeeReview review = reviewList.get(pos);
            if (review.getBookingField().equalsIgnoreCase("football")) {
                Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
                intent.putExtra("booking_id", review.getBookingId());
                startActivity(intent);
            }
            else {
                // padel
                Intent intent = new Intent(getContext(), OlePadelBookingDetailActivity.class);
                intent.putExtra("booking_id", review.getBookingId());
                startActivity(intent);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.btnCalendar) {
            showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
                @Override
                public void filterData(DialogFragment df, String from, String to) {
                    df.dismiss();
                    fromDate = from;
                    toDate = to;
                    binding.tvDate.setVisibility(View.VISIBLE);
                    if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                        binding.tvDate.setText(String.format("%s %s %s", fromDate, getString(R.string.to), toDate));
                    }
                    else if (!fromDate.isEmpty()) {
                        binding.tvDate.setText(fromDate);
                    }
                    else if (!toDate.isEmpty()) {
                        binding.tvDate.setText(toDate);
                    }
                    else {
                        binding.tvDate.setText("");
                        binding.tvDate.setVisibility(View.GONE);
                    }
                    getEmployeeRatingAPI(true);
                }
            });
        }
    }

    private void getEmployeeRatingAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployeeRatings(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), empId, fromDate, toDate, rateStar);
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
                                reviewList.add(gson.fromJson(arr.get(i).toString(), OleEmployeeReview.class));
                            }
                            binding.tvReview.setText(getResources().getString(R.string.reviews_place, object.getString("reviews_count")));
                            binding.tvRate.setText(object.getString("ratings"));
                            float perc = (Float.parseFloat(object.getString("ratings")) / 5) * 100;
                            binding.progressbar.setProgress(perc);
                        }
                        else {
                            reviewList.clear();
                            binding.tvReview.setText(getResources().getString(R.string.reviews_place, "0"));
                            binding.tvRate.setText("0.0");
                            binding.progressbar.setProgress(0);
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