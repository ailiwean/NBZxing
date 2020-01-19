package com.NBZxing.lib.manager;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***
 *  Created by SWY
 *  DATE 2019/6/15
 *
 */
public class ThreadManager {

    private static final String TAG = "ThreadPool";
    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 8;
    private static final int KEEP_ALIVE_TIME = 10; // 10 seconds

    ThreadPoolExecutor executor;

    private ThreadManager() {
        //任务拒绝策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), handler);
    }

    private static class Holder {
        static ThreadManager INSTANCE = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addTask(final Runnable runnable) {
        executor.execute(runnable);
    }

    public void close() {
        executor.shutdown();
    }
}
