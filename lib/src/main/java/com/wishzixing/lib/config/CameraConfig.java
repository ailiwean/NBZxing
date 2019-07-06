package com.wishzixing.lib.config;

import android.graphics.Point;
import android.graphics.Rect;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 *  Camera用到所有配置参数
 */
public class CameraConfig {

    private static final int DESIRED_SHARPNESS = 30;
    public int previewFormat;
    public String previewFormatString;

    public boolean isAutoZoom;

    //Zxing解析区域对应View
    Rect parseRect;

    //Zxing对应显示解码区域大小
    Rect showRect;

    Point screenPoint;

    Point cameraPoint;

    int tenDesiredZoom;

    int scanModel = 0;

    int autoFocusModel = 0;

    //成功是否播放音效
    boolean isBeep;

    //成功是否震动提醒
    boolean isVibration;

    //同个二维码只扫描回调一次
    boolean isJustOne;

    //控制调焦的时间阀值
    long timeThreshold;

    //是否启用备用调焦
    boolean isUserSpareFocus;

    private CameraConfig() {
    }

    private static class Holer {
        static CameraConfig INSTANCE = new CameraConfig();
    }

    public static CameraConfig getInstance() {
        return Holer.INSTANCE;
    }

    public Point getScreenPoint() {
        return screenPoint;
    }

    public Point getCameraPoint() {
        return cameraPoint;
    }

    public long getTimeThreshold() {
        return timeThreshold;
    }

    public boolean isUserSpareFocus() {
        return isUserSpareFocus;
    }

    public Rect getFramingRect() {
        return parseRect;
    }

    public Rect getShowRect() {
        return showRect;
    }

    public CameraConfig setShowRect(Rect showRect) {
        this.showRect = showRect;
        return this;
    }

    public boolean isAutoZoom() {
        return isAutoZoom;
    }

    public int getScanModel() {
        return scanModel;
    }

    public int getAutoFocusModel() {
        return autoFocusModel;
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public String getPreviewFormatString() {
        return previewFormatString;
    }

    public int getTenDesiredZoom() {
        return tenDesiredZoom;
    }

    public boolean isPorScreen() {
        if (screenPoint == null)
            try {
                throw new Exception("未获取到屏幕尺寸信息");
            } catch (Exception e) {
                e.printStackTrace();
            }
        return screenPoint.x < screenPoint.y;
    }

    public boolean isBeep() {
        return isBeep;
    }

    public boolean isVibration() {
        return isVibration;
    }

    public boolean isJustOne() {
        return isJustOne;
    }
}
