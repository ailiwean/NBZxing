package com.NBZxing.lib.manager;

import android.hardware.Camera;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public interface PixsValuesCus {

    void cusAction(byte[] data, Camera camera, int x, int y);

    void stop();
}
