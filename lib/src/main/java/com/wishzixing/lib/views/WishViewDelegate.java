package com.wishzixing.lib.views;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.wishzixing.lib.WishLife;
import com.wishzixing.lib.config.AutoFocusConfig;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.config.ParseRectConfig;
import com.wishzixing.lib.config.PointConfig;
import com.wishzixing.lib.config.ScanConfig;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.listener.LightCallBack;
import com.wishzixing.lib.listener.ResultListener;
import com.wishzixing.lib.listener.SurfaceListener;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.manager.PixsValuesCusManager;
import com.wishzixing.lib.util.InactivityTimerUtils;
import com.wishzixing.lib.util.PermissionUtils;
import com.wishzixing.lib.util.YuvUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/9
 *  WishView只负责基础View的业务,WishViewDelegate负责与相机交互的业务
 */
public class WishViewDelegate implements WishLife {

    SurfaceView surfaceView;

    TextureView textureView;

    Activity mActivity;

    private boolean hasSurface = false;

    private boolean hasTexture = false;

    InactivityTimerUtils inactivityTimer;

    private SurfaceListener surfaceListener;

    public WishViewDelegate(TextureView textureView) {
        this.textureView = textureView;
    }
        
    @Override
    public void onCreate(Activity activity) {
        CameraManager.init(activity);
        mActivity = activity;
        hasSurface = false;
        inactivityTimer = new InactivityTimerUtils(activity);
        CameraManager.get().openDriver(textureView);
        YuvUtils.init(activity);
    }

    @Override
    public void onResume() {

        if (!PermissionUtils.hasPermission())
            return;

        if (textureView != null)
            initTexture();

    }

    @Override
    public void onPause() {

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

                            //动态调整Texture
                            int mWidth = PointConfig.getInstance().getShowPoint().x;
                            int mHeight = PointConfig.getInstance().getShowPoint().y;

                            int mPreviewWidth = CameraConfig.getInstance().getCameraPoint().y;
                            int mPreviewHeight = CameraConfig.getInstance().getCameraPoint().x;

                            if (mHeight >= mPreviewHeight) {
                                int xDiff = (int) (((float) mHeight / mPreviewHeight - 1) * mPreviewWidth);
                                ViewGroup.LayoutParams params = textureView.getLayoutParams();
                                params.width = mWidth + xDiff;
                                params.height = mHeight;
                                textureView.setLayoutParams(params);
                                textureView.setTranslationX(-xDiff / 2);
                            } else {
                                ViewGroup.LayoutParams paramsTexture = textureView.getLayoutParams();
                                paramsTexture.width = mPreviewWidth;
                                paramsTexture.height = mPreviewHeight;
                                textureView.setLayoutParams(paramsTexture);
                            }
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

    @Override
    public void onStop() {
        if (surfaceListener != null)
            surfaceListener.onNoVisible();
    }

    void refreshCamera() {

        CameraManager.get().preViewSurface();

        if (surfaceListener != null)
            surfaceListener.onVisiable();

    }

    @Override
    public void onDestory() {
        PixsValuesCusManager.getInstance().stop();
        inactivityTimer.shutdown();
        CameraManager.get().closeDriver();
    }

    //增加新的像素解析能力
    public void addNewAbleAction(PixsValuesCus pixsValuesCus) {
        PixsValuesCusManager.getInstance().addNewAction(pixsValuesCus);
    }

    public Camera getCamera() {
        return CameraManager.get().getCamera();
    }

    public WishViewDelegate setSpareAutoFocus(boolean isUser, @AutoFocusConfig.Type int type) {
        AutoFocusConfig.getInstance().setUser(isUser);
        AutoFocusConfig.getInstance().setModel(type);
        return this;
    }

    public WishViewDelegate setScanModel(@ScanConfig.Type int type) {
        ScanConfig.getInstance().setScanModel(type);
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
        AutoFocusConfig.getInstance().setTimeThreshold(time);
        return this;
    }

    public void postParsePath(String path) {

    }

}
