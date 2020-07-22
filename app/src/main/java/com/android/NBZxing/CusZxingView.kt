package com.android.NBZxing

import android.content.Context
import android.os.Environment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.ailiwean.core.view.ZxingCameraView
import com.ailiwean.core.zxing.ScanTypeConfig

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
     * 可扩展顶层View
     */
    override fun provideFloorView(): View? {
        val v = LayoutInflater.from(context)
                .inflate(R.layout.floorview_layout, this, false)

        v.findViewById<View>(R.id.img)
                .setOnClickListener {
                    parseFile(Environment.getExternalStorageDirectory().absolutePath + "/scan.jpg")
                }
        return v
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

}