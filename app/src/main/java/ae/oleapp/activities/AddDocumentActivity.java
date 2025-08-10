package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddDocumentBinding;

import ae.oleapp.models.DocDetails;
import ae.oleapp.models.DocList;
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

public class AddDocumentActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddDocumentBinding binding;
    private boolean update, pdf;

    private EasyImage easyImage;
    private File file = new File("");

    private DocList docLists;
    private DocDetails docDetails;
    private String clubId = "", typeID="",type="",fileId="", photoFilePath = "";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_PICK_PDF = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update =  bundle.getBoolean("is_update");
            docDetails = new Gson().fromJson(bundle.getString("docDetails", ""), DocDetails.class);
            fileId = bundle.getString("fileId","");
            type = bundle.getString("type","");
            typeID = bundle.getString("typeId","");
            clubId = bundle.getString("club_id","");
        }

        if (update){
            getFile(fileId,clubId);
        }else{
            populateData(update);
        }

        binding.btnSubmit.setOnClickListener(this);
        binding.fileImgVu.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.etIssueDate.setOnClickListener(this);
        binding.etExpiryDate.setOnClickListener(this);
        binding.deleteFile.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit){
            if (binding.etFileName.getText().toString().isEmpty()){
                Functions.showToast(getContext(), getString(R.string.document_cannot_empty), FancyToast.SUCCESS);
                return;
            }
            if (update){
                updateRecord();
            }
            else{
                addRecord();
            }
        }
        else if (v == binding.fileImgVu) {

            showActionSheet();

        }
        else if (v == binding.btnClose) {
            finish();
        }
        else if (v == binding.etIssueDate) {
            DateClicked("issue");
        }
        else if (v == binding.etExpiryDate) {
            DateClicked("expiry");
        }
        else if (v == binding.deleteFile) {
            deleteFileClicked();
        }
    }

    private void showActionSheet() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.choose_photo),getString(R.string.choose_pdf))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            pdf = false;
                            updateInvoice();
                        }
                        if (index == 1){
                            pdf = true;
                            choosePdf();
                        }
                    }
                }).show();
    }

    private void choosePdf() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_pdf)), REQUEST_PICK_PDF);
    }
    private void updateInvoice() {
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


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //file = new File(resultUri.getPath());

                photoFilePath = resultUri.getPath();
                file = new File(photoFilePath);
                Glide.with(getContext()).load(file).into(binding.fileImgVu);
                Functions.showToast(getContext(),getString(R.string.image_uploaded), FancyToast.SUCCESS);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == REQUEST_PICK_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            String filePath = getFilePathFromUri(fileUri);

            if (filePath != null && !filePath.isEmpty()) {
                photoFilePath = filePath;
                Glide.with(getContext()).load(R.drawable.pdf_icon).into(binding.fileImgVu);
                Functions.showToast(getContext(),getString(R.string.file_uploaded), FancyToast.SUCCESS);
                //binding.uploadPdf.setText("Selected File: " + filePath);
            }
        } else {
            if (!pdf){
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


    }
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);

        if (documentFile != null && documentFile.exists()) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                File tempFile = createTempFile(".pdf"); // Specify the file extension as ".pdf"
                if (tempFile != null) {
                    OutputStream outputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[4 * 1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    filePath = tempFile.getAbsolutePath();
                    outputStream.close();
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }

    private File createTempFile(String fileExtension) {
        try {
            File tempDir = getCacheDir();
            String tempFileName = "temp_file_" + System.currentTimeMillis();
            String tempFilePath = tempDir.getAbsolutePath() + "/" + tempFileName + fileExtension;
            return new File(tempFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void deleteFileClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles((getString(R.string.delete_file)))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteFile(fileId,clubId);
                        }
                    }
                }).show();
    }

    private void updateRecord(){
        updateDocument(true, fileId,
                binding.etFileName.getText().toString(),
                binding.etFileNumber.getText().toString(),
                binding.etIssueDate.getText().toString(),
                binding.etExpiryDate.getText().toString(),
                file);
    }
    private void addRecord(){

        addDocument(true, type, typeID,
                binding.etFileName.getText().toString(),
                binding.etFileNumber.getText().toString(),
                binding.etIssueDate.getText().toString(),
                binding.etExpiryDate.getText().toString(),
                file);
    }


    private void addDocument(boolean isLoader, String type, String typeId, String fileName, String fileNumber, String issuedDate, String expiryDate, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                if (file.exists()) {
                    RequestBody fileReqBody;
                    if (pdf){
                        fileReqBody = RequestBody.create(MediaType.parse("application/pdf"), file);
                    }else{
                        fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                    }
                    part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
                }

            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addNewFile(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),type),
                    RequestBody.create(MediaType.parse("multipart/form-data"),typeId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),fileName),
                    RequestBody.create(MediaType.parse("multipart/form-data"),fileNumber),
                    RequestBody.create(MediaType.parse("multipart/form-data"),issuedDate),
                    RequestBody.create(MediaType.parse("multipart/form-data"),expiryDate));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.file_added), FancyToast.SUCCESS);
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

    private void updateDocument(boolean isLoader, String fileId, String fileName, String fileNumber, String issuedDate, String expiryDate, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                if (file.exists()) {
                    RequestBody fileReqBody;
                    if (pdf){
                        fileReqBody = RequestBody.create(MediaType.parse("application/pdf"), file);
                    }else{
                        fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                    }
                    part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
                }else{

                }

            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateFile(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),fileId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),fileName),
                    RequestBody.create(MediaType.parse("multipart/form-data"),fileNumber),
                    RequestBody.create(MediaType.parse("multipart/form-data"),issuedDate),
                    RequestBody.create(MediaType.parse("multipart/form-data"),expiryDate));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.file_updated), FancyToast.SUCCESS);
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
    private void getFile(String fileId, String clubId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFile(Functions.getAppLang(getContext()), clubId, fileId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                docLists = gson.fromJson(obj.toString(), DocList.class);
                                populateData(update);
                            }
                        }  catch (Exception e) {
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
    private void deleteFile(String fileId, String clubId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteFile(Functions.getAppLang(getContext()), clubId, fileId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.file_deleted), FancyToast.SUCCESS);
                                finish();
                            }
                        }  catch (Exception e) {
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
    private void populateData(Boolean isUpdate){

        if (isUpdate){
            binding.deleteFile.setVisibility(View.VISIBLE);
            binding.title.setText(getString(R.string.update_file));
            binding.updateTv.setText(getString(R.string.update_file));
            binding.btnTvAddUpdate.setText(getString(R.string.update_file));
            binding.etFileName.setText(docLists.getName());
            binding.etFileNumber.setText(docLists.getNumber());
            binding.etIssueDate.setText(docLists.getIssueDate());
            binding.etExpiryDate.setText(docLists.getExpiryDate());
            if (!docLists.getPhotoUrl().isEmpty()){
                if (docLists.getPhotoUrl().contains("jpg")
                        || docLists.getPhotoUrl().contains("png")
                        || docLists.getPhotoUrl().contains("jpeg")){
                    Glide.with(getContext()).load(docLists.getPhotoUrl()).into(binding.fileImgVu);
                }else{
                    Glide.with(getContext()).load(R.drawable.pdf_icon).into(binding.fileImgVu);
                }
            }else{
                Glide.with(getContext()).load(R.drawable.attachment_img).into(binding.fileImgVu);
            }

        }else{
            binding.deleteFile.setVisibility(View.GONE);
            binding.title.setText(getString(R.string.add_file));
            binding.updateTv.setText(getString(R.string.add_file));
            binding.btnTvAddUpdate.setText(getString(R.string.add_file));
        }

    }
    private void DateClicked(String val) {

        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                if (val.equalsIgnoreCase("issue")){
                    binding.etIssueDate.setText(formatter.format(selectedDate.getTime()));
                }else{
                    binding.etExpiryDate.setText(formatter.format(selectedDate.getTime()));
                }
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        pickerDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}