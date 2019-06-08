package com.wishzixing.lib.config;

import android.graphics.Rect;
import android.view.View;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ParseRectConfig {

    Rect parseRect = new Rect(65, 512, 915, 1312);

    private ParseRectConfig() {

    }

    public static ParseRectConfig getInstance() {
        return new ParseRectConfig();
    }

    public ParseRectConfig setParseRectFromView(final View view) {
        int left = (int) view.getX();
        int top = (int) view.getY();
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        parseRect = new Rect(left, top, right, bottom);
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

    public void go() {
        CameraConfig.getInstance().parseRect = this.parseRect;
    }
}
