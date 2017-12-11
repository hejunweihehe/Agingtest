package com.android.agingtest.test;

import com.android.agingtest.BaseActivity;
import com.android.agingtest.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class ReceiverActivity extends BaseActivity {
    AudioManager am;
    private int mSoundId;
    private SoundPool mSoundPool;

    @Override
    public void setText() {
        super.setText();
        mTextTitle.setText(R.string.receiver_title);
    }

    public void doTest() {
        super.doTest();
        am = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        am.setStreamVolume(0, am.getStreamMaxVolume(0), 0);
        am.setSpeakerphoneOn(false);
        am.setMode(2);
        mSoundPool = new SoundPool(2, 0, 100);
        mSoundId = mSoundPool.load(this, R.raw.pizzicato, 1);
        mSoundPool.setVolume(0, 1.0F, 1.0F);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool paramAnonymousSoundPool, int paramAnonymousInt1, int paramAnonymousInt2) {
                if(mSoundPool != null){
                    mSoundPool.play(paramAnonymousInt1, 1.0F, 1.0F, 1, -1, 1.0F);
                }
            }
        });
    }

    public void stopTest(boolean paramBoolean) {
        super.stopTest(paramBoolean);
        am.setMode(0);
        if (mSoundPool != null) {
            mSoundPool.stop(mSoundId);
            mSoundPool = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        am.setMode(0);
        if (mSoundPool != null) {
            mSoundPool.stop(mSoundId);
            mSoundPool = null;
        }
    }
}