package com.android.agingtest.test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.KeyguardManager;

import com.android.agingtest.ReportActivity;
import com.android.agingtest.TestUtils;
import com.android.agingtest.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FlashLightActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "FlashLightActivity";
    private int[] alltestindex = null;
    private Button flashBtn;
    private boolean isFlashOn = false;
    String key;
    private int key_index = -1;
    private KeyguardManager keyguardManager;
    private Camera mCamera;
    private long mFlashTime;
    //闪光灯
    private static final int FLASH = 0;

    //手电筒
    private static final int TORCH = 1;

    //刷新时间
    private static final int REFRESH = 2;

    //当前的模式，默认是闪光灯
    private int currentMode = FLASH;
    Message message;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
			Log.d(TAG, "handleMessage what ="+paramAnonymousMessage.what);
            switch (paramAnonymousMessage.what) {
                case FLASH:
                    int count = (int) paramAnonymousMessage.obj;
                    if (count % 2 != 0) {
                        turnOff();
                    } else {
                        turnOn();
                    }
                    count++;
                    message = mHandler.obtainMessage();
                    message.obj = count;
                    message.what = FLASH;
                    mHandler.sendMessageDelayed(message, 1000L);
                    break;
                case TORCH:
                    mHandler.sendEmptyMessageDelayed(TORCH, 1000L);
                    break;
            }
        }
    };

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long l = System.currentTimeMillis() - mStartTime;
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
            mTestTimeTv.setText(localSimpleDateFormat.format(new Date(l)));
            if (isTestOver()) {
                stopTest(true);
                return;
            }
            timeHandler.sendEmptyMessageDelayed(REFRESH, 1000L);
        }
    };
    private PowerManager.WakeLock mLock;
    private PowerManager mPowerManager;
    private SharedPreferences mSharedPreferences;
    private long mStartTime;
    private Button mStopBt;
    private TextView mTestTimeTv;
    private int testMode = -1;
    private Button torchBtn;
    private TextView mTextTitle;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.flashlight_test);
        initValues();
        initViews();
        startTest();
    }

    private void initViews() {
        mTestTimeTv = ((TextView) findViewById(R.id.test_time));
        mTextTitle = ((TextView) findViewById(R.id.test_title));
        mTextTitle.setText(getString(R.string.flashlight_title));
        mStopBt = ((Button) findViewById(R.id.stop_test));
        mTestTimeTv.setText(R.string.default_time_string);
        mStopBt.setOnClickListener(this);
        flashBtn = ((Button) findViewById(R.id.flash_mode));
        torchBtn = ((Button) findViewById(R.id.torch_mode));
        flashBtn.setOnClickListener(this);
        torchBtn.setOnClickListener(this);
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

    //关闭闪光灯
    private void turnOff() {
        if (mCamera != null) {
            Camera.Parameters localParameters = mCamera.getParameters();
            localParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(localParameters);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    //打开闪光灯
    private void turnOn() {
        isFlashOn = true;
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        Camera.Parameters localParameters = mCamera.getParameters();
        localParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(localParameters);
        //startPreview不能缺少
        mCamera.startPreview();
    }

    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        Log.d(TAG, "dispatchKeyEvent=>keycode: " + paramKeyEvent.getKeyCode());
        switch (paramKeyEvent.getKeyCode()) {
            default:
                return super.dispatchKeyEvent(paramKeyEvent);
            case 3:
            case 4:
        }
        Toast.makeText(this, R.string.testing_tip, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void initValues() {
        key_index = getIntent().getIntExtra("current_test_index", -1);
        alltestindex = getIntent().getIntArrayExtra("all_index");
        Log.e(TAG, "key_index->" + key_index + ",alltestindex[key_index]->" + TestUtils.ALLKEYS[alltestindex[key_index]]);
        key = "flashlight";
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPowerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        mLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "sprocomm");
        keyguardManager = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE));
        mFlashTime = (60000L * mSharedPreferences.getInt(key + "time", 10));
    }

    public boolean isTestOver() {
        return System.currentTimeMillis() - mStartTime >= mFlashTime;
    }

    /**
     * 1表示手电筒 0表示闪光灯
     */
    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.stop_test:
                stopTest(false);
                break;
            case R.id.flash_mode:
                if (currentMode == TORCH) {
                    //移除手电筒的线程
                    mHandler.removeMessages(TORCH);
                    turnOff();
                }
                currentMode = FLASH;
                //闪光灯
                Message message = mHandler.obtainMessage();
                message.obj = 0;
                message.what = FLASH;
                mHandler.sendMessage(message);
                break;
            case R.id.torch_mode:
                //关闭闪光灯
                if (currentMode == FLASH) {
                    turnOff();
                    turnOn();
                    //移除闪光灯的线程
                    mHandler.removeMessages(FLASH);
                }
                currentMode = TORCH;
                //手电筒
                mHandler.sendEmptyMessage(TORCH);
                break;
        }
    }


    protected void onPause() {
        super.onPause();
        if (mLock.isHeld())
            mLock.release();
    }

    protected void onResume() {
        super.onResume();
        mLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOff();
        closeCamera();
    }

    public void startTest() {
        mStartTime = System.currentTimeMillis();
        if (currentMode == FLASH) {
            Message message = mHandler.obtainMessage();
            message.obj = 0;
            message.what = FLASH;
            mHandler.sendMessageDelayed(message, 1000L);
        } else {
            mHandler.sendEmptyMessageDelayed(currentMode, 1000L);
        }
        timeHandler.sendEmptyMessageDelayed(REFRESH, 1000L);
    }

    public void stopTest(boolean paramBoolean) {
        int i = 1;
        mHandler.removeMessages(TORCH);
        mHandler.removeMessages(FLASH);
		timeHandler.removeMessages(REFRESH);
        turnOff();
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();
        if (!paramBoolean) {
            i = 0;
        }
        localEditor.putInt("flashlightresult", i);
        localEditor.commit();
        startNext();
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}