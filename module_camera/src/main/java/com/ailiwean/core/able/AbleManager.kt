package com.ailiwean.core.able

import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import com.ailiwean.core.Config
import com.ailiwean.core.TypeRunnable
import com.ailiwean.core.WorkThreadServer
import com.ailiwean.core.helper.ScanHelper
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource
import com.ailiwean.module_grayscale.Dispatch
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

    private var processDispatch: Dispatch? = null

    private val grayProcessHandler by lazy {
        Handler(HandlerThread("GrayProcessThread")
            .apply { start() }
            .looper)
    }

    init {
        loadAbility()
        if (Config.hasDepencidesScale()) {
            processDispatch = GrayScaleDispatch
        }
    }

    fun loadAbility() {
        ableList.clear()
        ableList.apply {
            add(XQRScanCrudeAble(handlerHolder.get()))
            if (Config.isSupportAutoZoom)
                add(XQRScanZoomAble(handlerHolder.get()))
            else add(XQRScanAble(handlerHolder.get()))
            add(XQRScanAbleRotate(handlerHolder.get()))
            add(LighSolveAble(handlerHolder.get()))
        }
    }

    /**
     * 相机实时数据解析
     */
    public override fun cusAction(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        executeToParseWay2(
            data,
            dataWidth,
            dataHeight,
            ScanHelper.getScanByteRect(dataWidth, dataHeight)
        )
    }

    /***
     * 灰度处理后解析
     */
    private fun grayscaleProcess(source: PlanarYUVLuminanceSource) {
        grayProcessHandler.removeCallbacksAndMessages(null)
        grayProcessHandler.post {
            processDispatch!!.dispatch(source.matrix, source.width, source.height)
            for (able in ableList) {
                //任务是否可以执行(由任务内部逻辑实现)
                if (able.isCycleRun(false)) {
                    //线程池推送任务
                    server.post(TypeRunnable.create(able.provideType(false)) {
                        able.cusAction(source.matrix, source.width, source.height, false)
                        able.needParseDeploy(source, false, null)
                    })
                }
            }
        }
    }

    /***
     *  解析原始数据
     */
    private fun originProcess(
        source: PlanarYUVLuminanceSource,
        data: ByteArray,
        dataWidth: Int,
        dataHeight: Int
    ) {
        ableList.forEach { able ->
            if (able.isCycleRun(true))
                server.post(TypeRunnable.create(able.provideType(true)) {
                    able.cusAction(data, dataWidth, dataHeight, true)
                    able.needParseDeploy(source, true, null)
                })
        }
    }

    /***
     * 任务调度
     * 方式二： 直接拷贝一份ByteArray同时处理原始数据与灰度变换后的数据
     * 该方式速度快，占用内存较高
     */
    private fun executeToParseWay2(data: ByteArray, dataWidth: Int, dataHeight: Int, rect: Rect) {
        //生成全局YUVLuminanceSource
        val oriSource = generateGlobeYUVLuminanceSource(data, dataWidth, dataHeight, rect) ?: return
        //执行原始数据解析
        originProcess(oriSource, data, dataWidth, dataHeight)
        //copy一份相同的数据后处理灰度
        val graySource = oriSource.copyAll()
        if (processDispatch != null)
            grayscaleProcess(graySource)
    }

    private fun generateGlobeYUVLuminanceSource(
        data: ByteArray?,
        dataWidth: Int,
        dataHeight: Int,
        rect: Rect
    ): PlanarYUVLuminanceSource? {
        return ScanHelper.buildLuminanceSource(data, dataWidth, dataHeight, rect)
    }

    companion object {
        fun createInstance(handler: Handler): AbleManager {
            return AbleManager(handler)
        }
    }

    override fun release() {
        ableList.forEach { it.release() }
        ableList.clear()
        server.quit()
        if (processDispatch == null) return
        grayProcessHandler.removeCallbacksAndMessages(null)
        grayProcessHandler.looper.quit()
    }

    fun clear() {
        server.clear()
        ableList.clear()
    }

}