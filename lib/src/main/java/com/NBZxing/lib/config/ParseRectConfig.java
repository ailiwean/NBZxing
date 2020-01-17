package com.NBZxing.lib.config;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import com.NBZxing.lib.util.MathUtils;


/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ParseRectConfig {

    Rect parseRect;

    Rect showRect;

    private View parseView;

    private View preView;

    private ParseRectConfig() {

    }

    private static class Holder {
        static ParseRectConfig INSTANCE = new ParseRectConfig();
    }

    public static ParseRectConfig getInstance() {
        return Holder.INSTANCE;
    }

    public ParseRectConfig setParseRectFromView(final View view) {
        this.parseView = view;
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

    public ParseRectConfig setPreview(View preView) {
        this.preView = preView;
        return this;
    }

    /***
     * 根据扫描框位置确定扫码区域
     */
    private void relaScreen() {

        if (parseView == null)
            return;

        PointF ratio = MathUtils.getRatio();

        float ratioX = ratio.x;
        float ratioY = ratio.y;

        int left = (int) parseView.getX();
        int top = (int) parseView.getY();
        int right = left + parseView.getMeasuredWidth();
        int bottom = top + parseView.getMeasuredHeight();
        int oriRight = right;
        int oriLeft = left;
        int oriTop = top;
        int oriBottom = bottom;
        left *= ratioX;
        top *= ratioY;
        right *= ratioX;
        bottom *= ratioY;
        parseRect = new Rect(left, top, right, bottom);
        showRect = new Rect(oriLeft, oriTop, oriRight, oriBottom);
    }

    /***
     * 根据扫描框以及TextureView位置确定扫描区域
     */
    private void relaPreView() {

        if (parseView == null)
            return;

        int preViewHeight = preView.getMeasuredHeight();
        int preViewWidth = preView.getMeasuredWidth();

        PointConfig.getInstance().setShowPoint(new Point(preViewWidth, preViewHeight));

        PointF pointF = MathUtils.getRatio();

        int oriLeft = (int) (parseView.getX() - preView.getX());
        int oriTop = (int) (parseView.getY() - preView.getY());
        int oriRight = oriLeft + parseView.getMeasuredWidth();
        int oriBottom = oriTop + parseView.getMeasuredHeight();

        int parLeft = (int) (oriLeft * pointF.x);
        int parTop = (int) (oriTop * pointF.y);
        int parRight = (int) (oriRight * pointF.x);
        int parBottom = (int) (oriBottom * pointF.y);

        if (parLeft < 0)
            parLeft = 0;

        if (parTop < 0)
            parTop = 0;

        if (parRight > pointF.x * preViewWidth)
            parRight = (int) (pointF.x * preViewWidth);

        if (parBottom > pointF.y * preViewHeight)
            parBottom = (int) (pointF.y * preViewHeight);

        parseRect = new Rect(parLeft, parTop, parRight, parBottom);
        showRect = new Rect((int) parseView.getX(), (int) parseView.getY(), (int) (parseView.getX() + parseView.getMeasuredWidth()), (int) (parseView.getY() + parseView.getMeasuredHeight()));

    }

    public void go() {

        if (preView == null)
            relaScreen();
        else relaPreView();

        if (parseRect == null)
            parseRect = new Rect(0, 0, 0, 0);

        CameraConfig.getInstance().parseRect = this.parseRect;
        CameraConfig.getInstance().showRect = this.showRect;
    }
}
