package com.wishzixing.lib.views;

import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wishzixing.lib.WishLife;
import com.wishzixing.lib.config.Config;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.util.InactivityTimerUtils;
import com.wishzixing.lib.util.Utils;

/***
 *  Created by SWY
 *  DATE 2019/6/9
 *  WishView只负责基础View的业务,WishViewDelegate负责与相机交互的业务
 */
public class WishViewDelegate implements WishLife {

    SurfaceView surfaceView;

    private boolean hasSurface = false;

    InactivityTimerUtils inactivityTimer;

    public WishViewDelegate(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    @Override
    public void onCreate(Activity activity) {
        Utils.init(activity);
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

                            }
                        }, 100);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;
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
}
