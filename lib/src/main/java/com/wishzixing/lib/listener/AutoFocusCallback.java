package com.wishzixing.lib.listener;

import android.hardware.Camera;

/***
 *  Created by SWY
 *  DATE 2019/6/2
 *  当完成一个自动聚焦活动时调用它
 */
public class AutoFocusCallback implements Camera.AutoFocusCallback {

    private AutoFocusCallback() {
    }

    private static class Holder {
        static AutoFocusCallback INSTANCE = new AutoFocusCallback();
    }

    public static AutoFocusCallback getInstance() {
        return Holder.INSTANCE;
    }

    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

    public void onAutoFocus(boolean success, Camera camera) {

    }

}
