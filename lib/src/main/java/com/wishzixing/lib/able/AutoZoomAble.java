package com.wishzixing.lib.able;


import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import com.wishzixing.lib.cv4j.core.datamodel.CV4JImage;
import com.wishzixing.lib.cv4j.core.datamodel.Rect;
import com.wishzixing.lib.cv4j.image.util.QRCodeScanner;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.util.YuvUtils;

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



    }

    @Override
    public void stop() {

    }

}
