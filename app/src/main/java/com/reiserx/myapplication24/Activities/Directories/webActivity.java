package com.reiserx.myapplication24.Activities.Directories;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Utilities.CONSTANTS;
import com.reiserx.myapplication24.databinding.ActivityWebBinding;

public class webActivity extends AppCompatActivity {

    ActivityWebBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        String url = getIntent().getStringExtra("url");
        String name = getIntent().getStringExtra("name");
        binding.web.loadUrl(url);
        binding.web.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url1));

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
            request.setDestinationInExternalPublicDir(CONSTANTS.audioVideoDirectory(), name);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                    Toast.LENGTH_LONG).show();

        });

        binding.progressBar.setProgress(binding.web.getProgress());
    }
}