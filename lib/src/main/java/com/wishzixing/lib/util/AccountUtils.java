package com.wishzixing.lib.util;

import com.wishzixing.lib.config.CameraConfig;

/***
 *  Created by SWY
 *  DATE 2019/6/23
 *
 *  全局静态采集光场强度
 */
public class AccountUtils {

    //上次记录的时间戳
    static long lastRecordTime = System.currentTimeMillis();

    //上次记录的索引
    static int darkIndex = 0;
    //一个历史记录的数组，255是代表亮度最大值
    static long[] darkList = new long[]{255, 255, 255, 255};
    //扫描间隔
    static int waitScanTime = 300;

    static int lastAvDark = 0;

    public static int getAvDark(byte[] data) {

        /***
         *  根据像素点采集光场强度
         */
        if (data.length == 0)
            return lastAvDark;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRecordTime < waitScanTime) {
            return lastAvDark;
        }

        lastRecordTime = currentTime;

        int width = CameraConfig.getInstance().getCameraPoint().x;
        int height = CameraConfig.getInstance().getCameraPoint().y;
        //像素点的总亮度
        long pixelLightCount = 0L;
        //像素点的总数
        long pixeCount = width * height;
        //采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
        int step = 10;
        //data.length - allCount * 1.5f的目的是判断图像格式是不是YUV420格式，只有是这种格式才相等
        //因为int整形与float浮点直接比较会出问题，所以这么比
        if (Math.abs(data.length - pixeCount * 1.5f) < 0.00001f) {
            for (int i = 0; i < pixeCount; i += step) {
                //如果直接加是不行的，因为data[i]记录的是色值并不是数值，byte的范围是+127到—128，
                // 而亮度FFFFFF是11111111是-127，所以这里需要先转为无符号unsigned long参考Byte.toUnsignedLong()
                pixelLightCount += ((long) data[i]) & 0xffL;
            }
            //平均亮度
            long cameraLight = pixelLightCount / (pixeCount / step);
            //更新历史记录
            int lightSize = darkList.length;
            darkList[darkIndex = darkIndex % lightSize] = cameraLight;
            darkIndex++;

            int avDark = 0;
            //判断在时间范围waitScanTime * lightSize内是不是亮度过暗
            for (int i = 0; i < lightSize; i++) {
                avDark += darkList[i];
            }
            avDark /= lightSize;

            lastAvDark = avDark;

            return avDark;
        }

        return lastAvDark;
    }
}
