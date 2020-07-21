package com.ailiwean.core.able;

import android.os.Handler;

import com.ailiwean.core.zxing.CustomMultiFormatReader;
import com.ailiwean.core.zxing.core.Binarizer;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: PixsValuesAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 3:27 PM
 */
public abstract class PixsValuesAble {

    Handler handler;

    CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();

    public PixsValuesAble(Handler handler) {
        this.handler = handler;
    }

    /***
     * 其他操作重写这个
     * @param data
     * @param dataWidth
     * @param dataHeight
     */
    protected void cusAction(byte[] data, int dataWidth, int dataHeight) {
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
}
