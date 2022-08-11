package com.reiserx.myapplication24.Activities.LoginSystem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    FirebaseAuth mAuth;

    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.resetPassBtn.setEnabled(false);
        binding.deleteAccBtn.setEnabled(false);
        binding.logoutBtn.setEnabled(false);

        prog = new ProgressDialog(this);
        prog.setMessage("Processing...");

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        mAuth =  FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.userId.setText(currentUser.getUid());
            binding.emailId.setText(currentUser.getEmail());

            binding.resetPassBtn.setEnabled(true);
            binding.deleteAccBtn.setEnabled(true);
            binding.logoutBtn.setEnabled(true);

            binding.logoutBtn.setOnClickListener(view -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Are you sure want to logout of this account");
                alert.setTitle("LOGOUT");
                alert.setPositiveButton("logout", (dialogInterface, i) -> {
                    mAuth.signOut();
                    finishAffinity();
                });
                alert.setNegativeButton("cancel", null);
                alert.show();
            });

            binding.resetPassBtn.setOnClickListener(view -> {
                resetPassword();
            });

            binding.deleteAccBtn.setOnClickListener(view -> {
                try {
                    displayDialog("Delete account", "Are your sure you want to delete your account, there's no going back!", currentUser);
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void displayDialog (String title, String message, FirebaseUser currentUser) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("delete", (dialogInterface, i) -> {
            login(currentUser);
        });
        alert.setNegativeButton("cancel", null);
        alert.show();
    }

    public void resetPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.custum_2_edittext_dialog, null);
        final EditText input = mView.findViewById(R.id.title_text);
        final EditText input2 = mView.findViewById(R.id.msg_text);
        final CheckBox notifyCheckBox = mView.findViewById(R.id.checkBox);
        final CheckBox forgroundCheckBox = mView.findViewById(R.id.checkBox2);
        final CheckBox job = mView.findViewById(R.id.job);

        alert.setMessage("Enter your email address to reset your password");
        alert.setTitle("Reset password");
        alert.setView(mView);

        input.setHint("Enter your email");

        input2.setVisibility(View.GONE);
        notifyCheckBox.setVisibility(View.GONE);
        forgroundCheckBox.setVisibility(View.GONE);
        job.setVisibility(View.GONE);

        alert.setPositiveButton("reset", (dialog, whichButton) -> {

            String title = input.getText().toString();
            Snackbar.make(findViewById(android.R.id.content), "Resetting password", Snackbar.LENGTH_LONG).show();

            if (!title.trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Password reset");
                builder.setPositiveButton("ok", null);
                mAuth.sendPasswordResetEmail(title.trim()).addOnSuccessListener(unused -> {
                    builder.setMessage("We have a password reset link to your email address, open your email app and reset your password");
                    builder.show();
                    Snackbar.make(findViewById(android.R.id.content), "Password reset complete", Snackbar.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    builder.setMessage("Password reset failed: " + e);
                    builder.show();
                    Snackbar.make(findViewById(android.R.id.content), "Password reset failed", Snackbar.LENGTH_LONG).show();

                });
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Please enter your email", Snackbar.LENGTH_LONG).show();
            }

        });

        alert.setNegativeButton("CANCEL", (dialog, whichButton) -> {
        });

        alert.show();
    }

    private void login(FirebaseUser currentUser) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.simple_edittext_dialog, null);
        final EditText input = mView.findViewById(R.id.editTextNumber);

        alert.setMessage("Please enter your password to continue");
        alert.setTitle("Login");
        alert.setView(mView);

        input.setHint("Enter your password");

        alert.setPositiveButton("Login", (dialog, whichButton) -> {
            //What ever you want to do with the value
            if (!input.getText().toString().equals("")) {
                String data = input.getText().toString();
                prog.show();
                mAuth.signInWithEmailAndPassword(currentUser.getEmail(), data).addOnSuccessListener(authResult -> {
                    FirebaseDatabase.getInstance().getReference().child("Administration").child("Account").child("DeleteRequest").child(currentUser.getUid()).setValue(currentUser.getUid());
                    FirebaseDatabase.getInstance().getReference("Administration").child("UserData").child(currentUser.getEmail().replaceAll("\\.", "")).removeValue();
                    currentUser.delete().addOnSuccessListener(unused -> {
                        mAuth.getCurrentUser().getIdToken(true);
                        mAuth.signOut();
                        prog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    }).addOnFailureListener(e -> {
                        prog.dismiss();
                        Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    });
                });

            } else {
                prog.dismiss();
                Toast.makeText(ProfileActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("cancel", (dialog, whichButton) -> {

        });

        alert.show();
    }
}