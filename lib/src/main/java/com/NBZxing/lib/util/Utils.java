package com.NBZxing.lib.util;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * @author Cuizhen
 */
public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    public static void init(Context context) {
        Utils.context = context;
//        WaterMarkUtils.init(R.drawable-xhdpi.mark);
    }

    public static Context getAppContext() {
        if (context == null) {
            throw new RuntimeException("未在Application中初始化");
        }
        return context;
    }
}
