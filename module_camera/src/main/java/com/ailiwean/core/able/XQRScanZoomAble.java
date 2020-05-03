package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.detector.Detector;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/27 4:14 PM
 */
public class XQRScanZoomAble extends XQRScanAble {

    long zoomTime = 0;

    XQRScanZoomAble(Handler handler) {
        this(handler, false);
    }

    XQRScanZoomAble(Handler handler, boolean isRotate) {
        super(handler, isRotate);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        super.cusAction(data, dataWidth, dataHeight);
        DetectorResult decoderResult = null;
        ResultPoint[] points;
        try {
            decoderResult = new Detector(binaryBitmap.getBlackMatrix()).detect(null);
        } catch (NotFoundException | FormatException e) {
            e.printStackTrace();
        }
        if (decoderResult == null)
            return;
        points = decoderResult.getPoints();
        int lenght = ScanHelper.getQrLenght(points);
        if (lenght < Config.scanRect.getPreX() / 3 * 2) {
            //自动变焦时间间隔为500ms
            if (System.currentTimeMillis() - zoomTime < 500)
                return;
            Message.obtain(handler, Config.AUTO_ZOOM, Config.currentZoom + 0.05 + "")
                    .sendToTarget();
            zoomTime = System.currentTimeMillis();
        }
    }
}
