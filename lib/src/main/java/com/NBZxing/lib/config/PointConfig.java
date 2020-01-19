package com.NBZxing.lib.config;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.WindowManager;

import com.NBZxing.lib.manager.CameraManager;
import com.NBZxing.lib.util.Utils;

import java.util.regex.Pattern;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class PointConfig implements Config {

    private Point screenPoint = new Point();
    private Point showPoint = new Point();
    private Point cameraPoint;

    private int previewFormat;
    private String previewFormatString;

    private PointConfig() {
        WindowManager manager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getSize(screenPoint);
    }

    public PointConfig setScreenPoint(Point point) {
        this.screenPoint = point;
        return this;
    }

    public PointConfig setShowPoint(Point point) {
        this.showPoint = point;
        return this;
    }

    public Point getShowPoint() {
        return showPoint;
    }

    private void initCameraPoint() {

        if (CameraManager.get() == null)
            return;

        if (CameraManager.get().getCamera() == null)
            return;

        if (cameraPoint != null)
            return;

        Camera camera = CameraManager.get().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenPoint.x;
        screenResolutionForCamera.y = screenPoint.y;
        // preview size is always something like 480*320, other 320*480
        if (screenPoint.x < screenPoint.y) {
            screenResolutionForCamera.x = screenPoint.y;
            screenResolutionForCamera.y = screenPoint.x;
        }

        cameraPoint = getCameraResolution(parameters, screenResolutionForCamera);
    }

    private Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }
        previewFormat = parameters.getPreviewFormat();
        previewFormatString = parameters.get("preview-format");

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        //cameraResolution为最后调整的适合SurfaceView显示的区域大小
        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
            cameraResolution = new Point(
                    (screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

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

    public void config(Camera camera) {

    }

    public void go() {
        initCameraPoint();
        CameraConfig.getInstance().screenPoint = screenPoint;
        CameraConfig.getInstance().cameraPoint = cameraPoint;
        CameraConfig.getInstance().previewFormat = previewFormat;
        CameraConfig.getInstance().previewFormatString = previewFormatString;
    }

}
