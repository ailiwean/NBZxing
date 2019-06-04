package com.wishzixing.lib.listener;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Message;
import android.util.Log;

import com.wishzixing.lib.R;
import com.wishzixing.lib.able.AccountLigFieAble;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.handler.DecodeHandler;

/***
 *  Created by SWY
 *  DATE 2019/6/2
 *  传入Camera中, 当存在预览帧时回调onPreviewFrame方法
 *
 */
public class PreviewCallback implements Camera.PreviewCallback {


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        Log.e("调用", "调用");

        AccountLigFieAble.getInstance().account(data, camera);

        Point cameraResolution = CameraConfig.getInstance().getCameraPoint();
        Message message = DecodeHandler.getInstance().obtainMessage(R.id.decode, cameraResolution.x,
                cameraResolution.y, data);
        message.sendToTarget();

    }

}
