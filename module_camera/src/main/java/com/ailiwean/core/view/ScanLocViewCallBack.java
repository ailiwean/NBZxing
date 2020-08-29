package com.ailiwean.core.view;

import android.graphics.PointF;

/**
 * @Package: com.ailiwean.core.view
 * @ClassName: LocationCallBack
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/29 3:32 PM
 */
public interface ScanLocViewCallBack extends CameraStarLater {

    /***
     * @param qrPoint 二维码中心位置坐标，相对于FreeZxingView
     * @param runnable 扫码结果回调
     */
    void toLocation(PointF qrPoint, Runnable runnable);

}
