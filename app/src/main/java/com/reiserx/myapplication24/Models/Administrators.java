package com.reiserx.myapplication24.Models;

public class Administrators {

    public String uid, role;
    boolean fileUploadAccess;

    public Administrators() {
    }

    public Administrators(String uid, String role, boolean fileUploadAccess) {
        this.uid = uid;
        this.role = role;
        this.fileUploadAccess = fileUploadAccess;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getFileUploadAccess() {
        return fileUploadAccess;
    }

    public void setFileUploadAccess(boolean fileUploadAccess) {
        this.fileUploadAccess = fileUploadAccess;
    }
}
