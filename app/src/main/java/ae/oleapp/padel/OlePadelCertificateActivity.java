package ae.oleapp.padel;

import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPadelCertificateBinding;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleFileUtils;
import ae.oleapp.util.Functions;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePadelCertificateActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelCertificateBinding binding;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelCertificateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.global_rank);

        binding.nameVu.setVisibility(View.GONE);
        binding.bar.backBtn.setOnClickListener(this);
        binding.btnUpload.setOnClickListener(this);
        binding.attachVu.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnUpload) {
            if (file != null) {
                if (Functions.getPrefValue(getContext(),Constants.kIsSignIn).equalsIgnoreCase("1")){
                    uploadDocumentAPI(true);
                }else{
                    Functions.showToast(getContext(),getString(R.string.please_login_first), FancyToast.ERROR);
                }

            }
        }
        else if (v == binding.btnDel) {
            binding.nameVu.setVisibility(View.GONE);
            file = null;
        }
        else if (v == binding.attachVu) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.image), getResources().getString(R.string.pdf_file))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                pickPhotoClicked();
                            }
                            else {
                                pickDocClicked();
                            }
                        }
                    }).show();
        }
    }

    public void pickPhotoClicked() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                FilePickerBuilder.getInstance()
                        .setMaxCount(1)
                        .setActivityTheme(R.style.AppThemePlayer)
                        .setActivityTitle(getString(R.string.image))
                        .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                        .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                        .enableVideoPicker(false)
                        .enableCameraSupport(true)
                        .showGifs(false)
                        .showFolderView(false)
                        .enableSelectAll(false)
                        .enableImagePicker(true)
                        .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .pickPhoto(OlePadelCertificateActivity.this, 1121);
            }
        });
    }

    public void pickDocClicked() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                // Choose a directory using the system's file picker.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.setType("application/pdf");

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker when it loads.
//                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

                startActivityForResult(intent, 1122);
//                FilePickerBuilder.getInstance()
//                        .setMaxCount(10) //optional
//                        .setActivityTheme(R.style.AppThemePlayer) //optional
//                        .pickFile(getContext());
//                String[] pdfs = {"pdf"};
//                FilePickerBuilder.getInstance()
//                        .setMaxCount(1)
//                        .setActivityTheme(R.style.AppThemePlayer)
//                        .setActivityTitle(getString(R.string.pdf_file))
//                        .setImageSizeLimit(5) //Provide Size in MB
////                        .addFileSupport("PDF", pdfs)
//                        .enableDocSupport(true)
//                        .enableSelectAll(false)
//                        .sortDocumentsBy(SortingTypes.NAME)
//                        .withOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                        .pickFile(PadelCertificateActivity.this, 1122);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1121) {
            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            if (dataList != null && dataList.size() > 0) {
                Uri uri = dataList.get(0);
                try {
                    String path = ContentUriUtils.INSTANCE.getFilePath(getContext(), uri);
                    file = new File(path);
                    binding.tvFileName.setText(file.getName());
                    binding.nameVu.setVisibility(View.VISIBLE);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (resultCode == RESULT_OK && requestCode == 1122) {
            Uri uri = data.getData();
            String p = uri.getPath();
            try {
                String path = OleFileUtils.getPath(getContext(), uri);
                if (path == null) {
                    path = ContentUriUtils.INSTANCE.getFilePath(getContext(), uri);
                }
                file = new File(path);
                if (!path.endsWith(".pdf")) {
                    binding.tvFileName.setText(file.getName()+ ".pdf");
                }
                else {
                    binding.tvFileName.setText(file.getName());
                }
                binding.nameVu.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Functions.showToast(getContext(), "Unknown file path", FancyToast.ERROR);
            }

//            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
//            if (dataList != null && dataList.size() > 0) {
//                Uri uri = dataList.get(0);
//                try {
//                    String path = ContentUriUtils.INSTANCE.getFilePath(getContext(), uri);
//                    file = new File(path);
//                    binding.tvFileName.setText(file.getName());
//                    binding.nameVu.setVisibility(View.VISIBLE);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private void uploadDocumentAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        RequestBody fileReqBody;
        if (binding.tvFileName.getText().toString().endsWith(".pdf")) {
            fileReqBody = RequestBody.create(MediaType.parse("application/pdf"), file);
        }
        else {
            fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        }
        MultipartBody.Part part = MultipartBody.Part.createFormData("document", binding.tvFileName.getText().toString(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.uploadDocument(part, RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())), RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getPrefValue(getContext(), Constants.kUserID)));
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