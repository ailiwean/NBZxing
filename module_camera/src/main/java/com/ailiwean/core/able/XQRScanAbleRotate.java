package com.ailiwean.core.able;

import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;
import com.ailiwean.core.zxing.core.common.HybridBinarizerFine;

import static com.ailiwean.core.helper.ScanHelper.buildLuminanceSource;
import static com.ailiwean.core.helper.ScanHelper.getScanByteRect;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAbleRotate
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/5/3 2:32 PM
 */
public class XQRScanAbleRotate extends PixsValuesAble {

    protected Result result;
    BinaryBitmap binaryBitmap;

    XQRScanAbleRotate(Handler handler) {
        super(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        if (result != null && !isNative)
            return;
        data = rotateByte(data, dataWidth, dataHeight);
        if (data == null)
            return;
        dataWidth += dataHeight;
        dataHeight = dataWidth - dataHeight;
        dataWidth -= dataHeight;

        Rect rect = getScanByteRect(dataWidth, dataHeight);
        if (dataWidth == dataHeight)
            dataWidth--;
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, dataWidth, dataHeight, rect);
        result = toLaunchParse(new HybridBinarizerFine(source));
        if (result != null) {
            sendMessage(Config.SCAN_RESULT, covertResultRotate(result));
        }
    }

    private byte[] rotateByte(byte[] data, int dataWidth, int dataHeight) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < dataHeight; y++) {
            for (int x = 0; x < dataWidth; x++) {
                int i = x * dataHeight + dataHeight - y - 1;
                if (i >= data.length || x + y * dataWidth >= data.length) {
                    return null;
                }
                rotatedData[i] = data[x + y * dataWidth];
            }
        }
        return rotatedData;
    }

    protected com.ailiwean.core.Result covertResultRotate(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText());
        PointF[] pointFS = ScanHelper.rotatePointR(result.getResultPoints());
        result_.setQrPointF(ScanHelper.calCenterPointF(pointFS));
        result_.setQrLeng(ScanHelper.calQrLenghtShow(result.getResultPoints()));
        result_.setFormat(result.getBarcodeFormat());
        result_.setQrRotate(ScanHelper.calQrRotate(pointFS));
        result_.setRotate(true);
        return result_;
    }
}
