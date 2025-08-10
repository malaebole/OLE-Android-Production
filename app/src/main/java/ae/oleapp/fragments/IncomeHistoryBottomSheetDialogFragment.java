package ae.oleapp.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentBestPlayerDialogBinding;
import ae.oleapp.databinding.FragmentIncomeHistoryBottomSheetDialogBinding;
import ae.oleapp.models.BankEarning;
import ae.oleapp.models.EmployeeSalary;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.models.PartnerEarning;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeHistoryBottomSheetDialogFragment extends DialogFragment implements View.OnClickListener{

            private FragmentIncomeHistoryBottomSheetDialogBinding binding;
//            private String incomeId = "";
            private ResultDialogCallback dialogCallback;
            private IncomeDetailsModel incomeDetailsModel;
            private String IncomeId = "";
            private String incomeFrom = "";
            private String Amount = "";
            private String Note = "";
            private String ReceiptUrl = "";
            private String bankName = "";
            private boolean paymentmethod;
            Dialog dialog;



    public IncomeHistoryBottomSheetDialogFragment(String IncomeId, String incomeFrom, String Amount, String Note, String ReceiptUrl, Boolean paymentmethod,String bankName) {
//        this.incomeDetailsModel = incomeDetailsModel;
        this.IncomeId = IncomeId;
        this.incomeFrom = incomeFrom;
        this.Amount = Amount;
        this.Note = Note;
        this.ReceiptUrl = ReceiptUrl;
        this.bankName = bankName;
        this.paymentmethod = paymentmethod;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public IncomeHistoryBottomSheetDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIncomeHistoryBottomSheetDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //getincomeDetails(incomeId);

        if (paymentmethod){
            binding.paymentMethodLayout.setVisibility(View.VISIBLE);
            binding.paymentMethodTv.setText(bankName);
        }else {
            binding.paymentMethodLayout.setVisibility(View.GONE);
        }


        binding.sourceTv.setText(incomeFrom);
        binding.noteTv.setText(Note);
        binding.amountTv.setText(Amount);
        if (!ReceiptUrl.isEmpty()){
            Glide.with(getActivity()).load(ReceiptUrl).into(binding.invoiceImgVu);
        }
        binding.btnClose.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);

        return view;
    }


    private void showImageDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invoice_vu); // Custom dialog layout with an ImageView
        ImageView dialogImageView = dialog.findViewById(R.id.dialogImageView);
        ImageButton downloadImgBtn = dialog.findViewById(R.id.btnDownload);

        String imageUrl = ReceiptUrl;

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
                saveToGallery(imageUrl);
            }
        });


        dialog.show();
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
                Glide.with(getContext())
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

    @Override
    public void onClick(View v) {

        if (v == binding.btnClose){
            dismiss();

        }
        else if (v == binding.invoiceImgVu) {
            if (!ReceiptUrl.isEmpty()){
                showImageDialog();
            }
        }
        else if (v == binding.btnEdit){
            dialogCallback.didSubmitResult(IncomeHistoryBottomSheetDialogFragment.this, true);
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isEditClicked);
    }
}