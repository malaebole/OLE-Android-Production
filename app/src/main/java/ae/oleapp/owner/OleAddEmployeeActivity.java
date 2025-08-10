package ae.oleapp.owner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.AssignCountryActivity;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.PlayerGridAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityAddEmployeeBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.Employee;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.OleUserRole;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleAddEmployeeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAddEmployeeBinding binding;
    private final List<OleUserRole> oleUserRoles = new ArrayList<>();
    private String roleId = "", status = "", clubId = "",name = "", selectedLineupEmployeeId="";
    private Employee employee = null;
    private OleRankClubAdapter clubAdapter;
    private EasyImage easyImage;
    private File photoFile;
    private PlayerGridAdapter adapter;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding.toolbarTitle.setText(R.string.employee);
        makeStatusbarTransperant();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            employee = gson.fromJson(bundle.getString("data", ""), Employee.class);
            status = bundle.getString("status", "");
            clubId = bundle.getString("club_id", "");
        }

        if (AppManager.getInstance().clubs.size() == 1) {
            binding.clubVu.setVisibility(View.GONE);
            Club club = AppManager.getInstance().clubs.get(0);
            clubId = club.getId();
        }
        else {
            binding.clubVu.setVisibility(View.VISIBLE);
        }

        for (int i =0; i<AppManager.getInstance().clubs.size(); i++){
            if (clubId.equalsIgnoreCase(AppManager.getInstance().clubs.get(i).getId())){
                index = i;
            }
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(layoutManager);
        clubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, index, false);
        clubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(clubAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(gridLayoutManager);
        adapter = new PlayerGridAdapter(getContext(), playerList, false, false);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);


        if (employee == null) {
            binding.tvBtnTitle.setText(R.string.add_now);
        }
        else {
            binding.tvBtnTitle.setText(R.string.update);
            populateData();
        }

        binding.backBtn.setOnClickListener(this);
        binding.etRole.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.relAddRole.setOnClickListener(this);
        binding.photoVu.setOnClickListener(this);
        binding.cardVuGeneral.setOnClickListener(this);
        binding.cardVuLineup.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);

        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    name = query;
                    //pageNo = 1;
                    getPlayerListAPI(true);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.text_should_3_charachters), FancyToast.ERROR);
                }
                binding.searchVu.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                name = binding.searchVu.getQuery().toString();
                if (binding.searchVu.getQuery().toString().equalsIgnoreCase("")){
                    //pageNo = 1;
                    getPlayerListAPI(false);
                }
                return true;
            }
        });

        binding.cardVuLineup.setEnabled(!status.equalsIgnoreCase("old"));



    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            clubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
        }
    };
    PlayerGridAdapter.ItemClickListener itemClickListener = new PlayerGridAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.checkEmployee(true, playerList.get(pos).getId());
            selectedLineupEmployeeId = playerList.get(pos).getId();
        }
    };


    private void populateData() {
        Glide.with(getContext()).load(employee.getEmployeePhoto()).into(binding.imgVu);
        binding.etName.setText(employee.getName());
        binding.etPhone.setText(employee.getPhone());
        binding.etEmpId.setText(employee.getEmployeeId());
        binding.etEmail.setText(employee.getEmail());
        binding.etPass.setText("");
        roleId = employee.getRoleId();
        binding.etRole.setText(employee.getRoleName());
        binding.etSalary.setText(employee.getSalary());
        clubId = employee.getClubId();
        for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
            if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                clubAdapter.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserRolesAPI(oleUserRoles.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn) {
            finish();
        }
        else if (v == binding.etRole) {
            roleClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();

        } else if (v == binding.btnContinue) {
            if (selectedLineupEmployeeId.isEmpty()){
                Functions.showToast(getContext(), "Please Search and Select Player First!", FancyToast.ERROR);
                return;
            }
            continueClicked();

        } else if (v == binding.relAddRole) {
            addRoleClicked();
        }
        else if (v == binding.photoVu) {
            photoClicked();
        } else if (v == binding.cardVuGeneral) {
            cardVuGeneralClicked();

        } else if (v == binding.cardVuLineup) {
            cardVuLineupClicked();
        }
    }




    private void cardVuGeneralClicked() {
        binding.cardVuGeneral.setStrokeColor(Color.parseColor("#ffe200"));
        binding.cardVuLineup.setStrokeColor(Color.parseColor("#0E5BAC"));
        binding.chooseVu.setVisibility(View.GONE);
        binding.recyclerVu.setVisibility(View.GONE);
        binding.btnContinue.setVisibility(View.GONE);
        binding.photoVu.setVisibility(View.VISIBLE);
        binding.scrollVu.setVisibility(View.VISIBLE);
        binding.btnAdd.setVisibility(View.VISIBLE);

    }


    private void cardVuLineupClicked() {
        binding.cardVuGeneral.setStrokeColor(Color.parseColor("#0E5BAC"));
        binding.cardVuLineup.setStrokeColor(Color.parseColor("#ffe200"));
        binding.chooseVu.setVisibility(View.VISIBLE);
        binding.recyclerVu.setVisibility(View.VISIBLE);
        binding.btnContinue.setVisibility(View.VISIBLE);
        binding.photoVu.setVisibility(View.GONE);
        binding.scrollVu.setVisibility(View.GONE);
        binding.btnAdd.setVisibility(View.GONE);



    }



    private void photoClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                                pickImage();
                        }
                        else if (index == 1) {
                            if (employee != null) {
                                deletePhotoAPI(true, employee.getId());
                            }
                            else {
                                photoFile = null;
                                binding.imgVu.setImageResource(0);
                                binding.imgVu.setImageDrawable(null);
                            }
                        }
                    }
                }).show();
    }

    private void pickImage() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else{
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                easyImage = new EasyImage.Builder(getContext())
                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                        .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false).build();
                easyImage.openChooser(getContext());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                photoFile = new File(resultUri.getPath());
                Glide.with(getContext()).load(photoFile).into(binding.imgVu);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1,1)
                                .start(getContext());
                    }
                }

                @Override
                public void onImagePickerError(Throwable error, MediaSource source) {
                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                }

                @Override
                public void onCanceled(@NonNull MediaSource mediaSource) {

                }
            });
        }
    }

    private void roleClicked() {
        if (oleUserRoles.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.add_user_roles), FancyToast.ERROR);
            return;
        }
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleUserRole oleUserRole : oleUserRoles) {
            oleSelectionList.add(new OleSelectionList(oleUserRole.getRoleId(), oleUserRole.getRoleName()));
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_user_role), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                roleId = selectedItem.getId();
                binding.etRole.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    private void addClicked() {
        if (clubId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
            return;
        }
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        if (binding.etEmpId.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_employee_id), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
        if (employee == null && binding.etPass.getText().toString().length()<6) {
            Functions.showToast(getContext(), getString(R.string.invalid_pass), FancyToast.ERROR);
            return;
        }
        if (roleId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_user_role), FancyToast.ERROR);
            return;
        }
        if (binding.etSalary.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), "Please Enter Salary!", FancyToast.ERROR);
            return;
        }
        if (employee == null) {
            addEmployeeAPI(true, binding.etName.getText().toString(),binding.etSalary.getText().toString(), binding.etPhone.getText().toString(), binding.etEmail.getText().toString(), binding.etPass.getText().toString(), binding.etEmpId.getText().toString(), "");
        }
        else {
            addEmployeeAPI(true, binding.etName.getText().toString(),binding.etSalary.getText().toString(), binding.etPhone.getText().toString(), binding.etEmail.getText().toString(), binding.etPass.getText().toString(), binding.etEmpId.getText().toString(), employee.getId());
        }
    }

    private void continueClicked() {

            Intent intent = new Intent(getContext(), AssignCountryActivity.class);
            intent.putExtra("update",false);
            intent.putExtra("emp_id",selectedLineupEmployeeId);
            intent.putExtra("club_id",clubId);
            startActivity(intent);
            finish();

    }

    private void addRoleClicked() {
        Intent intent = new Intent(getContext(), OleAddUserRoleActivity.class);
        startActivity(intent);
    }

    private void addEmployeeAPI(boolean isLoader, String name, String salary, String phone, String email, String pass, String empId, String id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (photoFile != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            filePart = MultipartBody.Part.createFormData("employee_photo", photoFile.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addEmployee(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), phone),
                RequestBody.create(MediaType.parse("text/plain"), email),
                RequestBody.create(MediaType.parse("text/plain"), pass),
                RequestBody.create(MediaType.parse("text/plain"), roleId),
                RequestBody.create(MediaType.parse("text/plain"), empId),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), salary),
                RequestBody.create(MediaType.parse("text/plain"), id));
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
                        }
                        else {
                            oleUserRoles.clear();
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
    private void getPlayerListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerList(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, 1, name, "", "", "", "", "", "", "", "lineup");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
//                            if (pageNo == 1) {
                            playerList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                playerList.add(gson.fromJson(arr.get(i).toString(), PlayerInfo.class));
                            }
//                            }else{
//                                List<PlayerInfo> more = new ArrayList<>();
//                                for (int i = 0; i < arr.length(); i++) {
//                                    more.add(gson.fromJson(arr.get(i).toString(), PlayerInfo.class));
//                                }
//
//                                if (more.size() > 0) {
//                                    playerList.addAll(more);
//                                }
//                                else {
//                                    pageNo = pageNo-1;
//                                }
//                            }
                            if (playerList.size() == 0) {
                                Functions.showToast(getContext(), getString(R.string.player_not_found), FancyToast.ERROR);
                            }
//                            playerList.clear();
//                            for (int i = 0; i < arr.length(); i++) {
//                                playerList.add(gson.fromJson(arr.get(i).toString(), PlayerInfo.class));
//                            }
                        }
                        else {
                            playerList.clear();
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


    private void deletePhotoAPI(boolean isLoader, String empId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deletePhoto(Functions.getAppLang(getContext()), empId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.imgVu.setImageResource(0);
                            binding.imgVu.setImageDrawable(null);
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