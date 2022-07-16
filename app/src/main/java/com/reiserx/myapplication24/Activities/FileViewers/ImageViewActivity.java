package com.reiserx.myapplication24.Activities.FileViewers;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.Utilities.CONSTANTS;
import com.reiserx.myapplication24.databinding.ActivityImageViewBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class ImageViewActivity extends AppCompatActivity {

    ActivityImageViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        String Url = getIntent().getStringExtra("url");

        TouchImageView img = findViewById(R.id.imageView6);

        Glide.with(this).load(Url)
                .placeholder(R.drawable.image_placeholder)
                .into(img);
        binding.floatingActionButton.setOnClickListener(view -> {
            BitmapDrawable draw = (BitmapDrawable) binding.imageView6.getDrawable();
            SaveImage(draw.getBitmap());
        });
    }
    private void SaveImage(Bitmap finalBitmap) {


        File myDir = new File(CONSTANTS.imageDirectory());
        try {
            myDir.mkdirs();
        } catch (Exception e) {
            Log.d("jhdhfish", e.toString());
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
            snackbarTop.showSnackBar("Image saved at "+myDir, true);
            Log.d("jhdhfish", String.valueOf(myDir));

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("jhdhfish", e.toString());
        }
    }
}