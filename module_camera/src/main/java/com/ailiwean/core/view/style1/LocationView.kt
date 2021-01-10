package com.ailiwean.core.view.style1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ailiwean.core.Result
import com.ailiwean.core.view.ScanLocViewCallBack
import com.google.android.cameraview.R

/**
 * @Package:        com.ailiwean.core.view
 * @ClassName:      LocationView
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/8/29 3:34 PM
 */
class LocationView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        androidx.appcompat.widget.AppCompatImageView(context, attributeSet, def), ScanLocViewCallBack {

    override fun cameraStartLaterInit() {
        visibility = View.GONE
        setImageResource(R.drawable.ic_qr_loc)
    }

    override fun toLocation(result: Result, run: Runnable) {
        var qrPoint = result.qrPointF
        visibility = View.VISIBLE
        translationX = (qrPoint.x - layoutParams.width / 2)
        translationY = (qrPoint.y - layoutParams.height / 2)
        scaleX = 0f
        scaleY = 0f
        animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        run.run()
                    }
                })
                .start()
    }


}