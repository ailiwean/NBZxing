package com.android.NBZxing;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import me.devilsen.czxing.Scanner;
import me.devilsen.czxing.code.BarcodeFormat;
import me.devilsen.czxing.util.BarCodeUtil;
import me.devilsen.czxing.view.ScanActivityDelegate;
import me.devilsen.czxing.view.ScanView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.<CusZxingView>findViewById(R.id.cusZxing)
                .synchLifeStart(this);


//        Resources resources = getResources();
//        List<Integer> scanColors = Arrays.asList(Color.parseColor("#000000"), Color.parseColor("#000000"), Color.parseColor("#000000"));
//
//        Scanner.with(this)
//                .setMaskColor(Color.parseColor("#000000"))   // 设置设置扫码框四周颜色
//                .setBorderColor(Color.parseColor("#000000"))   // 扫码框边框颜色
//                .setBorderSize(BarCodeUtil.dp2px(this, 200))            // 设置扫码框大小
////        .setBorderSize(BarCodeUtil.dp2px(this, 200), BarCodeUtil.dp2px(this, 100))     // 设置扫码框长宽（如果同时调用了两个setBorderSize方法优先使用上一个）
//                .setCornerColor(Color.parseColor("#000000"))     // 扫码框角颜色
//                .setScanLineColors(scanColors)                          // 扫描线颜色（这是一个渐变颜色）
////        .setHorizontalScanLine()                              // 设置扫码线为水平方向（从左到右）
//                .setScanMode(ScanView.SCAN_MODE_TINY)                   // 扫描区域 0：混合 1：只扫描框内 2：只扫描整个屏幕
////        .setBarcodeFormat(BarcodeFormat.EAN_13)                 // 设置扫码格式
//                .setTitle("My Scan View")                               // 扫码界面标题
//                .showAlbum(true)                                        // 显示相册(默认为true)
//                .setScanNoticeText("扫描二维码")                         // 设置扫码文字提示
//                .setFlashLightOnText("打开闪光灯")                       // 打开闪光灯提示
//                .setFlashLightOffText("关闭闪光灯")                      // 关闭闪光灯提示
//                .setFlashLightInvisible()                               // 不使用闪光灯图标及提示
//                .continuousScan()                                       // 连续扫码，不关闭扫码界面
//                .setOnClickAlbumDelegate(new ScanActivityDelegate.OnClickAlbumDelegate() {
//                    @Override
//                    public void onClickAlbum(Activity activity) {       // 点击右上角的相册按钮
////                        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                        activity.startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
//                    }
//
//                    @Override
//                    public void onSelectData(int requestCode, Intent data) { // 选择图片返回的数据
////                        if (requestCode == CODE_SELECT_IMAGE) {
////                            selectPic(data);
////                        }
//                    }
//                })
//                .setOnScanResultDelegate(new ScanActivityDelegate.OnScanDelegate() { // 接管扫码成功的数据
//                    @Override
//                    public void onScanResult(Activity activity, String result, BarcodeFormat format) {
////
//                        Toast.makeText(activity, result, Toast.LENGTH_LONG);
//
////                         Intent intent = new Intent(MainActivity.this, DelegateActivity.class);
////                        intent.putExtra("result", result);
////                        startActivity(intent);
//                    }
//                })
//                .start();

    }
}
