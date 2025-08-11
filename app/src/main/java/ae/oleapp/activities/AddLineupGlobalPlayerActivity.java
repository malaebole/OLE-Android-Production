package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddLineupGlobalPlayerBinding;
import ae.oleapp.databinding.ActivityLineupGlobalTeamsBinding;
import ae.oleapp.models.LineupGlobalPlayers;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OleEditPlayerActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.PreviewFieldView;
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
import retrofit2.http.Field;

public class AddLineupGlobalPlayerActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddLineupGlobalPlayerBinding binding;
    private List<Shirt> shirtList = new ArrayList<>();
    private LineupGlobalPlayers lineupGlobalPlayer;
    private ShirtListAdapter shirtAdapter;
    private boolean isUpdate = false;
    private String selectedCountryId = "", selectedTeamId = "", playerId="", selectedShirtId = "",photoFilePath = "", photoUrl="";
    private EasyImage easyImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLineupGlobalPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isUpdate = bundle.getBoolean("is_update");
            selectedCountryId = bundle.getString("country_id");
            selectedTeamId = bundle.getString("team_id");
            playerId = bundle.getString("player_id");
            lineupGlobalPlayer = new Gson().fromJson(bundle.getString("player"), LineupGlobalPlayers.class);
            String shirtsJson = bundle.getString("shirts");
            if (shirtsJson != null) {
                Type listType = new TypeToken<ArrayList<Shirt>>() {}.getType();
                shirtList = new Gson().fromJson(shirtsJson, listType);
            }

        }

        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new ShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);
        shirtAdapter.setPremiumShirt(false);
//        shirtAdapter.notifyDataSetChanged();

        populateData();


        binding.backBtn.setOnClickListener(this);
        binding.deleteBtn.setOnClickListener(this);
        binding.emojiVu.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }



    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            Glide.with(getApplicationContext()).load(shirt.getPhotoUrl()).into(binding.shirt);
            selectedShirtId = shirt.getId();

        }
    };



    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        }
        else if (v == binding.deleteBtn) {
            deleteBtnClicked();
        }
        else if (v == binding.emojiVu) {
            chooseSource();
        }
        else if (v == binding.btnSubmit) {
                    UserInfo info = Functions.getUserinfo(getContext());
                    String isGlobalLineup = "";
                    if (info.getGlobalLineup() !=null){
                        isGlobalLineup = info.getGlobalLineup();
                    }

                    if (binding.etPlayerName.getText().toString().isEmpty()){
                        Functions.showToast(getContext(),"Please Enter Player Name", FancyToast.ERROR);
                        return;
                    }
                    if (selectedShirtId.isEmpty()){
                        Functions.showToast(getContext(),"Please Select Shirt", FancyToast.ERROR);
                        return;
                    }

                    if (!isGlobalLineup.isEmpty() && isGlobalLineup.equalsIgnoreCase("1")){
                        if (binding.etPlayerNameArabic.getText().toString().isEmpty()){
                            Functions.showToast(getContext(),"Please Enter Player Name Arabic", FancyToast.ERROR);
                            return;
                        }
                    }

                    if (isUpdate){
                        updateGlobalPlayer(true,binding.etPlayerName.getText().toString(), binding.etPlayerNameArabic.getText().toString(), selectedTeamId, selectedCountryId,selectedShirtId, photoUrl, playerId);
                    }
                    else{
                        addGlobalPlayer(true, binding.etPlayerName.getText().toString(), binding.etPlayerNameArabic.getText().toString(), selectedTeamId, selectedCountryId,selectedShirtId, photoUrl);
                    }
        }

    }

    private void populateData() {
        if (isUpdate){
            binding.toolbarTitle.setText("Update");
            binding.tvBtnContinue.setText("Update");
            binding.etPlayerName.setText(lineupGlobalPlayer.getNickName());
            binding.etPlayerNameArabic.setText(lineupGlobalPlayer.getArabicName());
            if (lineupGlobalPlayer.getEmojiUrl().isEmpty()){
                binding.imgVuPlace.setVisibility(View.VISIBLE);
            }else{
                binding.imgVuPlace.setVisibility(View.GONE);
            }
            Glide.with(getApplicationContext()).load(lineupGlobalPlayer.getEmojiUrl()).into(binding.playerImgVu);
            Glide.with(getApplicationContext()).load(lineupGlobalPlayer.getBibUrl()).into(binding.shirt);
            if (lineupGlobalPlayer.getAddedBy().equalsIgnoreCase(Functions.getPrefValue(getContext(),Constants.kUserID))){
                binding.deleteBtn.setVisibility(View.VISIBLE);
            }else{
                binding.deleteBtn.setVisibility(View.GONE);
            }
            if (selectedShirtId.isEmpty()){
               selectedShirtId =  lineupGlobalPlayer.getBibId();
            }

        }else{
            binding.deleteBtn.setVisibility(View.GONE);
            binding.toolbarTitle.setText("Add New Player");
            binding.tvBtnContinue.setText("Save");

        }

    }

    private void chooseSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.please_select_image_source));
        builder.setItems(new CharSequence[]
                        {getResources().getString(R.string.pick_image), getResources().getString(R.string.cancel)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                pickImage();
                                break;
                            case 1:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

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
                easyImage.openChooser(AddLineupGlobalPlayerActivity.this);
            }
        });
    }

    private void deleteBtnClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(("Delete"))
                .setMessage("Do you want to delete player?")
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeGlobalLineupPlayer(true);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();

    }

    private void addGlobalPlayer(boolean isLoader, String name, String arabicName, String teamId, String countryId, String bibId, String photo) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.addGlobalLineupPlayer(name,arabicName,teamId,countryId,bibId,photo);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), "Player Added Successfully!", FancyToast.SUCCESS);
                            finish();
                        }
                    }
                    catch (Exception e) {
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

    private void updateGlobalPlayer(boolean isLoader, String name, String arabicName, String teamId, String countryId, String bibId, String photo, String playerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.updateGlobalLineupPlayer(name,arabicName,teamId,countryId,bibId,photo,playerId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), "Player Updated Successfully!", FancyToast.SUCCESS);
                            finish();
                        }
                    }
                    catch (Exception e) {
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

    private void removeGlobalLineupPlayer(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.removeGlobalLineupPlayer(playerId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), "Player Deleted Successfully!", FancyToast.SUCCESS);
                            finish();
                        }
                    }
                    catch (Exception e) {
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
                                .start( AddLineupGlobalPlayerActivity.this);
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
    private void cutFacePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing"): null;
        RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface2.cutFace("https://www.cutout.pro/api/v1/matting?mattingType=3", part, RequestBody.create("true", MediaType.parse("multipart/form-data")), RequestBody.create("true", MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bmp = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                        photoFilePath = ((BaseActivity)getContext()).saveBitmap(bmp);
                        File file = new File(photoFilePath);
                        Glide.with(getApplicationContext()).load(file).into(binding.playerImgVu);
                        savePlayerPhoto(true,file);

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

    private void savePlayerPhoto(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.savePlayerPhoto(part);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            photoUrl = object.getJSONObject(Constants.kData).getString("photo");
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