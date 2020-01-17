package com.NBZxing.lib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
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
    private static final int VIBRATE_DURATION = 10;

    private static boolean playBeep = false;
    private static MediaPlayer mediaPlayer;

    public static void playBeep() {

        playBeep = true;

        AudioManager audioService = (AudioManager) Utils.getAppContext().getSystemService(Utils.getAppContext().AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(0);
                }
            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

            AssetFileDescriptor file = null;
            try {
                file = Utils.getAppContext().getAssets().openFd("scan.wav");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file == null)
                return;

            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                mediaPlayer = null;
            }

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