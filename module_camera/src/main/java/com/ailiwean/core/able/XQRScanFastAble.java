package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanFast
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/7/21 2:09 PM
 */
class XQRScanFastAble extends PixsValuesAble {

    protected Result result;

    public XQRScanFastAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
        if (result != null)
            return;
        result = toLaunchParse(source.getGlobaBinary());
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
