package com.ailiwean.core.zxing;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;

/**
 * @Package: com.ailiwean.core.zxing
 * @ClassName: ScanRect
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 10:46 AM
 */
public class ScanRect {

    /***
     * 相机阅览过程中采集到的数据宽高
     * 受预览比例{{@link com.google.android.cameraview.AspectRatio}} 与CameraView的宽高影响
     */
    private int dataX;
    private int dataY;

    /***
     * 可预览到的控件宽高(即CameraView的宽高)
     */
    private int preX;
    private int preY;

    /***
     * TextureView的宽高分别与CameraView宽高的差值
     * 为了处理画面拉伸TextureView的宽高比例会同步{{@link #dataX}{ {@link #dataY}的宽高比例，
     * 因此要么是宽要么是高往往会超出CameraView，这里主要记录该值
     */
    private int extraX;
    private int extraY;

    /***
     *该矩形实现了剪裁比例同步，即将外部设定的View区域对应到CameraView/TxtureView上
     *参照 {@link com.google.android.cameraview.CameraView#defineScanParseRect(View view)}
     */
    private RectF r = new RectF();

    /***
     * 将{{@link #r}对应到默认相机采集到数据上形成一个矩形
     */
    private Rect scanR = null;

    /***
     * 将{{@link #r}对应到相机采集到数据旋转90°上形成一个矩形
     */
    @Deprecated
    private Rect scanRR = null;

    public void setRect(RectF r) {
        //默认适配的是0度，也就是手机垂直方向拿着
        //朝左倾斜90
        if (Config.is90() && r != null) {
            r = ScanHelper.adapter90(r);
        }
        //朝右倾斜90
        if (Config.is270() && r != null) {
            r = ScanHelper.adapter270(r);
        }

        this.r = r;

        scanR = null;
        scanRR = null;
    }

    public Rect getScanR() {
        return scanR;
    }

    public ScanRect setScanR(Rect scanR) {
        this.scanR = scanR;
        return this;
    }

    @Deprecated
    public Rect getScanRR() {
        return scanRR;
    }

    @Deprecated
    public ScanRect setScanRR(Rect scanRR) {
        this.scanRR = scanRR;
        return this;
    }

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

    public int getExtraX() {
        return extraX;
    }

    public ScanRect setExtraX(int extraX) {
        this.extraX = extraX;
        return this;
    }

    public int getExtraY() {
        return extraY;
    }

    public ScanRect setExtraY(int extraY) {
        this.extraY = extraY;
        return this;
    }
}
