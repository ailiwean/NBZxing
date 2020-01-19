package com.NBZxing.lib.config;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class UserConfig {

    public static void useDefault() {
//        AutoFocusConfig.getInstance().go();
//        PointConfig.getInstance().go();
        ParseRectConfig.getInstance().go();
        ZoomConfig.getInstance().go();
        BeepVibrationConfig.getInstance().go();
    }
}
