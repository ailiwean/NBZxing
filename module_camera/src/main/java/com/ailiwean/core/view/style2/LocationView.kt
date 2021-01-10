package com.ailiwean.core.view.style2

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.ailiwean.core.Result
import com.ailiwean.core.view.ScanLocViewCallBack


/**
 * @Package:        com.ailiwean.core.view
 * @ClassName:      LocationView2
 * @Description:
 * @Author:         SWY(https://github.com/ailiwean)
 * @CreateDate:     2020/10/25 3:35 PM
 */
class LocationView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        androidx.appcompat.widget.AppCompatImageView(context, attributeSet, def), ScanLocViewCallBack {

    private var animator: ObjectAnimator? = null

    override fun cameraStartLaterInit() {
        startAnim()
    }

    fun startAnim() {
        val scaleYProper = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f, 1f)
        val scaleXProper = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f, 1f)
        animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleYProper, scaleXProper)
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.duration = 2000
        animator?.repeatCount = Int.MAX_VALUE - 1
        animator?.start()
    }
    
    override fun toLocation(result: Result?, runnable: Runnable?) {
        var params = layoutParams
        result?.let {
            translationX = it.qrPointF.x - it.qrLeng * 1f
            translationY = it.qrPointF.y - it.qrLeng * 1f
            rotation = it.qrRotate
            params.width = (it.qrLeng * 2f).toInt()
            params.height = (it.qrLeng * 2f).toInt()
            (params as FrameLayout.LayoutParams).gravity = Gravity.TOP or Gravity.LEFT
            layoutParams = params
        }
        runnable?.run()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}