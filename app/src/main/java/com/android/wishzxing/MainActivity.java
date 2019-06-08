package com.android.wishzxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wishzixing.lib.util.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.init(this);
        Intent intent = new Intent(this, Scanner.class);
        startActivity(intent);
    }
}
