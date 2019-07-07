package com.wishzixing.lib.config;

import android.support.annotation.IntDef;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class ScanConfig {

    private int scanModel = ALL;

    private boolean justOneScan = true;

    private ScanConfig() {

    }

    private static class Holder {
        static ScanConfig INSTANCE = new ScanConfig();
    }

    public static ScanConfig getInstance() {
        return Holder.INSTANCE;
    }

    //设定扫描模式
    public ScanConfig setScanModel(@Type int scanModel) {
        this.scanModel = scanModel;
        return this;
    }

    public ScanConfig setJustOneScan(boolean isJustOneScan) {
        this.justOneScan = isJustOneScan;
        return this;
    }

    public static final int ALL = 0;
    public static final int QRCODE = 1;
    public static final int BARCODE = 2;

    @IntDef({ALL, QRCODE, BARCODE})
    public @interface Type {
    }

    public void go() {
        CameraConfig.getInstance().isJustOne = this.justOneScan;
        CameraConfig.getInstance().scanModel = this.scanModel;
    }
}
