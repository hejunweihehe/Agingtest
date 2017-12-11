package com.android.agingtest.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.agingtest.R;
import com.android.agingtest.ReportActivity;
import com.android.agingtest.TPTestView;
import com.android.agingtest.TestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TPTestActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TPTestActivity";
    String key;
    private long mTptestTime;
    private SharedPreferences mSharedPreferences;
    private long mStartTime;
    private TextView mTestTimeTv;
    private TextView mTextTitle;
    private Button finish;
    private TPTestView tpTestView;
    private TextView test_result;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    long l = System.currentTimeMillis() - mStartTime;
                    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
                    mTestTimeTv.setText(localSimpleDateFormat.format(new Date(l)));
                    Log.d(TAG, "handleMessage=>testTime: " + l);
                    if (isTestOver()) {
                        stopTest(true);
                        return;
                    }

                    mHandler.sendEmptyMessageDelayed(0, 1000L);
                    break;
                case 1:
                    Log.d(TAG, "what = 1");
                    stopTest(false);
                    break;
            }
        }
    };

    private boolean isTestOver() {
        return System.currentTimeMillis() - mStartTime >= mTptestTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tptest);
        mStartTime = System.currentTimeMillis();
        initValues();
        initViews();
        mHandler.sendEmptyMessageDelayed(0, 1000L);
    }

    private void initViews() {
        tpTestView = ((TPTestView)findViewById(R.id.tp_view));
        tpTestView.setHandler(mHandler);
        mTestTimeTv = ((TextView) findViewById(R.id.test_time));
        mTextTitle = ((TextView) findViewById(R.id.test_title));
        mTextTitle.setText(getString(R.string.tp_test_title));
        finish = ((Button) findViewById(R.id.finish));
        test_result = ((TextView) findViewById(R.id.test_result));
        mTestTimeTv.setText(R.string.default_time_string);
        finish.setOnClickListener(this);
    }

    private void initValues() {
        key = "tp_test";
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTptestTime = (60000L * mSharedPreferences.getInt("tp_testtime", getResources().getInteger(R.integer.default_tp_test_time)));
        Log.d(TAG, "initValues=>startTime: " + mStartTime + " time: " + mTptestTime);
    }

    private void stopTest(boolean paramBoolean) {
        Log.d(TAG, "stopTest");
        mHandler.removeMessages(0);
        tpTestView.setDisable(true);
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();
        int i = 0;
        if (paramBoolean)
            i = 1;
        localEditor.putInt("tp_testresult", i);
        localEditor.commit();
        if(paramBoolean){
            test_result.setTextColor(getColor(R.color.pass_text_color));
            test_result.setText(getString(R.string.tp_test_success));
        }else{
            test_result.setTextColor(getColor(R.color.fail_text_color));
            test_result.setText(getString(R.string.tp_test_fail));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish:
                mHandler.removeMessages(0);
                finish();
                break;
        }
    }
}
