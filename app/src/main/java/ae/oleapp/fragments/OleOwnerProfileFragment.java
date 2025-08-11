package ae.oleapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.HashMap;

import ae.oleapp.R;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentOwnerProfileBinding;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleEmpMyRatingsActivity;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.player.OleEditPlayerActivity;
import ae.oleapp.signup.VerifyPhoneActivity;
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

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleOwnerProfileFragment extends BaseFragment implements View.OnClickListener {

    private UserInfo userInfo;
    private EasyImage easyImage;
    private OlefragmentOwnerProfileBinding binding;

    public OleOwnerProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentOwnerProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.btnRatings.setVisibility(View.GONE);

        getProfileAPI(userInfo == null);

        binding.btnVerify.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.btnChange.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.btnRatings.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBadgeValue();
        userInfo = Functions.getUserinfo(getContext());
        populateData();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnVerify) {
            verifyClicked();
        }
        else if (v == binding.btnChange) {
            photoChangeClicked();
        }
        else if (v == binding.btnUpdate) {
            updateClicked();
        }
        else if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.btnRatings) {
            Intent intent = new Intent(getContext(), OleEmpMyRatingsActivity.class);
            startActivity(intent);
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    private void menuClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void verifyClicked() {
        Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
        intent.putExtra("is_update", true);
        intent.putExtra("phone", userInfo.getPhone());
        startActivity(intent);
    }

    private void updateClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (Functions.isArabic(binding.etName.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.enter_name_english), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }

        updateProfileAPI(true, binding.etName.getText().toString(), binding.etPhone.getText().toString(), binding.etEmail.getText().toString());
    }

    private void photoChangeClicked() {
        ActionSheet.createBuilder(getContext(), getChildFragmentManager())
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
                            deletePhotoAPI(true);
                        }
                    }
                }).show();
    }

    private void pickImage() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else {
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
                easyImage.openChooser(getActivity());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(resultUri.getPath());
                Glide.with(requireActivity()).load(file).into(binding.imgVu);
                updatePhotoAPI(true, file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1,1)
                                .start(getActivity());
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

    private void populateData() {
        if (userInfo == null) {
            return;
        }
        Glide.with(this).load(userInfo.getPhotoUrl()).placeholder(R.drawable.owner_active).into(binding.imgVu);
        binding.etName.setText(userInfo.getName());
        binding.etEmail.setText(userInfo.getEmail());
        binding.etPhone.setText(userInfo.getPhone());
        if (userInfo.getPhoneVerified().equalsIgnoreCase("1")) {
            binding.btnVerify.setVisibility(View.GONE);
        }
        else {
            binding.btnVerify.setVisibility(View.VISIBLE);
        }
        if (userInfo.getIsEmployee() != null && userInfo.getIsEmployee().equalsIgnoreCase("1")) {
            binding.btnRatings.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnRatings.setVisibility(View.GONE);
        }
    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),"", "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            Functions.saveUserinfo(getContext(), userInfo);
                            populateData();
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

    private void deletePhotoAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deletePhoto(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.imgVu.setImageResource(R.drawable.owner_active);
                            UserInfo userInfo = Functions.getUserinfo(getContext());
                            userInfo.setPhotoUrl("");
                            Functions.saveUserinfo(getContext(), userInfo);
                            if (getActivity() instanceof OleOwnerMainTabsActivity) {
                                ((OleOwnerMainTabsActivity)getActivity()).populateSideMenuData();
                            }
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

    private void updatePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateProfilePhoto(part, RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())), RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getPrefValue(getContext(), Constants.kUserID)));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            String url = object.getJSONObject(Constants.kData).getString("photo_url");
                            Glide.with(requireActivity()).load(url).into(binding.imgVu);
                            UserInfo userInfo = Functions.getUserinfo(getContext());
                            userInfo.setPhotoUrl(url);
                            Functions.saveUserinfo(getContext(), userInfo);
                            if (getActivity() instanceof OleOwnerMainTabsActivity) {
                                ((OleOwnerMainTabsActivity)getActivity()).populateSideMenuData();
                            }
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

    private void updateProfileAPI(boolean isLoader, String name, String phone, String email) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        HashMap<String, String> dynamicParams = new HashMap<>();
        dynamicParams.put("user_id", Functions.getPrefValue(getContext(), Constants.kUserID));
        dynamicParams.put("name", name);
        if (!email.equalsIgnoreCase(userInfo.getEmail())) {
            dynamicParams.put("email", email);
        }
        if (!phone.equalsIgnoreCase(userInfo.getPhone())) {
            dynamicParams.put("phone", phone);
        }

        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateUser(Functions.getAppLang(getContext()), dynamicParams);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (!phone.equalsIgnoreCase(userInfo.getPhone())) {
                                Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
                                intent.putExtra("is_update", true);
                                intent.putExtra("phone", phone);
                                startActivity(intent);
                                userInfo.setPhoneVerified("0");
                            }
                            userInfo.setName(name);
                            userInfo.setPhone(phone);
                            userInfo.setEmail(email);
                            Functions.saveUserinfo(getContext(), userInfo);

                            if (getActivity() instanceof OleOwnerMainTabsActivity) {
                                ((OleOwnerMainTabsActivity)getActivity()).populateSideMenuData();
                            }
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
