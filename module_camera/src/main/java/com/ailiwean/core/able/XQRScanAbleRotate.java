package com.ailiwean.core.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;
import com.ailiwean.core.zxing.core.common.HybridBinarizer;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAbleRotate
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/5/3 2:32 PM
 */
public class XQRScanAbleRotate extends PixsValuesAble {

    protected Result result;

    XQRScanAbleRotate(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        result = toLaunchParse(new HybridBinarizer(source.onlyCopyWarpRotate()));
        if (result != null && result.getText() != null && !"".equals(result.getText())) {
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
