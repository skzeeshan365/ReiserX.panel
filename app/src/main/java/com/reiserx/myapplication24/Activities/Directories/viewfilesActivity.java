package com.reiserx.myapplication24.Activities.Directories;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Directories.viewFilesAdapter;
import com.reiserx.myapplication24.Models.FileUpload;
import com.reiserx.myapplication24.databinding.ActivityViewfilesBinding;

import java.util.ArrayList;
import java.util.Locale;

public class viewfilesActivity extends AppCompatActivity {

    ActivityViewfilesBinding binding;

    FirebaseDatabase database;

    ArrayList<FileUpload> data;
    viewFilesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();

        String UserID = getIntent().getStringExtra("UserID");
        String getPath = getIntent().getStringExtra("Path");
        String message = getIntent().getStringExtra("Message");
        String path = getPath.replace(".", "");

        Log.d("htyftb", getPath);
        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new viewFilesAdapter(this, data, UserID, getPath, message, findViewById(android.R.id.content));
        binding.rec.setAdapter(adapter);

        if (message.equals("Videos")) {
            database.getReference("Main").child(UserID).child("Upload").child("Videos").child(path.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    data.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        FileUpload fileUpload = snapshot1.getValue(FileUpload.class);
                        fileUpload.setId(snapshot1.getKey());
                        data.add(fileUpload);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (message.equals("Audios")){
            database.getReference("Main").child(UserID).child("Upload").child("Audios").child(path.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    data.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        FileUpload fileUpload = snapshot1.getValue(FileUpload.class);
                        fileUpload.setId(snapshot1.getKey());
                        data.add(fileUpload);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (message.equals("PDF")){
            database.getReference("Main").child(UserID).child("Upload").child("PDF").child(path.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    data.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        FileUpload fileUpload = snapshot1.getValue(FileUpload.class);
                        fileUpload.setId(snapshot1.getKey());
                        data.add(fileUpload);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}