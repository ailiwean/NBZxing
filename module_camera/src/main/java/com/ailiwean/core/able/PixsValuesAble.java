package com.ailiwean.core.able;

import android.os.Handler;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: PixsValuesAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 3:27 PM
 */
public abstract class PixsValuesAble {

    Handler handler;

    public PixsValuesAble(Handler handler) {
        this.handler = handler;
    }

    abstract void cusAction(byte[] data, int dataWidth, int dataHeight);
}
