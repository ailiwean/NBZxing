package com.wishzixing.lib.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishzixing.lib.R;

/***
 *  Created by SWY
 *  DATE 2019/6/23
 *
 */
public class LightView extends FrameLayout {

    private TextView tv;
    private ImageView iv;

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

        if (tv.getText().equals("轻触打开"))
            open();
        else close();

    }

    public void open() {

        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_open));
        tv.setText("");

    }

    public void close() {

        tv.setText("轻触打开");
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_close));

    }

}
