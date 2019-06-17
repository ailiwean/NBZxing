package com.wishzixing.lib.util;

import android.graphics.PointF;
import android.util.Log;

import com.google.zxing.ResultPoint;
import com.wishzixing.lib.config.CameraConfig;

/***
 *  Created by SWY
 *  DATE 2019/6/16
 *
 */
public class MathUtils {

    public static int getMin(float... values) {

        if (values.length == 0)
            return 0;

        int min = (int) values[0];

        for (float item : values) {
            if (min > item)
                min = (int) item;
        }

        return min;
    }

    public static int getMax(float... values) {

        if (values.length == 0)
            return 0;

        int max = (int) values[0];

        for (float item : values) {
            if (max < item)
                max = (int) item;
        }

        return max;

    }

    public static int getLen(ResultPoint[] p) {

        int x1 = (int) p[0].getX();
        int y1 = (int) p[0].getY();

        int x2 = (int) p[1].getX();
        int y2 = (int) p[1].getY();

        double len = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

        len = Math.sqrt(len);

        Log.e("len:" + len, "len:" + len);

        return 500;
    }

    public static PointF getRatio() {

        int cameraX = CameraConfig.getInstance().getCameraPoint().x;
        int cameraY = CameraConfig.getInstance().getCameraPoint().y;

        if (CameraConfig.getInstance().isPorScreen()) {
            int tem = cameraX;
            cameraX = cameraY;
            cameraY = tem;
        }

        float ratioX = (float) cameraX / CameraConfig.getInstance().getScreenPoint().x;
        float ratioY = (float) cameraY / CameraConfig.getInstance().getScreenPoint().y;

        return new PointF(ratioX, ratioY);
    }
}
