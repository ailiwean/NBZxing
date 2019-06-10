package com.wishzixing.lib.able;

import android.hardware.Camera;

import com.wishzixing.lib.manager.PixsValuesCus;

/***
 *  Created by SWY
 *  DATE 2019/6/2
 *  计算预览区域光场强度回调
 */
public class AccountLigFieAble implements PixsValuesCus {

    private static int STANDVALUES = 100;

    //上次记录的时间戳
    long lastRecordTime = System.currentTimeMillis();

    //上次记录的索引
    int darkIndex = 0;
    //一个历史记录的数组，255是代表亮度最大值
    long[] darkList = new long[]{255, 255, 255, 255};
    //扫描间隔
    int waitScanTime = 300;

    boolean isBright = true;
    private LightCallBack callBack;

    private AccountLigFieAble() {
    }

    @Override
    public void cusAction(byte[] data, Camera camera, int x, int y) {
        account(data, camera);
    }

    @Override
    public void stop() {

    }

    private static class Holder {
        static AccountLigFieAble INSTANCE = new AccountLigFieAble();
    }

    public static AccountLigFieAble getInstance() {
        return Holder.INSTANCE;
    }

    public AccountLigFieAble setCallBack(LightCallBack lightCallBack) {
        callBack = lightCallBack;
        return this;
    }

    private void account(byte[] data, Camera camera) {

        /***
         *  根据像素点采集光场强度
         */
        if (callBack == null)
            return;

        if (data.length == 0)
            return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRecordTime < waitScanTime) {
            return;
        }
        lastRecordTime = currentTime;

        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;
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

            if (avDark > STANDVALUES && !isBright) {
                isBright = true;
                callBack.lightValues(true);
            }
            if (avDark < STANDVALUES && isBright) {
                isBright = false;
                callBack.lightValues(false);
            }
        }
    }

    public static interface LightCallBack {
        void lightValues(boolean isBright);
    }

}

