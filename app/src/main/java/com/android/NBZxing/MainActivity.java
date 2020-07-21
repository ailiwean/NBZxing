package com.android.NBZxing;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        this.<CusZxingView>findViewById(R.id.zxingview)
//                .synchLifeStart(this);

        ZxingFragment fragment = new ZxingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.parent, fragment)
                .commit();
    }
}
