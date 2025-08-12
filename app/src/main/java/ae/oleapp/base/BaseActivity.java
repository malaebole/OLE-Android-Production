package ae.oleapp.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.baoyz.actionsheet.ActionSheet;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ae.oleapp.BuildConfig;
import ae.oleapp.R;
import ae.oleapp.dialogs.OleDateRangeFilterDialogFragment;
import ae.oleapp.dialogs.OleLevelCompleteDialogFragment;
import ae.oleapp.dialogs.OleLoyaltyCardDialogFragment;
import ae.oleapp.dialogs.OlePadelWinMatchDialogFragment;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OlePaymentWebDialogFragment;
import ae.oleapp.dialogs.OleRatingPagerDialogFragment;
import ae.oleapp.dialogs.OleRestrictBookingPlayerDialogFragment;
import ae.oleapp.dialogs.OleWinMatchDialogFragment;
import ae.oleapp.dialogs.RatingPagerDialogFragment;
import ae.oleapp.fragments.BestPlayerDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleFieldData;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OlePlayerLevel;
import ae.oleapp.models.Shirt;
import ae.oleapp.player.OleEmployeeRateActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleInAppReviewManager;
import okhttp3.ResponseBody;
import payment.sdk.android.PaymentClient;
import payment.sdk.android.cardpayment.CardPaymentData;
import payment.sdk.android.cardpayment.CardPaymentRequest;
import payment.sdk.android.core.Order;
import payment.sdk.android.samsungpay.SamsungPayResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    private PaymentCallback paymentCallback;
    private String bookingId = "", paymentOrderRef = "";
    public AppUpdateManager appUpdateManager;
    public ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    //public Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        Functions.changeLanguage(this, Functions.getPrefValue(this, Constants.kAppLang));

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        OleInAppReviewManager.showRateDialogIfMeetsConditions(this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK) {
                        Functions.showToast(getContext(), getString(R.string.major_changes_in_app), FancyToast.ERROR);
                        inAppUpdates();
                    }
                });
    }

    protected void applyEdgeToEdge(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(systemBars.left, statusBarInsets.top, systemBars.right, navBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }


///// EDGE TO EDGE START ////
//protected void applyEdgeToEdge(View rootView) {
//    ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
//        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//        // Keep content inside safe area without shifting vertically unless needed
//        return WindowInsetsCompat.CONSUMED;
//    });
//    setStatusBarAndNavBarColor();
//}

//    private void setStatusBarAndNavBarColor() {
//        Window window = getWindow();
//
//        if (Build.VERSION.SDK_INT >= 34) { // Android 15+
//            boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
//            ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), (view, windowInsets) -> {
//                Insets statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
//                Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
//
//                // Combine paddings for status + navigation bars
//                int left = isLandscape ? navBarInsets.left : Math.max(statusBarInsets.left, navBarInsets.left);
//                int top = statusBarInsets.top;
//                int right = isLandscape ? navBarInsets.right : Math.max(statusBarInsets.right, navBarInsets.right);
//                int bottom = navBarInsets.bottom;
//
//                view.setPadding(left, top, right, bottom);
//                return windowInsets;
//            });
//        } else {
//            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blueColorNew));
//            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.grayColor));
//        }
//    }

    public void inAppUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).setAllowAssetPackDeletion(true).build());
            } else {
                Functions.showToast(getContext(), getString(R.string.new_version_already_installed), FancyToast.SUCCESS);
                finishActivityIfNecessary();
            }
        });
    }

    protected void finishActivityIfNecessary() {
        // Override this method in the activity that extends BaseActivity
        // if you need to finish the activity when the update is already installed
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            LocalBroadcastManager.getInstance(this).registerReceiver(movetoRatingReceiver, new IntentFilter("move_to_rating"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            if (movetoRatingReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(movetoRatingReceiver);
            }
        }

    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    BroadcastReceiver movetoRatingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //get push data
                String notType = bundle.getString("type", "");
                String bookingId = bundle.getString("booking_id", "");
                String clubId = bundle.getString("club_id", "");
                String bookingType = bundle.getString("booking_type", "");
                String isRated = bundle.getString("is_rated", "");
                if (notType.equalsIgnoreCase(Constants.kBookingCompleteNotification) && !bookingId.isEmpty()) {
                    if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("RatingPagerDialogFragment");
                        if (prev != null) {
                            fragmentTransaction.remove(prev);
                        }
                        fragmentTransaction.addToBackStack(null);
                        OleRatingPagerDialogFragment dialogFragment = new OleRatingPagerDialogFragment(bookingId);
                        dialogFragment.show(fragmentTransaction, "RatingPagerDialogFragment");
                    } else {
                        gotoEmpRate(bookingId, clubId, isRated);
                    }
                }
            }
        }
    };

    protected void showRatingDialog(String gameId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("RatingPagerDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        RatingPagerDialogFragment dialogFragment = new RatingPagerDialogFragment(gameId);
        dialogFragment.show(fragmentTransaction, "RatingPagerDialogFragment");
    }


    protected void showBestPlayerDialog(String gameId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("BestPlayerDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        BestPlayerDialogFragment dialogFragment = new BestPlayerDialogFragment(gameId);
        dialogFragment.setDialogCallback((df, isSubmitted) -> {
            df.dismiss();
            if (isSubmitted) {
                showRatingDialog(gameId);
            }

        });
        dialogFragment.show(fragmentTransaction, "BestPlayerDialogFragment");
    }

//    protected void createZegoEngine() {
//        ExpressManager.getInstance().createEngine(getApplication(), AppCenter.appID);
//        ExpressManager.getInstance().setExpressHandler(new ExpressManager.ExpressManagerHandler() {
//            @Override
//            public void onRoomUserUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList) {
//                if (updateType == ZegoUpdateType.ADD) {
//                    for (int i = 0; i < userList.size(); i++) {
//                        ZegoUser user = userList.get(i);
//                        ExpressManager.getInstance().setRemoteVideoView(user.userID, null);
//                    }
//                }
//            }
//
//            @Override
//            public void onRoomUserDeviceUpdate(ZegoDeviceUpdateType updateType, String userID, String roomID) {
//                Log.d("MainActivity",
//                        "onRoomUserDeviceUpdate() called with: updateType = [" + updateType + "], userID = [" + userID
//                                + "], roomID = [" + roomID + "]");
//
//            }
//
//            @Override
//            public void onRoomTokenWillExpire(String roomID, int remainTimeInSecond) {
//
//            }
//        });
//    }

//    protected void joinRoom(String roomID, String userName, String userId,String zegoToken) {
////        KProgressHUD hud = Functions.showLoader(getContext());
//        ZegoUser user = new ZegoUser(userId, userName);
//        //String token = ExpressManager.generateToken(userId, AppCenter.appID, AppCenter.serverSecret);
//        int mediaOptions = ZegoMediaOptions.autoPlayAudio | ZegoMediaOptions.publishLocalAudio;
//        ExpressManager.getInstance().joinRoom(roomID, user, zegoToken, mediaOptions, new IZegoRoomLoginCallback() {
//            @Override
//            public void onRoomLoginResult(int errorCode, JSONObject jsonObject) {
////                Functions.hideLoader(hud);
//                if (errorCode == 0) {
//                    ExpressManager.getInstance().enableCamera(false);
//                } else {
////                    Toast.makeText(getApplication(), "join room failed,errorCode :" + errorCode, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }

    protected void playSoundFromAssets(String fileName) {
        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            final MediaPlayer mp = new MediaPlayer();

            /*  For API 24+, we can just use the AssetFileDescriptor to play the sound. However,
                for API 23-, we can't use the AssetFileDescriptor directly but can retrieve a
                FileDescriptor from it that points to the beginning of our assets. The offset
                and length from the AssetFileDescriptor serve for the FileDescriptor as well.
             */

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mp.setDataSource(afd);
            } else {
                FileDescriptor fd = afd.getFileDescriptor();
                mp.setDataSource(fd, afd.getStartOffset(), afd.getLength());

                // One might think that mp.setDataSource(fd) would play the sound file we want, but
                // it actually plays all sound files one after another. It seems that fd is a
                // FileDescriptor that points to the beginning of the assets.
            }
            afd.close();
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showDateRangeFilter(String fromDate, String toDate, OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback callback) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("EmpReviewFilterDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        OleDateRangeFilterDialogFragment dialogFragment = new OleDateRangeFilterDialogFragment(fromDate, toDate);
        dialogFragment.setFragmentCallback(callback);
        dialogFragment.show(fragmentTransaction, "EmpReviewFilterDialogFragment");
    }

    protected void gotoEmpRate(String bookingId, String clubId, String isRated) {
        Intent rateIntent = new Intent(getContext(), OleEmployeeRateActivity.class);
        rateIntent.putExtra("booking_id", bookingId);
        rateIntent.putExtra("club_id", clubId);
        rateIntent.putExtra("is_rated", isRated);
        startActivity(rateIntent);
    }

    protected int getRandomX(int viewWidth, float subVuW) {
        Random random = new Random();
        return random.nextInt(viewWidth - (int) subVuW);
    }

    protected int getRandomY(int viewHeight, float subVuH) {
        Random random = new Random();
        return random.nextInt(viewHeight - (int) subVuH);
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

    protected void setBackground(ImageView imageView) {
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kRefereeType)) {
            imageView.setImageResource(R.drawable.referee_bg);
        } else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            imageView.setImageResource(R.drawable.owner_bg);
        } else {
            imageView.setImageResource(R.drawable.player_bg);
        }
    }

    public Activity getContext() {
        return this;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    protected void makeStatusbarTransperant() {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable bitmapDrawable) {
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    protected Bitmap getBitmapFromView(View view, Drawable drawable) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        if (drawable != null) {
            Bitmap bitmap = getBitmap(drawable);
            canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), canvas.getHeight() - bitmap.getHeight(), new Paint());
        } else {
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    protected Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    protected Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap) throws IOException {

        final ContentValues values = new ContentValues();
        //values.put(MediaStore.MediaColumns.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".jpg");
        //values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");

//                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream))
//                    throw new IOException("Failed to save bitmap.");

                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
                    throw new IOException("Failed to save bitmap.");
            }

            return uri;
        } catch (IOException e) {

            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
    }

    public String saveBitmap(Bitmap bitmap) {
        File mediaStorageDir = getCacheDir();
        File myFilePath = new File(mediaStorageDir.getAbsolutePath() + "/camera");
        if (!myFilePath.exists())
            myFilePath.mkdir();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File fileName = new File(myFilePath.getPath() + File.separator + "IMG_" + timeStamp + ".png");

        try {
            FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            return fileName.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void makeCall(String phone) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("Call " + phone)
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            startActivity(callIntent);
                        }
                    }
                }).show();
    }

    protected DialogFragment gotoLevelDialog(OlePlayerLevel olePlayerLevel) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("LevelCompleteDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleLevelCompleteDialogFragment dialogFragment = new OleLevelCompleteDialogFragment(olePlayerLevel);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "LevelCompleteDialogFragment");
        return dialogFragment;
    }

    protected void gotoRestrictDialog(String popupId, String title, String msg) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("RestrictBookingPlayerDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleRestrictBookingPlayerDialogFragment dialogFragment = new OleRestrictBookingPlayerDialogFragment(popupId, title, msg);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "RestrictBookingPlayerDialogFragment");
    }

    protected void gotoLoyaltyCardDialog(Club club, String discountValue, String targetBookings, String expiryDate, String playerBookings, String remainingBookings, String popupId, String popupType, OleLoyaltyCardDialogFragment.LoyaltyCardDialogCallback callback) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("LoyaltyCardDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleLoyaltyCardDialogFragment dialogFragment = new OleLoyaltyCardDialogFragment(club, discountValue, targetBookings, expiryDate, playerBookings, remainingBookings, popupId, popupType);
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogCallback(callback);
        dialogFragment.show(getSupportFragmentManager(), "LoyaltyCardDialogFragment");
    }

    protected void gotoWinFootballMatchDialog(String popupId, String msg, OleMatchResults matchResult, OleWinMatchDialogFragment.WinMatchDialogFragmentCallback callback) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("WinMatchDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleWinMatchDialogFragment dialogFragment = new OleWinMatchDialogFragment(popupId, msg, matchResult);
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogCallback(callback);
        dialogFragment.show(getSupportFragmentManager(), "WinMatchDialogFragment");
    }

    protected void gotoWinPadelMatchDialog(String popupId, String msg, OlePadelMatchResults matchResult, OleWinMatchDialogFragment.WinMatchDialogFragmentCallback callback) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("PadelWinMatchDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OlePadelWinMatchDialogFragment dialogFragment = new OlePadelWinMatchDialogFragment(popupId, msg, matchResult);
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogCallback(callback);
        dialogFragment.show(getSupportFragmentManager(), "PadelWinMatchDialogFragment");
    }

    public void getCountriesAPI(CountriesCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getCountries(Functions.getAppLang(getContext()));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<OleCountry> countries = new ArrayList<>();
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleCountry oleCountry = gson.fromJson(arr.get(i).toString(), OleCountry.class);
                                countries.add(oleCountry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.getCountries(countries);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.getCountries(new ArrayList<>());
            }
        });
    }

    public void getBibsAPI(BibsCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNode.getBibs();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<Shirt> shirts = new ArrayList<>();
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Shirt shirt = gson.fromJson(arr.get(i).toString(), Shirt.class);
                                shirts.add(shirt);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.getBibs(shirts);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.getBibs(new ArrayList<>());
            }
        });
    }


    public void gameAvailRequestAPI(String bookingId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.gameAvailabilityRequest(Functions.getAppLang(getContext()), bookingId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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

    public void getFieldDataAPI(FieldDataCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFieldData(Functions.getAppLang(getContext()));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                OleFieldData oleFieldData = null;
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            oleFieldData = gson.fromJson(obj.toString(), OleFieldData.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.getFieldData(oleFieldData);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.getFieldData(null);
            }
        });
    }

    public void getWalletDataAPI(String clubId, WalletDataCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubPaymentMethod(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String value = "", paymentMethod = "", currency = "", shopPaymentMethod = "";
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            value = object.getJSONObject(Constants.kData).getString("my_credit");
                            paymentMethod = object.getJSONObject(Constants.kData).getString("club_payment_method"); // cash/card/both
                            currency = object.getJSONObject(Constants.kData).getString("currency");
                            shopPaymentMethod = object.getJSONObject(Constants.kData).getString("shop_payment_method"); // cash/card/both
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.getWalletData(value, paymentMethod, currency, shopPaymentMethod);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.getWalletData("", "", "", "");
            }
        });
    }

    public void addRemoveFavAPI(String clubId, String status, String type, FavCallback callback) {
        addRemoveFavAPI(clubId, status, type, Functions.getPrefValue(this, Constants.kAppModule), callback);
    }

    public void addRemoveFavAPI(String clubId, String status, String type, String module, FavCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addRemoveFav(Functions.getAppLang(this), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, status, type, module);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Boolean success = false;
                String msg = getString(R.string.error_occured);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            success = true;
                            msg = object.getString(Constants.kMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = e.getLocalizedMessage();
                    }
                }
                callback.addRemoveFav(success, msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.addRemoveFav(false, t.getLocalizedMessage());
            }
        });
    }

    public void addToWishlistAPI(String prodId, FavCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addWishlist(Functions.getAppLang(this), Functions.getPrefValue(getContext(), Constants.kUserID), prodId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Boolean success = false;
                String msg = getString(R.string.error_occured);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            success = true;
                            msg = object.getString(Constants.kMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = e.getLocalizedMessage();
                    }
                }
                callback.addRemoveFav(success, msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.addRemoveFav(false, t.getLocalizedMessage());
            }
        });
    }

    public void removeFromWishlistAPI(String prodId, FavCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removeWishlist(Functions.getAppLang(this), Functions.getPrefValue(getContext(), Constants.kUserID), prodId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Boolean success = false;
                String msg = getString(R.string.error_occured);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            success = true;
                            msg = object.getString(Constants.kMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = e.getLocalizedMessage();
                    }
                }
                callback.addRemoveFav(success, msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.addRemoveFav(false, t.getLocalizedMessage());
            }
        });
    }

    protected void sendFcmTokenApi(String token) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
        //yeh server py unique id bhej rhi haii
        Log.d("uniqueIDDDDDDD", uniqueID);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendFcmToken(Functions.getAppLang(getContext()), userId, uniqueID, "android", token, BuildConfig.VERSION_NAME);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    protected void sendAppLangApi() {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendAppLang(userId, Functions.getAppLang(getContext()));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    protected void getUnreadNotificationAPI(UnreadCountCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.unreadNotifCount(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int count = 0;
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            String c = obj.getString("total_unread");
                            count = Integer.parseInt(c);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.unreadNotificationCount(count);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.unreadNotificationCount(0);
            }
        });
    }

    protected void getFacilityAPI(FacilityCallback callback) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFacilities(Functions.getAppLang(getContext()));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<OleClubFacility> facilities = new ArrayList<>();
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleClubFacility facility = gson.fromJson(arr.get(i).toString(), OleClubFacility.class);
                                if (facility.getCurrency() == null || facility.getCurrency().equalsIgnoreCase("")) {
                                    facility.setCurrency(Functions.getPrefValue(getContext(), Constants.kCurrency));
                                }
                                facilities.add(facility);
                            }
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.facilities(facilities);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.facilities(new ArrayList<>());
            }
        });
    }

    public void openWebDialog(String url, OlePaymentWebDialogFragment.PaymentWebCallback callback) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("PaymentWebDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OlePaymentWebDialogFragment dialogFragment = new OlePaymentWebDialogFragment(url);
        dialogFragment.setCancelable(false);
        dialogFragment.setPaymentCallback(callback);
        dialogFragment.show(fragmentTransaction, "PaymentWebDialogFragment");
    }

    public void openPaymentDialog(String price, String currency, String date, String bookingId, String totalPlayers, boolean isCashHide, boolean isWalletHide, String clubId, OlePaymentDialogFragment.PaymentDialogCallback callback) {
        openPaymentDialog(price, currency, date, bookingId, totalPlayers, isCashHide, isWalletHide, "1", clubId, callback);
    }

    public void openPaymentDialog(String price, String currency, String date, String bookingId, String totalPlayers, boolean isCashHide, boolean isWalletHide, String isFromMatch, String clubId, OlePaymentDialogFragment.PaymentDialogCallback callback) {
        openPaymentDialog(price, currency, date, bookingId, totalPlayers, isCashHide, isWalletHide, isFromMatch, "", clubId, callback);
    }

    public void openPaymentDialog(String price, String currency, String date, String bookingId, String totalPlayers, boolean isCashHide, boolean isWalletHide, String isFromMatch, String note, String clubId, OlePaymentDialogFragment.PaymentDialogCallback callback) {
        openPaymentDialog(price, currency, date, bookingId, totalPlayers, isCashHide, isWalletHide, isFromMatch, note, clubId, "", callback);
    }

    public void openPaymentDialog(String price, String currency, String date, String bookingId, String totalPlayers, boolean isCashHide, boolean isWalletHide, String isFromMatch, String note, String clubId, String payFullAmount, OlePaymentDialogFragment.PaymentDialogCallback callback) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("PaymentDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OlePaymentDialogFragment dialogFragment = new OlePaymentDialogFragment(price, currency, date, bookingId, totalPlayers, isCashHide, isWalletHide, isFromMatch, clubId, payFullAmount);
        dialogFragment.setDialogCallback(callback);
        dialogFragment.setNote(note);
        dialogFragment.show(fragmentTransaction, "PaymentDialogFragment");
    }

    public void makePayment(String bookingId, String paymentMethod, String amount, String currency, PaymentCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
        this.bookingId = bookingId;
        paymentOrderRef = "";

        createOrderAPI(bookingId, amount, currency, paymentMethod);
    }

    private void createOrderAPI(String bookingId, String price, String currency, String paymentMethod) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.createOrderRequest(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, price, currency);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            paymentOrderRef = obj.getString("reference");
