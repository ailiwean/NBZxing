package com.ailiwean.core.able;

import android.os.Handler;
import android.os.Message;

import com.ailiwean.core.Config;
import com.ailiwean.core.helper.ScanHelper;
import com.ailiwean.core.zxing.CustomMultiFormatReader;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.oned.OneDReader;


/**
 * @Package: com.ailiwean.core.able
 * @ClassName: QRScanAble
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 10:18 AM
 */
public class XQRScanAble extends PixsValuesAble {

    private CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();
    private Result result;
    BinaryBitmap binaryBitmap;

    private boolean isRotate;

    public XQRScanAble(Handler handler) {
        super(handler);
    }


    public XQRScanAble(Handler handler, boolean isRotate) {
        super(handler);
        this.isRotate = isRotate;
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        if (result != null)
            return;

        if (isRotate) {
            
        }


        //先生产扫码需要的BinaryBitmap
        binaryBitmap = ScanHelper.byteToBinaryBitmap(data, dataWidth, dataHeight);
        result = reader.decode(binaryBitmap);
        if (result != null) {
            Message.obtain(handler, Config.SCAN_RESULT, result).sendToTarget();
            binaryBitmap = null;
        }
    }
}
