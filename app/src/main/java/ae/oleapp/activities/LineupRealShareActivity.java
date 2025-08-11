package ae.oleapp.activities;


import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityLineupRealShareBinding;
import ae.oleapp.databinding.ActivityShareFieldBinding;
import ae.oleapp.models.DragData;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.LineupGlobalPlayers;
import ae.oleapp.models.LineupRealDragData;
import ae.oleapp.models.LineupRealGameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.LineupRealPreviewFieldView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LineupRealShareActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLineupRealShareBinding binding;
    private String teamId = "";
    private LineupRealGameTeam gameTeam;
    private final int teamAVuWidth = 0;
    private final int teamAVuHeight = 0;
    private int fullTeamAVuWidth = 0;
    private int fullTeamAVuHeight = 0;
    private float subVuH = 0, subVuW = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLineupRealShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            teamId = bundle.getString("team_id", "");
        }

//        binding.vuTeamA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                binding.vuTeamA.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                teamAVuWidth = binding.vuTeamA.getWidth();
//                teamAVuHeight = binding.vuTeamA.getHeight();
//            }
//        });


        binding.fullTeamAVu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.fullTeamAVu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                fullTeamAVuWidth = binding.fullTeamAVu.getWidth();
                fullTeamAVuHeight = binding.fullTeamAVu.getHeight();
            }
        });


       // binding.vuTeamA.setVisibility(View.VISIBLE);
        binding.fullVuDetail.setVisibility(View.VISIBLE);
        binding.btnShare.setVisibility(View.VISIBLE);


        LineupRealPreviewFieldView fieldView = new LineupRealPreviewFieldView(getContext());
        binding.fullTeamAVu.addView(fieldView);

        fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                subVuW = fieldView.getWidth();
                subVuH = fieldView.getHeight();
                binding.fullTeamAVu.removeView(fieldView);
                startRealLineup(true, teamId);
            }
        });

        binding.btnClose.setOnClickListener(this);
