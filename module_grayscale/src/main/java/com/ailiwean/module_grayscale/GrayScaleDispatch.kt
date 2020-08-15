package com.ailiwean.module_grayscale

import java.util.*
import kotlin.collections.ArrayList


/**
 * @Package:        com.ailiwean.module_grayscale
 * @ClassName:      GrayScaleDispatch
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/8/9 6:33 PM
 */
object GrayScaleDispatch : Dispatch {

    private var grayScaleProcess = ArrayList<Dispatch>()
    var random = Random()

    init {
        grayScaleProcess.add(LightGreyScale())
        grayScaleProcess.add(OverBrightScale())
        grayScaleProcess.add(OverDarkScale())
    }

    override fun dispatch(data: ByteArray?, width: Int, height: Int): ByteArray {
        return grayScaleProcess[random.nextInt(grayScaleProcess.size)].dispatch(
                data, width, height)
    }

}