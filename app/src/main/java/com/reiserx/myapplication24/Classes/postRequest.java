package com.reiserx.myapplication24.Classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.reiserx.myapplication24.R;

import org.json.JSONArray;
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

public class postRequest {
    String fcmToken = null;
    String title = null;
    String msg = null;
    int id;
    ProgressDialog prog;
    Context context;
    int requestCode;

    String finalResponse;
    String res;
    String multicast_id;
    String success;
    String failure;
    String canonical_ids;
    String message_id = null;

    String TAG = "hghuhiuhuihiu";

    public postRequest(String fcmToken, String title, String msg, ProgressDialog prog, int requestCode, Context context) {
        this.fcmToken = fcmToken;
        this.title = title;
        this.msg = msg;
        this.prog = prog;
        this.context = context;
        this.requestCode = requestCode;
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public void notif() {
        prog.setMessage("Sending request...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "jnfijfnfjsn");
        executor.execute(() -> {
            try {
                id = getRandom(0, 1000);
                Log.d("checkingboool", String.valueOf(requestCode));
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject dataJson = new JSONObject();
                dataJson.put("title", title);
                dataJson.put("content", msg);
                dataJson.put("id", String.valueOf(id));
                dataJson.put("requestCode", String.valueOf(requestCode));
                json.put("data", dataJson);
                json.put("to", fcmToken);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                Request request = new Request.Builder()
                        .header("Authorization", context.getString(R.string.serverKey))
                        .url(context.getString(R.string.fcm))
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                res = Objects.requireNonNull(response.body()).string();
                Log.d("getJson", res);
                JSONObject jObject = new JSONObject(res);
                multicast_id = jObject.getString("multicast_id");
                success = jObject.getString("success");
                failure = jObject.getString("failure");
                canonical_ids = jObject.getString("canonical_ids");
                JSONArray jArray = jObject.getJSONArray("results");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject childrenObject = jArray.getJSONObject(i);
                    message_id = childrenObject.getString("message_id");
                }
                prog.dismiss();
                if (requestCode == 2) {
                    finalResponse = "Multicast Id: " + multicast_id + "\n" + "Success: " + success + "\n" + "Failure: " + failure + "\n" + "Canonical Ids: " + canonical_ids + "\n" + "Message Id: " + message_id + "\n\n" + "Notification Id: " + id;
                } else {
                    finalResponse = "Multicast Id: " + multicast_id + "\n" + "Success: " + success + "\n" + "Failure: " + failure + "\n" + "Canonical Ids: " + canonical_ids + "\n" + "Message Id: " + message_id + "\n\n" + "Request Id: " + id;
                }
            } catch (Exception e) {
                prog.dismiss();
                Log.i("msgError", e.getMessage());
                finalResponse = e.getMessage();
            }
            prog.setMessage("Request sent");
            prog.dismiss();
            handler.post(() -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Response");
                dialog.setMessage(finalResponse);
                dialog.setPositiveButton("CLOSE", (dialog12, which) -> {
                });
                dialog.setCancelable(false);
                dialog.show();
            });
        });
    }
    public void notifyBackground() {
        prog.setMessage("Sending request...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "jnfijfnfjsn");
        executor.execute(() -> {
            try {
                id = getRandom(0, 1000);
                Log.d("checkingboool", String.valueOf(requestCode));
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject dataJson = new JSONObject();
                dataJson.put("title", title);
                dataJson.put("content", msg);
                dataJson.put("id", String.valueOf(id));
                dataJson.put("requestCode", String.valueOf(requestCode));
                json.put("data", dataJson);
                json.put("to", fcmToken);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                Request request = new Request.Builder()
                        .header("Authorization", context.getString(R.string.serverKey))
                        .url(context.getString(R.string.fcm))
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                res = Objects.requireNonNull(response.body()).string();
                Log.d("getJson", res);
                JSONObject jObject = new JSONObject(res);
                multicast_id = jObject.getString("multicast_id");
                success = jObject.getString("success");
                failure = jObject.getString("failure");
                canonical_ids = jObject.getString("canonical_ids");
                JSONArray jArray = jObject.getJSONArray("results");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject childrenObject = jArray.getJSONObject(i);
                    message_id = childrenObject.getString("message_id");
                }
                prog.dismiss();
                if (requestCode == 2) {
                    finalResponse = "Multicast Id: " + multicast_id + "\n" + "Success: " + success + "\n" + "Failure: " + failure + "\n" + "Canonical Ids: " + canonical_ids + "\n" + "Message Id: " + message_id + "\n\n" + "Notification Id: " + id;
                } else {
                    finalResponse = "Multicast Id: " + multicast_id + "\n" + "Success: " + success + "\n" + "Failure: " + failure + "\n" + "Canonical Ids: " + canonical_ids + "\n" + "Message Id: " + message_id + "\n\n" + "Request Id: " + id;
                }
            } catch (Exception e) {
                prog.dismiss();
                Log.i("msgError", e.getMessage());
                finalResponse = e.getMessage();
            }
            prog.setMessage("Request sent");
            prog.dismiss();
        });
    }
}