package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.AssignCountryListAdapter;
import ae.oleapp.adapters.AssignedCountryAdapter;
import ae.oleapp.adapters.PlayerListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityLineupGlobalBinding;
import ae.oleapp.databinding.ActivityMainBinding;
import ae.oleapp.models.AssignedCountries;
import ae.oleapp.models.Country;
import ae.oleapp.models.FieldImage;
import ae.oleapp.models.Team;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LineupGlobalActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLineupGlobalBinding binding;
    private final List<AssignedCountries> assignedCountries = new ArrayList<>();
    private AssignedCountryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLineupGlobalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        getAssignedCountries(true);

        GridLayoutManager playerLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(playerLayoutManager);
        adapter = new AssignedCountryAdapter(getContext(), assignedCountries);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.backBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        }

    }

    AssignedCountryAdapter.ItemClickListener itemClickListener = new AssignedCountryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            //selectedCountryId =
            //countryClicked(assignedCountries.get(pos).getId());
            Intent intent = new Intent(getContext(), LineupGlobalTeamsActivity.class);
            intent.putExtra("country_id", assignedCountries.get(pos).getId());
            intent.putExtra("country_name", assignedCountries.get(pos).getName());
            startActivity(intent);

        }
    };

    private void getAssignedCountries(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getAssignedCountries();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray countries = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            assignedCountries.clear();
                            for (int i = 0; i < countries.length(); i++) {
                                assignedCountries.add(gson.fromJson(countries.get(i).toString(), AssignedCountries.class));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
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

    @Override
    protected void onResume() {
        super.onResume();
        getAssignedCountries(false);
    }
}