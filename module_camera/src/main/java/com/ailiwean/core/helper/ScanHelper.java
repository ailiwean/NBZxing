package com.ailiwean.core.helper;

import android.graphics.PointF;
import android.graphics.Rect;

import com.ailiwean.core.Config;
import com.ailiwean.core.zxing.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.InvertedLuminanceSource;
import com.ailiwean.core.zxing.core.ResultPoint;
import com.ailiwean.core.zxing.core.common.HybridBinarizer;

/**
 * @Package: com.ailiwean.core.helper
 * @ClassName: ZxingHelper
 * @Description: Zxing相关数据转换
 * @Author: SWY
 * @CreateDate: 2020/4/23 10:23 AM
 */
public class ScanHelper {

    /***
     * 字节转BinaryBitmap
     */
    public static BinaryBitmap byteToBinaryBitmap(byte[] bytes, int dataWidth, int dataHeight) {
        Rect rect = getScanByteRect(dataWidth, dataHeight);
        PlanarYUVLuminanceSource source = buildLuminanceSource(bytes, dataWidth, dataHeight, rect);
        return new BinaryBitmap(new HybridBinarizer(source));
    }

    /***
     * 旋转矩形 -90
     * @param rect
     * @return
     */
    private static Rect rotateRect(Rect rect) {
        Rect rect1 = new Rect();
        rect1.left = rect.top;
        rect1.top = rect.left;
        rect1.right = rect.bottom;
        rect1.bottom = rect.right;
        return rect1;
    }

    /***
     * 二维码坐标转换屏幕坐标
     * @param point
     * @return
     */
    public static PointF rotatePoint(ResultPoint[] point) {

        if (point == null || point.length == 0)
            return new PointF(0, 0);

        if (Config.scanRect.getScanR() == null)
            return new PointF(0, 0);

        PointF avargPoint = new PointF();
        for (ResultPoint item : point) {
            avargPoint.x += Math.abs(item.getX());
            avargPoint.y += Math.abs(item.getY());
        }
        avargPoint.x /= point.length;
        avargPoint.y /= point.length;
        float preX = Config.scanRect.getPreX();
        float preY = Config.scanRect.getPreY();
        float aspX = preX * Config.scanRatio / (float) Config.scanRect.getScanR().height();
        float aspY = preY * Config.scanRatio / (float) Config.scanRect.getScanR().width();
        float extraX = preX * (1 - Config.scanRatio) / 2;
        float extraY = preY * (1 - Config.scanRatio) / 2;
        return new PointF(preX - aspX * avargPoint.y - extraX, aspY * avargPoint.x + extraY);
    }

    /***
     * 二维码坐标转换屏幕坐标
     * @param point
     * @return
     */
    public static PointF rotatePointR(ResultPoint[] point) {

        if (point == null || point.length == 0)
            return new PointF(0, 0);

        if (Config.scanRect.getScanRR() == null)
            return new PointF(0, 0);

        PointF avargPoint = new PointF();
        for (ResultPoint item : point) {
            avargPoint.x += Math.abs(item.getX());
            avargPoint.y += Math.abs(item.getY());
        }
        avargPoint.x /= point.length;
        avargPoint.y /= point.length;
        float preX = Config.scanRect.getPreX();
        float preY = Config.scanRect.getPreY();
        float aspX = preX * Config.scanRatio / (float) Config.scanRect.getScanRR().width();
        float aspY = preY * Config.scanRatio / (float) Config.scanRect.getScanRR().height();
        float extraX = preX * (1 - Config.scanRatio) / 2;
        float extraY = preY * (1 - Config.scanRatio) / 2;
        return new PointF(aspX * avargPoint.x + extraX, aspY * avargPoint.y + extraY);
    }


    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data A preview frame.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width,
                                                                int height, Rect rect) {
        if ((rect.left == 0 && rect.right == 0) || (rect.top == 0 && rect.bottom == 0)) {
            try {
                throw new Exception("扫码解析区域异常");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                rect.width(), rect.height(), false);
    }

    /***
     * 获取显示区域对应的相机源数据解码区域
     * @return
     */
    public static Rect getScanByteRect(int dataWidth, int dataHeight) {
        if (dataWidth > dataHeight) {
            if (Config.scanRect.getScanR() == null) {
                Config.scanRect.setScanR(new Rect());
                Config.scanRect.getScanR().left = (int) (Config.scanRect.getRect().left * dataHeight);
                Config.scanRect.getScanR().top = (int) (Config.scanRect.getRect().top * dataWidth);
                Config.scanRect.getScanR().right = (int) (Config.scanRect.getRect().right * dataHeight);
                Config.scanRect.getScanR().bottom = (int) (Config.scanRect.getRect().bottom * dataWidth);
                Config.scanRect.setScanR(rotateRect(Config.scanRect.getScanR()));
            }
            return Config.scanRect.getScanR();
        } else {
            if (Config.scanRect.getScanRR() == null) {
                Config.scanRect.setScanRR(new Rect());
                Config.scanRect.getScanRR().left = (int) (Config.scanRect.getRect().left * dataWidth);
                Config.scanRect.getScanRR().top = (int) (Config.scanRect.getRect().top * dataHeight);
                Config.scanRect.getScanRR().right = (int) (Config.scanRect.getRect().right * dataWidth);
                Config.scanRect.getScanRR().bottom = (int) (Config.scanRect.getRect().bottom * dataHeight);
            }
            return Config.scanRect.getScanRR();
        }
    }

    /***
     * 计算探测器获取二维码大小
     */
    public static int getQrLenght(ResultPoint[] point) {

        if (Config.scanRect.getScanR() == null)
            return 0;

        //计算中心点坐标
        PointF avargPoint = new PointF();
        for (ResultPoint item : point) {
            avargPoint.x += item.getX();
            avargPoint.y += item.getY();
        }
        avargPoint.x /= point.length;
        avargPoint.y /= point.length;
        //根据中心点到一点举例计算二维码边长
        int sideA = (int) (avargPoint.x - point[0].getX());
        int sideB = (int) (avargPoint.y - point[0].getY());

        //数据长度转显示长度
        float preX = Config.scanRect.getPreX();
        float aspX = preX / (float) Config.scanRect.getScanR().height();

        return (int) (Math.sqrt(sideA * sideA + sideB * sideB) / Math.sqrt(2) * 2 * aspX);
    }

}
