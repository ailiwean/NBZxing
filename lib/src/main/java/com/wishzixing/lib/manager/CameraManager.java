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
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.config.Config;
import com.wishzixing.lib.listener.AutoFocusCallback;
import com.wishzixing.lib.listener.PreviewCallback;
import com.wishzixing.lib.util.PermissionUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *  Camera管理,  有关Camera的操作通过该类间接实现
 */
public class CameraManager {

    private static CameraManager cameraManager;

    private WeakReference<Context> weakReference;
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
    private Camera.Parameters parameters;

    private ShowType showType;

    public enum ShowType {
        SurfaceView,
        TextureView
    }

    private CameraManager(Context context) {

        this.weakReference = new WeakReference<>(context);
        // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
        // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
        // the more efficient one shot callback, as the older one can swamp the system and cause it
        // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
        //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
        previewCallback = new PreviewCallback();
        autoFocusCallback = AutoFocusCallback.getInstance();
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
    public void openDriver(SurfaceHolder holder) {

        if (!PermissionUtils.hasPermission())
            return;

        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                try {

                    throw new IOException();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!initialized) {
                initialized = true;
            }
            FlashlightManager.enableFlashlight();
            this.showType = ShowType.SurfaceView;
        }

        Config.useDefault();
        initCamera();
    }

    public void openDriver(SurfaceTexture surfaceTexture) {

        if (!PermissionUtils.hasPermission())
            return;

        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                try {
                    throw new IOException();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                camera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!initialized) {
                initialized = true;
            }
            FlashlightManager.enableFlashlight();
            this.showType = ShowType.TextureView;
        }

        Config.useDefault();
        initCamera();
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            //提前置空防止其他对象访问失效Camera
            Camera camera_ = camera;
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
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
        startPreview();
        requestPreviewFrame();
        camera.autoFocus(AutoFocusCallback.getInstance());
    }

    public ShowType getShowType() {
        return showType;
    }

    /***
     * 相机重新预览并执行能力
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

    public boolean isPreviewing() {
        return previewing;
    }

    public void setPreviewing(boolean previewing) {
        this.previewing = previewing;
    }

    public PreviewCallback getPreviewCallback() {
        return previewCallback;
    }

    public AutoFocusCallback getAutoFocusCallback() {
        return autoFocusCallback;
    }

}