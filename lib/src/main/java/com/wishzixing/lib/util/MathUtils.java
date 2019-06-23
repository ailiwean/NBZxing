package com.wishzixing.lib.util;

import android.graphics.PointF;

import com.google.zxing.ResultPoint;
import com.wishzixing.lib.config.CameraConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        if (p.length < 4)
            return Integer.MAX_VALUE;

        int x1 = (int) p[0].getX();
        int y1 = (int) p[0].getY();

        int x2 = (int) p[1].getX();
        int y2 = (int) p[1].getY();

        int x3 = (int) p[2].getX();
        int y3 = (int) p[2].getY();

        int x4 = (int) p[3].getX();
        int y4 = (int) p[3].getY();

        List<Point> points = new ArrayList<>();
        points.add(new Point(x1, y1));
        points.add(new Point(x2, y2));
        points.add(new Point(x3, y3));
        points.add(new Point(x4, y4));

        Collections.sort(points);

        Point LT;

        Point LB;

        Point RT;

        Point RB;

        if (points.get(0).y > points.get(1).y) {
            LT = points.get(1);
            LB = points.get(0);
        } else {
            LT = points.get(0);
            LB = points.get(1);
        }

        if (points.get(2).y > points.get(3).y) {
            RT = points.get(3);
            RB = points.get(2);
        } else {
            RT = points.get(2);
            RB = points.get(3);
        }

        return getMax(getDistance(LT, LB), getDistance(LT, RT), getDistance(RT, RB), getDistance(LB, RB));
    }

    public static int getDistance(Point a, Point b) {

        int xD = a.x - b.x;
        int yD = a.y - b.y;

        return (int) Math.sqrt(xD * xD + yD * yD);
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

    public static class Point implements Comparable<Point> {

        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public Point setX(int x) {
            this.x = x;
            return this;
        }

        public int getY() {
            return y;
        }

        public Point setY(int y) {
            this.y = y;
            return this;
        }

        @Override
        public int compareTo(Point o) {
            return x - o.x;
        }
    }
}
