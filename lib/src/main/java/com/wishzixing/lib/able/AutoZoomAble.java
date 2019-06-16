package com.wishzixing.lib.able;

import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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

        //计算扫描框中的二维码的宽度，两点间距离公式
        float point1X = p[0].getX();
        float point1Y = p[0].getY();
        float point2X = p[1].getX();
        float point2Y = p[1].getY();


        Log.e("size:", p.length + "");

        float len = (int) Math.sqrt(Math.abs(point1X - point2X) * Math.abs(point1X - point2X) + Math.abs(point1Y - point2Y) * Math.abs(point1Y - point2Y));

        PointF[] cd = MathUtils.getPointCD(new PointF(point1X, point1Y), new PointF(point2X, point2Y));

        float point3X = cd[0].x;
        float point3Y = cd[0].y;
        float point4X = cd[1].x;
        float point4Y = cd[1].y;

        Rect frameRect = CameraConfig.getInstance().getFramingRect();
        if (frameRect != null && camera != null) {
            float frameWidth = (frameRect.right - frameRect.left) - 200;
            Camera.Parameters parameters = camera.getParameters();
            int maxZoom = parameters.getMaxZoom();
            int zoom = parameters.getZoom();
            if (parameters.isZoomSupported()) {

                int left = (int) (point1X < point2X ? point1X : point2X);
                int top = (int) (point1Y < point3Y ? point1Y : point3Y);
                int right = (int) (point3X > point4X ? point3X : point4X);
                int bottom = (int) (point2Y > point4Y ? point2Y : point4Y);


//                Log.e("frameRIght:" + frameRect.right, "rectRight:" + right);
//                Log.e("frameBottom:" + frameRect.bottom, "rectBottom:" + bottom);
//                Log.e("frameTop:" + frameRect.top, "rectTop:" + top);
//                Log.e("frameLeft:" + frameRect.left, "rectLeft:" + left);

                //扫码区域已在扫描框内退出
                if (frameRect.contains(left, top, right, bottom))
                    Log.e("在内部", "在内部");
                return;


                //放大条件为：扫描区二维码小并且未达到最大倍率
//                if (len <= frameWidth / 4 && zoom < maxZoom) {//二维码在扫描框中的宽度小于扫描框的1/4，放大镜头
//
//                    if (zoom + maxZoom / 5 < maxZoom)
//                        zoom += maxZoom / 5;
//                    else zoom = maxZoom;
//                    parameters.setZoom(zoom);
//                    camera.setParameters(parameters);
//                }
            }
        }


    }

    @Override
    public void stop() {

    }

}
