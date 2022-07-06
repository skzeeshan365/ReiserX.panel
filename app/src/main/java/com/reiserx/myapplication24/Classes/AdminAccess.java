package com.reiserx.myapplication24.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

public class AdminAccess {
    String fileName = "Admins";
    Context context;
    SharedPreferences save;

    public AdminAccess(Context context) {
        this.context = context;
        save = context.getSharedPreferences("Admins", MODE_PRIVATE);
    }
    public boolean fileUploadAccess () {
        if (save.getBoolean("fileUploadAccess", false)) {
            return true;
        } else {
            showError();
            return false;
        }
    }
    public void showError() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Access denied");
        alert.setMessage("You don't have access to this feature, please contact the developer to grant you this access");
        alert.setPositiveButton("ok", null);
        alert.show();
    }
}
