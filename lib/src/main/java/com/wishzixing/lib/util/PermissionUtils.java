package com.wishzixing.lib.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 */
public class PermissionUtils {

    public static void init(Activity context) {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, 1);
        }

    }


    public static boolean hasPermission() {
        return ContextCompat.checkSelfPermission(Utils.getAppContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

}
