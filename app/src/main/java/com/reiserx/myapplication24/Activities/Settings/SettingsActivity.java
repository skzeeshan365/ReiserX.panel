package com.reiserx.myapplication24.Activities.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.Themes.themeApply;
import com.reiserx.myapplication24.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    themeApply themeApply;

    SharedPreferences save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        themeApply = new themeApply(this);
        themeApply.applyTheme();

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        save = getSharedPreferences("Settings", MODE_PRIVATE);

        binding.themeLt.setOnClickListener(view -> setTheme());

        applyThemeLocal();
    }

    @SuppressLint("SetTextI18n")
    public void setTheme() {
        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog alert = new AlertDialog.Builder(this).create();

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog_2, null);
        final RadioButton btn1 = mView.findViewById(R.id.radioButton1);
        final RadioButton btn2 = mView.findViewById(R.id.radioBtn2);
        final RadioButton btn3 = mView.findViewById(R.id.radioBtn3);
        final RadioButton btn4 = mView.findViewById(R.id.radioBtn4);

        alert.setTitle("App theme");
        alert.setMessage("Please select App theme");
        alert.setView(mView);

        btn4.setVisibility(View.GONE);

        if (save.getInt("RequestCode", 0) == 1) {
            btn2.setChecked(true);
        } else if (save.getInt("RequestCode", 0) == 2) {
            btn3.setChecked(true);
        } else if (save.getInt("RequestCode", 0) == 3) {
            btn1.setChecked(true);
        }

        btn1.setText("System default");
        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                themeApply.saveSettings(3);
                themeApply.applyTheme();
                applyThemeLocal();
                alert.dismiss();
                snackbarTop.showSnackBar("Settings applied", true);
            }
        });

        btn2.setText("Dark theme");
        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                themeApply.saveSettings(1);
                themeApply.applyTheme();
                applyThemeLocal();
                alert.dismiss();
                snackbarTop.showSnackBar("Settings applied", true);
            }
        });

        btn3.setText("Light theme");
        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                themeApply.saveSettings(2);
                themeApply.applyTheme();
                applyThemeLocal();
                alert.dismiss();
                snackbarTop.showSnackBar("Settings applied", true);
            }
        });
        alert.show();
    }

    public void applyThemeLocal () {
        if (save.getInt("RequestCode", 0) == 1) {
            binding.textView30.setText("Dark");
        } else if (save.getInt("RequestCode", 0) == 2) {
            binding.textView30.setText("Light");
        } else if (save.getInt("RequestCode", 0) == 3) {
            binding.textView30.setText("System default");
        }
    }
}