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

    private final boolean isImportant;
    private final Runnable runnable;
    final int type;

    private TypeRunnable(int type, boolean isImportant, Runnable runnable) {
        this.type = type;
        this.runnable = runnable;
        this.isImportant = isImportant;
    }

    public static TypeRunnable create(boolean isImportant, @IntRange(from = 0, to = 1) int type, Runnable runnable) {
        return new TypeRunnable(type, isImportant, runnable);
    }

    @Override
    public void run() {
        runnable.run();
    }

    public int getType() {
        return type;
    }

    public boolean isImportant() {
        return isImportant;
    }
}
