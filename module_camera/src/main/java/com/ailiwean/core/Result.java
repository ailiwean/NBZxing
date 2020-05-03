package com.ailiwean.core;

import android.graphics.PointF;

import androidx.annotation.NonNull;

/**
 * @Package: com.ailiwean.core
 * @ClassName: Result
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/5/3 3:46 PM
 */
public class Result {

    String text;

    PointF pointF;

    boolean isRotate;

    public String getText() {
        return text;
    }

    public Result setText(String text) {
        this.text = text;
        return this;
    }

    public PointF getPointF() {
        return pointF;
    }

    public Result setPointF(PointF pointF) {
        this.pointF = pointF;
        return this;
    }


    public boolean isRotate() {
        return isRotate;
    }

    public Result setRotate(boolean rotate) {
        isRotate = rotate;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
