package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.zxing.CustomMultiFormatReader;
import com.ailiwean.core.zxing.core.Binarizer;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;

import java.lang.ref.WeakReference;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: PixsValuesAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 3:27 PM
 */
public abstract class PixsValuesAble {

    WeakReference<Handler> handlerHolder;
    boolean isNative;

    CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();

    public PixsValuesAble(Handler handler) {
        this.handlerHolder = new WeakReference<>(handler);
    }

    /***
     * 其他操作重写这个
     * @param data
     * @param dataWidth
     * @param dataHeight
     */
    protected void cusAction(byte[] data, int dataWidth, int dataHeight) {
    }

    protected void cusAction(byte[] data, int dataWidth, int dataHeight, boolean isNative) {
        this.isNative = isNative;
        cusAction(data, dataWidth, dataHeight);
    }

    /***
     * 需要解析二维码子类重写这个
     * @param source
     */
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
    }

    Result toLaunchParse(Binarizer binarizer) {
        return reader.decode(new BinaryBitmap(binarizer));
    }

    protected void sendMessage(int type, Object obj) {
        if (handlerHolder != null && handlerHolder.get() != null) {
            Message.obtain(handlerHolder.get(), type, obj)
                    .sendToTarget();
        }
    }

    public void release() {
        handlerHolder.clear();
        handlerHolder = null;
    }


}
