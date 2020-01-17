package com.NBZxing.lib.able;

import android.hardware.Camera;
import android.os.Message;

import com.google.zxing.Result;
import com.NBZxing.lib.R;
import com.NBZxing.lib.handler.CameraCoordinateHandler;
import com.NBZxing.lib.manager.PixsValuesCus;
import com.NBZxing.lib.util.RxQrBarParseUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *  通往解码的桥梁
 */
public class ZXDecodePixAble implements PixsValuesCus {


    @Override
    public void cusAction(final byte[] data, Camera camera, final int x, final int y) {

        Result result = RxQrBarParseUtils.getInstance().decodeFromByte(data);

        if (result != null) {
            Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_succeeded, result);
            message.sendToTarget();

        }
    }

    @Override
    public void stop() {

    }

    private ZXDecodePixAble() {
    }

    private static class Holder {
        static ZXDecodePixAble INSTANCE = new ZXDecodePixAble();
    }

    public static ZXDecodePixAble getInstance() {
        return Holder.INSTANCE;
    }

}
