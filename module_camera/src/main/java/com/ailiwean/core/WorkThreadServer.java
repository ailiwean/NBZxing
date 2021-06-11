package com.ailiwean.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Package: com.ailiwean.core
 * @ClassName: WorkThreadServer
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/19 5:46 PM
 */
public class WorkThreadServer {

    private ThreadPoolExecutor executor;

    private WorkThreadServer() {
        if (!Config.hasDepencidesScale())
            executor = new ThreadPoolExecutor(
                    corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(queueMaxSize, true),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        else executor = new RespectScalePool(
                corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                RespectScaleQueue.create(queueMaxSize, 1),
                new RespectScalePool.RespectScalePolicy());
    }

    // private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量大小
    private static final int corePoolSize = 2;
    //线程池最大容纳线程数
    private static final int maximumPoolSize = 3;
    //线程池队列长度
    private static final int queueMaxSize = 4;
    //线程空闲后的存活时长
    private static final int keepAliveTime = 30;

    public static WorkThreadServer createInstance() {
        return new WorkThreadServer();
    }

    public void post(TypeRunnable typeRunnable) {
        if (executor != null)
            executor.execute(typeRunnable);
    }

    public void quit() {
        if (executor != null) {
            executor.shutdownNow();
            executor.getQueue().clear();
            executor = null;
        }
    }

    public void clear() {
        if (executor != null)
            executor.getQueue().clear();
    }
}
