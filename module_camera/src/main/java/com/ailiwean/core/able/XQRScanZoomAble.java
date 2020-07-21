package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.FormatException;
import com.ailiwean.core.zxing.core.NotFoundException;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.ResultPoint;
import com.ailiwean.core.zxing.core.common.DetectorResult;
import com.ailiwean.core.zxing.core.qrcode.detector.Detector;

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
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
        super.needParseDeploy(source);
        if (result != null)
            return;
        DetectorResult decoderResult = null;
        ResultPoint[] points;
        try {
            decoderResult = new Detector(source.getGlobaBinary().getBlackMatrix()).detect(null);
        } catch (NotFoundException | FormatException e) {
            e.printStackTrace();
        }
        if (decoderResult == null) {
            try {
                decoderResult = new Detector(source.getHybridBinary().getBlackMatrix()).detect(null);
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
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
