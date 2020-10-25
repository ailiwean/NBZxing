package com.ailiwean.core.view.style1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
public class MaskView extends View {

    private Paint paint;
    private Rect clearRect = new Rect();
    private Rect[] drawRects;
    private float margin_left;
    private float margin_top;
    private float margin_right;
    private float margin_bottom;
    private int id;
    private int bgColor;

    public MaskView(Context context) {
        this(context, null);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaskView);
        id = typedArray.getResourceId(R.styleable.MaskView_camera_clearById, 0);
        margin_left = typedArray.getDimension(R.styleable.MaskView_camera_clear_margin_left, 0f);
        margin_top = typedArray.getDimension(R.styleable.MaskView_camera_clear_margin_top, 0f);
        margin_right = typedArray.getDimension(R.styleable.MaskView_camera_clear_margin_right, 0f);
        margin_bottom = typedArray.getDimension(R.styleable.MaskView_camera_clear_margin_bottom, 0f);
        bgColor = typedArray.getColor(R.styleable.MaskView_camera_maskBgColor, Color.parseColor("#1f000000"));
        paint.setColor(bgColor);
        typedArray.recycle();
    }

    private Rect[] clearRect2Rect() {
        Rect[] rects = new Rect[4];
        rects[0] = new Rect(0, 0, getMeasuredWidth(), clearRect.top);
        rects[1] = new Rect(0, clearRect.top, clearRect.left, clearRect.bottom);
        rects[2] = new Rect(clearRect.right, clearRect.top, getMeasuredWidth(), clearRect.bottom);
        rects[3] = new Rect(0, clearRect.bottom, getMeasuredWidth(), getMeasuredHeight());
        return rects;
    }

    public void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        post(() -> {
            ViewGroup viewGroup = (ViewGroup) getParent();
            View parseRectView = viewGroup.findViewById(id);
            if (parseRectView != null) {
                int[] parse_screen = new int[2];
                parseRectView.getLocationOnScreen(parse_screen);

                int[] parent_screen = new int[2];
                viewGroup.getLocationInWindow(parent_screen);

                clearRect.left = parse_screen[0] - parent_screen[0];
                clearRect.top = parse_screen[1] - parent_screen[1];
                clearRect.right = parse_screen[0] - parent_screen[0] + parseRectView.getMeasuredWidth();
                clearRect.bottom = parse_screen[1] - parent_screen[1] + parseRectView.getMeasuredHeight();

            } else {

                clearRect.left = (int) margin_left;
                clearRect.top = (int) margin_top;
                clearRect.right = (int) (getMeasuredWidth() - margin_right);
                clearRect.bottom = (int) (getMeasuredHeight() - margin_bottom);

            }
            drawRects = clearRect2Rect();
            invalidate();
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (clearRect == null)
            return;
        if (drawRects != null)
            for (Rect rect : drawRects)
                canvas.drawRect(rect, paint);
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
