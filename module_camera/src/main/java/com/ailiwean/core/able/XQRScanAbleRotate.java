package com.ailiwean.core.able;

import android.os.Handler;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: XQRScanZoomAbleRotate
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/5/3 2:32 PM
 */
public class XQRScanAbleRotate extends XQRScanAble {

    public XQRScanAbleRotate(Handler handler) {
        this(handler, true);
    }

    private XQRScanAbleRotate(Handler handler, boolean isRotate) {
        super(handler, isRotate);
    }
}
