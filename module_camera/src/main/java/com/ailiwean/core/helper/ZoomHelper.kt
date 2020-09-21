package com.ailiwean.core.helper

import android.annotation.SuppressLint
import com.ailiwean.core.Config
import com.ailiwean.core.OnGestureListener
import com.google.android.cameraview.BaseCameraView

/**
 * @Package:        com.ailiwean.core.helper
 * @ClassName:      ZoomHelper
 * @Description:    变焦Helper
 * @Author:         SWY
 * @CreateDate:     2020/4/19 1:45 AM
 */
object ZoomHelper {

    var currentOnce: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    fun toAutoZoom(view: BaseCameraView) {
        Config.currentZoom = 0f
        view.setOnTouchListener(object : OnGestureListener(view.context) {
            override fun onStepFingerChange(total: Float, offset: Float) {

                if (currentOnce == 0f)
                    currentOnce = Config.currentZoom

                currentOnce += offset / 10000
                view.setZoom(currentOnce.let {
                    when {
                        it > 1f -> 1f
                        it < 0f -> 0f
                        else -> it
                    }
                })
            }

            override fun onDoubleClick() {
                view.setZoom(Config.currentZoom + 0.025f)
            }

            override fun onStepEnd() {
                currentOnce = 0f
            }

        })
    }

    fun close(view: BaseCameraView) {
        view.setOnTouchListener(null)
    }

}