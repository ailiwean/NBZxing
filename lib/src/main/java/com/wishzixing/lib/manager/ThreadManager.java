package com.wishzixing.lib.manager;

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
        executor = new ThreadPoolExecutor(2, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private static class Holder {
        static ThreadManager INSTANCE = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addTask(Runnable runnable) {
        executor.execute(runnable);
    }

}
