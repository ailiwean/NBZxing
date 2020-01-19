package com.NBZxing.lib.util;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * @author Cuizhen
 */
public class Utils {

    private static WeakReference<Context> weakReference = null;

    public static void init(Context context) {
        weakReference = new WeakReference<>(context);
    }

    public static Context getContext() {
        if (weakReference.get() == null) {
            throw new RuntimeException("未初始化");
        }
        return weakReference.get();
    }
}
