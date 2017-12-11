package com.android.agingtest.test;

import android.app.ActionBar;
import android.app.Activity;
import com.android.agingtest.R;
import com.android.agingtest.ReportActivity;
import com.android.agingtest.TestUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FrontTakingPictureActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "FrontTakingPicture";
    private int[] alltestindex = null;
    private Camera mCamera;
    private String currentPicturePath;
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] paramAnonymousArrayOfByte, Camera paramAnonymousCamera) {
            mCamera.startPreview();
        }
    };
    String key;
    private int key_index = -1;
    private int mBackCameraId = -1;
    private int mCameraId;
    private int mDisplayOrientation;
    private int mDisplayRotation;
    private int mFrontCameraId = -1;
    private long mFrontTakingPictureTime;

    private boolean mIsPreviewing;
    private PowerManager.WakeLock mLock;
    private PowerManager mPowerManager;
    private SharedPreferences mSharedPreferences;
    private long mStartTime;
    private Button mStopBt;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private TextView mTestTimeTv;
    private boolean safeToTakePicture;
    private TextView mTextTitle;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            switch (paramAnonymousMessage.what) {
                case 0:
                    long l = System.currentTimeMillis() - mStartTime;
                    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
                    mTestTimeTv.setText(localSimpleDateFormat.format(new Date(l)));
                    if (isTestOver()) {
                        FrontTakingPictureActivity.this.stopTest(true);
                        return;
                    }
                    if (mCamera != null) {
                        mCamera.takePicture(null, null, jpegCallback);
                    }

                    mHandler.sendEmptyMessageDelayed(0, 1000L);
            }
        }
    };

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "onCreate()...");
        setContentView(R.layout.activity_taking_picture);
        initValues();
        initViews();
        startTest();
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mIsPreviewing = false;
        }
    }

    private void getCameraId() {
        int i = Camera.getNumberOfCameras();
        Camera.CameraInfo[] arrayOfCameraInfo = new Camera.CameraInfo[i];
        for (int j = 0; j < i; j++) {
            arrayOfCameraInfo[j] = new Camera.CameraInfo();
            Camera.getCameraInfo(j, arrayOfCameraInfo[j]);
        }

        for (int cameraIndex = 0; cameraIndex < arrayOfCameraInfo.length; cameraIndex++) {
            if ((mBackCameraId == -1) && (arrayOfCameraInfo[cameraIndex].facing == CameraInfo.CAMERA_FACING_BACK))
                mBackCameraId = cameraIndex;
            else if ((mFrontCameraId == -1) && (arrayOfCameraInfo[cameraIndex].facing == CameraInfo.CAMERA_FACING_FRONT))
                mFrontCameraId = cameraIndex;
        }
        Log.d(TAG, "getCameraId=>back: " + mBackCameraId + " front: " + mFrontCameraId);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> paramList, double paramDouble) {
        return null;
    }

    private void initValues() {
        key_index = getIntent().getIntExtra("current_test_index", -1);
        alltestindex = getIntent().getIntArrayExtra("all_index");
        Log.e(TAG, "key_index->" + key_index + ",alltestindex[key_index]->" + TestUtils.ALLKEYS[alltestindex[key_index]]);
        key = "front_camera";
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPowerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        mLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "front_taking_picture_test");
        mFrontTakingPictureTime = (60000L * mSharedPreferences.getInt("front_cameratime", getResources().getInteger(R.integer.default_front_taking_picture_time)));
        Log.d(TAG, "mFrontTakingPictureTime->" + mFrontTakingPictureTime);
    }

    private void initViews() {
        mTestTimeTv = ((TextView) findViewById(R.id.test_time));
        mTextTitle = ((TextView) findViewById(R.id.test_title));
        mTextTitle.setText(getString(R.string.front_taking_picture_title));
        mStopBt = ((Button) findViewById(R.id.stop_test));
        mSurfaceView = ((SurfaceView) findViewById(R.id.camera_surface));
        mTestTimeTv.setText(R.string.default_time_string);
        mStopBt.setOnClickListener(this);
    }

    private boolean isTestOver() {
        return System.currentTimeMillis() - mStartTime >= mFrontTakingPictureTime;
    }

    private void onTakePictures() {
        currentPicturePath = (System.currentTimeMillis() + "test.jpg");
        Log.d(TAG, "onTakePictures");
        if ((mCamera != null) && (mIsPreviewing)) {
            Log.d(TAG, "onTakePictures autoFocus");
            mCamera.takePicture(null, null, null);
        }
    }

    private void setCameraParameters() {
        Camera.Parameters localParameters = mCamera.getParameters();
        localParameters.setPictureSize(640, 480);
        localParameters.setFlashMode(Camera.Parameters.WHITE_BALANCE_AUTO);
        Camera.Size localSize1 = localParameters.getPictureSize();
        Camera.Size localSize2 = getOptimalPreviewSize(localParameters.getSupportedPreviewSizes(), localSize1.width / localSize1.height);
        if (localSize2 != null)
            localParameters.setPreviewSize(localSize2.width, localSize2.height);
        mCamera.setParameters(localParameters);
    }

    private void setPreviewDisplay(SurfaceHolder paramSurfaceHolder, int paramInt) {
        try {
            mCamera.setPreviewDisplay(paramSurfaceHolder);
            mDisplayRotation = getDisplayRotation(this);
            mDisplayOrientation = getDisplayOrientation(mDisplayRotation, paramInt);
            mCamera.setDisplayOrientation(mDisplayOrientation);
        } catch (IOException localIOException) {
            Log.d(TAG, "setPreviewDisplay=>error: ", localIOException);
            closeCamera();
        }
    }

    private void startNext() {
        closeCamera();
        int i = 1 + key_index;
        if (i < alltestindex.length) {
            Intent localIntent2 = new Intent(this, TestUtils.ALLCLASSES[alltestindex[i]]);
            localIntent2.putExtra("all_index", alltestindex);
            localIntent2.putExtra("current_test_index", i);
            startActivity(localIntent2);
            finish();
        } else {
            Intent localIntent1 = new Intent(this, ReportActivity.class);
            startActivity(localIntent1);
            finish();
        }
    }

    private void startTest() {
        //获取前置摄像头ID
        getCameraId();
        mStartTime = System.currentTimeMillis();
        mCameraId = mFrontCameraId;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(surfaceHolderCallback);
        mHandler.sendEmptyMessageDelayed(0, 1000L);
    }

    private void stopPreview() {
        if ((mCamera != null) && (mIsPreviewing)) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mIsPreviewing = false;
    }

    private void stopTest(boolean paramBoolean) {
        mHandler.removeMessages(0);
        getWindow().clearFlags(128);
        stopPreview();
        new Thread(new Runnable() {
            public void run() {
                File[] arrayOfFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).listFiles();
                if (arrayOfFile != null)
                    for (int i = 0; i < arrayOfFile.length; i++)
                        if (arrayOfFile[i].getName().endsWith("test.jpg"))
                            arrayOfFile[i].delete();
            }
        }).start();
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();
        int i = 0;
        if (paramBoolean)
            i = 1;
        localEditor.putInt("front_cameraresult", i);
        localEditor.commit();
        startNext();
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    public int getDisplayOrientation(int paramInt1, int paramInt2) {
        Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(paramInt2, localCameraInfo);
        Log.d(TAG, "getDisplayOrientation=>degrees: " + paramInt1 + " camera: " + localCameraInfo.orientation);
        if (localCameraInfo.facing == 1)
            return (180 + (paramInt1 + localCameraInfo.orientation)) % 360;
        return (360 + (localCameraInfo.orientation - paramInt1)) % 360;
    }

    public int getDisplayRotation(Activity paramActivity) {
        switch (paramActivity.getWindowManager().getDefaultDisplay().getRotation()) {
            default:
                return 0;
            case 0:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
        }
        return 270;
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.stop_test:
                stopTest(false);
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        super.onDestroy();
        closeCamera();
    }

    protected void onPause() {
        super.onPause();
        if (mLock.isHeld())
            mLock.release();
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()...");
        mLock.acquire();
    }

    public void startPreview(int paramInt) {
        Log.d(TAG, "startPreview=>id: " + paramInt + " camera: " + mCamera);

        try {
            if (mCamera == null) {
                mCamera = Camera.open(paramInt);
            }
            if (mIsPreviewing)
                stopPreview();
            if ((mCamera == null) || (mIsPreviewing))
                return;
        } catch (RuntimeException localRuntimeException) {
            stopTest(false);
            return;
        }

        setCameraParameters();
        setPreviewDisplay(mSurfaceHolder, paramInt);
        try {
            mCamera.startPreview();
            safeToTakePicture = true;
            mIsPreviewing = true;
            //onTakePictures();
        } catch (Exception localException) {
            stopTest(false);
        }
    }


    SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override

        public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {
            mSurfaceHolder = paramSurfaceHolder;
            if (mCamera == null)
                return;
            if ((mIsPreviewing) && (paramSurfaceHolder.isCreating())) {
                setPreviewDisplay(paramSurfaceHolder, mCameraId);
                return;
            }
            startPreview(mCameraId);
        }

        public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
            startPreview(mCameraId);
        }

        public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
            closeCamera();
        }
    };
}