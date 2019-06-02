package com.wishzixing.lib.listener;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.wishzixing.lib.config.CameraConfig;

/***
 *  Created by SWY
 *  DATE 2019/6/2
 *
 */
public class PreviewCallback implements Camera.PreviewCallback {
    private final boolean useOneShotPreviewCallback;
    private Handler previewHandler;
    private int previewMessage;

    public PreviewCallback(boolean useOneShotPreviewCallback) {
        this.useOneShotPreviewCallback = useOneShotPreviewCallback;

    }

    public void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = CameraConfig.getInstance().getCameraPoint();
        if (!useOneShotPreviewCallback) {
            camera.setPreviewCallback(null);
        }
        if (previewHandler != null) {
            Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x,
                    cameraResolution.y, data);
            message.sendToTarget();
            previewHandler = null;

        }
    }
}
