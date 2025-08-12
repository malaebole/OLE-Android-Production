package ae.oleapp.inventory;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleInventoryCheckoutAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityInventoryCheckoutBinding;
import ae.oleapp.dialogs.OleInventorySaleDialogFragment;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Employee;
import ae.oleapp.models.OleInventoryItem;
import ae.oleapp.models.OleSelectionList;
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

public class OleInventoryCheckoutActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityInventoryCheckoutBinding binding;
    private List<OleInventoryItem> itemList = new ArrayList<>();
    private final List<Employee> employeeList = new ArrayList<>();
    private OleInventoryCheckoutAdapter adapter;
    private String filePath = "", currency = "", paymentMethod = "", clubId = "", empId = "";
    private double itemTotal = 0;

    private EasyImage easyImage;
    private File file = new File("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityInventoryCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.sell);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemList = new Gson().fromJson(bundle.getString("data", ""), new TypeToken<List<OleInventoryItem>>() {
            }.getType());
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleInventoryCheckoutAdapter(getContext(), itemList);
        binding.recyclerVu.setAdapter(adapter);

        calculatePrice();

        binding.receiptVu.setVisibility(View.GONE);
        binding.relEmployee.setVisibility(View.GONE);

        selectPaymentMethod(binding.relCash);

        binding.bar.backBtn.setOnClickListener(this);
        binding.relCash.setOnClickListener(this);
        binding.relPos.setOnClickListener(this);
        binding.relStaff.setOnClickListener(this);
        binding.etEmployee.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discountTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void discountTextChanged() {
        if (!binding.etDiscount.getText().toString().isEmpty()) {
            double discount = Double.parseDouble(binding.etDiscount.getText().toString());
            if (discount > itemTotal) {
                binding.etDiscount.setText(String.valueOf(itemTotal));
                binding.tvDiscount.setText(String.format(Locale.ENGLISH, "%.2f %s", itemTotal, currency));
                binding.tvGrandTotal.setText(String.format("0 %s", currency));
            } else {
                binding.tvDiscount.setText(String.format("%s %s", binding.etDiscount.getText().toString(), currency));
                binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", itemTotal - discount, currency));
            }
        } else {
            binding.tvDiscount.setText(String.format("0 %s", currency));
            binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", itemTotal, currency));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (v == binding.relCash || v == binding.relPos || v == binding.relStaff) {
            selectPaymentMethod(v);
        } else if (v == binding.imgVuReceipt) {
            receiptClicked();
        } else if (v == binding.etEmployee) {
            if (!employeeList.isEmpty()) {
                List<OleSelectionList> oleSelectionList = new ArrayList<>();
                for (Employee employee : employeeList) {
                    oleSelectionList.add(new OleSelectionList(employee.getId(), employee.getName()));
                }
                OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_employee), false);
                dialog.setLists(oleSelectionList);
                dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                    @Override
                    public void selectedItem(List<OleSelectionList> selectedItems) {
                        OleSelectionList selectedItem = selectedItems.get(0);
                        empId = selectedItem.getId();
                        binding.etEmployee.setText(selectedItem.getValue());
                    }
                });
                dialog.show();
            }
        } else if (v == binding.btnConfirm) {
            confirmClicked();
        }
    }

    private void confirmClicked() {
        if (paymentMethod.equalsIgnoreCase("pos") && filePath.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.take_receipt_photo), FancyToast.ERROR);
            return;
        } else if (paymentMethod.equalsIgnoreCase("staff") && empId.isEmpty() && binding.etMsg.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.write_note), FancyToast.ERROR);
            return;
        }
        if (!paymentMethod.equalsIgnoreCase("pos")) {
            filePath = "";
        }
        if (!paymentMethod.equalsIgnoreCase("staff")) {
            empId = "";
        }
        String jsonStr = "";
        try {
            JSONArray array = new JSONArray();
            for (OleInventoryItem item : itemList) {
                JSONObject object = new JSONObject();
                object.put("item_id", item.getId());
                object.put("item_qty", item.getQty());
                object.put("price", item.getSalePrice());
                array.put(object);
            }
            jsonStr = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!jsonStr.isEmpty()) {
            newSaleAPI(true, binding.etDiscount.getText().toString(), jsonStr, binding.etMsg.getText().toString());
        }
    }

