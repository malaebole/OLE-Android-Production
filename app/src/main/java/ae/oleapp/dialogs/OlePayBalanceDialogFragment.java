package ae.oleapp.dialogs;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentPayBalanceDialogBinding;
import ae.oleapp.util.Functions;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class OlePayBalanceDialogFragment extends DialogFragment {

    private OlefragmentPayBalanceDialogBinding binding;
    private String type = "";
    private PayBalanceDialogFragmentCallback fragmentCallback;
    private String filePath = "";

    private EasyImage easyImage;
    private File file = new File("");

    public OlePayBalanceDialogFragment() {
        // Required empty public constructor
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFragmentCallback(PayBalanceDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPayBalanceDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (type.equalsIgnoreCase("payment")) {
            binding.tvTitle.setText(R.string.payment);
            binding.tvDesc.setText(R.string.enter_amount);
            binding.etAmount.setHint(R.string.amount);
            binding.receiptVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvTitle.setText(R.string.make_discount);
            binding.tvDesc.setText(R.string.enter_discount_value);
            binding.etAmount.setHint(R.string.discount);
            binding.receiptVu.setVisibility(View.GONE);
        }

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAmount.getText().toString().isEmpty()) {
                    fragmentCallback.enteredValue(OlePayBalanceDialogFragment.this, binding.etAmount.getText().toString(), type, filePath);
                }
            }
        });
        binding.imgVuReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptClicked();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void receiptClicked() {
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
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                filePath = resultUri.getPath();
                file = new File(resultUri.getPath());
                Glide.with(getContext()).load(file).into(binding.imgVuReceipt);
                //updatePhotoAPI(true, file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1,1)
//                                .start(getActivity());

                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(false)
                                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .start(getActivity());
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
//                        .pickPhoto(OlePayBalanceDialogFragment.this, 1121);
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
//                    Glide.with(getContext()).load(file).into(binding.imgVuReceipt);
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
//                            .start(getActivity(), OlePayBalanceDialogFragment.this);
//                }
//            }
//        }
//    }

    public interface PayBalanceDialogFragmentCallback {
        void enteredValue(DialogFragment dialogFragment, String value, String type, String filePath);
    }
}