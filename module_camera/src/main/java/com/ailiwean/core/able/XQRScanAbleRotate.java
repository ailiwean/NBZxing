package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAbleRotate
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/5/3 2:32 PM
 */
public class XQRScanAbleRotate extends XQRScanAble {

    XQRScanAbleRotate(Handler handler) {
        super(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        if (result != null)
            return;
        data = rotateByte(data, dataWidth, dataHeight);
        dataWidth += dataHeight;
        dataHeight = dataWidth - dataHeight;
        dataWidth -= dataHeight;
        //先生产扫码需要的BinaryBitmap
        binaryBitmap = ScanHelper.byteToBinaryBitmap(data, dataWidth, dataHeight);
        result = reader.decode(binaryBitmap);
        if (result != null) {
            result = rotateResultPoint(result);
            Message.obtain(handler, Config.SCAN_RESULT, covertResult(result)).sendToTarget();
        }

    }

    private Result rotateResultPoint(Result result) {
        List<ResultPoint> resultPoints = new ArrayList<>();
        for (ResultPoint point : result.getResultPoints()) {
            resultPoints.add(new ResultPoint(point.getY(), point.getX()));
        }
        return new Result(result.getText(), result.getRawBytes(),
                result.getNumBits(),
                resultPoints.toArray(new ResultPoint[]{}),
                result.getBarcodeFormat(),
                result.getTimestamp()
        );
    }

    private byte[] rotateByte(byte[] data, int dataWidth, int dataHeight) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < dataHeight; y++) {
            for (int x = 0; x < dataWidth; x++) {
                rotatedData[x * dataHeight + dataHeight - y - 1] = data[x + y * dataWidth];
            }
        }
        return rotatedData;
    }

    protected com.ailiwean.core.Result covertResult(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText());
        result_.setPointF(ScanHelper.rotatePointR(result.getResultPoints()));
        return result_;
    }
}
