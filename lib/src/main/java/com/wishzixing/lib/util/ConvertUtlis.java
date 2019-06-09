package com.wishzixing.lib.util;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.common.HybridBinarizer;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.source.BitmapLuminanceSource;
import com.wishzixing.lib.source.PlanarYUVLuminanceSource;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 *  转换生成二进制Bitmap工具
 */
public class ConvertUtlis {

    //BitMap转换二进制Bitmap
    public static BinaryBitmap bitmapToBinary(Bitmap bitmap) {
        return new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(bitmap)));
    }

    //字节转Bitmap
    public static BinaryBitmap byteToBinay(byte[] bytes, int width, int height) {

        //width，height都正常
        PlanarYUVLuminanceSource source = buildLuminanceSource(bytes, width, height);

        if (source == null)
            return null;

        return new BinaryBitmap(new HybridBinarizer(source));
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data   A preview frame.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    private static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {

        Rect rect = CameraConfig.getInstance().getFramingRect();

        Log.e(rect.left + "left", rect.top + "top");
        Log.e(rect.right + "right", rect.bottom + "bottom");

        if ((rect.left == 0 && rect.right == 0) || (rect.top == 0 && rect.bottom == 0))
            return null;

        int previewFormat = CameraConfig.getInstance().getPreviewFormat();
        String previewFormatString = CameraConfig.getInstance().getPreviewFormatString();
        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to support.
            // In theory, it's the only one we should ever care about.
            case ImageFormat.NV21:
                // This format has never been seen in the wild, but is compatible as we only care
                // about the Y channel, so allow it.
            case ImageFormat.NV16:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                        rect.width(), rect.height());
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                            rect.width(), rect.height());
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);
    }

}
