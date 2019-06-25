package com.wishzixing.lib.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishzixing.lib.R;
import com.wishzixing.lib.util.LightControlUtils;

/***
 *  Created by SWY
 *  DATE 2019/6/23
 *
 */
public class LightView extends FrameLayout {

    private TextView tv;
    private ImageView iv;


    private boolean isBright;

    public LightView(Context context) {
        super(context);
        initView();
    }

    public LightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.light_layout, null);
        iv = v.findViewById(R.id.light_img);
        tv = v.findViewById(R.id.light_text);

        addView(v);

    }

    public void toggle() {

        if (tv.getText().equals("轻触照亮"))
            open();
        else close();

    }

    public void open() {

        isBright = true;
        tv.setText("轻触关闭");
        LightControlUtils.openLight();
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_open));

    }

    public void close() {
        isBright = false;
        tv.setText("轻触照亮");
        LightControlUtils.closeLight();
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_close));

    }

    public boolean isBright() {
        return isBright;
    }

}
