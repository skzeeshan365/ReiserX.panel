package com.reiserx.myapplication24.Utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reiserx.myapplication24.Activities.FileViewers.PdfViewerActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class downloadFiles {
    File outputFile;
    Context context;

    public downloadFiles(String Path, Context context) {
        String path = Environment.getExternalStorageDirectory().toString() + "/".concat(Path);
        outputFile = new File(path);
        this.context = context;
    }

    public void download(String url, String Filename) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
                try {
                    Log.d("hgvhbjhj", "downloading...");
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();
                } catch(FileNotFoundException e) {
                    return; // swallow a 404
            } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("hgvhbjhj", e.toString());
                }
            handler.post(() -> {
                if (outputFile.exists()) {
                    Intent intent = new Intent(context, PdfViewerActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("name", Filename);
                    context.startActivity(intent);
                }
            });
        });
    }
}