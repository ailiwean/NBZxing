package com.ailiwean.core.view;

import android.graphics.Camera;

/**
 * @Package: com.ailiwean.core.view
 * @ClassName: LightViewCallBack
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/23 11:30 PM
 */
public interface ScanLightViewCallBack extends CameraStarLater {

    //光线变亮
    void lightBrighter();

    //光线变暗
    void lightDark();

    //闪光灯打开关闭
    void regLightOperator(Runnable open, Runnable close);

}
