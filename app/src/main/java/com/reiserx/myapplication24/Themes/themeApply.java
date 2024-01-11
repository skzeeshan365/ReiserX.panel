package com.reiserx.myapplication24.Themes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class themeApply {
    Context context;
    SharedPreferences save;


    public themeApply(Context context) {
        this.context = context;
        save = context.getSharedPreferences("Settings", MODE_PRIVATE);

        if (save.getInt("RequestCode", 0) == 0) {
            saveSettings(3);
        }
    }

    public void saveSettings (int RequestCode) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putInt("RequestCode", RequestCode);
        myEdit.apply();
    }

    public void applyTheme () {
            if (save.getInt("RequestCode", 0) == 1) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (save.getInt("RequestCode", 0) == 2) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (save.getInt("RequestCode", 0) == 3) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
    }
}
