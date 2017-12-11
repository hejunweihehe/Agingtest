package com.android.agingtest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TimeSettingsActivity extends Activity
        implements OnClickListener, OnFocusChangeListener {
    private EditText[] allTimeEt = new EditText[8];
    private View mBackTakingPictureContainer;
    private EditText mBackTakingPictureEt;
    private EditText mCameraMotor;
    private Button mCancel;
    private EditText mFlashLight;
    private View mFrontTakingPictureContainer;
    private EditText mFrontTakingPictureEt;
    private EditText mLoudSpeaker;
    private Button mOk;
    private View mPlayVideoContainer;
    private EditText mPlayVideoEt;
    private View mReceiverContainer;
    private EditText mReceiverEt;
    private SharedPreferences mSharedPreferences;
    private View mVibrateContainer;
    private EditText mVibrateEt;
    private EditText tp_test_time;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_settings);
        initValues();
        initViews();
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
        mVibrateEt = ((EditText) findViewById(R.id.vibrate_time));
        tp_test_time = ((EditText) findViewById(R.id.tp_test_time));
        mReceiverEt = ((EditText) findViewById(R.id.receiver_time));
        mFrontTakingPictureEt = ((EditText) findViewById(R.id.front_taking_picture_time));
        mBackTakingPictureEt = ((EditText) findViewById(R.id.back_taking_picture_time));
        mPlayVideoEt = ((EditText) findViewById(R.id.play_video_time));
        mCameraMotor = ((EditText) findViewById(R.id.motor_time));
        mFlashLight = ((EditText) findViewById(R.id.flashlight_time));
        mLoudSpeaker = ((EditText) findViewById(R.id.loudspeaker_time));
        allTimeEt[0] = mVibrateEt;
        allTimeEt[1] = mReceiverEt;
        allTimeEt[2] = mFrontTakingPictureEt;
        allTimeEt[3] = mBackTakingPictureEt;
        allTimeEt[4] = mPlayVideoEt;
        allTimeEt[5] = mCameraMotor;
        allTimeEt[6] = mFlashLight;
        allTimeEt[7] = mLoudSpeaker;
        mOk = ((Button) findViewById(R.id.ok));
        mCancel = ((Button) findViewById(R.id.cancel));
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private boolean updateSharedPreference() {
        Editor localEditor = mSharedPreferences.edit();
        int i = allTimeEt.length;
        for (int j = 0; j < i; j++) {
            try {
                String str1 = TestUtils.ALLKEYS[j] + "time";
                String str2 = allTimeEt[j].getText().toString();
                if ((!TextUtils.isEmpty(str2)) && (TextUtils.isDigitsOnly(str2))) {
                    localEditor.putInt(str1, Integer.parseInt(str2));
                    localEditor.commit();
                }
            } catch (Exception localException) {
                localException.printStackTrace();
                return false;
            }
        }
        localEditor.putInt("tp_testtime", Integer.parseInt(tp_test_time.getText().toString()));
        localEditor.commit();
        return true;
    }

    private void updateUI() {
        Resources localResources = getResources();
        int i = allTimeEt.length;
        for (int j = 0; j < i; j++) {
            String str = TestUtils.ALLKEYS[j] + "time";
            EditText localEditText = allTimeEt[j];
            localEditText.setText(mSharedPreferences.getInt(str, localResources.getInteger(R.integer.default_test_time)) + "");
            localEditText.setOnFocusChangeListener(this);
        }
        tp_test_time.setText(mSharedPreferences.getInt("tp_testtime", localResources.getInteger(R.integer.default_test_time)) + "");
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.ok:
                if (updateSharedPreference()) {
                    Toast.makeText(this, R.string.setting_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.setting_fail, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    public void onFocusChange(View paramView, boolean paramBoolean) {
        if (paramBoolean) ;
        switch (paramView.getId()) {
            case R.id.vibrate_time:
                mVibrateEt.setSelection(mVibrateEt.getText().toString().length());
                break;
            case R.id.receiver_time:
                mReceiverEt.setSelection(mReceiverEt.getText().toString().length());
                break;
            case R.id.front_taking_picture_time:
                mFrontTakingPictureEt.setSelection(mFrontTakingPictureEt.getText().toString().length());
                break;
            case R.id.back_taking_picture_time:
                mBackTakingPictureEt.setSelection(mBackTakingPictureEt.getText().toString().length());
                break;
            case R.id.play_video_time:
                mPlayVideoEt.setSelection(mPlayVideoEt.getText().toString().length());
                break;
            case R.id.motor_time:
                mCameraMotor.setSelection(mCameraMotor.getText().toString().length());
                break;
            case R.id.flashlight_time:
                mFlashLight.setSelection(mFlashLight.getText().toString().length());
                break;
            case R.id.loudspeaker_time:
                mLoudSpeaker.setSelection(mLoudSpeaker.getText().toString().length());
            case R.id.tp_test_time:
                tp_test_time.setSelection(tp_test_time.getText().toString().length());
                break;
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        updateUI();
        super.onResume();
    }

    protected void onStart() {
        super.onStart();
    }
}