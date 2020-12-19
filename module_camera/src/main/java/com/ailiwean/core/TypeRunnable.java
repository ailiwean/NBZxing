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
    public String tagId;

    private TypeRunnable(int type, boolean isImportant, Runnable runnable, String tagId) {
        this.type = type;
        this.runnable = runnable;
        this.isImportant = isImportant;
        this.tagId = tagId;
    }

    public static TypeRunnable create(boolean isImportant,
                                      @IntRange(from = 0, to = 1) int type,
                                      String tagId,
                                      Runnable runnable) {
        return new TypeRunnable(type, isImportant, runnable, tagId);
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
