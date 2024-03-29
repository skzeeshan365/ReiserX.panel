package com.reiserx.myapplication24.Activities.Operations;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.myapplication24.Adapters.Others.opeationsAdapter;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Models.operationsModel;
import com.reiserx.myapplication24.databinding.ActivityOperationListBinding;

import java.util.ArrayList;

public class OperationListActivity extends AppCompatActivity {

    ActivityOperationListBinding binding;

    ArrayList<operationsModel> data;
    opeationsAdapter adapter;
    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOperationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String UserID = getIntent().getStringExtra("UserID");
        setTitle("Operations");

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new opeationsAdapter(this, data, UserID, prog);
        binding.rec.setAdapter(adapter);

        data.add(new operationsModel("Directorys", 0));
        data.add(new operationsModel("Contacts", 1));
        data.add(new operationsModel("Call Logs", 2));
        data.add(new operationsModel("Location", 3));
        data.add(new operationsModel("Notification history", 4));
        data.add(new operationsModel("Screenshot", 5));
        data.add(new operationsModel("Record audio", 6));

        data.add(new operationsModel("ads", 7));

        data.add(new operationsModel("Camera capture", 8));
        data.add(new operationsModel("Clear file preferences", 9));

        data.add(new operationsModel("Launch services", 10));
        data.add(new operationsModel("App list", 11));
        data.add(new operationsModel("Block Apps", 12));
        data.add(new operationsModel("Usage stats data", 13));
        data.add(new operationsModel("Python", 14));

        data.add(new operationsModel("ads", 15));

        data.add(new operationsModel("Device info", 16));

        data.add(new operationsModel("App Logs", 17));
        data.add(new operationsModel("Error Logs", 18));

        adapter.notifyDataSetChanged();
    }
}