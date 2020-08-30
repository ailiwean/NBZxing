package com.ailiwean.core.view

import android.view.View

/**
 * @Package:        com.ailiwean.core.view
 * @ClassName:      FreeInterface
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/8/30 6:03 PM
 */
interface FreeInterface {

    /***
     * 提供一个扫码区域View, 将根据这个View剪裁数据
     */
    fun provideParseRectView(): View?

    /***
     * 提供一个扫描条View, 需实现[ScanBarCallBack]
     */
    fun provideScanBarView(): ScanBarCallBack?

    /***
     * 提供一个手电筒View,需实现[ScanLightViewCallBack]
     */
    fun provideLightView(): ScanLightViewCallBack?

    /***
     * 提供一个定位点View, 需实现[ScanLocViewCallBack]
     */
    fun provideLocView(): ScanLocViewCallBack?


}