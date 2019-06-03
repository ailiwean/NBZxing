package com.wishzixing.lib.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.util.RxQrBarParseTool;


/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 */
public class DecodHandler extends Handler {


    private DecodHandler() {
    }

    private static class Holder {
        static DecodHandler decodHandler = new DecodHandler();
    }

    public static DecodHandler getInstance() {
        return Holder.decodHandler;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.decode) {

            Result result = RxQrBarParseTool.getInstance().decodeFromByte((byte[]) message.obj, message.arg1, message.arg2);
            sendMessage(result);

        } else if (message.what == R.id.quit) {
            Looper.myLooper().quit();
        }
    }

    private void sendMessage(Result rawResult) {

        if (rawResult != null) {
            Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_failed);
            if (message.getTarget() != null)
                message.sendToTarget();
        }

    }

}
