package com.ailiwean.core.able

import android.os.Handler
import android.os.HandlerThread
import com.ailiwean.core.Config
import com.ailiwean.core.TypeRunnable
import com.ailiwean.core.WorkThreadServer
import com.ailiwean.core.helper.ScanHelper
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource
import com.ailiwean.module_grayscale.GrayScaleDispatch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: AbleManager
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 2:32 PM
 */
class AbleManager private constructor(handler: Handler) : PixsValuesAble(handler) {

    private val ableList = CopyOnWriteArrayList<PixsValuesAble>()

    private var server: WorkThreadServer = WorkThreadServer.createInstance()

    private var processClz: Class<out Any>? = null
    private var processDispatch: GrayScaleDispatch? = null

    private val grayProcessHandler by lazy {
        Handler(HandlerThread("GrayProcessThread")
                .apply { start() }
                .looper)
    }

    init {
        loadAbility()
        try {
            processClz = Class.forName(Config.GARY_SCALE_PATH)
        } catch (e: Exception) {
        }
        if (processClz != null)
            processDispatch = processClz?.newInstance() as GrayScaleDispatch?
    }

    fun loadAbility() {
        ableList.clear()
        ableList.add(XQRScanCrudeAble(handlerHolder.get()))
        ableList.add(XQRScanFineAble(handlerHolder.get()))
        ableList.add(XQRScanZoomAble(handlerHolder.get()))
        ableList.add(XQRScanAbleRotate(handlerHolder.get()))
        ableList.add(LighSolveAble(handlerHolder.get()))
//        ableList.add(XQRScanAble(handler))
//        ableList.add(GrayscaleStrengAble(handler))
//        ableList.add(XQRScanFastAble(handler))
    }

    public override fun cusAction(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        originProcess(data, dataWidth, dataHeight)
        grayscaleProcess(data, dataWidth, dataHeight)
    }

    private fun originProcess(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        executeToParse(data, dataWidth, dataHeight, true, server)
    }

    private fun grayscaleProcess(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        if (processClz == null)
            return
        grayProcessHandler.removeCallbacksAndMessages(null)
        grayProcessHandler.post {
            val newByte = processDispatch!!.dispatch(data, dataWidth, dataHeight, Config.scanRect.scanR)
            if (newByte.isNotEmpty())
                executeToParse(newByte, dataWidth, dataHeight, false, server)
        }
    }

    private fun executeToParse(data: ByteArray, dataWidth: Int, dataHeight: Int, isNative: Boolean, server: WorkThreadServer) {
        val source = generateGlobeYUVLuminanceSource(data, dataWidth, dataHeight) ?: return
        for (able in ableList) {
            server.post(TypeRunnable.create(if (isNative) TypeRunnable.NORMAL else TypeRunnable.SCALE) {
                able.cusAction(data, dataWidth, dataHeight, isNative)
                able.needParseDeploy(source)
            })
        }
    }

    private fun generateGlobeYUVLuminanceSource(data: ByteArray?, dataWidth: Int, dataHeight: Int): PlanarYUVLuminanceSource? {
        return ScanHelper.buildLuminanceSource(data, dataWidth, dataHeight, ScanHelper.getScanByteRect(dataWidth, dataHeight))
    }

    companion object {
        fun createInstance(handler: Handler): AbleManager {
            return AbleManager(handler)
        }
    }

    override fun release() {
        ableList.forEach {
             it.release()
        }
        ableList.clear()
        server.quit()
        if (processClz == null)
            return
        grayProcessHandler.removeCallbacksAndMessages(null)
        grayProcessHandler.looper.quit()
    }

    fun clear() {
        server.clear()
        ableList.clear()
    }

}