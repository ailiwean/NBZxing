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
import android.graphics.Point;
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
import com.wishzixing.lib.able.AccountLigFieAble;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.handler.CameraCoordinateHandler;
import com.wishzixing.lib.listener.OnGestureListener;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.util.AutoFocusUtils;
import com.wishzixing.lib.util.LightManager;
import com.wishzixing.lib.util.RxBeepTool;
import com.wishzixing.lib.util.RxQrBarParseTool;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @ClassName: ActivityScanerCode
 * @Description: 封装了中间层Activity提供了基础界面  封装ZXing与CameraManager管理者
 * @Author: SWY
 * @Date: 2019/4/3 13:33
 */
public abstract class ActivityScanerCode extends AppCompatActivity {


    private InactivityTimer inactivityTimer;


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

        hasSurface = false;
        //初始化 CameraManager
        CameraManager.init(this);

        inactivityTimer = new InactivityTimer(this);
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

                    initCamera(surfaceView.getHolder());
                    //扫描动画初始化
                    initScanerAnimation();

                    //初始化自动调焦
                    AutoFocusUtils.getInstance().setModel(AutoFocusUtils.SENSOR).startAutoFocus();

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

                                initCamera(surfaceView.getHolder());

                                //扫描动画初始化
                                initScanerAnimation();

                                //初始化自动调焦
                                AutoFocusUtils.getInstance().setModel(AutoFocusUtils.SENSOR).startAutoFocus();

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
            CameraCoordinateHandler.getInstance().quitSynchronously();
            CameraCoordinateHandler.getInstance().removeCallbacksAndMessages(null);
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initView() {
        /**
         * 闪光灯 按钮
         */
        mContainer = findViewById(R.id.capture_containter);
        mCropLayout = findViewById(R.id.capture_crop_layout);
        surfaceView = findViewById(R.id.capture_preview);

        lightLayout = findViewById(R.id.light_layout);
        lightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView text = lightLayout.findViewById(R.id.light_text);
                ImageView imageView = lightLayout.findViewById(R.id.light_img);

                if (text.getText().equals("轻点照亮")) {

                    text.setText("轻点关闭");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.light_open));
                    lightLayout.setTag(true);
                    openLight();

                } else {

                    text.setText("轻点照亮");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.light_close));
                    lightLayout.setTag(false);
                    closeLight();
                }

            }
        });

        OnGestureListener onGestureListener = new OnGestureListener(this);
        onGestureListener.regOnDoubleClickCallback(new OnGestureListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {

                Camera camera = CameraManager.get().getCamera();

                if (camera == null)
                    return;

                Camera.Parameters p = camera.getParameters();

                if (p == null)
                    return;

                if (p.getZoom() != p.getMaxZoom()) {
                    p.setZoom(p.getMaxZoom());
                } else p.setZoom(1);

                camera.setParameters(p);
            }
        });

        onGestureListener.regOnDoubleFingerCallback(new OnGestureListener.DoubleFingerCallback() {

            int change = 0;

            @Override
            public void onStepFingerChange(float total, float value) {
                change += Math.abs(value);
                //变化量大于50进行调焦
                if (change > 50) {

                    final Camera camera = CameraManager.get().getCamera();

                    if (camera == null)
                        return;

                    final Camera.Parameters p = camera.getParameters();

                    //防止画面切换闪退
                    if (p == null)
                        return;

                    int zoom = (int) (p.getZoom() + (p.getMaxZoom() * total / 800f));
                    if (zoom > p.getMaxZoom())
                        zoom = p.getMaxZoom();
                    if (zoom <= 0)
                        zoom = 1;
                    if (valueAnimator != null && valueAnimator.isRunning())
                        valueAnimator.cancel();
                    valueAnimator = ValueAnimator.ofInt(p.getZoom(), zoom);
                    valueAnimator.setDuration(300);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            p.setZoom((Integer) animation.getAnimatedValue());
                            camera.setParameters(p);

                        }
                    });
                    valueAnimator.start();
                    change = 0;
                }

            }

            @Override
            public void onStepEnd() {

            }
        });

        mContainer.setOnTouchListener(onGestureListener);

    }

    private void initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
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

    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;
    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
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

    //打开闪光灯
    private void openLight() {
        if (mFlashing) {
            mFlashing = false;
            // 开闪光灯
            LightManager.openLight();
        }
    }

    //关闭
    private void closeLight() {

        if (!mFlashing) {
            mFlashing = true;
            // 关闪光灯
            LightManager.closeLight();
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraConfig.getInstance().getCameraPoint();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

            AccountLigFieAble.getInstance().setCallBack(new AccountLigFieAble.LightCallBack() {
                @Override
                public void lightValues(boolean isBright) {

                    if (isBright) {

                        if (lightLayout.getTag() == null || !(boolean) lightLayout.getTag()) {
                            lightLayout.setVisibility(View.INVISIBLE);
                            initScanerAnimation();
                        }
                    } else {

                        lightLayout.setVisibility(View.VISIBLE);

                        ImageView mQrLineView = findViewById(R.id.capture_scan_line);
                        mQrLineView.setAnimation(null);
                        mQrLineView.setVisibility(View.INVISIBLE);
                    }

                }
            });

        } catch (IOException | RuntimeException ioe) {
        }

        CameraCoordinateHandler.getInstance().startPreviewAndDecode();

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
                Result rawResult = RxQrBarParseTool.getInstance().decodeFromPhoto(photo);

                handleDecode(rawResult);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //========================================打开本地图片识别二维码 end=================================

    /***
     *
     * 获取到扫码数据后回调
     *
     */
    public void initCallBackResult(Result result) {

        if (result != null) {

            //扫描到数据,震动并回调
            //扫描成功之后的振动与声音提示
            /**
             * 扫描成功后是否震动
             */
            boolean vibrate = true;
            RxBeepTool.playBeep(this, vibrate);

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

        inactivityTimer.onActivity();
        initCallBackResult(result);

    }

    //使用自定义相册，选择图片后回调解析
    public void postImgFilePath(String path) {

        Result result = RxQrBarParseTool.getInstance().decodeFromPhoto(BitmapFactory.decodeFile(path));

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

    /**
     * Finishes an activity after a period of inactivity.
     *
     * @author vondear
     */
    public static class InactivityTimer {

        private static final int INACTIVITY_DELAY_SECONDS = 5 * 60;

        private final ScheduledExecutorService inactivityTimer =
                Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        private final Activity activity;
        private ScheduledFuture<?> inactivityFuture = null;

        public InactivityTimer(Activity activity) {
            this.activity = activity;
            onActivity();
        }

        public void onActivity() {
            cancel();
            inactivityFuture = inactivityTimer.schedule(new FinishListener(activity),
                    INACTIVITY_DELAY_SECONDS,
                    TimeUnit.SECONDS);
        }

        private void cancel() {
            if (inactivityFuture != null) {
                inactivityFuture.cancel(true);
                inactivityFuture = null;
            }
        }

        public void shutdown() {
            cancel();
            inactivityTimer.shutdown();
        }

        private final class DaemonThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        }


        public final class FinishListener
                implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

            private final Activity activityToFinish;

            public FinishListener(Activity activityToFinish) {
                this.activityToFinish = activityToFinish;
            }

            public void onCancel(DialogInterface dialogInterface) {
                run();
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                run();
            }

            public void run() {
                activityToFinish.finish();
            }

        }

    }
}