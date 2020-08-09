package com.ailiwean.module_grayscale;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: LightGreyScale
 * @Description: 浅灰色灰度增强
 * @Author: SWY
 * @CreateDate: 2020/8/9 6:37 PM
 */
class LightGreyScale implements Dispatch {
    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        for (int i = 0; i < width * height; i++) {
            newByte[i] = (byte) (newByte[i] * 2f);
        }
        return newByte;
    }
}
