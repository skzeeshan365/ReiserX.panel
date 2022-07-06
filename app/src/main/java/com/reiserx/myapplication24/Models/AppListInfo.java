package com.reiserx.myapplication24.Models;

public class AppListInfo {
    String packageName, appID, Label;
    boolean processStatus;

    public AppListInfo() {
    }

    public AppListInfo(String packageName, String Label, boolean processStatus) {
        this.packageName = packageName;
        this.processStatus = processStatus;
        this.Label = Label;
    }

    public AppListInfo(String packageName, String Label, String appID, boolean processStatus) {
        this.packageName = packageName;
        this.appID = appID;
        this.Label = Label;
        this.processStatus = processStatus;
    }

    public AppListInfo(boolean processStatus) {
        this.processStatus = processStatus;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(boolean processStatus) {
        this.processStatus = processStatus;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }
}
