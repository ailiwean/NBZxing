<<<<<<< HEAD:module_camera/src/main/java/com/NBZxing/lib/views/LightView.java
package com.NBZxing.lib.views;
=======
package com.ailiwean.core.view;
>>>>>>> backTexture:module_camera/src/main/java/com/ailiwean/core/view/LightView.java

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

<<<<<<< HEAD:module_camera/src/main/java/com/NBZxing/lib/views/LightView.java
import com.NBZxing.lib.R;
import com.NBZxing.lib.util.LightControlUtils;
=======
import com.google.android.cameraview.R;
>>>>>>> backTexture:module_camera/src/main/java/com/ailiwean/core/view/LightView.java

/***
 *  Created by SWY
 *  DATE 2019/6/23
 *
 */
public class LightView extends FrameLayout {

    private TextView tv;
    private ImageView iv;
    private LightClick lightClick;

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
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    public void toggle() {

        if (tv.getText().equals("轻触照亮"))
            open();
        else close();

    }

    private void open() {
        isBright = true;
        tv.setText("轻触关闭");
        if (lightClick != null)
            lightClick.onClick(true);
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_open));

    }

    private void close() {
        isBright = false;
        tv.setText("轻触照亮");
        if (lightClick != null)
            lightClick.onClick(false);
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.light_close));

    }

    public void inital() {
        close();
        setVisibility(View.GONE);
    }

    public void setBright(boolean isBright) {
        if (!isBright) {
            setVisibility(View.VISIBLE);
        } else if (!this.isBright) {
            setVisibility(View.INVISIBLE);
        }
    }

    public interface LightClick {
        void onClick(boolean isOpen);
    }

    public void regLightClick(LightClick click) {
        this.lightClick = click;
    }

}
