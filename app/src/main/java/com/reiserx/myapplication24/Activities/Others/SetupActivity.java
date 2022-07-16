package com.reiserx.myapplication24.Activities.Others;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.myapplication24.Adapters.Others.setupAdapter;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Models.operationsModel;
import com.reiserx.myapplication24.databinding.ActivitySetupBinding;

import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    ActivitySetupBinding binding;

    ArrayList<operationsModel> data;
    setupAdapter adapter;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        i = getIntent().getIntExtra("requestCode", 0);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Notice");
        alert.setMessage("Before using this service please read our privacy policy and terms of use/terms of service");
        alert.setPositiveButton("ok", null);
        alert.show();

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new setupAdapter(this, data);
        binding.rec.setAdapter(adapter);

        data.add(new operationsModel("Privacy policy", 0));
        data.add(new operationsModel("Terms of use", 1));
        data.add(new operationsModel("Open source license", 2));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (i == 1) {
            finishAffinity();
        } else finish();
        super.onBackPressed();
    }
}