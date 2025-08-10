package ae.oleapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddExpenseBinding;
import ae.oleapp.databinding.ActivityAddUpcomingExpenseBinding;
import ae.oleapp.dialogs.ClubBankSelectionListDialog;
import ae.oleapp.dialogs.ExpenseSelectionListDialog;
import ae.oleapp.dialogs.ExpenseTypeDialog;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.ExpenseDetailsModel;
import ae.oleapp.models.ExpenseList;
import ae.oleapp.models.ExpenseTypeModel;
import ae.oleapp.models.UpcomingExpense;
import ae.oleapp.models.UpcomingExpenseDetailsModel;
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

public class AddUpcomingExpenseActivity  extends BaseActivity implements View.OnClickListener {

    private ActivityAddUpcomingExpenseBinding binding;
    private boolean update;
    private UpcomingExpenseDetailsModel upcomingExpenseDetailsModel;
    private final List<ClubBankLists> clubBankLists = new ArrayList<>();
    private final List<ExpenseList> expenseLists = new ArrayList<>();
    private final List<ExpenseTypeModel> itemList = new ArrayList<>();
    private String recordId = "", clubId = "", expenseListItemId = "", clubBankListItemId = "", upComingPaymentType = "", expenseId = "", photoFilePath = "";
    private EasyImage easyImage;
    private File file = new File("");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpcomingExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            upcomingExpenseDetailsModel = new Gson().fromJson(bundle.getString("result", ""), UpcomingExpenseDetailsModel.class);
            update = bundle.getBoolean("is_update");
            recordId = bundle.getString("record_id", "");
            expenseId = bundle.getString("expense_id", "");
            clubId = bundle.getString("club_id", "");
        }


        getExpenseList();
        getClubBanksList(clubId);
        populateData(update);


        binding.btnSubmit.setOnClickListener(this);
        binding.etExpenseSource.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.etExpenseType.setOnClickListener(this);
        binding.etBankName.setOnClickListener(this);


        itemList.add(new ExpenseTypeModel(0, "One Time"));
        itemList.add(new ExpenseTypeModel(1, "Every Week"));
        itemList.add(new ExpenseTypeModel(2, "Every Month"));
        itemList.add(new ExpenseTypeModel(3, "Every Quarter"));
        itemList.add(new ExpenseTypeModel(4, "Every Year"));


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit) {
            if (binding.etExpenseSource.getText().toString().isEmpty()){
                Functions.showToast(getContext(), getString(R.string.expense_for), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
             if (binding.etAmount.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.amount_cannot_be_empty), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
             if (binding.etDate.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_date), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
             if (binding.etExpenseType.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_pay_type), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            if (expenseListItemId.isEmpty()) {
                expenseListItemId = expenseId;
            }

            if (update) {
                updateUpcomingExpense();
            } else {
                addUpcomingExpense();
            }

        }
        else if (v == binding.etExpenseSource) {
            ExpenseSelectionListDialog dialog = new ExpenseSelectionListDialog(getContext(), getString(R.string.select_upcoming_expense));
            dialog.setLists(expenseLists);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new ExpenseSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<ExpenseList> expenseLists) {
                    ExpenseList item = expenseLists.get(0);
                    binding.etExpenseSource.setText(item.getName());
                    expenseListItemId = item.getId();
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
        else if (v == binding.etDate) {
            dateClicked();
        }
        else if (v == binding.etExpenseType) {
            ExpenseTypeDialog dialog = new ExpenseTypeDialog(getContext(), getString(R.string.select_payment_type));
            dialog.setLists(itemList);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new ExpenseTypeDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<ExpenseTypeModel> expenseTypeModels) {
                    ExpenseTypeModel item = expenseTypeModels.get(0);
                    binding.etExpenseType.setText(item.getValue());
                    upComingPaymentType = item.getValue();

                        if (item.getValue().equalsIgnoreCase("One Time")){
                            upComingPaymentType = "one_time";
                        }
                         else if (item.getValue().equalsIgnoreCase("Every Week")){
                            upComingPaymentType = "weekly";
                         }
                        else if (item.getValue().equalsIgnoreCase("Every Month")){
                            upComingPaymentType = "monthly";
                         }
                        else if (item.getValue().equalsIgnoreCase("Every Quarter")){
                            upComingPaymentType = "quarterly";
                        }
                        else if (item.getValue().equalsIgnoreCase("Every Year")){
                            upComingPaymentType = "yearly";
                        }
                }
            });
            dialog.show();

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


    private void updateUpcomingExpense() {

        if (clubBankListItemId.isEmpty()){
            clubBankListItemId = upcomingExpenseDetailsModel.getBankId();
        }
        updateUpcomingExpenseDetails(true, binding.etDate.getText().toString(),
                clubBankListItemId, expenseListItemId,
                binding.etAmount.getText().toString(),
                binding.etNote.getText().toString(), file);

    }

    private void addUpcomingExpense() {
        addUpcomingExpenseDetails(true, binding.etDate.getText().toString(),
                clubBankListItemId, expenseListItemId,
                binding.etAmount.getText().toString(),
                binding.etNote.getText().toString(), file);
    }

    private void dateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        pickerDialog.show();
    }

    private void updateInvoice() {
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

    private void updateUpcomingExpenseDetails(boolean isLoader, String recurringDate, String bankid, String expenseId, String amount, String note, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateUpcomingExpense(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), recordId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), expenseId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankid),
                    RequestBody.create(MediaType.parse("multipart/form-data"), amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"), note),
                    RequestBody.create(MediaType.parse("multipart/form-data"), upComingPaymentType),
                    RequestBody.create(MediaType.parse("multipart/form-data"), recurringDate));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.upcoming_expense_updated), FancyToast.SUCCESS);
                                finish();
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
                    Functions.hideLoader(hud);
                    if (t instanceof UnknownHostException) {
                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                    } else {
                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
            });
        }
    }
    private void addUpcomingExpenseDetails(boolean isLoader, String recurringDate, String bankid, String expenseId, String amount, String note, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId != null) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addUpcomingExpense(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), expenseId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankid),
                    RequestBody.create(MediaType.parse("multipart/form-data"), amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"), note),
                    RequestBody.create(MediaType.parse("multipart/form-data"), upComingPaymentType),
                    RequestBody.create(MediaType.parse("multipart/form-data"), recurringDate));
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
                    Functions.hideLoader(hud);
                    if (t instanceof UnknownHostException) {
                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                    } else {
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
        } else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1, 1)
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

    private void populateData(Boolean isUpdate) {

        if (isUpdate) {
            binding.btnTvAddUpdate.setText(R.string.update);
            binding.title.setText(R.string.update);
            binding.etExpenseSource.setText(upcomingExpenseDetailsModel.getTitle());
            binding.etAmount.setText(upcomingExpenseDetailsModel.getAmount());
            binding.etBankName.setText(upcomingExpenseDetailsModel.getBankName());
            binding.etNote.setText(upcomingExpenseDetailsModel.getNotes());
            binding.etDate.setText(upcomingExpenseDetailsModel.getRecurringDate());
            if (!upcomingExpenseDetailsModel.getReceipt().isEmpty()){
                Glide.with(getContext()).load(upcomingExpenseDetailsModel.getReceipt()).into(binding.invoiceImgVu);
            }else{
                Glide.with(getContext()).load(R.drawable.attachment_img).into(binding.invoiceImgVu);
            }

            if (upcomingExpenseDetailsModel.getRecurringType().equalsIgnoreCase("one_time")){
                upComingPaymentType = "one_time";
                binding.etExpenseType.setText("One Time");
            }
            else if (upcomingExpenseDetailsModel.getRecurringType().equalsIgnoreCase("weekly")){
                upComingPaymentType = "weekly";
                binding.etExpenseType.setText("Every Week");

            }
            else if (upcomingExpenseDetailsModel.getRecurringType().equalsIgnoreCase("monthly")){
                upComingPaymentType = "monthly";
                binding.etExpenseType.setText("Every Month");
            }
            else if (upcomingExpenseDetailsModel.getRecurringType().equalsIgnoreCase("quarterly")){
                upComingPaymentType = "quarterly";
                binding.etExpenseType.setText("Every Quarter");

            }
            else if (upcomingExpenseDetailsModel.getRecurringType().equalsIgnoreCase("yearly")){
                upComingPaymentType = "yearly";
                binding.etExpenseType.setText("Every Year");
            }


        } else {
            binding.title.setText(getString(R.string.add_upcoming_expense));
            binding.updateTv.setText(getString(R.string.add_upcoming_expense));
            binding.btnTvAddUpdate.setText(R.string.add);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}