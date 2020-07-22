package com.ailiwean.core;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ClassName: OnGestureListener
 * @Description: 封装了双击以及双指手势操作
 * @Author: SWY
 * @Date: 2019/4/3 17:39
 */
public abstract class OnGestureListener implements View.OnTouchListener {

    /**
     * 两次点击时间间隔，单位毫秒
     */
    private final int totalTime = 500;

    private long lastClickTime = 0L;


    Context mContext;

    //记录第一根手指xy
    PointF first = new PointF(0, 0);

    //第二根手指
    PointF sencond = new PointF(0, 0);

    //两指间距离
    float distance = 0;

    //一个手势过程中总的偏移量(distance差值)
    float total = 0;

    public OnGestureListener(Context context) {
        this.mContext = context;
    }

    public abstract void onStepFingerChange(float total, float offset);

    public abstract void onStepEnd();

    public abstract void onDoubleClick();

    /**
     * 触摸事件处理
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            distance = 0;
            total = 0;
        }

        /***
         *  监听手势
         */
        if (event.getPointerCount() > 1 && event.getActionMasked() == MotionEvent.ACTION_MOVE) {

            first.x = event.getX(0);
            first.y = event.getY(0);
            sencond.x = event.getX(1);
            sencond.y = event.getY(1);

            if (distance != 0) {
                int offset = (int) (getDistance(first, sencond) - distance);
                total += offset;
                onStepFingerChange(total, offset);
            }
            distance = getDistance(first, sencond);
            return true;
        }

        //一根手指离开重置
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            distance = 0;
            onStepEnd();
        }

        /**
         * 监听双击
         */
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (System.currentTimeMillis() - lastClickTime < totalTime) {
                onDoubleClick();
                lastClickTime = 0L;
            } else
                lastClickTime = System.currentTimeMillis();
        }

        return true;
    }

    //获取两指间距离
    private float getDistance(PointF a, PointF b) {

        int py = (int) (Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));

        return (float) Math.sqrt(py);
    }

}
