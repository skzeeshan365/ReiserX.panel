package com.reiserx.myapplication24.Activities.FileViewers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.databinding.ActivityTextViewerBinding;

public class TextViewerActivity extends AppCompatActivity {

    ActivityTextViewerBinding binding;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        String data = getIntent().getStringExtra("data");
        long timestamp = getIntent().getLongExtra("timestamp", 0);
        int requestCode = getIntent().getIntExtra("requestCode", 0);
        String title = getIntent().getStringExtra("name");
        setTitle(title);

        if (requestCode == 2) {
            binding.agreeButton.setVisibility(View.GONE);
        }
        String data1 = data+"\n\nModified "+ TimeAgo.using(timestamp);
        binding.textView21.setText(data1);
    }
}