package ae.oleapp.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivitySuggestJerseyBinding;
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

public class SuggestJerseyActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySuggestJerseyBinding binding;
    private EasyImage easyImage;
    private String photoFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuggestJerseyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        binding.btnClose.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.imgCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
        else if (view == binding.btnSubmit) {
            if (photoFilePath.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.add_shirt_photo), FancyToast.ERROR);
                return;
            }
            if (binding.etShirtName.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_shirt_name), FancyToast.ERROR);
                return;
            }
            if (binding.etCountry.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_country_name), FancyToast.ERROR);
                return;
            }
            if (binding.etYear.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_year), FancyToast.ERROR);
                return;
            }
            suggestJerseyApi(binding.etShirtName.getText().toString(), binding.etCountry.getText().toString(), binding.etYear.getText().toString());
        }
        else if (view == binding.imgCard) {
            pickImage();
        }
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
                easyImage.openChooser(SuggestJerseyActivity.this);
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
                photoFilePath = resultUri.getPath();
                Glide.with(getContext()).load(new File(photoFilePath)).into(binding.shirtImgVu);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        photoFilePath = mediaFiles[0].getFile().getPath();
                        Glide.with(getContext()).load(new File(photoFilePath)).into(binding.shirtImgVu);
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1,1)
//                                .start(SuggestJerseyActivity.this);
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

    private void suggestJerseyApi(String name, String country, String year) {
        KProgressHUD hud = Functions.showLoader(getContext());
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("shirt_photo", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.suggestJersey(filePart,
                RequestBody.create(Functions.getAppLang(getContext()), MediaType.parse("multipart/form-data")),
                RequestBody.create(Functions.getPrefValue(getContext(), Constants.kUserID), MediaType.parse("multipart/form-data")),
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(country, MediaType.parse("multipart/form-data")),
                RequestBody.create(year, MediaType.parse("multipart/form-data")));
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