package com.reiserx.myapplication24.Models;

public class policyModel {
    String data;
    long timestamp;

    public policyModel(String data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public policyModel() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
