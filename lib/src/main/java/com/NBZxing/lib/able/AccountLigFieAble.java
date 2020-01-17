package com.NBZxing.lib.able;

import android.hardware.Camera;
import android.os.Message;

import com.NBZxing.lib.R;
import com.NBZxing.lib.handler.CameraCoordinateHandler;
import com.NBZxing.lib.manager.PixsValuesCus;
import com.NBZxing.lib.util.AccountUtils;


/***
 *  Created by SWY
 *  DATE 2019/6/2
 *  计算预览区域光场强度回调
 */
public class AccountLigFieAble implements PixsValuesCus {

    int STANDVALUES = 100;

    boolean isBright = true;

    private AccountLigFieAble() {
    }

    @Override
    public void cusAction(byte[] data, Camera camera, int x, int y) {
        account(data, camera);
    }

    @Override
    public void stop() {

    }

    private static class Holder {
        static AccountLigFieAble INSTANCE = new AccountLigFieAble();
    }

    public static AccountLigFieAble getInstance() {
        return Holder.INSTANCE;
    }

    private void account(byte[] data, Camera camera) {

        int avDark = AccountUtils.getAvDark(data);

        if (avDark > STANDVALUES && !isBright) {
            isBright = true;
            sendMessage(isBright);
        }
        if (avDark < STANDVALUES && isBright) {
            isBright = false;
            sendMessage(isBright);
        }

    }

    private void sendMessage(boolean isBright) {
        Message message = Message.obtain(CameraCoordinateHandler.getInstance(), R.id.isbright, isBright);
        message.sendToTarget();
    }

}

