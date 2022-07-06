package com.reiserx.myapplication24.Activities.Directories;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.databinding.ActivityCheckDownloadBinding;

import java.util.Locale;

public class checkDownload extends AppCompatActivity {

    ActivityCheckDownloadBinding binding;
    ProgressDialog prog;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prog = new ProgressDialog(this);
        prog.setMessage("Getting files...");
        prog.setCancelable(false);
        prog.show();

        String UserID = getIntent().getStringExtra("UserID");
        String Path = getIntent().getStringExtra("Path");
        String fol = Path.replace(".", "");

        binding.images.setVisibility(View.GONE);
        binding.videos.setVisibility(View.GONE);
        binding.audios.setVisibility(View.GONE);
        binding.pdfBtn.setVisibility(View.GONE);

        database.getReference("Main").child(UserID).child("Upload").child("Images").child(fol.toLowerCase(Locale.ROOT)).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.images.setVisibility(View.VISIBLE);
                    binding.images.setOnClickListener(v -> {
                        Intent intent = new Intent(checkDownload.this, MainActivity.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Path", Path);
                        startActivity(intent);
                    });
                } else {
                    binding.images.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("Main").child(UserID).child("Upload").child("Videos").child(fol.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    binding.videos.setVisibility(View.VISIBLE);

                    binding.videos.setOnClickListener(v -> {
                        Intent intent = new Intent(checkDownload.this, viewfilesActivity.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Message", "Videos");
                        intent.putExtra("Path", Path);
                        startActivity(intent);
                    });
                } else {
                    binding.videos.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("Main").child(UserID).child("Upload").child("Audios").child(fol.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.audios.setVisibility(View.VISIBLE);
                    binding.audios.setOnClickListener(v -> {
                        Intent intent = new Intent(checkDownload.this, viewfilesActivity.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Message", "Audios");
                        intent.putExtra("Path", Path);
                        startActivity(intent);
                    });
                } else {
                    binding.audios.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference("Main").child(UserID).child("Upload").child("PDF").child(fol.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.pdfBtn.setVisibility(View.VISIBLE);
                    binding.pdfBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(checkDownload.this, viewfilesActivity.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Message", "PDF");
                        intent.putExtra("Path", Path);
                        startActivity(intent);
                    });
                } else {
                    binding.pdfBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        prog.dismiss();
    }
}