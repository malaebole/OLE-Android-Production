package ae.oleapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    private FragmentCameraBinding binding;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_PATH = "/camera";
    private String filePath;
    public static int degrees = 0;
    public ImageView camera;
    public ImageView gallery, cameraToggle;
    OrientationEventListener mOrientationEventListener;
    private int mOrientation =  -1;
    private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;
    private int onClickOrientation =  1;
    protected List<Camera.Size> mPreviewSizeList;
    protected List<Camera.Size> mPictureSizeList;
    private int mWidth =  0;
    private int mHeight =  0;
    private CameraFragmentCallback fragmentCallback;
    private boolean isFlashOn = false;

    public CameraFragment() {
        // Required empty public constructor
    }

    public void setFragmentCallback(CameraFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        mSurfaceView = view.findViewById(R.id.camera_preview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        camera = view.findViewById(R.id.camera);
        ImageView imgFlash = view.findViewById(R.id.camera_flash);
        ImageView imgClose = view.findViewById(R.id.close);

        imgFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.Parameters params = mCamera.getParameters();
                if (isFlashOn) {
                    isFlashOn = false;
                    imgFlash.setImageResource(R.drawable.cam_flash_offl);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                else {
                    isFlashOn = true;
                    imgFlash.setImageResource(R.drawable.cam_flashl);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                }
                mCamera.setParameters(params);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback.closeCamera();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onClickOrientation = mOrientation;
                    mCamera.takePicture(null, null, mPicture);
                }catch (Exception e){
                    Log.e("Tag","Error taking picture : " + e.getMessage());
                }
            }
        });

        return view;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(Activity context){
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance

            setCameraDisplayOrientation(context, Camera.CameraInfo.CAMERA_FACING_BACK,c);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)

            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private final Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.e("TAG","Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                /*fos.write(data);
                fos.close();*/
                filePath = pictureFile.getPath();

                /*Bitmap bitmap = ExifUtil.rotateBitmap(6, BitmapFactory.decodeFile(filePath));

                FileOutputStream fos1 = new FileOutputStream(pictureFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG,85,fos1);

                filePath = pictureFile.getPath();*/


                try {

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = false;
                    opts.inPreferredConfig = Bitmap.Config.RGB_565;
                    opts.inDither = true;

                    Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length,opts);

                    ExifInterface exif = new ExifInterface(pictureFile.toString());

                    Log.d("TAG", "EXIF value >>" + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                        realImage = rotate(realImage, 90);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                        realImage = rotate(realImage, 270);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                        realImage = rotate(realImage, 180);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")) {
                        switch (onClickOrientation){
                            case ORIENTATION_PORTRAIT_NORMAL:
                                realImage = rotate(realImage, 90);
                                break;
                            case ORIENTATION_PORTRAIT_INVERTED:
                                realImage = rotate(realImage, -90);
                                break;
                            case ORIENTATION_LANDSCAPE_NORMAL:
                                break;
                            case ORIENTATION_LANDSCAPE_INVERTED:
                                realImage = rotate(realImage, 180);
                                break;
                        }
                    }
                    boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                }catch (Exception e){

                    e.printStackTrace();
                }

                fos.close();

                fragmentCallback.takePicture(filePath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Rotate the bitmap
     * @param bitmap
     * @param degrees
     * @return
     */
    Bitmap rotate(Bitmap bitmap, int degrees){
        try {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return oriented;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

//    /** Create a file Uri for saving an image or video */
//    private static Uri getOutputMediaFileUri(int type){
//        return Uri.fromFile(getOutputMediaFile(type));
//    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir =  getActivity().getCacheDir();
        File myFilePath = new File(mediaStorageDir.getAbsolutePath()
                + IMAGE_PATH);
        if (!myFilePath.exists())
            myFilePath.mkdir();

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile = new File(myFilePath.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        mCamera = getCameraInstance(getActivity());
        try {
            mCamera.setPreviewDisplay(holder);
            Camera.Parameters cameraParams = mCamera.getParameters();
            mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
            mPictureSizeList = cameraParams.getSupportedPictureSizes();

            List<String> focusModes = cameraParams.getSupportedFocusModes();
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
                cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            mCamera.setParameters(cameraParams);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        mWidth = w;
        mHeight = h;
        refreshCamera(mWidth, mHeight);
    }

    public void refreshCamera(int w, int h){
        if (mSurfaceHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            e.printStackTrace();
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Camera.Size previewSize = determinePreviewSize(w, h);
        Camera.Size pictureSize = determinePictureSize(previewSize);

        Camera.Parameters cameraParams = mCamera.getParameters();
        cameraParams.setPreviewSize(previewSize.width, previewSize.height);
        cameraParams.setPictureSize(pictureSize.width, pictureSize.height);
        mCamera.setParameters(cameraParams);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected Camera.Size determinePreviewSize(int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
//        if (portrait) {
//            reqPreviewWidth = reqHeight;
//            reqPreviewHeight = reqWidth;
//        } else {
//            reqPreviewWidth = reqWidth;
//            reqPreviewHeight = reqHeight;
//        }

        reqPreviewWidth = reqHeight;
        reqPreviewHeight = reqWidth;

        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    protected Camera.Size determinePictureSize(Camera.Size previewSize) {
        Camera.Size retSize = null;
        for (Camera.Size size : mPictureSizeList) {
            if (size.equals(previewSize)) {
                return size;
            }
        }

        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : mPictureSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(getActivity()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                        }
                    }
                    else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                        }
                    }
                    else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                        }
                    }
                    else { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        }
                    }
                }
            };
        }

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationEventListener.disable();
    }

    public interface CameraFragmentCallback {
        void takePicture(String filePath);
        void closeCamera();
    }
}