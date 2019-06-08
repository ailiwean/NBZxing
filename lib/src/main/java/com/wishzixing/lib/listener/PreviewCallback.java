package com.wishzixing.lib.listener;

import android.hardware.Camera;

import com.wishzixing.lib.manager.PixsValuesCusManager;


/***
 *  Created by SWY
 *  DATE 2019/6/2
 *  传入Camera中, 当存在预览帧时回调onPreviewFrame方法
 *
 */
public class PreviewCallback implements Camera.PreviewCallback {

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        PixsValuesCusManager.getInstance().each(data, camera);
    }

}
