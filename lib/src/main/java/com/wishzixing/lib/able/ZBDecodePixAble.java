package com.wishzixing.lib.able;

import android.graphics.Rect;
import android.hardware.Camera;
import android.text.TextUtils;
import android.widget.Toast;

import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.core.zbar.Image;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.util.Utils;
import com.wishzixing.lib.util.ZBarScannerUtils;

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

        Image barcode = new Image(y, x, "Y800");
        barcode.setData(data);

        Rect parseRect = CameraConfig.getInstance().getFramingRect();
        barcode.setCrop(parseRect.left, parseRect.top, parseRect.right, parseRect.bottom);

        String resultStr = ZBarScannerUtils.getInstance().scanImage(barcode);

        if (!TextUtils.isEmpty(resultStr))
            Toast.makeText(Utils.getAppContext(), resultStr, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void stop() {

    }
}
