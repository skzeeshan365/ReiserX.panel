package com.reiserx.myapplication24.Classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reiserx.myapplication24.R;

import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Notify {

    String TAG = "jkfhsb";

    public void notif(String fcmToken, Context context, int requestCode, ProgressDialog prog) {
        prog.setMessage("Sending request...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "jnfijfnfjsn");
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject dataJson = new JSONObject();
                dataJson.put("title", "title");
                dataJson.put("content", "msg");
                dataJson.put("id", String.valueOf(getRandom(0, 100)));
                dataJson.put("requestCode", requestCode);
                json.put("data", dataJson);
                json.put("to", fcmToken);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                Request request = new Request.Builder()
                        .header("Authorization", context.getString(R.string.serverKey))
                        .url(context.getString(R.string.fcm))
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String res = Objects.requireNonNull(response.body()).string();
                prog.setMessage("Request sent");
                prog.dismiss();
                Log.d(TAG, res);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
            }
            handler.post(() -> {
        });
    });
}
    public void notif(String fcmToken, Context context, int requestCode) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "jnfijfnfjsn");
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject dataJson = new JSONObject();
                dataJson.put("title", "title");
                dataJson.put("content", "msg");
                dataJson.put("id", String.valueOf(getRandom(0, 100)));
                dataJson.put("requestCode", requestCode);
                json.put("data", dataJson);
                json.put("to", fcmToken);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                Request request = new Request.Builder()
                        .header("Authorization", context.getString(R.string.serverKey))
                        .url(context.getString(R.string.fcm))
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String res = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, res);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
            }
            handler.post(() -> {
            });
        });
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
