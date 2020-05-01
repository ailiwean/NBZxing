package com.ailiwean.core.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ailiwean.core.Config
import com.ailiwean.core.Utils
import com.ailiwean.core.able.AbleManager
import com.ailiwean.core.helper.VibrateHelper
import com.ailiwean.core.helper.ScanHelper
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.BaseCameraView
import com.google.android.cameraview.CameraView
import com.google.android.cameraview.R
import com.google.zxing.Result
import kotlinx.android.synthetic.main.base_zxing_layout.view.*

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      ZxingCamera
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/19 12:38 AM
 */
abstract class ZxingCameraView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        BaseCameraView(context, attributeSet, def) {

    init {
        facing = FACING_BACK
        this.setAspectRatio(AspectRatio.of(16, 9))
        this.addView(LayoutInflater.from(context).inflate(R.layout.base_zxing_layout, this, false)
                , ViewGroup.LayoutParams(-1, -1))

        //注册打开关闭闪光灯点击事件
        lightView.regLightClick {
            lightOperator(it)
        }
    }

    private val handleZX = HandleZX {
        when (it.what) {
            Config.SCAN_RESULT -> {
                scanSucHelper()
                if (it.obj is Result) {
                    showQRLoc(ScanHelper.rotatePoint((it.obj as Result).resultPoints)
                            , it.obj.toString()
                    )
                }
            }
            Config.LIGHT_CHANGE -> {
                lightView.setBright(it.obj.toString().toBoolean())
            }

            Config.AUTO_ZOOM -> {
                setZoom(it.obj.toString().toFloat())
            }
        }
    }

    private lateinit var ableCollect: AbleManager

    override fun onCameraOpen(camera: CameraView) {
        super.onCameraOpen(camera)
        initConfig()
    }

    override fun onCameraClose(camera: CameraView) {
        super.onCameraClose(camera)
    }

    override fun onPreviewByte(camera: CameraView, data: ByteArray) {
        super.onPreviewByte(camera, data)
        val dataWidht = Config.scanRect.dataX
        val dataHeight = Config.scanRect.dataY
        ableCollect.cusAction(data, dataWidht, dataHeight)
    }


    /***
     * 扫码成功后的一些动作
     */
    fun scanSucHelper() {
        ableCollect.release()
        stop()
        VibrateHelper.playVibrate()
        VibrateHelper.playBeep()
        scan_bar.stopAnim()
    }

    /***
     * 显示二维码位置, 动画播放完回调扫描结果
     */
    fun showQRLoc(point: PointF, content: String) {
        qr_loc.visibility = View.VISIBLE
        qr_loc.translationX = point.x - Utils.dp2px(25f)
        qr_loc.translationY = point.y - Utils.dp2px(25f)
        qr_loc.scaleX = 0f
        qr_loc.scaleY = 0f
        qr_loc.animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        resultBack(content)
                    }
                })
                .start()
    }

    /***
     * 相机启动数据初始化
     */
    fun initConfig() {
        qr_loc.visibility = View.GONE
        scan_bar.startAnim()
        ableCollect = AbleManager.getInstance(handleZX)
        //闪光灯按钮初始化
        lightView.inital()
    }

    /***
     * 扫码结果回调
     */
    abstract fun resultBack(content: String)

    class HandleZX constructor(val callback: (Message) -> Unit) : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            msg?.apply {
                callback(msg)
            }
        }
    }

    @SuppressLint("WrongViewCast")
    fun getView(): View {
        return findViewById(R.id.base_floor)
    }

}