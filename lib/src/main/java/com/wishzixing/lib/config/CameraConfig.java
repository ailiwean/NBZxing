package com.wishzixing.lib.config;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.View;
import android.view.WindowManager;

import com.wishzixing.lib.util.Utils;

import java.util.regex.Pattern;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 *  Camera用到所有配置参数
 */
public class CameraConfig {

    private static final int TEN_DESIRED_ZOOM = 27;
    private static final int DESIRED_SHARPNESS = 30;

    //Zxing解析区域对应View
    public Rect parseRect;

    private Point screenPoint;

    private Point cameraPoint;

    public Context mContext;

    private int previewFormat;
    private String previewFormatString;

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private CameraConfig() {
        mContext = Utils.getAppContext();
        screenPoint = creatScreenPoint();
    }

    private static class Holer {
        public static CameraConfig INSTANCE = new CameraConfig();
    }

    public static CameraConfig getInstance() {
        return Holer.INSTANCE;
    }

    public CameraConfig bindRectView(final View view) {

        int left = (int) view.getX();
        int top = (int) view.getY();
        parseRect = new Rect(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
        return this;
    }

    private Point creatScreenPoint() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point screenPoint = new Point();
        manager.getDefaultDisplay().getSize(screenPoint);
        return screenPoint;
    }

    public Rect getParseRect() {
        return parseRect;
    }

    public Point getScreenPoint() {
        return screenPoint;
    }

    public Point getCameraPoint() {
        return cameraPoint;
    }

    public void initCamera(Camera camera) {

        Camera.Parameters parameters = camera.getParameters();
        previewFormat = parameters.getPreviewFormat();
        previewFormatString = parameters.get("preview-format");

        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenPoint.x;
        screenResolutionForCamera.y = screenPoint.y;
        // preview size is always something like 480*320, other 320*480
        if (screenPoint.x < screenPoint.y) {
            screenResolutionForCamera.x = screenPoint.y;
            screenResolutionForCamera.y = screenPoint.x;
        }

        cameraPoint = getCameraResolution(parameters, screenResolutionForCamera);

        parameters.setPreviewSize(cameraPoint.x, cameraPoint.y);
        setFlash(parameters);
        setZoom(parameters);
        //setSharpness(parameters);
        /***
         * 绝大部分安卓手机中图像传感器方向是横向的，且不能改变，所以orientation是90或是270，也就是说，当点击拍照后保存图片的时候，需要对图片做旋转处理，使其为"自然方向"。 （可能存在一些特殊的定制或是能外接摄像头的安卓机，他们的orientation会是0或者180）
         */
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public String getPreviewFormatString() {
        return previewFormatString;
    }

    private void setFlash(Camera.Parameters parameters) {
        parameters.set("flash-value", 2);
        // This is the standard setting to turn the flash off that all devices should honor.
        parameters.set("flash-mode", "off");
    }

    private void setZoom(Camera.Parameters parameters) {

        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }
        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

    private int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    private Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
            cameraResolution = new Point(
                    (screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    private Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }

        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

}
