package com.ailiwean.core;

import com.ailiwean.core.zxing.ScanRect;
import com.ailiwean.core.zxing.ScanTypeConfig;

/**
 * @Package: com.ailiwean.core
 * @ClassName: Config
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 11:53 AM
 */
public class Config {

    //当前变焦倍率
    public static float currentZoom;

    /***
     * Handler 返回标识
     */
    //扫码结果回调
    public static final int SCAN_RESULT = 0;

    //环境亮度变化
    public static final int LIGHT_CHANGE = 1;

    //自动缩放
    public static final int AUTO_ZOOM = 2;

    //###############################################

    //扫码类型
    public static ScanTypeConfig scanTypeConfig = ScanTypeConfig.HIGH_FREQUENCY;

    //扫码区域
    public static ScanRect scanRect;

    //灰度算法类路径
    public static final String GARY_SCALE_PATH = "com.ailiwean.module_grayscale.GrayScaleDispatch";

    public static void initConfig() {
        currentZoom = 0f;
        scanRect = new ScanRect();
    }

}
