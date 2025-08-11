package ae.oleapp.dialogs;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.BanksAdapter;
import ae.oleapp.adapters.OleAddExpenseAdapter;
import ae.oleapp.adapters.OleHorizontalBankAdapter;
import ae.oleapp.databinding.OlefragmentAddExpenseDialogBinding;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.OleClubExpense;
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

public class OleAddExpenseDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentAddExpenseDialogBinding binding;
    private String expenseId = "", clubId = "", date = "", bankId="";
    private List<OleClubExpense> expenseList = new ArrayList<>();
    private OleAddExpenseAdapter adapter;
    private OleHorizontalBankAdapter bankAdapter;
    //private File photoFile = null;
    private AddExpenseDialogFragmentCallback fragmentCallback;

    private EasyImage easyImage;
    private File file = null; //new File("");
    private final List<ClubBankLists> clubBankLists = new ArrayList<>();

    public OleAddExpenseDialogFragment() {
        // Required empty public constructor
    }

    public OleAddExpenseDialogFragment(String clubId, List<OleClubExpense> expenseList, String date) {
        this.clubId = clubId;
        this.expenseList = expenseList;
        this.date = date;
    }

    public void setFragmentCallback(AddExpenseDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddExpenseDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleAddExpenseAdapter(getContext(), expenseList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        LinearLayoutManager bankLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.bankRecyclerVu.setLayoutManager(bankLayoutManager);
        bankAdapter = new OleHorizontalBankAdapter(getContext(), clubBankLists);
        bankAdapter.setOnItemClickListener(bankItemClickListener);
        binding.bankRecyclerVu.setAdapter(bankAdapter);

        binding.btnSubmit.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);
        //binding.etBankName.setOnClickListener(this);
        getClubBanksList(clubId);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (expenseId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_expense), FancyToast.ERROR);
                return;
            }
            if (bankId.isEmpty()){
                Functions.showToast(getContext(), getString(R.string.select_bank), FancyToast.ERROR);
                return;
            }
            if (binding.etAmount.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_amount), FancyToast.ERROR);
                return;
            }
            addExpenseAPI(true, binding.etAmount.getText().toString());
        }
        else if (v == binding.imgVuReceipt) {
            receiptClicked();
        }
    }

    OleAddExpenseAdapter.OnItemClickListener itemClickListener = new OleAddExpenseAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            expenseId = expenseList.get(pos).getId();
            adapter.setSelectedIndex(pos);
        }
    };
    OleHorizontalBankAdapter.OnItemClickListener bankItemClickListener = new OleHorizontalBankAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            bankId = clubBankLists.get(pos).getId();
            bankAdapter.setSelectedIndex(pos);
        }
    };

    private void receiptClicked() {
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
                easyImage.openChooser(getActivity());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                file = new File(resultUri.getPath());
                Glide.with(requireActivity()).load(file).into(binding.imgVuReceipt);
                //updatePhotoAPI(true, file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1,1)
//                                .start(getActivity());

                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(false)
                                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .start(getActivity());
                    }
                }

                @Override
                public void onImagePickerError(Throwable error, MediaSource source) {
                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                }

                @Override
                public void onCanceled(@NonNull MediaSource mediaSource) {
                    //canceled
                }
            });
        }
    }

//    private void receiptClicked() {
//       // String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        String[] permissions = new String[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
//        }else {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        }
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                FilePickerBuilder.getInstance()
//                        .setMaxCount(1)
//                        .setActivityTheme(R.style.AppThemePlayer)
//                        .setActivityTitle(getString(R.string.image))
//                        .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
//                        .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
//                        .enableVideoPicker(false)
//                        .enableCameraSupport(true)
//                        .showGifs(false)
//                        .showFolderView(false)
//                        .enableSelectAll(false)
//                        .enableImagePicker(true)
//                        .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                        .pickPhoto(OleAddExpenseDialogFragment.this, 1121);
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                File file = new File(resultUri.getPath());
//                try {
//                    photoFile = new Compressor(getContext())
//                            .setMaxHeight(1920).setMaxWidth(1080)
//                            .setQuality(75)
//                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                            .compressToFile(file);
//                    Glide.with(getApplicationContext()).load(photoFile).into(binding.imgVuReceipt);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Functions.showToast(getContext(), "Error in image compression", FancyToast.ERROR);
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//        else {
//            if (resultCode == RESULT_OK && requestCode == 1121) {
//                ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
//                if (dataList != null && dataList.size() > 0) {
//                    Uri uri = dataList.get(0);
//                    CropImage.activity(uri)
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                            .start(getActivity(), OleAddExpenseDialogFragment.this);
//                }
//            }
//        }
//    }

    private void getClubBanksList(String clubId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubBanksList(Functions.getAppLang(getContext()), clubId,"");
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                for (int i = 0; i < data.length(); i++) {
                                    clubBankLists.add(gson.fromJson(data.get(i).toString(), ClubBankLists.class));
                                }
                                bankAdapter.notifyDataSetChanged();
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

    private void addExpenseAPI(boolean isLoader, String amount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (file != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("receipt_photo", file.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addExpense(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), bankId),
                RequestBody.create(MediaType.parse("text/plain"), expenseId),
                RequestBody.create(MediaType.parse("text/plain"), amount),
                RequestBody.create(MediaType.parse("text/plain"), date));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            fragmentCallback.didAddExpense(OleAddExpenseDialogFragment.this, new Gson().fromJson(obj.toString(), OleClubExpense.class));
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

    public interface AddExpenseDialogFragmentCallback {
        void didAddExpense(DialogFragment df, OleClubExpense expense);
    }
}