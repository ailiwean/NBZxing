package com.ailiwean.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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

    //参数初始化
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量大小
    private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //线程池最大容纳线程数
    private static final int maximumPoolSize = CPU_COUNT * 2 + 1;
    //线程空闲后的存活时长
    private static final int keepAliveTime = 30;

    private static class Holder {
        static WorkThreadServer INSTANCE = new WorkThreadServer();
    }

    public static WorkThreadServer getInstance() {
        if (Holder.INSTANCE.executor == null) {
            Holder.INSTANCE.executor = new ThreadPoolExecutor(
                    corePoolSize, corePoolSize, keepAliveTime, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(maximumPoolSize, true), new ThreadPoolExecutor.DiscardOldestPolicy());
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
