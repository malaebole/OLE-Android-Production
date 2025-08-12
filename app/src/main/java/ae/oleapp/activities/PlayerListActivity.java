package ae.oleapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
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
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePlayerListAdapter;
import ae.oleapp.adapters.PlayerGridAdapter;
import ae.oleapp.adapters.ShirtListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityPlayerListBinding;
import ae.oleapp.fragments.AdsSubscriptionPopupFragment;
import ae.oleapp.fragments.FriendRequestMessageDialogFragment;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OleEditPlayerActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleEndlessRecyclerViewScrollListener;
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

public class PlayerListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPlayerListBinding binding;
    private String name = "";
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private PlayerGridAdapter adapter;
    private final List<Shirt> shirtList = new ArrayList<>();
    private ShirtListAdapter shirtAdapter;
    private String selectedShirtId = "", photoFilePath = "";
    private PlayerInfo profileInfo = null;
    private EasyImage easyImage;
   // private int pageNo = 1;
   // private OleEndlessRecyclerViewScrollListener scrollListener;
    private GridLayoutManager gridLayoutManager;
    //private int playersLoaded = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

//        gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
//        binding.recyclerVu.setLayoutManager(gridLayoutManager);
//        scrollListener = new OleEndlessRecyclerViewScrollListener(gridLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Calculate the number of new players loaded in this loadMore event
////                int newPlayersCount = totalItemsCount - playersLoaded;
////                playersLoaded = totalItemsCount;
//
//                // Check if the number of new players loaded is greater than or equal to 15
//                if (totalItemsCount >= 50) {
//                    pageNo = page + 1;
//                   getPlayerListAPI(true);
//                }
//            }
//        };
//        binding.recyclerVu.addOnScrollListener(scrollListener);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(gridLayoutManager);
        adapter = new PlayerGridAdapter(getContext(), playerList, false, false);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        LinearLayoutManager shirtLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        shirtAdapter = new ShirtListAdapter(getContext(), shirtList);
        shirtAdapter.setItemClickListener(shirtClickListener);
        binding.shirtRecyclerVu.setAdapter(shirtAdapter);

        binding.addPlayerVu.setVisibility(View.VISIBLE);
        binding.chooseVu.setVisibility(View.GONE);
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));

        getBibsAPI(new BaseActivity.BibsCallback() {
            @Override
            public void getBibs(List<Shirt> shirts) {
                shirtList.clear();
                shirtList.addAll(shirts);
                shirtAdapter.notifyDataSetChanged();
            }
        });

        binding.tvProfileDesc.setVisibility(View.GONE);
        binding.profileVu.setVisibility(View.GONE);

        binding.btnClose.setOnClickListener(this);
        binding.btnLink.setOnClickListener(this);
        binding.btnAddPlayer.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.imgCard.setOnClickListener(this);
        binding.contactsVu.setOnClickListener(this);


        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.addPlayerVu.setVisibility(View.GONE);
                    binding.chooseVu.setVisibility(View.VISIBLE);
                    //getPlayerListAPI(true);

                }
                else {
                    binding.addPlayerVu.setVisibility(View.VISIBLE);
                    binding.chooseVu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    name = query;
                    //pageNo = 1;
                    getPlayerListAPI(true);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.text_should_3_charachters), FancyToast.ERROR);
                }
                binding.searchVu.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                name = binding.searchVu.getQuery().toString();
                if (binding.searchVu.getQuery().toString().equalsIgnoreCase("")){
                    //pageNo = 1;
                    getPlayerListAPI(false);
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            onBackPressed();
        }
        else if (v == binding.btnLink) {
            List<PlayerInfo> list = adapter.getSelectedList();
            if (list.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_player), FancyToast.ERROR);
                return;
            }
            String ids = "";
            for (PlayerInfo info : list) {
                if (ids.isEmpty()) {
                    ids = info.getId();
                }
                else {
                    ids = String.format("%s,%s", ids, info.getId());
                }
            }
            UserInfo info = Functions.getUserinfo(getContext());
            showMessageDialog(ids,info.getNickName());

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
            addPlayerAPI(true, binding.etName.getText().toString(), "", "");
        }
        else if (v == binding.btnAddPlayer) {
            addLineupPlayerAPI(true);
        }
        else if (v == binding.imgCard) {
            chooseSource();
        }
        else if (v == binding.contactsVu) {
            contactsClicked();
        }
    }

    protected void showMessageDialog(String ids, String name) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("FriendRequestMessageDialogFragment");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.addToBackStack(null);
        FriendRequestMessageDialogFragment dialogFragment = new FriendRequestMessageDialogFragment(name);
            dialogFragment.setDialogCallback((df, message) -> {
                df.dismiss();
                if (!message.isEmpty()){
                    linkPlayersAPI(true, ids);
                }
            });
            dialogFragment.show(fragmentTransaction, "FriendRequestMessageDialogFragment");
        }




    private void contactsClicked(){
        checkPermission();
    }

    private void checkPermission() {

        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
        }else {
            permissions = new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(PlayerListActivity.this, AddFromContactsActivity.class);
                startActivity(intent);
            }
        });
    }

    ShirtListAdapter.ItemClickListener shirtClickListener = new ShirtListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Shirt shirt = shirtList.get(pos);
            Glide.with(getApplicationContext()).load(shirt.getPhotoUrl()).into(binding.shirtImgVu);
            selectedShirtId = shirt.getId();
            shirtAdapter.setSelectedId(selectedShirtId);
        }
    };

    PlayerGridAdapter.ItemClickListener itemClickListener = new PlayerGridAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.selectPos(pos);
        }
    };

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
                easyImage.openChooser(PlayerListActivity.this);
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
                                .start(PlayerListActivity.this);
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

    private void getPlayerListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerList(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, 1, name, "", "", "", "", "", "", "", "lineup");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
