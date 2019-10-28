package com.chiragzq.barcodebatteryscanner;


import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private int currentCameraId;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.scanner_view);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

        Toolbar myToolbar = findViewById(R.id.scanner_toolbar);
        setSupportActionBar(myToolbar);

        currentCameraId = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(currentCameraId);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public void changeCamera(MenuItem item) {
        int numCameras = Camera.getNumberOfCameras();
        currentCameraId = ++currentCameraId % numCameras;
        mScannerView.stopCamera();
        mScannerView.startCamera(currentCameraId);
    }

    @Override
    public void handleResult(Result rawResult) {
        String barcodeValue = rawResult.getText();
        Log.d("BARCODE VALUE", barcodeValue);

        Intent intent = new Intent();
        intent.putExtra("BARCODE_DATA", barcodeValue);
        setResult(RESULT_OK, intent);
        finish();

//        System.out.println(rawResult.);
//        Toast.makeText(this, "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat(), Toast.LENGTH_SHORT).show();
//        // Note:
//        // * Wait 2 seconds to resume the preview.
//        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
//        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(ScannerActivity.this);
//            }
//        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.change_camera_menu, menu);
        return true;
    }
}
