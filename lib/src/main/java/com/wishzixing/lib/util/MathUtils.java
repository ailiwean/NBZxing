package com.wishzixing.lib.util;

import android.graphics.PointF;
import android.util.Log;

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

    public static int getLen(PointF a, PointF b) {

        int cameraX = CameraConfig.getInstance().getCameraPoint().x;
        int cameraY = CameraConfig.getInstance().getCameraPoint().y;

        if (CameraConfig.getInstance().isPorScreen()) {
            int tem = cameraX;
            cameraX = cameraY;
            cameraY = tem;
        }

        float ratioX = (float) cameraX / CameraConfig.getInstance().getScreenPoint().x;
        float ratioY = (float) cameraY / CameraConfig.getInstance().getScreenPoint().y;

        int aX = (int) a.x;
        int aY = (int) a.y;

        int bX = (int) b.x;
        int bY = (int) b.y;
        
        int xDiff = (int) ((aX - bX) * ratioX);
        int yDiff = (int) ((aY - bY) * ratioY);

        int len = (int) Math.sqrt(xDiff * xDiff + yDiff * yDiff);



        return len;
    }


}
