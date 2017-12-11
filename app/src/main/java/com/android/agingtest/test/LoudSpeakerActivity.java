package com.android.agingtest.test;

import com.android.agingtest.BaseActivity;
import com.android.agingtest.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class LoudSpeakerActivity extends BaseActivity {
    private static final String TAG = "LoudSpeakerAcitity";
    private String FileName = "/system/media/audio/alarms/Alarm_Beep_01.ogg";
    private AudioManager audioManager;
    private File file;
    MediaPlayer mPlayer = null;

    @Override
    public void setText() {
        super.setText();
        mTextTitle.setText(getString(R.string.loudspeaker_title));
    }

    public void initValues() {
        super.initValues();
        audioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        int i = audioManager.getStreamMaxVolume(3);
        Log.e(TAG, "maxVolume-->" + i);
        audioManager.setStreamVolume(3, i - 8, 4);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(0);
        file = new File(FileName);
        mPlayer = new MediaPlayer();
    }

    public void startTest() {
        super.startTest();
        try {
            mPlayer.setDataSource(file.getAbsolutePath());
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException localIOException) {
            mPlayer = null;
            localIOException.printStackTrace();
            Log.e(TAG, "IOException =" + localIOException.getMessage());
        } catch (IllegalStateException localIllegalStateException) {
            mPlayer = null;
            localIllegalStateException.printStackTrace();
            Log.e(TAG, "IllegalStateException =" + localIllegalStateException.getMessage());
        } catch (IllegalArgumentException localIllegalArgumentException) {
            mPlayer = null;
            localIllegalArgumentException.printStackTrace();
            Log.e(TAG, "IllegalArgumentException =" + localIllegalArgumentException.getMessage());
        }
    }

    public void stopTest(boolean paramBoolean) {
        super.stopTest(paramBoolean);
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        audioManager.setSpeakerphoneOn(false);
    }
}