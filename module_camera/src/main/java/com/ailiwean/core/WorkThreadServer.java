package com.ailiwean.core;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.ArrayBlockingQueue;
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


    private static WorkThreadServer INSTANCE = new WorkThreadServer();

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

    public static WorkThreadServer getInstance() {

        if (INSTANCE.executor == null) {
            synchronized (WorkThreadServer.class) {
                if (INSTANCE.executor == null) {
                    INSTANCE.executor = new ThreadPoolExecutor(
                            corePoolSize, corePoolSize, keepAliveTime, TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(maximumPoolSize, true), new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        if (INSTANCE.handlerThread == null) {
            synchronized (WorkThreadServer.class) {
                if (INSTANCE.handlerThread == null) {
                    INSTANCE.handlerThread = new HandlerThread("Default");
                    INSTANCE.handlerThread.start();
                    INSTANCE.handler = new Handler(INSTANCE.handlerThread.getLooper());
                }
            }
        }
        return INSTANCE;
    }

    public Handler getBgHandle() {
        return INSTANCE.handler;
    }

    public void post(Runnable runnable) {
        if (INSTANCE.executor != null)
            INSTANCE.executor.execute(runnable);
    }

    public static void quit() {
        if (INSTANCE.handlerThread != null) {
            INSTANCE.handlerThread.quit();
            INSTANCE.handlerThread = null;
        }
        if (INSTANCE.executor != null) {
            INSTANCE.executor.shutdown();
            INSTANCE.executor = null;
        }
    }
}
