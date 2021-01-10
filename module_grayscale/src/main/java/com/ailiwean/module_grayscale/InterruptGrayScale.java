package com.ailiwean.module_grayscale;

import android.graphics.Rect;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: InterruptScale
 * @Description: 开闭操作
 * @Author: SWY
 * @CreateDate: 2020/9/12 10:14 PM
 */
class InterruptGrayScale implements Dispatch {

    //结构元素步长
    private int stepX = 2;
    private int stepY = 2;

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        int offset = (int) (Math.random() * 3) + 1;
        Rect rect = new Rect(0, 0, width, height);
        for (int i = 0; i < offset; i++) {
            openOp(data, width, rect, i);
            closeOp(data, width, rect, i);
        }
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        int offset = (int) (Math.random() * 5) + 1;
        for (int i = 0; i < offset; i++) {
            openOp(newByte, width, rect, i);
            closeOp(newByte, width, rect, i);
        }
        return newByte;
    }

    private void openOp(byte[] newByte, int width, Rect rect, int offset) {

        for (int step_h = rect.top + offset; step_h + stepY < rect.bottom; step_h += stepY) {
            for (int step_w = rect.left + offset; step_w + stepX < rect.right; step_w += stepX) {
                int count = 0;
                int avage = 0;
                int min = Integer.MAX_VALUE;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) < 150)
                            count++;
                        avage += newByte[y_ * width + x_] & 0xff;
                        if ((newByte[y_ * width + x_] & 0xff) < min)
                            min = newByte[y_ * width + x_] & 0xff;
                    }
                }
                if (count == 0) {
                    continue;
                }
                avage /= stepY * stepX;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        newByte[y_ * width + x_] = (byte) (min / 5 * 4);
                    }
                }
            }
        }
    }

    private void closeOp(byte[] newByte, int width, Rect rect, int offset) {
        for (int step_h = rect.top + offset; step_h + stepY < rect.bottom; step_h += stepY) {
            for (int step_w = rect.left + offset; step_w + stepX < rect.right; step_w += stepX) {
                int count = 0;
                int max = 0;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) < 150)
                            count++;
                        if ((newByte[y_ * width + x_] & 0xff) > max)
                            max = newByte[y_ * width + x_] & 0xff;

                    }
                }
                if (count > stepX * stepY / 2) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        newByte[y_ * width + x_] = (byte) max;
                    }
                }
            }
        }
    }

}
