package com.ailiwean.core.able

import android.os.Handler
import android.os.HandlerThread
import com.ailiwean.core.Config
import com.ailiwean.core.TypeRunnable
import com.ailiwean.core.WorkThreadServer
import com.ailiwean.core.helper.ScanHelper
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource
import com.ailiwean.module_grayscale.Dispatch
import com.ailiwean.module_grayscale.GrayScaleDispatch
import java.util.*
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
        ableList.add(XQRScanCrudeAble(handlerHolder.get()))
        ableList.add(XQRScanZoomAble(handlerHolder.get()))
        ableList.add(XQRScanAbleRotate(handlerHolder.get()))
        ableList.add(LighSolveAble(handlerHolder.get()))
//        ableList.add(XQRScanAble(handler))
//        ableList.add(GrayscaleStrengAble(handlerHolder.get()))
//        ableList.add(XQRScanFastAble(handler))
    }

    public override fun cusAction(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        executeToParseWay2(data, dataWidth, dataHeight)
    }

    /***
     * 灰度处理后解析
     */
    private fun grayscaleProcess(source: PlanarYUVLuminanceSource) {
        if (processDispatch == null)
            return
        grayProcessHandler.removeCallbacksAndMessages(null)
        grayProcessHandler.post {
            processDispatch!!.dispatch(source.matrix, source.width, source.height)
            for (able in ableList) {
                //任务是否可以执行(由任务内部逻辑实现)
                if (able.isCycleRun(false)) {
                    //线程池推送任务
                    server.post(TypeRunnable.create(
                            able.isImportant(false),
                            //区分类型
                            TypeRunnable.SCALE, source.tagId) {
                        able.needParseDeploy(source, false)
                    })
                }
            }
        }
    }

    /***
     *  解析原始数据
     */
    private fun originProcess(typeRunList: ArrayList<TypeRunnable>) {
        for (item in typeRunList) {
            //线程池推送任务
            server.post(item)
        }
    }

    /***
     *  解析原始数据
     */
    private fun originProcess(source: PlanarYUVLuminanceSource, data: ByteArray, dataWidth: Int, dataHeight: Int) {
        ableList.forEach { able ->
            if (able.isCycleRun(true))
                server.post(TypeRunnable.create(
                        able.isImportant(true),
                        //区分类型
                        TypeRunnable.NORMAL, source.tagId) {
                    able.cusAction(data, dataWidth, dataHeight, true)
                    able.needParseDeploy(source, true)
                })
        }
    }

    /***
     * 任务调度
     * 方式一： 原始数据扫描后基于原始数据进行灰度变换后再处理
     *  这种方式占用内存最少，速度有点慢
     */
    private fun executeToParseWay1(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        //生成全局YUVLuminanceSource
        val source = generateGlobeYUVLuminanceSource(data, dataWidth, dataHeight) ?: return
        var typeRunList = ArrayList<TypeRunnable>()
        ableList.forEach { able ->
            if (able.isCycleRun(true))
                TypeRunnable.create(
                        //任务是否为重要的(不会被线程池舍弃)
                        able.isImportant(true),
                        //区分类型
                        TypeRunnable.NORMAL, source.tagId) {
                    able.cusAction(data, dataWidth, dataHeight, true)
                    able.needParseDeploy(source, true)
                }.apply {
                    typeRunList.add(this)
                }
        }
        //原始任务结束后回调
        server.regPostListBack(source.tagId, typeRunList.size) {
            //灰度处理与解析
            grayscaleProcess(source)
        }
        //执行原始数据解析
        originProcess(typeRunList)
    }

    /***
     * 任务调度
     * 方式二： 直接拷贝一份ByteArray同时处理原始数据与灰度变换后的数据
     * 该方式速度快，占用内存较高
     */
    private fun executeToParseWay2(data: ByteArray, dataWidth: Int, dataHeight: Int) {
        //生成全局YUVLuminanceSource
        val oriSource = generateGlobeYUVLuminanceSource(data, dataWidth, dataHeight) ?: return
        //执行原始数据解析
        originProcess(oriSource, data, dataWidth, dataHeight)
        //copy一份相同的数据后处理灰度
        val graySource = oriSource.copyAll()
        grayscaleProcess(graySource)
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
        if (processDispatch == null)
            return
        grayProcessHandler.removeCallbacksAndMessages(null)
        grayProcessHandler.looper.quit()
    }

    fun clear() {
        server.clear()
        ableList.clear()
    }

}