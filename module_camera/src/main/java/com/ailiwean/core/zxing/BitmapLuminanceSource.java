package com.ailiwean.core.zxing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ailiwean.core.zxing.core.LuminanceSource;


/***
 *  Created by SWY
 */
public class BitmapLuminanceSource extends LuminanceSource {

    private byte[] bitmapPixels;

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());
        for (int i = 0; i < data.length; i++) {
            int pixel = data[i];
            int r = (pixel >> 16) & 0xff; // red
            int g2 = (pixel >> 7) & 0x1fe; // 2 * green
            int b = pixel & 0xff; // blue
            this.bitmapPixels[i] = (byte) ((r + g2 + b) / 4);
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
