package ae.oleapp.fragments;



import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.PdfViewerActivity;
import ae.oleapp.databinding.FragmentDocDetailsBottomSheetDialogBinding;
import ae.oleapp.models.DocDetails;
import ae.oleapp.models.DocList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DocDetailsBottomSheetDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentDocDetailsBottomSheetDialogBinding binding;
    private String fileId = "", clubId="";
    private ResultDialogCallback dialogCallback;

    private DocList docLists;
    private DocDetails docDetails;
    Dialog dialog;


    public DocDetailsBottomSheetDialogFragment(String fileId, String clubId, DocDetails docDetails) {
        this.fileId = fileId;
        this.docDetails = docDetails;
        this.clubId = clubId;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public DocDetailsBottomSheetDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDocDetailsBottomSheetDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getFile(fileId,clubId);

        //showDocDetails(fileId,clubId);

        binding.btnClose.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);

        return view;
    }

    private void getFile(String fileId, String clubId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFile(Functions.getAppLang(getContext()), clubId, fileId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                docLists = gson.fromJson(obj.toString(), DocList.class);
                                showDocDetails();
                            }
                        }  catch (Exception e) {
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
    //    private void showImageDialog() {
//        Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_invoice_vu); // Custom dialog layout with an ImageView
//        ImageView dialogImageView = dialog.findViewById(R.id.dialogImageView);
//
//        String imageUrl = docLists.getPhotoUrl();
//
//        Glide.with(getActivity())
//                .load(imageUrl)
//                .into(new CustomTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        // Image loaded successfully
//                        dialogImageView.setImageDrawable(resource);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        // Called when the resource is cleared
//                    }
//                });
//
//
//        dialog.show();
//    }
    private void showDialog() {

        if (!docLists.getPhotoUrl().isEmpty()){
            if (docLists.getPhotoUrl().contains(".jpg")
                    || docLists.getPhotoUrl().contains(".png")
                    || docLists.getPhotoUrl().contains(".jpeg")){
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_invoice_vu); // Custom dialog layout with an ImageView
                ImageView dialogImageView = dialog.findViewById(R.id.dialogImageView);
                ImageButton downloadImgBtn = dialog.findViewById(R.id.btnDownload);


                String fileUrl = docLists.getPhotoUrl();

                if (isImageFile(fileUrl)) {
                    String imageUrl = docLists.getPhotoUrl();
                    Glide.with(getActivity())
                            .load(imageUrl)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    // Image loaded successfully
                                    dialogImageView.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Called when the resource is cleared
                                }
                            });

                    downloadImgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveToGallery(fileUrl);
                        }
                    });

                }
                dialog.show();
            }
            else{
                String fileUrl = docLists.getPhotoUrl();
                if (isPdfFile(fileUrl)) {

                    Intent intent = new Intent(getActivity(), PdfViewerActivity.class);
                    intent.putExtra("file_url", fileUrl);
                    intent.putExtra("folder_name", docDetails.getName());
                    intent.putExtra("file_name",  docLists.getName());
                    startActivity(intent);

                }
            }
        }

    }

    private boolean isImageFile(String fileUrl) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String extension : imageExtensions) {
            if (fileUrl.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPdfFile(String fileUrl) {
        return fileUrl.toLowerCase().endsWith(".pdf");
    }

    private void saveToGallery(String fileUrl) {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        Permissions.check(getContext(), permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Glide.with(requireActivity())
                        .asBitmap()
                        .load(fileUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    saveBitmapToGallery(getContext(), resource);
                                    Functions.showToast(getContext(), getString(R.string.image_saved), FancyToast.SUCCESS, FancyToast.LENGTH_SHORT);
                                    getDialog().dismiss();
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Functions.showToast(getContext(), getString(R.string.failed_save_image), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Called when the resource is cleared
                            }
                        });
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // Handle permission denial
            }
        });
    }

    private void saveBitmapToGallery(Context context, Bitmap bitmap) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        // Get the directory for saving the image
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        // Save the bitmap to the image file
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        // Notify the gallery about the new image
        MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, null);
    }


    private void showDocDetails(){

        binding.tvName.setText(docDetails.getName());
        binding.tvDocType.setText(docLists.getName());
        binding.tvDocNumber.setText(docLists.getNumber());
        binding.tvIssueDate.setText(docLists.getIssueDate());
        binding.tvExpiryDate.setText(docLists.getExpiryDate());

        if (!docLists.getPhotoUrl().isEmpty()){
            if (docLists.getPhotoUrl().contains("jpg")
                    || docLists.getPhotoUrl().contains("png")
                    || docLists.getPhotoUrl().contains("jpeg")){
                Glide.with(getActivity()).load(docLists.getPhotoUrl()).into(binding.invoiceImgVu);
            }else{
                Glide.with(getActivity()).load(R.drawable.pdf_icon).into(binding.invoiceImgVu);
            }
        }else{
            Glide.with(getActivity()).load(R.drawable.attachment_img).into(binding.invoiceImgVu);
        }


    }

    @Override
    public void onClick(View v) {

        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.invoiceImgVu) {
            showDialog();

        }
        else if (v == binding.btnEdit){
            dialogCallback.didSubmitResult(DocDetailsBottomSheetDialogFragment.this, true, fileId, docDetails);
        }

    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isEditClicked, String fileId, DocDetails docDetails);
    }
}