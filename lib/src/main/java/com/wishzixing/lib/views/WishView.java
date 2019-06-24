package com.wishzixing.lib.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.wishzixing.lib.R;
import com.wishzixing.lib.WishLife;
import com.wishzixing.lib.config.AutoFocusConfig;
import com.wishzixing.lib.config.ScanConfig;
import com.wishzixing.lib.listener.LightCallBack;
import com.wishzixing.lib.listener.OnGestureListener;
import com.wishzixing.lib.listener.ResultListener;
import com.wishzixing.lib.listener.SurfaceListener;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.util.PermissionUtils;
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

    private LightView lightView;

    private TextView hintView;

    private FrameLayout lightParent;

    WishViewDelegate wishViewDelegate;
    private ScanView scanView;

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
        /*
      整体根布局
     */ /**
         * 整体根布局
         */RelativeLayout mContainer = findViewById(R.id.capture_containter);


        mCropLayout = findViewById(R.id.capture_crop_layout);

        surfaceView = findViewById(R.id.capture_preview);

        lightView = findViewById(R.id.lightView);
        lightView.setOnClickListener(this);
        lightView.setVisibility(INVISIBLE);
        hintView = findViewById(R.id.loadingHint);
        lightParent = findViewById(R.id.lightparent);

        scanView = findViewById(R.id.scanView);

        ImageView ivBack = findViewById(R.id.back);
        TextView tvAlbum = findViewById(R.id.openAlbum);
        ivBack.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        RelativeLayout titleParent = findViewById(R.id.rl_title);

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

        wishViewDelegate = new WishViewDelegate(surfaceView);

        wishViewDelegate.setParseRectFromView(mCropLayout);
        wishViewDelegate.setAutoFocusModel(AutoFocusConfig.PIXVALUES);
        wishViewDelegate.setScanModel(ScanConfig.ALL);

        wishViewDelegate.regAccountLigListener(new LightCallBack() {
            @Override
            public void lightValues(boolean isBright) {

                if (!isBright) {
                    lightView.setVisibility(VISIBLE);
                    lightView.close();
                } else {
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
            public void onCreate() {
                hintView.setText("");
                scanView.startScanAnima();
            }

            @Override
            public void onDestory() {
                hintView.setText("正在加载...");
                scanView.stopScanAnima();
            }
        });

    }

    @Override
    public void onCreate(Activity activity) {
        get = new WeakReference<>(activity);
        PermissionUtils.init(get.get());
        initView();
        initDefDelegate();
        wishViewDelegate.onCreate(activity);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onResume() {
        wishViewDelegate.onResume();
    }

    @Override
    public void onPause() {
        wishViewDelegate.onPause();
    }

    @Override
    public void onStop() {
        wishViewDelegate.onStop();
    }

    @Override
    public void onDestory() {
        wishViewDelegate.onDestory();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.back) {
            get.get().finish();
        }

        if (v.getId() == R.id.openAlbum) {
            Toast.makeText(getContext(), "打开相册", Toast.LENGTH_SHORT).show();
        }

    }

    public WishViewDelegate getDelegate() {
        if (wishViewDelegate == null) {
            throw new RuntimeException("WishView must be onCreat");
        }
        return wishViewDelegate;
    }

}
