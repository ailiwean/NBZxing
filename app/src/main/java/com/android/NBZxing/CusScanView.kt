package com.android.NBZxing

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import com.ailiwean.core.Result
import com.ailiwean.core.view.style1.NBZxingView
import com.ailiwean.core.zxing.ScanTypeConfig
import com.google.android.cameraview.AspectRatio


/**
 * @Package:        com.android.NBZXing
 * @ClassName:      CusZxing
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/30 4:06 PM
 */
class CusScanView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : NBZxingView(context, attributeSet, def) {

    override fun resultBack(content: Result) {
        Toast.makeText(context, content.text, Toast.LENGTH_LONG).show()
    }

    /***
     * 返回扫码类型
     * 1 ScanTypeConfig.HIGH_FREQUENCY 高频率格式(默认)
     * 2 ScanTypeConfig.ALL  所有格式
     * 3 ScanTypeConfig.ONLY_QR_CODE 仅QR_CODE格式
     * 4 ScanTypeConfig.TWO_DIMENSION 所有二维码格式
     * 5 ScanTypeConfig.ONE_DIMENSION 所有一维码格式
     */
    override fun configScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    fun toParse(string: String) {
        parseFile(string)
    }

    override fun provideAspectRatio(): AspectRatio {
        return AspectRatio.of(16, 9)
    }

    override fun resultBackFile(content: String) {
        if (content.isEmpty())
            Toast.makeText(context, "未扫描到内容", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    /***
     *  是否支持黑边二维码扫描
     */
    override fun isSupportBlackEdgeQrScan(): Boolean {
        return true
    }

}