package com.ailiwean.core.able;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.CustomMultiFormatReader;
import com.ailiwean.core.zxing.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.Result;
import com.ailiwean.core.zxing.core.common.GlobalHistogramBinarizer;

import static com.ailiwean.core.helper.ScanHelper.buildLuminanceSource;
import static com.ailiwean.core.helper.ScanHelper.getScanByteRect;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanFast
 * @Description: GlobalHistogramBinarizer版全局直方图方法
 * @Author: SWY
 * @CreateDate: 2020/7/20 11:28 AM
 */
class XQRScanFast extends PixsValuesAble {

    CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();
    protected Result result;
    BinaryBitmap binaryBitmap;

    XQRScanFast(Handler handler) {
        super(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        if (result != null)
            return;
        //先生产扫码需要的BinaryBitmap
        Rect rect = getScanByteRect(dataWidth, dataHeight);
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, dataWidth, dataHeight, rect);
        result = reader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
        if (result != null) {
            Message.obtain(handler, Config.SCAN_RESULT, covertResult(result)).sendToTarget();
        }
    }

    protected com.ailiwean.core.Result covertResult(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText() + "GlobalHistogramBinarizer");
        result_.setPointF(ScanHelper.rotatePoint(result.getResultPoints()));
        return result_;
    }
}
