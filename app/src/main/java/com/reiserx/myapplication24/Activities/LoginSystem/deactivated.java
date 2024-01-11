package com.reiserx.myapplication24.Activities.LoginSystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityDeactivatedBinding;

import java.util.Objects;

public class deactivated extends AppCompatActivity {

    ActivityDeactivatedBinding binding;

    String TAG = "jifiufh";

    String title;

    FirebaseUser user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeactivatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mesg.setMovementMethod(LinkMovementMethod.getInstance());

        title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        binding.titles.setText(title);
        binding.mesg.setText(message);

        if (Objects.requireNonNull(getSupportActionBar()).isShowing()) {
            getSupportActionBar().hide();
        }

        process();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        process();
    }

    public void process() {
        if (title.equals("BANNED!")) {
            binding.button2.setText("close");
            binding.button2.setOnClickListener(v -> finishAffinity());
            binding.textView6.setText("support@reiserx.com");
        } else if (title.equals("ACCESS DENIED!")) {

            SpannableString spannableString = new SpannableString(getString(R.string.denied_text));
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    // Handle the click action here, e.g., open the URL in a browser
                    Uri uri = Uri.parse("https://reiserx.com/reiserx-remote/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            };

// Set the clickable span for the link
            int startIndex = getString(R.string.denied_text).indexOf("website");
            int endIndex = startIndex + "website".length();
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set the spannable string to the TextView
            binding.mesg.setText(spannableString);
            binding.mesg.setMovementMethod(LinkMovementMethod.getInstance());

            binding.button2.setText("logout");
            binding.button2.setOnClickListener(v -> {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                    finish();
                }
            });
        } else {

            SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

            Task<Void> userss = FirebaseAuth.getInstance().getCurrentUser().reload();
            userss.addOnSuccessListener(unused1 -> {

                user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    binding.textView6.setText(user.getEmail());
                    if (user.isEmailVerified()) {
                        user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                            Log.d(TAG, String.valueOf(getTokenResult));
                        }).addOnFailureListener(e -> {
                            Log.d(TAG, String.valueOf(e.toString()));
                        });
                        binding.button2.setEnabled(true);
                        binding.button2.setText("next");
                        binding.mesg.setText("Your email has been verified");

                        binding.button2.setOnClickListener(view -> {

                                Intent i = new Intent(deactivated.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("UserID", user.getUid());
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                startActivity(i);
                                finish();
                        });
                    } else {
                        binding.button2.setText("VERIFY");

                        binding.button2.setOnClickListener(view -> {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(deactivated.this, task -> {
                                        // Re-enable button
                                        binding.button2.setEnabled(true);

                                        if (task.isSuccessful()) {
                                            binding.button2.setEnabled(true);
                                            AlertDialog.Builder alert = new AlertDialog.Builder(deactivated.this);
                                            alert.setTitle("Email sent");
                                            alert.setMessage("We have sent a verification email to your registered email address please verify it from there");
                                            alert.setPositiveButton("ok", (dialogInterface, i) -> {
                                                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                                startActivity(intent);
                                            });
                                            alert.show();
                                        } else {
                                            Log.e(TAG, "sendEmailVerification", task.getException());
                                            snackbarTop.showSnackBar("Failed to send verification email, "+task.getException(), false);
                                        }
                                    });
                        });
                    }
                }
            });

        }
    }
}