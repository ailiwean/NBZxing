package com.ailiwean.core.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.ailiwean.core.Utils
import com.google.android.cameraview.R

/**
 * @Package:        com.ailiwean.core.view
 * @ClassName:      ScanBarView
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/26 9:50 AM
 */
class ScanBarView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : FrameLayout(context, attributeSet, def) {

    private val ALPHA_LENGHT = 100f


    val barView: ImageView by lazy {
        val view = ImageView(context)
        view.layoutParams = ViewGroup.LayoutParams(-1, Utils.dp2px(50f))
        view.setBackgroundResource(R.drawable.ic_scan_bar)
        view
    }

    init {
        post {
            addView(barView)
            startAnim()
        }
    }

    private var animator: ValueAnimator? = null


    fun startAnim() {
        if (animator != null && animator?.isRunning!!) {
            return
        }

        if (measuredHeight == 0)
            return

        visibility = View.VISIBLE
        animator = ValueAnimator.ofFloat(0f, measuredHeight.toFloat())
                .setDuration(2000)
        animator?.addUpdateListener { it ->
            val values = it.animatedValue as Float
            alpha = if (values <= ALPHA_LENGHT) {
                values / ALPHA_LENGHT
            } else {
                (measuredHeight - values) / ALPHA_LENGHT
            }
            translationY = values
        }
        animator?.repeatCount = Int.MAX_VALUE - 1
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.start()
    }

    fun stopAnim() {
        visibility = View.INVISIBLE
        animator?.cancel()
    }
}