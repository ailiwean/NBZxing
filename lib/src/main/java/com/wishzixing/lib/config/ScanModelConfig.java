package com.wishzixing.lib.config;

import android.support.annotation.IntDef;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ScanModelConfig {

    private int scanModel = 0;

    private ScanModelConfig() {

    }

    public static ScanModelConfig getInstance() {
        return new ScanModelConfig();
    }

    //设定扫描模式
    public ScanModelConfig setScanModel(@Type int scanModel) {
        this.scanModel = scanModel;
        return this;
    }

    public static final int ALL = 0;
    public static final int QRCODE = 1;
    public static final int BARCODE = 2;

    @IntDef({ALL, QRCODE, BARCODE})
    public @interface Type {
    }


    public void go() {
        CameraConfig.getInstance().scanModel = scanModel;
    }
}
