package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

    public LighSolveAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void cusAction(byte[] data, int dataWidth, int dataHeight) {
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
}
