package ae.oleapp.signup;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.MainActivity;
import ae.oleapp.activities.OleWebVuActivity;
import ae.oleapp.adapters.OleShirtListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPlayerSignupBinding;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OlePlayerMainTabsActivity;
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

public class PlayerSignupActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPlayerSignupBinding binding;
    private String selectedCountryId = "";
    private final String selectedCityId = "";
    private String dobStr = "";
    //private String selectedGender = "";
    private Boolean isForReferee = false;
    private final List<Shirt> shirtList = new ArrayList<>();
    private OleShirtListAdapter shirtAdapter;
    private String selectedShirtId = "", photoFilePath = "", userModule = "", userIpDetails="";
    private EasyImage easyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        ipdetails(true,"");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForReferee = bundle.getBoolean("is_referee", false);
            //userModule = bundle.getString("userModule", "");
        }

        userModule = Functions.getPrefValue(getContext(), Constants.kUserModule);
        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new OleShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);

        getBibsAPI(new BibsCallback() {
            @Override
            public void getBibs(List<Shirt> shirts) {
                shirtList.clear();
                shirtList.addAll(shirts);
                shirtAdapter.notifyDataSetChanged();
            }
        });

//        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); // checkx
//        countryCode = tm.getNetworkCountryIso();


        if (isForReferee) {
            //binding.imgPlayer.setImageResource(R.drawable.referee_active);
        }
        else {
           // binding.imgPlayer.setImageResource(R.drawable.player_active);
        }

        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo != null) {
            binding.etFullName.setText(userInfo.getFirstName());
//            binding.etLastName.setText(userInfo.getLastName());
            binding.etEmail.setText(userInfo.getEmail());
        }

        binding.btnBack.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);
        binding.tvTerms.setOnClickListener(this);
//        binding.etCountry.setOnClickListener(this);
//        binding.etCity.setOnClickListener(this);
        binding.etDob.setOnClickListener(this);
//        binding.etGender.setOnClickListener(this);
        binding.imgCard.setOnClickListener(this);

    }

    OleShirtListAdapter.ItemClickListener shirtClickListener = new OleShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            Glide.with(getContext()).load(shirt.getPhotoUrl()).into(binding.shirtImgVu);
            selectedShirtId = shirt.getId();
            shirtAdapter.setSelectedId(selectedShirtId);
        }
    };
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
                easyImage.openChooser(PlayerSignupActivity.this);
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
                                .start(PlayerSignupActivity.this);
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

    private void ipdetails(Boolean isLoader, String ip) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(),"Image processing") : null;
        Call<ResponseBody>  call = AppManager.getInstance().apiInterfaceNode.getIpDetails(ip,Functions.getAppLang(getContext()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            userIpDetails = data.getString("login_type");
                            userModule = data.getString("allow_module");
                            selectedCountryId = data.getString("country_id");
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kLoginType, userIpDetails);
                            editor.putString(Constants.kUserModule, userModule);
                            editor.apply();


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
    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            backClicked();
        }
        else if (v == binding.btnSignup) {
            signupClicked();
        }
        else if (v == binding.tvTerms) {
            termsClicked();
        }
//        else if (v == binding.etCountry) {
//            countryClicked();
//        }
//        else if (v == binding.etCity) {
//            cityClicked();
//        }
        else if (v == binding.etDob) {
            dobClicked();
        }
//        else if (v == binding.etGender) {
//            genderClicked();
//        }
        else if (v == binding.imgCard) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.please_select_image_source));
            builder.setItems(new CharSequence[]
                            {getResources().getString(R.string.choose_photo), getResources().getString(R.string.delete_photo), getResources().getString(R.string.cancel)},
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which) {
                                case 0:
                                    pickImage();
                                    break;
                                case 1:
                                    photoFilePath = "";
                                    binding.emojiImgVu.setImageDrawable(null);
                                    binding.emojiImgVu.setImageResource(0);
                                    break;
                                case 2:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
            builder.create().show();
        }
    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }

    private void signupClicked() {
        if (binding.etFullName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_full_name), FancyToast.ERROR);
            return;
        }
