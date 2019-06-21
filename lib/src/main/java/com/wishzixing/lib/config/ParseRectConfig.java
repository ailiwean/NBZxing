package com.wishzixing.lib.config;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import com.wishzixing.lib.util.MathUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ParseRectConfig {

    Rect parseRect;
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

        PointF ratio = MathUtils.getRatio();

        float ratioX = ratio.x;
        float ratioY = ratio.y;

        int left = (int) ((int) v.getX() * ratioX);
        int top = (int) ((int) v.getY() * ratioY);
        int right = left + v.getMeasuredWidth();
        right *= ratioX;
        int bottom = top + v.getMeasuredHeight();
        bottom *= ratioY;

        parseRect = new Rect(left, top, right, bottom);
    }

    public void go() {
        creat();
        if (parseRect == null)
            parseRect = new Rect(0, 0, 0, 0);
        CameraConfig.getInstance().parseRect = this.parseRect;
    }
}
