package ae.oleapp.owner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.baoyz.actionsheet.ActionSheet;
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
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OlePromoPlayerListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCreatePromoCodeBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePromoCode;
import ae.oleapp.player.OlePlayerListActivity;
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

public class OleCreatePromoCodeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreatePromoCodeBinding binding;
    private OleBookingFieldAdapter fieldAdapter;
    private final List<Field> fieldList = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private String clubId = "";
    private boolean isPerc = false;
    private boolean isUnlimited = false;
    private OlePromoCode olePromoCode;
    private OlePromoPlayerListAdapter adapter;
    private EasyImage easyImage;
    private File coverImage;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreatePromoCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.promo_code);

        amountClicked();
        unlimitClicked();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            clubId = bundle.getString("club_id", "");
            olePromoCode = gson.fromJson(bundle.getString("promo", ""), OlePromoCode.class);
        }

        binding.tvCurrency1.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));
        binding.tvCurrency2.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));
        binding.tvCurrency3.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        binding.fieldRecyclerVu.setVisibility(View.GONE);
        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleBookingFieldAdapter(getContext(), fieldList, true);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        LinearLayoutManager playersLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.playersRecyclerVu.setLayoutManager(playersLayoutManager);
        adapter = new OlePromoPlayerListAdapter(getContext(), playerList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.playersRecyclerVu.setAdapter(adapter);

        if (olePromoCode == null) {
            // new
            binding.btnDelete.setVisibility(View.GONE);
            binding.tvBtntitle.setText(R.string.add_now);
            if (clubId.isEmpty() && AppManager.getInstance().clubs.size() > 0) {
                Club club = AppManager.getInstance().clubs.get(0);
                clubId = club.getId();
            }
            else {
                for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
                    if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                        oleRankClubAdapter.setSelectedIndex(i);
                        break;
                    }
                }
            }
            getAllFieldsAPI(true, clubId);
        }
        else {
            // update
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.tvBtntitle.setText(R.string.update);
            populateData();
            getAllFieldsAPI(true, olePromoCode.getClubId());
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.percVu.setOnClickListener(this);
        binding.amountVu.setOnClickListener(this);
        binding.limitedVu.setOnClickListener(this);
        binding.unlimitedVu.setOnClickListener(this);
        binding.btnGenerate.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.relAddPlayer.setOnClickListener(this);
        binding.imgVuBanner.setOnClickListener(this);
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getAllFieldsAPI(true, clubId);
        }
    };

    OleBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            fieldAdapter.setSelectedPosition(pos);
        }
    };

    OlePromoPlayerListAdapter.OnItemClickListener itemClickListener = new OlePromoPlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {

        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            playerList.remove(pos);
            adapter.notifyDataSetChanged();
        }
    };

    private void populateData() {
        clubId = olePromoCode.getClubId();
        for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
            if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                oleRankClubAdapter.setSelectedIndex(i);
                break;
            }
        }
        binding.etTitle.setText(olePromoCode.getCouponTitle());
        binding.etCode.setText(olePromoCode.getCouponCode());
        binding.etFromDate.setText(olePromoCode.getStartFrom());
        binding.etToDate.setText(olePromoCode.getExpiry());
        binding.etPlayerLimit.setText(olePromoCode.getPlayerUsageLimit());
        if (olePromoCode.getUsageLimit().isEmpty()) {
            unlimitClicked();
        }
        else {
            limitClicked();
            binding.etLimit.setText(olePromoCode.getUsageLimit());
        }
        if (olePromoCode.getDiscountType().equalsIgnoreCase("amount")) {
            amountClicked();
            binding.etOneHour.setText(olePromoCode.getOneHourDiscount());
            binding.etOneHalfHour.setText(olePromoCode.getOneHalfHourDiscount());
            binding.etTwoHour.setText(olePromoCode.getTwoHourDiscount());
        }
        else {
            percClicked();
            binding.etPerc.setText(olePromoCode.getDiscount());
        }

        Glide.with(getApplicationContext()).load(olePromoCode.getPromoImage()).into(binding.imgVuBanner);

        playerList.clear();
        if (olePromoCode.getPlayerList() != null) {
            playerList.addAll(olePromoCode.getPlayerList());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDelete) {
            deleteClicked();
        }
        else if (v == binding.etFromDate) {
            fromDateClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
        else if (v == binding.percVu) {
            percClicked();
        }
        else if (v == binding.amountVu) {
            amountClicked();
        }
        else if (v == binding.unlimitedVu) {
            unlimitClicked();
        }
        else if (v == binding.limitedVu) {
            limitClicked();
        }
        else if (v == binding.btnGenerate) {
            generateClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
        else if (v == binding.relAddPlayer) {
            addPlayerClicked();
        }
        else if (v == binding.imgVuBanner) {
            bannerClicked();
        }
    }

    private void bannerClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                            Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                                @Override
                                public void onGranted() {
                                    // do your task.
                                    easyImage = new EasyImage.Builder(getContext())
                                            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                                            .setCopyImagesToPublicGalleryFolder(false)
                                            .allowMultiple(false).build();
                                    easyImage.openChooser(OleCreatePromoCodeActivity.this);
                                }
                            });
                        }
                        else if (index == 1) {
                            coverImage = null;
                            binding.imgVuBanner.setImageDrawable(null);
                        }
                    }
                }).show();
    }

    private void deleteClicked() {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteCouponAPI();
                        }
                    }
                }).show();
    }

    private void addPlayerClicked() {
        Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
        intent.putExtra("is_selection", true);
        this.startActivityForResult(intent, 106);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(resultUri.getPath());
                coverImage = file;
                Glide.with(getApplicationContext()).load(file).into(binding.imgVuBanner);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == 106 && resultCode == Activity.RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            for (OlePlayerInfo info: list) {
                if (!isExist(info.getId())) {
                    playerList.add(info);
                }
            }
            adapter.notifyDataSetChanged();
        }
        else {
            if (easyImage == null) {
                return;
            }
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.ActivityBuilder builder = CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
                        builder.setAspectRatio(4,2);
                        builder.start(OleCreatePromoCodeActivity.this);
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

    private boolean isExist(String pId) {
        boolean result = false;
        for (OlePlayerInfo info : playerList) {
            if (info.getId().equalsIgnoreCase(pId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void fromDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etFromDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void toDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etToDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void percClicked() {
        isPerc = true;
        binding.imgPerc.setImageResource(R.drawable.check);
        binding.imgAmount.setImageResource(R.drawable.uncheck);
        binding.tvPerc.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.tvAmount.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.percVu.setStrokeColor(getResources().getColor(R.color.blueColorNew));
        binding.amountVu.setStrokeColor(getResources().getColor(R.color.separatorColor));
        binding.discPercVu.setVisibility(View.VISIBLE);
        binding.oneHourVu.setVisibility(View.GONE);
        binding.oneHalfHourVu.setVisibility(View.GONE);
        binding.twoHourVu.setVisibility(View.GONE);
        binding.percVu.invalidate();
        binding.amountVu.invalidate();
    }

    private void amountClicked() {
        isPerc = false;
        binding.imgPerc.setImageResource(R.drawable.uncheck);
        binding.imgAmount.setImageResource(R.drawable.check);
        binding.tvAmount.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.tvPerc.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.amountVu.setStrokeColor(getResources().getColor(R.color.blueColorNew));
        binding.percVu.setStrokeColor(getResources().getColor(R.color.separatorColor));
        binding.discPercVu.setVisibility(View.GONE);
        binding.oneHourVu.setVisibility(View.VISIBLE);
        binding.oneHalfHourVu.setVisibility(View.VISIBLE);
        binding.twoHourVu.setVisibility(View.VISIBLE);
        binding.percVu.invalidate();
        binding.amountVu.invalidate();
    }

    private void unlimitClicked() {
        isUnlimited = true;
        binding.limitVu.setVisibility(View.GONE);
        binding.imgUnlimit.setImageResource(R.drawable.check);
        binding.unlimitedVu.setStrokeColor(getResources().getColor(R.color.blueColorNew));
        binding.tvUnlimit.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.imgLimit.setImageResource(R.drawable.uncheck);
        binding.limitedVu.setStrokeColor(getResources().getColor(R.color.separatorColor));
        binding.tvLimit.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.unlimitedVu.invalidate();
        binding.limitedVu.invalidate();
    }

    private void limitClicked() {
        isUnlimited = false;
        binding.limitVu.setVisibility(View.VISIBLE);
        binding.imgUnlimit.setImageResource(R.drawable.uncheck);
        binding.unlimitedVu.setStrokeColor(getResources().getColor(R.color.separatorColor));
        binding.tvUnlimit.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.imgLimit.setImageResource(R.drawable.check);
        binding.limitedVu.setStrokeColor(getResources().getColor(R.color.blueColorNew));
        binding.tvLimit.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.unlimitedVu.invalidate();
        binding.limitedVu.invalidate();
    }

    private void generateClicked() {
        String DATA = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }
        binding.etCode.setText(sb.toString());
    }

    private void addClicked() {
        if (clubId.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
            return;
        }
        if (fieldAdapter.selectedFields.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
            return;
        }
        if (binding.etTitle.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_title), FancyToast.ERROR);
            return;
        }
        if (binding.etCode.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_promo_code), FancyToast.ERROR);
            return;
        }
        if (binding.etFromDate.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_from_date), FancyToast.ERROR);
            return;
        }
        if (binding.etToDate.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_to_date), FancyToast.ERROR);
            return;
        }
        if (isPerc && binding.etPerc.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_discount_value), FancyToast.ERROR);
            return;
        }
        if (!isPerc && (binding.etOneHour.getText().toString().isEmpty() || binding.etOneHalfHour.getText().toString().isEmpty() || binding.etTwoHour.getText().toString().isEmpty())) {
            Functions.showToast(getContext(), getString(R.string.enter_discount_value), FancyToast.ERROR);
            return;
        }
        if (!isUnlimited && binding.etLimit.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_usage_limit), FancyToast.ERROR);
            return;
        }

        String fieldsId = "";
        for (Field field: fieldAdapter.selectedFields) {
            if (fieldsId.isEmpty()) {
                fieldsId = field.getId();
            }
            else {
                fieldsId = String.format("%s,%s", fieldsId, field.getId());
            }
        }

        String pIds = "";
        for (OlePlayerInfo info: playerList) {
            if (pIds.isEmpty()) {
                pIds = info.getId();
            }
            else {
                pIds = String.format("%s,%s", pIds, info.getId());
            }
        }

        String limit = "";
        if (!pIds.isEmpty() || isUnlimited) {
            limit = "";
        }
        else {
            limit = binding.etLimit.getText().toString();
        }

        if (olePromoCode == null) {
            // new
            if (isPerc) {
                addPromoCodeAPI(binding.etTitle.getText().toString(), binding.etCode.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), limit, binding.etPerc.getText().toString(), "", "", "", "percent", fieldsId, binding.etPlayerLimit.getText().toString(), pIds);
            }
            else {
                addPromoCodeAPI(binding.etTitle.getText().toString(), binding.etCode.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), limit, binding.etOneHour.getText().toString(), binding.etOneHour.getText().toString(), binding.etOneHalfHour.getText().toString(), binding.etTwoHour.getText().toString(), "amount", fieldsId, binding.etPlayerLimit.getText().toString(), pIds);
            }
        }
        else {
            // update
            if (isPerc) {
                updatePromoCodeAPI(binding.etTitle.getText().toString(), binding.etCode.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), limit, binding.etPerc.getText().toString(), "", "", "", "percent", fieldsId, binding.etPlayerLimit.getText().toString(), pIds);
            }
            else {
                updatePromoCodeAPI(binding.etTitle.getText().toString(), binding.etCode.getText().toString(), binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), limit, binding.etOneHour.getText().toString(), binding.etOneHour.getText().toString(), binding.etOneHalfHour.getText().toString(), binding.etTwoHour.getText().toString(), "amount", fieldsId, binding.etPlayerLimit.getText().toString(), pIds);
            }
        }
    }

    private void getAllFieldsAPI(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldList.clear();
                            Gson gson = new Gson();
                            fieldAdapter.selectedFields.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                                if (olePromoCode != null) {
                                    String[] arrIds = olePromoCode.getFields().split(",");
                                    for (String str : arrIds) {
                                        if (field.getId().equalsIgnoreCase(str)) {
                                            fieldAdapter.selectedFields.add(field);
                                            break;
                                        }
                                    }
                                }
                            }
                            binding.fieldRecyclerVu.setVisibility(View.VISIBLE);
                            fieldAdapter.notifyDataSetChanged();
                        }
                        else {
                            fieldList.clear();
                            fieldAdapter.selectedFields.clear();
                            fieldAdapter.notifyDataSetChanged();
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

    private void addPromoCodeAPI(String title, String code, String from, String to, String limit, String value, String oneHour, String oneHalfHour, String twoHour, String type, String fieldIds, String playerLimit, String pIds) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part coverPart = null;
        if (coverImage != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
            coverPart = MultipartBody.Part.createFormData("promo_image", coverImage.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addCoupon(
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), title),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), fieldIds),
                RequestBody.create(MediaType.parse("text/plain"), code),
                RequestBody.create(MediaType.parse("text/plain"), limit),
                RequestBody.create(MediaType.parse("text/plain"), type),
                RequestBody.create(MediaType.parse("text/plain"), value),
                RequestBody.create(MediaType.parse("text/plain"), oneHour),
                RequestBody.create(MediaType.parse("text/plain"), oneHalfHour),
                RequestBody.create(MediaType.parse("text/plain"), twoHour),
                RequestBody.create(MediaType.parse("text/plain"), from),
                RequestBody.create(MediaType.parse("text/plain"), to),
                RequestBody.create(MediaType.parse("text/plain"), playerLimit),
                RequestBody.create(MediaType.parse("text/plain"), pIds),
                coverPart);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void updatePromoCodeAPI(String title, String code, String from, String to, String limit, String value, String oneHour, String oneHalfHour, String twoHour, String type, String fieldIds, String playerLimit, String pIds) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part coverPart = null;
        if (coverImage != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
            coverPart = MultipartBody.Part.createFormData("promo_image", coverImage.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateCoupon(
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), title),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), fieldIds),
                RequestBody.create(MediaType.parse("text/plain"), code),
                RequestBody.create(MediaType.parse("text/plain"), limit),
                RequestBody.create(MediaType.parse("text/plain"), type),
                RequestBody.create(MediaType.parse("text/plain"), value),
                RequestBody.create(MediaType.parse("text/plain"), oneHour),
                RequestBody.create(MediaType.parse("text/plain"), oneHalfHour),
                RequestBody.create(MediaType.parse("text/plain"), twoHour),
                RequestBody.create(MediaType.parse("text/plain"), from),
                RequestBody.create(MediaType.parse("text/plain"), to),
                RequestBody.create(MediaType.parse("text/plain"), olePromoCode.getId()),
                RequestBody.create(MediaType.parse("text/plain"), playerLimit),
                RequestBody.create(MediaType.parse("text/plain"), pIds),
                coverPart);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void deleteCouponAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteCoupon(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), "single", olePromoCode.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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
}