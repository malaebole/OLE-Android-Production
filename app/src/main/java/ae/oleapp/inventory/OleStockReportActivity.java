package ae.oleapp.inventory;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import ae.oleapp.adapters.OleStockReportAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityStockReportBinding;
import ae.oleapp.dialogs.OleUpdateStockDialogFragment;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleStockReportActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityStockReportBinding binding;
    private String clubId = "";
    private final List<OleInventoryItem> itemList = new ArrayList<>();
    private OleStockReportAdapter adapter;
    private boolean isOwnerLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityStockReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.stock);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleStockReportAdapter(getContext(), itemList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        getStockReportAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
    }

    OleStockReportAdapter.ItemClickListener itemClickListener = new OleStockReportAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (isOwnerLogin) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("UpdateStockDialogFragment");
                if (fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
                fragmentTransaction.addToBackStack(null);
                OleUpdateStockDialogFragment dialogFragment = new OleUpdateStockDialogFragment(itemList.get(pos).getName(), clubId, itemList.get(pos).getId());
                dialogFragment.setCancelable(false);
                dialogFragment.setFragmentCallback(new OleUpdateStockDialogFragment.UpdateStockDialogFragmentCallback() {
                    @Override
                    public void stockUpdated(DialogFragment df) {
                        df.dismiss();
                        getStockReportAPI(false);
                    }
                });
                dialogFragment.show(fragmentTransaction, "UpdateStockDialogFragment");
            }
        }
    };

    private void getStockReportAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getStockReport(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            itemList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                itemList.add(gson.fromJson(array.get(i).toString(), OleInventoryItem.class));
                            }
                            isOwnerLogin = object.getString("is_owner_login").equalsIgnoreCase("1");
                        }
                        else {
                            itemList.clear();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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