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
import ae.oleapp.adapters.OleHideFieldsAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityHiddenFieldsBinding;
import ae.oleapp.models.OleHideField;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleHiddenFieldsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityHiddenFieldsBinding binding;
    private OleHideFieldsAdapter adapter;
    private final List<OleHideField> oleHideFields = new ArrayList<>();
    private String clubId = "", fieldId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityHiddenFieldsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.hidden_fields);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            fieldId = bundle.getString("field_id", "");
        }

        binding.noDataVu.setVisibility(View.GONE);
        binding.btnHide.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleHideFieldsAdapter(getContext(), oleHideFields);
        adapter.setItemClickListener(new OleHideFieldsAdapter.ItemClickListener() {
            @Override
            public void itemClicked(View view, int pos) {
                Intent intent = new Intent(getContext(), OleHideFieldActivity.class);
                intent.putExtra("club_id", clubId);
                intent.putExtra("field_id", fieldId);
                intent.putExtra("is_update", true);
                intent.putExtra("date", oleHideFields.get(pos).getDate());
                intent.putExtra("slot_ids", oleHideFields.get(pos).getHiddenIds());
                intent.putExtra("type", oleHideFields.get(pos).getHiddenType());
                startActivity(intent);
            }
        });
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnHide.setOnClickListener(this);
        binding.btnHideField.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHiddenFieldsAPI(oleHideFields.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnHide || v == binding.btnHideField) {
            hideClicked();
        }
    }

    private void hideClicked() {
        Intent intent = new Intent(getContext(), OleHideFieldActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("field_id", fieldId);
        intent.putExtra("is_update", false);
        intent.putExtra("date", "");
        startActivity(intent);
    }

    private void getHiddenFieldsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.hiddenFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, fieldId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleHideFields.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleHideFields.add(gson.fromJson(arr.get(i).toString(), OleHideField.class));
                            }
                            adapter.notifyDataSetChanged();
                            if (oleHideFields.isEmpty()) {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                                binding.btnHide.setVisibility(View.GONE);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.GONE);
                                binding.btnHide.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            oleHideFields.clear();
                            adapter.notifyDataSetChanged();
                            binding.noDataVu.setVisibility(View.VISIBLE);
                            binding.btnHide.setVisibility(View.GONE);
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
