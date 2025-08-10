package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import ae.oleapp.databinding.ActivityAddIncomeBinding;
import ae.oleapp.databinding.ActivityAddPartnerBinding;
import ae.oleapp.databinding.ActivityPayToPartnerBinding;
import ae.oleapp.dialogs.ClubBankSelectionListDialog;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.IncomeHistory;
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

public class PayToPartnerActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPayToPartnerBinding binding;
    private boolean update;

    private final List<ClubBankLists> clubBankLists = new ArrayList<>();
    private final List<PartnerData> partnerDataList = new ArrayList<>();
    private EasyImage easyImage;
    private File file = new File("");
    private String clubId="", clubBankListItemId = "",  photoFilePath = "", earnings = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayToPartnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
            earnings = bundle.getString("earnings","");
        }

        //populateData(update);
        getClubBanksList(clubId);
        getClubPartnersList(clubId);

        binding.btnSubmit.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.etPaymentMethod.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit){
            if (binding.etPaymentMethod.getText().toString().isEmpty()){
                Functions.showToast(getContext(),getString(R.string.payment_method), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            else if (binding.etAmount.getText().toString().isEmpty()){
                Functions.showToast(getContext(),getString(R.string.amount_cannot_be_empty), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            payToPartner(true, binding.etAmount.getText().toString(), "", binding.etNote.getText().toString(),file);

        }
        else if (v == binding.invoiceImgVu) {
            updateInvoice();
        }
        else if (v == binding.btnClose) {
            finish();
        }
        else if (v == binding.etPaymentMethod) {
            ClubBankSelectionListDialog dialog = new ClubBankSelectionListDialog(getContext(), getString(R.string.select_bank));
            dialog.setLists(clubBankLists);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new ClubBankSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<ClubBankLists> ClubBankLists, int pos) {
                    ClubBankLists item = clubBankLists.get(pos);
                    binding.etPaymentMethod.setText(item.getName());
                    clubBankListItemId = item.getId();
                }
            });
            dialog.show();
        }

    }

    private void getClubBanksList(String clubId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubBanksList(Functions.getAppLang(getContext()), clubId,"");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                clubBankLists.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    clubBankLists.add(gson.fromJson(data.get(i).toString(), ClubBankLists.class));
                                }


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

    private void getClubPartnersList(String clubId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubPartnersList(Functions.getAppLang(getContext()), clubId,"");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                partnerDataList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    partnerDataList.add(gson.fromJson(data.get(i).toString(), PartnerData.class));
                                }
                                binding.tvEarnings.setText(getString(R.string.available_earnings)+" : "
                                        +object.getString("total_incomes")
                                        +getString(R.string.aed));
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
                Glide.with(getContext()).load(file).into(binding.invoiceImgVu);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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

    private void payToPartner(boolean isLoader, String amount, String month, String note, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }

            Call<ResponseBody> call = AppManager.getInstance().apiInterface.payToPartner(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubBankListItemId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"),month),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.amount_distributed), FancyToast.SUCCESS);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}