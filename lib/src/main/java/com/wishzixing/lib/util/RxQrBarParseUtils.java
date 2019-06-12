package com.wishzixing.lib.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.core.CustomMultiFormatReader;

/***
 *  Created by SWY
 *  DATE 2019/6/2
 *
 */
public class RxQrBarParseUtils {

    private RxQrBarParseUtils() {
    }

    private static class Holder {
        static RxQrBarParseUtils rxQrBarParseUtils = new RxQrBarParseUtils();
    }

    public static RxQrBarParseUtils getInstance() {
        return Holder.rxQrBarParseUtils;
    }

    /**
     * 解析图片中的 二维码 或者 条形码
     *
     * @param photo 待解析的图片
     * @return Result 解析结果，解析识别时返回NULL
     */
    public Result decodeFromPhoto(Bitmap photo) {

        Result rawResult = null;
        if (photo != null) {
            Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            photo.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生

            // 开始对图像资源解码
            try {
                rawResult = CustomMultiFormatReader.getInstance().decodeWithState(ConvertUtlis.bitmapToBinary(smallBitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rawResult;
    }


    public Result decodeFromByte(byte[] data, int width, int height) {

        Result rawResult = null;

        BinaryBitmap bitmap = ConvertUtlis.byteToBinay(data, CameraConfig.getInstance().getFramingRect());

        if (bitmap == null)
            return null;

        try {
            rawResult = CustomMultiFormatReader.getInstance().decodeWithState(bitmap);
        } catch (ReaderException e) {
            e.printStackTrace();
        } finally {
            CustomMultiFormatReader.getInstance().reset();
        }

        return rawResult;
    }


    /**
     * Resize the bitmap
     *
     * @param bitmap 图片引用
     * @param width  宽度
     * @param height 高度
     * @return 缩放之后的图片引用
     */
    private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

    }

}