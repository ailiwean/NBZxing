package com.wishzixing.lib.able;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.manager.ThreadManager;
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
    public void cusAction(final byte[] data, Camera camera, final int x, final int y) {

        ThreadManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Result result = RxQrBarParseUtils.getInstance().decodeFromByte((data), x, y);
                if (result != null) {
                    Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_succeeded, result);
                    message.sendToTarget();
                } else {
                    Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.decode_failed);
                    if (message.getTarget() != null)
                        message.sendToTarget();
                }
            }
        });

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
            sendResultMessage((Result) message.obj);
        }

        private void sendResultMessage(Result rawResult) {


        }
    }
}
