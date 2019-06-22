package com.wishzixing.lib.config;

import android.support.annotation.IntDef;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class AutoFocusConfig {

    private int autoModel = SENSOR;

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

    public void go() {
        CameraConfig.getInstance().autoFocusModel = autoModel;
    }

    public static final int TIME = 1;
    public static final int SENSOR = 2;
    public static final int PIXVALUES = 3;

    @IntDef({TIME, SENSOR, PIXVALUES})
    public static @interface Type {

    }
}
