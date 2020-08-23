package com.android.NBZxing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ailiwean.core.view.ZxingCameraView
import com.ailiwean.core.zxing.ScanTypeConfig
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine


/**
 * @Package:        com.android.NBZXing
 * @ClassName:      CusZxing
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/30 4:06 PM
 */
class CusZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : ZxingCameraView(context, attributeSet, def) {

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
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

}