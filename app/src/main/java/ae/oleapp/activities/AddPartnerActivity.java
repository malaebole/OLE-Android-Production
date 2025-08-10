package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddBankBinding;
import ae.oleapp.databinding.ActivityAddPartnerBinding;
import ae.oleapp.dialogs.BankAccountTypeDialog;
import ae.oleapp.models.AccountTypeModel;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.PartnerData;
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

public class AddPartnerActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddPartnerBinding binding;
    private PartnerData partnerData;
    private String clubId = "", partnerId="", photoFilePath = "";
    private EasyImage easyImage;
    private boolean update;
    private File file = new File("");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPartnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update =  bundle.getBoolean("is_update");
            clubId = bundle.getString("club_id", "");
            partnerId = bundle.getString("partner_id","");
        }
        getClubPartnersList(clubId, partnerId);



        binding.btnSubmit.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.partnerImgVu.setOnClickListener(this);
        binding.deletePartner.setOnClickListener(this);

        if (update){
            binding.deletePartner.setVisibility(View.VISIBLE);
        }else{
            binding.deletePartner.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (
                    binding.etName.getText().toString().isEmpty() ||
                            binding.etEmail.getText().toString().isEmpty() ||
                            binding.etPhoneNo.getText().toString().isEmpty() ||
                            binding.etShareValue.getText().toString().isEmpty()){
                Functions.showToast(getContext(), getString(R.string.fill_form_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            if (update){
                updatePartner(true,
                        binding.etName.getText().toString(),
                        binding.etEmail.getText().toString(),
                        binding.etPhoneNo.getText().toString(),
                        binding.etShareValue.getText().toString(), file);
            }else{
                addPartner(true,
                        binding.etName.getText().toString(),
                        binding.etEmail.getText().toString(),
                        binding.etPhoneNo.getText().toString(),
                        binding.etShareValue.getText().toString(), file);

            }



        }
        else if (v == binding.btnClose) {
            this.finish();
        }
        else if (v == binding.partnerImgVu) {
            updateInvoice();
        }

        else if (v == binding.deletePartner){
            deletePartnerClicked();
        }

    }

    private void deletePartnerClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.delete_partner ))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deletePartner(true, clubId, partnerId);
                        }
                    }
                }).show();
    }


    private void updateInvoice() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
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

    private void addPartner(boolean isLoader, String name, String email, String phone, String shareValue, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPartner(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), name),
                    RequestBody.create(MediaType.parse("multipart/form-data"), email),
                    RequestBody.create(MediaType.parse("multipart/form-data"), phone),
                    RequestBody.create(MediaType.parse("multipart/form-data"), shareValue));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.partner_added), FancyToast.SUCCESS);
                                finish();
                            } else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    }


    private void updatePartner(boolean isLoader, String name, String email, String phone, String shareValue, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePartner(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), partnerId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), name),
                    RequestBody.create(MediaType.parse("multipart/form-data"), email),
                    RequestBody.create(MediaType.parse("multipart/form-data"), phone),
                    RequestBody.create(MediaType.parse("multipart/form-data"), shareValue));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.partner_updated), FancyToast.SUCCESS);
                                finish();
                            } else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //file = new File(resultUri.getPath());
                photoFilePath = resultUri.getPath();
                file = new File(photoFilePath);
                Glide.with(getContext()).load(file).into(binding.partnerImgVu);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1, 1)
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

    private void getClubPartnersList(String clubId,String partnerId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubPartnersList(Functions.getAppLang(getContext()), clubId, partnerId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                partnerData = gson.fromJson(obj.toString(), PartnerData.class);
                                populateData(update);
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void deletePartner(Boolean isLoader, String clubId, String partnerId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.deletePartner(Functions.getAppLang(getContext()), clubId, partnerId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        Functions.hideLoader(hud);
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(),getString(R.string.partner_deleted), FancyToast.SUCCESS);
                                finish();
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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



    private void populateData(Boolean isUpdate){

        if (isUpdate){
            binding.title.setText(R.string.update);
            binding.btnTvAddUpdate.setText(R.string.update);
            binding.etName.setText(partnerData.getName());
            binding.etEmail.setText(partnerData.getEmail());
            binding.etPhoneNo.setText(partnerData.getPhone());
            binding.etShareValue.setText(partnerData.getShares());

            if (!partnerData.getPhotoUrl().isEmpty()){
                Glide.with(getContext()).load(partnerData.getPhotoUrl()).into(binding.partnerImgVu);
            }

        }else{
            binding.title.setText(getString(R.string.add_new_partner));
            binding.btnTvAddUpdate.setText(R.string.submit);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

    }
}