package com.ailiwean.core.able;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.CustomMultiFormatReader;
import com.ailiwean.core.zxing.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.InvertedLuminanceSource;
import com.ailiwean.core.zxing.core.Result;
import com.ailiwean.core.zxing.core.common.HybridBinarizer;

import static com.ailiwean.core.helper.ScanHelper.buildLuminanceSource;
import static com.ailiwean.core.helper.ScanHelper.getScanByteRect;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: RevMatrixSanAble
 * @Description: 反色二维码识别
 * @Author: SWY
 * @CreateDate: 2020/7/18 3:07 PM
 */
class RevColorSanAble extends PixsValuesAble {

    protected Result result;
    BinaryBitmap binaryBitmap;
    protected CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();

    public RevColorSanAble(Handler handler) {
        super(handler);
    }

    @Override
    void cusAction(byte[] data, int dataWidth, int dataHeight) {
        if (result != null)
            return;
        Rect rect = getScanByteRect(dataWidth, dataHeight);
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, dataWidth, dataHeight, rect);
        assert source != null;
        binaryBitmap = new BinaryBitmap(new HybridBinarizer(new InvertedLuminanceSource(source)));
        result = reader.decode(binaryBitmap);
        if (result != null) {
            Message.obtain(handler, Config.SCAN_RESULT, covertResult(result)).sendToTarget();
        }
    }

    protected com.ailiwean.core.Result covertResult(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText());
        result_.setPointF(ScanHelper.rotatePoint(result.getResultPoints()));
        return result_;
    }

}