//        if (Functions.isArabic(binding.etFirstName.getText().toString())) {
//            Functions.showToast(getContext(), getString(R.string.enter_first_name_english), FancyToast.ERROR);
//            return;
//        }
//        if (binding.etLastName.getText().toString().isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.enter_last_name), FancyToast.ERROR);
//            return;
//        }
//        if (Functions.isArabic(binding.etLastName.getText().toString())) {
//            Functions.showToast(getContext(), getString(R.string.enter_last_name_english), FancyToast.ERROR);
//            return;
//        }
        if (binding.etTeamName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_team_name), FancyToast.ERROR);
            return;
        }
        if (Functions.isArabic(binding.etTeamName.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.enter_team_name_english), FancyToast.ERROR);
            return;
        }
//        if (selectedCountryId.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
//            return;
//        }
//        if (selectedCityId.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_city), FancyToast.ERROR);
//            return;
//        }
        if (binding.etDob.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_dob), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
//        if (selectedGender.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_gender), FancyToast.ERROR);
//            return;
//        }
        if (selectedShirtId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_bib), FancyToast.ERROR);
            return;
        }

            signUpApi(String.format("%s %s", binding.etFullName.getText().toString(), ""),
                    "",
                    "",
                    binding.etTeamName.getText().toString(),
                    binding.etEmail.getText().toString(),
                    Constants.kPlayerType);



    }

    private void termsClicked() {
        Intent intent = new Intent(getContext(), OleWebVuActivity.class);
        startActivity(intent);
    }

//    private void genderClicked() {
//        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
//                .setCancelButtonTitle(getResources().getString(R.string.cancel))
//                .setOtherButtonTitles(getResources().getString(R.string.male), getResources().getString(R.string.female))
//                .setCancelableOnTouchOutside(true)
//                .setListener(new ActionSheet.ActionSheetListener() {
//                    @Override
//                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//
//                    }
//
//                    @Override
//                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
//                        if (index == 0) {
//                            selectedGender = "male";
//                            binding.etGender.setText(R.string.male);
//                        }
//                        else {
//                            selectedGender = "female";
//                            binding.etGender.setText(R.string.female);
//                        }
//                    }
//                }).show();
//    }

