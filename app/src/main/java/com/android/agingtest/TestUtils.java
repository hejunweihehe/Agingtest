package com.android.agingtest;

import com.android.agingtest.test.BackTakingPictureActivity;
import com.android.agingtest.test.FlashLightActivity;
import com.android.agingtest.test.FrontTakingPictureActivity;
import com.android.agingtest.test.LoudSpeakerActivity;
import com.android.agingtest.test.MotorZoomActivity;
import com.android.agingtest.test.PlayVideoActivity;
import com.android.agingtest.test.ReceiverActivity;
import com.android.agingtest.test.VibrateActivity;
import android.content.Context;
import android.os.PowerManager;
import android.os.Vibrator;


import java.util.ArrayList;
import java.util.HashMap;

public class TestUtils {
    public static final Class[] ALLCLASSES;
    public static final String[] ALLKEYS;
    public static final HashMap<String, Integer> indexMap;
    private static final ArrayList<Class> mTestList = new ArrayList();

    static {
        ALLKEYS = new String[]{"vibrate", "receiver", "front_camera", "back_camera", "video", "motor", "flashlight", "loudspeaker"};
        ALLCLASSES = new Class[]{VibrateActivity.class,
                ReceiverActivity.class, FrontTakingPictureActivity.class, BackTakingPictureActivity.class,
                PlayVideoActivity.class, MotorZoomActivity.class, FlashLightActivity.class, LoudSpeakerActivity.class};
        indexMap = new HashMap();
        //振动
        indexMap.put("vibrate", Integer.valueOf(0));
        //听筒
        indexMap.put("receiver", Integer.valueOf(1));
        //前置摄像头拍照
        indexMap.put("front_camera", Integer.valueOf(2));
        //后置摄像头拍照
        indexMap.put("back_camera", Integer.valueOf(3));
        //视频
        indexMap.put("video", Integer.valueOf(4));
        //摄像头马达测试
        indexMap.put("motor", Integer.valueOf(5));
        //闪光灯
        indexMap.put("flashlight", Integer.valueOf(6));
        //扬声器
        indexMap.put("loudspeaker", Integer.valueOf(7));
    }

    public static void cancelVibrate(Context paramContext) {
        Vibrator localVibrator = (Vibrator) paramContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (localVibrator.hasVibrator())
            localVibrator.cancel();
    }

    public static void reboot(Context paramContext, String paramString) {
        ((PowerManager) paramContext.getSystemService(Context.POWER_SERVICE)).reboot(paramString);
    }

    public static void vibrate(Context paramContext) {
        ((Vibrator) paramContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{0L, 1000L, 0L, 1000L, 0L, 1000L}, 0);
    }
}