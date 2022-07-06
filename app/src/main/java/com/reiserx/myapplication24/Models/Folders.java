package com.reiserx.myapplication24.Models;

public class Folders {
    public String folder;

    public Folders() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Folders(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
