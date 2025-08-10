package ae.oleapp.owner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
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
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleClubDayAdapter;
import ae.oleapp.adapters.OleFieldColorListAdapter;
import ae.oleapp.adapters.OleFieldSizeAdapter;
import ae.oleapp.adapters.OleFieldTypeAdapter;
import ae.oleapp.adapters.OleGrassTypeAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAddFieldBinding;
import ae.oleapp.dialogs.OleColorPreviewDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleColorModel;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleFieldData;
import ae.oleapp.models.OleFieldDataChild;
import ae.oleapp.models.OleFieldPrice;
import ae.oleapp.models.OleKeyValuePair;
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

public class OleAddFieldActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAddFieldBinding binding;
    private Club club;
    private String sizeId = "";
    private String fieldTypeId = "";
    private String grassTypeId = "";
    private String color = "";
    private String currentDayId = "";
    private String isMerge = "0";
    private OleClubDayAdapter dayAdapter;
    private List<OleKeyValuePair> daysList = new ArrayList<>();
    private final List<OleFieldPrice> priceList = new ArrayList<>();
    private final List<Field> fieldList = new ArrayList<>();
    private OleBookingFieldAdapter fieldAdapter;
    private String fieldId = "";
    private String clubId = "";
    private boolean isFootballUpdate = false;
    private boolean isPadelUpdate = false;
    private Field field;
    private File coverImage;
    private EasyImage easyImage;
    private boolean isPadel = false;
    private OleFieldSizeAdapter oleFieldSizeAdapter;
    private OleFieldTypeAdapter oleFieldTypeAdapter;
    private OleGrassTypeAdapter oleGrassTypeAdapter;
    private OleFieldColorListAdapter colorListAdapter;
    private List<OleColorModel> colorList;
    private int stepCount = 1;
    private OleRankClubAdapter oleRankClubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddFieldBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fieldId = bundle.getString("field_id", "");
            clubId = bundle.getString("club_id", "");
            isFootballUpdate = bundle.getBoolean("is_football_update", false);
            isPadelUpdate = bundle.getBoolean("is_padel_update", false);
        }

        if (AppManager.getInstance().oleFieldData == null) {
            getFieldDataAPI(new BaseActivity.FieldDataCallback() {
                @Override
                public void getFieldData(OleFieldData oleFieldData) {
                    AppManager.getInstance().oleFieldData = oleFieldData;
                    setFieldData();
                }
            });
        }
        else {
            setFieldData();
        }

        colorList = Arrays.asList(
                new OleColorModel(getString(R.string.black), "#000000"),
                new OleColorModel(getString(R.string.gray), "#C4C4C4"),
                new OleColorModel(getString(R.string.blue), "#1E75C9"),
                new OleColorModel(getString(R.string.yellow), "#FFBA00"),
                new OleColorModel(getString(R.string.pink), "#FD6C9E"),
                new OleColorModel(getString(R.string.red), "#FE2717"),
                new OleColorModel(getString(R.string.purple), "#800080"),
                new OleColorModel(getString(R.string.aqua), "#0CFFEC"));


        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        if (isFootballUpdate) {
            footballClicked();
            binding.bar.toolbarTitle.setText(R.string.field);
            binding.btnTitle.setText(R.string.update);
            getOneFieldAPI(true);
            binding.clubVu.setVisibility(View.GONE);
            binding.btnAddClub.setVisibility(View.GONE);
            binding.relMerge.setVisibility(View.GONE);
            binding.mergeSwitch.setChecked(false);
        }
        else if (isPadelUpdate) {
            padelClicked();
            binding.bar.toolbarTitle.setText(R.string.field);
            binding.btnTitle.setText(R.string.update);
            getOneFieldAPI(true);
            binding.clubVu.setVisibility(View.GONE);
            binding.btnAddClub.setVisibility(View.GONE);
        }
        else {
            binding.clubVu.setVisibility(View.VISIBLE);
            binding.btnAddClub.setVisibility(View.VISIBLE);
            binding.btnTitle.setText(R.string.add_now);
            binding.bar.toolbarTitle.setText(R.string.add_field);
            populateClub();
            footballClicked();
        }

        moveToStep1();

        binding.tvCurrency1.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));
        binding.tvCurrency2.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));
        binding.tvCurrency3.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        daysList = Functions.getDays(getContext());

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(facLayoutManager);
        dayAdapter = new OleClubDayAdapter(getContext(), daysList);
        dayAdapter.setOnItemClickListener(dayClickListener);
        binding.daysRecyclerVu.setAdapter(dayAdapter);

        LinearLayoutManager colorLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.colorRecyclerVu.setLayoutManager(colorLayoutManager);
        colorListAdapter = new OleFieldColorListAdapter(getContext(), colorList);
        colorListAdapter.setOnItemClickListener(colorClickListener);
        binding.colorRecyclerVu.setAdapter(colorListAdapter);

        binding.fieldRecyclerVu.setVisibility(View.GONE);
        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleBookingFieldAdapter(getContext(), fieldList, true);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        binding.etOneHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oneHourTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etOneHalfHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oneHalfHourTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etTwoHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                twoHourTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.imgVuBanner.setOnClickListener(this);
        binding.tvPreview.setOnClickListener(this);
        binding.btnAddClub.setOnClickListener(this);
        binding.mergeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mergeSwitchChanged(binding.mergeSwitch);
            }
        });
        binding.priceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.daysRecyclerVu.setVisibility(View.GONE);
                }
                else {
                    binding.daysRecyclerVu.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPadelUpdate && !isFootballUpdate) {
            getClubList(true);
        }
    }

    private void populateClub() {
        if (AppManager.getInstance().clubs.size() > 0) {
            Club club = AppManager.getInstance().clubs.get(0);
            this.club = club;
        }
        if (!clubId.isEmpty()) {
            for (int i = 0; i < AppManager.getInstance().clubs.size(); i++) {
                if (AppManager.getInstance().clubs.get(i).getId().equalsIgnoreCase(clubId)) {
                    this.club = AppManager.getInstance().clubs.get(i);
                    oleRankClubAdapter.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (club != null) {
            binding.clubVu.setVisibility(View.VISIBLE);
            if (club.getClubType() != null && club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                padelClicked();
            }
            else {
                footballClicked();
            }
        }
        else {
            binding.clubVu.setVisibility(View.GONE);
        }
    }

    private void setFieldData() {
        LinearLayoutManager sizeLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.sizeRecyclerVu.setLayoutManager(sizeLayoutManager);
        oleFieldSizeAdapter = new OleFieldSizeAdapter(getContext(), AppManager.getInstance().oleFieldData.getFieldSizes(), -1);
        oleFieldSizeAdapter.setOnItemClickListener(sizeClickListener);
        binding.sizeRecyclerVu.setAdapter(oleFieldSizeAdapter);

        LinearLayoutManager typeLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.typeRecyclerVu.setLayoutManager(typeLayoutManager);
        oleFieldTypeAdapter = new OleFieldTypeAdapter(getContext(), AppManager.getInstance().oleFieldData.getFiledTypes(), -1);
        oleFieldTypeAdapter.setOnItemClickListener(typeClickListener);
        binding.typeRecyclerVu.setAdapter(oleFieldTypeAdapter);

        LinearLayoutManager grasstypeLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.grassRecyclerVu.setLayoutManager(grasstypeLayoutManager);
        oleGrassTypeAdapter = new OleGrassTypeAdapter(getContext(), AppManager.getInstance().oleFieldData.getGrassType(), -1);
        oleGrassTypeAdapter.setOnItemClickListener(grasstypeClickListener);
        binding.grassRecyclerVu.setAdapter(oleGrassTypeAdapter);
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            club = AppManager.getInstance().clubs.get(pos);
            clubId = club.getId();
            if (club.getClubType() != null && club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                padelClicked();
            }
            else {
                footballClicked();
            }
            isMerge = "0";
            binding.mergeSwitch.setChecked(false);
            fieldList.clear();
            binding.fieldRecyclerVu.setVisibility(View.GONE);
        }
    };

    OleFieldSizeAdapter.OnItemClickListener sizeClickListener = new OleFieldSizeAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleFieldSizeAdapter.setSelectedIndex(pos);
            OleFieldDataChild data = AppManager.getInstance().oleFieldData.getFieldSizes().get(pos);
            sizeId = data.getId();
        }
    };

    OleFieldTypeAdapter.OnItemClickListener typeClickListener = new OleFieldTypeAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleFieldTypeAdapter.setSelectedIndex(pos);
            OleFieldDataChild data = AppManager.getInstance().oleFieldData.getFiledTypes().get(pos);
            fieldTypeId = data.getId();
        }
    };

    OleGrassTypeAdapter.OnItemClickListener grasstypeClickListener = new OleGrassTypeAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleGrassTypeAdapter.setSelectedIndex(pos);
            OleFieldDataChild data = AppManager.getInstance().oleFieldData.getGrassType().get(pos);
            grassTypeId = data.getId();
        }
    };

    OleFieldColorListAdapter.OnItemClickListener colorClickListener = new OleFieldColorListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            colorListAdapter.setSelectedIndex(pos);
            OleColorModel oleColorModel = colorList.get(pos);
            color = oleColorModel.getColor();
        }
    };

    OleBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            int index = fieldAdapter.checkFieldExist(fieldList.get(pos).getId());
            if (index == -1) {
                if (fieldAdapter.selectedFields.size() >= 3) {
                    Functions.showToast(getContext(), getString(R.string.max_three_field_merge), FancyToast.ERROR);
                }
                else {
                    fieldAdapter.selectedFields.add(fieldList.get(pos));
                    fieldAdapter.notifyDataSetChanged();
                }
            }
            else {
                fieldAdapter.selectedFields.remove(index);
                fieldAdapter.notifyDataSetChanged();
            }
        }
    };

    OleClubDayAdapter.OnItemClickListener dayClickListener = new OleClubDayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            //// check empty price if new selected day id is not 0 >> means all day
            int index = checkDayExist(currentDayId);
            if (index != -1) {
                OleFieldPrice oleFieldPrice = priceList.get(index);
                if (oleFieldPrice.getOneHour() == null || oleFieldPrice.getOneHour().isEmpty() ||
                        oleFieldPrice.getOneHalfHour() == null || oleFieldPrice.getOneHalfHour().isEmpty() ||
                        oleFieldPrice.getTwoHour() == null || oleFieldPrice.getTwoHour().isEmpty()) {
                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
                    return;
                }
            }
            //// end

            currentDayId = daysList.get(pos).getKey();
            dayAdapter.setCurrentDayId(currentDayId);
            index = checkDayExist(currentDayId);
            if (index != -1) {
                OleFieldPrice oleFieldPrice = priceList.get(index);
                binding.etOneHour.setText(oleFieldPrice.getOneHour());
                binding.etOneHalfHour.setText(oleFieldPrice.getOneHalfHour());
                binding.etTwoHour.setText(oleFieldPrice.getTwoHour());
            }
            else {
                OleFieldPrice oleFieldPrice = new OleFieldPrice();
                oleFieldPrice.setDayId(currentDayId);
                binding.etOneHour.setText("");
                binding.etOneHalfHour.setText("");
                binding.etTwoHour.setText("");
                priceList.add(oleFieldPrice);
            }

            dayAdapter.notifyDataSetChanged();
        }
    };

    private void footballClicked() {
        isPadel = false;
        binding.sizeVu.setVisibility(View.VISIBLE);
        binding.grassTypeVu.setVisibility(View.VISIBLE);
        binding.relMerge.setVisibility(View.VISIBLE);
        binding.bannerVu.setVisibility(View.GONE);
        moveToStep1();
    }

    private void padelClicked() {
        isPadel = true;
        binding.sizeVu.setVisibility(View.GONE);
        binding.grassTypeVu.setVisibility(View.GONE);
        binding.relMerge.setVisibility(View.GONE);
        binding.mergeSwitch.setChecked(false);
        binding.fieldRecyclerVu.setVisibility(View.GONE);
        binding.bannerVu.setVisibility(View.VISIBLE);
        moveToStep1();
    }

    private void oneHourTextChanged() {
        int index = checkDayExist(currentDayId);
        if (index != -1) {
            OleFieldPrice oleFieldPrice = priceList.get(index);
            if (!binding.etOneHour.getText().toString().isEmpty()) {
                oleFieldPrice.setOneHour(binding.etOneHour.getText().toString());
            }
            else {
                oleFieldPrice.setOneHour("");
            }
        }
    }

    private void oneHalfHourTextChanged() {
        int index = checkDayExist(currentDayId);
        if (index != -1) {
            OleFieldPrice oleFieldPrice = priceList.get(index);
            if (!binding.etOneHalfHour.getText().toString().isEmpty()) {
                oleFieldPrice.setOneHalfHour(binding.etOneHalfHour.getText().toString());
            }
            else {
                oleFieldPrice.setOneHalfHour("");
            }
        }
    }

    private void twoHourTextChanged() {
        int index = checkDayExist(currentDayId);
        if (index != -1) {
            OleFieldPrice oleFieldPrice = priceList.get(index);
            if (!binding.etTwoHour.getText().toString().isEmpty()) {
                oleFieldPrice.setTwoHour(binding.etTwoHour.getText().toString());
            }
            else {
                oleFieldPrice.setTwoHour("");
            }
        }
    }

    private void addSamePrice() {
        priceList.clear();
        for (int i = 0; i < 7; i++) {
            OleFieldPrice oleFieldPrice = new OleFieldPrice();
            oleFieldPrice.setDayId(String.valueOf(i+1));
            oleFieldPrice.setOneHour(binding.etOneHour.getText().toString());
            oleFieldPrice.setOneHalfHour(binding.etOneHalfHour.getText().toString());
            oleFieldPrice.setTwoHour(binding.etTwoHour.getText().toString());
            priceList.add(oleFieldPrice);
        }
    }

    private int checkDayExist(String dayId) {
        int index = -1;
        for (int i = 0; i < priceList.size(); i++) {
            if (priceList.get(i).getDayId().equalsIgnoreCase(dayId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onBackPressed() {
        backClicked();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            backClicked();
        }
        else if (v == binding.btnAdd) {
            if (isPadel) {
                addPadelClicked();
            }
            else {
                addFootballClicked();
            }
        }
        else if (v == binding.imgVuBanner) {
            bannerClicked();
        }
        else if (v == binding.tvPreview) {
            previewClicked();
        }
        else if (v == binding.btnAddClub) {
            startActivity(new Intent(getContext(), OleAddClubActivity.class));
        }
    }

    private void previewClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ColorPreviewDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleColorPreviewDialogFragment dialogFragment = new OleColorPreviewDialogFragment(colorList, colorListAdapter.getSelectedIndex());
        dialogFragment.setDialogCallback(new OleColorPreviewDialogFragment.ColorPreviewDialogCallback() {
            @Override
            public void colorPicked(int colorPos) {
                colorListAdapter.setSelectedIndex(colorPos);
                OleColorModel oleColorModel = colorList.get(colorPos);
                color = oleColorModel.getColor();
            }
        });
        dialogFragment.show(fragmentTransaction, "ColorDialogFragment");
    }

    private void backClicked() {
        if (stepCount == 2) {
            moveToStep1();
        }
        else {
            finish();
        }
    }

    private void bannerClicked() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                easyImage = new EasyImage.Builder(getContext())
                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                        .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false).build();
                easyImage.openChooser(OleAddFieldActivity.this);
            }
        });
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
                Glide.with(getContext()).load(file).into(binding.imgVuBanner);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
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
                        builder.start(OleAddFieldActivity.this);
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

    private void addFootballClicked() {
        if (stepCount == 1) {
            if (!isFootballUpdate && club == null) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            if (binding.etFieldName.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_field_name), FancyToast.ERROR);
                return;
            }
            if (sizeId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field_size), FancyToast.ERROR);
                return;
            }
            if (fieldTypeId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field_type), FancyToast.ERROR);
                return;
            }
            if (grassTypeId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_grass_type), FancyToast.ERROR);
                return;
            }
            moveToStep2();
        }
        else {
            String field1Id = "", field2Id = "", field3Id = "";
            if (!isFootballUpdate) {
                if (isMerge.equalsIgnoreCase("1")) {
                    if (fieldAdapter.selectedFields.size() < 2) {
                        Functions.showToast(getContext(), getString(R.string.select_atleast_teo_fields), FancyToast.ERROR);
                        return;
                    }
                    for (int i = 0; i < fieldAdapter.selectedFields.size(); i++) {
                        if (i == 0) {
                            field1Id = fieldAdapter.selectedFields.get(i).getId();
                        } else if (i == 1) {
                            field2Id = fieldAdapter.selectedFields.get(i).getId();
                        } else if (i == 2) {
                            field3Id = fieldAdapter.selectedFields.get(i).getId();
                        }
                    }
                }
            }
            if (binding.priceSwitch.isChecked()) {
                if (binding.etOneHour.getText().toString().isEmpty() || binding.etOneHalfHour.getText().toString().isEmpty() || binding.etTwoHour.getText().toString().isEmpty()) {
                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
                    return;
                }
                addSamePrice();
            }
            else {
                if (priceList.size() < 7) {
                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
                    return;
                }
            }

            String price = "";
            try {
                JSONArray array = new JSONArray();
                for (OleFieldPrice oleFieldPrice : priceList) {
                    JSONObject object = new JSONObject();
                    object.put("one_half_hour", oleFieldPrice.getOneHalfHour());
                    object.put("one_hour", oleFieldPrice.getOneHour());
                    object.put("two_hour", oleFieldPrice.getTwoHour());
                    object.put("day_id", oleFieldPrice.getDayId());
                    array.put(object);
                }
                price = array.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isFootballUpdate) {
                updateFieldAPI(true, binding.etFieldName.getText().toString(), price);
            }
            else {
                addFieldAPI(true, binding.etFieldName.getText().toString(), price, field1Id, field2Id, field3Id);
            }
        }
    }

    private void moveToStep1() {
        binding.step1Vu.setVisibility(View.VISIBLE);
        binding.step2Vu.setVisibility(View.GONE);
        stepCount = 1;
        binding.btnTitle.setText(R.string.next);
    }

    private void moveToStep2() {
        binding.step1Vu.setVisibility(View.GONE);
        binding.step2Vu.setVisibility(View.VISIBLE);
        stepCount = 2;
        if (isFootballUpdate || isPadelUpdate) {
            binding.btnTitle.setText(R.string.update);
        }
        else {
            binding.btnTitle.setText(R.string.add_now);
        }
    }

    private void addPadelClicked() {
        if (stepCount == 1) {
            if (!isPadelUpdate && coverImage == null) {
                Functions.showToast(getContext(), getString(R.string.add_cover_image), FancyToast.ERROR);
                return;
            }
            if (!isPadelUpdate && club == null) {
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            if (binding.etFieldName.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_field_name), FancyToast.ERROR);
                return;
            }
            if (fieldTypeId.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_field_type), FancyToast.ERROR);
                return;
            }
            moveToStep2();
        }
        else {
            if (binding.priceSwitch.isChecked()) {
                if (binding.etOneHour.getText().toString().isEmpty() || binding.etOneHalfHour.getText().toString().isEmpty() || binding.etTwoHour.getText().toString().isEmpty()) {
                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
                    return;
                }
                addSamePrice();
            }
            else {
                if (priceList.size() < 7) {
                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
                    return;
                }
            }

            String price = "";
            try {
                JSONArray array = new JSONArray();
                for (OleFieldPrice oleFieldPrice : priceList) {
                    JSONObject object = new JSONObject();
                    object.put("one_half_hour", oleFieldPrice.getOneHalfHour());
                    object.put("one_hour", oleFieldPrice.getOneHour());
                    object.put("two_hour", oleFieldPrice.getTwoHour());
                    object.put("day_id", oleFieldPrice.getDayId());
                    array.put(object);
                }
                price = array.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isPadelUpdate) {
                updatePadelFieldApi(binding.etFieldName.getText().toString(), price);
            }
            else {
                addPadelFieldApi(binding.etFieldName.getText().toString(), price);
            }
        }
    }

    private void mergeSwitchChanged(SwitchCompat aSwitch) {
        if (aSwitch.isChecked()) {
            if (club == null) {
                aSwitch.setChecked(false);
                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
                return;
            }
            if (club.getFieldsCount() >= 2) {
                isMerge = "1";
                if (fieldList.size() == 0) {
                    getAllFieldsAPI(true, club.getId());
                }
                binding.fieldRecyclerVu.setVisibility(View.VISIBLE);
            } else {
                aSwitch.setChecked(false);
                Functions.showToast(getContext(), getString(R.string.min_two_fields_merge), FancyToast.ERROR);
            }
        }
        else {
            isMerge = "0";
            binding.fieldRecyclerVu.setVisibility(View.GONE);
        }
    }

    private void populateData() {
        if (isFootballUpdate) {
            sizeId = field.getFieldSize().getId();
            for (int i = 0; i < AppManager.getInstance().oleFieldData.getFieldSizes().size(); i++) {
                OleFieldDataChild dataChild = AppManager.getInstance().oleFieldData.getFieldSizes().get(i);
                if (dataChild.getId().equalsIgnoreCase(sizeId)) {
                    oleFieldSizeAdapter.setSelectedIndex(i);
                    break;
                }
            }
            grassTypeId = field.getGrassType().getId();
            for (int i = 0; i < AppManager.getInstance().oleFieldData.getGrassType().size(); i++) {
                OleFieldDataChild dataChild = AppManager.getInstance().oleFieldData.getGrassType().get(i);
                if (dataChild.getId().equalsIgnoreCase(grassTypeId)) {
                    oleGrassTypeAdapter.setSelectedIndex(i);
                    break;
                }
            }
            isMerge = field.getIsMerge();
        }
        if (isPadelUpdate && field.getImages() != null && field.getImages().size() > 0) {
            Glide.with(getContext()).load(field.getImages().get(0).getPhotoPath()).into(binding.imgVuBanner);
        }
        binding.etFieldName.setText(field.getName());
        fieldTypeId = field.getFieldType().getId();
        for (int i = 0; i < AppManager.getInstance().oleFieldData.getFiledTypes().size(); i++) {
            OleFieldDataChild dataChild = AppManager.getInstance().oleFieldData.getFiledTypes().get(i);
            if (dataChild.getId().equalsIgnoreCase(fieldTypeId)) {
                oleFieldTypeAdapter.setSelectedIndex(i);
                break;
            }
        }
        color = field.getFieldColor();
        for (int i = 0; i < colorList.size(); i++) {
            if (colorList.get(i).getColor().equalsIgnoreCase(color)) {
                colorListAdapter.setSelectedIndex(i);
                break;
            }
        }
        priceList.clear();
        priceList.addAll(field.getDaysPrice());
        currentDayId = "1";
        dayAdapter.setCurrentDayId(currentDayId);
        dayAdapter.notifyDataSetChanged();
        for (OleFieldPrice oleFieldPrice : priceList) {
            if (oleFieldPrice.getDayId().equalsIgnoreCase(currentDayId)) {
                binding.etOneHour.setText(oleFieldPrice.getOneHour());
                binding.etOneHalfHour.setText(oleFieldPrice.getOneHalfHour());
                binding.etTwoHour.setText(oleFieldPrice.getTwoHour());
                break;
            }
        }
    }

    private void getAllFieldsAPI(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
        call.enqueue(new Callback<ResponseBody>() {
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
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                            }
                            if (isFootballUpdate) {
                                for (Field f: fieldList) {
                                    if (f.getId().equalsIgnoreCase(field.getField1Id()) || f.getId().equalsIgnoreCase(field.getField2Id()) || f.getId().equalsIgnoreCase(field.getField3Id())) {
                                        fieldAdapter.selectedFields.add(f);
                                    }
                                }
                            }
                            fieldAdapter.notifyDataSetChanged();
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

    private void getOneFieldAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOneField(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), fieldId);
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
                            field = gson.fromJson(obj.toString(), Field.class);
                            populateData();
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

    private void addFieldAPI(boolean isLoader, String name, String price, String field1Id, String field2Id, String field3Id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addField(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),
                club.getId(), name, fieldTypeId, sizeId, grassTypeId, color, isMerge, field1Id, field2Id, field3Id, price);
        call.enqueue(new Callback<ResponseBody>() {
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

    private void addPadelFieldApi(String name, String price) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part coverPart = null;
        if (coverImage != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
            coverPart = MultipartBody.Part.createFormData("cover", coverImage.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPadelField(coverPart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), club.getId()),
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), fieldTypeId),
                RequestBody.create(MediaType.parse("text/plain"), color),
                RequestBody.create(MediaType.parse("text/plain"), price));
        call.enqueue(new Callback<ResponseBody>() {
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

    private void updatePadelFieldApi(String name, String price) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part coverPart = null;
        if (coverImage != null) {
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
            coverPart = MultipartBody.Part.createFormData("cover", coverImage.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePadelField(coverPart,
                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("text/plain"), clubId),
                RequestBody.create(MediaType.parse("text/plain"), fieldId),
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), fieldTypeId),
                RequestBody.create(MediaType.parse("text/plain"), color),
                RequestBody.create(MediaType.parse("text/plain"), price));
        call.enqueue(new Callback<ResponseBody>() {
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

    private void updateFieldAPI(boolean isLoader, String name, String price) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateField(Functions.getAppLang(getContext()),
                Functions.getPrefValue(getContext(), Constants.kUserID),
                clubId, fieldId, name, fieldTypeId, sizeId, grassTypeId, color, price);
        call.enqueue(new Callback<ResponseBody>() {
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

    private void getClubList(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.getMyClubs(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            AppManager.getInstance().clubs.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Club club = gson.fromJson(arr.get(i).toString(), Club.class);
                                AppManager.getInstance().clubs.add(club);
                            }
                            populateClub();
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
