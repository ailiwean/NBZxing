package com.wishzixing.lib.able;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;

import com.google.zxing.BinaryBitmap;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.manager.ThreadManager;
import com.wishzixing.lib.util.ConvertUtlis;

/***
 *  Created by SWY
 *  DATE 2019/6/10
 *
 */
public class AutoZoomAble implements PixsValuesCus {

    HandlerThread handlerThread = new HandlerThread("autoZoomAble");
    Handler handler;

    private AutoZoomAble() {
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private static class Holder {
        static AutoZoomAble INSTANCE = new AutoZoomAble();
    }

    public static AutoZoomAble getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void cusAction(final byte[] data, final Camera camera, final int x, final int y) {

        ThreadManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                BinaryBitmap bitmap = ConvertUtlis.byteToBinay(data, new Rect(0, 0, x, y));
            }
        });

    }

    @Override
    public void stop() {

    }

}
