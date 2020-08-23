package com.ailiwean.core.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.ailiwean.core.Config.*
import com.ailiwean.core.Result
import com.ailiwean.core.Utils
import com.ailiwean.core.able.AbleManager
import com.ailiwean.core.helper.VibrateHelper
import com.ailiwean.core.zxing.BitmapLuminanceSource
import com.ailiwean.core.zxing.CustomMultiFormatReader
import com.ailiwean.core.zxing.ScanTypeConfig
import com.ailiwean.core.zxing.core.BinaryBitmap
import com.ailiwean.core.zxing.core.common.GlobalHistogramBinarizer
import com.ailiwean.core.zxing.core.common.HybridBinarizer
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.BaseCameraView
import com.google.android.cameraview.CameraView
import com.google.android.cameraview.R
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.base_zxing_layout.view.*
import java.io.File
import java.lang.ref.WeakReference

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      ZxingCamera
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/8/23 12:38 AM
 */
abstract class FreeZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        BaseCameraView(context, attributeSet, def), Handler.Callback, FreeInterface {

    init {

        //使用后置相机
        facing = FACING_BACK

        //设定相机数据选取比例
        this.setAspectRatio(AspectRatio.of(16, 9))

    }

    private val handleZX = HandleZX(this)

    private var ableCollect: AbleManager? = null

    private var busHandle: Handler? = null

    /***
     * Handler结果回调
     */
    override fun handleMessage(it: Message): Boolean {
        when (it.what) {

            //扫码结果回调
            SCAN_RESULT -> {
                scanSucHelper()
                if (it.obj is Result) {
                    showQRLoc((it.obj as Result).pointF, it.obj.toString())
                }
            }

            //环境亮度变换回调
            LIGHT_CHANGE -> {
                lightView.setBright(it.obj.toString().toBoolean())
            }

            //放大回调
            AUTO_ZOOM -> {
                setZoom(it.obj.toString().toFloat())
            }

        }
        return true
    }

    /***
     * 相机采集数据实时回调
     */
    override fun onPreviewByteBack(camera: CameraView, data: ByteArray) {
        super.onPreviewByteBack(camera, data)
        //解析数据
        ableCollect?.cusAction(data, scanRect.dataX, scanRect.dataY)
    }

    /***
     * 扫码成功后的一些动作
     */
    private fun scanSucHelper() {

        //关闭相机
        onCameraPause()

        //清理线程池任务缓存
        ableCollect?.clear()

        //关闭扫码条动画
        scan_bar.stopAnim()

        //播放音频
        VibrateHelper.playVibrate()

        //震动
        VibrateHelper.playBeep()

    }

