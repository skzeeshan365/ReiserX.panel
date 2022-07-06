package com.reiserx.myapplication24.BackwardCompatibility;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class RequiresVersion {
    Context context;
    String UserID;
    SharedPreferences save;

    public RequiresVersion(Context context, String UserID) {
        this.context = context;
        this.UserID = UserID;
        save = context.getSharedPreferences("Compatibility", MODE_PRIVATE);
    }

    public boolean Requires (float version) {
        if (version<=save.getFloat(UserID, 0)) {
            Log.d("iufghirnfriwgn", "true");
            return true;
        } else {
            Log.d("iufghirnfriwgn", "false");
            showError(version, save.getFloat(UserID, 0));
            return false;
        }
    }
    public void setCurrentVersion (float version) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putFloat(UserID, version);
        myEdit.apply();
    }
    public void showError(float requiredVersion, float currentVersion) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Not compatible");
        alert.setMessage("This feature requires ReiserX v"+requiredVersion+", current is "+currentVersion+"\nplease update ReiserX to the latest version");
        alert.setPositiveButton("ok", null);
        alert.show();
    }
}
