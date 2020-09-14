package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

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
public class XQRScanAble extends PixsValuesAble {

    protected Result result;

    XQRScanAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
        if (result != null)
            return;
        result = toLaunchParse(source.getHybridBinary());
        if (result != null && !"".equals(result.getText())) {
            sendMessage(Config.SCAN_RESULT, covertResult(result));
        }
    }

    protected com.ailiwean.core.Result covertResult(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText());
        result_.setPointF(ScanHelper.rotatePoint(result.getResultPoints()));
        return result_;
    }
}
