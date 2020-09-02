package com.ailiwean.core.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.google.android.cameraview.R;

/**
 * @Package: com.byh.lib.usercommon.global.widget
 * @ClassName: MaskView
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/9/2 9:48 AM
 */
public class CameraZxingMaskView extends View {

    private Paint paint;
    private Rect drawRect;
    private Rect clearRect;
    private float margin_left;
    private float margin_top;
    private float margin_right;
    private float margin_bottom;
    private int id;
    private int bgColor;

    public CameraZxingMaskView(Context context) {
        this(context, null);
    }

    public CameraZxingMaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraZxingMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CameraZxingMaskView);
        id = typedArray.getResourceId(R.styleable.CameraZxingMaskView_camera_clearById, 0);
        margin_left = typedArray.getDimension(R.styleable.CameraZxingMaskView_camera_clear_margin_left, 0f);
        margin_top = typedArray.getDimension(R.styleable.CameraZxingMaskView_camera_clear_margin_top, 0f);
        margin_right = typedArray.getDimension(R.styleable.CameraZxingMaskView_camera_clear_margin_right, 0f);
        margin_bottom = typedArray.getDimension(R.styleable.CameraZxingMaskView_camera_clear_margin_bottom, 0f);
        bgColor = typedArray.getColor(R.styleable.CameraZxingMaskView_camera_maskBgColor, Color.parseColor("#1f000000"));
        paint.setColor(bgColor);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        typedArray.recycle();
    }

    public void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        post(() -> {
            if (drawRect == null)
                drawRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
            if (clearRect != null)
                return;
            ViewGroup viewGroup = (ViewGroup) getParent();
            View parseRectView = viewGroup.findViewById(id);
            if (parseRectView != null) {
                int[] parse_screen = new int[2];
                parseRectView.getLocationOnScreen(parse_screen);

                int[] parent_screen = new int[2];
                viewGroup.getLocationInWindow(parent_screen);

                clearRect = new Rect(
                        parse_screen[0] - parent_screen[0],
                        parse_screen[1] - parent_screen[1],
                        parse_screen[0] - parent_screen[0] + parseRectView.getMeasuredWidth(),
                        parse_screen[1] - parent_screen[1] + parseRectView.getMeasuredHeight());
            } else {
                clearRect = new Rect(
                        (int) margin_left,
                        (int) margin_top,
                        (int) (getMeasuredWidth() - margin_right),
                        (int) (getMeasuredHeight() - margin_bottom));
            }
            invalidate();
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawRect != null)
            canvas.drawRect(drawRect, paint);

        if (clearRect != null) {
            canvas.save();
            canvas.clipRect(clearRect);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.restore();
        }
    }

    public void setClearRect(Rect rect) {
        this.clearRect = rect;
        invalidate();
    }

    public void setMaskingColor(@ColorInt int color) {
        paint.setColor(color);
        invalidate();
    }
}
