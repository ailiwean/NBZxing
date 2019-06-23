package com.wishzixing.lib.views;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
     * 整体根布局
     */
    private RelativeLayout mContainer = null;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;
    private LinearLayout lightLayout;

    //调焦连续值生成
    private ValueAnimator valueAnimator;

    private SurfaceView surfaceView;

    //是否能获取Surface输出源
    private boolean hasSurface = false;

    private Handler lazyLoading = new Handler(Looper.getMainLooper());

    WishViewDelegate wishViewDelegate;
    private LightView lightView;
    private TextView hintView;

    public WishView(Context context) {
        super(context);
    }

    public WishView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {

        //添加内容
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_scaner_code, null);
        addView(view);
        mContainer = findViewById(R.id.capture_containter);
        mCropLayout = findViewById(R.id.capture_crop_layout);
        surfaceView = findViewById(R.id.capture_preview);

        lightView = findViewById(R.id.lightView);
        lightView.setOnClickListener(this);
        lightView.setVisibility(INVISIBLE);

        hintView = findViewById(R.id.loadingHint);

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
            }

            @Override
            public void onDestory() {
                hintView.setText("正在加载...");
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

    }

    public WishViewDelegate getDelegate() {
        if (wishViewDelegate == null) {
            throw new RuntimeException("WishView must be onCreat");
        }
        return wishViewDelegate;
    }
}
