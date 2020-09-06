package com.android.NBZxing

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.ailiwean.core.view.*
import com.ailiwean.core.zxing.ScanTypeConfig
import com.google.android.cameraview.AspectRatio
import kotlinx.android.synthetic.main.floorview_layout.view.*


/**
 * @Package:        com.android.NBZXing
 * @ClassName:      CusZxing
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/30 4:06 PM
 */
class CusScanView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : FreeZxingView(context, attributeSet, def) {

    init {
        setAspectRatio(AspectRatio.of(16, 9))
    }

    override fun resultBack(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    /***
     * 返回扫码类型
     * 1 ScanTypeConfig.HIGH_FREQUENCY 高频率格式(默认)
     * 2 ScanTypeConfig.ALL  所有格式
     * 3 ScanTypeConfig.ONLY_QR_CODE 仅QR_CODE格式
     * 4 ScanTypeConfig.TWO_DIMENSION 所有二维码格式
     * 5 ScanTypeConfig.ONE_DIMENSION 所有一维码格式
     */
    override fun getScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    fun toParse(string: String) {
        parseFile(string)
    }

    override fun resultBackFile(content: String) {
        if (content.isEmpty())
            Toast.makeText(context, "未扫描到内容", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    override fun provideFloorView(): Int {
        return R.layout.floorview_layout
    }


    override fun provideParseRectView(): View? {
        return scanRectView
    }

    override fun provideScanBarView(): ScanBarCallBack? {
        return scanBarView
    }

    override fun provideLightView(): ScanLightViewCallBack? {
        return lightView
    }

    override fun provideLocView(): ScanLocViewCallBack? {
        return locView
    }


}