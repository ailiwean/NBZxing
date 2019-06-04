package com.wishzixing.lib.handler;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.util.RxQrBarParseTool;


/***
 *  Created by SWY
 *  DATE 2019/6/3
 *  只负责解析字节数据并回调CameraCoordinateHandler
 *
 */
public class DecodeHandler extends Handler {


    private DecodeHandler() {
    }

    private static class Holder {
        static DecodeHandler decodeHandler = new DecodeHandler();
    }

    public static DecodeHandler getInstance() {
        return Holder.decodeHandler;
    }

    @Override
    public void handleMessage(Message message) {

        Result result = RxQrBarParseTool.getInstance().decodeFromByte((byte[]) message.obj, message.arg1, message.arg2);
        sendResultMessage(result);
    }

    private void sendResultMessage(Result rawResult) {

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
