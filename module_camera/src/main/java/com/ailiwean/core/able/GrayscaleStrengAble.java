package com.ailiwean.core.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ailiwean.core.Config;
import com.ailiwean.core.TypeRunnable;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.LightGreySource;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;
import com.ailiwean.core.zxing.core.common.HybridBinarizer;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: GammaAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/8/7 6:05 PM
 */
class GrayscaleStrengAble extends PixsValuesAble {

    public GrayscaleStrengAble(Handler handler) {
        super(handler);
    }

    protected Result result;

    int i = -1;

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        if (result != null)
            return;

        i++;
        if (i % 2 != 1) {
            return;
        }

        //浅色二维码增强
        result = toLaunchParse(new HybridBinarizer(new LightGreySource(source)));

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
