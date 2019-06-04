package com.wishzixing.lib.handler;

import android.os.Looper;

import java.util.concurrent.CountDownLatch;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 *
 */
public class DecodeThread extends Thread {

    private final CountDownLatch handlerInitLatch;

    private DecodeThread() {
        handlerInitLatch = new CountDownLatch(1);
    }

    public static class Holder {
        static DecodeThread decodeThread = new DecodeThread();
    }

    public static DecodeThread getInstance() {
        return Holder.decodeThread;
    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
