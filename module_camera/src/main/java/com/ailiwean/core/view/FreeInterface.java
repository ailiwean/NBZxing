package com.ailiwean.core.view;

import android.view.View;

/**
 * @Package: com.ailiwean.core.view
 * @ClassName: FreeInterface
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/23 10:45 PM
 */
public interface FreeInterface {

    /***
     * 提供一个扫码区域View, 将根据这个View剪裁数据
     */
    View provideParseRectView();

    /***
     * 提供一个扫描条View, 需实现{@link ScanBarCallBack}
     */
    ScanBarCallBack provideScanBarView();

    /***
     * 提供一个手电筒View,需实现{@link ScanLightViewCallBack}
     */
    ScanLightViewCallBack provideLightView();

    /***
     * 提供一个定位点View, 需实现{@link ScanLocViewCallBack}
     */
    ScanLocViewCallBack provideLocView();


}