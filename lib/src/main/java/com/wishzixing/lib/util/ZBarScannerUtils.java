package com.wishzixing.lib.util;

import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.config.ScanConfig;
import com.wishzixing.lib.core.zbar.Config;
import com.wishzixing.lib.core.zbar.Image;
import com.wishzixing.lib.core.zbar.ImageScanner;
import com.wishzixing.lib.core.zbar.Symbol;
import com.wishzixing.lib.core.zbar.SymbolSet;

/***
 *  Created by SWY
 *  DATE 2019/7/8
 *
 */
public class ZBarScannerUtils {

    private static ImageScanner mImageScanner = new ImageScanner();

    private ZBarScannerUtils() {
        mImageScanner = new ImageScanner();
        if (CameraConfig.getInstance().getScanModel() == ScanConfig.QRCODE) {
            mImageScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            mImageScanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
        } else if (CameraConfig.getInstance().getScanModel() == ScanConfig.BARCODE) {
            mImageScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            mImageScanner.setConfig(Symbol.CODE128, Config.ENABLE, 1);
            mImageScanner.setConfig(Symbol.CODE39, Config.ENABLE, 1);
            mImageScanner.setConfig(Symbol.EAN13, Config.ENABLE, 1);
            mImageScanner.setConfig(Symbol.EAN8, Config.ENABLE, 1);
            mImageScanner.setConfig(Symbol.UPCA, Config.ENABLE, 1);
            mImageScanner.setConfig(Symbol.UPCE, Config.ENABLE, 1);
            mImageScanner.setConfig(Symbol.UPCE, Config.ENABLE, 1);
        } else if (CameraConfig.getInstance().getScanModel() == ScanConfig.ALL) {
            mImageScanner.setConfig(Symbol.NONE, Config.X_DENSITY, 3);
            mImageScanner.setConfig(Symbol.NONE, Config.Y_DENSITY, 3);
        }
    }

    private static class Holder {
        static ZBarScannerUtils INSTANCE = new ZBarScannerUtils();
    }

    public static ZBarScannerUtils getInstance() {
        return Holder.INSTANCE;
    }

    public String scanImage(Image barcode) {
        int pushCode = mImageScanner.scanImage(barcode);

        String resultStr = null;
        if (pushCode != 0) {
            SymbolSet symSet = mImageScanner.getResults();
            for (Symbol sym : symSet)
                resultStr = sym.getData();
        }
        return resultStr;
    }

}
