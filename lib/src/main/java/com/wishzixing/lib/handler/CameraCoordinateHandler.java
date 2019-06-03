package com.wishzixing.lib.handler;

import android.os.Handler;
import android.os.Message;

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

    private State state;

    private CameraCoordinateHandler() {
        DecodeThread.getInstance().start();
        state = State.SUCCESS;
        CameraManager.get().startPreview();

    }

    private static class Holder {
        static CameraCoordinateHandler cameraCoordinateHandler = new CameraCoordinateHandler();
    }

    public static CameraCoordinateHandler getInstance() {
        return Holder.cameraCoordinateHandler;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            decodeSucceed((Result) message.obj);
        } else if (message.what == R.id.decode_failed) {
            state = State.PREVIEW;
            decodeFail();
        }
    }

    /***
     * 相机重新预览并开始解析
     */
    private void restartPreviewAndDecode() {

        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(DecodHandler.getInstance(), R.id.decode);
        }

    }

    /***
     * 停止预览与解析
     */
    public void quitSynchronously() {
        state = State.DONE;
        DecodeThread.getInstance().interrupt();
        CameraManager.get().stopPreview();
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
        removeMessages(R.id.decode);
        removeMessages(R.id.auto_focus);
    }

    private enum State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE
    }

    //解析成功
    private void decodeSucceed(Result result) {

    }

    //解析失败
    private void decodeFail() {

    }


}
