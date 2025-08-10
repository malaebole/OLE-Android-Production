package ae.oleapp.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baoyz.actionsheet.ActionSheet;
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
import ae.oleapp.adapters.ClubsBanksAdapter;
import ae.oleapp.adapters.CustomItemCashCardAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddIncomeBinding;
import ae.oleapp.dialogs.ClubBankSelectionListDialog;
import ae.oleapp.dialogs.ExpenseSelectionListDialog;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.CustomItem;
import ae.oleapp.models.ExpenseList;
import ae.oleapp.models.GameHistory;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.models.IncomeHistory;
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

public class AddIncomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddIncomeBinding binding;
    private boolean update = false;
    private IncomeDetailsModel incomeDetailsModel;
    private final List<ClubBankLists> clubBankLists = new ArrayList<>();
    private final List<CustomItem> customItems = new ArrayList<>();
    private EasyImage easyImage;
    private File file = new File("");
    private String incomeId = "";
    private String clubId="";
    private final String clubBankListItemId = "";
    private String photoFilePath = "";
    private String incomeType="";
    private String bankId="";
    private ClubsBanksAdapter clubsBanksAdapter;
    private CustomItemCashCardAdapter customItemCashCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddIncomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //incomeDetailsModel = new Gson().fromJson(bundle.getString("result", ""), IncomeDetailsModel.class);
            update =  bundle.getBoolean("is_update");
            incomeId = bundle.getString("income_id","");
            clubId = bundle.getString("club_id","");
        }

        customItems.add(new CustomItem("0", "Cash"));
        customItems.add(new CustomItem("1", "Card"));
        customItems.add(new CustomItem("2", "Both"));


        if (update){
            getincomeDetails(false, incomeId);
        }else{
            binding.title.setText(getString(R.string.add_income));
            binding.updateTv.setText(getString(R.string.add_income));
            binding.btnTvAddUpdate.setText(R.string.add);
        }
        getClubBanksList(clubId);
        binding.loader.setVisibility(View.VISIBLE);

        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.banksRecyclerVu.setLayoutManager(teamLayoutManager);
        clubsBanksAdapter = new ClubsBanksAdapter(getContext(), clubBankLists);
        clubsBanksAdapter.setItemClickListener(itemClickListener);
        binding.banksRecyclerVu.setAdapter(clubsBanksAdapter);

        LinearLayoutManager cashType = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.depositTypeRecyclerVu.setLayoutManager(cashType);
        customItemCashCardAdapter = new CustomItemCashCardAdapter(getContext(), customItems);
        customItemCashCardAdapter.setItemClickListener(itemClickListenerr);
        binding.depositTypeRecyclerVu.setAdapter(customItemCashCardAdapter);


        binding.btnSubmit.setOnClickListener(this);
        binding.invoiceImgVu.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
//        binding.etPaymentMethod.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit){
            if (binding.etIncomeSource.getText().toString().isEmpty()){
                Functions.showToast(getContext(),getString(R.string.source_cannot_be_empty), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            if (binding.etAmount.getText().toString().isEmpty()){
                Functions.showToast(getContext(),getString(R.string.amount_cannot_be_empty), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            if (binding.etDate.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Date is required!", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }

            if (update){
                updateRecord();
            }else{
                addRecord();
            }
        }  else if (v == binding.invoiceImgVu) {
            updateInvoice();
        }
        else if (v == binding.btnClose) {
            finish();
        }
//        else if (v == binding.etPaymentMethod) {
//            ClubBankSelectionListDialog dialog = new ClubBankSelectionListDialog(getContext(), getString(R.string.select_bank));
//            dialog.setLists(clubBankLists);
//            dialog.setShowSearch(true);
//            dialog.setOnItemSelected(new ClubBankSelectionListDialog.OnItemSelected() {
//                @Override
//                public void selectedItem(List<ClubBankLists> ClubBankLists, int pos) {
//                    ClubBankLists item = clubBankLists.get(pos);
//                    binding.etPaymentMethod.setText(item.getName());
//                    clubBankListItemId = item.getId();
//                }
//            });
//            dialog.show();
//        }
        else if (v == binding.etDate) {
            fromDateClicked();
        }

    }

    ClubsBanksAdapter.ItemClickListener itemClickListener = new ClubsBanksAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            clubsBanksAdapter.setSelectedId(clubBankLists.get(pos).getId());
            bankId = clubBankLists.get(pos).getId();

        }
    };
    CustomItemCashCardAdapter.ItemClickListener itemClickListenerr = new CustomItemCashCardAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            customItemCashCardAdapter.setSelectedId(customItems.get(pos).getId());
            incomeType = customItems.get(pos).getValue();

        }
    };

    private void updateRecord(){
        updateIncomeDetails(true, bankId, binding.etIncomeSource.getText().toString(),binding.etAmount.getText().toString(),binding.etNote.getText().toString(),binding.etDate.getText().toString(),incomeType,file);
    }
    private void addRecord(){
        addIncome(true, bankId, binding.etIncomeSource.getText().toString(), binding.etAmount.getText().toString(), binding.etNote.getText().toString(),file, binding.etDate.getText().toString(),incomeType);
    }

    private void fromDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
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
        pickerDialog.show();
    }

    private void getClubBanksList(String club_id) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubBanksList(Functions.getAppLang(getContext()), club_id,"");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    binding.loader.setVisibility(View.GONE);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                for (int i = 0; i < data.length(); i++) {
                                    clubBankLists.add(gson.fromJson(data.get(i).toString(), ClubBankLists.class));
                                }
                                clubsBanksAdapter.notifyDataSetChanged();
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
                    binding.loader.setVisibility(View.GONE);
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

    private void updateIncomeDetails(boolean isLoader, String bankid, String incomesource, String amount, String note,String date,String IncomeType, File file) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }

            Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateIncome(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),incomeId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),bankid),
                    RequestBody.create(MediaType.parse("multipart/form-data"),incomesource),
                    RequestBody.create(MediaType.parse("multipart/form-data"),amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"),date),
                    RequestBody.create(MediaType.parse("multipart/form-data"),IncomeType),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.income_updated), FancyToast.SUCCESS);
