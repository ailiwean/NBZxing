package com.wishzixing.lib.config;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class Config {

    public static void useDefault() {
        AutoFocusConfig.getInstance().go();
        PointConfig.getInstance().go();
        ScanConfig.getInstance().go();
        ParseRectConfig.getInstance().go();
        ZoomConfig.getInstance().go();
        BeepVibrationConfig.getInstance().go();
    }

}
