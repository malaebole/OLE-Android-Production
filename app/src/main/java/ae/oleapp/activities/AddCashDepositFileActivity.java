package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddCashDepositFileBinding;
import ae.oleapp.databinding.ActivityAddNewFolderBinding;
import ae.oleapp.models.DocDetails;
import ae.oleapp.models.OleCashDeposit;
import ae.oleapp.models.ReportData;
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

public class AddCashDepositFileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddCashDepositFileBinding binding;
    private static final int REQUEST_PICK_xlsx = 3;
    private String photoFilePath = "";
    private boolean reportFileAdded;
    private boolean canUpdate = false;
    private final File file = new File("");
    private String clubId = "", Date = "";
    private ReportData reportData;
    List<String> buttonTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCashDepositFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club", "");
            Date = bundle.getString("date", "");
            reportFileAdded = bundle.getBoolean("reportFileAdded");
            canUpdate = bundle.getBoolean("update");
        }



        binding.btnDone.setOnClickListener(this);
        binding.fileImgVu.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        populateData();



    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            this.finish();
        } else if (v == binding.fileImgVu) {
            chooseOptions();
        } else if (v == binding.btnDone) {
            if (binding.etNote.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.write_note), FancyToast.ERROR);
                return;
            }
            if (reportFileAdded && canUpdate){
                updateDepositReport(true,clubId,binding.etNote.getText().toString(),reportData.getId(),file);
            }else{
                addDepositReport(true,clubId,binding.etNote.getText().toString(),Date,file);
            }
        }
    }
    private void chooseOptions() {

        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(buttonTitles.toArray(new String[buttonTitles.size()]))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {


                        if (index == 0 && reportFileAdded && canUpdate){
                            chooseXlsx();
                        }else if (index == 0 && reportFileAdded){
                            downloadXlsx(reportData.getReportFile());
                        } else if (index ==0 && reportData==null) {
                            chooseXlsx();
                        } else if (index == 1) {
                            downloadXlsx(reportData.getReportFile());
                        }

                    }
                }).show();
    }

    private void downloadXlsx(String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ole_reports"+"_"+"sheet"+".xlsx");
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Functions.showToast(getContext(),getString(R.string.report_downloading), FancyToast.SUCCESS);
        this.finish();


    }

    private void chooseXlsx() {

        String[] permissions;
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
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.chose_file)), REQUEST_PICK_xlsx);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_PICK_xlsx && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            String filePath = getFilePathFromUri(fileUri);

            if (filePath != null && !filePath.isEmpty()) {
                photoFilePath = filePath;
                Glide.with(getContext()).load(R.drawable.xlsx).into(binding.fileImgVu);
                //Functions.showToast(getContext(),getString(R.string.file_uploaded), FancyToast.SUCCESS);
                //binding.uploadPdf.setText("Selected File: " + filePath);
            }
        }


    }


    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);

        if (documentFile != null && documentFile.exists()) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                File tempFile = createTempFile(".xlsx"); // Specify the file extension as ".xlsx"
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

    //addDepositReport


    private void addDepositReport(boolean isLoader, String clubId, String note, String date, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                if (file.exists()) {
                    RequestBody fileReqBody;
                    fileReqBody = RequestBody.create(MediaType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), file);
                    part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
                }

            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addDepositReport(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"),Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),date),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.file_uploaded), FancyToast.SUCCESS);
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
    private void updateDepositReport(boolean isLoader, String clubId, String note, String fileId, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                if (file.exists()) {
                    RequestBody fileReqBody;
                    fileReqBody = RequestBody.create(MediaType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), file);
                    part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
                }

            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateDepositReport(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"),Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),fileId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
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

    private void depositReportByDate(boolean isLoader, String clubId, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.depositReportByDate(Functions.getAppLang(getContext()),date, clubId );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            reportData = new Gson().fromJson(obj.toString(), ReportData.class);
                            binding.etNote.setText(reportData.getNote());
                            Glide.with(getContext()).load(R.drawable.xlsx).into(binding.fileImgVu);
                           // populateData();
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

    private void populateData() {
        if (reportFileAdded){
            depositReportByDate(true,clubId,Date);
            if (canUpdate){
                buttonTitles.clear();
                buttonTitles.add(getString(R.string.update_report));
                buttonTitles.add(getString(R.string.download_report));
                binding.title.setText(getString(R.string.update_report));
                binding.btnText.setText(getString(R.string.update));
            }else{
                buttonTitles.clear();
                buttonTitles.add(getString(R.string.download_report));
                binding.btnDone.setVisibility(View.GONE);
                binding.title.setText(getString(R.string.download_report));
                binding.etNote.setEnabled(false);
            }

        }else{
            buttonTitles.clear();
            buttonTitles.add(getString(R.string.upload_report));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}