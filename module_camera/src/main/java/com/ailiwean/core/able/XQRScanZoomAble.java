package com.ailiwean.core.able;

import android.os.Handler;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.ResultPoint;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/27 4:14 PM
 */
public class XQRScanZoomAble extends XQRScanAble {

    long zoomTime = 0;

    int lastLenght = 0;

    XQRScanZoomAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        super.needParseDeploy(source, isNative);
        if (result == null)
            return;

        if (result.getText() != null)
            return;

        ResultPoint[] points = result.getResultPoints();
        if (points == null || points.length < 3)
            return;

        int lenght = ScanHelper.getQrLenght(points);
        sendMessage(Config.RT_LOCATION,
                ScanHelper.rotatePoint(points));

        //自动变焦时间间隔为500ms
        if (System.currentTimeMillis() - zoomTime < 500)
            return;
        if (lenght < lastLenght * 0.8f) {
            Config.currentZoom = 0;
        } else if (lenght < Config.scanRect.getPreX() / 3 * 2) {
            Config.currentZoom += 0.07;
        }
        zoomTime = System.currentTimeMillis();
        lastLenght = lenght;
        sendMessage(Config.AUTO_ZOOM, Config.currentZoom + "");
    }
}
