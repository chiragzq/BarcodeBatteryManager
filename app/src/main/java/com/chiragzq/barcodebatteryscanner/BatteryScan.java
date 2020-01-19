package com.chiragzq.barcodebatteryscanner;

import java.util.Date;

public class BatteryScan {
    private String id;
    private Date scanTime;

    public BatteryScan(String id, Date scanTime) {
        this.id = id;
        this.scanTime = scanTime;
    }

    public String getId() {
        return id;
    }

    public Date getScanTime() {
        return scanTime;
    }

    @Override
    public String toString() {
        return id + " - " + scanTime.toString().substring(0, 20);// + scanTime.toString().substring(24);
    }
}
