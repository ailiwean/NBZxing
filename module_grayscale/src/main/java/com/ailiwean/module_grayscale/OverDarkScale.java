package com.ailiwean.module_grayscale;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: OverDarkScale
 * @Description: 过暗环境伽马处理
 * @Author: SWY
 * @CreateDate: 2020/8/15 10:23 PM
 */
class OverDarkScale implements Dispatch {

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        double random = Math.random() / 2 + 0.4f;
        for (int i = 0; i < width * height; i++)
            newByte[i] = (byte) (byte) (255 * Math.pow((newByte[i] & 0xff) / 255f, random));
        return newByte;
    }
}