package com.reiserx.myapplication24.Activities.Directories;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.BackwardCompatibility.RequiresVersion;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.folderInfo;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityCheckDetailsBinding;

import java.util.Locale;

public class checkDetails extends AppCompatActivity {

    ActivityCheckDetailsBinding binding;

    private SharedPreferences file;
    private FirebaseDatabase database;

    ProgressDialog prog;

    int imageNo, videoNo, audioNO;

    String message, Path;

    int Code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();

        String UserID = getIntent().getStringExtra("UserID");
        Path = getIntent().getStringExtra("Path");
        message = getIntent().getStringExtra("Message");
        String fol = Path.replace(".", "");

        setTitle(Path);

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        file = getSharedPreferences("Downloads", MODE_PRIVATE);

        prog = new ProgressDialog(this);
        prog.setMessage("Getting file details...");
        prog.setCancelable(false);
        prog.show();

        binding.imageNo.setVisibility(View.GONE);
        binding.videoNo.setVisibility(View.GONE);
        binding.audioNo.setVisibility(View.GONE);

        performTask performTask = new performTask(Path, 3, UserID);
        performTask.Task();

        binding.message.setText(message);

        SharedPreferences save = getSharedPreferences("Download", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        database.getReference("Main").child(UserID).child("GetFileList").child("Folder").child(fol.toLowerCase(Locale.ROOT)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    folderInfo folderInfo = snapshot.getValue(folderInfo.class);
                    binding.image.setText("Images".concat(": ".concat(String.valueOf(folderInfo != null ? folderInfo.getImage() : 0))));
                    binding.folder.setText("Folders".concat(": ".concat(String.valueOf(folderInfo != null ? folderInfo.getFolder() : 0))));
                    binding.video.setText("Videos".concat(": ".concat(String.valueOf(folderInfo != null ? folderInfo.getVideo() : 0))));
                    binding.audio.setText("Audios".concat(": ".concat(String.valueOf(folderInfo != null ? folderInfo.getAudio() : 0))));
                    binding.pdfTxt.setText("PDF".concat(": ".concat(String.valueOf(folderInfo != null ? folderInfo.getPdf() : 0))));
                    binding.size.setText("Size: ".concat(folderInfo.getSize()));

                    imageNo = folderInfo.image;
                    videoNo = folderInfo.video;
                    audioNO = folderInfo.audio;
                    check(Path, save, UserID);
                    binding.imageNo.setText(String.valueOf(folderInfo.image));
                    binding.videoNo.setText(String.valueOf(folderInfo.video));
                    binding.audioNo.setText(String.valueOf(folderInfo.audio));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void check(String Path, SharedPreferences save, String UserID) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        binding.downloadData.setText("NEXT");
        file = getSharedPreferences("Downloads", MODE_PRIVATE);

        binding.downloadData.setOnClickListener(v -> {
            RequiresVersion requiresVersion = new RequiresVersion(checkDetails.this, UserID);
            if (requiresVersion.Requires(3.0f)) {
                dialog.setTitle("File operation ");
                dialog.setMessage("Download or check files");
                dialog.setPositiveButton("DOWNLOAD", (dialog13, which) -> {

                    if (message.endsWith(".jpg") || message.endsWith(".jpeg") || message.endsWith(".png") || message.endsWith(".mp4") || message.endsWith(".wmv") || message.endsWith(".webm") || message.endsWith(".mp3") || message.endsWith(".mp4a") || message.endsWith(".wma") || message.endsWith(".m4a") || message.endsWith(".pdf") || message.endsWith(".docx")) {
                        performTask performTask = new performTask(Path, message, 10, UserID);
                        performTask.Task();
                        imageProg(UserID);
                        videoProg(UserID);
                        audioProg(UserID);
                    } else {
                        downloadFiles(UserID, Path);
                        imageProg(UserID);
                        videoProg(UserID);
                        audioProg(UserID);
                    }
                });
                dialog.setNegativeButton("CHECK", (dialog1, which) -> {
                    if (message.endsWith(".jpg") || message.endsWith(".jpeg") || message.endsWith(".png") || message.endsWith(".mp4") || message.endsWith(".wmv") || message.endsWith(".webm") || message.endsWith(".mp3") || message.endsWith(".mp4a") || message.endsWith(".wma") || message.endsWith(".m4a") || message.endsWith(".pdf") || message.endsWith(".docx")) {
                        Intent intent = new Intent(checkDetails.this, checkDownload.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Path", message);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(checkDetails.this, checkDownload.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Path", Path);
                        startActivity(intent);
                    }
                });

                dialog.setNeutralButton("CANCEL", null);
                dialog.show();
            }
        });
        prog.dismiss();
    }

    public void imageProg(String UserID) {
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        mdb.getReference("Main").child(UserID).child("Upload").child("Images").child("Download progress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int value = snapshot.getValue(int.class);
                    binding.imageNo.setVisibility(View.VISIBLE);
                    binding.imageNo.setText("Image: ".concat(String.valueOf(value).concat("/").concat(String.valueOf(imageNo))));

                    if (imageNo == value) {
                        mdb.getReference("Main").child(UserID).child("Upload").child("Images").child("Download progress")
                                .removeValue();
                        binding.imageNo.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void videoProg(String UserID) {
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        DatabaseReference ref = mdb.getReference("Main").child(UserID).child("Upload").child("Videos").child("Download progress");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int value = snapshot.getValue(int.class);
                    binding.videoNo.setVisibility(View.VISIBLE);
                    binding.videoNo.setText("Video: ".concat(String.valueOf(value).concat("/").concat(String.valueOf(videoNo))));

                    if (videoNo == value) {

                        ref.removeValue();
                        binding.videoNo.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void audioProg(String UserID) {
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        DatabaseReference ref = mdb.getReference("Main").child(UserID).child("Upload").child("Audios").child("Download progress");
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int value = snapshot.getValue(int.class);
                    binding.audioNo.setVisibility(View.VISIBLE);
                    binding.audioNo.setText("Audio: ".concat(String.valueOf(value).concat("/").concat(String.valueOf(audioNO))));

                    if (audioNO == value) {
                        ref.removeValue();
                        binding.audioNo.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void downloadFiles(String UserID, String path) {
        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog_2, null);
        final RadioButton btn1 = mView.findViewById(R.id.radioButton1);
        final RadioButton btn2 = mView.findViewById(R.id.radioBtn2);
        final RadioButton btn3 = mView.findViewById(R.id.radioBtn3);
        final RadioButton btn4 = mView.findViewById(R.id.radioBtn4);

        alert.setTitle("Download");
        alert.setMessage("Please select file type to download on the server");
        alert.setView(mView);

        btn1.setText("Images");
        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                Code = 4;
            }
        });

        btn2.setText("Videos");
        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                Code = 17;
            }
        });

        btn3.setText("Audios");
        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                Code = 18;
            }
        });

        btn4.setText("PDF");
        btn4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn4.isChecked()) {
                Code = 19;
            }
        });

        alert.setPositiveButton("download", (dialog, whichButton) -> {
            if (Code == 0) {
                snackbarTop.showSnackBar("Please select file type", false);
            } else {
                performTask performTask = new performTask(Path, Code, UserID);
                performTask.Task();
                snackbarTop.showSnackBar("Request sent", true);
                InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                interstitialAdsClass.loadAds();
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }
}