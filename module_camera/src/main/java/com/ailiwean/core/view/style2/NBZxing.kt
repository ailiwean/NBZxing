package com.ailiwean.core.view.style2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ailiwean.core.Result
import com.ailiwean.core.view.FreeZxingView
import com.ailiwean.core.view.ScanBarCallBack
import com.ailiwean.core.view.ScanLightViewCallBack
import com.ailiwean.core.view.ScanLocViewCallBack
import com.google.android.cameraview.R
import kotlinx.android.synthetic.main.nbzxing_style2_floorview.view.*

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      ZxingCamera
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/19 12:38 AM
 */
open class NBZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        FreeZxingView(context, attributeSet, def) {

    override fun resultBack(content: Result) {

    }

    override fun provideFloorView(): Int {
        return R.layout.nbzxing_style2_floorview
    }

    override fun provideParseRectView(): View? {
        return scanRectView
    }

    override fun provideLightView(): ScanLightViewCallBack? {
        return lightView
    }

    override fun provideLocView(): ScanLocViewCallBack? {
        return locView
    }

    override fun provideScanBarView(): ScanBarCallBack? {
        return null
    }

}