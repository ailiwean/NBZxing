package com.ailiwean.core

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.TypedValue
import androidx.core.app.ActivityCompat.requestPermissions
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
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
        return holder!!.get()
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

}