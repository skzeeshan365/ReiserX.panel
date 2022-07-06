package com.reiserx.myapplication24.Activities.FileViewers;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.myapplication24.Methods.DesignLayout;
import com.reiserx.myapplication24.databinding.ActivityTextViewerBinding;

public class TextViewerActivity extends AppCompatActivity {

    ActivityTextViewerBinding binding;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String data = getIntent().getStringExtra("data");
        long timestamp = getIntent().getLongExtra("timestamp", 0);
        int requestCode = getIntent().getIntExtra("requestCode", 0);
        String title = getIntent().getStringExtra("name");
        setTitle(title);

        if (requestCode == 2) {
            binding.agreeButton.setVisibility(View.GONE);
        } else {
            SharedPreferences save = getSharedPreferences("policy", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = save.edit();

            binding.agreeButton.setVisibility(View.VISIBLE);
            DesignLayout designLayout = new DesignLayout();
            designLayout.Apply(binding.agreeButton, 0, 5, "#FF424242", true);
            binding.agreeButton.setText("Accept & agree");

            binding.agreeButton.setOnClickListener(view -> {
                myEdit.putBoolean("accepted", true);
                myEdit.apply();
                finish();
            });
        }
        String data1 = data+"\n\nModified "+ TimeAgo.using(timestamp);
        binding.textView21.setText(data1);
    }
}