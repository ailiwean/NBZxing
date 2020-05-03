package com.ailiwean.core.zxing;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @Package: com.ailiwean.core.zxing
 * @ClassName: ScanRect
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 10:46 AM
 */
public class ScanRect {

    int dataX;
    int dataY;

    int preX;
    int preY;

    RectF r;
    Rect scanR;
    Rect scanRR;

    /***
     * 实际预览区域/总预览区域
     * @param r
     */
    public void setRect(RectF r) {
        this.r = r;
        scanR = null;
    }

    /***
     * 预览区域对应像素区域
     * @return
     */
    public Rect getScanR() {
        return scanR;
    }

    public ScanRect setScanR(Rect scanR) {
        this.scanR = scanR;
        return this;
    }


    public Rect getScanRR() {
        return scanRR;
    }

    public ScanRect setScanRR(Rect scanRR) {
        this.scanRR = scanRR;
        return this;
    }

    /***
     * @param x 相机返回数据x
     * @param y 相机返回的数据y
     */
    public void setData(int x, int y) {
        this.dataX = x;
        this.dataY = y;
    }

    public int getDataX() {
        return dataX;
    }

    public ScanRect setDataX(int dataX) {
        this.dataX = dataX;
        return this;
    }

    public int getDataY() {
        return dataY;
    }

    public ScanRect setDataY(int dataY) {
        this.dataY = dataY;
        return this;
    }

    public RectF getRect() {
        return r;
    }

    public int getPreX() {
        return preX;
    }

    public ScanRect setPreX(int preX) {
        this.preX = preX;
        return this;
    }

    public int getPreY() {
        return preY;
    }

    public ScanRect setPreY(int preY) {
        this.preY = preY;
        return this;
    }
}
