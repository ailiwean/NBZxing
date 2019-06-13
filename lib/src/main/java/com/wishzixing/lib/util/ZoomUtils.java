package com.wishzixing.lib.util;

import android.animation.ValueAnimator;
import android.hardware.Camera;

import com.wishzixing.lib.manager.CameraManager;

/***
 *  Created by SWY
 *  DATE 2019/6/9
 *
 */
public class ZoomUtils {

    static int DURCATION = 300;

    private static ValueAnimator valueAnimator;

    public static void setMaxZoom() {

        Camera camera = CameraManager.get().getCamera();
        if (camera == null)
            return;
        Camera.Parameters p = camera.getParameters();
        if (p == null)
            return;

        if (!p.isZoomSupported())
            return;
        p.setZoom(p.getMaxZoom());
        camera.setParameters(p);
    }

    public static void setMinZoom() {
        Camera camera = CameraManager.get().getCamera();
        if (camera == null)
            return;
        Camera.Parameters p = camera.getParameters();
        if (p == null)
            return;

        if (!p.isZoomSupported())
            return;
        p.setZoom(1);
        camera.setParameters(p);
    }

    public static void zoomToggle() {

        Camera camera = CameraManager.get().getCamera();
        if (camera == null)
            return;
        Camera.Parameters p = camera.getParameters();
        if (p == null)
            return;

        if (p.getZoom() != p.getMaxZoom())
            p.setZoom(p.getMaxZoom());
        else p.setZoom(1);
        camera.setParameters(p);
    }

    public static void animalZoom(int target) {


        final Camera camera = CameraManager.get().getCamera();

        if (camera == null)
            return;

        final Camera.Parameters p = camera.getParameters();
        //防止画面切换闪退
        if (p == null)
            return;

        if (!p.isZoomSupported())
            return;

        if (valueAnimator != null && valueAnimator.isRunning())
            valueAnimator.cancel();
        valueAnimator = ValueAnimator.ofInt(p.getZoom(), target);
        valueAnimator.setDuration(DURCATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                final Camera camera = CameraManager.get().getCamera();

                if (camera == null)
                    return;

                final Camera.Parameters p = camera.getParameters();
                //防止画面切换闪退
                if (p == null)
                    return;

                p.setZoom((Integer) animation.getAnimatedValue());
                camera.setParameters(p);

            }
        });
        valueAnimator.start();

    }

}
