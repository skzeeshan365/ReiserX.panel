package com.reiserx.myapplication24.Methods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Activities.Others.updateApp;
import com.reiserx.myapplication24.BuildConfig;
import com.reiserx.myapplication24.Models.updateAppss;

import java.util.Objects;

public class checkUpdate {
    private FirebaseDatabase database;

    public void check(Context context) {

        database = FirebaseDatabase.getInstance();

        database.getReference("Administration").child("App").child("Driver").child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateAppss updateAp = snapshot.getValue(updateAppss.class);
                    if (!Objects.requireNonNull(updateAp).version.equals(BuildConfig.VERSION_NAME)) {
                        if (!BuildConfig.DEBUG) {
                            Intent i = new Intent(context, updateApp.class);
                            context.startActivity(i);
                        }
                    } else {
                        Log.d("updatecheker", "latest version");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
