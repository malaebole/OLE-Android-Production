package ae.oleapp.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;

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
import java.io.OutputStream;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityShareFieldBinding;
import ae.oleapp.models.DragData;
import ae.oleapp.models.FormationTeams;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.PreviewFieldView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareFieldActivity extends BaseActivity implements View.OnClickListener {

    private ActivityShareFieldBinding binding;
    private String gameId = "", teamAStatus = "", teamBStatus = "", teamAId = "", teamBId = "";
    private GameTeam gameTeam;
    private int teamAVuWidth = 0, teamAVuHeight = 0, teamBVuWidth = 0, teamBVuHeight = 0, selectedTab = 0;
    private int fullTeamAVuWidth = 0, fullTeamAVuHeight = 0, fullTeamBVuWidth = 0, fullTeamBVuHeight = 0;
    private float subVuH = 0, subVuW = 0;
    private boolean teamACaptainAvailable = false, teamBCaptainAvailable = false;
    private String teamACaptainId = "", teamBCaptainId = "";
    private boolean isFromGroupFormation = false;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareFieldBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameId = bundle.getString("game_id", "");
            isFromGroupFormation = bundle.getBoolean("is_group_formation", false);
        }

        binding.vuTeamA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamA.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                teamAVuWidth = binding.vuTeamA.getWidth();
                teamAVuHeight = binding.vuTeamA.getHeight();
            }
        });

        binding.vuTeamB.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.vuTeamB.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                teamBVuWidth = binding.vuTeamB.getWidth();
                teamBVuHeight = binding.vuTeamB.getHeight();
            }
        });

        binding.fullTeamAVu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.fullTeamAVu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                fullTeamAVuWidth = binding.fullTeamAVu.getWidth();
                fullTeamAVuHeight = binding.fullTeamAVu.getHeight();
            }
        });
        binding.fullTeamBVu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.fullTeamBVu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                fullTeamBVuWidth = binding.fullTeamBVu.getWidth();
                fullTeamBVuHeight = binding.fullTeamBVu.getHeight();
            }
        });

        binding.vuTeamA.setVisibility(View.VISIBLE);
        binding.vuTeamB.setVisibility(View.INVISIBLE);
        binding.fullVuDetail.setVisibility(View.INVISIBLE);
        binding.btnShare.setVisibility(View.INVISIBLE);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    selectedTab = 0;
                    binding.btnShare.setVisibility(View.INVISIBLE);
                    binding.fullVuDetail.setVisibility(View.INVISIBLE);
                    binding.filterVu.setVisibility(View.VISIBLE);
                    binding.btmVu.setVisibility(View.VISIBLE);
                    binding.shareVu.setVisibility(View.VISIBLE);
                    binding.vuTeamA.setVisibility(View.VISIBLE);
                    binding.vuTeamB.setVisibility(View.INVISIBLE);
                    binding.tvTeamName.setText(gameTeam.getTeamAName());
                    binding.tvTeamTitle.setText(gameTeam.getTeamAName());
                    resetTabs();
                    if (teamAStatus.equalsIgnoreCase("win")) {
                        binding.imgVuWin.setImageResource(R.drawable.rating_check);
                        binding.tvWin.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                    else if (teamAStatus.equalsIgnoreCase("loss")) {
                        binding.imgVuLost.setImageResource(R.drawable.rating_check);
                        binding.tvLost.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                    else if (teamAStatus.equalsIgnoreCase("played")) {
                        binding.imgVuPlayed.setImageResource(R.drawable.rating_check);
                        binding.tvPlayed.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                    else if (teamAStatus.equalsIgnoreCase("skills")) {
                        binding.imgVuSkills.setImageResource(R.drawable.rating_check);
                        binding.tvSkills.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                }
                else if (tab.getPosition() == 1) {
                    selectedTab = 1;
                    binding.btnShare.setVisibility(View.INVISIBLE);
                    binding.fullVuDetail.setVisibility(View.INVISIBLE);
                    binding.filterVu.setVisibility(View.VISIBLE);
                    binding.btmVu.setVisibility(View.VISIBLE);
                    binding.shareVu.setVisibility(View.VISIBLE);
                    binding.vuTeamA.setVisibility(View.INVISIBLE);
                    binding.vuTeamB.setVisibility(View.VISIBLE);
                    binding.tvTeamName.setText(gameTeam.getTeamBName());
                    binding.tvTeamTitle.setText(gameTeam.getTeamBName());
                    resetTabs();
                    if (teamBStatus.equalsIgnoreCase("win")) {
                        binding.imgVuWin.setImageResource(R.drawable.rating_check);
                        binding.tvWin.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                    else if (teamBStatus.equalsIgnoreCase("loss")) {
                        binding.imgVuLost.setImageResource(R.drawable.rating_check);
                        binding.tvLost.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                    else if (teamBStatus.equalsIgnoreCase("played")) {
                        binding.imgVuPlayed.setImageResource(R.drawable.rating_check);
                        binding.tvPlayed.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                    else if (teamBStatus.equalsIgnoreCase("skills")) {
                        binding.imgVuSkills.setImageResource(R.drawable.rating_check);
                        binding.tvSkills.setTextColor(getResources().getColor(R.color.yellowColor));
                    }
                }
                else {
                    selectedTab = 2;
                    binding.filterVu.setVisibility(View.GONE);
                    binding.btmVu.setVisibility(View.GONE);
                    binding.shareVu.setVisibility(View.GONE);
                    binding.fullVuDetail.setVisibility(View.VISIBLE);
                    binding.btnShare.setVisibility(View.VISIBLE);
                    binding.tvTeamName.setText(String.format("%s vs %s", gameTeam.getTeamAName(), gameTeam.getTeamBName()));
                    binding.fullViewTitle.setText(String.format("%s vs %s", gameTeam.getTeamAName(), gameTeam.getTeamBName()));
                    binding.tvTeamTitle.setText("");
                    resetTabs();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        PreviewFieldView fieldView = new PreviewFieldView(getContext());
        binding.vuTeamA.addView(fieldView);
        fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                subVuW = fieldView.getWidth();
                subVuH = fieldView.getHeight();
                binding.vuTeamA.removeView(fieldView);
                getTeams(true);
            }
        });

        binding.btnClose.setOnClickListener(this);
        binding.tabWin.setOnClickListener(this);
        binding.tabLost.setOnClickListener(this);
        binding.tabPlayed.setOnClickListener(this);
        binding.tabSkills.setOnClickListener(this);
        binding.btnFacebook.setOnClickListener(this);
        binding.btnWhatsapp.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnInsta.setOnClickListener(this);
        binding.btnMore.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);



    }

    private void shareGameCount(boolean isLoader, String gameId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.shareGameCount(gameId,type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

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

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
        else if (view == binding.tabWin) {
            resetTabs();
            if (selectedTab == 0) {
                if (teamAStatus.equalsIgnoreCase("win")) {
                    teamAStatus = "";
                } else {
                    teamAStatus = "win";
                    binding.imgVuWin.setImageResource(R.drawable.rating_check);
                    binding.tvWin.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            else if (selectedTab == 1) {
                if (teamBStatus.equalsIgnoreCase("win")) {
                    teamBStatus = "";
                } else {
                    teamBStatus = "win";
                    binding.imgVuWin.setImageResource(R.drawable.rating_check);
                    binding.tvWin.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            getTeams(true);
        }
        else if (view == binding.tabLost) {
            resetTabs();
            if (selectedTab == 0) {
                if (teamAStatus.equalsIgnoreCase("loss")) {
                    teamAStatus = "";
                } else {
                    teamAStatus = "loss";
                    binding.imgVuLost.setImageResource(R.drawable.rating_check);
                    binding.tvLost.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            else if (selectedTab == 1) {
                if (teamBStatus.equalsIgnoreCase("loss")) {
                    teamBStatus = "";
                } else {
                    teamBStatus = "loss";
                    binding.imgVuLost.setImageResource(R.drawable.rating_check);
                    binding.tvLost.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            getTeams(true);
        }
        else if (view == binding.tabPlayed) {
            resetTabs();
            if (selectedTab == 0) {
                if (teamAStatus.equalsIgnoreCase("played")) {
                    teamAStatus = "";
                } else {
                    teamAStatus = "played";
                    binding.imgVuPlayed.setImageResource(R.drawable.rating_check);
                    binding.tvPlayed.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            else if (selectedTab == 1) {
                if (teamBStatus.equalsIgnoreCase("played")) {
                    teamBStatus = "";
                } else {
                    teamBStatus = "played";
                    binding.imgVuPlayed.setImageResource(R.drawable.rating_check);
                    binding.tvPlayed.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            getTeams(true);
        }
        else if (view == binding.tabSkills) {
            resetTabs();
            if (selectedTab == 0) {
                if (teamAStatus.equalsIgnoreCase("skills")) {
                    teamAStatus = "";
                } else {
                    teamAStatus = "skills";
                    binding.imgVuSkills.setImageResource(R.drawable.rating_check);
                    binding.tvSkills.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            else if (selectedTab == 1) {
                if (teamBStatus.equalsIgnoreCase("skills")) {
                    teamBStatus = "";
                } else {
                    teamBStatus = "skills";
                    binding.imgVuSkills.setImageResource(R.drawable.rating_check);
                    binding.tvSkills.setTextColor(getResources().getColor(R.color.yellowColor));
                }
            }
            getTeams(true);
        }
        else if (view == binding.btnMore) {
            shareClicked("", false);
        }
        else if (view == binding.btnFacebook) {
            shareClicked("com.facebook.katana", false);
        }
        else if (view == binding.btnWhatsapp) {
            shareClicked("com.whatsapp", false);
        }
        else if (view == binding.btnSave) {
            saveToGallery("");
        }
        else if (view == binding.btnInsta) {
            shareClicked("com.instagram.android", false);
        }
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
                                    shareGameCount(false, gameId, "lineup");
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
                                    shareGameCount(false, gameId, "lineup");
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
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                bitmap = getBitmapFromView(binding.shareVu, binding.fieldBgImgVu.getDrawable());
                binding.fieldLogo.setVisibility(View.INVISIBLE);
                binding.tvTeamTitle.setVisibility(View.INVISIBLE);

                Bitmap finalBitmap = bitmap;
                try {
                    saveBitmap(getContext(), finalBitmap);
                    Functions.showToast(getContext(),getString(R.string.image_saved), FancyToast.SUCCESS, FancyToast.LENGTH_SHORT);
                    shareGameCount(false, gameId, "lineup");
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

    private void resetTabs() {
        binding.imgVuWin.setImageResource(R.drawable.rating_uncheck);
        binding.imgVuLost.setImageResource(R.drawable.rating_uncheck);
        binding.imgVuPlayed.setImageResource(R.drawable.rating_uncheck);
        binding.imgVuSkills.setImageResource(R.drawable.rating_uncheck);
        binding.tvWin.setTextColor(Color.parseColor("#66FFFFFF"));
        binding.tvLost.setTextColor(Color.parseColor("#66FFFFFF"));
        binding.tvPlayed.setTextColor(Color.parseColor("#66FFFFFF"));
        binding.tvSkills.setTextColor(Color.parseColor("#66FFFFFF"));
    }

    private void populateTeamData() {
        teamAId = gameTeam.getTeamAId();
        teamBId = gameTeam.getTeamBId();
        binding.tvTitle.setText(gameTeam.getGroupName());
        binding.tabLayout.getTabAt(0).setText(gameTeam.getTeamAName());
        binding.tabLayout.getTabAt(1).setText(gameTeam.getTeamBName());
        binding.tvTeamName.setText(gameTeam.getTeamAName());
        binding.tvTeamTitle.setText(gameTeam.getTeamAName());
        binding.date.setText(gameTeam.getGameDate());
        binding.stadium.setText(gameTeam.getClubName());
        binding.city.setText(gameTeam.getCityName());

        checkCaptainAvailable();

        for (PlayerInfo info : gameTeam.getTeamAPlayers()) {
            replaceViewTeamA(new DragData(info, -1), 0, 0);
            replaceViewFullTeamA(new DragData(info, -1), 0, 0);
        }

        for (PlayerInfo info : gameTeam.getTeamBPlayers()) {
            replaceViewTeamB(new DragData(info, -1), 0, 0);
            replaceViewFullTeamB(new DragData(info, -1), 0, 0);
        }
    }

    private void checkCaptainAvailable() {
        for (PlayerInfo info : gameTeam.getTeamAPlayers()) {
            if (info.getIsCaptain().equalsIgnoreCase("1")) {
                teamACaptainAvailable = true;
                teamACaptainId = info.getId();
                break;
            }
        }
        for (PlayerInfo info : gameTeam.getTeamBPlayers()) {
            if (info.getIsCaptain().equalsIgnoreCase("1")) {
                teamBCaptainAvailable = true;
                teamBCaptainId = info.getId();
                break;
            }
        }
    }

    private void replaceViewTeamA(DragData state, int x, int y) {
        PlayerInfo info = state.getItem();
        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(),gameTeam.getTeamAPlayers().size(), gameTeam.getTeamBPlayers().size());
//        PreviewFieldView fieldViewA = new PreviewFieldView(getContext());
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
//        else {
//            if (x == 0 && y == 0) {
//                setViewMargin(params, getRandomX(teamAVuWidth, subVuW), getRandomY(teamAVuHeight, subVuH));
//            }
//            else {
//                setViewMargin(params, x, y);
//            }
//            binding.vuTeamA.addView(fieldViewA, params);
//            float relX = (float) params.leftMargin / (float) getScreenWidth();
//            float relY = (float) (params.topMargin) / (float) getScreenHeight();
//            info.setxCoordinate(String.valueOf(relX));
//            info.setyCoordinate(String.valueOf(relY));
//            saveCoordinateAPI(false, gameTeam.getTeamAId(), info.getId(), relX, relY, "0", true);
//        }
    }

    private void replaceViewTeamB(DragData state, int x, int y) {
        PlayerInfo info = state.getItem();
//        PreviewFieldView fieldView = new PreviewFieldView(getContext());
        PreviewFieldView fieldView = new PreviewFieldView(getContext(),gameTeam.getTeamAPlayers().size(), gameTeam.getTeamBPlayers().size());
        populateDataInTeamBVu(fieldView, info, teamBVuWidth, teamBVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * teamBVuWidth; //getScreenWidth();
            float actualYValue = yValue * teamBVuHeight; // getScreenHeight();
            setViewMargin(params, actualXValue, actualYValue);
            binding.vuTeamB.addView(fieldView, params);
        }
//        else {
//            if (x == 0 && y == 0) {
//                setViewMargin(params, getRandomX(teamBVuWidth, subVuW), getRandomY(teamBVuHeight, subVuH));
//            }
//            else {
//                setViewMargin(params, x, y);
//            }
//            binding.vuTeamB.addView(fieldView, params);
//            float relX = (float) params.leftMargin / (float) getScreenWidth();
//            float relY = (float) (params.topMargin) / (float) getScreenHeight();
//            info.setxCoordinate(String.valueOf(relX));
//            info.setyCoordinate(String.valueOf(relY));
//            saveCoordinateAPI(false, gameTeam.getTeamBId(), info.getId(), relX, relY, "0", true);
//        }
    }

    private void replaceViewFullTeamA(DragData state, int x, int y) {
        PlayerInfo info = state.getItem();
        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(),gameTeam.getTeamAPlayers().size(), gameTeam.getTeamBPlayers().size());
        populateDataInTeamAVu(fieldViewA, info, fullTeamAVuWidth, fullTeamAVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * fullTeamAVuWidth; //getScreenWidth();
            float actualYValue = yValue *  fullTeamAVuHeight; //getScreenHeight();
            int h = (fullTeamAVuHeight/3)+40;
//            int h = (fullTeamAVuHeight/3);
//            setFullViewMargin(params, actualXValue, fullTeamAVuHeight - h - actualYValue);
            setFullViewMargin(params, actualXValue, fullTeamAVuHeight - h - actualYValue);
            binding.fullTeamAVu.addView(fieldViewA, params);
        }
    }

    private void replaceViewFullTeamB(DragData state, int x, int y) {
        PlayerInfo info = state.getItem();
        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(),gameTeam.getTeamAPlayers().size(), gameTeam.getTeamBPlayers().size());
        populateDataInTeamBVu(fieldViewA, info, fullTeamBVuWidth, fullTeamBVuHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (info.getxCoordinate() != null && !info.getxCoordinate().isEmpty() && info.getyCoordinate() != null && !info.getyCoordinate().isEmpty()) {
            float xValue = Float.parseFloat(info.getxCoordinate());
            float yValue = Float.parseFloat(info.getyCoordinate());
            float actualXValue = xValue * fullTeamBVuWidth; //getScreenWidth();
            float actualYValue = yValue * fullTeamBVuHeight; //getScreenHeight();
            setFullViewMargin(params, actualXValue, actualYValue);
            binding.fullTeamBVu.addView(fieldViewA, params);
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

    private void populateDataInTeamAVu(PreviewFieldView viewA, PlayerInfo playerInfo, int viewWidth, int viewHeight) {
        if (isFromGroupFormation) {
            if (teamACaptainAvailable && teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                viewA.setParentViewSize(viewWidth, viewHeight);
            }
        }
        else {
            if (teamACaptainAvailable) {
                if (teamACaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    viewA.setParentViewSize(viewWidth, viewHeight);
                }
            }
            else {
                viewA.setParentViewSize(viewWidth, viewHeight);
            }
        }
        viewA.setPlayerInfo(playerInfo, gameTeam.getTeamAShirt(), gameTeam.getTeamAgkShirt());
    }

    private void populateDataInTeamBVu(PreviewFieldView viewA, PlayerInfo playerInfo, int viewWidth, int viewHeight) {
        if (isFromGroupFormation) {
            if (teamBCaptainAvailable && teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                viewA.setParentViewSize(viewWidth, viewHeight);
            }
        }
        else {
            if (teamBCaptainAvailable) {
                if (teamBCaptainId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    viewA.setParentViewSize(viewWidth, viewHeight);
                }
            }
            else {
                viewA.setParentViewSize(viewWidth, viewHeight);
            }
        }
        viewA.setPlayerInfo(playerInfo, gameTeam.getTeamBShirt(), gameTeam.getTeamBgkShirt());
    }

    private PreviewFieldView checkTeamAGkExist() {
        PreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamA.getChildCount(); i++) {
            if (binding.vuTeamA.getChildAt(i) instanceof PreviewFieldView) {
                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamA.getChildAt(i);
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    break;
                }
            }
        }
        return view;
    }

    private PreviewFieldView checkTeamBGkExist() {
        PreviewFieldView view = null;
        for (int i = 0; i < binding.vuTeamB.getChildCount(); i++) {
            if (binding.vuTeamB.getChildAt(i) instanceof PreviewFieldView) {
                PreviewFieldView vu = (PreviewFieldView) binding.vuTeamB.getChildAt(i);
                if (vu.getPlayerInfo().getIsGoalkeeper() != null && vu.getPlayerInfo().getIsGoalkeeper().equalsIgnoreCase("1")) {
                    view = vu;
                    break;
                }
            }
        }
        return view;
    }

    PreviewFieldView.PreviewFieldViewCallback previewFieldCallback = new PreviewFieldView.PreviewFieldViewCallback() {
        @Override
        public void didStartDrag(PreviewFieldView view, PlayerInfo playerInfo, float newX, float newY) {

        }

        @Override
        public void didEndDrag(PreviewFieldView view, PlayerInfo playerInfo, float newX, float newY) {
            System.out.println(newX);
            System.out.println(newY);
            float relX = newX / (float) teamAVuWidth; //getScreenWidth();
            float relY = newY / (float) teamAVuHeight; //getScreenHeight();
            boolean isGK = false;
            if (selectedTab == 0) {
                if (newY + view.getHeight() > teamAVuHeight - 50) {
                    int w = teamAVuWidth / 4;
                    // goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamAVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    // check gk exist already && replace position
                    PreviewFieldView existingGk = checkTeamAGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamA.removeView(existingGk);
//                        PreviewFieldView fieldViewA = new PreviewFieldView(getContext());
                        PreviewFieldView fieldViewA = new PreviewFieldView(getContext(), gameTeam.getTeamAPlayers().size(), gameTeam.getTeamBPlayers().size());
                        populateDataInTeamAVu(fieldViewA, existingGk.getPlayerInfo(), teamAVuWidth, teamAVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue *teamAVuWidth; //getScreenWidth();
                        float actualYValue = yValue * teamAVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamA.addView(fieldViewA, params);
//                        saveCoordinateAPI(false, gameTeam.getTeamAId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", false);
                    }
                    //////
//                    saveCoordinateAPI(false, gameTeam.getTeamAId(), playerInfo.getId(), relX, relY, "1", false);
                    view.setImage(gameTeam.getTeamAgkShirt());
                    view.getPlayerInfo().setIsGoalkeeper("1");
                }
                else {
//                    saveCoordinateAPI(false, gameTeam.getTeamAId(), playerInfo.getId(), relX, relY, "0", false);
                    view.getPlayerInfo().setIsGoalkeeper("0");
                    view.setImage(gameTeam.getTeamAShirt());
                }
                view.getPlayerInfo().setxCoordinate(String.valueOf(relX));
                view.getPlayerInfo().setyCoordinate(String.valueOf(relY));
            }
            else {
                if (newY + view.getHeight() > teamBVuHeight - 50) {
                    int w = teamBVuWidth / 4;
                    // goal keeper
                    isGK = newX > w && newX + view.getWidth() < teamBVuWidth - w;
                }
                else {
                    isGK = false;
                }
                if (isGK) {
                    // check gk exist already && replace position
                    PreviewFieldView existingGk = checkTeamBGkExist();
                    if (existingGk != null && !existingGk.getPlayerInfo().getId().equalsIgnoreCase(playerInfo.getId())) {
                        existingGk.getPlayerInfo().setxCoordinate(playerInfo.getxCoordinate());
                        existingGk.getPlayerInfo().setyCoordinate(playerInfo.getyCoordinate());
                        existingGk.getPlayerInfo().setIsGoalkeeper("0");
                        binding.vuTeamB.removeView(existingGk);
                        PreviewFieldView fieldView = new PreviewFieldView(getContext(),gameTeam.getTeamAPlayers().size(), gameTeam.getTeamBPlayers().size());
                        populateDataInTeamBVu(fieldView, existingGk.getPlayerInfo(), teamBVuWidth, teamBVuHeight);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        float xValue = Float.parseFloat(playerInfo.getxCoordinate());
                        float yValue = Float.parseFloat(playerInfo.getyCoordinate());
                        float actualXValue = xValue * teamBVuWidth; //getScreenWidth();
                        float actualYValue = yValue * teamBVuHeight; //getScreenHeight();
                        setViewMargin(params, actualXValue, actualYValue);
                        binding.vuTeamB.addView(fieldView, params);
                        // saveCoordinateAPI(false, gameTeam.getTeamBId(), existingGk.getPlayerInfo().getId(), xValue, yValue, "0", false);
                    }
                    //////
                    // saveCoordinateAPI(false, gameTeam.getTeamBId(), playerInfo.getId(), relX, relY, "1", false);
                    view.setImage(gameTeam.getTeamBgkShirt());
                    playerInfo.setIsGoalkeeper("1");
                }
                else {
                    //saveCoordinateAPI(false, gameTeam.getTeamBId(), playerInfo.getId(), relX, relY, "0", false);
                    view.setImage(gameTeam.getTeamBShirt());
                    playerInfo.setIsGoalkeeper("0");
                }
                playerInfo.setxCoordinate(String.valueOf(relX));
                playerInfo.setyCoordinate(String.valueOf(relY));
            }
        }
    };

    private void getTeams(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getFormationDetails(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), gameId, teamAId, teamAStatus, teamBId, teamBStatus);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            gameTeam = new Gson().fromJson(object.getJSONObject(Constants.kData).toString(), GameTeam.class);
                            binding.vuTeamA.removeAllViews();
                            binding.vuTeamB.removeAllViews();
                            binding.fullTeamAVu.removeAllViews();
                            binding.fullTeamBVu.removeAllViews();
                            populateTeamData();
                            Glide.with(getContext()).load(object.getJSONObject(Constants.kData).getString("bg_image")).into(binding.fieldBgImgVu);
                            Glide.with(getContext()).load(object.getJSONObject(Constants.kData).getString("field_image")).into(binding.fieldImgVu);
                            Glide.with(getContext()).load(object.getJSONObject(Constants.kData).getString("share_image")).into(binding.finalshare);

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