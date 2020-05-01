package com.android.NBZXing;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.nbzxing.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.<CusZxing>findViewById(R.id.cusZxing)
                .synchLifeStart(this);
    }
}
