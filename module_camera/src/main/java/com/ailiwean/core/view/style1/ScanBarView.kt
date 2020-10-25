package com.ailiwean.core.view.style1

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.ailiwean.core.Utils
import com.ailiwean.core.view.ScanBarCallBack
import com.google.android.cameraview.R

/**
 * @Package:        com.ailiwean.core.view
 * @ClassName:      ScanBarView
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/26 9:50 AM
 */
class ScanBarView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        FrameLayout(context, attributeSet, def), ScanBarCallBack {

    private val BAR_HEIGHT = Utils.dp2px(20f)

    private val ALPHA_LENGHT = 0.2f

    private val barView: ImageView by lazy {
        val view = ImageView(context)
        view.layoutParams = ViewGroup.LayoutParams(-1, BAR_HEIGHT)
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

        val alpha_height = ALPHA_LENGHT * measuredHeight

        visibility = View.VISIBLE
        animator = ValueAnimator.ofFloat((-BAR_HEIGHT).toFloat(), measuredHeight.toFloat())
                .setDuration(4000)
        animator?.addUpdateListener { it ->
            val values = it.animatedValue as Float
            barView.alpha = if (values <= alpha_height) {
                values / alpha_height
            } else {
                (measuredHeight - values) / alpha_height
            }
            barView.translationY = values
        }
        animator?.repeatCount = Int.MAX_VALUE - 1
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.start()
    }

    fun stopAnim() {
        visibility = View.INVISIBLE
        animator?.cancel()
    }

    override fun startScanAnimator() {
        startAnim()
    }

    override fun cameraStartLaterInit() {

    }

    override fun stopScanAnimator() {
        stopAnim()
    }
}