//                            if (pageNo == 1) {
                                playerList.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    playerList.add(gson.fromJson(arr.get(i).toString(), PlayerInfo.class));
                                }
//                            }else{
//                                List<PlayerInfo> more = new ArrayList<>();
//                                for (int i = 0; i < arr.length(); i++) {
//                                    more.add(gson.fromJson(arr.get(i).toString(), PlayerInfo.class));
//                                }
//
//                                if (more.size() > 0) {
//                                    playerList.addAll(more);
//                                }
//                                else {
//                                    pageNo = pageNo-1;
//                                }
//                            }
                            if (playerList.size() == 0) {
                                Functions.showToast(getContext(), getString(R.string.player_not_found), FancyToast.ERROR);
                            }
//                            playerList.clear();
//                            for (int i = 0; i < arr.length(); i++) {
//                                playerList.add(gson.fromJson(arr.get(i).toString(), PlayerInfo.class));
//                            }
                        }
                        else {
                            playerList.clear();
                        }
                        adapter.notifyDataSetChanged();
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

    private void linkPlayersAPI(boolean isLoader, String ids) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.linkPlayers(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), "", ids);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent();
                            setResult(456, intent);
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
        call.enqueue(new Callback<>() {
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
                            Intent intent = new Intent();
                            intent.putExtra("player", new Gson().toJson(info));
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else if (object.getInt(Constants.kStatus) == 302) {
                            profileInfo = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            binding.profileVu.setVisibility(View.VISIBLE);
                            binding.tvProfileDesc.setVisibility(View.VISIBLE);
                            binding.tvName.setText(profileInfo.getNickName());
                            binding.tvPhone.setText(profileInfo.getPhone());
                            Glide.with(getApplicationContext()).load(profileInfo.getEmojiUrl()).into(binding.pEmojiImgVu);
                            Glide.with(getApplicationContext()).load(profileInfo.getBibUrl()).into(binding.pShirtImgVu);
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
        call.enqueue(new Callback<>() {
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
                            Intent intent = new Intent();
                            intent.putExtra("player", new Gson().toJson(info));
                            setResult(RESULT_OK, intent);
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

    private void cutFacePhotoAPI(boolean isLoader, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
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
                        photoFilePath = saveBitmap(bmp);
                        File file = new File(photoFilePath);
                        Glide.with(getApplicationContext()).load(file).into(binding.emojiImgVu);
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