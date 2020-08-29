package com.ailiwean.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.google.android.cameraview.R
import kotlinx.android.synthetic.main.nbzxing_default_floorview.view.*

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      ZxingCamera
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/19 12:38 AM
 */
open class NBZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        FreeZxingView(context, attributeSet, def) {

    override fun resultBack(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    override fun provideFloorView(): Int {
        return R.layout.nbzxing_default_floorview
    }

    override fun provideParseRectView(): View {
        return scanRectView
    }

    override fun provideLightView(): ScanLightViewCallBack {
        return lightView
    }

    override fun provideLocView(): ScanLocViewCallBack {
        return locView
    }

    override fun provideScanBarView(): ScanBarCallBack {
        return scanBarView
    }

}