package com.wishzixing.lib.config;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class Config {

    public static void useDefault() {
        AutoFocusConfig.getInstance().setModel(AutoFocusConfig.SENSOR).go();
        PointConfig.getInstance().go();
        ScanModelConfig.getInstance().setScanModel(ScanModelConfig.ALL).go();
        ParseRectConfig.getInstance().go();
        ZoomConfig.getInstance().go();
    }

}
