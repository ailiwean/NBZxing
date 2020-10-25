package com.ailiwean.core.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;


/**
 * @Package: com.ailiwean.core.able
 * @ClassName: QRScanAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 10:18 AM
 */
public class XQRScanFineAble extends PixsValuesAble {

    protected Result result;

    XQRScanFineAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
        if (result != null)
            return;
        result = toLaunchParse(source.getHybridBinaryFine());
        if (result != null && !"".equals(result.getText())) {
            sendMessage(Config.SCAN_RESULT, covertResult(result));
        }
    }

    protected com.ailiwean.core.Result covertResult(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        PointF[] pointFS = ScanHelper.rotatePoint(result.getResultPoints());
        result_.setQrPointF(ScanHelper.calCenterPointF(pointFS));
        result_.setQrLeng(ScanHelper.calQrLenghtShow(result.getResultPoints()));
        result_.setFormat(result.getBarcodeFormat());
        result_.setQrRotate(ScanHelper.calQrRotate(pointFS));
        return result_;
    }
}
