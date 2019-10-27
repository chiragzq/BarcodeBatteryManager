package com.chiragzq.barcodebatteryscanner;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = "scannerData";

    private static DataManager instance;

    private DataManager() {
        // Necessary to make constructor private
    }

    public void addScan(String battery, Context context) {
        try {
            FileOutputStream out = context.openFileOutput(battery, Context.MODE_APPEND);
            out.write((battery + "," + new Date().getTime() + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Do nothing
        }

    }

    public List<Pair<String, Date>> getScans(Context context) {
        try {
            FileInputStream in = context.openFileInput(FILE_NAME);
            BufferedReader reader  = new BufferedReader(new InputStreamReader(in));
            List<Pair<String, Date>> ret = new ArrayList<>();

            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                ret.add(new Pair<String, Date>(parts[0], new Date(Long.parseLong(parts[1]))));
            }
            return ret;
        } catch(FileNotFoundException e) {
            return new ArrayList<>();
        } catch(IOException e) {
            return new ArrayList<>();
        }
    }


    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
}
