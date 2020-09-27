/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.google.android.cameraview;

import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;


/**
 * Encapsulates all the operations related to camera preview in a backward-compatible manner.
 */
abstract class PreviewImpl {

    private Handler cameraHandler;

    interface Callback {
        void onSurfaceChanged();
    }

    private Callback mCallback;

    private int mWidth;

    private int mHeight;

    void setCallback(Callback callback) {
        mCallback = callback;
    }

    void setCameraHandle(Handler cameraHandle) {
        this.cameraHandler = cameraHandle;
    }

    abstract Surface getSurface();

    abstract View getView();

    abstract Class getOutputClass();

    abstract void setDisplayOrientation(int displayOrientation);

    abstract boolean isReady();

    protected void dispatchSurfaceChanged() {
        if (cameraHandler != null)
            cameraHandler.post(() -> mCallback.onSurfaceChanged());
        else mCallback.onSurfaceChanged();
    }

    SurfaceHolder getSurfaceHolder() {
        return null;
    }

    Object getSurfaceTexture() {
        return null;
    }

    void setBufferSize(int width, int height) {
    }

    void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        View v = (View) getView().getParent();
        getView().setTranslationX(0);
        getView().setTranslationY(0);
        if (v == null)
            return;
        if (width > v.getMeasuredWidth())
            getView().setTranslationX((float) -(width - v.getMeasuredWidth()) / 2);
        if (height > v.getMeasuredHeight())
            getView().setTranslationY((float) -(height - v.getMeasuredHeight()) / 2);
        cameraHandler.getLooper().getThread().interrupt();
    }

    int getWidth() {
        return mWidth;
    }

    int getHeight() {
        return mHeight;
    }

    /***
     * 当相机测量出的不支持设定的， 则提供一个支持的参数
     * @param aspectRatio
     */
    public void updateAspectRatio(AspectRatio aspectRatio) {
        if (getView().getParent() instanceof CameraView) {
            ((CameraView) getView().getParent()).setAspectRatio(aspectRatio);
        }
    }
}
