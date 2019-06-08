package com.wishzixing.lib.able;

import android.hardware.Camera;

import com.wishzixing.lib.manager.PixsValuesCus;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *  根据像素计算是否需要调焦
 */
public class PixsAutoFocusAble implements PixsValuesCus {

    @Override
    public void cusAction(byte[] data, Camera camera) {

    }

    @Override
    public void stop() {

    }

    private PixsAutoFocusAble() {

    }

    private static class Holde {
        static PixsAutoFocusAble INSTANCE = new PixsAutoFocusAble();
    }

    public static PixsAutoFocusAble getInstance() {
        return Holde.INSTANCE;
    }


}
