package com.NBZxing.lib.views;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.DecodeHintType;
import com.NBZxing.lib.WishLife;
import com.NBZxing.lib.config.AutoFocusConfig;
import com.NBZxing.lib.config.CameraConfig;
import com.NBZxing.lib.config.ParseRectConfig;
import com.NBZxing.lib.config.PointConfig;
import com.NBZxing.lib.config.ScanTypeConfig;
import com.NBZxing.lib.core.zxing.CustomMultiFormatReader;
import com.NBZxing.lib.handler.CameraCoordinateHandler;
import com.NBZxing.lib.listener.LightCallBack;
import com.NBZxing.lib.listener.ResultListener;
import com.NBZxing.lib.listener.SurfaceListener;
import com.NBZxing.lib.manager.CameraManager;
import com.NBZxing.lib.manager.PixsValuesCus;
import com.NBZxing.lib.manager.PixsValuesCusManager;
import com.NBZxing.lib.util.ClearUtils;
import com.NBZxing.lib.util.InactivityTimerUtils;
import com.NBZxing.lib.util.PermissionUtils;
import com.NBZxing.lib.util.YuvUtils;

import java.util.Map;

/***
 *  Created by SWY
 *  DATE 2019/6/9
 *  WishView只负责基础View的业务,WishViewDelegate负责与相机交互的业务
 */
public class WishViewDelegate implements WishLife {

    TextureView textureView;

    Activity mActivity;

    private boolean hasTexture = false;

    InactivityTimerUtils inactivityTimer;

    private SurfaceListener surfaceListener;

    private boolean isAdjust = false;

    public WishViewDelegate(TextureView textureView) {
        this.textureView = textureView;
    }

    @Override
    public void onCreate(Activity activity) {
        CameraManager.init(activity);
        mActivity = activity;
        inactivityTimer = new InactivityTimerUtils(activity);
        CameraManager.get().openDriver(textureView);
        CameraManager.get().registerSurfaceListener(new SurfaceListener() {
            @Override
            public void onVisiable() {

                if (!isAdjust) {
                    adjustSize();
                    isAdjust = true;
                }

                if (WishViewDelegate.this.surfaceListener != null)
                    WishViewDelegate.this.surfaceListener.onVisiable();
            }

            @Override
            public void onNoVisible() {
            }

            @Override
            public void onDestory() {

            }
        });
        YuvUtils.init(activity);
    }

    @Override
    public void onResume() {

        if (!PermissionUtils.hasPermission())
            return;

        if (textureView != null)
            initTexture();

    }

    private void initTexture() {


        if (hasTexture) {
            textureView.post(new Runnable() {
                @Override
                public void run() {
                    refreshCamera();
                }
            });
        } else {

            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, final int width, int height) {

                    textureView.post(new Runnable() {
                        @Override
                        public void run() {

                            refreshCamera();

                        }
                    });
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    if (surfaceListener != null)
                        surfaceListener.onDestory();
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
            hasTexture = true;
        }
    }


    private void adjustSize() {

//        //动态调整Texture
//        int mWidth = PointConfig.getInstance().getShowPoint().x;
//        int mHeight = PointConfig.getInstance().getShowPoint().y;
//
//        int mPreviewWidth = CameraConfig.getInstance().getCameraPoint().y;
//        int mPreviewHeight = CameraConfig.getInstance().getCameraPoint().x;
//
//        if (mHeight >= mPreviewHeight) {
//            int xDiff = (int) (((float) mHeight / mPreviewHeight - 1) * mPreviewWidth);
//            ViewGroup.LayoutParams params = textureView.getLayoutParams();
//            params.width = mWidth + xDiff;
//            params.height = mHeight;
//            textureView.setLayoutParams(params);
//            textureView.setTranslationX(-xDiff / 2);
//        } else {
//            ViewGroup.LayoutParams paramsTexture = textureView.getLayoutParams();
//            paramsTexture.width = mPreviewWidth;
//            paramsTexture.height = mPreviewHeight;
//            textureView.setLayoutParams(paramsTexture);
//        }
    }

    @Override
    public void onStop() {
        CameraManager.get().closeDriver();
        if (surfaceListener != null)
            surfaceListener.onNoVisible();
    }

    @Override
    public void onRestart() {
        CameraManager.get().openDriver(textureView);
    }

    @Override
    public void onBackPressed() {
        inactivityTimer.shutdown();
        hasTexture = false;
        ClearUtils.clear();
    }

    void refreshCamera() {
        CameraManager.get().preViewSurface();
    }

    //增加新的像素解析能力
    public void addNewAbleAction(PixsValuesCus pixsValuesCus) {
        PixsValuesCusManager.getInstance().addNewAction(pixsValuesCus);
    }

    public Camera getCamera() {
        return CameraManager.get().getCamera();
    }

    public WishViewDelegate setSpareAutoFocus(boolean isUser, @AutoFocusConfig.Type int type) {
//        AutoFocusConfig.getInstance().setUser(isUser);
//        AutoFocusConfig.getInstance().setModel(type);
        return this;
    }

    public WishViewDelegate setScanModel(ScanTypeConfig type, Map<DecodeHintType, Object> hintMap) {
        CustomMultiFormatReader.getInstance().setType(type, hintMap);
        return this;
    }

    public WishViewDelegate setParseRectFromView(final View view) {
        ParseRectConfig.getInstance().setParseRectFromView(view);
        return this;
    }

    public WishViewDelegate regSurfaceListener(SurfaceListener surfaceListener) {
        this.surfaceListener = surfaceListener;
        return this;
    }

    public WishViewDelegate regResultListener(ResultListener resultListener) {
        CameraCoordinateHandler.getInstance().regResultListener(resultListener);
        return this;
    }

    public WishViewDelegate regAccountLigListener(LightCallBack lightCallBack) {
        CameraCoordinateHandler.getInstance().regAccountListener(lightCallBack);
        return this;
    }

    public WishViewDelegate setAutoFocusTimeThreshold(long time) {
        //  AutoFocusConfig.getInstance().setTimeThreshold(time);
        return this;
    }

    public void postParsePath(String path) {

    }

}
