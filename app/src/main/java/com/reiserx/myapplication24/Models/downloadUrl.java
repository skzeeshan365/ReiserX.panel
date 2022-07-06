package com.reiserx.myapplication24.Models;

public class downloadUrl {
    String url, Id;
    long timeStamp;

    public downloadUrl() {
    }

    public downloadUrl(String url, long timeStamp) {
        this.url = url;
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
