package ae.oleapp.player;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePreviewFieldAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityNormalPreviewFieldBinding;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OlePlayerCoordinate;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleNormalPreviewFieldView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleNormalPreviewFieldActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityNormalPreviewFieldBinding binding;
    private String bookingId = "";
    private OleGameTeam oleGameTeam;
    private final List<OlePlayerCoordinate> coordinateList = new ArrayList<>();
    private float HEADER_HEIGHT = 0;
    private OlePreviewFieldAdapter adapter;
    private boolean isField = false;
    private List<OleKeyValuePair> shirtList, fieldList;
    private OleNormalPreviewFieldView selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityNormalPreviewFieldBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.preview);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            String str = bundle.getString("team", "");
            Gson gson = new Gson();
            oleGameTeam = gson.fromJson(str, OleGameTeam.class);
        }

        HEADER_HEIGHT = getResources().getDimension(R.dimen._55sdp);

        shirtList = new ArrayList<>();
        shirtList.add(new OleKeyValuePair("#000000", "black_shirt"));
        shirtList.add(new OleKeyValuePair("#C4C4C4", "gray_shirt"));
        shirtList.add(new OleKeyValuePair("#1E75C9", "blue_shirt"));
        shirtList.add(new OleKeyValuePair("#FFBA00", "yellow_shirt"));
        shirtList.add(new OleKeyValuePair("#FD6C9E", "pink_shirt"));
        shirtList.add(new OleKeyValuePair("#FE2717", "red_shirt"));
        shirtList.add(new OleKeyValuePair("#800080", "purple_shirt"));
        shirtList.add(new OleKeyValuePair("#0CFFEC", "cyan_shirt"));

        fieldList = new ArrayList<>();
        fieldList.add(new OleKeyValuePair("field_bg_1", "field_bg_1"));
        fieldList.add(new OleKeyValuePair("field_bg_2", "field_bg_2"));
        fieldList.add(new OleKeyValuePair("field_bg_3", "field_bg_3"));
        fieldList.add(new OleKeyValuePair("field_bg_4", "field_bg_4"));
        fieldList.add(new OleKeyValuePair("field_bg_5", "field_bg_5"));
        fieldList.add(new OleKeyValuePair("field_bg_6", "field_bg_6"));
        fieldList.add(new OleKeyValuePair("field_bg_7", "field_bg_7"));
        fieldList.add(new OleKeyValuePair("field_bg_8", "field_bg_8"));

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapter = new OlePreviewFieldAdapter(getContext(), new ArrayList<>(), isField);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        shirtClicked();

        teamAClicked();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    teamAClicked();
                }
                else {
                    teamBClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnShirt.setOnClickListener(this);
        binding.btnField.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

        getTeamAPI(true);
    }

    OlePreviewFieldAdapter.ItemClickListener itemClickListener = new OlePreviewFieldAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (isField) {
                String name = fieldList.get(pos).getValue();
                if (binding.tabLayout.getSelectedTabPosition() == 0) {
                    binding.fieldBgVu.setImageResource(adapter.getDrawable(name));
                    binding.fieldImgVu.setImageResource(adapter.getDrawable(name+"_img"));
                    oleGameTeam.setTeamAImage(name);
                    updateTeamAPI(oleGameTeam.getTeamAId(), "", "", "", name, "");
                }
                else {
                    binding.fieldBgVu.setImageResource(adapter.getDrawable(name));
                    binding.fieldImgVu.setImageResource(adapter.getDrawable(name+"_img"));
                    oleGameTeam.setTeamBImage(name);
                    updateTeamAPI("", oleGameTeam.getTeamBId(), "", "", "", name);
                }
            }
            else {
                String color = shirtList.get(pos).getKey();
                String name = shirtList.get(pos).getValue();
                if (binding.tabLayout.getSelectedTabPosition() == 0) {
                    binding.vuColor.setCardBackgroundColor(Color.parseColor(color));
                    oleGameTeam.setTeamAColor(color);
                    updateTeamAPI(oleGameTeam.getTeamAId(), "", color, "", "", "");
                    for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
                        if (binding.vuTeamA.getChildAt(i) instanceof OleNormalPreviewFieldView) {
                            OleNormalPreviewFieldView vu = (OleNormalPreviewFieldView) binding.vuTeamA.getChildAt(i);
                            vu.setImage(name);
                        }
                    }
                }
                else {
                    binding.vuColor.setCardBackgroundColor(Color.parseColor(color));
                    oleGameTeam.setTeamBColor(color);
                    updateTeamAPI("", oleGameTeam.getTeamBId(), "", color, "", "");
                    for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
                        if (binding.vuTeamB.getChildAt(i) instanceof OleNormalPreviewFieldView) {
                            OleNormalPreviewFieldView vu = (OleNormalPreviewFieldView) binding.vuTeamB.getChildAt(i);
                            vu.setImage(name);
                        }
                    }
                }
            }
        }
    };

    private void teamAClicked() {
        binding.tvTeamName.setText(oleGameTeam.getTeamAName());
        binding.vuColor.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamAColor()));
        binding.vuTeamA.setVisibility(View.VISIBLE);
        binding.vuTeamB.setVisibility(View.INVISIBLE);
        if (oleGameTeam.getTeamAImage().isEmpty()) {
            binding.fieldBgVu.setImageResource(R.drawable.field_bg_1);
            binding.fieldImgVu.setImageResource(R.drawable.field_bg_1_img);
        }
        else {
            binding.fieldBgVu.setImageResource(adapter.getDrawable(oleGameTeam.getTeamAImage()));
            binding.fieldImgVu.setImageResource(adapter.getDrawable(oleGameTeam.getTeamAImage()+"_img"));
        }
    }

    private void teamBClicked() {
        binding.tvTeamName.setText(oleGameTeam.getTeamBName());
        binding.vuColor.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamBColor()));
        binding.vuTeamA.setVisibility(View.INVISIBLE);
        binding.vuTeamB.setVisibility(View.VISIBLE);
        if (oleGameTeam.getTeamBImage().isEmpty()) {
            binding.fieldBgVu.setImageResource(R.drawable.field_bg_1);
            binding.fieldImgVu.setImageResource(R.drawable.field_bg_1_img);
        }
        else {
            binding.fieldBgVu.setImageResource(adapter.getDrawable(oleGameTeam.getTeamBImage()));
            binding.fieldImgVu.setImageResource(adapter.getDrawable(oleGameTeam.getTeamBImage()+"_img"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnShirt) {
            shirtClicked();
        }
        else if (v == binding.btnField) {
            fieldClicked();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void shirtClicked() {
        isField = false;
        binding.btnShirt.setImageResource(R.drawable.t_shirt_ic_blue);
        binding.btnField.setImageResource(R.drawable.field_black);
        adapter.setDatasource(shirtList, false);
    }

    private void fieldClicked() {
        isField = true;
        binding.btnShirt.setImageResource(R.drawable.t_shirt_ic);
        binding.btnField.setImageResource(R.drawable.field_blue);
        adapter.setDatasource(fieldList, true);
    }

    private void shareClicked() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else{
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.

                ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                        .setCancelButtonTitle(getResources().getString(R.string.cancel))
                        .setOtherButtonTitles(getResources().getString(R.string.image), getResources().getString(R.string.pdf_file))
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                if (index == 0) {
                                    Bitmap bitmap = getBitmapFromView(binding.shareVu);
//                                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "OleField", null);
//                                    Uri uri = Uri.parse(path);

                                    try {
                                        Uri uri = saveBitmap(getContext(), bitmap);

                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/*");
                                        share.putExtra(Intent.EXTRA_STREAM, uri);
                                        startActivity(Intent.createChooser(share, "Share"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    Bitmap bitmap = getBitmapFromView(binding.shareVu);
                                    createPdf(bitmap);
                                }
                            }
                        }).show();
            }
        });

    }

    private void createPdf(Bitmap bitmap){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Ole");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File file = new File(filePath, "OleField.pdf");
        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e, Toast.LENGTH_LONG).show();
        }
        document.close();

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share"));

    }

    private void populateData() {
        binding.tabLayout.getTabAt(0).setText(oleGameTeam.getTeamAName());
        binding.tabLayout.getTabAt(1).setText(oleGameTeam.getTeamBName());

        if (binding.tabLayout.getSelectedTabPosition() == 0) {
            teamAClicked();
        }
        else {
            teamBClicked();
        }
        binding.vuTeamA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamA.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                generateTeamAViews();

                if (coordinateList.size()>0) {
                    playerCoordinateAPI(true, coordinateList);
                }

            }
        });

        binding.vuTeamB.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamB.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                generateTeamBViews();

                if (coordinateList.size()>0) {
                    playerCoordinateAPI(true, coordinateList);
                }

            }
        });
        binding.vuTeamA.requestLayout();
        binding.vuTeamB.requestLayout();
    }

    private void generateTeamAViews() {
        int viewWidth = binding.vuTeamA.getWidth();
        int viewHeight = binding.vuTeamA.getHeight();
        float subVuW = viewWidth/4;
        float subVuH = viewHeight/3;
        int maxPlayers = 11;
        if (oleGameTeam.getTeamAPlayers().size() < maxPlayers) {
            maxPlayers = oleGameTeam.getTeamAPlayers().size();
        }
        int numViews = 0;
        while (numViews<maxPlayers) {
            OlePlayerInfo olePlayerInfo = oleGameTeam.getTeamAPlayers().get(numViews);
            if (olePlayerInfo.getxCoordinate() != null && !olePlayerInfo.getxCoordinate().isEmpty() && olePlayerInfo.getyCoordinate() != null && !olePlayerInfo.getyCoordinate().isEmpty()) {
                float xValue = Float.parseFloat(olePlayerInfo.getxCoordinate());
                float yValue = Float.parseFloat(olePlayerInfo.getyCoordinate());
                float actualXValue = xValue * getScreenWidth();
                float actualYValue = yValue * getScreenHeight();
                OleNormalPreviewFieldView fieldViewA = new OleNormalPreviewFieldView(getContext());
                populateDataInTeamAVu(fieldViewA, olePlayerInfo, viewWidth, viewHeight);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) subVuW, (int) subVuH);
                params.leftMargin = (int) actualXValue;
                if (viewWidth-params.leftMargin < subVuW) {
                    params.leftMargin = viewWidth - (int) subVuW;
                }
                params.topMargin = (int) actualYValue - (int) HEADER_HEIGHT;
                if (viewHeight-params.topMargin < subVuH) {
                    params.topMargin = viewHeight - (int) subVuH;
                }
                binding.vuTeamA.addView(fieldViewA, params);
                numViews += 1;
            }
            else {
                OleNormalPreviewFieldView fieldViewA = new OleNormalPreviewFieldView(getContext());
                populateDataInTeamAVu(fieldViewA, olePlayerInfo, viewWidth, viewHeight);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) subVuW, (int) subVuH);
                params.leftMargin = getRandomX(viewWidth, subVuW);
                if (viewWidth-params.leftMargin < subVuW) {
                    params.leftMargin = viewWidth - (int) subVuW;
                }
                params.topMargin = getRandomY(viewHeight, subVuH); //- (int) HEADER_HEIGHT;
                if (viewHeight-params.topMargin < subVuH) {
                    params.topMargin = viewHeight - (int) subVuH;
                }
                binding.vuTeamA.addView(fieldViewA, params);
                float relX = (float) params.leftMargin / (float) getScreenWidth();
                float relY = (params.topMargin + HEADER_HEIGHT) / (float) getScreenHeight();
//                    float relX = getRelativeX(fieldViewA);
//                    float relY = getRelativeY(fieldViewA);
                int index = checkCoordinateExist(olePlayerInfo.getId());
                if (index != -1) {
                    coordinateList.get(index).setxCoordinate(relX);
                    coordinateList.get(index).setyCoordinate(relY);
                }
                else {
                    coordinateList.add(new OlePlayerCoordinate(olePlayerInfo.getId(), oleGameTeam.getTeamAId(), relX, relY));
                }
                numViews += 1;
            }
        }
    }

    private void generateTeamBViews() {
        int viewWidth = binding.vuTeamB.getWidth();
        int viewHeight = binding.vuTeamB.getHeight();
        float subVuW = viewWidth/4;
        float subVuH = viewHeight/3;
        int maxPlayers = 11;
        if (oleGameTeam.getTeamBPlayers().size() < maxPlayers) {
            maxPlayers = oleGameTeam.getTeamBPlayers().size();
        }
        int numViews = 0;
        while (numViews<maxPlayers) {
            OlePlayerInfo olePlayerInfo = oleGameTeam.getTeamBPlayers().get(numViews);
            if (olePlayerInfo.getxCoordinate() != null && !olePlayerInfo.getxCoordinate().isEmpty() && olePlayerInfo.getyCoordinate() != null && !olePlayerInfo.getyCoordinate().isEmpty()) {
                float xValue = Float.parseFloat(olePlayerInfo.getxCoordinate());
                float yValue = Float.parseFloat(olePlayerInfo.getyCoordinate());
                float actualXValue = xValue * getScreenWidth();
                float actualYValue = yValue * getScreenHeight();
                OleNormalPreviewFieldView fieldViewB = new OleNormalPreviewFieldView(getContext());
                populateDataInTeamBVu(fieldViewB, olePlayerInfo, viewWidth, viewHeight);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) subVuW, (int) subVuH);
                params.leftMargin = (int) actualXValue;
                if (viewWidth-params.leftMargin < subVuW) {
                    params.leftMargin = viewWidth - (int) subVuW;
                }
                params.topMargin = (int) actualYValue - (int) HEADER_HEIGHT;// - viewHeight;
                if (viewHeight-params.topMargin < subVuH) {
                    params.topMargin = viewHeight - (int) subVuH;
                }
                binding.vuTeamB.addView(fieldViewB, params);
                numViews += 1;
            }
            else {
                randomlyGenerateBviews(viewWidth, viewHeight, olePlayerInfo, subVuW, subVuH);
                numViews += 1;
            }
        }
    }

    private void randomlyGenerateBviews(int viewWidth, int viewHeight, OlePlayerInfo olePlayerInfo, float subVuW, float subVuH) {
        OleNormalPreviewFieldView fieldViewB = new OleNormalPreviewFieldView(getContext());
        populateDataInTeamBVu(fieldViewB, olePlayerInfo, viewWidth, viewHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) subVuW, (int) subVuH);
        params.leftMargin = getRandomX(viewWidth, subVuW);
        if (viewWidth-params.leftMargin < subVuW) {
            params.leftMargin = viewWidth - (int) subVuW;
        }
        params.topMargin = getRandomY(viewHeight, subVuH); //- (int) HEADER_HEIGHT;
        if (viewHeight-params.topMargin < subVuH) {
            params.topMargin = viewHeight - (int) subVuH;
        }
        binding.vuTeamB.addView(fieldViewB, params);
        float relX = (float) params.leftMargin / (float) getScreenWidth();
        float relY = (params.topMargin + HEADER_HEIGHT + viewHeight) / (float) getScreenHeight();
        int index = checkCoordinateExist(olePlayerInfo.getId());
        if (index != -1) {
            coordinateList.get(index).setxCoordinate(relX);
            coordinateList.get(index).setyCoordinate(relY);
        }
        else {
            coordinateList.add(new OlePlayerCoordinate(olePlayerInfo.getId(), oleGameTeam.getTeamBId(), relX, relY));
        }
    }

    OleNormalPreviewFieldView.NormalPreviewFieldViewCallback previewFieldACallback = new OleNormalPreviewFieldView.NormalPreviewFieldViewCallback() {
        @Override
        public void didStartDrag() {

        }

        @Override
        public void didEndDrag(OlePlayerInfo olePlayerInfo, float newX, float newY) {
            System.out.println(newX);
            System.out.println(newY);
            newY = newY + HEADER_HEIGHT;
            float relX = newX / (float) getScreenWidth();
            float relY = newY / (float) getScreenHeight();
            int index = checkCoordinateExist(olePlayerInfo.getId());
            if (index != -1) {
                coordinateList.get(index).setxCoordinate(relX);
                coordinateList.get(index).setyCoordinate(relY);
            }
            else {
                if (binding.tabLayout.getSelectedTabPosition() == 0) {
                    coordinateList.add(new OlePlayerCoordinate(olePlayerInfo.getId(), oleGameTeam.getTeamAId(), relX, relY));
                }
                else {
                    coordinateList.add(new OlePlayerCoordinate(olePlayerInfo.getId(), oleGameTeam.getTeamBId(), relX, relY));
                }
            }

            playerCoordinateAPI(false, coordinateList);
        }
    };

    private int checkCoordinateExist(String pId) {
        int result = -1;
        for (int i = 0; i < coordinateList.size(); i++) {
            if (coordinateList.get(i).getPlayerId().equalsIgnoreCase(pId)) {
                result = i;
                break;
            }
        }
        return  result;
    }

    private void populateDataInTeamAVu(OleNormalPreviewFieldView viewA, OlePlayerInfo olePlayerInfo, int viewWidth, int viewHeight) {
        viewA.setParentViewSize(viewWidth, viewHeight);
        viewA.setPreviewFieldACallback(previewFieldACallback);
        String shirt = "";
        for (OleKeyValuePair pair : shirtList) {
            if (pair.getKey().equalsIgnoreCase(oleGameTeam.getTeamAColor())) {
                shirt = pair.getValue();
                break;
            }
        }
        viewA.setPlayerInfo(olePlayerInfo, shirt);
        viewA.setPreviewFieldItemCallback(new OleNormalPreviewFieldView.NormalPreviewFieldItemCallback() {
            @Override
            public void itemClicked(OlePlayerInfo olePlayerInfo) {
                selectedView = viewA;
                photoChangeClicked(olePlayerInfo);
            }
        });
    }

    private void populateDataInTeamBVu(OleNormalPreviewFieldView viewB, OlePlayerInfo olePlayerInfo, int viewWidth, int viewHeight) {
        viewB.setParentViewSize(viewWidth, viewHeight);
        viewB.setPreviewFieldACallback(previewFieldACallback);
        String shirt = "";
        for (OleKeyValuePair pair : shirtList) {
            if (pair.getKey().equalsIgnoreCase(oleGameTeam.getTeamBColor())) {
                shirt = pair.getValue();
                break;
            }
        }
        viewB.setPlayerInfo(olePlayerInfo, shirt);
        viewB.setPreviewFieldItemCallback(new OleNormalPreviewFieldView.NormalPreviewFieldItemCallback() {
            @Override
            public void itemClicked(OlePlayerInfo olePlayerInfo) {
                selectedView = viewB;
                photoChangeClicked(olePlayerInfo);
            }
        });
    }

    private void photoChangeClicked(OlePlayerInfo olePlayerInfo) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.camera), getResources().getString(R.string.gallery), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            pickImage(false);
                        }
                        else if (index == 1) {
                            pickImage(true);
                        }
                        else if (index == 2) {
                            removePhotoAPI(olePlayerInfo.getId());
                        }
                    }
                }).show();
    }

    private void pickImage(boolean isGallery) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                if (isGallery) {
                    FilePickerBuilder.getInstance()
                            .setMaxCount(1)
                            .setActivityTheme(R.style.AppThemePlayer)
                            .setActivityTitle(getString(R.string.image))
                            .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                            .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                            .enableVideoPicker(false)
                            .enableCameraSupport(false)
                            .showGifs(false)
                            .showFolderView(false)
                            .enableSelectAll(false)
                            .enableImagePicker(true)
                            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .pickPhoto(OleNormalPreviewFieldActivity.this, 1121);
                }
                else {
                    Intent intent = new Intent(getContext(), OleCustomCameraActivity.class);
                    intent.putExtra("is_gallery", false);
                    startActivityForResult(intent, 1122);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1122 && resultCode == RESULT_OK) {
            String filePath = data.getExtras().getString("file_path");
            File file = new File(filePath);
            if (selectedView != null) {
                Glide.with(getContext()).load(file).into(selectedView.getProfileImgVu());
                updatePlayerAPI(true, file, selectedView.getPlayerInfo().getId());
            }
        }
        else if (requestCode == 1121 && resultCode == RESULT_OK) {
            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            if (dataList != null && dataList.size() > 0) {
                Uri uri = dataList.get(0);
                try {
                    String filePath = ContentUriUtils.INSTANCE.getFilePath(getContext(), uri);
                    Intent intent = new Intent(getContext(), OleCustomCameraActivity.class);
                    intent.putExtra("is_gallery", true);
                    intent.putExtra("file_path", filePath);
                    startActivityForResult(intent, 1122);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getRandomX(int viewWidth, float subVuW) {
        Random random = new Random();
        return random.nextInt(viewWidth-(int)subVuW);
    }

    public int getRandomY(int viewHeight, float subVuH) {
        Random random = new Random();
        return random.nextInt(viewHeight-(int)subVuH);
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void getTeamAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFriendsCoord(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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
                            oleGameTeam = gson.fromJson(obj.toString(), OleGameTeam.class);
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

    private void playerCoordinateAPI(boolean isLoader, List<OlePlayerCoordinate> list) {
        String data = "";
        try {
            JSONArray array = new JSONArray();
            for (OlePlayerCoordinate coordinate : list) {
                JSONObject object = new JSONObject();
                object.put("player_id", coordinate.getPlayerId());
                object.put("team_id", coordinate.getTeamId());
                object.put("x_coordinate", coordinate.getxCoordinate());
                object.put("y_coordinate", coordinate.getyCoordinate());
                array.put(object);
            }
            data = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.saveFriendsCoord(Functions.getAppLang(getContext()), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            coordinateList.clear();
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

    private void updateTeamAPI(String teamAId, String teamBId, String teamAColor, String teamBColor, String teamAImage, String teamBImage) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateTeamImage(Functions.getAppLang(getContext()), teamAId, teamBId, teamAColor, teamBColor, teamAImage, teamBImage);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

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

    private void updatePlayerAPI(boolean isLoader, File file, String playerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.manageManualPlayerList(part,
                RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getAppLang(getContext())),
                RequestBody.create(MediaType.parse("multipart/form-data"), Functions.getPrefValue(getContext(), Constants.kUserID)),
                RequestBody.create(MediaType.parse("multipart/form-data"), playerId),
                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                RequestBody.create(MediaType.parse("multipart/form-data"), "update"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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

    private void removePhotoAPI(String playerId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeFriendPhoto(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (selectedView != null) {
                                selectedView.getProfileImgVu().setImageResource(0);
                                selectedView.getProfileImgVu().setImageDrawable(null);
                            }
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