package com.ailiwean.core.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.ailiwean.core.Utils;
import com.google.android.cameraview.R;

/**
 * @Package: com.ailiwean.core.helper
 * @ClassName: VibrateHelper
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 5:07 PM
 */
public class VibrateHelper {


    private static final float BEEP_VOLUME = 0.50f;
    private static final int VIBRATE_DURATION = 10;
    private static SoundPool soundPool;
    private static int voiceId;

    public static void playBeep() {
        if (soundPool != null) {
            soundPool.play(voiceId, 1, 1, 1, 0, 1);
        }
    }

    public static void playInit() {
        if (soundPool != null)
            soundPool.release();
        //实例化SoundPool
        //sdk版本21是SoundPool 的一个分水岭
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        voiceId = soundPool.load(Utils.getContext(), R.raw.scan, 1);
    }

    public static void playVibrate() {
        RxVibrateTool.vibrateOnce(Utils.getContext(), VIBRATE_DURATION);
    }

    /**
     * @author Vondear
     * @date 2017/7/25
     * 震动帮助类
     * androidManifest.xml中加入 以下权限
     * <uses-permission android:name="android.permission.VIBRATE" />
     */
    private static class RxVibrateTool {
        private static Vibrator vibrator;

        /**
         * 简单震动
         *
         * @param context     调用震动的Context
         * @param millisecond 震动的时间，毫秒
         */
        @SuppressLint("MissingPermission")
        @SuppressWarnings("static-access")
        static void vibrateOnce(Context context, int millisecond) {
            vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(millisecond, 25));
            } else vibrator.vibrate(millisecond);
        }

        /**
         * 复杂的震动
         *
         * @param context 调用震动的Context
         * @param pattern 震动形式
         *                数组参数意义：
         *                第一个参数为等待指定时间后开始震动，
         *                震动时间为第二个参数。
         *                后边的参数依次为等待震动和震动的时间
         * @param repeate 震动的次数，-1不重复，非-1为从pattern的指定下标开始重复 0为一直震动
         */
        @SuppressLint("MissingPermission")
        @SuppressWarnings("static-access")
        public static void vibrateComplicated(Context context, long[] pattern, int repeate) {
            vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, repeate);
        }

        /**
         * 停止震动
         */
        @SuppressLint("MissingPermission")
        public static void vibrateStop() {
            if (vibrator != null) {
                vibrator.cancel();
            }
        }
    }

}