//                            PaymentClient paymentClient = new PaymentClient(getContext(), "fbc005bbf76d48b7903e8f"); // test service

                            PaymentClient paymentClient = new PaymentClient(getContext(), "bb597fa0d7e549c0a11f28");
                            if (paymentMethod.equalsIgnoreCase("samsung")) {
                                Gson gson = new Gson();
                                Order order = gson.fromJson(obj.toString(), Order.class);
                                paymentClient.launchSamsungPay(order, "AL TAHADI PLAYGROUNDS", new SamsungPayResponse() {
                                    @Override
                                    public void onSuccess() {
                                        paymentCallback.paymentStatus(true, paymentOrderRef);
                                    }

                                    @Override
                                    public void onFailure(@NotNull String s) {
                                        paymentCallback.paymentStatus(false, paymentOrderRef);
                                    }
                                });
                            } else {
                                String paymentAuthorizationUrl = obj.getJSONObject("_links").getJSONObject("payment-authorization").getString("href");
                                String code = obj.getJSONObject("_links").getJSONObject("payment").getString("href").split("=")[1];
                                paymentClient.launchCardPayment(new CardPaymentRequest.Builder().gatewayUrl(paymentAuthorizationUrl).code(code).build(), 101);
                            }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                CardPaymentData cardPaymentData = CardPaymentData.getFromIntent(data);
                paymentCallback.paymentStatus(cardPaymentData.getCode() == CardPaymentData.STATUS_PAYMENT_AUTHORIZED || cardPaymentData.getCode() == CardPaymentData.STATUS_PAYMENT_CAPTURED, paymentOrderRef);
            }
        }
    }

    public interface CountriesCallback {
        void getCountries(List<OleCountry> countries);
    }

    public interface FieldDataCallback {
        void getFieldData(OleFieldData oleFieldData);
    }

    public interface WalletDataCallback {
        void getWalletData(String amount, String paymentMethod, String currency, String shopPaymentMethod);
    }

    public interface FavCallback {
        void addRemoveFav(boolean success, String msg);
    }

    public interface UnreadCountCallback {
        void unreadNotificationCount(int count);
    }

    public interface FacilityCallback {
        void facilities(List<OleClubFacility> facilities);
    }

    public interface PaymentCallback {
        void paymentStatus(boolean status, String orderRef);
    }

    public interface BibsCallback {
        void getBibs(List<Shirt> shirts);
    }
}