//    private void countryClicked() {
//        List<OleSelectionList> oleSelectionList = new ArrayList<>();
//        if (AppManager.getInstance().countries.size() == 0) {
//            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//            getCountriesAPI(new CountriesCallback() {
//                @Override
//                public void getCountries(List<OleCountry> countries) {
//                    hud.dismiss();
//                    AppManager.getInstance().countries = countries;
//                    countryClicked();
//                }
//            });
//        }
//        else {
//            for (OleCountry oleCountry : AppManager.getInstance().countries) {
//                oleSelectionList.add(new OleSelectionList(oleCountry.getId(), oleCountry.getName()));
//            }
//            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_country), false);
//            dialog.setLists(oleSelectionList);
//
//            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
//                @Override
//                public void selectedItem(List<OleSelectionList> selectedItems) {
//                    OleSelectionList selectedItem = selectedItems.get(0);
//                    selectedCountryId = selectedItem.getId();
//                    binding.etCountry.setText(selectedItem.getValue());
//                    selectedCityId = "";
//                    binding.etCity.setText("");
//                }
//            });
//            dialog.show();
//        }
//    }
//    private void cityClicked() {
//        if (selectedCountryId.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
//            return;
//        }
//        List<OleSelectionList> oleSelectionList = new ArrayList<>();
//        for (OleCountry oleCountry : AppManager.getInstance().countries) {
//            if (oleCountry.getId().equalsIgnoreCase(selectedCountryId)) {
//                for (OleCountry city : oleCountry.getCities()) {
//                    oleSelectionList.add(new OleSelectionList(city.getId(), city.getName()));
//                }
//                break;
//            }
//        }
//        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), false);
//        dialog.setLists(oleSelectionList);
//        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
//            @Override
//            public void selectedItem(List<OleSelectionList> selectedItems) {
//                OleSelectionList selectedItem = selectedItems.get(0);
//                selectedCityId = selectedItem.getId();
//                binding.etCity.setText(selectedItem.getValue());
//            }
//        });
//        dialog.show();
//    }

    private void dobClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                dobStr = formatter.format(calendar.getTime());
                formatter.applyPattern("dd/MM/yyyy");
                binding.etDob.setText(formatter.format(calendar.getTime()));

            }
        },
        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH));
        
        pickerDialog.show();
    }

    private void signUpApi(String fullName, String firstName, String lastName, String nickName, String email, String type)  //, String password
    {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("emoji", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.pregister(filePart,
                RequestBody.create(Functions.getAppLang(getContext()), MediaType.parse("multipart/form-data")),
               RequestBody.create(Functions.getPrefValue(getContext(), Constants.kUserID), MediaType.parse("multipart/form-data")),
                RequestBody.create(fullName, MediaType.parse("multipart/form-data")),
                RequestBody.create(firstName, MediaType.parse("multipart/form-data")),
                RequestBody.create(lastName, MediaType.parse("multipart/form-data")),
                RequestBody.create(nickName, MediaType.parse("multipart/form-data")),
                RequestBody.create(selectedCountryId, MediaType.parse("multipart/form-data")),
                RequestBody.create("", MediaType.parse("multipart/form-data")),
                RequestBody.create(dobStr, MediaType.parse("multipart/form-data")),
                RequestBody.create(email, MediaType.parse("multipart/form-data")),
                RequestBody.create("", MediaType.parse("multipart/form-data")),
                RequestBody.create(type, MediaType.parse("multipart/form-data")),
                RequestBody.create("android", MediaType.parse("multipart/form-data")),
                RequestBody.create(selectedShirtId, MediaType.parse("multipart/form-data"))); //    RequestBody.create(password, MediaType.parse("multipart/form-data")))
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                UserInfo userInfo = new Gson().fromJson(obj.toString(), UserInfo.class);
                                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                editor.putString(Constants.kUserID, userInfo.getId());
                                editor.putString(Constants.kIsSignIn, "1");
                                editor.putString(Constants.kUserType, userInfo.getUserRole());
                                editor.putString(Constants.kCurrency, userInfo.getCurrency());
                                editor.apply();

                                Functions.saveUserinfo(getContext(), userInfo);

                                String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                                if (!fcmToken.isEmpty()) {
                                    sendFcmTokenApi(fcmToken);
                                }

                                SharedPreferences.Editor editor1 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                if (!userModule.equalsIgnoreCase("all")){
                                    editor1.putString(Constants.kAppModule, Constants.kLineupModule);
                                    editor1.apply();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }else {
                                    editor1.putString(Constants.kAppModule, Constants.kFootballModule);
                                    editor1.apply();
                                    Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                 finish();
                            } else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                        }
                    } else {
                        Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
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

    public void getBibsAPI(BibsCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getBibs();
//        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getBibs(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<Shirt> shirts = new ArrayList<>();
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i=0; i<arr.length();i++) {
                                Shirt shirt = gson.fromJson(arr.get(i).toString(), Shirt.class);
                                shirts.add(shirt);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.getBibs(shirts);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.getBibs(new ArrayList<>());
            }
        });
    }

    private void cutFacePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
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
                        photoFilePath = saveBitmap(bmp);
                        File file = new File(photoFilePath);
                        Glide.with(getContext()).load(file).into(binding.emojiImgVu);
                        binding.imgVuPlace.setVisibility(View.GONE);
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
