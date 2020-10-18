package com.ailiwean.module_grayscale;

import android.graphics.Rect;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: Dispatch
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/9 6:31 PM
 */
public interface Dispatch {

    byte[] dispatch(byte[] data, int width, int height);

    byte[] dispatch(byte[] data, int width, int height, Rect rect);
}
