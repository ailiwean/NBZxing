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
     * 可扩展顶层View
     */
    override fun provideFloorView(): View? {
        val v = LayoutInflater.from(context)
                .inflate(R.layout.tool_title, this, false)

        v.findViewById<View>(R.id.vToolBar)
                .setBackgroundColor(Color.parseColor("#2f000000"))

        v.findViewById<TextView>(R.id.vTitle).text = "扫一扫"

        v.findViewById<View>(R.id.vLeftImage)
                .setOnClickListener { v: View? ->
                    if (context is Activity) {
                        (context as Activity).finish()
                    }
                }

        v.findViewById<TextView>(R.id.vRightTextView).text = "相册"
        v.findViewById<TextView>(R.id.vRightTextView)
                .setOnClickListener { v: View? ->
                    if (!checkPermissionRW()) {
                        requstPermissionRW()
                        return@setOnClickListener
                    }
                    if (context is Activity) {
                        Matisse.from(context as Activity)
                                .choose(MimeType.ofAll())
                                .countable(true)
                                .maxSelectable(9)
                                .gridExpectedSize(300)
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                .thumbnailScale(0.85f)
                                .imageEngine(GlideEngine())
                                .showPreview(false) // Default is `true`
                                .forResult(1)
                    }

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

    fun toParse(string: String) {
        parseFile(string)
    }


    fun checkPermissionRW(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            context.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        } else {
            return true
        }
    }


    fun requstPermissionRW() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as? Activity)?.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 200)
        }
    }

}