package ae.oleapp.dialogs;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hbb20.CCPCountry;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.activities.PlayerListActivity;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.FragmentAddPlayerBinding;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Shirt;
import ae.oleapp.player.OleEditPlayerActivity;
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

public class AddPlayerFragment extends DialogFragment implements View.OnClickListener {

    private FragmentAddPlayerBinding binding;
    private final List<Shirt> shirtList = new ArrayList<>();
    private ShirtListAdapter shirtAdapter;
    private String selectedShirtId = "", photoFilePath = "";
    private AddPlayerDialogCallback dialogCallback;
    private PlayerInfo playerInfo = null;
    private boolean isTeam = false;
    private PlayerInfo profileInfo = null;
    private EasyImage easyImage;

    public AddPlayerFragment() {
        // Required empty public constructor
    }

    public AddPlayerFragment(PlayerInfo userInfo, boolean isTeam) {
        this.playerInfo = userInfo;
        this.isTeam = isTeam;
    }

    public void setDialogCallback(AddPlayerDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new ShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);

       //((BaseActivity)getActivity())  checkx
        ((BaseActivity) getActivity()).getBibsAPI(new BaseActivity.BibsCallback() {
            @Override
            public void getBibs(List<Shirt> shirts) {
                shirtList.clear();
                shirtList.addAll(shirts);
                shirtAdapter.notifyDataSetChanged();
            }
        });

        binding.emailVu.setVisibility(View.GONE);
        binding.tvProfileDesc.setVisibility(View.GONE);
        binding.profileVu.setVisibility(View.GONE);
        if (playerInfo != null) {
            if (isTeam || playerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                binding.btnDelete.setVisibility(View.GONE);
            }
            else {
                binding.btnDelete.setVisibility(View.VISIBLE);
            }
            if (playerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                binding.emailVu.setVisibility(View.VISIBLE);
            }
            binding.btnLink.setVisibility(View.GONE);
            binding.tvDesc.setVisibility(View.GONE);
            binding.tvAdd.setText(R.string.update);
            binding.etName.setText(playerInfo.getNickName());
            binding.etEmail.setText(playerInfo.getEmail());
//            if (!userInfo.getCountryCode().isEmpty()) {
//                String phone = userInfo.getPhone().substring(userInfo.getCountryCode().length());
//                binding.etPhone.setText(phone);
//                int code = Integer.parseInt(userInfo.getCountryCode().substring(0));
//                binding.ccp.setCountryForPhoneCode(code);
//            }
            selectedShirtId = playerInfo.getBibId();
            Glide.with(getActivity()).load(playerInfo.getEmojiUrl()).into(binding.emojiImgVu);
            Glide.with(getActivity()).load(playerInfo.getBibUrl()).into(binding.shirtImgVu);
            //Glide.with(getActivity()).load(userInfo.getEmojiUrl()).placeholder(R.drawable.bibl).into(binding.emojiImgVu);
            if (playerInfo.getEmojiUrl().equalsIgnoreCase("")){
                binding.imgVuPlace.setVisibility(View.VISIBLE);
            }else{
                binding.imgVuPlace.setVisibility(View.GONE);
            }

        }
        else {
            binding.btnLink.setVisibility(View.VISIBLE);
            binding.tvDesc.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.GONE);
            binding.tvAdd.setText(R.string.add_now);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.btnLink.setOnClickListener(this);
        binding.imgCard.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnAddPlayer.setOnClickListener(this);

        return view;
    }

    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            Glide.with(requireContext()).load(shirt.getPhotoUrl()).into(binding.shirtImgVu);
            //Glide.with(getContext()).load(shirt.getPhotoUrl()).into(binding.shirtImgVu);
            selectedShirtId = shirt.getId();
            shirtAdapter.setSelectedId(selectedShirtId);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnAdd) {
            if (binding.etName.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.enter_nick_name), FancyToast.ERROR);
                return;
            }
//            if (Functions.isArabic(binding.etName.getText().toString())) {
//                Functions.showToast(getContext(), getString(R.string.enter_nick_name_english), FancyToast.ERROR);
//                return;
//            }
            if (selectedShirtId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_bib), FancyToast.ERROR);
                return;
            }
