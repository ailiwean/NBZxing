package com.NBZxing.lib.config;

import android.hardware.Camera;

import com.NBZxing.lib.manager.CameraManager;

import java.util.regex.Pattern;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ZoomConfig {

    private static final int TEN_DESIRED_ZOOM = 27;

    private int tenDesiredZoom = TEN_DESIRED_ZOOM;
    private int previewFormat;
    private String previewFormatString;

    //是否自动缩放
    private boolean isAutoZoom;

    private ZoomConfig() {
        init();
    }

    private static class Holer {
        static ZoomConfig INSTANCE = new ZoomConfig();
    }

    public static ZoomConfig getInstance() {
        return Holer.INSTANCE;
    }

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public ZoomConfig setZoom(int zoom) {
        this.tenDesiredZoom = zoom;
        return this;
    }

    public ZoomConfig setAutoZoom(boolean isAutoZoom) {
        this.isAutoZoom = isAutoZoom;
        return this;
    }

    private void init() {

        if (CameraManager.get().getCamera() == null)
            return;

        Camera.Parameters parameters = CameraManager.get().getCamera().getParameters();
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
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

    public void go() {
        CameraConfig.getInstance().tenDesiredZoom = tenDesiredZoom;
        CameraConfig.getInstance().isAutoZoom = isAutoZoom;
    }
}
