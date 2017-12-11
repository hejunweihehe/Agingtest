package com.android.agingtest.test;

import android.app.Activity;

import com.android.agingtest.ReportActivity;
import com.android.agingtest.TestUtils;
import com.android.agingtest.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MotorZoomActivity extends Activity {
    private static String TAG = "MotorZoomActivity";
    private int[] alltestindex = null;
    private Camera.Parameters parameters;
    private TextView mTextTitle;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.motor_zoom_test);
        initValues();
        initViews();
    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean paramAnonymousBoolean, Camera paramAnonymousCamera) {
//            if (paramAnonymousBoolean) {
//                MotorZoomActivity. - set2(MotorZoomActivity.this, true);
//                return;
//            }
//            MotorZoomActivity. - set2(MotorZoomActivity.this, false);
        }
    };
    private Camera camera;
    private int counter = 0;
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            Log.d(TAG, "doAutoFocus");
            if (counter % 2 == 0) {
                rest_nubmer = rest_nubmer - 1;
                finish_number = (total_number - rest_nubmer);
                parameters.setZoom(0);
                parameters.setFlashMode(Parameters.WHITE_BALANCE_AUTO);
                camera.setParameters(parameters);
                camera.autoFocus(autoFocusCallback);
                mHandler.postDelayed(doAutoFocus, 2000L);
            } else {
                parameters.setFlashMode(Parameters.WHITE_BALANCE_AUTO);
                parameters.setZoom(10);
                camera.setParameters(parameters);
                camera.autoFocus(autoFocusCallback);
                mHandler.postDelayed(doAutoFocus, 2000L);
                MotorZoomActivity localMotorZoomActivity = MotorZoomActivity.this;
            }
            counter++;
        }
    };
    int finish_number;
    private boolean focusSuccess = true;
    private boolean isAutoFocus;
    String key;
    private int key_index = -1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            long l = System.currentTimeMillis() - mStartTime;
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
            mTime.setText(localSimpleDateFormat.format(new Date(l)));
            mHandler.sendEmptyMessageDelayed(0, 1000L);
            if (isTestOver()) {
                stopTest(true);
            }
        }
    };
    private long mMotorTestTime = 0L;
    private SharedPreferences mSharedPreferences;
    private long mStartTime = 0L;
    private long mTestTimes = 0;
    private TextView mTime;
    PowerManager pm;
    private PowerManager.WakeLock mLock;
    int rest_nubmer;
    private Button stop;
    SurfaceHolder surfaceHolder;
    SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder paramAnonymousSurfaceHolder, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            surfaceHolder = paramAnonymousSurfaceHolder;
        }

        public void surfaceCreated(SurfaceHolder paramAnonymousSurfaceHolder) {
            surfaceHolder = paramAnonymousSurfaceHolder;
            camera = Camera.open();
            if (camera != null) {
                parameters = camera.getParameters();

                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                    startTest();
                } catch (IOException localIOException) {
                    localIOException.printStackTrace();
                    closeCamera();
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder paramAnonymousSurfaceHolder) {
            surfaceView = null;
            surfaceHolder = null;
        }
    };
    SurfaceView surfaceView;
    int total_number;

    private void initViews() {
        surfaceView = ((SurfaceView) findViewById(R.id.surfaceView1));
        mTime = ((TextView) findViewById(R.id.test_time));
        mTextTitle = ((TextView) findViewById(R.id.test_title));
        mTextTitle.setText(getString(R.string.camera_motor_test_title));
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        stop = ((Button) findViewById(R.id.stop_test));
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                stopTest(false);
            }
        });
    }

    private void startNext() {
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

    public void initValues() {
        key_index = getIntent().getIntExtra("current_test_index", -1);
        alltestindex = getIntent().getIntArrayExtra("all_index");
        Log.e(TAG, "key_index->" + key_index + ",alltestindex[key_index]->" + TestUtils.ALLKEYS[alltestindex[key_index]]);
        key = "motor";
        pm = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        mLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "front_taking_picture_test");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTestTimes = 60000L * mSharedPreferences.getInt("motortime", 10);
    }

    public boolean isTestOver() {
        Log.e(TAG, "counter->" + counter + ",mTestTime->" + mTestTimes);
        return System.currentTimeMillis() - mStartTime >= mTestTimes;
    }

    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLock.isHeld())
            mLock.release();
    }

    protected void onResume() {
        super.onResume();
        mLock.acquire();
    }

    public void startTest() {
        Log.d(TAG, "MotorZoomActivity startTest");
        mStartTime = System.currentTimeMillis();
        counter = 0;
        isAutoFocus = true;
        camera.autoFocus(autoFocusCallback);
        if (!focusSuccess)
            Toast.makeText(getApplicationContext(), R.string.autofocus_failed, Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(doAutoFocus, 1000L);
        mHandler.sendEmptyMessageDelayed(0, 1000L);
    }

    private void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void stopTest(boolean paramBoolean) {
        //getWindow().clearFlags(128);
//        if (!pm.isScreenOn())
//            pm.wakeUp(SystemClock.uptimeMillis());
        closeCamera();
        Log.e(TAG, "MotorZoom stop");
        isAutoFocus = false;
        mHandler.removeMessages(0);
        mHandler.removeCallbacks(doAutoFocus);
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();
        int i = 0;
        if (paramBoolean)
            i = 1;
        localEditor.putInt("motorresult", i);
        localEditor.commit();
        startNext();
        Log.e(TAG, "MotorZoom finish");
    }
}