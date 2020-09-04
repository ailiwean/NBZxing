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

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.ailiwean.core.Config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Set;

public class CameraView extends FrameLayout {

    /**
     * The camera device faces the opposite direction as the device's screen.
     */
    public static final int FACING_BACK = Constants.FACING_BACK;

    /**
     * The camera device faces the same direction as the device's screen.
     */
    public static final int FACING_FRONT = Constants.FACING_FRONT;
    private PreviewImpl preview;
    private Handler cameraHandler;

    /**
     * Direction the camera faces relative to device screen.
     */
    @IntDef({FACING_BACK, FACING_FRONT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Facing {
    }

    /**
     * Flash will not be fired.
     */
    public static final int FLASH_OFF = Constants.FLASH_OFF;

    /**
     * Flash will always be fired during snapshot.
     */
    public static final int FLASH_ON = Constants.FLASH_ON;

    /**
     * Constant emission of light during preview, auto-focus and snapshot.
     */
    public static final int FLASH_TORCH = Constants.FLASH_TORCH;

    /**
     * Flash will be fired automatically when required.
     */
    public static final int FLASH_AUTO = Constants.FLASH_AUTO;

    /**
     * Flash will be fired in red-eye reduction mode.
     */
    public static final int FLASH_RED_EYE = Constants.FLASH_RED_EYE;

    /**
     * The mode for for the camera device's flash control
     */
    @IntDef({FLASH_OFF, FLASH_ON, FLASH_TORCH, FLASH_AUTO, FLASH_RED_EYE})
    public @interface Flash {
    }

    protected Handler mainHand = new Handler(Looper.getMainLooper());

    CameraViewImpl mImpl;

    private final CallbackBridge mCallbacks;

    private boolean mAdjustViewBounds;

    private final DisplayOrientationDetector mDisplayOrientationDetector;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("WrongConstant")
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            mCallbacks = null;
            mDisplayOrientationDetector = null;
            return;
        }
        // Internal setup
        mCallbacks = new CallbackBridge();
        if (Build.VERSION.SDK_INT < 21) {
            mImpl = new Camera1(mCallbacks);
        } else {
            mImpl = new Camera2(mCallbacks, context);
        }
        // Attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr,
                R.style.Widget_CameraView);
        mAdjustViewBounds = a.getBoolean(R.styleable.CameraView_android_adjustViewBounds, false);
        setFacing(a.getInt(R.styleable.CameraView_facing, FACING_BACK));
        String aspectRatio = a.getString(R.styleable.CameraView_aspectRatio);
        if (aspectRatio != null) {
            setAspectRatio(AspectRatio.parse(aspectRatio));
        } else {
            setAspectRatio(Constants.DEFAULT_ASPECT_RATIO);
        }
        setAutoFocus(a.getBoolean(R.styleable.CameraView_autoFocus, true));
        setFlash(a.getInt(R.styleable.CameraView_flash, Constants.FLASH_AUTO));
        a.recycle();
        // Display orientation detector
        mDisplayOrientationDetector = new DisplayOrientationDetector(context) {
            @Override
            public void onDisplayOrientationChanged(int displayOrientation) {
                mImpl.setDisplayOrientation(displayOrientation);
            }
        };
    }

    @NonNull
    private PreviewImpl createPreviewImpl(Context context) {
        preview = new TextureViewPreview(context, this);
        preview.setCameraHandle(cameraHandler);
        return preview;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mDisplayOrientationDetector.enable(ViewCompat.getDisplay(this));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!isInEditMode()) {
            mDisplayOrientationDetector.disable();
        }
        super.onDetachedFromWindow();
    }

    RectF r = new RectF();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mImpl.getView() == null)
            return;
        // Measure the TextureView
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        AspectRatio ratio = getAspectRatio();
        if (mDisplayOrientationDetector.getLastKnownDisplayOrientation() % 180 == 0) {
            ratio = ratio.inverse();
        }
        assert ratio != null;
        //当显示的宽高比，与相机输出的宽高比不同时
        //当实际略宽时, 调整高度保证与输出比例相同
        if (height < width * ratio.getY() / ratio.getX()) {
            mImpl.getView().measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(width * ratio.getY() / ratio.getX(),
                            MeasureSpec.EXACTLY));
        }
        //当实际略高时，调整宽度保证与输出比例相同
        else {
            mImpl.getView().measure(
                    MeasureSpec.makeMeasureSpec(height * ratio.getX() / ratio.getY(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        }
    }

    /***
     * 确定扫码区域
     */
    protected void defineScanParseRect(View view) {

        if (view == null)
            return;

        Config.scanRect.setScanR(null);
        Config.scanRect.setScanRR(null);
        Config.scanRect.setRect(null);

        int oriHeight = getMeasuredHeight();
        int oriWidth = getMeasuredWidth();

        AspectRatio ratio = getAspectRatio();
        if (mDisplayOrientationDetector.getLastKnownDisplayOrientation() % 180 == 0) {
            ratio = ratio.inverse();
        }

        if (ratio == null)
            return;

        //当实际略宽时, 调整高度保证与输出比例相同
        if (oriHeight < oriWidth * ratio.getY() / ratio.getX()) {
            int measureHeight = (int) (oriWidth * ratio.getY() / (float) ratio.getX());
            float expectRatio = (measureHeight - oriHeight) / 2f / measureHeight;
            float[] edgeRatio = findEdgeRatio(view, oriWidth, measureHeight);
            r.left = edgeRatio[0];
            r.right = edgeRatio[2];
            r.top = expectRatio + edgeRatio[1];
            r.bottom = expectRatio + edgeRatio[3];
            Config.scanRect.setRect(r);
            Config.scanRect.setExtraX(0);
            Config.scanRect.setExtraY(measureHeight - oriHeight);
        }
        //当实际略高时，调整宽度保证与输出比例相同
        else {
            int measureWidht = (int) (oriHeight * ratio.getX() / (float) ratio.getY());
            float expectRatio = (measureWidht - oriWidth) / 2f / measureWidht;
            float[] edgeRatio = findEdgeRatio(view, measureWidht, oriHeight);
            r.left = expectRatio + edgeRatio[0];
            r.right = expectRatio + edgeRatio[2];
            r.top = edgeRatio[1];
            r.bottom = edgeRatio[3];
            Config.scanRect.setRect(r);
            Config.scanRect.setExtraX(measureWidht - oriWidth);
            Config.scanRect.setExtraY(0);
        }
        Config.scanRect.setPreX(oriWidth);
        Config.scanRect.setPreY(oriHeight);

        mImpl.rectMeteringWithFocus();
    }

    /***
     * 确定可视区域比例
     * @param view
     * @param realWidht
     * @param realHeight
     * @return
     */
    private float[] findEdgeRatio(View view, int realWidht, int realHeight) {

        int[] parseRect_screen = new int[2];
        view.getLocationOnScreen(parseRect_screen);

        int[] parseRect = new int[4];
        parseRect[0] = parseRect_screen[0];
        parseRect[1] = parseRect_screen[1];
        parseRect[2] = parseRect[0] + view.getMeasuredWidth();
        parseRect[3] = parseRect[1] + view.getMeasuredHeight();

        int[] surfaceRect_screen = new int[2];
        this.getLocationOnScreen(surfaceRect_screen);

        float[] edgeRatio = new float[4];
        edgeRatio[0] = (parseRect[0] - surfaceRect_screen[0]) / (float) realWidht;
        edgeRatio[1] = (parseRect[1] - surfaceRect_screen[1]) / (float) realHeight;
        edgeRatio[2] = (parseRect[2] - surfaceRect_screen[0]) / (float) realWidht;
        edgeRatio[3] = (parseRect[3] - surfaceRect_screen[1]) / (float) realHeight;

        for (int i = 0; i < 4; i++) {
            if (edgeRatio[i] < 0)
                edgeRatio[i] = 0;
            if (edgeRatio[i] > 1)
                edgeRatio[i] = 1;
        }
        return edgeRatio;
    }


    protected void provideCameraHandler(Handler handler) {
        this.cameraHandler = handler;
        if (preview != null)
            preview.setCameraHandle(cameraHandler);
    }

    protected PreviewImpl getPreviewImpl() {
        return preview;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.facing = getFacing();
        state.ratio = getAspectRatio();
        state.autoFocus = getAutoFocus();
        state.flash = getFlash();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setFacing(ss.facing);
        setAspectRatio(ss.ratio);
        setAutoFocus(ss.autoFocus);
        setFlash(ss.flash);
    }

    private void initPreView() {
        mImpl.updatePreView(createPreviewImpl(getContext()));
    }

    public void openCameraBefore() {
        removeAllViews();
        initPreView();
    }

    /**
     * Open a camera device and start showing camera preview. This is typically called from
     */
    protected void start() {
        if (!isCameraOpened() && !mImpl.start()) {
            //store the state ,and restore this state after fall back o Camera1
            Parcelable state = onSaveInstanceState();
            // Camera2 uses legacy hardware layer; fall back to Camera1
            mImpl = new Camera1(mCallbacks);
            mImpl.updatePreView(preview);
            onRestoreInstanceState(state);
            mImpl.start();
        }
    }

    /**
     * Stop camera preview and close the device. This is typically called from
     * {@link Activity}.
     */
    protected void stop() {
        mImpl.stop();
    }

    /**
     * @return {@code true} if the camera is opened.
     */
    public boolean isCameraOpened() {
        return mImpl.isCameraOpened();
    }

    /**
     * Add a new callback.
     *
     * @param callback The {@link Callback} to add.
     * @see #removeCallback(Callback)
     */
    public void addCallback(@NonNull Callback callback) {
        mCallbacks.add(callback);
    }

    /**
     * Remove a callback.
     *
     * @param callback The {@link Callback} to remove.
     * @see #addCallback(Callback)
     */
    public void removeCallback(@NonNull Callback callback) {
        mCallbacks.remove(callback);
    }

    /**
     * @param adjustViewBounds {@code true} if you want the CameraView to adjust its bounds to
     *                         preserve the aspect ratio of camera.
     * @see #getAdjustViewBounds()
     */
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (mAdjustViewBounds != adjustViewBounds) {
            mAdjustViewBounds = adjustViewBounds;
            requestLayout();
        }
    }

    /**
     * @return True when this CameraView is adjusting its bounds to preserve the aspect ratio of
     * camera.
     * @see #setAdjustViewBounds(boolean)
     */
    public boolean getAdjustViewBounds() {
        return mAdjustViewBounds;
    }

    /**
     * Chooses camera by the direction it faces.
     *
     * @param facing The camera facing. Must be either {@link #FACING_BACK} or
     *               {@link #FACING_FRONT}.
     */
    public void setFacing(@Facing int facing) {
        mImpl.setFacing(facing);
    }

    /**
     * Gets the direction that the current camera faces.
     *
     * @return The camera facing.
     */
    @Facing
    public int getFacing() {
        //noinspection WrongConstant
        return mImpl.getFacing();
    }

    /**
     * Gets all the aspect ratios supported by the current camera.
     */
    public Set<AspectRatio> getSupportedAspectRatios() {
        return mImpl.getSupportedAspectRatios();
    }

    /**
     * Sets the aspect ratio of camera.
     *
     * @param ratio The {@link AspectRatio} to be set.
     */
    public void setAspectRatio(@NonNull AspectRatio ratio) {
        if (mImpl.setAspectRatio(ratio)) {
            mainHand.post(this::requestLayout);
        }
    }

    /**
     * Gets the current aspect ratio of camera.
     *
     * @return The current {@link AspectRatio}. Can be {@code null} if no camera is opened yet.
     */
    @Nullable
    public AspectRatio getAspectRatio() {
        return mImpl.getAspectRatio();
    }

    /**
     * Enables or disables the continuous auto-focus mode. When the current camera doesn't support
     * auto-focus, calling this method will be ignored.
     *
     * @param autoFocus {@code true} to enable continuous auto-focus mode. {@code false} to
     *                  disable it.
     */
    public void setAutoFocus(boolean autoFocus) {
        mImpl.setAutoFocus(autoFocus);
    }

    /**
     * Returns whether the continuous auto-focus mode is enabled.
     *
     * @return {@code true} if the continuous auto-focus mode is enabled. {@code false} if it is
     * disabled, or if it is not supported by the current camera.
     */
    public boolean getAutoFocus() {
        return mImpl.getAutoFocus();
    }

    /**
     * Sets the flash mode.
     *
     * @param flash The desired flash mode.
     */
    public void setFlash(@Flash int flash) {
        mImpl.setFlash(flash);
    }

    /**
     * Gets the current flash mode.
     *
     * @return The current flash mode.
     */
    @Flash
    public int getFlash() {
        //noinspection WrongConstant
        return mImpl.getFlash();
    }

    /**
     * Take a picture. The result will be returned to
     * {@link Callback#onPictureTaken(CameraView, byte[])}.
     */
    public void takePicture() {
        if (isCameraOpened())
            mImpl.takePicture();
    }

    private class CallbackBridge implements CameraViewImpl.Callback {

        private final ArrayList<Callback> mCallbacks = new ArrayList<>();

        private boolean mRequestLayoutOnOpen;

        CallbackBridge() {
        }

        public void add(Callback callback) {
            mCallbacks.add(callback);
        }

        public void remove(Callback callback) {
            mCallbacks.remove(callback);
        }

        @Override
        public void onCameraOpened() {
            if (mRequestLayoutOnOpen) {
                mRequestLayoutOnOpen = false;
                requestLayout();
            }
            for (Callback callback : mCallbacks) {
                callback.onCameraOpened(CameraView.this);
            }
        }

        @Override
        public void onCameraClosed() {
            for (Callback callback : mCallbacks) {
                callback.onCameraClosed(CameraView.this);
            }
        }

        //阅览数据回调
        @Override
        public void onPreviewByte(byte[] data) {
            for (Callback callback : mCallbacks) {
                callback.onPreviewByte(CameraView.this, data);
            }
        }

        @Override
        public void onPictureTaken(byte[] data) {
            for (Callback callback : mCallbacks) {
                callback.onPictureTaken(CameraView.this, data);
            }
        }

        public void reserveRequestLayoutOnOpen() {
            mRequestLayoutOnOpen = true;
        }
    }

    protected static class SavedState extends BaseSavedState {

        @Facing
        int facing;

        AspectRatio ratio;

        boolean autoFocus;

        @Flash
        int flash;

        @SuppressWarnings("WrongConstant")
        public SavedState(Parcel source, ClassLoader loader) {
            super(source);
            facing = source.readInt();
            ratio = source.readParcelable(loader);
            autoFocus = source.readByte() != 0;
            flash = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(facing);
            out.writeParcelable(ratio, 0);
            out.writeByte((byte) (autoFocus ? 1 : 0));
            out.writeInt(flash);
        }

        public static final Creator<SavedState> CREATOR
                = new ClassLoaderCreator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return createFromParcel(in, null);
            }

            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }

    /**
     * Callback for monitoring events about {@link CameraView}.
     */
    @SuppressWarnings("UnusedParameters")
    public abstract static class Callback {

        /**
         * Called when camera is opened.
         *
         * @param cameraView The associated {@link CameraView}.
         */
        public void onCameraOpened(CameraView cameraView) {
        }

        /**
         * Called when camera is closed.
         *
         * @param cameraView The associated {@link CameraView}.
         */
        public void onCameraClosed(CameraView cameraView) {
        }

        /**
         * Called when a picture is taken.
         *
         * @param cameraView The associated {@link CameraView}.
         * @param data       JPEG data.
         */
        public void onPictureTaken(CameraView cameraView, byte[] data) {
        }

        /***
         * YUV数据回调
         */
        public void onPreviewByte(CameraView cameraView, byte[] data) {

        }

    }

}
