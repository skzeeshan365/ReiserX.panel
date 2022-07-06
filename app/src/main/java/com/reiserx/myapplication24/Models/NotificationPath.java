package com.reiserx.myapplication24.Models;

public class NotificationPath {
    String name, label;

    public NotificationPath(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public NotificationPath(String name) {
        this.name = name;
    }

    public NotificationPath() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
