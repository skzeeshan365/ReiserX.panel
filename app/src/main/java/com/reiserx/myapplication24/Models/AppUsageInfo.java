package com.reiserx.myapplication24.Models;

public class AppUsageInfo {
    public String appName, packageName;
    public long timeInForeground;
    public int launchCount;

    public AppUsageInfo(String appName, String packageName, long timeInForeground, int launchCount) {
        this.appName = appName;
        this.packageName = packageName;
        this.timeInForeground = timeInForeground;
        this.launchCount = launchCount;
    }

    public AppUsageInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTimeInForeground() {
        return timeInForeground;
    }

    public void setTimeInForeground(long timeInForeground) {
        this.timeInForeground = timeInForeground;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }
}
