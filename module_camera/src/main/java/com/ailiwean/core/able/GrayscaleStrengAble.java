package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
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
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
        if (result != null)
            return;
        i++;
        if (i % 2 != 1) {
            return;
        }

        //浅色二维码增强
        result = toLaunchParse(new HybridBinarizer(new LightGreySource(source)));

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
