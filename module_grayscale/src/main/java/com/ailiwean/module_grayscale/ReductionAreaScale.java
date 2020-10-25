package com.ailiwean.module_grayscale;

import android.graphics.Rect;
import android.util.Log;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: ReductionAreaScale
 * @Description: 缩小处理
 * @Author: SWY
 * @CreateDate: 2020/9/21 10:47 AM
 */
class ReductionAreaScale implements Dispatch {

    GrayScaleDispatch grayScaleDispatch;


    ReductionAreaScale(GrayScaleDispatch dispatch) {
        this.grayScaleDispatch = dispatch;
    }


    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        byte[] emptyByte = new byte[rect.width() * rect.height()];
        int areaSize = 0;
        double step = Math.random() * 2 + 1;

        int reductWidth, reductHeight = 0;

        for (float start_h = rect.top; start_h < rect.bottom; start_h += step) {
            reductHeight++;
            for (float start_w = rect.left; start_w < rect.right; start_w += step) {
                int index = ((int) start_h) * width + (int) start_w;
                emptyByte[areaSize] = newByte[index];
                areaSize++;
            }
        }
        reductWidth = areaSize / reductHeight;
        areaSize = 0;
        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {

                int index = start_h * width + start_w;
                int lef_w = (rect.width() - reductWidth) / 2 + rect.left;
                int rig_w = lef_w + reductWidth;
                int top_h = (rect.height() - reductHeight) / 2 + rect.top;
                int bot_h = top_h + reductHeight;

                if (start_h >= top_h && start_h < bot_h && start_w >= lef_w && start_w < rig_w) {
                    newByte[index] = emptyByte[areaSize++];
                } else newByte[index] = (byte) 255;

            }
        }
        return grayScaleDispatch.dispatch(newByte, width, height, rect);
    }
}
