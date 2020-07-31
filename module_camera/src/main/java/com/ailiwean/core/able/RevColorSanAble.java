package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.InvertedLuminanceSource;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;
import com.ailiwean.core.zxing.core.Result;
import com.ailiwean.core.zxing.core.common.HybridBinarizer;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: RevMatrixSanAble
 * @Description: 反色二维码识别
 * @Author: SWY
 * @CreateDate: 2020/7/18 3:07 PM
 */
class RevColorSanAble extends PixsValuesAble {

    protected Result result;

    public RevColorSanAble(Handler handler) {
        super(handler);
    }

    int i = -1;

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source) {
        if (result != null)
            return;
        //降低反色二维码调用频率
        i++;
        if (i % 6 != 1) {
            return;
        }
        result = toLaunchParse(new HybridBinarizer(new InvertedLuminanceSource(source)));
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
