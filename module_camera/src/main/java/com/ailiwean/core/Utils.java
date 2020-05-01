package com.ailiwean.core;

import android.content.Context;
import android.util.TypedValue;

import com.bumptech.glide.util.Util;

import java.lang.ref.WeakReference;

/**
 * @Package: com.ailiwean.core
 * @ClassName: Uitls
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 11:10 AM
 */
public class Utils {

    static WeakReference<Context> holder;

    public static void init(Context mContext) {
        holder = new WeakReference<>(mContext);
    }

    public static Context getContext() {
        return holder.get();
    }

    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, Utils.getContext().getResources().getDisplayMetrics());
    }

}
