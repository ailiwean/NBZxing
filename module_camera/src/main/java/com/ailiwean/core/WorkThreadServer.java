package com.ailiwean.core;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Package: com.ailiwean.core
 * @ClassName: WorkThreadServer
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/19 5:46 PM
 */
public class WorkThreadServer {

    private HandlerThread handlerThread;

    private Handler handler;

    private ThreadPoolExecutor executor;

    private WorkThreadServer() {
    }

    private static class Holder {
        static WorkThreadServer INSTANCE = new WorkThreadServer();
    }

    public static WorkThreadServer getInstance() {
        if (Holder.INSTANCE.executor == null) {
            Holder.INSTANCE.executor = new ThreadPoolExecutor(
                    2, 5, 1, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(20, true), new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        if (Holder.INSTANCE.handlerThread == null) {
            Holder.INSTANCE.handlerThread = new HandlerThread("Default");
            Holder.INSTANCE.handlerThread.start();
            Holder.INSTANCE.handler = new Handler(Holder.INSTANCE.handlerThread.getLooper());
        }
        return Holder.INSTANCE;
    }

    public Handler getBgHandle() {
        return handler;
    }

    public void post(Runnable runnable) {
        if (executor != null)
            executor.execute(runnable);
    }

    public void quit() {
        if (handler != null) {
            handlerThread.quit();
            handlerThread = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
