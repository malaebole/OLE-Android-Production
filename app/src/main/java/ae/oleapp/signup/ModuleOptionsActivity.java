package ae.oleapp.signup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityModuleOptionsBinding;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OlePlayerMainTabsActivity;
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

public class ModuleOptionsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityModuleOptionsBinding binding;
    private String appModule = "";
    private EasyImage easyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityModuleOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        binding.relFootball.setOnClickListener(this);
        binding.relPadel.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.btnChange.setOnClickListener(this);
        binding.btnContinue.setAlpha(0.5f);

        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo != null) {
            Glide.with(getApplicationContext()).load(userInfo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.imgVu);
            binding.tvName.setText(String.format("%s %s", getString(R.string.hello), userInfo.getFirstName()));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relFootball) {
            appModule = Constants.kFootballModule;
            binding.imgFootball.setImageResource(R.drawable.football_active);
            binding.imgPadel.setImageResource(R.drawable.padel_inactive);
            binding.relFootball.setBackgroundResource(R.drawable.user_type_selected);
            binding.relPadel.setBackgroundResource(R.drawable.user_type_unselected);
            binding.tvFootball.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.tvPadel.setTextColor(getResources().getColor(R.color.darkTextColor));
            binding.btnContinue.setAlpha(1.0f);
        }
        else if (v == binding.relPadel) {
            appModule = Constants.kPadelModule;
            binding.imgFootball.setImageResource(R.drawable.football_inactive);
            binding.imgPadel.setImageResource(R.drawable.padel_active);
            binding.relFootball.setBackgroundResource(R.drawable.user_type_unselected);
            binding.relPadel.setBackgroundResource(R.drawable.user_type_selected);
            binding.tvFootball.setTextColor(getResources().getColor(R.color.darkTextColor));
            binding.tvPadel.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.btnContinue.setAlpha(1.0f);
        }
        else if (v == binding.btnContinue) {
            if (appModule.equalsIgnoreCase(Constants.kFootballModule)) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.kAppModule, Constants.kFootballModule);
                editor.apply();
                Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                startActivity(intent);
                finish();
            }
            else if (appModule.equalsIgnoreCase(Constants.kPadelModule)) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.kAppModule, Constants.kPadelModule);
                editor.apply();
                Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else if (v == binding.btnChange) {
            photoChangeClicked();
        }
    }

    private void photoChangeClicked() {
        pickImage();
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
                easyImage.openChooser(ModuleOptionsActivity.this);
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
                File file = new File(resultUri.getPath());
                Glide.with(getApplicationContext()).load(file).into(binding.imgVu);
                updatePhotoAPI(true, file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
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
                                .start(ModuleOptionsActivity.this);
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

    private void updatePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
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
                            Glide.with(getApplicationContext()).load(url).into(binding.imgVu);
                            UserInfo userInfo = Functions.getUserinfo(getContext());
                            userInfo.setPhotoUrl(url);
                            Functions.saveUserinfo(getContext(), userInfo);
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