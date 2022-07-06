package com.reiserx.myapplication24.Activities.LoginSystem;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reiserx.myapplication24.databinding.ActivityDeactivatedBinding;

import java.util.Objects;

public class deactivated extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDeactivatedBinding binding = ActivityDeactivatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        binding.titles.setText(title);
        binding.mesg.setText(message);

        if (Objects.requireNonNull(getSupportActionBar()).isShowing()) {
            getSupportActionBar().hide();
        }

        if (title.equals("BANNED!")) {
            binding.button2.setText("close");
            binding.button2.setOnClickListener(v -> finishAffinity());
        } else {
            binding.button2.setText("logout");
            binding.button2.setOnClickListener(v -> {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}