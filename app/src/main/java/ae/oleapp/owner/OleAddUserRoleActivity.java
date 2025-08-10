package ae.oleapp.owner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleUserPermissionAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityAddUserRoleBinding;
import ae.oleapp.models.OleUserPermission;
import ae.oleapp.models.OleUserRole;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleAddUserRoleActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAddUserRoleBinding binding;
    private OleUserPermissionAdapter adapter;
    private final List<OleUserPermission> permissionList = new ArrayList<>();
    private OleUserRole oleUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddUserRoleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.add_user_roles);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleUserPermissionAdapter(getContext(), permissionList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnDelete.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            oleUserRole = gson.fromJson(bundle.getString("data", ""), OleUserRole.class);
            populateData();
        }

        getPermissionsAPI(true);

        KeyboardVisibilityEvent.setEventListener(getContext(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    binding.btnAdd.setVisibility(View.GONE);
                }
                else {
                    binding.btnAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    private void populateData() {
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.tvBtnTitle.setText(R.string.update);
        binding.etTitle.setText(oleUserRole.getRoleName());
        adapter.selectedList = oleUserRole.getPermissions();
    }

    OleUserPermissionAdapter.ItemClickListener itemClickListener = new OleUserPermissionAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.setSelection(permissionList.get(pos));
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDelete) {
            deleteClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void deleteClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteUserRoleAPI(true, oleUserRole.getRoleId());
                        }
                    }
                }).show();
    }

    private void addClicked() {
        if (binding.etTitle.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_title), FancyToast.ERROR);
            return;
        }
        if (adapter.selectedList.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_permissions), FancyToast.ERROR);
            return;
        }
        String ids = "";
        for (OleUserPermission permission: adapter.selectedList) {
            if (ids.isEmpty()) {
                ids = permission.getId();
            } else {
                ids = String.format("%s,%s", ids, permission.getId());
            }
        }
        if (oleUserRole == null) {
            addUserRoleAPI(true, binding.etTitle.getText().toString(), ids, "");
        }
        else {
            addUserRoleAPI(true, binding.etTitle.getText().toString(), ids, oleUserRole.getRoleId());
        }
    }

    private void getPermissionsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPermissions(Functions.getAppLang(getContext()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            permissionList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                permissionList.add(gson.fromJson(arr.get(i).toString(), OleUserPermission.class));
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

    private void addUserRoleAPI(boolean isLoader, String title, String ids, String roleId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addUserRole(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), title, ids, roleId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void deleteUserRoleAPI(boolean isLoader, String roleId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteUserRole(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), roleId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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