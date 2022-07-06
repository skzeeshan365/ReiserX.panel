package com.reiserx.myapplication24.Models;

public class AudiosDownloadUrl {
    String url, name, Id;
    long timeStamp;

    public AudiosDownloadUrl(String url, String name, long timeStamp) {
        this.url = url;
        this.name = name;
        this.timeStamp = timeStamp;
    }

    public AudiosDownloadUrl() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
