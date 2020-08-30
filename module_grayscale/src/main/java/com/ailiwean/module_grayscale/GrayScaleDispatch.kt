package com.ailiwean.module_grayscale

import android.graphics.Rect
import java.util.*
import kotlin.collections.ArrayList


/**
 * @Package:        com.ailiwean.module_grayscale
 * @ClassName:      GrayScaleDispatch
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/8/9 6:33 PM
 */
class GrayScaleDispatch : Dispatch {

    private var grayScaleProcess = ArrayList<Dispatch>()
    var random = Random()

    init {
        grayScaleProcess.add(LightGreyScale())
        grayScaleProcess.add(OverBrightScale())
        grayScaleProcess.add(OverDarkScale())
        grayScaleProcess.add(RevGrayScale())
    }

    override fun dispatch(data: ByteArray?, width: Int, height: Int): ByteArray {
        return grayScaleProcess[random.nextInt(grayScaleProcess.size)].dispatch(
                data, width, height)
    }

    override fun dispatch(data: ByteArray?, width: Int, height: Int, rect: Rect?): ByteArray {

        if (rect == null || (rect.left == 0 && rect.right == 0) ||
                rect.top == 0 && rect.bottom == 0)
            return dispatch(data, width, height)

        return grayScaleProcess[random.nextInt(grayScaleProcess.size)].dispatch(
                data, width, height, rect)

    }
}