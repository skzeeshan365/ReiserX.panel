package com.reiserx.myapplication24.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Models.Users;

import java.util.Calendar;

public class PeriodicTask {
    SharedPreferences save;

    String TAG = "CommandFile";

    Context context;

    public PeriodicTask(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void periodice(String userID, FirebaseDatabase mdb) {
        save = context.getSharedPreferences("commandFile", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        Calendar cal = Calendar.getInstance();
        int time = cal.get(Calendar.HOUR);
        Log.e(TAG, String.valueOf(cal.get(Calendar.HOUR)));
        if (time==0) {
            myEdit.putInt("hr", time);
            myEdit.apply();
            Log.e(TAG, "time 0");
        }
        if (time>save.getInt("hr", 0)) {
            DatabaseReference checkUser = mdb.getReference("Administration").child("TargetDevices").child(userID);
            listen(checkUser, time);
            Log.e(TAG, "fired");
        } else Log.e(TAG, "not fired");
    }
    private void initiate(int hour) {

        Log.e(TAG, "traced");

        save = context.getSharedPreferences("commandFile", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putInt("hr", hour);
        myEdit.apply();
    }
    public void listen(DatabaseReference checkRef, int time) {
        SharedPreferences service = context.getSharedPreferences("service", MODE_PRIVATE);

        checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Users u = snapshot1.getValue(Users.class);

                        if (service.getBoolean(u.uid, false)) {
                            Log.d(TAG, "false");
                            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Main").child(u.uid).child("ServiceStatus").child("CommandStatus");
                            DatabaseReference CommandService = FirebaseDatabase.getInstance().getReference("Main").child(u.uid).child("ServiceStatus").child("CheckStatus");
                            statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        boolean value = snapshot.getValue(boolean.class);
                                        if (!value) {
                                            Log.d(TAG, "Service dead");
                                            CommandService.setValue(true);
                                            SharedPreferences save = context.getSharedPreferences("Tokens", MODE_PRIVATE);
                                            Notify notify = new Notify();
                                            notify.notif(save.getString("tokens", ""), context, 5);
                                            initiate(time);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
