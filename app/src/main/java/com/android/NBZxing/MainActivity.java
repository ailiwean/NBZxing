package com.android.NBZxing;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zhihu.matisse.Matisse;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.<CusZxingView>findViewById(R.id.zxingview)
                .synchLifeStart(this);

//        ZxingFragment fragment = new ZxingFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.parent, fragment)
//                .commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String path = Matisse.obtainPathResult(data).get(0);
                this.<CusZxingView>findViewById(R.id.zxingview).toParse(path);
            }
        }

    }

}