//            String countryCode = binding.ccp.getSelectedCountryCodeWithPlus();
//            if (countryCode.isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
//                return;
//            }
//            if (binding.etPhone.getText().toString().isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
//                return;
//            }
//            if (binding.etPhone.getText().toString().startsWith("0")) {
//                Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
//                return;
//            }
            if (playerInfo == null) {
                addPlayerAPI(true, binding.etName.getText().toString(), "", "");
            }
            else {
                updatePlayerAPI(true, binding.etName.getText().toString(), playerInfo.getId(), "", "", playerInfo.getFriendShipId(), binding.etEmail.getText().toString());
            }
        }
        else if (v == binding.btnLink) {
            Intent intent = new Intent(getContext(), PlayerListActivity.class);
            startActivity(intent);
        }
        else if (v == binding.btnAddPlayer) {
            addLineupPlayerAPI(true);
        }
        else if (v == binding.imgCard) {
            chooseSource();
        }
        else if (v == binding.btnDelete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.delete))
                    .setMessage(getResources().getString(R.string.do_you_want_delete_player))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePlayerAPI(true, playerInfo.getId(), playerInfo.getFriendShipId());
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }

    private void chooseSource() {
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
                                if (!playerInfo.getEmojiUrl().isEmpty()){
                                    deletePhotoAPI(true);
                                }
                                else{
                                    photoFilePath = "";
                                    Glide.with(getContext()).load("").into(binding.pEmojiImgVu);
                                }
                                break;
                            case 2:
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
                easyImage.openChooser(AddPlayerFragment.this);
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
            easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1,1)
                                .start(getActivity(), AddPlayerFragment.this);
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

    private void addPlayerAPI(boolean isLoader, String name, String countryCode, String phone) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("emoji", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addFriend(filePart,
                RequestBody.create(Functions.getAppLang(getContext()), MediaType.parse("multipart/form-data")),
                RequestBody.create(Functions.getPrefValue(getContext(), Constants.kUserID), MediaType.parse("multipart/form-data")),
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(selectedShirtId, MediaType.parse("multipart/form-data")),
                RequestBody.create(countryCode, MediaType.parse("multipart/form-data")),
                RequestBody.create(phone, MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Gson gson = new Gson();
                            PlayerInfo info = gson.fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            dialogCallback.didAddPlayer(info);
                            dismiss();
                        }
                        else if (object.getInt(Constants.kStatus) == 302) {
                            profileInfo = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            binding.profileVu.setVisibility(View.VISIBLE);
                            binding.tvProfileDesc.setVisibility(View.VISIBLE);
                            binding.tvName.setText(profileInfo.getNickName());
                            binding.tvPhone.setText(profileInfo.getPhone());
                            Glide.with(getContext()).load(profileInfo.getEmojiUrl()).into(binding.pEmojiImgVu);
                            Glide.with(getContext()).load(profileInfo.getBibUrl()).into(binding.pShirtImgVu);
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

    private void updatePlayerAPI(boolean isLoader, String name, String id, String countryCode, String phone, String friendShipId, String email) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("emoji", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateFriend(filePart,
                RequestBody.create(Functions.getAppLang(getContext()), MediaType.parse("multipart/form-data")),
                RequestBody.create(Functions.getPrefValue(getContext(), Constants.kUserID), MediaType.parse("multipart/form-data")),
                RequestBody.create(id, MediaType.parse("multipart/form-data")),
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(selectedShirtId, MediaType.parse("multipart/form-data")),
                RequestBody.create(countryCode, MediaType.parse("multipart/form-data")),
                RequestBody.create(phone, MediaType.parse("multipart/form-data")),
                RequestBody.create(friendShipId, MediaType.parse("multipart/form-data")),
                RequestBody.create(email, MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Gson gson = new Gson();
                            PlayerInfo info = gson.fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            dialogCallback.didUpdatePlayer(info);
                            dismiss();
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
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
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
                        photoFilePath = ((BaseActivity)getActivity()).saveBitmap(bmp);
                        File file = new File(photoFilePath);
                        Glide.with(getContext()).load(file).into(binding.emojiImgVu);
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

    private void deletePlayerAPI(boolean isLoader, String id, String friendShipId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deletePlayer(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), id, friendShipId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dialogCallback.didDeletePlayer(id);
                            dismiss();
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

    private void deletePhotoAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removePhoto(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerInfo.getFriendShipId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            playerInfo.setEmojiUrl("");
                            dialogCallback.didUpdatePlayer(playerInfo);
                            dismiss();
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

    private void addLineupPlayerAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addLineupUser(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), profileInfo.getId(), profileInfo.getName(), profileInfo.getNickName(), profileInfo.getPhone(), profileInfo.getBibId(), profileInfo.getIsLink(), profileInfo.getEmojiName());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Gson gson = new Gson();
                            PlayerInfo info = gson.fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            dialogCallback.didAddPlayer(info);
                            dismiss();
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

    public interface AddPlayerDialogCallback {
        void didAddPlayer(PlayerInfo userInfo);
        void didUpdatePlayer(PlayerInfo userInfo);
        void didDeletePlayer(String id);
    }
}