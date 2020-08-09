package com.ailiwean.module_grayscale;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: Dispatch
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/9 6:31 PM
 */
interface Dispatch {
    byte[] dispatch(byte[] data, int width, int height);
}