    /***
     * Activity或Fragment创建，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onCreate() {
        super.onCreate()
        ableCollect = AbleManager.createInstance(handleZX)
    }

    /***
     * Activity或Fragment不可视，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onPause() {
        super.onPause()
        scan_bar.stopAnim()
    }

    /***
     * Activity或Fragment生命周期结束销毁，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onDestroy() {
        super.onDestroy()
        ableCollect?.release()
        busHandle?.looper?.quit()
    }

    /***
     * 显示二维码位置, 动画播放完回调扫描结果
     */
    fun showQRLoc(point: PointF, content: String) {
        qr_loc.visibility = View.VISIBLE
        qr_loc.translationX = point.x - Utils.dp2px(25f)
        qr_loc.translationY = point.y - Utils.dp2px(25f)
        qr_loc.scaleX = 0f
        qr_loc.scaleY = 0f
        qr_loc.animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        resultBack(content)
                    }
                })
                .start()

    }

    /***
     * 表层的View及一些配置初始化
     */
    private fun topViewInitWithConfig() {

        scan_bar.startAnim()
        qr_loc.visibility = View.INVISIBLE

        //重新装填AbleManager
        ableCollect?.init()

        //配置扫码类型
        initScanType()

        //重新接收数据
        handleZX.init()

        //音频资源加载
        VibrateHelper.playInit()
    }

    /***
     * 扫码结果回调
     */
    abstract fun resultBack(content: String)

    /***
     * 图片文件扫码
     * 扫码失败返回空字符串，详见{ @link #parseBitmap}
     */
    protected open fun resultBackFile(content: String) {}

    /***
     * 启动相机后的操作
     */
    override fun onCameraOpenBack(camera: CameraView) {
        super.onCameraOpenBack(camera)
        clearFindViewByIdCache()
        LayoutInflater.from(context).inflate(R.layout.base_zxing_layout, this, true)
        topViewInitWithConfig()
    }

    /***
     * 配置扫码类型
     */
    private fun initScanType() {
        scanTypeConfig = getScanType()
    }

    /***
     * 获取扫码类型
     */
    open fun getScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    /***
     * 初始化业务Handler
     */
    private fun initBusHandle() {
        val handlerThread = HandlerThread(System.currentTimeMillis().toString())
        handlerThread.start()
        busHandle = Handler(handlerThread.looper)
    }

    /***
     * File转Bitmap详见{@link #parseBitmap}
     */
    protected fun parseFile(filePath: String) {

        proscribeCamera()

        if (!checkPermissionRW())
            return

        val file = File(filePath)
        if (!file.exists())
            return

        if (busHandle == null)
            initBusHandle()

        busHandle?.post {
            val bitmap: Bitmap = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, getMediaUriFromPath(context, filePath)))
                        .copy(Bitmap.Config.RGB_565, false)
            } else
                BitmapFactory.decodeFile(filePath))
                    ?: return@post
            parseBitmap(bitmap)
        }
    }

    /***
     * 解析Bitmap
     * 解析过程中会关闭相机， 解析失败重新启动
     */
    protected fun parseBitmap(bitmap: Bitmap?) {

        proscribeCamera()

        if (bitmap == null)
            return
        if (busHandle == null)
            initBusHandle()
        busHandle?.post {
            bitmap.apply {
                if (config != Bitmap.Config.RGB_565
                        && config != Bitmap.Config.ARGB_8888) {
                    if (isMutable)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            config = Bitmap.Config.RGB_565
                        } else {
                            copy(Bitmap.Config.RGB_565, false)
                        }
                    else
                        copy(Bitmap.Config.RGB_565, false)
                }
                val source = BitmapLuminanceSource(this)
                var result = CustomMultiFormatReader.getInstance()
                        .decode(BinaryBitmap(GlobalHistogramBinarizer(source)))
                if (result == null)
                    result = CustomMultiFormatReader.getInstance()
                            .decode(BinaryBitmap(HybridBinarizer(source)))
                if (result != null) {
                    mainHand.post {
                        resultBackFile(result.text)
                        scanSucHelper()
                    }
                } else {
                    unProscibeCamera()
                    mainHand.post {
                        resultBackFile("")
                    }
                }
            }
        }
    }

    /***
     * path转Uri兼容Android10
     */
    private fun getMediaUriFromPath(context: Context, path: String): Uri {
        val mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = context.contentResolver.query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?", arrayOf(path.substring(path.lastIndexOf("/") + 1)),
                null)
        var uri: Uri? = null
        cursor?.let {
            it.moveToFirst()
            uri = ContentUris.withAppendedId(mediaUri,
                    it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID)))
        }
        cursor?.close()
        return uri ?: Uri.EMPTY
    }

    /***
     * 全局Handler
     */
    class HandleZX constructor(view: Callback) : Handler() {
        var hasResult = false
        var viewReference: WeakReference<Callback>? = null

        init {
            this@HandleZX.viewReference = WeakReference(view)
        }

        override fun handleMessage(msg: Message?) {
            if (hasResult)
                return
            msg?.apply {
                if (msg.what == SCAN_RESULT)
                    hasResult = true
                this@HandleZX.viewReference?.get()?.let {
                    it.handleMessage(msg)
                }
            }
        }

        fun init() {
            hasResult = false
        }
    }

}