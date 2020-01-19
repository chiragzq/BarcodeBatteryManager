package com.chiragzq.barcodebatteryscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DataManager {
    private static final String DATA_FILE_NAME = "scannerData";
    private static final String TMP_FILE_NAME = "tmpData";

    private static DataManager instance;

    private DataManager() {
        // Necessary to make constructor private
    }

    public void addScan(String battery, Context context) {
        try {
            FileOutputStream out = context.openFileOutput(DATA_FILE_NAME, Context.MODE_APPEND);
            out.write((battery + "," + new Date().getTime() + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Do nothing
        }

    }

    /**
     * Get a list of battery scans, with the most recent being first
     */
    public List<BatteryScan> getScans(Context context) {
        try {
            FileInputStream in = context.openFileInput(DATA_FILE_NAME);
            BufferedReader reader  = new BufferedReader(new InputStreamReader(in));
            List<BatteryScan> ret = new ArrayList<>();

            String line;
            while((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    ret.add(new BatteryScan(parts[0], new Date(Long.parseLong(parts[1]))));
                } catch(Exception e) {
                    Log.e("BATTERY SCAN", "An error occurred when reading scans");
                    Log.e("BATTERY SCAN", "Line data: " + line);
                    e.printStackTrace();
                }
            }
            Collections.reverse(ret);
            return ret;
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch(IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public long getLastScanTimestamp(Context context) {
        try {
            FileInputStream in = context.openFileInput(TMP_FILE_NAME);
            BufferedReader reader  = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            reader.close();
            return Long.parseLong(line);
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream out = context.openFileOutput(TMP_FILE_NAME, Context.MODE_PRIVATE);
                out.write("0".getBytes());
                out.close();
                return 0;
            } catch (IOException e1) {
                e1.printStackTrace();
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setLastScanTimestamp(Context context, long timestamp) {
        try {
            FileOutputStream out = context.openFileOutput(TMP_FILE_NAME, Context.MODE_PRIVATE);
            out.write(("" + timestamp + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            // oh well
        }
    }

    public int syncBatteries(Context context) {
        final List<BatteryScan> scans = getScans(context);
        if(scans.isEmpty()) return 0;

        long lastScan = getLastScanTimestamp(context);
        Log.i("BATTERY_SYNC", new Date(lastScan).toString());
        int startScanIndex = 0;
        while(startScanIndex < scans.size() && scans.get(startScanIndex).getScanTime().getTime() > lastScan) startScanIndex++;
        startScanIndex--;
        int syncs = 0;
        for(;startScanIndex >= 0; startScanIndex--) {
            final int _i = startScanIndex;
            try {
                @SuppressLint("StaticFieldLeak")
                boolean success = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... args) {
                        try {
                            Log.i("BATTERY_SYNC", "Syncing " + _i);
                            URL url = new URL("https://robotics.harker.org/battery/postScan");
//                            URL url = new URL("https://49d717cf.ngrok.io/battery/postScan");
                            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                            String body = "{\"data\": \"" + scans.get(_i).getId() + "__" + scans.get(_i).getScanTime().getTime() + "\"}";
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty("Content-Length", "" + body.length());
                            urlConnection.setRequestMethod("POST");
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), StandardCharsets.UTF_8));
                            writer.write(body);
                            writer.flush();
                            writer.close();

                            urlConnection.connect();
                            System.out.println(urlConnection.getResponseMessage());
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }.execute().get();
                if(success) {
                    syncs ++;
                } else {
                    startScanIndex--;
                    break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
        long newLastScan = scans.get(startScanIndex + 1).getScanTime().getTime();
        setLastScanTimestamp(context, newLastScan);

        return syncs;
    }


    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
}
