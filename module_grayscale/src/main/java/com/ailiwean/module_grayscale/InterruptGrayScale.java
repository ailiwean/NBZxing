package com.ailiwean.module_grayscale;

import android.graphics.Rect;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: InterruptScale
 * @Description: 膨胀处理
 * @Author: SWY
 * @CreateDate: 2020/9/12 10:14 PM
 */
class InterruptGrayScale implements Dispatch {

    OverDarkScale overDarkScale;

    public InterruptGrayScale() {
        overDarkScale = new OverDarkScale();
    }

    //结构元素步长
    private int stepX = 3;
    private int stepY = 3;

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        for (int step_h = 0; step_h + stepY < height; step_h += stepY) {
            for (int step_w = 0; step_w + stepX < width; step_w += stepX) {
                int count = 0;
                int avage = 0;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) < 130)
                            count++;
                        avage += newByte[y_ * width + x_] & 0xff;
                    }
                }
                if (count == 0) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) > avage)
                            newByte[y_ * width + x_] = (byte) ((avage & 0xff) / 2);
                        else newByte[y_ * width + x_] = (byte) ((newByte[y_ * width + x_] & 0xff)
                                / 2);
                    }
                }
            }
        }
        return newByte;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        int offset = (int) (Math.random() * 5);

        for (int step_h = rect.top + offset; step_h + stepY < rect.bottom; step_h += stepY) {
            for (int step_w = rect.left + offset; step_w + stepX < rect.right; step_w += stepX) {
                int count = 0;
                int avage = 0;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) < 150)
                            count++;
                        avage += newByte[y_ * width + x_] & 0xff;
                    }
                }
                if (count < stepY * stepX / 4) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) > avage)
                            newByte[y_ * width + x_] = (byte) ((avage & 0xff) / 2);
                        else newByte[y_ * width + x_] = (byte) ((newByte[y_ * width + x_] & 0xff)
                                / 2);
                    }
                }
            }
        }
        return overDarkScale.dispatch(data, width, height, rect);
    }
}
