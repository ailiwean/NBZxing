package com.wishzixing.lib.util;

import android.content.res.Resources;

/***
 *  Created by SWY
 *  DATE 2019/6/30
 *
 */
public class WindowUitls {

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Resources resources = Utils.getAppContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

}