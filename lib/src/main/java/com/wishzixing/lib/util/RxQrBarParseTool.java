package com.wishzixing.lib.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.wishzixing.lib.R;
import com.wishzixing.lib.able.CustomMultiFormatReader;
import com.wishzixing.lib.source.PlanarYUVLuminanceSource;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.manager.CameraManager;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @ClassName: RxQrBarParseTool
 * @Description: ZXing解析
 * @Author: Administrator
 * @Date: 2019/4/3 16:11
 */
public class RxQrBarParseTool {


    private RxQrBarParseTool() {
    }

    private static class Holder {
        static RxQrBarParseTool rxQrBarParseTool = new RxQrBarParseTool();
    }

    public static RxQrBarParseTool getInstance() {
        return Holder.rxQrBarParseTool;
    }

    private static CustomMultiFormatReader multiFormatReader;

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
                rawResult = getMultiFormatReader().decodeWithState(Convert.bitmapToBinary(smallBitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rawResult;
    }


    public Result decodeFromByte(byte[] data, int width, int height) {

        Result rawResult = null;
        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        // Here we are swapping, that's the difference to #11
        int tmp = width;
        width = height;
        height = tmp;

        BinaryBitmap bitmap = Convert.byteToBinay(rotatedData, width, height);

        try {
            rawResult = RxQrBarParseTool.getInstance().getMultiFormatReader().decodeWithState(bitmap);
        } catch (ReaderException e) {
            e.printStackTrace();
        } finally {
            RxQrBarParseTool.getInstance().getMultiFormatReader().reset();
        }

        return rawResult;
    }

    //获取解析的核心类
    public CustomMultiFormatReader getMultiFormatReader() {

        if (multiFormatReader != null)
            return multiFormatReader;

        multiFormatReader = new CustomMultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats = new Vector<>();

        Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<>(5);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
        // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
        Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<>(PRODUCT_FORMATS.size() + 4);
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
        ONE_D_FORMATS.add(BarcodeFormat.ITF);
        Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<>(1);
        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
        Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<>(1);
        DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

        int scanModel = CameraConfig.getInstance().getScanModel();

        // 这里设置可扫描的类型，我这里选择了都支持
        if (scanModel == CameraConfig.BARCODE)
            decodeFormats.addAll(ONE_D_FORMATS);

        if (scanModel == CameraConfig.QRCODE)
            decodeFormats.addAll(QR_CODE_FORMATS);

        if (scanModel == CameraConfig.ALL) {
            decodeFormats.addAll(ONE_D_FORMATS);
            decodeFormats.addAll(QR_CODE_FORMATS);
            decodeFormats.addAll(DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints);
        return multiFormatReader;
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