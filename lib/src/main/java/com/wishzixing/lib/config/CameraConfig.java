package com.wishzixing.lib.config;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;

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

    //Zxing解析区域对应View
    Rect parseRect;

    Point screenPoint;

    Point cameraPoint;

    int tenDesiredZoom;

    int scanModel = 0;

    int autoFocusModel = 0;

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

    public Rect getFramingRect() {
        return parseRect;
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
}
