package com.reiserx.myapplication24.Models;

public class FileUpload {
    public String url;
    public String name;
    public String id, database_ID;

    public FileUpload() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FileUpload(String url, String name) {
        this.url = url;
        this.name = name;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatabase_ID() {
        return database_ID;
    }

    public void setDatabase_ID(String database_ID) {
        this.database_ID = database_ID;
    }
}
