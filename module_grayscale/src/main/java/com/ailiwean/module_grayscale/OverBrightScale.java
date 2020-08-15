package com.ailiwean.module_grayscale;

import android.util.Log;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: OverBrightScale
 * @Description: 过曝光处理
 * @Author: SWY
 * @CreateDate: 2020/8/15 11:11 AM
 */
class OverBrightScale implements Dispatch {

    int max = 250;
    int min = 10;
    int step = 5;

    int[] operator = new int[(max - min) / step + 1];

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        if (operator[0] == 0) {
            for (int values = min, index = 0; values <= max; values += step, index++) {
                operator[index] = values;
            }
        }
        short random = (short) (Math.random() * operator.length);
        if (random == operator.length) random -= 1;
        for (int i = 0; i < width * height; i++) {
            if (operator[random] > (newByte[i] & 0xff))
                newByte[i] = 0;
            else
                newByte[i] = (byte) ((newByte[i] & 0xff) - operator[random]);
        }
        return newByte;
    }

}