//                                Intent intent = new Intent(getContext(), IncomeHistoryActivity.class);
//                                intent.putExtra("club_id",clubId);
//                                startActivity(intent);
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
    private void addIncome(boolean isLoader, String bankid, String incomesource, String amount, String note, File file, String date, String IncomeType) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;

            MultipartBody.Part part = null;
            if (!photoFilePath.isEmpty()) {
                file = new File(photoFilePath);
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
            }

            Call<ResponseBody> call = AppManager.getInstance().apiInterface.addIncome(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                    RequestBody.create(MediaType.parse("multipart/form-data"),clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"),bankid),
                    RequestBody.create(MediaType.parse("multipart/form-data"),incomesource),
                    RequestBody.create(MediaType.parse("multipart/form-data"),amount),
                    RequestBody.create(MediaType.parse("multipart/form-data"),date),
                    RequestBody.create(MediaType.parse("multipart/form-data"),IncomeType),
                    RequestBody.create(MediaType.parse("multipart/form-data"),note));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.income_added), FancyToast.SUCCESS);
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

    private void getincomeDetails(Boolean isLoader, String income_id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.incomeDetails(Functions.getAppLang(getContext()), income_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONObject obj = object.getJSONObject(Constants.kData);
                                Gson gson = new Gson();
                                incomeDetailsModel = gson.fromJson(obj.toString(), IncomeDetailsModel.class);
                                populateData(update);
                            }
                        }  catch (Exception e) {
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

    private void populateData(Boolean isUpdate){

        if (isUpdate){
            binding.title.setText(R.string.update);
            binding.btnTvAddUpdate.setText(R.string.update);
            binding.etIncomeSource.setText(incomeDetailsModel.getSource());
            binding.etAmount.setText(incomeDetailsModel.getAmount());
            //binding.etPaymentMethod.setText(incomeDetailsModel.getBankName());
            binding.etNote.setText(incomeDetailsModel.getNotes());
            binding.etDate.setText(incomeDetailsModel.getAddedDate());
            if (incomeDetailsModel.getPaymentMethod().equalsIgnoreCase("cash")){
                customItemCashCardAdapter.setSelectedId("0");
                incomeType = "cash";
                customItemCashCardAdapter.notifyDataSetChanged();
            }
            if (incomeDetailsModel.getPaymentMethod().equalsIgnoreCase("card")){
                customItemCashCardAdapter.setSelectedId("1");
                incomeType = "card";
                customItemCashCardAdapter.notifyDataSetChanged();

            }
            if (incomeDetailsModel.getPaymentMethod().equalsIgnoreCase("both")){
                customItemCashCardAdapter.setSelectedId("2");
                incomeType = "both";
                customItemCashCardAdapter.notifyDataSetChanged();

            }

            clubsBanksAdapter.setSelectedId(incomeDetailsModel.getBankId());

            //binding.dateLayout.setVisibility(View.GONE);
            if (!incomeDetailsModel.getReceipt().isEmpty()){
                Glide.with(getContext()).load(incomeDetailsModel.getReceipt()).into(binding.invoiceImgVu);
            }else{
                Glide.with(getContext()).load(R.drawable.attachment_img).into(binding.invoiceImgVu);
            }

        }
        else{
            binding.title.setText(getString(R.string.add_income));
            binding.updateTv.setText(getString(R.string.add_income));
            binding.btnTvAddUpdate.setText(R.string.add);
//            binding.dateLayout.setVisibility(View.VISIBLE);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}