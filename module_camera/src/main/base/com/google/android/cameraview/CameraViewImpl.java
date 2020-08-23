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

import android.view.View;

import androidx.annotation.FloatRange;

import java.util.Set;

abstract class CameraViewImpl {

    protected final Callback mCallback;

    protected PreviewImpl mPreview = null;

    CameraViewImpl(Callback callback) {
        mCallback = callback;
    }

    View getView() {
        if (mPreview != null) {
            return mPreview.getView();
        }
        return null;
    }

    public void updatePreView(PreviewImpl preview) {
        this.mPreview = preview;
    }

    /**
     * @return {@code true} if the implementation was able to start the camera session.
     */
    abstract boolean start();

    abstract void stop();

    abstract boolean isCameraOpened();

    abstract void setFacing(int facing);

    abstract int getFacing();

    abstract Set<AspectRatio> getSupportedAspectRatios();

    /**
     * @return {@code true} if the aspect ratio was changed.
     */
    abstract boolean setAspectRatio(AspectRatio ratio);

    abstract AspectRatio getAspectRatio();

    abstract void setAutoFocus(boolean autoFocus);

    abstract boolean getAutoFocus();

    abstract void setFlash(int flash);

    abstract int getFlash();

    abstract void takePicture();

    abstract void setDisplayOrientation(int displayOrientation);

    interface Callback {

        void onCameraOpened();

        void onCameraClosed();

        void onPreviewByte(byte[] data);

        void onPictureTaken(byte[] data);

    }

    /***
     * 以下为扩展
     */
    abstract void toZoomMax();

    abstract void toZoomMin();

    abstract void setZoom(@FloatRange(from = 0, to = 1) float percent);

    abstract void lightOperator(boolean isOpen);

}

