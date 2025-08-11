package ae.oleapp.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
import ae.oleapp.databinding.FragmentUpcomingExpenseHistoryBottomSheetDialogBinding;
import ae.oleapp.models.UpcomingExpenseDetailsModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpcomingExpenseHistoryBottomSheetDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentUpcomingExpenseHistoryBottomSheetDialogBinding binding;
    private String upcomingExpenseId = "", clubId = "", expenseId = "";
    private ResultDialogCallback dialogCallback;

    private UpcomingExpenseDetailsModel upcomingExpenseDetailsModel;
    Dialog dialog;


    public UpcomingExpenseHistoryBottomSheetDialogFragment(String upcomingExpenseId, String clubId, String expenseId) {
        this.clubId = clubId;
        this.expenseId = expenseId;
        this.upcomingExpenseId = upcomingExpenseId;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public UpcomingExpenseHistoryBottomSheetDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUpcomingExpenseHistoryBottomSheetDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getUpcomingExpenseDetails(clubId, upcomingExpenseId);

        binding.btnClose.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.btnPaid.setOnClickListener(this);

        return view;
    }

    private void getUpcomingExpenseDetails(String clubId, String upcomingExpenseId) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.upcomingExpenseHistory(Functions.getAppLang(getContext()), clubId, "", "", "", "", "", upcomingExpenseId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                upcomingExpenseDetailsModel = gson.fromJson(obj.toString(), UpcomingExpenseDetailsModel.class);
                                binding.expenseTv.setText(upcomingExpenseDetailsModel.getTitle());
                                binding.paymentMethodTv.setText(upcomingExpenseDetailsModel.getBankName());
                                binding.noteTv.setText(upcomingExpenseDetailsModel.getNotes());
                                binding.amountTv.setText(upcomingExpenseDetailsModel.getAmount());
                                binding.upcomingDateTv.setText(upcomingExpenseDetailsModel.getRecurringDate());
                                binding.paymentType.setText(upcomingExpenseDetailsModel.getRecurringType());
                                if (!upcomingExpenseDetailsModel.getReceipt().isEmpty()) {
                                    Glide.with(getActivity()).load(upcomingExpenseDetailsModel.getReceipt()).into(binding.invoiceImgVu);
                                }
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
                    if (t instanceof UnknownHostException) {
                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                    } else {
                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
            });
        }
    }

    private void showImageDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invoice_vu); // Custom dialog layout with an ImageView
        ImageView dialogImageView = dialog.findViewById(R.id.dialogImageView);
        ImageButton downloadImgBtn = dialog.findViewById(R.id.btnDownload);


        String imageUrl = upcomingExpenseDetailsModel.getReceipt();

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

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        } else if (v == binding.btnEdit) {
            dialogCallback.didSubmitResult(UpcomingExpenseHistoryBottomSheetDialogFragment.this, true, upcomingExpenseDetailsModel);
        } else if (v == binding.invoiceImgVu) {
            if (!upcomingExpenseDetailsModel.getReceipt().isEmpty()) {
                showImageDialog();
            }
        } else if (v == binding.btnPaid) {
            dialogCallback.didSubmitResult(UpcomingExpenseHistoryBottomSheetDialogFragment.this, false, upcomingExpenseDetailsModel);

        }


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


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isEditClicked, UpcomingExpenseDetailsModel upcomingExpenseDetailsModel);
    }
}