package com.wishzixing.lib.util;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntDef;
import android.util.Log;

import com.wishzixing.lib.listener.AutoFocusCallback;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.manager.SensorManager;

/***
 *  Created by SWY
 *  自动调焦
 *  DATE 2019/6/7
 *
 */
public class AutoFocusUtils {

    private final long TIMEINTERVAL = 1500L;

    private int model = 0;

    private HandlerThread handlerThread;
    private Handler timeHandler;


    private AutoFocusUtils() {
        handlerThread = new HandlerThread("time");
        handlerThread.start();
        timeHandler = new Handler(handlerThread.getLooper());
    }

    private static class Holder {
        static AutoFocusUtils INSTANCE = new AutoFocusUtils();
    }

    public static AutoFocusUtils getInstance() {
        return Holder.INSTANCE;
    }

    private void setFocus() {
        if (CameraManager.get().getCamera() == null)
            return;

        Camera camera = CameraManager.get().getCamera();
        camera.startPreview();
        camera.autoFocus(AutoFocusCallback.getInstance());
    }

    public AutoFocusUtils setModel(@Type int model) {
        this.model = model;
        return this;
    }

    public void startAutoFocus() {

        if (model == TIME)
            setTimeAutoFocus();
        if (model == SENSOR)
            setSensorAutoFocus();
        if (model == PIXVALUES)
            setPixvaluesAutoFocus();

    }

    private void setTimeAutoFocus() {
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocus();
                timeHandler.postDelayed(this, TIMEINTERVAL);
            }
        }, TIMEINTERVAL);
    }

    private void setSensorAutoFocus() {

        SensorManager.getInstance().registerListener(new SensorManager.SensorChange() {
            @Override
            public void change() {
                setFocus();
                Log.e("加速度传感器回调", "加速度传感器回调");
            }
        })
                .startListener();


    }

    private void setPixvaluesAutoFocus() {

    }


    /***
     * 停止所有自动调焦的方法
     */
    public void stopAutoFocus() {
        timeHandler.removeCallbacksAndMessages(null);
    }


    public static final int TIME = 1;
    public static final int SENSOR = 2;
    public static final int PIXVALUES = 3;

    @IntDef({TIME, SENSOR, PIXVALUES})
    public static @interface Type {
    }
}
