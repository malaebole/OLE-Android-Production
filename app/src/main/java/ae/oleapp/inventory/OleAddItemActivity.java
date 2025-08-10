package ae.oleapp.inventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAddItemBinding;
import ae.oleapp.models.OleInventoryItem;
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

public class OleAddItemActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAddItemBinding binding;
    private String clubId = "";
    private File photoFile = null;
    private EasyImage easyImage;
    private OleInventoryItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.add_item);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            item = new Gson().fromJson(bundle.getString("item", ""), OleInventoryItem.class);
        }

        if (item != null) {
            Glide.with(getContext()).load(item.getPhoto()).into(binding.imgVu);
            binding.etItemName.setText(item.getName());
            binding.etPurchasePrice.setText(item.getPurchasedPrice());
            binding.etSalePrice.setText(item.getSalePrice());
            binding.tvStock.setText(String.format("%s: %s", getString(R.string.current_stock), item.getCurrentStock()));
            binding.tvQty.setText(R.string.new_qty);
            binding.etQty.setHint(R.string.enter_new_qty);
            binding.tvAdd.setText(R.string.update);
        }
        else {
            binding.tvStock.setVisibility(View.GONE);
            binding.tvAdd.setText(R.string.add_item);
        }

        binding.bar.backBtn.setOnClickListener(this);
        binding.photoVu.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.photoVu) {
            pickImage();
        }
        else if (v == binding.btnAdd) {
            if (binding.etItemName.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_item_name), FancyToast.ERROR);
                return;
            }
            if (binding.etPurchasePrice.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_purchase_price), FancyToast.ERROR);
                return;
            }
            if (binding.etSalePrice.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_sale_price), FancyToast.ERROR);
                return;
            }
            if (item == null && binding.etQty.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_qty), FancyToast.ERROR);
                return;
            }
            if (item == null) {
                addItemAPI(true, binding.etItemName.getText().toString(), binding.etPurchasePrice.getText().toString(), binding.etSalePrice.getText().toString(), binding.etQty.getText().toString());
            }
            else {
                updateItemAPI(true, binding.etItemName.getText().toString(), binding.etPurchasePrice.getText().toString(), binding.etSalePrice.getText().toString(), binding.etQty.getText().toString(), item.getId());
            }
        }
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
                public void onImagePickerError(@NonNull Throwable throwable, @NonNull MediaSource mediaSource) {

                }

                @Override
                public void onMediaFilesPicked(@NonNull MediaFile[] mediaFiles, @NonNull MediaSource mediaSource) {
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
                public void onCanceled(@NonNull MediaSource mediaSource) {

                }
            });
        }
    }

    private void addItemAPI(boolean isLoader, String name, String purchasePrice, String salePrice, String stock) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (photoFile != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            filePart = MultipartBody.Part.createFormData("item_photo", photoFile.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addItem(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), purchasePrice),
                RequestBody.create(MediaType.parse("text/plain"), salePrice),
                RequestBody.create(MediaType.parse("text/plain"), stock));
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

    private void updateItemAPI(boolean isLoader, String name, String purchasePrice, String salePrice, String stock, String id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (photoFile != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            filePart = MultipartBody.Part.createFormData("item_photo", photoFile.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateItem(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), purchasePrice),
                RequestBody.create(MediaType.parse("text/plain"), salePrice),
                RequestBody.create(MediaType.parse("text/plain"), stock),
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
}