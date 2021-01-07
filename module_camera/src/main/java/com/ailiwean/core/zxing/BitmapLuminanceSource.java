package com.ailiwean.core.zxing;

import android.graphics.Bitmap;

import com.ailiwean.core.zxing.core.LuminanceSource;
import com.ailiwean.core.zxing.core.RGBLuminanceSource;


/***
 *  Created by SWY
 */
public class BitmapLuminanceSource extends LuminanceSource {

    RGBLuminanceSource source;

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());
        Bitmap ori = bitmap;
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        ori.recycle();
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), data);
    }

    @Override
    public byte[] getMatrix() {
        // 返回我们生成好的像素数据
        return source.getMatrix();
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        return source.getRow(y, row);
    }

}
