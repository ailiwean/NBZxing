package com.wishzixing.lib;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wishzixing.lib.util.PermissionUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/26
 *
 */
public class PermissionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
            } else {
                //用户不同意，向用户展示该权限作用
                Toast.makeText(PermissionActivity.this, "未授予权限,某些功能无法使用", Toast.LENGTH_SHORT).show();
            }
        }
        sendBorad();
        finish();
    }

    private void sendBorad() {
        Intent intent = new Intent();
        intent.setAction("REFRESH");
        sendBroadcast(intent);
    }
}
