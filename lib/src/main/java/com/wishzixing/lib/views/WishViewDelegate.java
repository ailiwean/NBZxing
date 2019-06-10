package com.wishzixing.lib.views;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.Result;
import com.wishzixing.lib.WishLife;
import com.wishzixing.lib.config.Config;
import com.wishzixing.lib.config.ParseRectConfig;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.manager.PixsValuesCusManager;
import com.wishzixing.lib.util.InactivityTimerUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/9
 *  WishView只负责基础View的业务,WishViewDelegate负责与相机交互的业务
 */
public class WishViewDelegate implements WishLife {

    SurfaceView surfaceView;

    private boolean hasSurface = false;

    InactivityTimerUtils inactivityTimer;
    private SurfaceListener surfaceListener;
    private ResultListener resultListener;

    public WishViewDelegate(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    @Override
    public void onCreate(Activity activity) {
        CameraManager.init(activity);
        hasSurface = false;
        inactivityTimer = new InactivityTimerUtils(activity);
    }

    @Override
    public void onResume() {

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            //Camera初始化
            //延迟初始化相机，提升加载速度
            surfaceView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    CameraManager.get().openDriver(surfaceView.getHolder());

                    Config.useDefault();

                    CameraManager.get().initCamera();

                    if (surfaceListener != null)
                        surfaceListener.onCreate();

                }
            }, 100);
        } else {

            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    if (!hasSurface) {
                        hasSurface = true;
                        //延迟初始化相机，提升加载速度
                        surfaceView.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                CameraManager.get().openDriver(surfaceView.getHolder());

                                Config.useDefault();

                                CameraManager.get().initCamera();

                                if (surfaceListener != null)
                                    surfaceListener.onCreate();
                            }
                        }, 100);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;
                    if (surfaceListener != null)
                        surfaceListener.onDestory();
                }
            });
        }

    }

    @Override
    public void onPause() {
        CameraManager.get().closeDriver();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestory() {
        inactivityTimer.shutdown();
    }

    public WishViewDelegate registerSurfaceListener(SurfaceListener surfaceListener) {
        this.surfaceListener = surfaceListener;
        return this;
    }

    public WishViewDelegate registerResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
        return this;
    }

    //增加新的像素解析能力
    public void addNewAbleAction(PixsValuesCus pixsValuesCus) {
        PixsValuesCusManager.getInstance().addNewAction(pixsValuesCus);
    }

    public Camera getCamera() {
        return CameraManager.get().getCamera();
    }

    public WishViewDelegate setParseRectFromView(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                ParseRectConfig.getInstance().setParseRectFromView(view).go();
            }
        });
        return this;
    }

    //surfaceView显示回调
    public static interface SurfaceListener {

        void onCreate();

        void onDestory();

    }

    //扫描结果回调
    public static interface ResultListener {

        void scanSucceed(Result result);

        void scanImgFail();

    }
}
