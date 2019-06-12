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

        handler.post(new Runnable() {
            @Override
            public void run() {

                BinaryBitmap bitmap = ConvertUtlis.byteToBinay(data, new Rect(0, 0, y, x));

                DetectorResult detectorResult = null;

                if (bitmap == null)
                    return;

                try {
                    detectorResult = new Detector(bitmap.getBlackMatrix()).detect(null);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
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
                float len = (int) Math.sqrt(Math.abs(point1X - point2X) * Math.abs(point1X - point2X) + Math.abs(point1Y - point2Y) * Math.abs(point1Y - point2Y));
                Rect frameRect = CameraConfig.getInstance().getFramingRect();
                if (frameRect != null && camera != null) {
                    float frameWidth = (frameRect.right - frameRect.left) - 200;
                    Camera.Parameters parameters = camera.getParameters();
                    int maxZoom = parameters.getMaxZoom();
                    int zoom = parameters.getZoom();
                    if (parameters.isZoomSupported()) {

                        //放大条件为：扫描区二维码小并且未达到最大倍率
                        if (len <= frameWidth / 4 && zoom < maxZoom) {//二维码在扫描框中的宽度小于扫描框的1/4，放大镜头

                            if (zoom + maxZoom / 5 < maxZoom)
                                zoom += maxZoom / 5;
                            else zoom = maxZoom;
                            parameters.setZoom(zoom);
                            camera.setParameters(parameters);
                        }
                    }
                }

            }
        });

    }

    @Override
    public void stop() {

    }

}
