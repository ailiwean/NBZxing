package com.ailiwean.core

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import com.ailiwean.core.zxing.core.common.BitMatrix
import java.io.*
import java.lang.ref.WeakReference

/**
 * @Package:        com.ailiwean.core
 * @ClassName:      Utils
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/10/10 2:18 PM
 */
object Utils {

    var holder: WeakReference<Context>? = null

    fun init(mContext: Context) {
        holder = WeakReference(mContext)
    }

    fun getContext(): Context? {
        return holder?.get()
    }

    fun dp2px(dpValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getContext()?.resources?.displayMetrics).toInt()
    }

    fun readFile(fileName: String?): ByteArray? {
        var len: Int
        val stream: FileInputStream
        var stream2: ByteArrayOutputStream? = null
        try {
            stream = FileInputStream(fileName)
            stream2 = ByteArrayOutputStream()
            val buffer = ByteArray(5)
            //先读后写,循环读写
            while (stream.read(buffer).also { len = it } != -1) {
                stream2.write(buffer, 0, len)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stream2?.toByteArray()
    }


    fun checkPermissionRW(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            context.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        } else {
            return true
        }
    }

    fun requstPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as? Activity)?.requestPermissions(arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ), 200)
        }
    }

    fun checkPermissionCamera(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    /***
     * path转Uri兼容Android10
     */
    fun getMediaUriFromPath(context: Context, path: String): Uri {
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

//    @JvmStatic
//    var time: Long = 0L
//    fun toPrint(bitMatrix: BitMatrix) {
//
//        if (System.currentTimeMillis() - time > 5000) {
//            val file0 = File(Environment.getExternalStorageDirectory(), "二值化处理后.jpg")
//            if (file0.exists()) file0.delete()
//            try {
//                file0.createNewFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            val cropNv21: ByteArray = bitMatrix.covertYData()
//            var newCrop = ByteArray(cropNv21.size + cropNv21.size / 2)
//
//            System.arraycopy(cropNv21, 0, newCrop, 0, cropNv21.size)
//
//            for (i in cropNv21.size until newCrop.size) {
//                newCrop[i] = 129.toByte()
//            }
//
//            val yuvImage = YuvImage(newCrop, ImageFormat.NV21, bitMatrix.width, bitMatrix.height, null)
//            try {
//                yuvImage.compressToJpeg(Rect(0, 0, bitMatrix.width, bitMatrix.height), 100, FileOutputStream(file0))
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//            time = System.currentTimeMillis();
//        }
//    }

}