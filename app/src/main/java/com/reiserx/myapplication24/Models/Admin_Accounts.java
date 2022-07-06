package com.reiserx.myapplication24.Models;

public class Admin_Accounts {

    public String uid, email;

    public Admin_Accounts() {
    }

    public Admin_Accounts(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
