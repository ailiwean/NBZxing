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
    }

    override fun dispatch(data: ByteArray?, width: Int, height: Int): ByteArray {
        if (random.nextInt(10) in 0..5)
            return grayScaleProcess[random.nextInt(grayScaleProcess.size)].dispatch(
                    data, width, height)
        return ByteArray(0)
    }

}