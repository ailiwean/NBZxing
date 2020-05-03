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

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
