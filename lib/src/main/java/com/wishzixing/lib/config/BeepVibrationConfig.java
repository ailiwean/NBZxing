package com.wishzixing.lib.config;

/***
 *  Created by SWY
 *  DATE 2019/6/22
 *
 */
public class BeepVibrationConfig {

    //成功是否播放音效
    boolean isBeep = true;

    //成功是否震动提醒
    boolean isVibration = true;

    private BeepVibrationConfig() {

    }

    public static class Holder {
        static BeepVibrationConfig INSTANCE = new BeepVibrationConfig();
    }

    public static BeepVibrationConfig getInstance() {
        return new BeepVibrationConfig();
    }

    public BeepVibrationConfig setBeep(boolean beep) {
        isBeep = beep;
        return this;
    }

    public BeepVibrationConfig setVibration(boolean vibration) {
        isVibration = vibration;
        return this;
    }

    public void go() {
        CameraConfig.getInstance().isBeep = this.isBeep;
        CameraConfig.getInstance().isVibration = this.isVibration;
    }


}
