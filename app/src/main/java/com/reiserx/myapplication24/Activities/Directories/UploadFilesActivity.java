package com.reiserx.myapplication24.Activities.Directories;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.FileUtil;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Methods.DesignLayout;
import com.reiserx.myapplication24.Models.uploadTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityUploadFilesBinding;

import java.util.ArrayList;

public class UploadFilesActivity extends AppCompatActivity {

    ActivityUploadFilesBinding binding;

    String UserID, path;
    Uri selectedData = null;

    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");
        path = getIntent().getStringExtra("Path");

        setTitle(path);

        prog = new ProgressDialog(this);
        prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        binding.attachHolder.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 25);
        });

        setDesign();
    }

    @SuppressLint("ResourceAsColor")
    public void setDesign () {
        DesignLayout designLayout = new DesignLayout();
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                designLayout.Apply(binding.attachHolder, 20, 8, "#201F1F", true);
                binding.textView17.setTextColor(Color.parseColor("#FFFFFF"));
                binding.imageView4.setColorFilter(getColor(R.color.white));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                designLayout.Apply(binding.attachHolder, 20, 8, "#FFFFFF", true);
                binding.textView17.setTextColor(R.color.black);
                binding.imageView4.setColorFilter(getColor(R.color.purple_500));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    selectedData = data.getData();
                    binding.imageView4.setImageResource(R.drawable.ic_baseline_done_24);
                    binding.imageView4.setColorFilter(getColor(R.color.green));
                    binding.textView17.setText("File attached");
                    ArrayList<String> filepath = new ArrayList<>();
                    filepath.add(FileUtil.convertUriToFilePath(this, selectedData));
                    String fileName = Uri.parse(filepath.get(0)).getLastPathSegment();
                    binding.editTextTextPersonName.setText(fileName);

                    binding.button3.setOnClickListener(view -> {
                        prog.setMessage("Processing...");
                        prog.show();
                        if (selectedData != null) {
                            if (binding.editTextTextPersonName.getText().toString().trim().equals("")) {
                                prog.dismiss();
                                snackbarTop.showSnackBar("Please enter filename", false);
                            } else {
                                prog.setMessage("Uploading...");
                                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("UploadedFiles").child(path).child(fileName);
                                reference.putFile(selectedData).addOnProgressListener(snapshot -> {
                                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                    prog.setProgress((int) progress);
                                }).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                            uploadTask uploadTask = new uploadTask(path, path.concat("/".concat(fileName)), UserID, uri.toString());
                                            uploadTask.uploadData();
                                            refresh();
                                            prog.setMessage("Uploaded");
                                            prog.dismiss();
                                            snackbarTop.showSnackBar("File uploaded successfully", true);
                                            InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(UploadFilesActivity.this);
                                            interstitialAdsClass.loadAds();
                                        });
                                    }
                                });
                            }
                        } else {
                            prog.dismiss();
                            snackbarTop.showSnackBar("Please select a file", false);
                        }
                    });

                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    public void refresh () {
        binding.imageView4.setImageResource(R.drawable.ic_baseline_add_24);
        setDesign();
        binding.textView17.setText("Attach a file");
        binding.editTextTextPersonName.setText("");
        selectedData = null;
    }
}