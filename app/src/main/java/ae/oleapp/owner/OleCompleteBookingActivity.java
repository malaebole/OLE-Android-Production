package ae.oleapp.owner;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCompleteBookingBinding;
import ae.oleapp.util.Functions;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class OleCompleteBookingActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCompleteBookingBinding binding;
    private String extraTime = "";
    private String price = "";
    private String currency = "";
    private String duration = "";
    private String filePath = "";

    private EasyImage easyImage;
    private File file = new File("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCompleteBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.complete_booking);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            price = bundle.getString("price", "");
            currency = bundle.getString("currency", "");
            duration = bundle.getString("duration", "");
        }

        binding.tvDuration.setText(duration);
        binding.tvPrice.setText(String.format("%s %s", price, currency));

        binding.bar.backBtn.setOnClickListener(this);
        binding.card30Minute.setOnClickListener(this);
        binding.card60Minute.setOnClickListener(this);
        binding.btnComplete.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discountTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        } else if (v == binding.btnComplete) {
            completeClicked();
        } else if (v == binding.imgVuReceipt) {
            receiptClicked();
        } else if (v == binding.card30Minute) {
            if (extraTime.equalsIgnoreCase("30")) {
                extraTime = "";
                binding.card30Minute.setCardBackgroundColor(Color.WHITE);
                binding.card30Minute.setStrokeColor(getResources().getColor(R.color.separatorColor));
                binding.tv30.setTextColor(getResources().getColor(R.color.darkTextColor));
            } else {
                extraTime = "30";
                binding.card30Minute.setCardBackgroundColor(getResources().getColor(R.color.blueColorNew));
                binding.card30Minute.setStrokeColor(Color.TRANSPARENT);
                binding.tv30.setTextColor(getResources().getColor(R.color.whiteColor));
                binding.card60Minute.setCardBackgroundColor(Color.WHITE);
                binding.card60Minute.setStrokeColor(getResources().getColor(R.color.separatorColor));
                binding.tv60.setTextColor(getResources().getColor(R.color.darkTextColor));
            }
        } else if (v == binding.card60Minute) {
            if (extraTime.equalsIgnoreCase("60")) {
                extraTime = "";
                binding.card60Minute.setCardBackgroundColor(Color.WHITE);
                binding.card60Minute.setStrokeColor(getResources().getColor(R.color.separatorColor));
                binding.tv60.setTextColor(getResources().getColor(R.color.darkTextColor));
            } else {
                extraTime = "60";
                binding.card60Minute.setCardBackgroundColor(getResources().getColor(R.color.blueColorNew));
                binding.card60Minute.setStrokeColor(Color.TRANSPARENT);
                binding.tv60.setTextColor(getResources().getColor(R.color.whiteColor));
                binding.card30Minute.setCardBackgroundColor(Color.WHITE);
                binding.card30Minute.setStrokeColor(getResources().getColor(R.color.separatorColor));
                binding.tv30.setTextColor(getResources().getColor(R.color.darkTextColor));
            }
        }
    }


    private void discountTextChanged() {
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(String.format("%s %s", price, currency));
        } else {
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(price) - val;
            if (val > Double.parseDouble(price)) {
                binding.etDiscount.setText(price);
                binding.tvPrice.setText(String.format("%s %s", "0", currency));
            } else {
                binding.tvPrice.setText(String.format("%s %s", total, currency));
            }
        }
    }

//    private void receiptClicked() {
//        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                FilePickerBuilder.getInstance()
//                        .setMaxCount(1)
//                        .setActivityTheme(R.style.AppThemePlayer)
//                        .setActivityTitle(getString(R.string.image))
//                        .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
//                        .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
//                        .enableVideoPicker(false)
//                        .enableCameraSupport(true)
//                        .showGifs(false)
//                        .showFolderView(false)
//                        .enableSelectAll(false)
//                        .enableImagePicker(true)
//                        .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                        .pickPhoto(OleCompleteBookingActivity.this, 1121);
//            }
//        });
//    }

    private void receiptClicked() {
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                filePath = resultUri.getPath();
                file = new File(filePath);
                Glide.with(getApplicationContext()).load(file).into(binding.imgVuReceipt);
                //updatePhotoAPI(true, file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            if (easyImage != null) {
                easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                    @Override
                    public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                        if (mediaFiles.length > 0) {
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1, 1)
//                                .start(getContext());

                            CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                                    .setFixAspectRatio(false)
                                    .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                    .start(getContext());
                        }
                    }

                    @Override
                    public void onImagePickerError(Throwable error, MediaSource source) {
                        Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                    }

                    @Override
                    public void onCanceled(@NonNull MediaSource mediaSource) {
                        //canceled
                    }
                });
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                File file = new File(resultUri.getPath());
//                try {
//                    file = new Compressor(getContext())
//                            .setMaxHeight(1920).setMaxWidth(1080)
//                            .setQuality(75)
//                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                            .compressToFile(file);
//                    filePath = file.getAbsolutePath();
//                    Glide.with(getApplicationContext()).load(new File(filePath)).into(binding.imgVuReceipt);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Functions.showToast(getContext(), "Error in image compression", FancyToast.ERROR);
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//        else {
//            if (resultCode == RESULT_OK && requestCode == 1121) {
//                ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
//                if (dataList != null && dataList.size() > 0) {
//                    Uri uri = dataList.get(0);
//                    CropImage.activity(uri)
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                            .start(getContext());
//                }
//            }
//        }
//    }

    private void completeClicked() {
        Intent intent = new Intent();
        intent.putExtra("note", binding.etNote.getText().toString());
        intent.putExtra("discount", binding.etDiscount.getText().toString());
        intent.putExtra("invoiceNo", binding.etInvoiceNo.getText().toString());
        intent.putExtra("balance", binding.etBalance.getText().toString());
        intent.putExtra("extraTime", extraTime);
        intent.putExtra("price", binding.etPrice.getText().toString());
        intent.putExtra("posPayment", binding.etPosPayment.getText().toString());
        intent.putExtra("filePath", filePath);
        setResult(RESULT_OK, intent);
        finish();
    }
}