//    private void receiptClicked() {
//        String[] permissions = new String[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
//        }else {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        }
//        //String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                FilePickerBuilder.getInstance()
//                        .setMaxCount(1)
//                        .setActivityTheme(R.style.AppTheme)
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
//                        .pickPhoto(getContext(), 1123);
//            }
//        });
//    }

    private void receiptClicked() {
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                filePath = resultUri.getPath();
                file = new File(filePath);
                Glide.with(getApplicationContext()).load(file).into(binding.imgVuReceipt);
                //updatePhotoAPI(true, file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1, 1)
//                                .start(getContext());

                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(false)
                                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .start(getContext());
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

    private void selectPaymentMethod(View view) {
        binding.relCash.setBackgroundResource(R.drawable.rounded_corner_bg_white);
        binding.relPos.setBackgroundResource(R.drawable.rounded_corner_bg_white);
        binding.relStaff.setBackgroundResource(R.drawable.rounded_corner_bg_white);
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuPos.setImageResource(R.drawable.uncheck);
        binding.imgVuStaff.setImageResource(R.drawable.uncheck);
        binding.tvCash.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.tvPos.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.tvStaff.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.receiptVu.setVisibility(View.GONE);
        binding.relEmployee.setVisibility(View.GONE);
        if (view == binding.relCash) {
            paymentMethod = "cash";
            binding.relCash.setBackgroundResource(R.drawable.rounded_corner_bg_blue_border);
            binding.imgVuCash.setImageResource(R.drawable.check);
            binding.tvCash.setTextColor(getResources().getColor(R.color.blueColorNew));
        } else if (view == binding.relPos) {
            paymentMethod = "pos";
            binding.relPos.setBackgroundResource(R.drawable.rounded_corner_bg_blue_border);
            binding.imgVuPos.setImageResource(R.drawable.check);
            binding.tvPos.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.receiptVu.setVisibility(View.VISIBLE);
        } else if (view == binding.relStaff) {
            paymentMethod = "staff";
            binding.relStaff.setBackgroundResource(R.drawable.rounded_corner_bg_blue_border);
            binding.imgVuStaff.setImageResource(R.drawable.check);
            binding.tvStaff.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.relEmployee.setVisibility(View.VISIBLE);
            if (employeeList.isEmpty()) {
                getEmployeesAPI(true);
            }
        }
    }

    private void calculatePrice() {
        itemTotal = 0;
        currency = "";
        for (OleInventoryItem item : itemList) {
            int qty = item.getQty();
            double price = Double.parseDouble(item.getSalePrice());
            itemTotal = itemTotal + (qty * price);
            if (currency.isEmpty()) {
                currency = item.getCurrency();
            }
        }
        binding.tvItemTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", itemTotal, currency));
        binding.tvDiscount.setText(String.format("0 %s", currency));
        binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%.2f %s", itemTotal, currency));
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                File file = new File(resultUri.getPath());
//                try {
//                    file = new Compressor(getContext())
//                            .setMaxHeight(1920).setMaxWidth(1080)
//                            .setQuality(75)
//                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                            .compressToFile(file);
//                    filePath = file.getAbsolutePath();
//                    Glide.with(getApplicationContext()).load(file).into(binding.imgVuReceipt);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Functions.showToast(getContext(), "Error in image compression", FancyToast.ERROR);
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//        else {
//            if (resultCode == RESULT_OK && requestCode == 1123) {
//                ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
//                if (dataList != null && dataList.size() > 0) {
//                    Uri uri = dataList.get(0);
//                    CropImage.activity(uri)
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                            .start(getContext());
//                }
//            }
//        }
//    }

    private void getEmployeesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployees(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            employeeList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                employeeList.add(gson.fromJson(arr.get(i).toString(), Employee.class));
                            }
                        } else {
                            employeeList.clear();
                        }
                    } catch (Exception e) {
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

    private void newSaleAPI(boolean isLoader, String discount, String json, String note) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        MultipartBody.Part filePart = null;
        if (!filePath.isEmpty()) {
            File photoFile = new File(filePath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            filePart = MultipartBody.Part.createFormData("receipt_photo", photoFile.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.newSaleItem(filePart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), discount),
                RequestBody.create(MediaType.parse("text/plain"), empId),
                RequestBody.create(MediaType.parse("text/plain"), paymentMethod),
                RequestBody.create(MediaType.parse("text/plain"), note),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), json));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            showSuccessDialog();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
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

    private void showSuccessDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("InventorySaleDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        OleInventorySaleDialogFragment dialogFragment = new OleInventorySaleDialogFragment();
        dialogFragment.setCancelable(false);
        dialogFragment.setFragmentCallback(new OleInventorySaleDialogFragment.InventorySaleDialogFragmentCallback() {
            @Override
            public void doneClicked(DialogFragment df) {
                df.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
        dialogFragment.show(fragmentTransaction, "InventorySaleDialogFragment");
    }
}