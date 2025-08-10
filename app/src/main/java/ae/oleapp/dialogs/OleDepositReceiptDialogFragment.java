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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import ae.oleapp.databinding.OlefragmentDepositReceiptDialogBinding;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import id.zelory.compressor.Compressor;
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

public class OleDepositReceiptDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentDepositReceiptDialogBinding binding;
    private String clubId = "", bookingCash = "", inventoryCash = "", date = "", clubBankListItemId="";
    //private final File photoFile = null;
    private DepositReceiptDialogFragmentCallback fragmentCallback;

    private EasyImage easyImage;
    private Double grandTotal = 0.0;
    private File file = null; //new File("");
    private  int finalRemainingAmount;
    private final List<ClubBankLists> clubBankLists = new ArrayList<>();

    public OleDepositReceiptDialogFragment() {
        //Required empty public constructor
    }

    public void setFragmentCallback(DepositReceiptDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    public OleDepositReceiptDialogFragment(String clubId, String bookingCash, String inventoryCash, String fromDate,Double grandTotal) {
        this.clubId = clubId;
        this.bookingCash = bookingCash;
        this.inventoryCash = inventoryCash;
        this.date = fromDate;
        this.grandTotal = grandTotal;
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
        binding = OlefragmentDepositReceiptDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        getClubBanksList(clubId);
        binding.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int amt = Integer.parseInt(binding.etAmount.getText().toString());
                    if (amt <= grandTotal){
                        int remainingAmt = (int) (grandTotal - amt);
                        binding.etRemAmount.setText(String.valueOf(remainingAmt)); // Convert to String
                    }else{
                        binding.etRemAmount.setText("0");
                    }
//                    if (!binding.etAmount.getText().toString().equalsIgnoreCase("")){
//                        binding.etRemAmount.setText(String.valueOf(remainingAmt)); // Convert to String
//                    }else{
//                        binding.etRemAmount.setText(String.valueOf(grandTotal)); // Convert to String
//                    }
                } catch (NumberFormatException e) {
                    // Handle parsing error if the entered text is not a valid integer
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!binding.etAmount.getText().toString().equalsIgnoreCase("")){
//                    finalRemainingAmount = Integer.parseInt(binding.etAmount.getText().toString() + binding.etRemAmount.getText().toString()); //(int) (grandTotal - Integer.parseInt(binding.etAmount.getText().toString()));
//                }
                if (binding.etAmount.getText().toString().isEmpty()){
                    binding.etRemAmount.setText("");
                }
            }
        });



        binding.btnSubmit.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);
        binding.etBankName.setOnClickListener(this);
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
            if (binding.etAmount.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_amount), FancyToast.ERROR);
                return;
            }
            if (binding.etNote.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.write_note), FancyToast.ERROR);
                return;
            }
            if (binding.etRemAmount.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_remaining_amount), FancyToast.ERROR);
                return;
            }
            if (file == null) {
                Functions.showToast(getContext(), getString(R.string.take_receipt_photo), FancyToast.ERROR);
                return;
            }
            int getAmt = Integer.parseInt(binding.etAmount.getText().toString()) + Integer.parseInt( binding.etRemAmount.getText().toString());
            //Log.d("Get Total Amount", String.valueOf(getAmt+" "+grandTotal+" "+binding.etRemAmount.getText().toString()));
            if (getAmt == grandTotal){
                addReceiptAPI(true, binding.etNote.getText().toString(), binding.etAmount.getText().toString(), binding.etRemAmount.getText().toString());
            }else{
                Functions.showToast(getContext(),getString(R.string.rem_amount), FancyToast.ERROR);
            }
        } else if (v == binding.etBankName) {
            ClubBankSelectionListDialog dialog = new ClubBankSelectionListDialog(getContext(), getString(R.string.select_bank));
            dialog.setLists(clubBankLists);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new ClubBankSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<ClubBankLists> ClubBankLists, int pos) {
                    ClubBankLists item = clubBankLists.get(pos);
                    binding.etBankName.setText(item.getName());
                    clubBankListItemId = item.getId();
                }
            });
            dialog.show();

        }

        else if (v == binding.imgVuReceipt) {
            receiptClicked();
        }
    }

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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                file = new File(resultUri.getPath());
                Glide.with(getContext()).load(file).into(binding.imgVuReceipt);
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

    private void addReceiptAPI(boolean isLoader, String note, String amount, String remainingCash) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (file != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("receipt_photo", file.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addDepositSlip(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), clubBankListItemId),
                RequestBody.create(MediaType.parse("text/plain"), remainingCash),
                RequestBody.create(MediaType.parse("text/plain"), note),
                RequestBody.create(MediaType.parse("text/plain"), bookingCash),
                RequestBody.create(MediaType.parse("text/plain"), inventoryCash),
                RequestBody.create(MediaType.parse("text/plain"), amount),
                RequestBody.create(MediaType.parse("text/plain"), date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fragmentCallback.didUploadReceipt(OleDepositReceiptDialogFragment.this);
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

    public interface DepositReceiptDialogFragmentCallback {
        void didUploadReceipt(DialogFragment df);
    }
}