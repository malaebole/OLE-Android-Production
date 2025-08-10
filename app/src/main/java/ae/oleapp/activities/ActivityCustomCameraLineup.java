package ae.oleapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.io.IOException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityCustomCameraLineupBinding;
import ae.oleapp.fragments.CameraFragment;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityCustomCameraLineup extends BaseActivity implements View.OnClickListener {

    private ActivityCustomCameraLineupBinding binding;
    private final int imgWidth = 500;
    private final int imgHeight = 500;
    private boolean isGallery = false;
    private String galleryFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomCameraLineupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isGallery = bundle.getBoolean("is_gallery", false);
            galleryFilePath = bundle.getString("file_path", "");
        }

        if (isGallery) {
            binding.fragmentCameraPreview.setVisibility(View.GONE);
            binding.galleryVu.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(new File(galleryFilePath)).into(binding.galleryPhoto);
            binding.galleryPhoto.setDrawingCacheEnabled(true);
//            ImageMatrixTouchHandler matrixTouchHandler = new ImageMatrixTouchHandler(binding.galleryPhoto.getContext());
//            binding.galleryPhoto.setOnTouchListener(matrixTouchHandler);
        }
        else {
            binding.fragmentCameraPreview.setVisibility(View.VISIBLE);
            binding.galleryVu.setVisibility(View.GONE);
            CameraFragment cameraFragment = new CameraFragment();
            cameraFragment.setFragmentCallback(callback);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_camera_preview, cameraFragment).commit();
        }

        binding.btnDone.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnDone) {
            Bitmap bitmap = binding.galleryPhoto.getDrawingCache();
            String imgFile = saveBitmap(bitmap);
            removeBG(new File(imgFile));
        }
        else if (v == binding.btnClose) {
            finish();
        }
    }

    CameraFragment.CameraFragmentCallback callback = new CameraFragment.CameraFragmentCallback() {
        @Override
        public void takePicture(String filePath) {
            File imgFile = new File(filePath);
            removeBG(imgFile);
        }

        @Override
        public void closeCamera() {
            finish();
        }
    };

    private void removeBG(File imgFile) {
        KProgressHUD hud = Functions.showLoader(getContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), imgFile);

                RequestBody fileRequestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image_file", "image.jpg", fileBody)
                        .build();

                Request request = new Request.Builder()
                        .header("x-api-key", "f26e81c27697a19c5f270f8ca33da95dec9a2a80")
                        .url("https://sdk.photoroom.com/v1/segment")
                        .post(fileRequestBody)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                    maskImage(bm, hud);
                }
                catch (IOException e) {
                    Functions.hideLoader(hud);
                    e.printStackTrace();
                }
            }
        });
    }

    private void maskImage(Bitmap original, KProgressHUD hud) {
        original = Bitmap.createScaledBitmap(original, imgWidth, imgHeight, true);
        Bitmap mask = BitmapFactory.decodeResource(getResources(), R.drawable.mask_imagel);
        mask = Bitmap.createScaledBitmap(mask, imgWidth, imgHeight, true);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(original, 0f, 0f, null);
        canvas.drawBitmap(mask, 0f, 0f, paint);
        paint.setXfermode(null);
        result = trimBitmap(result);
        String filePath = saveBitmap(result);
        Functions.hideLoader(hud);

        Intent intent = new Intent();
        intent.putExtra("file_path", filePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    private Bitmap trimBitmap(Bitmap bmp) {
        int imgHeight = bmp.getHeight();
        int imgWidth  = bmp.getWidth();

        //TRIM WIDTH - LEFT
        int startWidth = 0;
        for(int x = 0; x < imgWidth; x++) {
            if (startWidth == 0) {
                for (int y = 0; y < imgHeight; y++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        startWidth = x;
                        break;
                    }
                }
            } else break;
        }

        //TRIM WIDTH - RIGHT
        int endWidth  = 0;
        for(int x = imgWidth - 1; x >= 0; x--) {
            if (endWidth == 0) {
                for (int y = 0; y < imgHeight; y++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        endWidth = x;
                        break;
                    }
                }
            } else break;
        }

        //TRIM HEIGHT - TOP
        int startHeight = 0;
        for(int y = 0; y < imgHeight; y++) {
            if (startHeight == 0) {
                for (int x = 0; x < imgWidth; x++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        startHeight = y;
                        break;
                    }
                }
            } else break;
        }

        //TRIM HEIGHT - BOTTOM
        int endHeight = 0;
        for(int y = imgHeight - 1; y >= 0; y--) {
            if (endHeight == 0 ) {
                for (int x = 0; x < imgWidth; x++) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        endHeight = y;
                        break;
                    }
                }
            } else break;
        }

        return Bitmap.createBitmap(
                bmp,
                startWidth,
                startHeight,
                endWidth - startWidth,
                endHeight - startHeight
        );

    }
}