package com.ailiwean.core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.cameraview.BaseCameraView;
import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.R;

import org.jetbrains.annotations.NotNull;


/**
 * @Package: com.google.android.cameraview
 * @ClassName: ByCameraView
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020-03-25 14:12
 */
public class ByFaceCameraView extends BaseCameraView implements View.OnClickListener {

    PictorBack back;
    private ImageView bt;

    public ByFaceCameraView(Context context) {
        super(context);
        init();
    }

    public ByFaceCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ByFaceCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFacing(FACING_FRONT);
    }

    @Override
    public void onPictureTake(@NotNull CameraView camera, @NotNull byte[] data) {
        openTakePictureAnim();
        if (back != null)
            back.onBack(data);
    }

    @Override
    public void onCameraOpen(@NotNull CameraView camera) {
        openTakePictureAnim();
    }

    @Override
    public void onClick(View v) {
        closeTakePictureAnim();
        takePicture();
    }

    public void regTakePictorBack(PictorBack back) {
        this.back = back;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NotNull
    public View provideFloorView() {
        ViewGroup rootView = (ViewGroup) LayoutInflater.from(getContext()).
                inflate(R.layout.by_face_layout, this, false);

        ImageView bg = rootView.findViewById(R.id.face_bg);
        Glide.with(this).load(R.drawable.ic_camera_face_bg).into(bg);

        bt = rootView.findViewById(R.id.face_bt);
        bt.setOnClickListener(this);
        bt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bt.animate().scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(100)
                        .start();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                bt.animate().scaleY(1f)
                        .scaleX(1f)
                        .setDuration(100)
                        .start();
            }
            return false;
        });
        return rootView;
    }

    public interface PictorBack {
        void onBack(byte[] data);
    }

    public void openTakePictureAnim() {
        bt.setEnabled(true);
        bt.animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .start();
    }

    public void closeTakePictureAnim() {
        bt.setEnabled(false);
        bt.animate().scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .start();
    }

}
