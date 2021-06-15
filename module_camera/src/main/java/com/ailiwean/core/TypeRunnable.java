package com.ailiwean.core;

import androidx.annotation.IntDef;

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
    public static final int OTHER = 2;
    private final Runnable runnable;
    final int type;

    private TypeRunnable(int type,  Runnable runnable) {
        this.type = type;
        this.runnable = runnable;
    }

    public static TypeRunnable create(@Range int type,
                                      Runnable runnable) {
        return new TypeRunnable(type, runnable);
    }

    @Override
    public void run() {
        runnable.run();
    }

    public int getType() {
        return type;
    }

    @IntDef(value = {NORMAL, SCALE, OTHER})
    public @interface Range {
    }
}
