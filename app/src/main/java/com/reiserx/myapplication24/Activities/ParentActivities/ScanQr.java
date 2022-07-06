package com.reiserx.myapplication24.Activities.ParentActivities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Models.Users;
import com.reiserx.myapplication24.databinding.ActivityScanQrBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public class ScanQr extends AppCompatActivity {

    ActivityScanQrBinding binding;
    private CodeScanner mCodeScanner;

    ProgressDialog dialog;

    String TAG = "ijfvffnvenen";
    private String uid, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase mdb = FirebaseDatabase.getInstance();

        String UserID = getIntent().getStringExtra("UserID");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Processing");

        if (Objects.requireNonNull(getSupportActionBar()).isShowing()) {
            getSupportActionBar().hide();
        }

        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            dialog.show();
            try {
                JSONObject jsonObj = new JSONObject(result.getText());
                data = jsonObj.getString("data");
                uid = jsonObj.getString("uid");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, Arrays.toString(e.getStackTrace()));
            }

            mdb.getReference("Userdata").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        dialog.setMessage("Adding device");
                        Users users = snapshot.getValue(Users.class);
                        if (users != null)
                            if (String.valueOf(users.timestamp).equals(Decrypt(data, users.key))) {
                                Users deviceList = new Users(Objects.requireNonNull(users).uid, users.name, "null", users.timestamp);
                                mdb.getReference("Administration").child("TargetDevices").child(UserID).child(users.uid)
                                        .setValue(deviceList)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(ScanQr.this, "Device added successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            finish();
                                        });

                            } else {
                                dialog.dismiss();
                                Toast.makeText(ScanQr.this, "Access denied", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(ScanQr.this, "Device does not exists", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    finish();
                }
            });
        }));
        binding.scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private String Decrypt(final String data, final String Decryptionkey) {
        Log.d(TAG, data);
        Log.d(TAG, Decryptionkey);
        String decryptedString;
        try {
            javax.crypto.spec.SecretKeySpec key = (javax.crypto.spec.SecretKeySpec) generateKey(Decryptionkey);
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            byte[] decode = android.util.Base64.decode(data, android.util.Base64.DEFAULT);
            byte[] decval = c.doFinal(decode);
            decryptedString = new String(decval);
        } catch (Exception ex) {
            decryptedString = null;
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
        return decryptedString;
    }

    private javax.crypto.SecretKey generateKey(String pwd) throws Exception {
        final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] b = pwd.getBytes("UTF-8");
        digest.update(b, 0, b.length);
        byte[] key = digest.digest();
        javax.crypto.spec.SecretKeySpec sec = new javax.crypto.spec.SecretKeySpec(key, "AES");
        return sec;
    }

}