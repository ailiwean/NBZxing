package com.wishzixing.lib.able;

import android.hardware.Camera;
import android.os.Message;
import android.util.Log;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.util.RxQrBarParseUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *  通往解码的桥梁
 */
public class DecodePixAble implements PixsValuesCus {

    @Override
    public void cusAction(final byte[] data, Camera camera, final int x, final int y) {

        Result result = RxQrBarParseUtils.getInstance().decodeFromByte(data);
        if (result != null) {
            Log.e("解析成功", "解析成功");
            Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_succeeded, result);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_failed);
            if (message.getTarget() != null)
                message.sendToTarget();
        }
    }

    @Override
    public void stop() {

    }

    private DecodePixAble() {
    }

    private static class Holder {
        static DecodePixAble INSTANCE = new DecodePixAble();
    }

    public static DecodePixAble getInstance() {
        return Holder.INSTANCE;
    }

}
