package com.reiserx.myapplication24.Models;

public class BannedDevices {
    String buildno, Modelno;
    public BannedDevices() {

    }

    public BannedDevices(String buildno, String modelno) {
        this.buildno = buildno;
        Modelno = modelno;
    }

    public String getBuildno() {
        return buildno;
    }

    public void setBuildno(String buildno) {
        this.buildno = buildno;
    }

    public String getModelno() {
        return Modelno;
    }

    public void setModelno(String modelno) {
        Modelno = modelno;
    }
}
