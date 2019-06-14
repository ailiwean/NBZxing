package com.wishzixing.lib.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.manager.CameraManager;

/***
 *  Created by SWY
 *  DATE 2019/6/3
 *
 *  协调相机输出字节流以及解析Handler,并响应结果
 */
public class CameraCoordinateHandler extends Handler {


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

        if (message.what == R.id.decode_succeeded) {
            decodeSucceed((Result) message.obj);
            Log.e("解析成功", "解析成功");
        } else if (message.what == R.id.decode_failed) {
            //startPreviewAndDecode();
        }
    }

    //解析成功
    private void decodeSucceed(Result result) {

    }

}
