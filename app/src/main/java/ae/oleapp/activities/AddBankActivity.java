package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import ae.oleapp.dialogs.BankAccountTypeDialog;
import ae.oleapp.dialogs.ClubBankSelectionListDialog;
import ae.oleapp.models.AccountTypeModel;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.IncomeDetailsModel;
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

public class AddBankActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddBankBinding binding;
    private final List<AccountTypeModel> itemList = new ArrayList<>();
    private ClubBankLists clubBankLists;
    private String clubId = "", photoFilePath = "", accounType = "";

    private  ClubBankLists bank;
    private EasyImage easyImage;
    private boolean update;
    private File file = new File("");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update =  bundle.getBoolean("is_update");
            clubId = bundle.getString("club_id", "");
            //bankId = bundle.getString("bank_id","");
            bank = new Gson().fromJson(bundle.getString("bank", ""), ClubBankLists.class);
        }
        //getClubBanksList(clubId, bankId);

        populateData(update);


        itemList.add(new AccountTypeModel(0, "Cash"));
        itemList.add(new AccountTypeModel(1, "Card"));
        itemList.add(new AccountTypeModel(2, "Online"));

        binding.btnSubmit.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.etAccountType.setOnClickListener(this);
        binding.deleteBank.setOnClickListener(this);

        if (update){
            binding.deleteBank.setVisibility(View.VISIBLE);
        }else{
            binding.deleteBank.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (
                    binding.etBankName.getText().toString().isEmpty() ||
                            binding.etAccountTitle.getText().toString().isEmpty() ||
                            binding.etIbanNo.getText().toString().isEmpty() ||
                            binding.etAccountNo.getText().toString().isEmpty() ||
                            binding.etBranchName.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.fill_form_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            if (update){
                updateBank(true,
                        binding.etBankName.getText().toString(),
                        bank.getId(),
                        binding.etAccountTitle.getText().toString(),
                        binding.etIbanNo.getText().toString(),
                        binding.etAccountNo.getText().toString(),
                        binding.etBranchName.getText().toString(),
                        "",
                        accounType, file);
            }else{
                addBank(true,
                        binding.etBankName.getText().toString(),
                        binding.etAccountTitle.getText().toString(),
                        binding.etIbanNo.getText().toString(),
                        binding.etAccountNo.getText().toString(),
                        binding.etBranchName.getText().toString(),
                        "",
                        accounType, file);

            }



        }
        else if (v == binding.btnClose) {
            this.finish();
        }
        else if (v == binding.invoiceImgVu) {
            updateInvoice();
        }

        else if (v == binding.etAccountType) {
            BankAccountTypeDialog dialog = new BankAccountTypeDialog(getContext(), getString(R.string.select_deposit_type));
            dialog.setLists(itemList);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new BankAccountTypeDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<AccountTypeModel> accountTypeModels, int pos) {
                    AccountTypeModel item = itemList.get(pos);
                    binding.etAccountType.setText(item.getValue());
                    accounType = item.getValue();
                }
            });
            dialog.show();

        }
        else if (v == binding.deleteBank){

            deleteBankClicked();

        }

    }

    private void deleteBankClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.delete_bank))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteBank(true, clubId, bank.getId());
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

    private void addBank(boolean isLoader, String bankName, String accountTitle, String ibanNumber, String accountNumber,
                         String branchName, String bankCity,   String depositType,  File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addBank(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), accountTitle),
                    RequestBody.create(MediaType.parse("multipart/form-data"), accountNumber),
                    RequestBody.create(MediaType.parse("multipart/form-data"), ibanNumber),
                    RequestBody.create(MediaType.parse("multipart/form-data"), branchName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankCity),
                    RequestBody.create(MediaType.parse("multipart/form-data"), depositType));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.bank_added_successfully), FancyToast.SUCCESS);
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



    private void updateBank(boolean isLoader, String bankName, String bankId, String accountTitle, String ibanNumber, String accountNumber,
                         String branchName, String bankCity,   String depositType,  File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateBank(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), accountTitle),
                    RequestBody.create(MediaType.parse("multipart/form-data"), accountNumber),
                    RequestBody.create(MediaType.parse("multipart/form-data"), ibanNumber),
                    RequestBody.create(MediaType.parse("multipart/form-data"), branchName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankCity),
                    RequestBody.create(MediaType.parse("multipart/form-data"), depositType));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.bank_updated_successfully), FancyToast.SUCCESS);
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

//    private void getClubBanksList(String clubId, String bankId) {
//        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
//        if (userId!=null){
//            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubBanksList(Functions.getAppLang(getContext()), clubId, bankId);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.body() != null) {
//                        try {
//                            JSONObject object = new JSONObject(response.body().string());
//                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                                JSONObject obj = object.getJSONObject(Constants.kData);
//                                Gson gson = new Gson();
//                                clubBankLists = gson.fromJson(obj.toString(), ClubBankLists.class);
//                                populateData(update);
//                            }
//                            else {
//                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    if (t instanceof UnknownHostException) {
//                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                    }
//                    else {
//                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//            });
//        }
//    }


    private void deleteBank(Boolean isLoader, String clubId, String bankId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteBank(Functions.getAppLang(getContext()), clubId, bankId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        Functions.hideLoader(hud);
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                               Functions.showToast(getContext(),getString(R.string.bank_deleted_successfully), FancyToast.SUCCESS);
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
            binding.updateTv.setText(getString(R.string.update));
            binding.btnTvAddUpdate.setText(R.string.update);

            binding.etBankName.setText(bank.getName());
            binding.etAccountTitle.setText(bank.getAccountTitle());
            binding.etIbanNo.setText(bank.getIbanNumber());
            binding.etAccountNo.setText(bank.getAccountNumber());
            binding.etBranchName.setText(bank.getBranchName());
            //binding.etBankCity.setText(clubBankLists.getBankCity());
            binding.etAccountType.setText(bank.getAccountType());

            if (!bank.getPhoto().isEmpty()){
                Glide.with(getContext()).load(bank.getPhoto()).into(binding.invoiceImgVu);
            }

        }else{
            binding.title.setText(getString(R.string.add_new_bank));
            binding.updateTv.setText(getString(R.string.add_new_bank));
            binding.btnTvAddUpdate.setText(R.string.submit);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

    }
}