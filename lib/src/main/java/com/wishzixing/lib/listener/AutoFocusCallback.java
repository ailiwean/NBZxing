package com.wishzixing.lib.listener;

import android.hardware.Camera;
import android.os.Message;
import android.util.Log;

import com.wishzixing.lib.R;
import com.wishzixing.lib.handler.CameraCoordinateHandler;

/***
 *  Created by SWY
 *  DATE 2019/6/2
 *  当完成一个自动聚焦活动时调用它
 */
public class AutoFocusCallback implements Camera.AutoFocusCallback {

    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

    public void onAutoFocus(boolean success, Camera camera) {
        Message message = CameraCoordinateHandler.getInstance().obtainMessage(R.id.auto_focus, success);
        CameraCoordinateHandler.getInstance().sendMessageAtTime(message, AUTOFOCUS_INTERVAL_MS);
    }

}
