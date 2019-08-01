package com.android.wishzxing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.google.zxing.Result;
import com.wishzixing.lib.listener.ResultListener;
import com.wishzixing.lib.views.WishView;

public class MainActivity extends AppCompatActivity {

    WishView wishView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wishView = findViewById(R.id.wishView);
        wishView.onCreate(this);

        wishView.getDelegate().regResultListener(new ResultListener() {
            @Override
            public void scanSucceed(Result result) {
                Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void scanImgFail() {

            }
        });

//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) wishView.getCropParseView().getLayoutParams();
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        params.width = (int) (params.width * 0.8);
        // wishView.getCropParseView().setLayoutParams(params);

        //  wishView.getDelegate().setSpareAutoFocus(AutoFocusConfig.Hybride);
        //wishView.getDelegate().setAutoFocusTimeThreshold(2000);

    }

    @Override
    protected void onStop() {
        super.onStop();
        wishView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wishView.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        wishView.onRestart();
    }

    @Override
    public void onBackPressed() {
        wishView.onBackPressed();
        super.onBackPressed();
    }
}
