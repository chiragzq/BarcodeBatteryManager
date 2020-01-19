package com.chiragzq.barcodebatteryscanner;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class ViewDataActivity extends AppCompatActivity  {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_data);

        List<BatteryScan> scans = DataManager.getInstance().getScans(this);
        ArrayAdapter<BatteryScan> adapter = new ArrayAdapter<>(this, R.layout.battery_scan_data_view, scans);
        ((ListView) findViewById(R.id.battery_listview)).setAdapter(adapter);

        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }
}
