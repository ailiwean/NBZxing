package com.NBZxing.lib.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
import com.NBZxing.lib.PermissionActivity;
import com.NBZxing.lib.R;
import com.NBZxing.lib.WishLife;
import com.NBZxing.lib.config.ParseRectConfig;
import com.NBZxing.lib.config.PointConfig;
import com.NBZxing.lib.config.ScanTypeConfig;
import com.NBZxing.lib.listener.LightCallBack;
import com.NBZxing.lib.listener.OnGestureListener;
import com.NBZxing.lib.listener.ResultListener;
import com.NBZxing.lib.listener.SurfaceListener;
import com.NBZxing.lib.util.PermissionUtils;
import com.NBZxing.lib.util.Utils;
import com.NBZxing.lib.util.WindowUitls;
import com.NBZxing.lib.util.ZoomUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 */
public class NBView extends FrameLayout implements WishLife, View.OnClickListener {


    Activity mActivity;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;

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

    public NBView(Context context) {
        super(context);
    }

    public NBView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NBView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        OnGestureListener onGestureListener = new OnGestureListener(Utils.getContext());
        onGestureListener.regOnDoubleClickCallback(new OnGestureListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                ZoomUtils.zoomToggle();
            }
        });

        onGestureListener.regOnDoubleFingerCallback(new OnGestureListener.DoubleFingerCallback() {

            int defZoom;

            @Override
            public void onStepFingerChange(float total, float value) {

                if (defZoom == 0)
                    defZoom = ZoomUtils.getZoom();

                int zoom = (int) (defZoom + total / 10);
                if (zoom > ZoomUtils.getMaxZoom())
                    zoom = ZoomUtils.getMaxZoom();
                if (zoom <= 0)
                    zoom = 1;

                ZoomUtils.setZoom(zoom);
            }

            @Override
            public void onStepEnd() {
                defZoom = 0;
            }
        });
        mContainer.setOnTouchListener(onGestureListener);

    }

    //初始化默认Delegate
    private void initDefDelegate() {

        wishViewDelegate = new WishViewDelegate(textureView);

        wishViewDelegate.setParseRectFromView(mCropLayout);

        wishViewDelegate.setScanModel(ScanTypeConfig.ALL, null);

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
        Utils.init(activity);
        PermissionUtils.init(activity);
        initView();
        initDefDelegate();
        initConfig();
        wishViewDelegate.onCreate(activity);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //   activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestPermission();
    }

    private void initConfig() {
        textureView.post(new Runnable() {
            @Override
            public void run() {
                //为解决某些机型获取屏幕高度异常问题
                // Point screenPoint = new Point(decorView.getMeasuredWidth(), decorView.getMeasuredHeight());
                // PointConfig.getInstance().setScreenPoint(screenPoint);
                // PointConfig.getInstance().setShowPoint(new Point(getMeasuredWidth(), getMeasuredHeight()));
                //设定预览尺寸,即解析框取决于框内所在像素
                ParseRectConfig.getInstance().setPreview(textureView);
            }
        });
    }

    private void requestPermission() {

        if (PermissionUtils.hasPermission())
            return;

        Intent intent = new Intent(getContext(), PermissionActivity.class);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(0, 0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH");
        mActivity.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onResume();
                mActivity.unregisterReceiver(this);
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
//            get.get().finish();
        }

        if (v.getId() == R.id.openAlbum) {
            Toast.makeText(getContext(), "打开相册", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == lightView.getId()) {
            lightView.toggle();
        }
    }

    public NBView bindParseView(View view) {
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
    public RelativeLayout getCropParseView() {
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
