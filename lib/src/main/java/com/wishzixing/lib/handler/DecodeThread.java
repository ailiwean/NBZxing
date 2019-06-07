package com.wishzixing.lib.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.util.RxQrBarParseTool;

import java.util.concurrent.CountDownLatch;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 *
 */
public class DecodeThread extends Thread {

    private final CountDownLatch handlerInitLatch;

    Handler decodeHandler;

    private DecodeThread() {
        handlerInitLatch = new CountDownLatch(1);
    }

    public static class Holder {
        static DecodeThread decodeThread = new DecodeThread();
    }

    public static DecodeThread getInstance() {
        return Holder.decodeThread;
    }

    public Handler getHandler() {

        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return decodeHandler;
    }


    @Override
    public void run() {
        super.run();
        Looper.prepare();
        decodeHandler = DecodeHandler.getInstance();
        handlerInitLatch.countDown();
        Looper.loop();
    }

    /***
     *  Created by SWY
     *  DATE 2019/6/3
     *  只负责解析字节数据并回调CameraCoordinateHandler
     *
     */
    private static class DecodeHandler extends Handler {

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

}
