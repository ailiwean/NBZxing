package com.android.NBZxing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;
import com.NBZxing.lib.listener.ResultListener;
import com.NBZxing.lib.views.NBView;

public class MainActivity extends AppCompatActivity {

    NBView NBView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NBView = findViewById(R.id.wishView);
        NBView.onCreate(this);

        NBView.getDelegate().regResultListener(new ResultListener() {
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
        NBView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NBView.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        NBView.onRestart();
    }

    @Override
    public void onBackPressed() {
        NBView.onBackPressed();
        super.onBackPressed();
    }
}
