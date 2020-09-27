package com.ailiwean.module_grayscale;

import android.graphics.Rect;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: OverDarkScale
 * @Description: 过暗环境伽马处理
 * @Author: SWY
 * @CreateDate: 2020/8/15 10:23 PM
 */
class OverDarkScale implements Dispatch {

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        double random = Math.random() / 2 + 0.4f;
        for (int i = 0; i < width * height; i++)
            newByte[i] = (byte) (byte) (255 * Math.pow((newByte[i] & 0xff) / 255f, random));
        return newByte;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        double random = Math.random() / 2 + 0.4f;
        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {
                int index = start_h * width + start_w;
                newByte[index] = (byte) (byte) (255 * Math.pow((newByte[index] & 0xff) / 255f, random));
            }
        }
        return newByte;
    }
}