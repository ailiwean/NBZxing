package com.wishzixing.lib.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.wishzixing.lib.PermissionActivity;
import com.wishzixing.lib.R;
import com.wishzixing.lib.WishLife;
import com.wishzixing.lib.config.ParseRectConfig;
import com.wishzixing.lib.config.PointConfig;
import com.wishzixing.lib.config.ScanConfig;
import com.wishzixing.lib.listener.LightCallBack;
import com.wishzixing.lib.listener.OnGestureListener;
import com.wishzixing.lib.listener.ResultListener;
import com.wishzixing.lib.listener.SurfaceListener;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.util.PermissionUtils;
import com.wishzixing.lib.util.Utils;
import com.wishzixing.lib.util.WindowUitls;
import com.wishzixing.lib.util.ZoomUtils;

import java.lang.ref.WeakReference;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 */
public class WishView extends FrameLayout implements WishLife, View.OnClickListener {

    WeakReference<Activity> get;
    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;

    private SurfaceView surfaceView;

    private TextureView textureView;

    private LightView lightView;

    private TextView hintView;

    private FrameLayout lightParent;

    WishViewDelegate wishViewDelegate;
    private ScanView scanView;
    private RelativeLayout title;
    private FrameLayout scanParent;
    private ImageView ivBack;
    private TextView tvAlbum;

    public WishView(Context context) {
        super(context);
    }

    public WishView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        //添加内容
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_scaner_code, null);
        addView(view);

        RelativeLayout mContainer = findViewById(R.id.capture_containter);

        mCropLayout = findViewById(R.id.capture_crop_layout);

        // surfaceView = findViewById(R.id.surface_preview);
        textureView = findViewById(R.id.texture_preview);

        lightView = findViewById(R.id.lightView);
        lightView.setOnClickListener(this);
        lightView.setVisibility(INVISIBLE);
        hintView = findViewById(R.id.loadingHint);
        lightParent = findViewById(R.id.lightparent);

        scanView = findViewById(R.id.scanView);
        scanParent = findViewById(R.id.scan_animation_layout);

        ivBack = findViewById(R.id.back);
        tvAlbum = findViewById(R.id.openAlbum);
        ivBack.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        title = findViewById(R.id.rl_title);

        OnGestureListener onGestureListener = new OnGestureListener(get.get());
        onGestureListener.regOnDoubleClickCallback(new OnGestureListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                ZoomUtils.zoomToggle();
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
                    ZoomUtils.animalZoom(zoom);
                    change = 0;
                }

            }

            @Override
            public void onStepEnd() {
                change = 0;
            }
        });
        mContainer.setOnTouchListener(onGestureListener);

    }

    //初始化默认Delegate
    private void initDefDelegate() {

        //  wishViewDelegate = new WishViewDelegate(surfaceView);

        wishViewDelegate = new WishViewDelegate(textureView);

        wishViewDelegate.setParseRectFromView(mCropLayout);

        wishViewDelegate.setScanModel(ScanConfig.ALL);

        wishViewDelegate.regAccountLigListener(new LightCallBack() {
            @Override
            public void lightValues(boolean isBright) {

                if (!isBright) {
                    lightView.setVisibility(VISIBLE);
                } else if (!lightView.isBright()) {
                    lightView.setVisibility(INVISIBLE);
                }

            }
        });

        wishViewDelegate.regResultListener(new ResultListener() {
            @Override
            public void scanSucceed(Result result) {

            }

            @Override
            public void scanImgFail() {

            }
        });

        wishViewDelegate.regSurfaceListener(new SurfaceListener() {
            @Override
            public void onVisiable() {
                hintView.setText("");
                scanView.startScanAnima();
            }

            @Override
            public void onNoVisible() {
                hintView.setText("正在加载...");
                scanView.stopScanAnima();
            }

            @Override
            public void onDestory() {

            }
        });

    }

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Activity activity) {
        get = new WeakReference<>(activity);
        PermissionUtils.init(get.get());
        Utils.init(get.get());
        initView();
        initDefDelegate();
        initConfig();
        initAccelerated();
        wishViewDelegate.onCreate(activity);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //   activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestPermission();
    }

    private void initConfig() {
        textureView.post(new Runnable() {
            @Override
            public void run() {
                View decorView = get.get().getWindow().getDecorView();
                //为解决某些机型获取屏幕高度异常问题
                Point screenPoint = new Point(decorView.getMeasuredWidth(), decorView.getMeasuredHeight() + WindowUitls.getStatusBarHeight());
                PointConfig.getInstance().setScreenPoint(screenPoint);
                PointConfig.getInstance().setShowPoint(new Point(getMeasuredWidth(), getMeasuredHeight()));
                //设定预览尺寸,即解析框取决于框内所在像素
                ParseRectConfig.getInstance().setPreview(textureView);
            }
        });
    }

    //启用硬件加速
    private void initAccelerated() {

        get.get().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        if (surfaceView != null)
            if (surfaceView.isHardwareAccelerated())
                surfaceView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (textureView != null)
            if (textureView.isHardwareAccelerated())
                textureView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    private void requestPermission() {

        if (PermissionUtils.hasPermission())
            return;

        Intent intent = new Intent(get.get(), PermissionActivity.class);
        get.get().startActivity(intent);
        get.get().overridePendingTransition(0, 0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH");
        get.get().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onResume();
                get.get().unregisterReceiver(this);
            }
        }, intentFilter);

    }

    @Override
    public void onResume() {
        wishViewDelegate.onResume();
    }


    @Override
    public void onStop() {
        wishViewDelegate.onStop();
    }

    @Override
    public void onRestart() {
        wishViewDelegate.onRestart();
    }

    @Override
    public void onBackPressed() {
        wishViewDelegate.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.back) {
            get.get().finish();
        }

        if (v.getId() == R.id.openAlbum) {
            Toast.makeText(getContext(), "打开相册", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == lightView.getId()) {
            lightView.toggle();
        }
    }

    public WishView bindParseView(View view) {
        if (view.getParent() != null) {
            ViewGroup vp = (ViewGroup) view.getParent();
            vp.removeView(view);
        }
        addView(view);
        ParseRectConfig.getInstance().setParseRectFromView(view);
        return this;
    }

    public WishViewDelegate getDelegate() {
        if (wishViewDelegate == null) {
            throw new RuntimeException("WishView must be onCreat");
        }
        return wishViewDelegate;
    }

    //获取默认剪裁解析View
    public RelativeLayout getCropView() {
        return mCropLayout;
    }

    //手电筒View的Parent
    public FrameLayout getLightParent() {
        return lightParent;
    }

    //标题View
    public RelativeLayout getTitle() {
        return title;
    }

    //获取扫描动画View的Parent
    public FrameLayout getScanParent() {
        return scanParent;
    }

    //获取中心提示View
    public TextView getHintView() {
        return hintView;
    }

    //返回
    public ImageView getBackView() {
        return ivBack;
    }

    //相册
    public TextView getAlbumView() {
        return tvAlbum;
    }

}
