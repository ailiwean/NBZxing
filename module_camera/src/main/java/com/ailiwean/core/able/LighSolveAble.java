package com.ailiwean.core.able;

import android.os.Handler;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.LightHelper;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: LightAble
 * @Description: 根据像素值计算周围环境亮度
 * @Author: SWY
 * @CreateDate: 2020/4/26 3:25 PM
 */
public class LighSolveAble extends PixsValuesAble {

    private int STANDVALUES = 100;

    private boolean isBright = true;

    //上次记录的时间戳
    static long lastRecordTime = System.currentTimeMillis();

    //扫描间隔
    static int waitScanTime = 500;

    public LighSolveAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void cusAction(byte[] data, int dataWidth, int dataHeight, boolean isNative) {
        super.cusAction(data, dataWidth, dataHeight, isNative);
        //非原始数据不采集亮度
        if (!isNative)
            return;
        int avDark = LightHelper.getAvDark(data, dataWidth, dataHeight);
        if (avDark > STANDVALUES && !isBright) {
            isBright = true;
            sendMessage(Config.LIGHT_CHANGE, true);
        }
        if (avDark < STANDVALUES && isBright) {
            isBright = false;
            sendMessage(Config.LIGHT_CHANGE, false);
        }
    }

    @Override
    public boolean isCycleRun(boolean isNative) {
        //非原始数据不走采集亮度任务
        if (!isNative)
            return false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRecordTime < waitScanTime) {
            return false;
        }
        lastRecordTime = currentTime;
        return true;
    }

    /***
     *  采集环境亮度对于原始数据来说是重要且不能舍弃的
     * @return
     */
    @Override
    public boolean isImportant(boolean isNative) {
        return isNative;
    }
}
