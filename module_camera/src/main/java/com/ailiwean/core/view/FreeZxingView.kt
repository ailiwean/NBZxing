package com.ailiwean.core.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
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
import com.ailiwean.core.zxing.ScanTypeConfig
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
        BaseCameraView(context, attributeSet, def), Handler.Callback {

    init {
        facing = FACING_BACK
        this.setAspectRatio(AspectRatio.of(16, 9))
    }

    private val handleZX = HandleZX(this)

    override fun handleMessage(it: Message): Boolean {
        when (it.what) {
            SCAN_RESULT -> {
                scanSucHelper()
                if (it.obj is Result) {
                    showQRLoc((it.obj as Result).pointF, it.obj.toString())
                }
            }
            LIGHT_CHANGE -> {
                lightView.setBright(it.obj.toString().toBoolean())
            }

            AUTO_ZOOM -> {
                setZoom(it.obj.toString().toFloat())
            }
        }
        return true
    }

    private var ableCollect: AbleManager? = null

    override fun onPreviewByte(camera: CameraView, data: ByteArray) {
        super.onPreviewByte(camera, data)
        val dataWidht = scanRect.dataX
        val dataHeight = scanRect.dataY
        ableCollect?.cusAction(data, dataWidht, dataHeight)
    }

    /***
     * 扫码成功后的一些动作
     */
    private fun scanSucHelper() {
        ableCollect?.clear()
        onCameraPause()
        scan_bar.stopAnim()
        VibrateHelper.playVibrate()
        VibrateHelper.playBeep()
    }

    override fun onCreate() {
        super.onCreate()
        ableCollect = AbleManager.createInstance(handleZX)
    }

    override fun onPause() {
        super.onPause()
        scan_bar.stopAnim()
    }

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
     * 相机启动数据初始化
     */
    fun initConfig() {
        ableCollect?.init()
        scan_bar.startAnim()
        qr_loc.visibility = View.INVISIBLE
        initScanType()
        handleZX.init()
        VibrateHelper.playInit()
    }

    /***
     * 扫码结果回调
     */
    abstract fun resultBack(content: String)
    protected open fun resultBackFile(content: String) {

    }

    override fun onCameraOpen(camera: CameraView) {
        super.onCameraOpen(camera)
        clearFindViewByIdCache()
        LayoutInflater.from(context).inflate(R.layout.base_zxing_layout, this, true)
        //注册打开关闭闪光灯点击事件
        lightView.regLightClick {
            lightOperator(it)
        }
        initConfig()
    }

    @SuppressLint("WrongViewCast")
    fun getView(): View? {
        return findViewById(R.id.base_floor)
    }

    private fun initScanType() {
        scanTypeConfig = getScanType()
    }

    open fun getScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    var busHandle: Handler? = null

    private fun initBusHandle() {
        val handlerThread = HandlerThread(System.currentTimeMillis().toString())
        handlerThread.start()
        busHandle = Handler(handlerThread.looper)
    }

    protected fun parseFile(filePath: String) {
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

    protected fun parseBitmap(bitmap: Bitmap?) {
        if (bitmap == null)
            return
        if (busHandle == null)
            initBusHandle()
        busHandle?.post {

        }
    }

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