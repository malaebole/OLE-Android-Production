package ae.oleapp.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.LoginActivity;
import ae.oleapp.adapters.OleAppIntroAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityIntroSliderBinding;
import ae.oleapp.external.OleLinePagerIndicatorDecoration;
import ae.oleapp.models.OleAppIntro;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroSliderActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityIntroSliderBinding binding;
    private final List<OleAppIntro> introList = new ArrayList<>();
    private OleAppIntroAdapter adapter;
    String userIpDetails = "", userModule = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityIntroSliderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        userIpDetails = Functions.getPrefValue(getContext(),Constants.kLoginType);
        userModule =  Functions.getPrefValue(getContext(), Constants.kUserModule);


        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerVu);
        binding.recyclerVu.addItemDecoration(new OleLinePagerIndicatorDecoration());

        adapter = new OleAppIntroAdapter(getContext(), introList);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnEmpLogin.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.btnSkip.setOnClickListener(this);

        getCarouselApi();

        if (!userIpDetails.isEmpty()){
            if (!userIpDetails.equalsIgnoreCase("otp")){
                binding.btnSkip.setVisibility(View.GONE);
            }else{
                binding.btnSkip.setVisibility(View.VISIBLE);
            }
        }


//        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); // checkx
//        countryCode = tm.getNetworkCountryIso();
//
//        if (!countryCode.equalsIgnoreCase("ae")){
//            binding.btnSkip.setVisibility(View.GONE);
//        }else {
//            binding.btnSkip.setVisibility(View.VISIBLE);
//        }
    }



    @Override
    public void onClick(View v) {
        if (v == binding.btnLogin) {
            Intent i = new Intent(IntroSliderActivity.this, LoginActivity.class);
            startActivity(i);
        }
        else if (v == binding.btnEmpLogin) {
            Intent i = new Intent(IntroSliderActivity.this, EmpLoginActivity.class);
            startActivity(i);
        }
        else if (v == binding.btnSkip) {
            skipClicked();
        }
    }

    private void skipClicked() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.kUserType, Constants.kPlayerType);
        editor.putString(Constants.kCurrency, getString(R.string.aed));
        editor.putString(Constants.kAppModule, Constants.kFootballModule);
        editor.remove(Constants.kUserID);
        editor.remove(Constants.kIsSignIn);
        editor.remove(Constants.kUserInfo);
        editor.apply();
        Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
        startActivity(intent);
        finish();
    }

    private void getCarouselApi() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getCarousel(Functions.getAppLang(getContext()));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            introList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                introList.add(gson.fromJson(arr.get(i).toString(), OleAppIntro.class));
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