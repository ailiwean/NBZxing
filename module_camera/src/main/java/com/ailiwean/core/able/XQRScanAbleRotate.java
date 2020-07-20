package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.CustomMultiFormatReader;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.Result;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAbleRotate
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/5/3 2:32 PM
 */
public class XQRScanAbleRotate extends PixsValuesAble {

    CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();
    protected Result result;
    BinaryBitmap binaryBitmap;

    XQRScanAbleRotate(Handler handler) {
        super(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        if (result != null)
            return;
        data = rotateByte(data, dataWidth, dataHeight);
        if (data == null)
            return;
        dataWidth += dataHeight;
        dataHeight = dataWidth - dataHeight;
        dataWidth -= dataHeight;

        //先生产扫码需要的BinaryBitmap
        binaryBitmap = ScanHelper.byteToBinaryBitmap(data, dataWidth, dataHeight);
        result = reader.decode(binaryBitmap);
        if (result != null) {
            Message.obtain(handler, Config.SCAN_RESULT, covertResultRotate(result)).sendToTarget();
        }
    }

    private byte[] rotateByte(byte[] data, int dataWidth, int dataHeight) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < dataHeight; y++) {
            for (int x = 0; x < dataWidth; x++) {
                int i = x * dataHeight + dataHeight - y - 1;
                if (i >= data.length || x + y * dataWidth >= data.length) {
                    return null;
                }
                rotatedData[i] = data[x + y * dataWidth];
            }
        }
        return rotatedData;
    }


    protected com.ailiwean.core.Result covertResultRotate(Result result) {
        com.ailiwean.core.Result result_ = new com.ailiwean.core.Result();
        result_.setText(result.getText());
        result_.setPointF(ScanHelper.rotatePointR(result.getResultPoints()));
        result_.setRotate(true);
        return result_;
    }


}
