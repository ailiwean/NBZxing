package com.google.android.cameraview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ailiwean.core.Config
import com.ailiwean.core.Utils
import com.ailiwean.core.helper.ZoomHelper
import com.ailiwean.core.view.LifeOwner

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      BaseCameraView
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/19 12:02 AM
 */
abstract class BaseCameraView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        CameraView(context, attributeSet, def), LifeOwner {

    init {
        Utils.init(context)
        autoFocus = true
        adjustViewBounds = false
        this.setAspectRatio(AspectRatio.of(4, 3))
        this.addCallback(object : Callback() {

            var hasFloorView = false

            override fun onCameraOpened(cameraView: CameraView) {
                mainHand.post {
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
                mainHand.post {
                    onCameraClose(cameraView)
                }
            }

            override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
                mainHand.post {
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


    //保证避免多次调用start()
    var isShoudCreateOpen = true

    /***
     * 绑定AppCompatActivity生命周期并启动相机
     */
    fun synchLifeStart(appCompatActivity: AppCompatActivity) {
        appCompatActivity.lifecycle.addObserver(this)
        appCompatActivity.lifecycle.addObserver(object : LifeOwner {
            //在onCreate()中调用提升相机打开速度
            override fun onCreate() {
                onComCreate()
            }

            override fun onResume() {
                onComResume()
            }

            override fun onPause() {
                onComPause()
            }

            override fun onStop() {

            }

            override fun onDestroy() {

            }
        })
    }

    fun synchLifeStart(fragment: Fragment) {
        fragment.fragmentManager?.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                if (f != fragment) {
                    return
                }
                if (isShoudCreateOpen) {
                    onCreate()
                    onComCreate()
                } else {
                    onResume()
                    onComResume()
                }
            }

            override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                if (f != fragment) {
                    return
                }
                onPause()
                onComPause()
            }


            override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                if (f != fragment) {
                    return
                }
                onStop()
            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                if (f != fragment) {
                    return
                }
                onDestroy()
            }
        }, false)
    }

    fun onComCreate() {
        if (!isShoudCreateOpen)
            return
        if (checkPermission()) {
            openCamera()
        } else {
            requstPermission()
        }
    }

    fun onComResume() {
        if (isShoudCreateOpen) {
            return
        }
        if (checkPermission())
            openCamera()
    }

    fun onComPause() {
        closeCamera()
        isShoudCreateOpen = false
    }

    protected val cameraHandler by lazy {
        val handlerThread = HandlerThread(System.currentTimeMillis().toString())
        handlerThread.start()
        Handler(handlerThread.looper)
    }

    fun openCamera() {
        cameraHandler.post {
            start()
        }
    }

    fun closeCamera() {
        cameraHandler.post {
            stop()
        }
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

    override fun onCreate() {

    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onStop() {

    }

    override fun onDestroy() {
        cameraHandler.looper.quit()
    }

    fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    fun requstPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as? Activity)?.requestPermissions(arrayOf(Manifest.permission.CAMERA), 200)
        }
    }
}