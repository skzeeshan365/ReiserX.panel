package com.reiserx.myapplication24.Models;

public class contacts_lists {
    public String name;
    public String phoneNumber;

    public contacts_lists() {
    }

    public contacts_lists(String name, String phoneNumber ) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
