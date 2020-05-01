package com.ailiwean.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.R;

import org.jetbrains.annotations.NotNull;

/**
 * @Package: com.google.android.cameraview
 * @ClassName: ByScannerView
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/8 11:39 AM
 */
public class ByScannerView extends ZxingCameraView {
    public ByScannerView(Context context) {
        super(context);
    }

    public ByScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ByScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NotNull
    @Override
    public View provideFloorView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.by_scan_layout, this, false);
    }

    @Override
    public void resultBack(@NotNull String content) {

    }
}
