package com.wishzixing.lib.able;


import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.detector.Detector;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.util.ConvertUtlis;
import com.wishzixing.lib.util.MathUtils;
import com.wishzixing.lib.util.ZoomUtils;

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

        DetectorResult decoderResult = null;
        ResultPoint[] points;

        BinaryBitmap binaryBitmap = ConvertUtlis.byteToBinay(data, new Rect(0, 0, x, y));

        if (binaryBitmap == null)
            return;

        try {
            decoderResult = new Detector(binaryBitmap.getBlackMatrix()).detect(null);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        if (decoderResult == null)
            return;

        points = decoderResult.getPoints();

        int len = MathUtils.getLen(points);

        Rect rect = CameraConfig.getInstance().getShowRect();

        if (rect == null)
            return;

        int showRectLen = rect.bottom - rect.top;

        if (len < showRectLen / 4) {
            ZoomUtils.setZoom(ZoomUtils.getZoom() + ZoomUtils.getMaxZoom() / 20);
        }

        if (len > showRectLen / 2) {
            ZoomUtils.setZoom(ZoomUtils.getZoom() - ZoomUtils.getMaxZoom() / 20);
        }

    }

    @Override
    public void stop() {
        handlerThread.interrupt();
        handler.removeCallbacksAndMessages(null);
    }

}
