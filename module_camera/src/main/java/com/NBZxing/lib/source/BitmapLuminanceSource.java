<<<<<<< HEAD:module_camera/src/main/java/com/NBZxing/lib/source/BitmapLuminanceSource.java
package com.NBZxing.lib.source;
=======
package com.ailiwean.core.zxing;
>>>>>>> backTexture:module_camera/src/main/java/com/ailiwean/core/zxing/BitmapLuminanceSource.java

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 *  author Vondear
 *  定义解析Bitmap LuminanceSource
 *
 */
public class BitmapLuminanceSource extends LuminanceSource {

    private byte bitmapPixels[];

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());

        // 首先，要取得该图片的像素数组内容
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

        // 将int数组转换为byte数组，也就是取像素值中蓝色值部分作为辨析内容
        for (int i = 0; i < data.length; i++) {
            this.bitmapPixels[i] = (byte) data[i];
        }
    }

    @Override
    public byte[] getMatrix() {
        // 返回我们生成好的像素数据
        return bitmapPixels;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        // 这里要得到指定行的像素数据
        System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
        return row;
    }

}
