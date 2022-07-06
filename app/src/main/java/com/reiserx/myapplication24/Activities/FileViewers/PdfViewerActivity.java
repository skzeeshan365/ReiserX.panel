package com.reiserx.myapplication24.Activities.FileViewers;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.Utilities.CONSTANTS;
import com.reiserx.myapplication24.databinding.ActivityPdfViewerBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PdfViewerActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    ActivityPdfViewerBinding binding;

    File file;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.pdfView.setBackgroundColor(Color.BLACK);
        String Url = getIntent().getStringExtra("url");
        fileName = getIntent().getStringExtra("name");

        setTitle(fileName);

        file = new File(CONSTANTS.pdfDirectory());
        if (!file.exists()) file.mkdir();
        file = new File(CONSTANTS.pdfDirectory(), fileName);

        if (file.exists()) {
            binding.pdfView.fromUri(Uri.fromFile(file))
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(15)
                    .load();
            binding.pdfView.setMinZoom(0.5f);
            binding.pdfView.setMidZoom(2);
            binding.pdfView.setMaxZoom(8);
        } else {
            fileDownloader fileDownloader = new fileDownloader(this, Url, fileName);
            fileDownloader.execute();
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        setTitle(fileName+" "+nbPages);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onPageError(int page, Throwable t) {
        Toast.makeText(this, "Error at "+page+" "+t.toString(), Toast.LENGTH_SHORT).show();
    }

    public class fileDownloader extends AsyncTask<String, Long, Boolean> {

        ProgressDialog prog;
        File mediaFile;
        Context context;
        String Url, fileName;

        public fileDownloader(Context context, String Url, String fileName) {
            this.context = context;
            this.Url = Url;
            this.fileName = fileName;
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
            Call call = client.newCall(new Request.Builder().url(Url).get().build());

            try {
                Response response = call.execute();
                if (response.code() == 200 || response.code() == 201) {

                    InputStream inputStream = null;
                    try {
                        inputStream = response.body().byteStream();

                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = response.body().contentLength();
                        mediaFile = new File(CONSTANTS.pdfDirectory(), fileName);
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
                Log.d("jsnfsb", e.toString());
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
                binding.pdfView.fromUri(Uri.fromFile(file))
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(PdfViewerActivity.this))
                        .spacing(10)
                        .load();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.single_menu_item) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(PdfViewerActivity.this, getApplicationContext().getPackageName() + ".provider", file), "application/pdf");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No Pdf reader found to open this file", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}