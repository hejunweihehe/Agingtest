package com.android.agingtest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ReportActivity extends Activity
        implements OnClickListener {
    private static final String TAG = "ReportActivity";
    int isResetState = -1;
    private View mBackTakingPictureContainer;
    private TextView mBackTakingPictureTv;
    private TextView mCameraMotor;
    private Button mCancel;
    private TextView mFlashLight;
    private View mFrontTakingPictureContainer;
    private TextView mFrontTakingPictureTv;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            Log.d(TAG, "handleManager=>what: " + paramAnonymousMessage.what);
            switch (paramAnonymousMessage.what) {
                case 0:
            }
        }
    };
    private Button mOk;
    private View mPlayVideoContainer;
    private TextView mPlayVideoTv;
    private View mReceiverContainer;
    private TextView mReceiverTv;
    private SharedPreferences mSharedPreferences;
    private View mVibrateContainer;
    private TextView mVibrateTv;
    private TextView mloudSpeaker;
    private String result = "result";

    protected void onCreate(Bundle paramBundle) {
        Log.d(TAG, "onCreate()...");
        super.onCreate(paramBundle);
        Log.e(TAG, "isResetSuccess->" + isResetState);
        setContentView(R.layout.activity_report);
        initValues();
        initViews();
    }

    private int getStateColor(int paramInt) {
        Resources localResources = getResources();
        switch (paramInt) {
            case 0:
                return localResources.getColor(R.color.fail_text_color);
            case 1:
                return localResources.getColor(R.color.pass_text_color);
        }
        return localResources.getColor(R.color.not_test_text_color);
    }

    private int getStateText(int paramInt) {
        switch (paramInt) {
            case 0:
                return R.string.fail;
            case 1:
                return R.string.pass;
        }
        return R.string.not_tested;
    }

    private void initValues() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void initViews() {
        mVibrateContainer = findViewById(R.id.vibrate_container);
        mReceiverContainer = findViewById(R.id.receiver_container);
        mFrontTakingPictureContainer = findViewById(R.id.front_taking_picture_container);
        mBackTakingPictureContainer = findViewById(R.id.back_taking_picture_container);
        mPlayVideoContainer = findViewById(R.id.play_video_container);
        mVibrateTv = ((TextView) findViewById(R.id.vibrate_report));
        mReceiverTv = ((TextView) findViewById(R.id.receiver_report));
        mFrontTakingPictureTv = ((TextView) findViewById(R.id.front_taking_picture_report));
        mBackTakingPictureTv = ((TextView) findViewById(R.id.back_taking_picture_report));
        mPlayVideoTv = ((TextView) findViewById(R.id.play_video_report));
        mCameraMotor = ((TextView) findViewById(R.id.camera_motor_report));
        mFlashLight = ((TextView) findViewById(R.id.flash_light_report));
        mloudSpeaker = ((TextView) findViewById(R.id.loud_speaker_report));
        mOk = ((Button) findViewById(R.id.ok));
        mCancel = ((Button) findViewById(R.id.cancel));
        mOk.setOnClickListener(this);
    }

    private void updateUI() {
        int i = mSharedPreferences.getInt("reboot" + result, -1);
        int j = mSharedPreferences.getInt("sleep" + result, -1);
        int k = mSharedPreferences.getInt("vibrate" + result, -1);
        mVibrateTv.setText(getStateText(k));
        mVibrateTv.setTextColor(getStateColor(k));
        int m = mSharedPreferences.getInt("receiver" + result, -1);
        mReceiverTv.setText(getStateText(m));
        mReceiverTv.setTextColor(getStateColor(m));
        int n = mSharedPreferences.getInt("front_camera" + result, -1);
        mFrontTakingPictureTv.setText(getStateText(n));
        mFrontTakingPictureTv.setTextColor(getStateColor(n));
        int i1 = mSharedPreferences.getInt("back_camera" + result, -1);
        mBackTakingPictureTv.setText(getStateText(i1));
        mBackTakingPictureTv.setTextColor(getStateColor(i1));
        int i2 = mSharedPreferences.getInt("video" + result, -1);
        mPlayVideoTv.setText(getStateText(i2));
        mPlayVideoTv.setTextColor(getStateColor(i2));
        int i3 = mSharedPreferences.getInt("motor" + result, -1);
        mCameraMotor.setText(getStateText(i3));
        mCameraMotor.setTextColor(getStateColor(i3));
        int i4 = mSharedPreferences.getInt("flashlight" + result, -1);
        mFlashLight.setText(getStateText(i4));
        mFlashLight.setTextColor(getStateColor(i4));
        int i5 = mSharedPreferences.getInt("loudspeaker" + result, -1);
        mloudSpeaker.setText(getStateText(i5));
        mloudSpeaker.setTextColor(getStateColor(i5));
        int i6 = mSharedPreferences.getInt("tp_test" + result, -1);
    }

    private void updateViewsVisible() {
        Resources localResources = getResources();
        if (!localResources.getBoolean(R.bool.vibrate_visible))
            mVibrateContainer.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.receiver_visible))
            mReceiverContainer.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.front_taking_picture_visible))
            mFrontTakingPictureContainer.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.back_taking_picture_visible))
            mBackTakingPictureContainer.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.play_video_visible))
            mPlayVideoContainer.setVisibility(View.GONE);
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.ok:
                mHandler.removeMessages(0);
                Intent localIntent2 = new Intent(this, AgingTestMainActivity.class);
                startActivity(localIntent2);
                break;
        }
    }

    protected void onPause() {
        Log.d(TAG, "onPause()...");
        super.onPause();
    }

    protected void onResume() {
        updateUI();
        super.onResume();
    }

    protected void onStart() {
        Log.d(TAG, "onStart()...");
        updateViewsVisible();
        super.onStart();
    }
}