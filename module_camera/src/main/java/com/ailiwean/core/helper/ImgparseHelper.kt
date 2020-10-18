package com.ailiwean.core.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import com.ailiwean.core.Utils
import com.ailiwean.core.zxing.BitmapLuminanceSource
import com.ailiwean.core.zxing.CustomMultiFormatReader
import com.ailiwean.core.zxing.core.BinaryBitmap
import com.ailiwean.core.zxing.core.Result
import com.ailiwean.core.zxing.core.common.HybridBinarizer
import java.io.File

/**
 * @Package:        com.ailiwean.core.helper
 * @ClassName:      ImgparseHelper
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/10/10 2:15 PM
 */
object ImgparseHelper {

    fun parseFile(filePath: String): Result? {

        val file = File(filePath)
        if (!file.exists())
            return null

        val bitmap: Bitmap = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Utils.getContext()?.let { it ->
                it.contentResolver.let { reso ->
                    ImageDecoder.createSource(reso, Utils.getMediaUriFromPath(it, filePath))
                }.let {
                    ImageDecoder.decodeBitmap(it)
                            .copy(Bitmap.Config.RGB_565, false)
                }
            }
        } else
            BitmapFactory.decodeFile(filePath))
                ?: return null

        return parseBitmap(bitmap)
    }

    fun parseBitmap(bitmap: Bitmap?): Result? {

        if (bitmap == null)
            return null

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
            return CustomMultiFormatReader.getInstance()
                    .decode(BinaryBitmap(HybridBinarizer(source)))
        }

    }

}