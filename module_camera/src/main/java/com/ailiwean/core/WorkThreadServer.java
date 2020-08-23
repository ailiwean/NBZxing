package com.ailiwean.core;

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

    private ThreadPoolExecutor executor;

    private WorkThreadServer() {
        boolean hasGrayScale;
        try {
            Class.forName(Config.GARY_SCALE_PATH);
            hasGrayScale = true;
        } catch (ClassNotFoundException e) {
            hasGrayScale = false;
        }

        if (!hasGrayScale)
            executor = new ThreadPoolExecutor(
                    corePoolSize, corePoolSize, keepAliveTime, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(maximumPoolSize, true),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        else executor = new RespectScalePool(
                corePoolSize, corePoolSize, keepAliveTime, TimeUnit.SECONDS,
                RespectScaleQueue.create(maximumPoolSize / 3 * 2, maximumPoolSize / 3),
                new RespectScalePool.RespectScalePolicy());
    }

    //参数初始化
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量大小
    private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //线程池最大容纳线程数
    private static final int maximumPoolSize = CPU_COUNT * 2 + 1;
    //线程空闲后的存活时长
    private static final int keepAliveTime = 30;

    public static WorkThreadServer createInstance() {
        return new WorkThreadServer();
    }

    public void post(Runnable runnable) {
        if (executor != null)
            executor.execute(runnable);
    }

    public void quit() {
        if (executor != null) {
            executor.shutdown();
            executor.getQueue().clear();
            executor = null;
        }
    }

    public void clear() {
        if (executor != null)
            executor.getQueue().clear();
    }

}
