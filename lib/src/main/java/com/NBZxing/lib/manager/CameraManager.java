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

package com.NBZxing.lib.manager;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.TextureView;

import com.NBZxing.lib.config.CameraConfig;
import com.NBZxing.lib.config.Config;
import com.NBZxing.lib.handler.CameraCoordinateHandler;
import com.NBZxing.lib.listener.AutoFocusCallback;
import com.NBZxing.lib.listener.PreviewCallback;
import com.NBZxing.lib.listener.SurfaceListener;
import com.NBZxing.lib.util.PermissionUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *  Camera管理,  有关Camera的操作通过该类间接实现
 */
public class CameraManager {

    private static CameraManager cameraManager;

    private WeakReference<Activity> weakReference;

    private SurfaceListener surfaceListener;

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
    private boolean initialized;
    private volatile boolean previewing;
    private volatile boolean isOpenningCamera = false;
    private Camera.Parameters parameters;
    TextureView textureView;

    private void initPreView(TextureView textureView) {
        this.textureView = textureView;
    }

    private CameraManager(Activity activity) {
        this.weakReference = new WeakReference<>(activity);
        // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
        // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
        // the more efficient one shot callback, as the older one can swamp the system and cause it
        // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
        //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
        previewCallback = new PreviewCallback();
        autoFocusCallback = AutoFocusCallback.getInstance();
        //开启回调
        CameraCoordinateHandler.getInstance().start();
    }

    /**
     * Initializes this static object with the Context of the calling Activity.
     *
     * @param activity The Activity which wants to use the camera.
     */
    public static void init(Activity activity) {
        cameraManager = new CameraManager(activity);
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return cameraManager;
    }

    public void openDriver(TextureView textureView) {

        //是否打开相机中，正在打开相机时不允许多线程操作
        if (isOpenningCamera)
            return;

        initPreView(textureView);

        if (!PermissionUtils.hasPermission())
            return;

        if (camera != null)
            return;

        isOpenningCamera = true;

        ThreadManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {

                if (camera == null) {

                    camera = Camera.open();

                    isOpenningCamera = false;

                    if (camera == null) {
                        try {
                            throw new IOException();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    camera.lock();

                    if (!initialized) {
                        initialized = true;
                    }
                    FlashlightManager.enableFlashlight();
                    preViewSurface();
                }
            }
        });

    }

    public void preViewSurface() {

        if (camera == null) {
            openDriver(textureView);
            return;
        }

        if (textureView == null || textureView.getSurfaceTexture() == null)
            return;

        if (previewing)
            return;

        //一些配置参数需要用到camera对象
        Config.useDefault();

        initCamera();

        if (surfaceListener != null && weakReference.get() != null) {
            weakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    surfaceListener.onVisiable();
                }
            });
        }

        try {
            camera.setPreviewTexture(textureView.getSurfaceTexture());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registerSurfaceListener(SurfaceListener surfaceListener) {
        this.surfaceListener = surfaceListener;
    }


    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            //提前置空防止其他对象访问失效Camera
            Camera camera_ = camera;
            camera_.unlock();
            camera = null;
            camera_.stopPreview();
            previewing = false;
            camera_.setPreviewCallback(null);
            camera_.release();
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

    public void initCamera() {

        if (camera == null)
            return;

        parameters = camera.getParameters();
        parameters.set("flash-value", 10);
        parameters.set("flash-mode", "off");
        parameters.set("zoom", "1");
        //连续对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewSize(CameraConfig.getInstance().getCameraPoint().x, CameraConfig.getInstance().getCameraPoint().y);
        int[] previewFpsRange = selectPreviewFpsRange(camera, 60.0f);
        parameters.setPreviewFpsRange(previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        camera.setDisplayOrientation(90);
        try {
            camera.setParameters(parameters);
        } catch (Exception e) {
        }
        startPreview();
        requestPreviewFrame();
    }

    //获取预览帧数
    private int[] selectPreviewFpsRange(Camera camera, float desiredPreviewFps) {
        // The camera API uses integers scaled by a factor of 1000 instead of floating-point frame
        // rates.
        int desiredPreviewFpsScaled = (int) (desiredPreviewFps * 1000.0f);

        // The method for selecting the best range is to minimize the sum of the differences between
        // the desired value and the upper and lower bounds of the range.  This may select a range
        // that the desired value is outside of, but this is often preferred.  For example, if the
        // desired frame rate is 29.97, the range (30, 30) is probably more desirable than the
        // range (15, 30).
        int[] selectedFpsRange = null;
        int minDiff = Integer.MAX_VALUE;
        List<int[]> previewFpsRangeList = camera.getParameters().getSupportedPreviewFpsRange();
        for (int[] range : previewFpsRangeList) {
            int deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            int diff = Math.abs(deltaMin) + Math.abs(deltaMax);
            if (diff < minDiff) {
                selectedFpsRange = range;
                minDiff = diff;
            }
        }
        return selectedFpsRange;
    }

    /***
     * 相机取景字节回调
     */
    public void requestPreviewFrame() {
        if (camera != null) {
            camera.setPreviewCallback(previewCallback);
        }
    }

    public Context getContext() {
        return weakReference.get();
    }

    public Camera getCamera() {
        return camera;
    }

    public PreviewCallback getPreviewCallback() {
        return previewCallback;
    }

    public AutoFocusCallback getAutoFocusCallback() {
        return autoFocusCallback;
    }

}