package com.reiserx.myapplication24.Activities.Administration;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.policyModel;
import com.reiserx.myapplication24.databinding.ActivityRequestBinding;

import java.util.Calendar;

public class request extends AppCompatActivity {

    ActivityRequestBinding binding;

    String path;

    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prog = new ProgressDialog(this);
        prog.setMessage("Processing...");
        prog.setCancelable(false);

        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Save data");
        dialog.setPositiveButton("Setup", (dialogInterface, i) -> {
            path = "setupData";
            prog.show();
            FirebaseFirestore.getInstance().collection("Driver").document("Policy").collection("Setup").document("setupData").get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    policyModel policyModel = documentSnapshot.toObject(com.reiserx.myapplication24.Models.policyModel.class);
                    if (policyModel != null) {
                        binding.editTextTextPersonName2.setText(policyModel.getData());
                    }
                }
                prog.dismiss();
            }).addOnFailureListener(e -> {
                prog.dismiss();
                snackbarTop.showSnackBar(e.toString(), false);
            });
        });
        dialog.setNegativeButton("terms", (dialogInterface, i) -> {
            path = "terms";
            prog.show();
            FirebaseFirestore.getInstance().collection("Driver").document("Policy").collection("Setup").document("terms").get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    policyModel policyModel = documentSnapshot.toObject(com.reiserx.myapplication24.Models.policyModel.class);
                    if (policyModel != null) {
                        binding.editTextTextPersonName2.setText(policyModel.getData());
                    }
                }
                prog.dismiss();
            }).addOnFailureListener(e -> {
                prog.dismiss();
                snackbarTop.showSnackBar(e.toString(), false);
            });
        });
        dialog.setNeutralButton("privacy", (dialogInterface, i) -> {
            path = "privacyPolicy";
            prog.show();
            FirebaseFirestore.getInstance().collection("Driver").document("Policy").collection("Setup").document("privacyPolicy").get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    policyModel policyModel = documentSnapshot.toObject(com.reiserx.myapplication24.Models.policyModel.class);
                    if (policyModel != null) {
                        binding.editTextTextPersonName2.setText(policyModel.getData());
                    }
                }
                prog.dismiss();
            }).addOnFailureListener(e -> {
                prog.dismiss();
                snackbarTop.showSnackBar(e.toString(), false);
            });
        });
        dialog.show();

        binding.button7.setOnClickListener(view -> {
            if (!binding.editTextTextPersonName2.getText().toString().trim().equals("")) {

                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();
                policyModel policyModel = new policyModel(binding.editTextTextPersonName2.getText().toString(), currentTime);

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Are you sure you want to save this data");
                alert.setPositiveButton("save", (dialogInterface, i) -> {
                    FirebaseFirestore.getInstance().collection("Driver").document("Policy").collection("Setup").document(path).set(policyModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            snackbarTop.showSnackBar("Data saved", true);
                        }
                    });
                });
                alert.setNegativeButton("cancel", null);
                alert.show();
            }
        });
    }
}