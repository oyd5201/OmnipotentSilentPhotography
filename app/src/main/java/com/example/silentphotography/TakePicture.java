package com.example.silentphotography;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TakePicture {
    private static final String TAG = "TestCamera2";
    private SurfaceView mySurfaceView;
    private SurfaceHolder myHolder;
    private Camera myCamera;
    private Context mContext;
    private SurfaceView mSurfaceView;
    private Camera.Parameters mParameters;
    public TakePicture(Context context, SurfaceView surfaceView){
        this.mContext = context;
        this.mSurfaceView = surfaceView;
        initSurface();
        initCamera();
    }

    // 初始化surface
    @SuppressWarnings("deprecation")
    private void initSurface() {
        // 初始化surfaceview

        // 初始化surfaceholder
        myHolder = mSurfaceView.getHolder();

    }

    // 初始化摄像头
    private void initCamera() {

        // 如果存在摄像头
        if (checkCameraHardware(mContext.getApplicationContext())) {
            // 获取摄像头（首选前置，无前置选后置）
            if (openFacingFrontCamera()) {
                Log.i(TAG, "openCameraSuccess");
                // 进行对焦
                autoFocus();
            } else {
                Log.i(TAG, "openCameraFailed");
            }

        }
    }

    // 对焦并拍照
    private void autoFocus() {

        try {
            // 因为开启摄像头需要时间，这里让线程睡两秒
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startPreview();
        // 自动对焦
        myCamera.autoFocus(myAutoFocus);

        // 对焦后拍照

        myCamera.takePicture(null, null, myPicCallback);
    }

    private void startPreview() {

        setCameraParameters();
        myCamera.startPreview();
    }

    /**
     * 设置Camera参数
     * 设置预览界面的宽高，图片保存的宽、高
     */
    private void setCameraParameters() {
        if (myCamera != null) {
            Log.d(TAG, "setCameraParameters >> begin. mCamera != null");
            if (mParameters == null) {
                mParameters = myCamera.getParameters();
            }

            int PreviewWidth = 0;
            int PreviewHeight = 0;
            List<Camera.Size> sizeList = mParameters.getSupportedPreviewSizes();
            if (sizeList.size() > 1) {
                Iterator<Camera.Size> itor = sizeList.iterator();
                while (itor.hasNext()) {
                    Camera.Size cur = itor.next();
                    if (cur.width >= PreviewWidth
                            && cur.height >= PreviewHeight) {
                        PreviewWidth = cur.width;
                        PreviewHeight = cur.height;
                        break;
                    }
                }
            }else if (sizeList.size()==1){
                Camera.Size size = sizeList.get(0);
                PreviewWidth = size.width;
                PreviewHeight = size.height;
            }
           mParameters.setPreviewSize(PreviewWidth, PreviewHeight); //获得摄像区域的大小
           mParameters.setPictureSize(PreviewWidth, PreviewHeight);//设置拍出来的屏幕大小


            try {
                myCamera.setParameters(mParameters);
            }catch (Exception e) {
                Camera.Parameters parameters = myCamera.getParameters();// 得到摄像头的参数
                myCamera.setParameters(parameters);

            }

        } else {
            Log.e(TAG, "setCameraParameters >> mCamera == null!!");
        }
    }

    // 判断是否存在摄像头
    private boolean checkCameraHardware(Context context) {

        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // 设备存在摄像头
            return true;
        } else {
            // 设备不存在摄像头
            return false;
        }

    }

    // 得到后置摄像头
    private boolean openFacingFrontCamera() {

        // 尝试开启前置摄像头
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    Log.i(TAG, "tryToOpenCamera");
                    myCamera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        // 如果开启前置失败（无前置）则开启后置
        if (myCamera == null) {
            for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    try {
                        myCamera = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                        return false;
                    }
                }
            }
        }

        try {
            // 这里的myCamera为已经初始化的Camera对象
            myCamera.setPreviewDisplay(myHolder);
        } catch (IOException e) {
            e.printStackTrace();
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }

        myCamera.startPreview();

        return true;
    }

    // 自动对焦回调函数(空实现)
    private Camera.AutoFocusCallback myAutoFocus = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };

    // 拍照成功回调函数
    private Camera.PictureCallback myPicCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 完成拍照后关闭Activity


            // 将得到的照片进行270°旋转，使其竖直
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.preRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());

            // 创建并保存图片文件
            File pictureFile = new File(getDiskCachePath(mContext), "myPicture.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.close();
            } catch (Exception error) {
                Toast.makeText(mContext, "拍照失败", Toast.LENGTH_SHORT)
                        .show();
                ;
                Log.i(TAG, "保存照片失败" + error.toString());
                error.printStackTrace();
                myCamera.stopPreview();
                myCamera.release();
                myCamera = null;
            }

            Log.i(TAG, "获取照片成功");
            Toast.makeText(mContext, "获取照片成功", Toast.LENGTH_SHORT)
                    .show();
            ;
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    };

    /**
     * 获取cache路径
     *
     * @param context
     * @return
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }


}
