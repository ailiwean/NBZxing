package com.ailiwean.core;

import androidx.annotation.IntRange;

/**
 * @Package: com.ailiwean.core
 * @ClassName: TypeRunnable
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/22 10:03 AM
 */
public class TypeRunnable implements Runnable {

    public static final int NORMAL = 0;
    public static final int SCALE = 1;

    Runnable runnable;
    int type;

    private TypeRunnable(int type, Runnable runnable) {
        this.type = type;
        this.runnable = runnable;
    }

    public static TypeRunnable create(@IntRange(from = 0, to = 1) int type, Runnable runnable) {
        return new TypeRunnable(type, runnable);
    }

    @Override
    public void run() {
        runnable.run();
    }

    public int getType() {
        return type;
    }
}
