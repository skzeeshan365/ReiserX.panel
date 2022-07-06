package com.reiserx.myapplication24.Activities.Logs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Others.errorLogsAdapter;
import com.reiserx.myapplication24.Models.exceptionUpload;
import com.reiserx.myapplication24.databinding.ActivityErrorLogsBinding;

import java.util.ArrayList;

public class ErrorLogs extends AppCompatActivity {

    ActivityErrorLogsBinding binding;

    ArrayList<exceptionUpload> data;
    errorLogsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityErrorLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Error logs");

        String UserID = getIntent().getStringExtra("UserID");

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new errorLogsAdapter(this, data);
        binding.rec.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Main").child(UserID).child("Errors").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        exceptionUpload exceptionUpload = snapshot1.getValue(exceptionUpload.class);
                        data.add(exceptionUpload);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}