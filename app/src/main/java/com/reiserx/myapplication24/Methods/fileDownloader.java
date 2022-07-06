package com.reiserx.myapplication24.Methods;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.reiserx.myapplication24.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class fileDownloader extends AsyncTask<String, Long, Boolean> {

    ProgressDialog prog;
    File mediaFile;
    Context context;

    public fileDownloader(Context context) {
        this.context = context;
    }

    @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prog = new ProgressDialog(context);
            prog.setMessage("downloading...");
            prog.setCancelable(false);
        prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();
            String url = params[0];
            String name = params[1];
            Call call = client.newCall(new Request.Builder().url(url).get().build());

            try {
                Response response = call.execute();
                if (response.code() == 200 || response.code() == 201) {

                    InputStream inputStream = null;
                    try {
                        inputStream = response.body().byteStream();

                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = response.body().contentLength();
                        mediaFile = new File(Environment.getExternalStorageDirectory()+"/ReiserX", name);
                        OutputStream output = new FileOutputStream(mediaFile);

                        publishProgress(0L, target);
                        while (true) {
                            int readed = inputStream.read(buff);

                            if (readed == -1) {
                                break;
                            }
                            output.write(buff, 0, readed);
                            //write buff
                            downloaded += readed;
                            publishProgress(downloaded, target);
                            if (isCancelled()) {
                                return false;
                            }
                        }

                        output.flush();
                        output.close();

                        return downloaded == target;
                    } catch (IOException ignore) {
                        return false;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            prog.setProgress(values[0].intValue());
            prog.setMax(values[1].intValue());

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            prog.dismiss();

            if (mediaFile != null && mediaFile.exists()) {
                Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider",
                        mediaFile);

                if (mediaFile.getPath().contains(R.string.app_name+".apk")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                    Log.d("Downloadcode", mediaFile.getPath());
                } else if (mediaFile.getPath().contains(R.string.targetApp+".apk")) {

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("application/vnd.android.package-archive");
                    share.putExtra(Intent.EXTRA_STREAM, mediaFile);
                    context.startActivity(Intent.createChooser(share, "Share Via"));
                }
            }
        }
}
