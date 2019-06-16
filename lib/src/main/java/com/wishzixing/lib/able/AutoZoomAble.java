package com.wishzixing.lib.able;

import android.graphics.PointF;
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

        BinaryBitmap bitmap = ConvertUtlis.byteToBinay(data, new Rect(0, 0, x, y));

        DetectorResult detectorResult = null;

        if (bitmap == null)
            return;

        try {
            try {
                detectorResult = new Detector(bitmap.getBlackMatrix()).detect(null);
            } catch (FormatException e) {
                e.printStackTrace();
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        if (detectorResult == null) {
            return;
        }

        ResultPoint[] p = detectorResult.getPoints();

        if (p.length < 4)
            return;

        float point1X = p[0].getX();
        float point1Y = p[0].getY();
        float point2X = p[1].getX();
        float point2Y = p[1].getY();
        float point3X = p[2].getX();
        float point3Y = p[2].getY();
        float point4X = p[3].getX();
        float point4Y = p[3].getY();


        int len = MathUtils.getLen(new PointF(point3X, point3Y), new PointF(point4X, point4Y));

        Rect frameRect = CameraConfig.getInstance().getFramingRect();
        if (frameRect != null && camera != null) {
            float frameWidth = (frameRect.right - frameRect.left);
            Camera.Parameters parameters = camera.getParameters();
            int maxZoom = parameters.getMaxZoom();
            int zoom = parameters.getZoom();
            if (parameters.isZoomSupported()) {


                //   Log.e("frameWidht:" + frameWidth, "QRWidht:" + len);

//                int left = MathUtils.getMin(point1X, point2X, point3X, point4X);
//                int top = MathUtils.getMin(point1Y, point2Y, point3Y, point4Y);
//                int right = MathUtils.getMax(point1X, point2X, point3X, point4X);
//                int bottom = MathUtils.getMax(point1Y, point2Y, point3Y, point4Y);

                //放大条件为：扫描区二维码小并且未达到最大倍率
                if (len <= frameWidth / 4 && zoom < maxZoom) {//二维码在扫描框中的宽度小于扫描框的1/3，放大镜头
                    if (zoom + maxZoom / 10 < maxZoom) {
                        ZoomUtils.setZoom(zoom + maxZoom / 10);
                    } else ZoomUtils.setZoom(maxZoom);
                }
            }
        }

    }

    @Override
    public void stop() {

    }

}
