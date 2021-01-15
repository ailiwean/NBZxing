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
import com.ailiwean.core.zxing.core.common.GlobalHistogramBinarizer
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
                    ImageDecoder.decodeBitmap(it) { decoder, _, _ ->
                        decoder.setTargetSampleSize(2)
                        decoder.isMutableRequired = true
                    }
                }
            }
        } else
            BitmapFactory.decodeFile(filePath, BitmapFactory.Options().apply {
                inSampleSize = 2
            })) ?: return null

        return parseBitmap(bitmap)
    }

    fun parseBitmap(bitmap: Bitmap?): Result? {
        if (bitmap == null)
            return null
        val source = BitmapLuminanceSource(bitmap)
        return CustomMultiFormatReader.getInstance()
                .decode(BinaryBitmap(GlobalHistogramBinarizer(source)))
                ?: CustomMultiFormatReader.getInstance()
                        .decode(BinaryBitmap(HybridBinarizer(source)))
    }

}