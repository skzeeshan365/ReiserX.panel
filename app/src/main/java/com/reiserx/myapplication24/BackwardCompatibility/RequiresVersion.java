package com.reiserx.myapplication24.BackwardCompatibility;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.reiserx.myapplication24.Utilities.Version;

public class RequiresVersion {
    Context context;
    String UserID;
    SharedPreferences save;

    public RequiresVersion(Context context, String UserID) {
        this.context = context;
        this.UserID = UserID;
        save = context.getSharedPreferences("Compatibility", MODE_PRIVATE);
    }

    public boolean Requires(String version) {

        Version required = new Version(version);
        Version current = new Version(save.getString(UserID, ""));

        if (required.compareTo(current) < 0) {
            return true;
        } else if (required.compareTo(current) > 0) {
            showError(version, save.getString(UserID, ""));
            return false;
        } else return required.equals(current);
    }

    public void setCurrentVersion(String version) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putString(UserID, version);
        myEdit.apply();
    }

    public void showError(String requiredVersion, String currentVersion) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Not compatible");
        alert.setMessage("This feature requires ReiserX v" + requiredVersion + ", current is " + currentVersion + "\nplease update ReiserX to the latest version");
        alert.setPositiveButton("ok", null);
        alert.show();
    }
}