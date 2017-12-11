package com.android.agingtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
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

public class BaseActivity extends Activity {
    public static final String TAG = "BaseActivity";
    private int[] alltestindex = null;
    protected boolean isStartTest = false;
    String key;
    private int key_index = -1;
    public PowerManager mPowerManager;
    private PowerManager.WakeLock mLock;
    protected SharedPreferences mSharedPreferences;
    public long mStartTime = 0L;
    private long mTestTime = 0L;
    private Button stopBtn;
    protected TextView timeTextView;
    protected TextView mTextTitle;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            Log.e(TAG, "handleMessage-what->" + paramAnonymousMessage.what);
            switch (paramAnonymousMessage.what) {
                case 0:
                    long l = System.currentTimeMillis() - mStartTime;
                    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
                    timeTextView.setText(localSimpleDateFormat.format(new Date(l)));
                    Log.d(TAG, "handleMessag=>testTime: " + l);
                    if (isTestOver()) {
                        stopTest(true);
                        return;
                    }
                    mHandler.sendEmptyMessageDelayed(0, 1000L);
                    break;
                case 1:
                    break;
            }
        }
    };

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.base_layout);
        key_index = getIntent().getIntExtra("current_test_index", -1);
        alltestindex = getIntent().getIntArrayExtra("all_index");
        Log.e(TAG, "key_index->" + key_index);
        if (key_index != -1) {
            key = TestUtils.ALLKEYS[alltestindex[key_index]];
        } else {
            finish();
            Log.e(TAG, "no this test key");
        }
        initValues();
        stopBtn = ((Button) findViewById(R.id.stop_test));
        stopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                stopTest(false);
            }
        });
        timeTextView = ((TextView) findViewById(R.id.test_time));
        mTextTitle = ((TextView) findViewById(R.id.test_title));
        setText();
        doTest();
        startTest();
    }

    public void setText() {
    }

    public boolean isTestOver() {
        boolean bool = false;
        if (System.currentTimeMillis() - mStartTime >= mTestTime) bool = true;
        Log.e(TAG, "isTestOver-->" + bool);
        return bool;
    }

    public void initValues() {
        mPowerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        mPowerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        mLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "front_taking_picture_test");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //测试时间单位是分钟
        mTestTime = (60000L * mSharedPreferences.getInt(key + "time", 10));
        Log.d(TAG, " mTestTime =" + mTestTime);
    }

    public void doTest() {
        Log.e(TAG, "doTest-->");
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
        isStartTest = true;
        mStartTime = System.currentTimeMillis();
        mHandler.sendEmptyMessage(0);
        mHandler.sendEmptyMessage(1);
    }

    public void stopTest(boolean paramBoolean) {
        int i = 1;
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
//        if (!mPowerManager.isScreenOn())
//            mPowerManager.wakeUp(SystemClock.uptimeMillis());
        stopBtn.setEnabled(false);
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();
        String str = key + "result";
        if (!paramBoolean) i = 0;
        localEditor.putInt(str, i);
        Log.e(TAG, "stopTest key->" + key);
        localEditor.commit();
        startNext();
    }

    public void startNext() {
        int i = 1 + key_index;
        Log.e(TAG, "startNext-currentIndex->" + i + ",alltestindex->" + alltestindex.length);
        if (i < alltestindex.length) {
            String str = TestUtils.ALLKEYS[alltestindex[i]];
            Log.e(TAG, "key->" + str + ",alltestindex->" + alltestindex.toString());
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}