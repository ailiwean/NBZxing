package com.ailiwean.module_grayscale;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: RevGrayScale
 * @Description: 反色
 * @Author: SWY
 * @CreateDate: 2020/8/22 6:19 PM
 */
class RevGrayScale implements Dispatch {
    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        for (int i = 0; i < width * height; i++)
            newByte[i] = (byte) (255 - newByte[i] & 0xff);
        return newByte;
    }
}
