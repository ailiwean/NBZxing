package com.wishzixing.lib.util;

import android.graphics.PointF;

/***
 *  Created by SWY
 *  DATE 2019/6/16
 *
 */
public class MathUtils {


    public static PointF[] getPointCD(PointF a, PointF b) {

        int aX = (int) a.x;
        int aY = (int) a.y;

        int bX = (int) b.x;
        int bY = (int) b.y;

        PointF c = new PointF(aX + bY - aY, aY + aX - bX);
        PointF d = new PointF(bX + bY - aY, bY + aX - bX);

        return new PointF[]{c, d};
    }


}
