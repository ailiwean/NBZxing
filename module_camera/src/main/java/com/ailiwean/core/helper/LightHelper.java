
package com.ailiwean.core.helper;

import android.hardware.Camera;

/**
 * @Package: com.ailiwean.core.helper
 * @ClassName: LightHelper
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 3:10 PM
 */
public class LightHelper {
    
    static int lastAvDark = 0;

    /***
     * 根据像素点采集环境亮度
     */
    public static int getAvDark(byte[] data, int dataWidth, int dataheight) {

        if (data.length == 0)
            return lastAvDark;

        long pixelLightCount = 0L;
        long pixCount = 0L;
        int step = 20;
        for (int i = 0; i < data.length; i += step) {
            pixelLightCount += data[i] & 0xff;
            pixCount++;
        }
        lastAvDark = (int) (pixelLightCount / pixCount);
        return lastAvDark;
    }

    /***
     * camera1 打开/关闭闪光灯
     */
    public static void openLight(Camera mCamera, boolean isOpen) {
        if (mCamera == null)
            return;
        Camera.Parameters parameters = mCamera.getParameters();
        if (isOpen)
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        else parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

}
