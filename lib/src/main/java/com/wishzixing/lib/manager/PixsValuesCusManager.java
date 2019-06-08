package com.wishzixing.lib.manager;

import android.hardware.Camera;
import android.util.Log;

import com.wishzixing.lib.able.AccountLigFieAble;
import com.wishzixing.lib.able.AutoFocusAble;
import com.wishzixing.lib.able.DecodePixAble;

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
        actionList.add(DecodePixAble.getInstance());
        actionList.add(AccountLigFieAble.getInstance());
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
    public void each(byte[] bytes, Camera camera) {
        for (int i = 0; i < actionList.size(); i++) {
            actionList.get(i).cusAction(bytes, camera);
        }
    }

}
