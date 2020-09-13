package com.ailiwean.module_grayscale;

import android.graphics.Rect;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: TranslationScale
 * @Description: 平移操作
 * @Author: SWY
 * @CreateDate: 2020/9/13 12:57 AM
 */
class TranslationScale implements Dispatch {

    int maxOffsetXRange;
    int maxOffsetYRange;

    TranslationScale(int maxOffsetXRange, int maxOffsetYRange) {
        this.maxOffsetXRange = maxOffsetXRange;
        this.maxOffsetYRange = maxOffsetYRange;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
//        int offsetX = (int) (Math.random() * -2 * maxOffsetXRange + maxOffsetXRange);
//        int offsetY = (int) (Math.random() * -2 * maxOffsetYRange + maxOffsetYRange);
//
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//
//                int offset;
//                int current;
//
//                //从右下角开始copy
//                if (offsetX > 0 && offsetY > 0) {
//
//                    offset = (height - 1 - i - offsetY) * width + width - 1 - j - offsetX;
//                    current = (height - i) * width - 1 - j;
//
//                    if ((height - i) < offsetY || (width - j) < offsetX) {
//                        newByte[current] = (byte) 255;
//                    } else newByte[current] = newByte[offset];
//                }
//
//                //从右上角开始copy
//                if (offsetX > 0 && offsetY < 0) {
//
//                    offset = (i - offsetY) * width + width - j - 1 - offsetX;
//                    current = i * width + width - j - 1;
//
//                    if (i > (height + offsetY) || (width - j) < offsetX)
//                        newByte[current] = (byte) 255;
//                    else newByte[current] = newByte[offset];
//
//                }
//
//                //从左下角开始copy
//                if (offsetX < 0 && offsetY > 0) {
//
//                    offset = (height - 1 - i - offsetY) * width + j - offsetX;
//                    current = (height - 1 - i) * width + j;
//
//                    if ((height - 1 - i) < offsetY || j > (width + offsetX))
//                        newByte[current] = (byte) 255;
//                    else newByte[current] = newByte[offset];
//
//                }
//
//                //从左上角开始copy
//                if (offsetX < 0 && offsetY < 0) {
//
//                    offset = (i - offsetY) * width + j - offsetX;
//                    current = i * width + j;
//
//                    if (i >= (height - 1 + offsetY) || j >= (width + offsetX))
//                        newByte[current] = (byte) 255;
//                    else newByte[current] = newByte[offset];
//
//                }
//
//            }
//        }
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();

        int offsetX, offsetY;
        if (Math.random() > 0.5f) {
            offsetX = (int) (Math.random() * -2 * maxOffsetXRange + maxOffsetXRange);
            offsetY = 0;
        } else {
            offsetX = 0;
            offsetY = (int) (Math.random() * -2 * maxOffsetYRange + maxOffsetYRange);
        }

        for (int i = rect.top; i < rect.bottom; i++) {
            for (int j = rect.left; j < rect.right; j++) {

                int offset;
                int current;

                //从右下角开始copy
                if (offsetX >= 0 && offsetY >= 0) {

                    offset = (rect.bottom - i + rect.top - offsetY) * width + rect.right + rect.left - j - offsetX;
                    current = (rect.bottom + rect.top - i) * width + rect.right + rect.left - j;

                    if ((rect.bottom - i) < offsetY || (rect.right - j) < offsetX) {
                        newByte[current] = (byte) 255;
                    } else
                        newByte[current] = newByte[offset];
                }

                //从右上角开始copy
                if (offsetX >= 0 && offsetY <= 0) {

                    offset = (i - offsetY) * width + rect.right + rect.left - j - offsetX;
                    current = i * width + rect.right + rect.left - j;

                    if (i > (rect.bottom + offsetY) || (rect.right - j) < offsetX)
                        newByte[current] = (byte) 255;
                    else newByte[current] = newByte[offset];

                }

                //从左下角开始copy
                if (offsetX <= 0 && offsetY >= 0) {

                    offset = (rect.bottom + rect.top - i - offsetY) * width + j - offsetX;
                    current = (rect.bottom + rect.top - i) * width + j;

                    if ((rect.bottom - i) < offsetY || j > (rect.right + offsetX))
                        newByte[current] = (byte) 255;
                    else newByte[current] = newByte[offset];

                }

                //从左上角开始copy
                if (offsetX <= 0 && offsetY <= 0) {

                    offset = (i - offsetY) * width + j - offsetX;
                    current = i * width + j;

                    if (i > (rect.bottom + offsetY) || j > (rect.right + offsetX))
                        newByte[current] = (byte) 255;
                    else newByte[current] = newByte[offset];

                }

            }
        }
        return newByte;
    }
}
