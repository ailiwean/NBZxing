package com.ailiwean.module_grayscale;

import android.util.Log;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: OverBrightScale
 * @Description: 过曝光处理使用伽马变换
 * @Author: SWY
 * @CreateDate: 2020/8/15 11:11 AM
 */
class OverBrightScale implements Dispatch {

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        float random = (float) (Math.random() * 10f) + 2f;
        for (int i = 0; i < width * height; i++)
            newByte[i] = (byte) (byte) (255 * Math.pow((newByte[i] & 0xff) / 255f, random));
        return newByte;
    }
}
