package com.reiserx.myapplication24.Activities.Operations;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.BackwardCompatibility.RequiresVersion;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Classes.postRequest;
import com.reiserx.myapplication24.Models.networkState;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityOperationBinding;



import java.util.Objects;
import java.util.Random;

public class operation extends AppCompatActivity {

    ActivityOperationBinding binding;

    FirebaseDatabase database;
    FirebaseDatabase mdb = FirebaseDatabase.getInstance();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOperationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();

        showAd();

        String UserID = getIntent().getStringExtra("UserID");
        setTitle(getIntent().getStringExtra("name"));

        userStatus(UserID);

        binding.button4.setOnClickListener(view -> {
            Intent intent = new Intent(operation.this, OperationListActivity.class);
            intent.putExtra("UserID", UserID);
            startActivity(intent);
        });

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        setDesign(binding.accessibilityRefresh);
        setDesign(binding.accUpdateBtn);

        binding.accUpdateBtn.setOnClickListener(view -> {
            RequiresVersion requiresVersion = new RequiresVersion(this, UserID);
            if (requiresVersion.Requires(3.9f)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Accessibility service status");
                alert.setMessage("This is accessibility service running status (NOT ENABLED STATUS), you can fetch the status by clicking fetch");
                alert.setPositiveButton("fetch", (dialogInterface, i) -> {
                    fetchAccStatus(UserID);
                });
                alert.setNegativeButton("cancel", null);
                alert.show();
            }
        });

        binding.accessibilityRefresh.setOnClickListener(view -> {
            SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
            performTask performTask = new performTask("get", 16, UserID);
            performTask.Task();
            snackbarTop.showSnackBar("Request sent...", true);
        });

        database.getReference().child("Main").child(UserID).child("ServiceStatus").child("Accessibility").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = snapshot.getValue(Boolean.class);
                    if (value) {
                        binding.accessibilityServiceInfo.setText("enabled");
                    } else binding.accessibilityServiceInfo.setText("not enabled");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("Main").child(UserID).child("ServiceStatus").child("NetworkState").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    networkState value = Objects.requireNonNull(dataSnapshot).getValue(networkState.class);
                    if (Objects.requireNonNull(value).state) {
                        binding.networkTimestamp.setText("Online ");
                        binding.timestamp.setText(value.timestamp);
                        if (value.getType().equals("mobile")) {
                            binding.imageView3.setImageResource(R.drawable.ic_baseline_signal_cellular_alt_24);
                        } else if (value.getType().equals("wifi")) {
                            binding.imageView3.setImageResource(R.drawable.ic_baseline_wifi_24);
                        }
                    } else {
                        binding.networkTimestamp.setText("Offline " + value.timestamp);
                        binding.timestamp.setText(value.timestamp);
                    }
                } else {
                    binding.networkTimestamp.setText("Not available");
                    binding.timestamp.setText("Not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        getRunningTask(UserID);
        database.getReference("Main").child(UserID).child("ServiceStatus").child("Offline").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = Objects.requireNonNull(dataSnapshot).getValue(String.class);
                    binding.serviceStateTxt.setText(value);
                } else {
                    binding.serviceStateTxt.setText("Not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        ScrnLock(UserID);

        getVersion(UserID);

        acc_service_update(UserID);

        SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = service.edit();

        binding.switch4.setOnClickListener(v -> binding.switch4.setChecked(binding.switch4.isChecked()));
        binding.switch4.setChecked(service.getBoolean(UserID, false));
        binding.switch4.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.switch4.isChecked()) {
                myEdit.putBoolean(UserID, true);
                getTokens(UserID);
            } else {
                myEdit.putBoolean(UserID, false);
            }
            myEdit.apply();
        });

    }

    private void userStatus(String userid) {

        binding.switch1.setOnClickListener(v -> binding.switch1.setChecked(binding.switch1.isChecked()));

        binding.switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switch1.isChecked()) {
                mdb.getReference("Main").child(userid).child("ServiceStatus").child("Status")
                        .setValue("Online")
                        .addOnSuccessListener(aVoid -> {
                        });
            } else {
                mdb.getReference("Main").child(userid).child("ServiceStatus").child("Status")
                        .setValue("Offline")
                        .addOnSuccessListener(aVoid -> {
                        });
            }
        });
        mdb.getReference("Main").child(userid).child("ServiceStatus").child("Status").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (status != null) {
                        if (status.equals("Online")) {
                            binding.switch1.setChecked(true);
                            binding.switch1.setText("//Service active");
                        } else {
                            binding.switch1.setChecked(false);
                            binding.switch1.setText("//Service not active");
                        }
                    }
                } else {
                    binding.switch1.setChecked(false);
                    binding.switch1.setText("//User is offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getVersion(String UserID) {
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        mdb.getReference("Main").child(UserID).child("ServiceStatus").child("version").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) {
                        binding.versionText.setText("v" + value);
                        RequiresVersion requiresVersion = new RequiresVersion(operation.this, UserID);
                        requiresVersion.setCurrentVersion(Float.parseFloat(value));
                    }
                } else binding.versionText.setText("Not available");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void ScrnLock (String UserID) {
        DatabaseReference scrnLock = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("ServiceStatus").child("screenLock");
        scrnLock.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = snapshot.getValue(Boolean.class);
                    if (value) {
                        binding.scrnLockData.setText("Locked");
                    } else binding.scrnLockData.setText("Unlocked");
                } else binding.scrnLockData.setText("N/A");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getRunningTask (String UserID) {
        DatabaseReference scrnLock = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("ServiceStatus").child("Current task");
        scrnLock.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    if (value!= null && !value.equals("null")) {
                        binding.runningTaskText.setText(value);
                    } else binding.runningTaskText.setText("Not available");
                } else binding.runningTaskText.setText("Not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTokens (String UserID) {

        SharedPreferences save = getSharedPreferences("Tokens", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("Main").document(UserID).collection("TokenUrl");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    myEdit.putString("tokens", String.valueOf(document.getData().get("Notification token")));
                    myEdit.apply();
                }
            } else {
                Log.d("fbbfbfwfbbff", "Error getting documents: ", task.getException());
                Toast.makeText(operation.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void acc_service_update (String UserID) {
        DatabaseReference acc = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("AccessibilityUpdate");
        acc.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    if (value!= null) {
                        binding.accServiceUpdate.setText(value);
                    } else binding.accServiceUpdate.setText("Not available");
                } else binding.accServiceUpdate.setText("Not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setDesign (ImageView view) {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                view.setColorFilter(getColor(R.color.white));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:

                view.setColorFilter(getColor(R.color.purple_500));
                break;
        }
    }

    private void fetchAccStatus(String UserID) {

        ProgressDialog prog = new ProgressDialog(this);
        prog.setMessage("Processing...");
        prog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection("Main").document(UserID).collection("TokenUrl");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    postRequest postRequest = new postRequest(String.valueOf(document.getData().get("Notification token")), "com.reiserx.testtrace.accessibility", "3", prog, 7, operation.this);
                    postRequest.notifyBackground();
                }
            } else {
                Log.d("fbbfbfwfbbff", "Error getting documents: ", task.getException());
                Toast.makeText(this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public void showAd () {
        if (getRandom(0, 1)==1) {
            InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
            interstitialAdsClass.loadAds();
        }
    }
}