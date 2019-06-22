package com.wishzixing.lib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Vibrator;

import java.io.IOException;

/***
 *  Created by SWY
 *
 *  DATE 2019/6/2
 *
 */
public class RxBeepUtils {

    private static final float BEEP_VOLUME = 0.50f;
    private static final int VIBRATE_DURATION = 50;
    private static MediaPlayer mediaPlayer;

    public static void playBeep() {
        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor file = Utils.getAppContext().getAssets().openFd("scan.wav");
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            mediaPlayer = null;
        }
    }

    public static void playVibrate() {
        RxVibrateTool.vibrateOnce(Utils.getAppContext(), VIBRATE_DURATION);
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
        public static void vibrateOnce(Context context, int millisecond) {
            vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
            vibrator.vibrate(millisecond);
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