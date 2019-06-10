package com.wishzixing.lib.able;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
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

    private HandlerThread handlerThread = new HandlerThread("Decode");
    private DecodeHandler decodeHandler;

    @Override
    public void cusAction(byte[] data, Camera camera, int x, int y) {
        Message message = decodeHandler.obtainMessage(R.id.decode, x,
                y, data);
        message.sendToTarget();
    }

    @Override
    public void stop() {

    }

    private DecodePixAble() {
        handlerThread.start();
        decodeHandler = new DecodeHandler(handlerThread.getLooper());
    }

    private static class Holder {
        static DecodePixAble INSTANCE = new DecodePixAble();
    }

    public static DecodePixAble getInstance() {
        return Holder.INSTANCE;
    }

    /***
     *  只负责解析字节数据并回调CameraCoordinateHandler
     */
    private static class DecodeHandler extends Handler {

        private DecodeHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {

            Result result = RxQrBarParseUtils.getInstance().decodeFromByte((byte[]) message.obj, message.arg1, message.arg2);
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
}
