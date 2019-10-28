package com.chiragzq.barcodebatteryscanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void openScannerView(View v) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String battery = data.getStringExtra("BARCODE_DATA");
            Log.d("BARCODE DATA RECEIVED", data.getStringExtra("BARCODE_DATA"));

            //TODO why doesn't this work
            Toast.makeText(getApplicationContext(), "Scanned Battery: " + battery, Toast.LENGTH_LONG).show();

            DataManager.getInstance().addScan(battery, getApplicationContext());
            System.out.println(DataManager.getInstance().getScans(getApplicationContext()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }
}