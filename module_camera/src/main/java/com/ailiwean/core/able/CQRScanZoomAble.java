package com.ailiwean.core.able;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ailiwean.core.Config;
import com.ailiwean.core.czxing.BarcodeFormat;
import com.ailiwean.core.czxing.BarcodeReader;
import com.ailiwean.core.czxing.CodeResult;
import com.ailiwean.core.helper.ScanHelper;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: CQRScanAble
 * @Description: C++版Zxing ， 可基于openCV优化
 * @Author: SWY
 * @CreateDate: 2020/6/12 2:25 PM
 */
class CQRScanZoomAble extends PixsValuesAble implements BarcodeReader.ReadCodeListener {

    BarcodeReader barcodeReader;

    boolean isStop;

    public CQRScanZoomAble(Handler handler) {
        super(handler);
        barcodeReader = BarcodeReader.getInstance();
        barcodeReader.setReadCodeListener(this);
        barcodeReader.setBarcodeFormat(BarcodeFormat.QR_CODE);
        barcodeReader.prepareRead();
    }

    @Override
    void cusAction(byte[] data, int dataWidth, int dataHeight) {
        Rect rect = ScanHelper.getScanByteRect(dataWidth, dataHeight);
        barcodeReader.read(data, rect.left, rect.top, rect.width(), rect.height(), dataWidth, dataHeight);
    }

    @Override
    public void onReadCodeResult(CodeResult result) {
        if (result == null) {
            return;
        }
        if (!TextUtils.isEmpty(result.getText()) && !isStop) {
            isStop = true;
            Message.obtain(handler, Config.SCAN_RESULT, covertResult(result)).sendToTarget();
            barcodeReader.stopRead();
        } else if (result.getPoints() != null) {
            // tryZoom(result);
        }
    }

    protected com.ailiwean.core.Result covertResult(CodeResult result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText());
        List<ResultPoint> pointList = new ArrayList<>();
        for (int i = 0; i + 1 < result.getPoints().length; i += 2) {
            pointList.add(new ResultPoint(result.getPoints()[i], result.getPoints()[i + 1]));
        }
        result_.setPointF(ScanHelper.rotatePoint(pointList.toArray(new ResultPoint[]{})));
        return result_;
    }

}
