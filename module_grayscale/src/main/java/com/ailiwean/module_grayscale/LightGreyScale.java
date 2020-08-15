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
        short random = (short) (Math.random() * 100);
        for (int i = 0; i < width * height; i++) {
            newByte[i] = (byte) (newByte[i] - random);
        }
        return newByte;
    }
}
