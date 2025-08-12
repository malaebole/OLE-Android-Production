package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import ae.oleapp.adapters.GiftTargetAdapter;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddBankBinding;
import ae.oleapp.databinding.ActivityCreateGiftBinding;
import ae.oleapp.dialogs.BankAccountTypeDialog;
import ae.oleapp.models.AccountTypeModel;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.ClubGifts;
import ae.oleapp.models.GameHistory;
import ae.oleapp.models.GiftTargetList;
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
import retrofit2.http.Part;

public class CreateGiftActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCreateGiftBinding binding;
    private ClubGifts clubGiftsList;
    private final List<GiftTargetList> giftTargetList = new ArrayList<>();
    private GiftTargetAdapter giftTargetAdapter;
    private String clubId = "", giftId="", targetId="", photoFilePath = "", selectedGiftId="";
    private EasyImage easyImage;
    private boolean update;
    private File file = new File("");
    private int selectedIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGiftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update =  bundle.getBoolean("is_update");
            clubId = bundle.getString("club_id", "");
            giftId = bundle.getString("gift_id","");
            targetId = bundle.getString("target_id","");

        }

        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.giftTargetRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        giftTargetAdapter = new GiftTargetAdapter(getContext(), giftTargetList);
        giftTargetAdapter.setItemClickListener(itemClickListener);
        binding.giftTargetRecyclerVu.setAdapter(giftTargetAdapter);

        getGiftTargetList();

        binding.btnSubmit.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.deleteGift.setOnClickListener(this);
        binding.etStartDate.setOnClickListener(this);
        binding.etEndDate.setOnClickListener(this);

        if (update){
            binding.deleteGift.setVisibility(View.VISIBLE);
            getGiftsList(true, clubId, giftId);
        }else{
            binding.deleteGift.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (
                    binding.etGiftTitle.getText().toString().isEmpty() ||
                            binding.etDescription.getText().toString().isEmpty() ||
                            binding.etStartDate.getText().toString().isEmpty() ||
                            binding.etEndDate.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.fill_form_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            if (update){
                updateGift(true,
                        binding.etGiftTitle.getText().toString(),
                        binding.etDescription.getText().toString(),
                        binding.etStartDate.getText().toString(),
                        binding.etEndDate.getText().toString(), file);
            }else{
                addGift(true,
                        binding.etGiftTitle.getText().toString(),
                        binding.etDescription.getText().toString(),
                        binding.etStartDate.getText().toString(),
                        binding.etEndDate.getText().toString(), file);

            }

        }
        else if (v == binding.btnClose) {
            this.finish();
        }
        else if (v == binding.invoiceImgVu) {
            updateInvoice();
        }
        else if (v == binding.deleteGift){

            deleteGiftClicked();

        }
        else if (v == binding.etStartDate){
            startDateClicked();
        }
        else if (v == binding.etEndDate){
            endDateClicked();
        }

    }

    private void deleteGiftClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.delete_gift))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteGift(true, clubId, giftId);
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



    private void startDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etStartDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
    }

    private void endDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etEndDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    GiftTargetAdapter.ItemClickListener itemClickListener = new GiftTargetAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {


            //selectedIndex = pos;
            selectedGiftId = giftTargetList.get(pos).getId(); //check this
            giftTargetAdapter.setSelectedId(selectedGiftId);
            giftTargetAdapter.notifyDataSetChanged();
            //giftTargetAdapter.setSelectedIndex(pos);


        }
    };



    private void getGiftTargetList() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getGiftTargetList(Functions.getAppLang(getContext()));
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray obj = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                for (int i=0; i<obj.length(); i++){
                                    giftTargetList.add(gson.fromJson(obj.get(i).toString(), GiftTargetList.class));
                                }
                                //populateData(update);
                                giftTargetAdapter.setSelectedId(targetId);
                                giftTargetAdapter.notifyDataSetChanged();
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
    private void getGiftsList(Boolean isLoader, String clubId, String giftId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getGiftsList(Functions.getAppLang(getContext()), clubId, giftId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject data = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                clubGiftsList = gson.fromJson(data.toString(), ClubGifts.class);
                                populateData(update);
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
    private void addGift(boolean isLoader, String name, String details, String startDate, String endDate, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addGift(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), selectedGiftId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), name),
                    RequestBody.create(MediaType.parse("multipart/form-data"), details),
                    RequestBody.create(MediaType.parse("multipart/form-data"), startDate),
                    RequestBody.create(MediaType.parse("multipart/form-data"), endDate));
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.gift_created), FancyToast.SUCCESS);
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
    private void updateGift(boolean isLoader, String name, String details, String startDate, String endDate, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateGift(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), giftId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), selectedGiftId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), name),
                    RequestBody.create(MediaType.parse("multipart/form-data"), details),
                    RequestBody.create(MediaType.parse("multipart/form-data"), startDate),
                    RequestBody.create(MediaType.parse("multipart/form-data"), endDate));
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.gift_updated), FancyToast.SUCCESS);
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
    private void deleteGift(Boolean isLoader, String clubId, String giftId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteGift(Functions.getAppLang(getContext()), clubId, giftId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        Functions.hideLoader(hud);
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(),getString(R.string.gift_deleted), FancyToast.SUCCESS);
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //file = new File(resultUri.getPath());
                photoFilePath = resultUri.getPath();
                file = new File(photoFilePath);
                Glide.with(getApplicationContext()).load(file).into(binding.invoiceImgVu);
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






    private void populateData(Boolean isUpdate){

        if (isUpdate){
            binding.title.setText(R.string.update);
            binding.updateTv.setText(getString(R.string.update));
            binding.btnTvAddUpdate.setText(R.string.update);

            binding.etGiftTitle.setText(clubGiftsList.getName());
            binding.etDescription.setText(clubGiftsList.getDetails());
            binding.etStartDate.setText(clubGiftsList.getStartDate());
            binding.etEndDate.setText(clubGiftsList.getEndDate());

            if (!clubGiftsList.getPhotoUrl().isEmpty()){
                Glide.with(getApplicationContext()).load(clubGiftsList.getPhotoUrl()).into(binding.invoiceImgVu);
            }

        }else{
            binding.title.setText(getString(R.string.create_new_gift));
            binding.updateTv.setText(getString(R.string.create_new_gift));
            binding.btnTvAddUpdate.setText(R.string.submit);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

    }
}