package com.ailiwean.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.cameraview.R;

/***
 *  Created by SWY
 *  DATE 2019/6/23
 *
 */
public class ScanLightView extends FrameLayout implements ScanLightViewCallBack {

    private TextView tv;
    private ImageView iv;

    private boolean isOpen;

    Runnable open;
    Runnable close;

    public ScanLightView(Context context) {
        super(context);
        initView();
    }

    public ScanLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScanLightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.light_layout, null);
        iv = v.findViewById(R.id.light_img);
        tv = v.findViewById(R.id.light_text);
        addView(v);
        setOnClickListener(v1 -> toggle());
    }

    public void toggle() {
        if (tv.getText().equals("轻触照亮"))
            open();
        else close();
    }

    private void open() {
        if (open != null)
            open.run();
        isOpen = true;
        tv.setText("轻触关闭");
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_open));
    }

    private void close() {
        if (close != null)
            close.run();
        isOpen = false;
        tv.setText("轻触照亮");
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_close));

    }


    @Override
    public void lightBrighter() {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void lightDark() {
        if (!isOpen)
            setVisibility(View.GONE);
    }

    @Override
    public void regLightOperator(Runnable open, Runnable close) {
        this.open = open;
        this.close = close;
    }

    @Override
    public void cameraStartLaterInit() {
        close();
        setVisibility(View.GONE);
    }

}
