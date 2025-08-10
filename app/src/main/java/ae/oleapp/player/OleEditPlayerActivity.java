package ae.oleapp.player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEditPlayerBinding;
import ae.oleapp.dialogs.AddPlayerFragment;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.UserInfo;
import ae.oleapp.signup.VerifyPhoneActivity;
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

public class OleEditPlayerActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEditPlayerBinding binding;
    private EasyImage easyImage;
    private UserInfo userInfo;
    private String strDob = "";
    private String selectedCityId = "";
    private String selectedCountryId = "";
    private final List<Shirt> shirtList = new ArrayList<>();
    private ShirtListAdapter shirtAdapter;
    private String selectedShirtId = "", photoFilePath = "";
    private final PlayerInfo profileInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEditPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.profile);

        getProfileAPI(userInfo == null);

        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                binding.etNationality.setText(binding.ccp.getSelectedCountryName());
            }
        });
        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new ShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);

        //((BaseActivity)getActivity())  checkx
        ((BaseActivity) getContext()).getBibsAPI(new BaseActivity.BibsCallback() {
            @Override
            public void getBibs(List<Shirt> shirts) {
                shirtList.clear();
                shirtList.addAll(shirts);
                shirtAdapter.notifyDataSetChanged();
            }
        });
        if (userInfo != null) {
            selectedShirtId = userInfo.getBibId();
        }

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnVerify.setOnClickListener(this);
       //binding.btnChange.setOnClickListener(this);
        binding.cardvu.setOnClickListener(this);
        binding.etCountry.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
        binding.etDob.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
    }
    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            Glide.with(getContext()).load(shirt.getPhotoUrl()).into(binding.shirtImgVu);
            //Glide.with(getContext()).load(shirt.getPhotoUrl()).into(binding.shirtImgVu);
            selectedShirtId = shirt.getId();
            shirtAdapter.setSelectedId(selectedShirtId);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        userInfo = Functions.getUserinfo(getContext());
        populateData();

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnVerify) {
            verifyClicked();
        }
//        else if (v == binding.btnChange) {
//            photoChangeClicked();
//        }
        else if (v == binding.cardvu) {
            chooseSource();
        }
        else if (v == binding.etCountry) {
            countryClicked();
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
        else if (v == binding.etDob) {
            dobClicked();
        }
        else if (v == binding.btnUpdate) {
            updateClicked();
        }
    }

    private void verifyClicked() {
        Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
        intent.putExtra("is_update", true);
        intent.putExtra("phone", userInfo.getPhone());
        startActivity(intent);
    }
    private void chooseSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.please_select_image_source));
        builder.setItems(new CharSequence[]
                        {getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image), getResources().getString(R.string.cancel)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                pickImage();
                                break;
                            case 1:
                                deletePhotoAPI(true);
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

//    private void photoChangeClicked() {
//        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
//                .setCancelButtonTitle(getResources().getString(R.string.cancel))
//                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
//                .setCancelableOnTouchOutside(true)
//                .setListener(new ActionSheet.ActionSheetListener() {
//                    @Override
//                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//                    }
//
//                    @Override
//                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
//                        if (index == 0) {
//                            //pickImage();
//                        }
//                        else if (index == 1) {
//                            //deletePhotoAPI(true);
//                        }
//                    }
//                }).show();
//    }

    private void countryClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        if (AppManager.getInstance().countries.size() == 0) {
            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
            getCountriesAPI(new CountriesCallback() {
                @Override
                public void getCountries(List<OleCountry> countries) {
                    hud.dismiss();
                    AppManager.getInstance().countries = countries;
                    countryClicked();
                }
            });
        }
        else {
            for (OleCountry oleCountry : AppManager.getInstance().countries) {
                oleSelectionList.add(new OleSelectionList(oleCountry.getId(), oleCountry.getName()));
            }
            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_country), false);
            dialog.setLists(oleSelectionList);

            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<OleSelectionList> selectedItems) {
                    OleSelectionList selectedItem = selectedItems.get(0);
                    selectedCountryId = selectedItem.getId();
                    binding.etCountry.setText(selectedItem.getValue());
                    selectedCityId = "";
                    binding.etCity.setText("");
                }
            });
            dialog.show();
        }
    }

    private void cityClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        for (OleCountry oleCountry : AppManager.getInstance().countries) {
            if (oleCountry.getId().equalsIgnoreCase(selectedCountryId)) {
                for (OleCountry city : oleCountry.getCities()) {
                    oleSelectionList.add(new OleSelectionList(city.getId(), city.getName()));
                }
                break;
            }
        }
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), true);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                selectedCityId = "";
                binding.etCity.setText("");
                for (OleSelectionList selectedItem: selectedItems) {
                    if (binding.etCity.getText().toString().isEmpty()) {
                        selectedCityId = selectedItem.getId();
                        binding.etCity.setText(selectedItem.getValue());
                    }
                    else {
                        selectedCityId = String.format("%s,%s",  selectedCityId, selectedItem.getId());
                        binding.etCity.setText(String.format("%s, %s",  binding.etCity.getText().toString(), selectedItem.getValue()));
                    }
                }
            }
        });
        dialog.show();
    }

    private void dobClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                strDob = formatter.format(calendar.getTime());
                formatter.applyPattern("dd/MM/yyyy");
                binding.etDob.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void updateClicked() {
        if (binding.etFName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_first_name), FancyToast.ERROR);
            return;
        }
        if (Functions.isArabic(binding.etFName.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.enter_first_name_english), FancyToast.ERROR);
            return;
        }
        if (binding.etLName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_last_name), FancyToast.ERROR);
            return;
        }
        if (Functions.isArabic(binding.etLName.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.enter_last_name_english), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
        if (binding.etDob.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_dob), FancyToast.ERROR);
            return;
        }
        if (selectedCountryId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
            return;
        }
        if (selectedCityId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_city), FancyToast.ERROR);
            return;
        }
        if (binding.etNationality.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_nationality), FancyToast.ERROR);
            return;
        }

        updateProfileAPI(true, String.format("%s %s", binding.etFName.getText().toString(), binding.etLName.getText().toString()), binding.etFName.getText().toString(), binding.etLName.getText().toString(), binding.etNationality.getText().toString(), binding.etPhone.getText().toString(), binding.etEmail.getText().toString());
    }

