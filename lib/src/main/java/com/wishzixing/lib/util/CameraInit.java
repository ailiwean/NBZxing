package com.wishzixing.lib.util;

import android.graphics.Point;
import android.view.SurfaceHolder;

import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.config.CameraConfig;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 */
public class CameraInit {

    SurfaceHolder surfaceHolder;

    private int width;

    private int height;

    private CameraInit(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public static CameraInit getInstance(SurfaceHolder surfaceHolder) {
        return new CameraInit(surfaceHolder);
    }

    public CameraInit setCrop(int height, int width) {
        this.width = width;
        this.height = height;
        return this;
    }

    public static void init(SurfaceHolder surfaceHolder) {

        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraConfig.getInstance().getCameraPoint();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);

//            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
//            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
//            setCropWidth(cropWidth);
//            setCropHeight(cropHeight);


        } catch (IOException | RuntimeException ioe) {
            return;
        }
    }


}