//        binding.btnFacebook.setOnClickListener(this);
//        binding.btnWhatsapp.setOnClickListener(this);
//        binding.btnSave.setOnClickListener(this);
//        binding.btnInsta.setOnClickListener(this);
//        binding.btnMore.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
//        else if (view == binding.btnMore) {
//            shareClicked("", true);
//        }
//        else if (view == binding.btnFacebook) {
//            shareClicked("com.facebook.katana", true);
//        }
//        else if (view == binding.btnWhatsapp) {
//            shareClicked("com.whatsapp", true);
//        }
//        else if (view == binding.btnSave) {
//            saveToGallery("");
//        }
//        else if (view == binding.btnInsta) {
//            shareClicked("com.instagram.android", true);
//        }
        else if (view == binding.btnShare) {
            shareClicked("", true);
        }
    }

    private void shareClicked(String path, boolean isFullVu) {

        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA,  Manifest.permission.READ_MEDIA_IMAGES};
        }else {
            permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Bitmap bitmap = null;
                if (isFullVu) {
                    binding.fullFieldLogo.setVisibility(View.VISIBLE);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bitmap = getBitmapFromView(binding.fullVuDetail, binding.fieldBgImgVu.getDrawable());
                    binding.fullFieldLogo.setVisibility(View.INVISIBLE);
                }
                else {
                    binding.fieldLogo.setVisibility(View.VISIBLE);
                    binding.tvTeamTitle.setVisibility(View.VISIBLE);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bitmap = getBitmapFromView(binding.shareVu, binding.fieldBgImgVu.getDrawable());
                    binding.fieldLogo.setVisibility(View.INVISIBLE);
                    binding.tvTeamTitle.setVisibility(View.INVISIBLE);
                }

                Bitmap finalBitmap = bitmap;
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
                                    try {
                                        Uri uri = saveBitmap(getContext(), finalBitmap);

                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/*");
                                        share.putExtra(Intent.EXTRA_STREAM, uri);
                                        if (path.isEmpty()) {
                                            startActivity(Intent.createChooser(share, "Share"));
                                        }
                                        else {
                                            share.setPackage(path);
                                            startActivity(share);
                                        }
                                    }
                                    catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        Functions.showToast(getContext(), "Install app first!", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        Uri uri = saveBitmap(getContext(), finalBitmap);

                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/*");
                                        share.putExtra(Intent.EXTRA_STREAM, uri);
                                        startActivity(Intent.createChooser(share, "Share"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    createPdf(finalBitmap);
                                }
                            }
                        }).show();
            }
        });

    }

    private void saveToGallery(String path){
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA,  Manifest.permission.READ_MEDIA_IMAGES};
        }else {
            permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Bitmap bitmap = null;
                binding.fieldLogo.setVisibility(View.VISIBLE);
                binding.tvTeamTitle.setVisibility(View.VISIBLE);
                bitmap = getBitmapFromView(binding.shareVu, binding.fieldBgImgVu.getDrawable());
                binding.fieldLogo.setVisibility(View.INVISIBLE);
                binding.tvTeamTitle.setVisibility(View.INVISIBLE);

                Bitmap finalBitmap = bitmap;
                try {
                    saveBitmap(getContext(), finalBitmap);
                    Functions.showToast(getContext(),getString(R.string.image_saved), FancyToast.SUCCESS, FancyToast.LENGTH_SHORT);
                }
                catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Functions.showToast(getContext(), "Install app first!", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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
        File file = new File(filePath, "OleLineUp1.pdf");
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



    private void populateTeamData() {
        for (LineupGlobalPlayers info : gameTeam.getPlayers()) {
            replaceViewTeamA(new LineupRealDragData(info, -1), 0, 0);
            replaceViewFullTeamA(new LineupRealDragData(info, -1), 0, 0);
        }

    }


    private void replaceViewTeamA(LineupRealDragData state, int x, int y) {
        LineupGlobalPlayers info = state.getItem();
        LineupRealPreviewFieldView fieldViewA = new LineupRealPreviewFieldView(getContext());
        populateDataInTeamAVu(fieldViewA, info, teamAVuWidth, teamAVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * teamAVuWidth; //getScreenWidth();
            float actualYValue = yValue * teamAVuHeight; //getScreenHeight();
            setViewMargin(params, actualXValue, actualYValue);
            binding.vuTeamA.addView(fieldViewA, params);
        }
    }


    private void replaceViewFullTeamA(LineupRealDragData state, int x, int y) {
        LineupGlobalPlayers info = state.getItem();
        LineupRealPreviewFieldView fieldViewA = new LineupRealPreviewFieldView(getContext());
        populateDataInTeamAVu(fieldViewA, info, fullTeamAVuWidth, fullTeamAVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * fullTeamAVuWidth; //getScreenWidth();
            float actualYValue = yValue *  fullTeamAVuHeight; //getScreenHeight();
            int h = (fullTeamAVuHeight/3)+40;
            setFullViewMargin(params, actualXValue, fullTeamAVuHeight - h - actualYValue);
            binding.fullTeamAVu.addView(fieldViewA, params);
        }
    }


    private void setViewMargin(RelativeLayout.LayoutParams params, float xValue, float yValue) {
        params.leftMargin = (int) xValue;
        if (teamAVuWidth-params.leftMargin < subVuW) {
            params.leftMargin = teamAVuWidth - (int) subVuW;
        }
        params.topMargin = (int) yValue;
        if (teamAVuHeight-params.topMargin < subVuH) {
            params.topMargin = teamAVuHeight - (int) subVuH;
        }
    }

    private void setFullViewMargin(RelativeLayout.LayoutParams params, float xValue, float yValue) {
        if (yValue < 0) {
            yValue = 0;
        }
        params.leftMargin = (int) xValue;
        if (fullTeamAVuWidth-params.leftMargin < subVuW) {
            params.leftMargin = fullTeamAVuWidth - (int) subVuW;
        }
        params.topMargin = (int) yValue;
        if (fullTeamAVuHeight-params.topMargin < subVuH) {
            params.topMargin = fullTeamAVuHeight - (int) subVuH;
        }
    }

    private void populateDataInTeamAVu(LineupRealPreviewFieldView viewA, LineupGlobalPlayers playerInfo, int viewWidth, int viewHeight) {

                viewA.setParentViewSize(viewWidth, viewHeight);
                viewA.setPlayerInfo(playerInfo, gameTeam.getTeamShirt(), gameTeam.getTeamGkShirt());
    }


    private LineupRealPreviewFieldView checkTeamAGkExist() {
        LineupRealPreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
            if (binding.vuTeamA.getChildAt(i) instanceof LineupRealPreviewFieldView vu) {
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    break;
                }
            }
        }
        return view;
    }


    LineupRealPreviewFieldView.LineupRealPreviewFieldViewCallback previewFieldCallback = new LineupRealPreviewFieldView.LineupRealPreviewFieldViewCallback() {
        @Override
        public void didStartDrag(LineupRealPreviewFieldView view, LineupGlobalPlayers playerInfo, float newX, float newY) {

        }

        @Override
        public void didEndDrag(LineupRealPreviewFieldView view, LineupGlobalPlayers playerInfo, float newX, float newY) {
            float relX = newX / (float) teamAVuWidth; //getScreenWidth();
            float relY = newY / (float) teamAVuHeight; //getScreenHeight();
            boolean isGK = false;
            if (newY + view.getHeight() > teamAVuHeight - 50) {
                int w = teamAVuWidth / 4;
                // goal keeper
                isGK = newX > w && newX + view.getWidth() < teamAVuWidth - w;
            }
            else {
                isGK = false;
            }
            if (isGK) {
                LineupRealPreviewFieldView existingGk = checkTeamAGkExist();
                if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                    existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                    existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                    existingGk.getPlayerInfo().setIsGoalkeeper("0");
                    binding.vuTeamA.removeView(existingGk);
                    LineupRealPreviewFieldView fieldViewA = new LineupRealPreviewFieldView(getContext());
                    populateDataInTeamAVu(fieldViewA, existingGk.getPlayerInfo(), teamAVuWidth, teamAVuHeight);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                    float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                    float actualXValue = xValue * teamAVuWidth; //getScreenWidth();
                    float actualYValue = yValue * teamAVuHeight; //getScreenHeight();
                    setViewMargin(params, actualXValue, actualYValue);
                    binding.vuTeamA.addView(fieldViewA, params);

                }

                view.setImage(gameTeam.getTeamGkShirt());
                view.getPlayerInfo().setIsGoalkeeper("1");
            }
            else {
                view.getPlayerInfo().setIsGoalkeeper("0");
                view.setImage(gameTeam.getTeamShirt());
            }
            view.getPlayerInfo().setxCoordinate(String.valueOf(relX));
            view.getPlayerInfo().setyCoordinate(String.valueOf(relY));


        }
    };

    private void startRealLineup(boolean isLoader, String teamId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.startRealLineup(teamId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            gameTeam = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), LineupRealGameTeam.class);
                           // binding.vuTeamA.removeAllViews();
                            binding.fullTeamAVu.removeAllViews();
                            populateTeamData();
                            Glide.with(getApplicationContext()).load(gameTeam.getBgImageUrl()).into(binding.fieldBgImgVu);
                            Glide.with(getApplicationContext()).load(gameTeam.getFieldImageUrl()).into(binding.fieldImgVu);
                            Glide.with(getApplicationContext()).load(gameTeam.getShareImageUrl()).into(binding.finalshare);

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
}