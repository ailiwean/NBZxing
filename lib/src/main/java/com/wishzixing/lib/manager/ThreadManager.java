package com.wishzixing.lib.manager;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***
 *  Created by SWY
 *  DATE 2019/6/15
 *
 */
public class ThreadManager {

    ThreadPoolExecutor executor;

    private ThreadManager() {
        

        executor = new ThreadPoolExecutor(2, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50));
        //任务拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private static class Holder {
        static ThreadManager INSTANCE = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addTask(final Runnable runnable) {

        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        if (totalMemory < maxMemory * 0.8) {
            executor.execute(runnable);
        }
    }

    public void close() {
        executor.shutdown();
    }
}
