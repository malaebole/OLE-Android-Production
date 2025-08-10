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
import ae.oleapp.adapters.OleUserRolesAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityUserRolesBinding;
import ae.oleapp.models.OleUserRole;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleUserRolesActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityUserRolesBinding binding;
    private OleUserRolesAdapter adapter;
    private final List<OleUserRole> oleUserRoles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityUserRolesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.user_roles);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleUserRolesAdapter(getContext(), oleUserRoles);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    OleUserRolesAdapter.ItemClickListener itemClickListener = new OleUserRolesAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleAddUserRoleActivity.class);
            Gson gson = new Gson();
            intent.putExtra("data", gson.toJson(oleUserRoles.get(pos)));
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getUserRolesAPI(oleUserRoles.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void addClicked() {
        Intent intent = new Intent(getContext(), OleAddUserRoleActivity.class);
        startActivity(intent);
    }

    private void getUserRolesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserRole(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleUserRoles.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleUserRoles.add(gson.fromJson(arr.get(i).toString(), OleUserRole.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            oleUserRoles.clear();
                            adapter.notifyDataSetChanged();
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