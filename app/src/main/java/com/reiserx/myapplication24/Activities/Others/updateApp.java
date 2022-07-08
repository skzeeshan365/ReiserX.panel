package com.reiserx.myapplication24.Activities.Others;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.BuildConfig;
import com.reiserx.myapplication24.Methods.fileDownloader;
import com.reiserx.myapplication24.Models.updateAppss;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityUpdateAppBinding;

import java.util.Objects;

public class updateApp extends AppCompatActivity {

    ActivityUpdateAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        
        database.getReference("Administration").child("App").child("Driver").child("Updates").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateAppss updateAp = snapshot.getValue(updateAppss.class);
                    if (!Objects.requireNonNull(updateAp).version.equals(BuildConfig.VERSION_NAME)) {
                        binding.versionTxt.setText("v"+updateAp.version+" update available");
                        binding.versionTxt.setTextColor(getResources().getColor(R.color.green));
                        binding.downloadDriverapp.setText("Install");
                        binding.downloadDriverapp.setEnabled(true);
                    } else {
                        binding.versionTxt.setText("v"+updateAp.version);
                        binding.downloadDriverapp.setText("Installed");
                        binding.downloadDriverapp.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SharedPreferences save = getSharedPreferences("Update", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        database.getReference("Administration").child("App").child("Target").child("Updates").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateAppss updateAp = snapshot.getValue(updateAppss.class);
                    if (save.getString("version", "").equals("")) {
                        binding.targetVersionTxt.setText("v"+ Objects.requireNonNull(updateAp).version);
                    } else {
                        String s = save.getString("version", "");
                        String data = s.substring(1,4);
                        if (data.equals(Objects.requireNonNull(updateAp).version)) {
                            binding.targetVersionTxt.setText("v"+updateAp.version);
                        } else {
                            binding.targetVersionTxt.setTextColor(getResources().getColor(R.color.green));
                            binding.targetVersionTxt.setText("v"+updateAp.version+" update available");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference("Administration").child("App").child("Target").child("Updates").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateAppss updateAp = snapshot.getValue(updateAppss.class);
                    if (save.getString("version", "").equals("")) {
                        binding.targetVersionTxt.setText("v"+ Objects.requireNonNull(updateAp).version);
                    } else {
                        String s = save.getString("version", "");
                        String data = s.substring(1,4);
                        if (data.equals(Objects.requireNonNull(updateAp).version)) {
                            binding.targetVersionTxt.setText("v"+updateAp.version);
                        } else {
                            binding.targetVersionTxt.setTextColor(getResources().getColor(R.color.green));
                            binding.targetVersionTxt.setText("v"+updateAp.version+" update available");
                        }
                    }
                    binding.compatibiblity.setText("Compatible with ReiserX v"+updateAp.version);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.downloadDriverapp.setOnClickListener(view -> {

            StorageReference reference = storage.getReference().child("App").child("Driver").child("app-release.apk");
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                Log.d("jrnejrngje", url);
                fileDownloader asyncTask = new fileDownloader(this);
                asyncTask.execute(url, R.string.app_name+".apk");
            });
        });

        binding.downloadTargetapp.setOnClickListener(view -> {
            StorageReference reference = storage.getReference().child("App").child("Target").child("app-release.apk");
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                myEdit.putString("version", binding.targetVersionTxt.getText().toString());
                myEdit.apply();
                fileDownloader asyncTask = new fileDownloader(this);
                asyncTask.execute(url, R.string.targetApp+".apk");
            });
        });

        binding.button6.setOnClickListener(view -> {
            Intent intent = new Intent(this, SetupActivity.class);
            intent.putExtra("requestCode", 0);
            startActivity(intent);
        });
    }
}