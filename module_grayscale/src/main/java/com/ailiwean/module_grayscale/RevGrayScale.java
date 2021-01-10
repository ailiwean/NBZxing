package com.ailiwean.module_grayscale;

import android.graphics.Rect;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: RevGrayScale
 * @Description: 反色
 * @Author: SWY
 * @CreateDate: 2020/8/22 6:19 PM
 */
class RevGrayScale implements Dispatch {
    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        for (int i = 0; i < width * height; i++)
            data[i] = (byte) (255 - data[i] & 0xff);
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {
                int index = start_h * width + start_w;
                newByte[index] = (byte) (255 - newByte[index] & 0xff);
            }
        }
        return newByte;
    }
}
