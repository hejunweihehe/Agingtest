package com.android.agingtest.test;

import android.app.Activity;
import com.android.agingtest.Player;
import com.android.agingtest.R;
import com.android.agingtest.ReportActivity;
import com.android.agingtest.TestUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PlayVideoActivity extends Activity
        implements View.OnClickListener, Player.PlayerListener {
    private static final String TAG = "PlayVideoActivity";
    private static final String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/moveTest.mp4";
    private int[] alltestindex = null;
    String key;
    private int key_index = -1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            Log.d(TAG, "handleMessage");
            switch (paramAnonymousMessage.what) {
                case 0:
                    long l = System.currentTimeMillis() - mStartTime;
                    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
                    mTestTimeTv.setText(localSimpleDateFormat.format(new Date(l)));
                    if (isTestOver()) {
                        stopTest(true);
                        return;
                    }
                    mHandler.sendEmptyMessageDelayed(0, 1000L);
            }
        }
    };
    private PowerManager.WakeLock mLock;
    private long mPlayVideoTime;
    private PowerManager mPowerManager;
    private SharedPreferences mSharedPreferences;
    private long mStartTime;
    private Button mStopBt;
    private VideoView video_view;
    private TextView mTestTimeTv;
    private TextView mTextTitle;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play_video);
        initValues();
        initViews();
        startTest();
    }

    private void initValues() {
        Log.d(TAG, "initValues");
        key_index = getIntent().getIntExtra("current_test_index", -1);
        alltestindex = getIntent().getIntArrayExtra("all_index");
        Log.e("lsz", "key_index->" + key_index + ",alltestindex[key_index]->" + TestUtils.ALLKEYS[alltestindex[key_index]]);
        key = "video";
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPowerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        mLock = mPowerManager.newWakeLock(10, "play_video_test");
        mPlayVideoTime = (60000L * mSharedPreferences.getInt("videotime", getResources().getInteger(R.integer.default_play_video_time)));
        Log.d(TAG, "initValues=>startTime: " + mStartTime + " time: " + mPlayVideoTime);
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        mTestTimeTv = ((TextView) findViewById(R.id.test_time));
        mTextTitle = ((TextView) findViewById(R.id.test_title));
        mTextTitle.setText(getString(R.string.play_video_title));
        mStopBt = ((Button) findViewById(R.id.stop_test));
        video_view = ((VideoView)findViewById(R.id.video_view));
        //mPlayer.setListener(this);
        mTestTimeTv.setText(R.string.default_time_string);
        mStopBt.setOnClickListener(this);
    }

    private void startNext() {
        Log.d(TAG, "startNext");
        //停止播放
        if (video_view != null && video_view.isPlaying()) {
            video_view.stopPlayback();
        }
        //释放资源
        if (video_view != null) {
            video_view.suspend();
        }
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
        Log.d(TAG, "startTest");
        mStartTime = System.currentTimeMillis();
        //mPlayer.playUrl(this);

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.move_test;
        video_view.setVideoURI(Uri.parse(uri));
        video_view.start();

        //监听视频播放完的代码
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mPlayer) {
                // TODO Auto-generated method stub
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        });
        mHandler.sendEmptyMessage(0);
    }

    private void stopTest(boolean paramBoolean) {
        Log.d(TAG, "stopTest");
        mHandler.removeMessages(0);
        //mPlayer.stop();
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();
        int i = 0;
        if (paramBoolean) {
            i = 1;
        }
        localEditor.putInt("videoresult", i);
        localEditor.commit();
        startNext();
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

    public boolean isTestOver() {
        Log.d(TAG, "isTestOver");
        if (System.currentTimeMillis() - mStartTime >= mPlayVideoTime) return true;
        return false;
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.stop_test:
                stopTest(false);
        }
    }


    public void onError() {
        Log.d(TAG, "onError");
        mHandler.removeMessages(0);
//        if (mPlayer != null)
//            mPlayer.stop();
        stopTest(false);
    }

    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (mLock.isHeld())
            mLock.release();
    }

    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mLock.acquire();
    }
}