//    private void pickImage() {
//        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                easyImage = new EasyImage.Builder(getContext())
//                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
//                        .setCopyImagesToPublicGalleryFolder(false)
//                        .allowMultiple(false).build();
//                easyImage.openChooser(OleEditPlayerActivity.this);
//            }
//        });
//    }
private void pickImage() {
    String[] permissions = new String[0];
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
    }else{
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
            easyImage.openChooser(OleEditPlayerActivity.this);
        }
    });
}

    private void populateData() {
        if (userInfo == null) {
            return;
        }
        //Glide.with(this).load(userInfo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.imgVu);
        Glide.with(getContext()).load(userInfo.getEmojiUrl()).into(binding.emojiImgVu);
        Glide.with(getContext()).load(userInfo.getBibUrl()).into(binding.shirtImgVu);
        binding.etFName.setText(userInfo.getFirstName());
        binding.etLName.setText(userInfo.getLastName());
        binding.etEmail.setText(userInfo.getEmail());
        binding.etPhone.setText(userInfo.getPhone());
        binding.etNationality.setText(userInfo.getNationality());
        strDob = userInfo.getDateOfBirth();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(strDob);
            dateFormat.applyPattern("dd/MM/yyyy");
            binding.etDob.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (userInfo.getPhoneVerified().equalsIgnoreCase("1")) {
            binding.btnVerify.setVisibility(View.GONE);
        }
        else {
            binding.btnVerify.setVisibility(View.VISIBLE);
        }

        selectedCityId = userInfo.getCity();
        selectedCountryId = userInfo.getCountry();
        for (OleCountry oleCountry : AppManager.getInstance().countries) {
            if (oleCountry.getId().equalsIgnoreCase(selectedCountryId)) {
                binding.etCountry.setText(oleCountry.getName());
                String[] arr = selectedCityId.split(",");
                binding.etCity.setText("");
                for (String id : arr) {
                    for (OleCountry city : oleCountry.getCities()) {
                        if (city.getId().equalsIgnoreCase(id)) {
                            if (binding.etCity.getText().toString().isEmpty()) {
                                binding.etCity.setText(city.getName());
                            }
                            else {
                                binding.etCity.setText(String.format("%s, %s", binding.etCity.getText().toString(), city.getName()));
                            }
                        }
                    }
                }
                break;
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
//                //Glide.with(getContext()).load(file).into(binding.imgVu);
//                //updatePhotoAPI(true, file);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//        else {
//            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
//                @Override
//                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
//                    if (mediaFiles.length > 0) {
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1,1)
//                                .start(OleEditPlayerActivity.this);
//                    }
//                }
//
//                @Override
//                public void onImagePickerError(Throwable error, MediaSource source) {
//                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
//                }
//
//                @Override
//                public void onCanceled(@NonNull MediaSource mediaSource) {
//
//                }
//            });
//        }
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(resultUri.getPath());
                cutFacePhotoAPI(true, file);

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
                                .start( OleEditPlayerActivity.this);
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

    private void updatePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateProfilePhoto(part, RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())), RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getPrefValue(getContext(), Constants.kUserID)));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            String url = object.getJSONObject(Constants.kData).getString("photo_url");
                            Glide.with(getContext()).load(url).into(binding.emojiImgVu);
                            UserInfo userInfo = Functions.getUserinfo(getContext());
                            userInfo.setEmojiUrl(url);
                            Functions.saveUserinfo(getContext(), userInfo);
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
    private void cutFacePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing"): null;
        RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface2.cutFace("https://www.cutout.pro/api/v1/matting?mattingType=3", part, RequestBody.create("true", MediaType.parse("multipart/form-data")), RequestBody.create("true", MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bmp = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                        photoFilePath = ((BaseActivity)getContext()).saveBitmap(bmp);
                        File file = new File(photoFilePath);
                        Glide.with(getContext()).load(file).into(binding.emojiImgVu);
                        updatePhotoAPI(true,file);

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

//    private void deletePhotoAPI(boolean isLoader) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deletePhoto(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            binding.imgVu.setImageResource(R.drawable.player_active);
//                            UserInfo userInfo = Functions.getUserinfo(getContext());
//                            userInfo.setPhotoUrl("");
//                            Functions.saveUserinfo(getContext(), userInfo);
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }
    private void deletePhotoAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removePhoto(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), userInfo.getFriendShipId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Glide.with(getContext()).load("").into(binding.emojiImgVu);
                            userInfo = Functions.getUserinfo(getContext());
                            userInfo.setEmojiUrl("");
                            Functions.saveUserinfo(getContext(), userInfo);
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),"", Functions.getPrefValue(getContext(),Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            userInfo.setEmojiUrl(obj.getString("emoji_url"));
                            userInfo.setBibUrl(obj.getString("bib_url"));
                            UserInfo info = gson.fromJson(obj.toString(), UserInfo.class);

                            Functions.saveUserinfo(getContext(), info);

                            populateData();
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

    private void updateProfileAPI(boolean isLoader, String name, String fName, String lName, String nationality, String phone, String email) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        HashMap<String, String> dynamicParams = new HashMap<>();
        dynamicParams.put("user_id", Functions.getPrefValue(getContext(), Constants.kUserID));
        dynamicParams.put("bib_id",selectedShirtId);
        dynamicParams.put("name", name);
        if (!fName.equalsIgnoreCase(userInfo.getFirstName())) {
            dynamicParams.put("first_name", fName);
        }
        if (!lName.equalsIgnoreCase(userInfo.getLastName())) {
            dynamicParams.put("last_name", lName);
        }
        if (!selectedCountryId.equalsIgnoreCase(userInfo.getCountry())) {
            dynamicParams.put("country", selectedCountryId);
        }
        if (!selectedCityId.equalsIgnoreCase(userInfo.getCity())) {
            dynamicParams.put("city", selectedCityId);
        }
        if (!nationality.equalsIgnoreCase(userInfo.getNationality())) {
            dynamicParams.put("nationality", nationality);
        }
        if (!strDob.equalsIgnoreCase(userInfo.getDateOfBirth())) {
            dynamicParams.put("dob", strDob);
        }
        if (!email.equalsIgnoreCase(userInfo.getEmail())) {
            dynamicParams.put("email", email);
        }
        if (!phone.equalsIgnoreCase(userInfo.getPhone())) {
            dynamicParams.put("phone", phone);
        }

        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateUser(Functions.getAppLang(getContext()), dynamicParams);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (!phone.equalsIgnoreCase(userInfo.getPhone())) {
                                Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
                                intent.putExtra("is_update", true);
                                intent.putExtra("phone", phone);
                                startActivity(intent);
                                userInfo.setPhoneVerified("0");
                            }

                            String age = object.getJSONObject(Constants.kData).getString("user_age");
                            if (age != null && !age.equalsIgnoreCase("")) {
                                userInfo.setUserAge(age);
                            }
                            String bibUrl = object.getJSONObject(Constants.kData).getString("bib_url");
                            String emojiurl = object.getJSONObject(Constants.kData).getString("emoji_url");
                            userInfo.setName(name);
                            userInfo.setFirstName(fName);
                            userInfo.setLastName(lName);
                            userInfo.setCity(selectedCityId);
                            userInfo.setCountry(selectedCountryId);
                            userInfo.setNationality(nationality);
                            userInfo.setDateOfBirth(strDob);
                            userInfo.setPhone(phone);
                            userInfo.setBibId(selectedShirtId);
                            userInfo.setBibUrl(bibUrl);
                            userInfo.setEmojiUrl(emojiurl);
                            Functions.saveUserinfo(getContext(), userInfo);

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
