package com.wishzixing.lib.able;

import android.hardware.Camera;
import android.os.Message;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.util.RxBeepUtils;
import com.wishzixing.lib.util.RxQrBarParseUtils;

import java.util.ArrayList;
import java.util.List;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *  通往解码的桥梁
 */
public class DecodePixAble implements PixsValuesCus {

    List<String> scanResult = new ArrayList<>();

    @Override
    public void cusAction(final byte[] data, Camera camera, final int x, final int y) {

        Result result = RxQrBarParseUtils.getInstance().decodeFromByte(data);

        if (result != null) {

            if (scanResult.contains(result.getText()))
                return;

            Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_succeeded, result);
            message.sendToTarget();

            if (CameraConfig.getInstance().isBeep())
                RxBeepUtils.playBeep();

            if (CameraConfig.getInstance().isVibration())
                RxBeepUtils.playVibrate();

            if (CameraConfig.getInstance().isJustOne())
                scanResult.add(result.getText());

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
