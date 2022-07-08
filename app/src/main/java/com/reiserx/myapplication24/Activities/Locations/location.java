package com.reiserx.myapplication24.Activities.Locations;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.Notify;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.locationModel;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityLocationBinding;

import java.util.Objects;

public class location extends AppCompatActivity {

    private FirebaseDatabase mdb = FirebaseDatabase.getInstance();

    int requestCode = 0;

    ActivityLocationBinding binding;

    ProgressDialog prog;

    String UserID;

    SnackbarTop snackbarTop;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        setTitle("Location");

        updateFirst(UserID);

        mdb.getReference("Main").child(UserID).child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    locationModel loc = snapshot.getValue(locationModel.class);
                    binding.tvLatitude.setText(Objects.requireNonNull(loc).latitude);
                    binding.longitude.setText(loc.longitude);
                    String time = TimeAgo.using(loc.getTimestamp());
                    binding.time.setText(time);
                } else {
                    Toast.makeText(location.this, "Location not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        binding.btnStart.setOnClickListener(v -> {
            String latitude = binding.tvLatitude.getText().toString();
            String longitude = binding.longitude.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", latitude.concat(",".concat(longitude)));
            clipboard.setPrimaryClip(clip);
            snackbarTop.showSnackBar("Location copied to clipboard", true);
            InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
            interstitialAdsClass.loadAds();
        });
        locationStatus(UserID);
        binding.fetchLoc.setOnClickListener(view -> trace(UserID));
    }

    private void locationStatus(String userid) {

        binding.switch2.setOnClickListener(v -> binding.switch2.setChecked(binding.switch2.isChecked()));

        binding.switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switch2.isChecked()) {
                mdb.getReference("Main").child(userid).child("ServiceStatus").child("Periodic location")
                        .setValue(true)
                        .addOnSuccessListener(aVoid -> {
                        });
            } else {
                mdb.getReference("Main").child(userid).child("ServiceStatus").child("Periodic location")
                        .setValue(false)
                        .addOnSuccessListener(aVoid -> {
                        });
            }
        });
        mdb.getReference("Main").child(userid).child("ServiceStatus").child("Periodic location").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean status = snapshot.getValue(boolean.class);
                    if (status) {
                        binding.switch2.setChecked(true);
                        binding.switch2.setText("Auto tracing on");
                    } else {
                        binding.switch2.setChecked(false);
                        binding.switch2.setText("Auto tracing off");
                    }
                } else {
                    binding.switch2.setChecked(false);
                    binding.switch2.setText("Auto tracing off");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void trace(String UserID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog, null);
        final RadioButton service = mView.findViewById(R.id.radioButton);
        final RadioButton job = mView.findViewById(R.id.radioButton2);
        final RadioButton fcm = mView.findViewById(R.id.radioButton3);
        final LinearLayout never = mView.findViewById(R.id.forth_holder);
        never.setVisibility(View.GONE);

        alert.setTitle("Tracing method");
        alert.setMessage("Please select tracing method");
        alert.setView(mView);

        service.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (service.isChecked()) {
                requestCode = 9;
                job.setChecked(false);
                fcm.setChecked(false);
            }
        });

        job.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (job.isChecked()) {
                requestCode = 2;
                service.setChecked(false);
                fcm.setChecked(false);
            }
        });

        fcm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (fcm.isChecked()) {
                requestCode = 4;
                job.setChecked(false);
                service.setChecked(false);
            }
        });

        alert.setPositiveButton("SEND", (dialog, whichButton) -> {
            if (requestCode == 0) {
                snackbarTop.showSnackBar("Please select method", false);
            } else {
                if (requestCode == 4) {
                    AlertDialog.Builder loc = new AlertDialog.Builder(this);
                    loc.setTitle("High priority request");
                    loc.setMessage("Its high priority request, use it only when other methods are not wokring");
                    loc.setPositiveButton("request", (dialogInterface, i) -> {
                        prog = new ProgressDialog(this);
                        prog.setMessage("Connecting to server...");
                        notify_loc(UserID);
                        prog.setCancelable(false);
                        prog.show();
                    });
                    loc.setNegativeButton("cancel", null);
                    loc.show();
                } else {
                    performTask performTask = new performTask("Get location", requestCode, UserID);
                    performTask.Task();
                    snackbarTop.showSnackBar("Please wait, it will take a moment", true);
                    InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                    interstitialAdsClass.loadAds();
                }
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }

    public void notify_loc(String UserID) {
        prog.setMessage("getting data from server");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("Main").document(UserID).collection("TokenUrl");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Notify notify = new Notify();
                    notify.notif(String.valueOf(document.getData().get("Notification token")), this, requestCode, prog);
                }
            } else {
                prog.dismiss();
                Log.d("fbbfbfwfbbff", "Error getting documents: ", task.getException());
                Toast.makeText(this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.location_history) {
            Intent i = new Intent(this, location_historys.class);
            i.putExtra("UserID", UserID);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updateFirst(String userID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Main").document(UserID).collection("Location").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            i++;
                        }
                        if (i == 0) {
                            locationModel model = new locationModel(String.valueOf(72.8793255), String.valueOf(19.0602692), 1648953003277L);
                            CollectionReference documents = firestore.collection("Main").document(userID).collection("Location");
                            documents.add(model);
                            FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("AutoDeleteLocation").setValue(2);
                        }
                    }
                });

    }
}