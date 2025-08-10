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
import ae.oleapp.adapters.OleSmsServiceAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivitySmsServiceBinding;
import ae.oleapp.models.OleSmsData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleSmsServiceActivity extends BaseActivity implements View.OnClickListener {

    private OleactivitySmsServiceBinding binding;
    private String clubId = "";
    private OleSmsServiceAdapter adapter;
    private final List<OleSmsData> oleSmsDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivitySmsServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.sms_services);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleSmsServiceAdapter(getContext(), oleSmsDataList);
        adapter.setOnItemClick(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.noDataVu.setVisibility(View.GONE);
        binding.btnBuy.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnBuy.setOnClickListener(this);
        binding.btnBuy2.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSmsAPI(oleSmsDataList.isEmpty());
    }

    OleSmsServiceAdapter.OnItemClickListener itemClickListener = new OleSmsServiceAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int position) {
            OleSmsData data = oleSmsDataList.get(position);
            Intent intent = new Intent(getContext(), OleBuySmsActivity.class);
            Gson gson = new Gson();
            intent.putExtra("data", gson.toJson(data));
            intent.putExtra("is_update", true);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnBuy || v == binding.btnBuy2) {
            buyClicked();
        }
    }

    private void buyClicked() {
        Intent smsIntent = new Intent(getContext(), OleBuySmsActivity.class);
        smsIntent.putExtra("club_id", clubId);
        smsIntent.putExtra("is_update", false);
        startActivity(smsIntent);
    }

    private void getSmsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getSMS(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleSmsDataList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleSmsData data = gson.fromJson(arr.get(i).toString(), OleSmsData.class);
                                oleSmsDataList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            if (oleSmsDataList.isEmpty()) {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                                binding.btnBuy.setVisibility(View.GONE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.GONE);
                                binding.btnBuy.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            binding.noDataVu.setVisibility(View.VISIBLE);
                            binding.btnBuy.setVisibility(View.GONE);
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