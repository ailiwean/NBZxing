package com.NBZxing.lib.config;

import android.support.annotation.IntDef;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class AutoFocusConfig {

    private int autoModel = Hybride;

    private long timeThreshold = 1000;

    private boolean isUser = false;

    private AutoFocusConfig() {

    }

    private static class Holder {
        static AutoFocusConfig INSTANCE = new AutoFocusConfig();
    }

    public static AutoFocusConfig getInstance() {
        return Holder.INSTANCE;
    }

    public AutoFocusConfig setModel(@Type int type) {
        this.autoModel = type;
        return this;
    }

    public AutoFocusConfig setTimeThreshold(long timeThreshold) {
        this.timeThreshold = timeThreshold;
        return this;
    }

    public AutoFocusConfig setUser(boolean user) {
        isUser = user;
        return this;
    }

    public void go() {
        CameraConfig.getInstance().autoFocusModel = this.autoModel;
        CameraConfig.getInstance().timeThreshold = this.timeThreshold;
        CameraConfig.getInstance().isUserSpareFocus = this.isUser;
    }

    public static final int TIME = 1;
    public static final int SENSOR = 2;
    public static final int PIXVALUES = 3;
    public static final int Hybride = 4;

    @IntDef({TIME, SENSOR, PIXVALUES, Hybride})
    public static @interface Type {

    }
}
