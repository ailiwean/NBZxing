package com.NBZxing.lib.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.NBZxing.lib.R;

/***
 *  Created by SWY
 *  DATE 2019/6/25
 *
 */
public class ScanView extends FrameLayout {

    private ImageView imageView;
    private TranslateAnimation animation;

    public ScanView(Context context) {
        super(context);
        initView();
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        imageView = new ImageView(getContext());
        imageView.setBackgroundResource(R.drawable.kakalib_scan_ray);
        addView(imageView);
        setVisibility(INVISIBLE);
    }

    public void startScanAnima() {
        setVisibility(VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(0, 0, -getMeasuredHeight(), 0);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Integer.MAX_VALUE);
        animation.setDuration(1000);
        setAnimation(animation);
        animation.start();
    }

    public void stopScanAnima() {
        setVisibility(INVISIBLE);
        if (getAnimation() != null)
            getAnimation().cancel();
    }


}
