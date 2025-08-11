package ae.oleapp.dialogs;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.PlayerListActivity;
import ae.oleapp.adapters.OlePositionListAdapter;
import ae.oleapp.databinding.OlefragmentAddPlayerDialogBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerPosition;
import ae.oleapp.player.OleCustomCameraActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleAddPlayerDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentAddPlayerDialogBinding binding;
    private String positionId = "";
    private AddPlayerDialogCallback dialogCallback;
    private final List<OlePlayerPosition> positionList = new ArrayList<>();
    private OlePositionListAdapter adapter;
    private OlePlayerInfo info;
    private final String photoFilePath = "";

    public OleAddPlayerDialogFragment() {
        // Required empty public constructor
    }

    public OleAddPlayerDialogFragment(OlePlayerInfo info) {
        this.info = info;
    }

    public void setDialogCallback(AddPlayerDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddPlayerDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePositionListAdapter(getContext(), R.layout.olerank_date, positionList, -1);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        if (info != null) {
            binding.etName.setText(info.getName());
            Glide.with(requireActivity()).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.imgVu);
            positionId = info.getPlayerPosition().getPositionId();
            binding.tvAddNow.setText(R.string.update);
        }
        else {
            binding.tvAddNow.setText(R.string.add_now);
        }

        getPlayerPositionAPI(true);

        binding.btnAdd.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnAdd) {
            addClicked();
        }
        else if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnPhoto) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.image))
                    .setMessage(getResources().getString(R.string.please_select_image_source))
                    .setPositiveButton(getResources().getString(R.string.camera), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pickImage(false);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.gallery), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pickImage(true);
                        }
                    })
                    .setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }

//    private void pickImage(boolean isGallery) {
//
//        String[] permissions = new String[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
//        }else{
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        }
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                if (isGallery) {
//                    FilePickerBuilder.getInstance()
//                            .setMaxCount(1)
//                            .setActivityTheme(R.style.AppThemePlayer)
//                            .setActivityTitle(getString(R.string.image))
//                            .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
//                            .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
//                            .enableVideoPicker(false)
//                            .enableCameraSupport(false)
//                            .showGifs(false)
//                            .showFolderView(false)
//                            .enableSelectAll(false)
//                            .enableImagePicker(true)
//                            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                            .pickPhoto(OleAddPlayerDialogFragment.this, 1124);
//                }
//                else {
//                    Intent intent = new Intent(getContext(), OleCustomCameraActivity.class);
//                    intent.putExtra("is_gallery", false);
//                    startActivityForResult(intent, 1123);
//                }
//            }
//        });
//    }


    private void pickImage(boolean isGallery) {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        Permissions.check(getContext(), permissions, null /*rationale*/, null /*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // Choose the method based on whether the user wants to pick from the gallery or take a new photo
                if (isGallery) {
                    ImagePicker.with(OleAddPlayerDialogFragment.this)
                            .crop() // Optionally enable cropping
                            .compress(1024) // Compress image to 1024 KB
                            .maxResultSize(1080, 1080) // Set maximum dimensions
                            .start(1124); // Start the ImagePicker with the request code 1124
                } else {
                    Intent intent = new Intent(getContext(), OleCustomCameraActivity.class);
                    intent.putExtra("is_gallery", false);
                    startActivityForResult(intent, 1123);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1123) {
                // Handle result from custom camera activity
                String photoFilePath = data.getExtras().getString("file_path");
                File file = new File(photoFilePath);
                Glide.with(requireActivity()).load(file).into(binding.imgVu);
            } else if (requestCode == 1124) {
                // Handle result from ImagePicker
                Uri uri = data.getData();
                if (uri != null) {
                    String filePath = getFilePathFromUri(getContext(), uri);
                    if (filePath != null) {
                        Intent intent = new Intent(getContext(), OleCustomCameraActivity.class);
                        intent.putExtra("is_gallery", true);
                        intent.putExtra("file_path", filePath);
                        startActivityForResult(intent, 1123);
                    }
                }
            }
        }
    }

    // Utility method to get file path from URI
    public String getFilePathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return null;
    }




//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1123 && resultCode == RESULT_OK) {
//            photoFilePath = data.getExtras().getString("file_path");
//            File file = new File(photoFilePath);
//            Glide.with(getApplicationContext()).load(file).into(binding.imgVu);
//        }
//        else if (requestCode == 1124 && resultCode == RESULT_OK) {
//            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
//            if (dataList != null && dataList.size() > 0) {
//                Uri uri = dataList.get(0);
//                try {
//                    String filePath = ContentUriUtils.INSTANCE.getFilePath(getContext(), uri);
//                    Intent intent = new Intent(getContext(), OleCustomCameraActivity.class);
//                    intent.putExtra("is_gallery", true);
//                    intent.putExtra("file_path", filePath);
//                    startActivityForResult(intent, 1123);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    OlePositionListAdapter.ItemClickListener clickListener = new OlePositionListAdapter.ItemClickListener() {
        @Override
        public void onItemClicked(View view, int pos) {
            positionId = positionList.get(pos).getPositionId();
            adapter.setSelectedIndex(pos);
        }
    };

    private void addClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (positionId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_position), FancyToast.ERROR);
            return;
        }
        if (info == null) {
            addPlayerAPI(true, binding.etName.getText().toString());
        }
        else {
            updatePlayerAPI(true, info.getId(), binding.etName.getText().toString());
        }
    }

    private void getPlayerPositionAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerPosition(Functions.getAppLang(getActivity()));
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
                            positionList.clear();
                            int selectedIndex = -1;
                            for (int i = 0; i < arr.length(); i++) {
                                OlePlayerPosition position = gson.fromJson(arr.get(i).toString(), OlePlayerPosition.class);
                                positionList.add(position);
                                if (position.getPositionId().equalsIgnoreCase(positionId)) {
                                    selectedIndex = i;
                                }
                            }
                            adapter.setSelectedIndex(selectedIndex);
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

    private void addPlayerAPI(boolean isLoader, String name) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.manageManualPlayerList(filePart,
                RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                RequestBody.create(MediaType.parse("multipart/form-data"), name),
                RequestBody.create(MediaType.parse("multipart/form-data"), positionId),
                RequestBody.create(MediaType.parse("multipart/form-data"), "add"));
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
                            OlePlayerInfo info = gson.fromJson(object.getJSONObject(Constants.kData).toString(), OlePlayerInfo.class);
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

    private void updatePlayerAPI(boolean isLoader, String playerId, String name) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.manageManualPlayerList(filePart,
                RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("multipart/form-data"), playerId),
                RequestBody.create(MediaType.parse("multipart/form-data"), name),
                RequestBody.create(MediaType.parse("multipart/form-data"), positionId),
                RequestBody.create(MediaType.parse("multipart/form-data"), "update"));
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
                            OlePlayerInfo info = gson.fromJson(object.getJSONObject(Constants.kData).toString(), OlePlayerInfo.class);
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
        void didAddPlayer(OlePlayerInfo olePlayerInfo);
        void didUpdatePlayer(OlePlayerInfo olePlayerInfo);
    }
}
