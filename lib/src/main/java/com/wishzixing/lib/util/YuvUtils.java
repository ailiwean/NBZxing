package com.wishzixing.lib.util;

import android.content.Context;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;

/***
 *  Created by SWY
 *  DATE 2019/6/22
 *
 */
public class YuvUtils {

    private static YuvUtils yuvUtils;
    private final RenderScript renderScript;
    private final ScriptIntrinsicYuvToRGB scriptIntrinsicYuvToRGB;

    private YuvUtils(Context context) {
        renderScript = RenderScript.create(context);
        scriptIntrinsicYuvToRGB = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(renderScript));
    }

    public static void init(Context context) {
        yuvUtils = new YuvUtils(context);
    }

    public static YuvUtils getInstance() {
        return yuvUtils;
    }

    public RenderScript getRenderScript() {
        return renderScript;
    }

    public ScriptIntrinsicYuvToRGB getYuvToRGBScript() {
        return scriptIntrinsicYuvToRGB;
    }

}
