package com.reiserx.myapplication24.Utilities;

import android.os.Environment;

import java.io.File;

public class CONSTANTS {
    public static String fileStorage () {
        return Environment.getExternalStorageDirectory()+ "/ReiserX";
    }

    public static String imageDirectory () {
        return Environment.getExternalStorageDirectory()+ "/ReiserX/Images";
    }

    public static String pdfDirectory () {
        return Environment.getExternalStorageDirectory()+"/ReiserX/PDF files";
    }

    public static String audioVideoDirectory () {
        return Environment.getExternalStorageDirectory()+"/ReiserX/Audio Video";
    }

    public static String textFiles () {
        return Environment.getExternalStorageDirectory()+"/ReiserX/Text files";
    }

}
