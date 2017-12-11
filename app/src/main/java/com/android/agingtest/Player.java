package com.android.agingtest;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;

public class Player
        implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, Callback, OnErrorListener {
    private static final String TAG = "AgingTestPlayer";
    private PlayerListener mListener;
    public MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private int videoHeight;
    private int videoWidth;

    public Player(SurfaceView paramSurfaceView) {
        surfaceHolder = paramSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(3);
        mediaPlayer = new MediaPlayer();
    }

    public void onBufferingUpdate(MediaPlayer paramMediaPlayer, int paramInt) {
        Log.i(TAG, "onBufferingUpdate");
    }

    public void onCompletion(MediaPlayer paramMediaPlayer) {
        Log.i(TAG, "onCompletion");
    }

    public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2) {
        Log.i(TAG, "onError");
        if (mListener != null)
            mListener.onError();
        return false;
    }

    public void onPrepared(MediaPlayer paramMediaPlayer) {
        Log.i(TAG, "onPrepared");
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
        if ((videoHeight != 0) && (videoWidth != 0))
            paramMediaPlayer.start();
    }

    public void playUrl(Context paramContext) {
        Log.e(TAG, "playUrl");
        try {
            AssetFileDescriptor localAssetFileDescriptor = paramContext.getAssets().openFd("moveTest.mp4");
            mediaPlayer.reset();
            mediaPlayer.setDataSource(localAssetFileDescriptor.getFileDescriptor(), localAssetFileDescriptor.getStartOffset(), localAssetFileDescriptor.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.setScreenOnWhilePlaying(true);
            Log.e(TAG, "before prepare");
            mediaPlayer.prepare();
            Log.e(TAG, "after prepare");
        } catch (IOException localIOException) {
            Log.e(TAG, "IOException =" + localIOException);
            localIOException.printStackTrace();
        } catch (IllegalStateException localIllegalStateException) {
            Log.e(TAG, "IllegalStateException =" + localIllegalStateException);
            localIllegalStateException.printStackTrace();
        } catch (IllegalArgumentException localIllegalArgumentException) {
            Log.e(TAG, "IllegalArgumentException =" + localIllegalArgumentException);
            localIllegalArgumentException.printStackTrace();
        }
    }

    public void setListener(PlayerListener paramPlayerListener) {
        Log.e(TAG, "setListener");
        mListener = paramPlayerListener;
    }

    public void stop() {
        Log.e(TAG, "stop");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {
        Log.e(TAG, "surface changed");
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        try {
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            Log.e(TAG, "surface created");
            return;
        } catch (Exception localException) {
            while (true)
                Log.e(TAG, "error", localException);
        }
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        stop();
        Log.e(TAG, "surface destroyed");
    }

    public static abstract interface PlayerListener {
        public abstract void onError();
    }
}