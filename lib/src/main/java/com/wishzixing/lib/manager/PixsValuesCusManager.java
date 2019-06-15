package com.wishzixing.lib.manager;

import android.graphics.Point;
import android.hardware.Camera;

import com.wishzixing.lib.able.AccountLigFieAble;
import com.wishzixing.lib.able.AutoFocusAble;
import com.wishzixing.lib.able.AutoZoomAble;
import com.wishzixing.lib.able.DecodePixAble;
import com.wishzixing.lib.able.PixsAutoFocusAble;
import com.wishzixing.lib.config.CameraConfig;

import java.util.ArrayList;
import java.util.List;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *  PixsValues解析能力的集合
 */
public class PixsValuesCusManager {

    List<PixsValuesCus> actionList = new ArrayList<>();

    private PixsValuesCusManager() {
        actionList.add(AutoFocusAble.getInstance());
        actionList.add(AutoZoomAble.getInstance());
        actionList.add(DecodePixAble.getInstance());
        actionList.add(AccountLigFieAble.getInstance());
        actionList.add(PixsAutoFocusAble.getInstance());
    }

    private static class Holder {
        static PixsValuesCusManager INSTANCE = new PixsValuesCusManager();
    }

    public static PixsValuesCusManager getInstance() {
        return Holder.INSTANCE;
    }

    public PixsValuesCusManager addNewAction(PixsValuesCus pixsValuesCus) {
        this.actionList.add(pixsValuesCus);
        return this;
    }

    /***
     * 执行这些能力
     */
    public void each(final byte[] bytes, final Camera camera) {

        final Point cameraResolution = CameraConfig.getInstance().getCameraPoint();
        for (int i = 0; i < actionList.size(); i++) {
            //相机旋转90°所以适应屏幕宽高应x,y交换
            final int finalI = i;
            ThreadManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    actionList.get(finalI).cusAction(bytes, camera, cameraResolution.y, cameraResolution.x);
                }
            });
        }

    }

}
