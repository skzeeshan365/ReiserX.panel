package com.reiserx.myapplication24.Models;

public class Imagedata {
    public String bitmap, name, id;

    public Imagedata() {
    }

    public Imagedata(String bitmap) {

        this.bitmap = bitmap;
    }

    public String getBitmap() {

        return bitmap;
    }

    public void setBitmap(String bitmap) {

        this.bitmap = bitmap;
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
}
