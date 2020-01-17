package com.NBZxing.lib.able;

import android.hardware.Camera;

import com.NBZxing.lib.manager.PixsValuesCus;

/***
 *  Created by SWY
 *  DATE 2019/7/7
 *
 */
public class ZBDecodePixAble implements PixsValuesCus {

    private ZBDecodePixAble() {

    }

    private static class Holder {
        static ZBDecodePixAble INSTANCE = new ZBDecodePixAble();
    }

    public static ZBDecodePixAble getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void cusAction(byte[] data, Camera camera, int x, int y) {

//        Image barcode = new Image(x, y, "Y800");
//        barcode.setData(data);
//
//        Rect parseRect = CameraConfig.getInstance().getFramingRect();
//        barcode.setCrop(parseRect.left, parseRect.top, parseRect.right, parseRect.bottom);
//
//        String resultStr = ZBarScannerUtils.getInstance().scanImage(barcode);
//
//        if (!TextUtils.isEmpty(resultStr)) {
//            Log.e(resultStr, resultStr);
//        }
    }

    @Override
    public void stop() {

    }
}
