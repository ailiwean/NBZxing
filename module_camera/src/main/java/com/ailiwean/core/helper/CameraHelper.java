package com.ailiwean.core.helper;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.FloatRange;

import com.ailiwean.core.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Camera2 API中一些计算
 */

public class CameraHelper {


    /**
     * 检查是否支持设备自动对焦
     * <p>
     * 很多设备的前摄像头都有固定对焦距离，而没有自动对焦。
     *
     * @param characteristics
     * @return
     */
    public static boolean checkAutoFocus(CameraCharacteristics characteristics) {
        int[] afAvailableModes = new int[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            afAvailableModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        }
        if (afAvailableModes.length == 0 || (afAvailableModes.length == 1 && afAvailableModes[0] == CameraMetadata.CONTROL_AF_MODE_OFF)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查相机支持哪几种focusMode
     *
     * @param cameraCharacteristics
     */
    public void checkFocusMode(CameraCharacteristics cameraCharacteristics) {
        int[] availableFocusModes = new int[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            availableFocusModes = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        }
        for (int focusMode : availableFocusModes != null ? availableFocusModes : new int[0]) {
            if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_OFF) {

            } else if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_MACRO) {

            } else if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE) {

            } else if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_AUTO) {

            }
        }
    }

    /**
     * 匹配指定方向的摄像头，前还是后
     * <p>
     * LENS_FACING_FRONT是前摄像头标志
     *
     * @param cameraCharacteristics
     * @param direction
     * @return
     */
    public static boolean matchCameraDirection(CameraCharacteristics cameraCharacteristics, int direction) {
        //这里设置后摄像头
        Integer facing = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
        }
        return (facing != null && facing == direction) ? true : false;
    }

    /**
     * 获取相机支持最大的调焦距离
     *
     * @param cameraCharacteristics
     * @return
     */
    public static Float getMinimumFocusDistance(CameraCharacteristics cameraCharacteristics) {
        Float distance = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                distance = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return distance;
    }

    /**
     * 获取最大的数字变焦值，也就是缩放值
     *
     * @param cameraCharacteristics
     * @return
     */
    public static Float getMaxZoom(CameraCharacteristics cameraCharacteristics) {
        Float maxZoom = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                maxZoom = cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxZoom;
    }

    /**
     * 计算zoom所对应的rect
     *
     * @param currentZoom 当前的zoom值
     * @return
     */
    public static Rect getZoomRect(CameraCharacteristics cameraCharacteristics, @FloatRange(from = 0, to = 1) float currentZoom) {
        Float maxZoom = getMaxZoom(cameraCharacteristics);

        if (currentZoom == 0) {
            currentZoom = 1;
        } else {
            currentZoom = currentZoom * maxZoom + 1;
        }

        if (currentZoom > maxZoom)
            currentZoom = maxZoom;

        if (currentZoom < 1)
            currentZoom = 1;

        Rect originReact = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            originReact = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        }
        Rect zoomRect;
        try {
            if (originReact == null) {
                return null;
            } else {
                float ratio = (float) 1 / currentZoom;
                int cropWidth = originReact.width() - Math.round((float) originReact.width() * ratio);
                int cropHeight = originReact.height() - Math.round((float) originReact.height() * ratio);
                zoomRect = new Rect(cropWidth / 2, cropHeight / 2, originReact.width() - cropWidth / 2, originReact.height() - cropHeight / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            zoomRect = null;
        }
        return zoomRect;
    }

    /***
     * ImageReader中读取YUV
     */
    public static byte[] readYuv(ImageReader reader) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return null;
        }
        Image image = null;
        image = reader.acquireLatestImage();
        if (image == null)
            return null;
        byte[] data = getByteFromImage(image);
        image.close();
        return data;
    }

    private static byte[] getByteFromImage(Image image) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return null;
            }
            if (image == null || image.getPlanes() == null || image.getPlanes().length == 0)
                return null;
            Image.Plane[] planes = image.getPlanes();
            int remaining0 = planes[0].getBuffer().remaining();
            int remaining2 = planes[2].getBuffer().remaining();
            byte[] yRawSrcBytes = new byte[remaining0];
            byte[] uvRawSrcBytes = new byte[remaining2];
            byte[] nv21 = new byte[remaining0 + remaining2];
            planes[0].getBuffer().get(yRawSrcBytes);
            planes[2].getBuffer().get(uvRawSrcBytes);
            //0b10000001 对应-127,YUV二值化操作
//            for (int i = 0; i < uvRawSrcBytes.length; i++)
//                nv21[yRawSrcBytes.length + i] = (byte) 0b10000001;
            System.arraycopy(yRawSrcBytes, 0, nv21, 0, yRawSrcBytes.length);
            System.arraycopy(uvRawSrcBytes, 0, nv21, yRawSrcBytes.length, uvRawSrcBytes.length);
            return nv21;
        } catch (
                Exception e) {
            return null;
        }

    }


    /***
     * camera1 zoom
     */
    public static void setZoom(@FloatRange(from = 0, to = 1) float z, Camera mCamera) {
        if (mCamera == null)
            return;
        Camera.Parameters p = mCamera.getParameters();
        if (p == null)
            return;
        if (!p.isZoomSupported())
            return;
        int zoom = (int) (z * p.getMaxZoom());
        if (zoom < 1)
            zoom = 1;
        p.setZoom(zoom);
        mCamera.setParameters(p);
    }
}
