package com.android.wishzxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wishzixing.lib.util.Utils;
import com.wishzixing.lib.views.WishView;

public class MainActivity extends AppCompatActivity {

    WishView wishView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wishView = findViewById(R.id.wishView);
        wishView.onCreate(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wishView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wishView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wishView.onDestory();
    }
}
