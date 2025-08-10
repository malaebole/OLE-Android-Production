package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddExpenseBinding;
import ae.oleapp.databinding.ActivityAddIncomeBinding;
import ae.oleapp.dialogs.ClubBankSelectionListDialog;
import ae.oleapp.dialogs.ExpenseSelectionListDialog;
import ae.oleapp.dialogs.SelectionListDialog;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.Country;
import ae.oleapp.models.ExpenseDetailsModel;
import ae.oleapp.models.ExpenseHistory;
import ae.oleapp.models.ExpenseList;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.SelectionList;
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

public class AddExpenseActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddExpenseBinding binding;
    private boolean update;
    private ExpenseDetailsModel expenseDetailsModel;

    private final List<ClubBankLists> clubBankLists = new ArrayList<>();
    private final List<ExpenseList> expenseLists = new ArrayList<>();
    private String recordId = "", clubId = "", expenseListItemId = "", clubBankListItemId = "", expenseId = "", photoFilePath = "";
    private EasyImage easyImage;
    private File file = new File("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            expenseDetailsModel = new Gson().fromJson(bundle.getString("result", ""), ExpenseDetailsModel.class);
            update = bundle.getBoolean("is_update");
            recordId = bundle.getString("record_id", "");
            expenseId = bundle.getString("expense_id", "");
            clubId = bundle.getString("club_id", "");
        }

        populateData(update);
        getExpenseList();
        getClubBanksList(clubId);

        binding.btnSubmit.setOnClickListener(this);
        binding.etExpenseSource.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.etBankName.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (binding.etAmount.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.amount_cannot_be_empty), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            if (binding.etExpenseSource.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_expense), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            if (expenseListItemId.isEmpty()) {
                expenseListItemId = expenseId;
            }

            if (update){
                updateExpense();
            }else{
                addExpense();
            }

        }
        else if (v == binding.etExpenseSource) {
            ExpenseSelectionListDialog dialog = new ExpenseSelectionListDialog(getContext(), getString(R.string.select_expense));
            dialog.setLists(expenseLists);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new ExpenseSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<ExpenseList> expenseLists) {
                    ExpenseList item = expenseLists.get(0);
                    binding.etExpenseSource.setText(item.getName());
                    expenseListItemId =  item.getId();
                }
            });
            dialog.show();

        }
        else if (v == binding.btnClose) {
            finish();
        }
        else if (v == binding.invoiceImgVu) {
            updateInvoice();
        }
        else if (v == binding.etBankName) {
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
    }


    private void updateExpense(){

        if (clubBankListItemId.isEmpty()){
            clubBankListItemId = expenseDetailsModel.getBankId();
        }
        updateExpenseDetails(true, clubBankListItemId, expenseListItemId, binding.etAmount.getText().toString(), binding.etNote.getText().toString(),file);
    }

    private  void addExpense(){
        addExpenseDetails(true, clubBankListItemId, expenseListItemId, binding.etAmount.getText().toString(), binding.etNote.getText().toString(),file);

    }

    private void updateInvoice() {
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
                easyImage.openChooser(getContext());
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

    private void updateExpenseDetails(boolean isLoader, String bankid, String expenseId, String amount, String note, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateExpense(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),recordId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),expenseId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),bankid),
                    RequestBody.create(MediaType.parse("multipart/form-data"),amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.expense_updated), FancyToast.SUCCESS);
                                finish();
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

    private void addExpenseDetails(boolean isLoader, String bankid, String expenseId, String amount, String note, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addFinanceExpense(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),expenseId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),bankid),
                    RequestBody.create(MediaType.parse("multipart/form-data"),amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.expense_added), FancyToast.SUCCESS);
                                finish();
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //file = new File(resultUri.getPath());
                photoFilePath = resultUri.getPath();
                file = new File(photoFilePath);
                Glide.with(getContext()).load(file).into(binding.invoiceImgVu);
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
                                .start(getContext());
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

    private void getExpenseList() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getExpenseList(Functions.getAppLang(getContext()));
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
                                    expenseLists.add(gson.fromJson(data.get(i).toString(), ExpenseList.class));
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
                    } else {
                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
            });
        }
    }

    private void populateData(Boolean isUpdate) {

        if (isUpdate) {
            binding.title.setText(R.string.update);
            binding.btnTvAddUpdate.setText(R.string.update);
            binding.etExpenseSource.setText(expenseDetailsModel.getTitle());
            binding.etAmount.setText(expenseDetailsModel.getAmount());
            binding.etBankName.setText(expenseDetailsModel.getBankName());
            binding.etNote.setText(expenseDetailsModel.getNotes());
            if (!expenseDetailsModel.getReceipt().isEmpty()){
                Glide.with(getContext()).load(expenseDetailsModel.getReceipt()).into(binding.invoiceImgVu);
            }else{
                Glide.with(getContext()).load(R.drawable.attachment_img).into(binding.invoiceImgVu);
            }

        } else {

            binding.title.setText(getString(R.string.add_expense));
            binding.updateTv.setText(getString(R.string.add_expense));
            binding.btnTvAddUpdate.setText(R.string.add);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

    }
}