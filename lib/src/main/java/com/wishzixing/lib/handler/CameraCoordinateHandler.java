package com.wishzixing.lib.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.listener.LightCallBack;
import com.wishzixing.lib.listener.ResultListener;
import com.wishzixing.lib.util.RxBeepUtils;

import java.util.ArrayList;
import java.util.List;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 *  协调相机输出字节流以及解析Handler,并响应结果
 */
public class CameraCoordinateHandler extends Handler {

    private ResultListener resultListener;
    private LightCallBack lightCallBack;

    private List<String> resultList = new ArrayList<>();

    private boolean isStop = false;

    private CameraCoordinateHandler(Looper mainLooper) {
        super(mainLooper);
    }

    private static class Holder {
        static CameraCoordinateHandler cameraCoordinateHandler = new CameraCoordinateHandler(Looper.getMainLooper());
    }

    public static CameraCoordinateHandler getInstance() {
        return Holder.cameraCoordinateHandler;
    }

    @Override
    public void handleMessage(Message message) {

        if (isStop)
            return;

        //解码回调
        if (message.what == R.id.decode_succeeded) {
            dispatchSucceed((Result) message.obj);
        } else if (message.what == R.id.decode_failed) {
        }

        //光场强度变换回调
        else if (message.what == R.id.isbright) {
            boolean isBright = (boolean) message.obj;
            lightChange(isBright);
        }
    }

    //解析成功
    private void dispatchSucceed(Result result) {

        if (resultList.contains(result.getText()))
            return;

        if (CameraConfig.getInstance().isBeep())
            RxBeepUtils.playBeep();

        if (CameraConfig.getInstance().isVibration())
            RxBeepUtils.playVibrate();

        if (resultListener != null)
            resultListener.scanSucceed(result);

        resultList.add(result.getText());
    }

    //光场变换
    private void lightChange(boolean isBright) {
        if (lightCallBack != null)
            lightCallBack.lightValues(isBright);
    }

    public void regResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void regAccountListener(LightCallBack lightCallBack) {
        this.lightCallBack = lightCallBack;
    }

    public void stop() {
        isStop = true;
    }

    public void start() {
        isStop = false;
    }
}
