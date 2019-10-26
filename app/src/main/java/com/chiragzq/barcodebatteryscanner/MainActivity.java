package com.chiragzq.barcodebatteryscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
    }

    public void openScannerView(View v) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivity(intent);
    }

}