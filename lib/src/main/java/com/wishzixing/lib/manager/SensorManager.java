package com.wishzixing.lib.manager;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.wishzixing.lib.util.Utils;

import java.util.Calendar;

/***
 *  Created by SWY
 *  加速度传感器变化控制调焦
 *  DATE 2019/6/8
 *
 *
 */
public class SensorManager implements SensorEventListener {


    private android.hardware.SensorManager mSensorManager;
    private Sensor mSensor;

    private int mX, mY, mZ;


    private static final int DELEY_DURATION = 500;

    private SensorChange sensorChange;

    private boolean threshold = false;

    private int foucsing = 1;  //1 表示没有被锁定 0表示被锁定

    private SensorManager() {
        mSensorManager = (android.hardware.SensorManager) Utils.getAppContext().getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
    }

    private static class Holder {
        static SensorManager INSTANCE = new SensorManager();
    }

    public static SensorManager getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];

            long stamp = System.currentTimeMillis();// 1393844912


            int px = Math.abs(mX - x);
            int py = Math.abs(mY - y);
            int pz = Math.abs(mZ - z);

            double value = Math.sqrt(px * px + py * py + pz * pz);

            if (value > 1.4) {
                threshold = true;
                return;
            }
            mX = x;
            mY = y;
            mZ = z;

            if (!threshold)
                return;

            if (System.currentTimeMillis() - stamp < DELEY_DURATION)
                return;

            if (sensorChange != null)
                sensorChange.change();

            stamp = System.currentTimeMillis();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startListener() {

        restParams();
        mSensorManager.registerListener(this, mSensor,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void stopListener() {
        mSensorManager.unregisterListener(this, mSensor);
    }

    private void restParams() {
        threshold = false;
        mX = 0;
        mY = 0;
        mZ = 0;
    }

    /***
     * 注册加速度传感器回调
     * @param sensorChange
     */
    public SensorManager registerListener(SensorChange sensorChange) {
        this.sensorChange = sensorChange;
        return this;
    }

    public static interface SensorChange {
        void change();
    }

}
