package com.ailiwean.core.view;

import android.graphics.PointF;

import com.ailiwean.core.Result;

/**
 * @Package: com.ailiwean.core.view
 * @ClassName: LocationCallBack
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/29 3:32 PM
 */
public interface ScanLocViewCallBack extends CameraStarLater {

    /***
     * @param result 扫码结果
     * @param runnable 扫码结果回调
     */
    void toLocation(Result result, Runnable runnable);

}
