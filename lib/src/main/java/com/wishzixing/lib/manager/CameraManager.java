/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wishzixing.lib.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.google.zxing.LuminanceSource;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.listener.AutoFocusCallback;
import com.wishzixing.lib.listener.PreviewCallback;

import java.io.IOException;

/**
 * @author vondear
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for
 * both preview and decoding.
 */

/***
 *  Camera管理,  有关Camera的操作通过该类间接实现
 */
public class CameraManager {

    static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
    private static final String TAG = CameraManager.class.getSimpleName();
    public static int FRAME_WIDTH = -1;
    public static int FRAME_HEIGHT = -1;
    public static int FRAME_MARGINTOP = -1;
    private static CameraManager cameraManager;

    static {
        int sdkInt;
        try {
            sdkInt = Build.VERSION.SDK_INT;
        } catch (NumberFormatException nfe) {
            // Just to be safe
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
    }

    private final Context context;
    private final boolean useOneShotPreviewCallback;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
     * clear the handler so it will only receive one message.
     */
    private final PreviewCallback previewCallback;
    /**
     * Autofocus callbacks arrive here, and are dispatched to the Handler which requested them.
     */
    private final AutoFocusCallback autoFocusCallback;
    private volatile Camera camera;
    private Rect framingRectInPreview;
    private boolean initialized;
    private volatile boolean previewing;
    private Camera.Parameters parameter;

    private CameraManager(Context context) {

        this.context = context;
        // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
        // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
        // the more efficient one shot callback, as the older one can swamp the system and cause it
        // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
        //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
        useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3 = Cupcake
        previewCallback = new PreviewCallback(useOneShotPreviewCallback);
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * Initializes this static object with the Context of the calling Activity.
     *
     * @param context The Activity which wants to use the camera.
     */
    public static void init(Context context) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context);
        }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return cameraManager;
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    public void openDriver(SurfaceHolder holder) throws IOException {
        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                throw new IOException();
            }
            camera.setPreviewDisplay(holder);

            if (!initialized) {
                initialized = true;
                CameraConfig.getInstance().initCamera(camera);
            }

            FlashlightManager.enableFlashlight();
        }
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (camera != null && previewing) {
            if (!useOneShotPreviewCallback) {
                camera.setPreviewCallback(null);
            }
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
     * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
     * respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message);
            if (useOneShotPreviewCallback) {
                camera.setOneShotPreviewCallback(previewCallback);
            } else {
                camera.setPreviewCallback(previewCallback);
            }
        }
    }

    /**
     * Asks the camera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {

        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
            camera.startPreview();
            camera.autoFocus(autoFocusCallback);
        }
    }

    /***
     *
     *  获取解码区域矩形位置
     *
     * @return
     */
    public Rect getFramingRect() {
        return CameraConfig.getInstance().getParseRect();
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview frame,
     * not UI / screen.
     */
    public Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect rect = new Rect(getFramingRect());
            Point cameraResolution = CameraConfig.getInstance().getCameraPoint();
            Point screenResolution = CameraConfig.getInstance().getScreenPoint();
            //modify here
            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data   A preview frame.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        int previewFormat = CameraConfig.getInstance().getPreviewFormat();
        String previewFormatString = CameraConfig.getInstance().getPreviewFormatString();
        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to support.
            // In theory, it's the only one we should ever care about.
            case ImageFormat.NV21:
                // This format has never been seen in the wild, but is compatible as we only care
                // about the Y channel, so allow it.
            case ImageFormat.NV16:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                        rect.width(), rect.height());
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                            rect.width(), rect.height());
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);
    }

    public Context getContext() {
        return context;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isPreviewing() {
        return previewing;
    }

    public void setPreviewing(boolean previewing) {
        this.previewing = previewing;
    }

    public boolean isUseOneShotPreviewCallback() {
        return useOneShotPreviewCallback;
    }

    public PreviewCallback getPreviewCallback() {
        return previewCallback;
    }

    public AutoFocusCallback getAutoFocusCallback() {
        return autoFocusCallback;
    }

    public final class PlanarYUVLuminanceSource extends LuminanceSource {
        private final byte[] yuvData;
        private final int dataWidth;
        private final int dataHeight;
        private final int left;
        private final int top;

        public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top,
                                        int width, int height) {
            super(width, height);

            if (left + width > dataWidth || top + height > dataHeight) {
                throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
            }

            this.yuvData = yuvData;
            this.dataWidth = dataWidth;
            this.dataHeight = dataHeight;
            this.left = left;
            this.top = top;
        }

        @Override
        public byte[] getRow(int y, byte[] row) {
            if (y < 0 || y >= getHeight()) {
                throw new IllegalArgumentException("Requested row is outside the image: " + y);
            }
            int width = getWidth();
            if (row == null || row.length < width) {
                row = new byte[width];
            }
            int offset = (y + top) * dataWidth + left;
            System.arraycopy(yuvData, offset, row, 0, width);
            return row;
        }

        @Override
        public byte[] getMatrix() {
            int width = getWidth();
            int height = getHeight();

            // If the caller asks for the entire underlying image, save the copy and give them the
            // original data. The docs specifically warn that result.length must be ignored.
            if (width == dataWidth && height == dataHeight) {
                return yuvData;
            }

            int area = width * height;
            byte[] matrix = new byte[area];
            int inputOffset = top * dataWidth + left;

            // If the width matches the full width of the underlying data, perform a single copy.
            if (width == dataWidth) {
                System.arraycopy(yuvData, inputOffset, matrix, 0, area);
                return matrix;
            }

            // Otherwise copy one cropped row at a time.
            byte[] yuv = yuvData;
            for (int y = 0; y < height; y++) {
                int outputOffset = y * width;
                System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
                inputOffset += dataWidth;
            }
            return matrix;
        }

        @Override
        public boolean isCropSupported() {
            return true;
        }

        public int getDataWidth() {
            return dataWidth;
        }

        public int getDataHeight() {
            return dataHeight;
        }

        public Bitmap renderCroppedGreyscaleBitmap() {
            int width = getWidth();
            int height = getHeight();
            int[] pixels = new int[width * height];
            byte[] yuv = yuvData;
            int inputOffset = top * dataWidth + left;

            for (int y = 0; y < height; y++) {
                int outputOffset = y * width;
                for (int x = 0; x < width; x++) {
                    int grey = yuv[inputOffset + x] & 0xff;
                    pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
                }
                inputOffset += dataWidth;
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }

    }

}