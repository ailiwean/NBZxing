package com.wishzixing.lib.config;

import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.wishzixing.lib.util.MathUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ParseRectConfig {

    Rect parseRect;

    Rect showRect;

    private View v;

    private ParseRectConfig() {

    }

    private static class Holder {
        static ParseRectConfig INSTANCE = new ParseRectConfig();
    }

    public static ParseRectConfig getInstance() {
        return Holder.INSTANCE;
    }

    public ParseRectConfig setParseRectFromView(final View view) {
        this.v = view;
        return this;
    }

    public ParseRectConfig setParseRect(Rect rect) {
        this.parseRect = rect;
        return this;
    }

    public ParseRectConfig setParseRect(int left, int top, int right, int bottom) {
        this.parseRect = new Rect(left, top, right, bottom);
        return this;
    }

    /***
     * 初始化时生成合适的解析窗口大小
     */
    private void creat() {

        PointF diff = MathUtils.getRatio();

        float diffX = diff.x;
        float diffY = diff.y;

        int left = (int) v.getX();
        int top = (int) v.getY();
        int right = left + v.getMeasuredWidth();
        int bottom = top + v.getMeasuredHeight();
        int oriRight = right;
        int oriLeft = left;
        int oriTop = top;
        int oriBottom = bottom;
        left -= diffX / 2;
        top -= diffY / 2;
        right -= diffX / 2;
        bottom -= diffY / 2;
        parseRect = new Rect(left, top, right, bottom);
        showRect = new Rect(oriLeft, oriTop, oriRight, oriBottom);
    }

    public void go() {
        creat();
        if (parseRect == null)
            parseRect = new Rect(0, 0, 0, 0);
        CameraConfig.getInstance().parseRect = this.parseRect;
        CameraConfig.getInstance().showRect = this.showRect;
    }
}
