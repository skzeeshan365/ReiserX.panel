package com.reiserx.myapplication24.Utilities;

import android.os.Environment;

public class CONSTANTS {

    public static String fileStorage () {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ReiserX";
    }

    public static String imageDirectory () {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ReiserX/Images";
    }

    public static String pdfDirectory () {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ReiserX/PDF files";
    }

    public static String audioVideoDirectory () {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/ReiserX/Audio Video";
    }

    public static String textFiles () {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/ReiserX/Text files";
    }

}
