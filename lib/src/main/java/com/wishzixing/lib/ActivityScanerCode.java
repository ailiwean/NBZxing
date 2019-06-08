package com.wishzixing.lib;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.wishzixing.lib.config.Config;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.listener.OnGestureListener;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.util.LightControlUtils;
import com.wishzixing.lib.util.RxBeepUtils;
import com.wishzixing.lib.util.RxQrBarParseUtils;
import com.wishzixing.lib.util.Utils;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName: ActivityScanerCode
 * @Description: 封装了中间层Activity提供了基础界面  封装ZXing与CameraManager管理者
 * @Author: SWY
 * @Date: 2019/4/3 13:33
 */
public abstract class ActivityScanerCode extends AppCompatActivity {


    /**
     * 整体根布局
     */
    private RelativeLayout mContainer = null;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;

    /**
     * 扫描边界的宽度
     */
    private int mCropWidth = 0;

    /**
     * 扫描边界的高度
     */
    private int mCropHeight = 0;

    /**
     * 闪光灯开启状态
     */
    private boolean mFlashing = true;
    private SurfaceView surfaceView;
    private ViewGroup lightLayout;

    //自定义图片选择的CallBack
    public View.OnClickListener onClickListener;

    //调焦连续值生成
    private ValueAnimator valueAnimator;

    private Handler lazyLoading = new Handler();

    private boolean hasSurface = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_code);

        initView();
        //权限初始化
        initPermission();

        Utils.init(this);

        hasSurface = false;
        //初始化 CameraManager
        CameraManager.init(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            //Camera初始化
            //延迟初始化相机，提升加载速度
            lazyLoading.postDelayed(new Runnable() {
                @Override
                public void run() {

                    CameraManager.get().openDriver(surfaceView.getHolder());
                    //扫描动画初始化
                    initScanerAnimation();

                    Config.useDefault();

                    CameraManager.get().initCamera();

                }
            }, 100);
        } else {

            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    if (!hasSurface) {
                        hasSurface = true;
                        //延迟初始化相机，提升加载速度
                        lazyLoading.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                CameraManager.get().openDriver(surfaceView.getHolder());

                                //扫描动画初始化
                                initScanerAnimation();

                                Config.useDefault();

                                CameraManager.get().initCamera();

                            }
                        }, 100);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;
                }
            });
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();

        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.pause();
            valueAnimator.cancel();
        }

        if (CameraCoordinateHandler.getInstance() != null) {
            CameraCoordinateHandler.getInstance().removeCallbacksAndMessages(null);
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

    }

    private void initPermission() {

    }

    private void initScanerAnimation() {
        TextView loadingHint = findViewById(R.id.loadingHint);
        loadingHint.setVisibility(View.INVISIBLE);
        ImageView mQrLineView = findViewById(R.id.capture_scan_line);
        mQrLineView.setVisibility(View.VISIBLE);
        ScaleUpDowm(mQrLineView);
    }

    private void pauseScannerAnimation() {

        ImageView mQrLineView = findViewById(R.id.capture_scan_line);
        mQrLineView.setVisibility(View.INVISIBLE);
        mQrLineView.setAnimation(null);

        TextView loadingHint = findViewById(R.id.loadingHint);
        loadingHint.setVisibility(View.VISIBLE);
    }

    public void ScaleUpDowm(View view) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0.0f, mCropLayout.getMeasuredHeight());
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(2000);
        view.startAnimation(animation);
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseScannerAnimation();

        lazyLoading.removeCallbacksAndMessages(null);
    }

    @SuppressLint("NewApi")
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.back) {
            finish();
        } else if (viewId == R.id.openAlbum) {

            pauseScannerAnimation();

            if (valueAnimator != null && valueAnimator.isRunning()) {
                valueAnimator.pause();
                valueAnimator.cancel();
            }
            if (onClickListener == null)
                openLocalImage(this);
            else onClickListener.onClick(view);

        }

    }

    public static final int GET_IMAGE_FROM_PHONE = 5002;

    // 打开相册
    public static void openLocalImage(final Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, GET_IMAGE_FROM_PHONE);
    }

    public static void openLocalImage(final Fragment fragment) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(intent, GET_IMAGE_FROM_PHONE);
    }

    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == GET_IMAGE_FROM_PHONE) {
            ContentResolver resolver = getContentResolver();
            // 照片的原始资源地址
            Uri originalUri = data.getData();
            try {
                // 使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                // 开始对图像资源解码
                Result rawResult = RxQrBarParseUtils.getInstance().decodeFromPhoto(photo);

                handleDecode(rawResult);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //========================================打开本地图片识别二维码 end=================================

    /***
     * 获取到扫码数据后回调
     */
    public void initCallBackResult(Result result) {

        if (result != null) {

            //扫描到数据,震动并回调
            //扫描成功之后的振动与声音提示
            /**
             * 扫描成功后是否震动
             */
            boolean vibrate = true;
            RxBeepUtils.playBeep(this, vibrate);

            onScanResult(result);

            //结束当前Activity
            finish();

        } else {

            //相册没有扫描到数据
            onScanFail("未扫描到数据");

        }
    }

    //设定自定义打开图片选择器事件
    public void setChooseCallBack(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public abstract void onScanResult(Result result);

    public abstract void onScanFail(String message);


    //扫描到数据回调子类
    @SuppressLint("NewApi")
    public void handleDecode(Result result) {

        //再次进行判断防止闪退
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.pause();
            valueAnimator.cancel();
        }

        initCallBackResult(result);

    }

    //使用自定义相册，选择图片后回调解析
    public void postImgFilePath(String path) {

        Result result = RxQrBarParseUtils.getInstance().decodeFromPhoto(BitmapFactory.decodeFile(path));
        handleDecode(result);
    }

    private enum State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE
    }

    public interface OnRxScanerListener {

        void onSuccess(Result result);

        void onFail(String message);
    }

}