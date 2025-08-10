package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddUpcomingExpenseBinding;
import ae.oleapp.databinding.ActivityMarkPaidBinding;
import ae.oleapp.dialogs.ExpenseSelectionListDialog;
import ae.oleapp.models.ExpenseList;
import ae.oleapp.models.UpcomingExpenseDetailsModel;
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

public class MarkPaidActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMarkPaidBinding binding;
    private boolean update;
    private UpcomingExpenseDetailsModel upcomingExpenseDetailsModel;
    private String upcomingExpenseId = "", clubId = "", photoFilePath = "";
    private EasyImage easyImage;
    private File file = new File("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarkPaidBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            upcomingExpenseDetailsModel = new Gson().fromJson(bundle.getString("result", ""), UpcomingExpenseDetailsModel.class);
            upcomingExpenseId = bundle.getString("upcoming_expense_id", "");
            clubId = bundle.getString("club_id", "");
        }

        populateData();

        binding.btnSubmit.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (binding.etAmount.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), "Amount cannot be empty", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            payUpcomingExpense(true, file);



        }
        else if (v == binding.btnClose) {
            finish();
        }
        else if (v == binding.invoiceImgVu) {
            updateInvoice();
        }

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

    private void payUpcomingExpense(boolean isLoader, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.payUpcomingExpense(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), upcomingExpenseId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), "Expense Paid Successfully!", FancyToast.SUCCESS);
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
                Glide.with(getContext()).load(file).into(binding.invoiceImgVu);
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


    private void populateData() {

            binding.etAmount.setText(upcomingExpenseDetailsModel.getAmount());
            binding.etNote.setText(upcomingExpenseDetailsModel.getNotes());
            if (!upcomingExpenseDetailsModel.getReceipt().isEmpty()){
                Glide.with(getContext()).load(upcomingExpenseDetailsModel.getReceipt()).into(binding.invoiceImgVu);
            }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

    }
}