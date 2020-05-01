package com.google.android.cameraview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import com.ailiwean.core.Config
import com.ailiwean.core.helper.ZoomHelper
import com.ailiwean.core.view.LifeOwner
import com.ailiwean.core.Utils
import com.ailiwean.core.WorkThreadServer

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      BaseCameraView
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/19 12:02 AM
 */
abstract class BaseCameraView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        CameraView(context, attributeSet, def) {

    val hand = Handler()

    init {
        Utils.init(context)
        autoFocus = true
        adjustViewBounds = false
        this.setAspectRatio(AspectRatio.of(4, 3))
        this.addCallback(object : Callback() {

            var hasFloorView = false

            override fun onCameraOpened(cameraView: CameraView) {
                hand.post {
                    if (!hasFloorView) {
                        provideFloorView()?.let {
                            this@BaseCameraView.addView(it, ViewGroup.LayoutParams(-1, -1))
                        }
                        hasFloorView = true
                    }
                    onCameraOpen(cameraView)
                }
            }

            override fun onCameraClosed(cameraView: CameraView) {
                hand.post {
                    onCameraClose(cameraView)
                }
            }

            override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
                hand.post {
                    onPictureTake(cameraView, data)
                }
            }

            override fun onPreviewByte(cameraView: CameraView, data: ByteArray?) {
                if (data != null)
                    this@BaseCameraView.onPreviewByte(cameraView, data)
            }

        })
    }

    open fun onCameraOpen(camera: CameraView) {
        ZoomHelper.toAutoZoom(this)
    }

    open fun onCameraClose(camera: CameraView) {
        ZoomHelper.close(this)
    }

    open fun onPictureTake(camera: CameraView, data: ByteArray) {
    }

    open fun onPreviewByte(camera: CameraView, data: ByteArray) {
    }

    abstract fun provideFloorView(): View?

    /***
     * 绑定AppCompatActivity生命周期并启动相机
     */
    fun synchLifeStart(appCompatActivity: AppCompatActivity) {
        appCompatActivity.lifecycle.addObserver(object : LifeOwner {
            //在onCreate()中调用提升相机打开速度
            override fun onCreate() {
                WorkThreadServer.getInstance().bgHandle.post {
                    start()
                }
            }

            //保证避免多次调用start()
            var isSingleLoad = false

            override fun onResume() {
                if (!isSingleLoad) {
                    isSingleLoad = true
                    return
                }
                start()
            }

            override fun onPause() {
                stop()
            }

            override fun onDestroy() {
            }
        })
    }

    /***
     * 数字变焦
     */
    fun setZoom(@FloatRange(from = 0.0, to = 1.0) percent: Float) {
        if (percent == 1f)
            mImpl.toZoomMax()
        else if (percent == 0f)
            mImpl.toZoomMin()
        else mImpl.setZoom(percent)
        //捕获当前倍率
        Config.currentZoom = percent
    }


    /***
     * 打开/关闭 闪光灯
     */
    fun lightOperator(isOpen: Boolean) {
        mImpl.lightOperator(isOpen)
    }

}