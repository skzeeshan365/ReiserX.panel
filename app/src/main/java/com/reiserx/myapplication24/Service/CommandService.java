package com.reiserx.myapplication24.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.myapplication24.Classes.PeriodicTask;
import com.reiserx.myapplication24.Receiver.AlarmReceiver;


public class CommandService extends Service {

    String TAG = "CommandService";

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStart");

        if (getMemory()) {
            //Firebase initialization
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            FirebaseDatabase mdb = FirebaseDatabase.getInstance();

            SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);

            String userID = save.getString("UserID", "");

            if (userID != null) {
                PeriodicTask periodicTask = new PeriodicTask(this);
                periodicTask.periodice(userID, mdb);
            }
        }
        return START_STICKY;
    }

    public void onTrimMemory(int level) {

        // Determine which lifecycle or system event was raised.
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                Log.d(TAG, "low");
                Intent in = new Intent(this, CommandService.class);
                this.stopService(in);
                in = null;

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                Log.d(TAG, "high");
                Intent ins = new Intent(this, CommandService.class);
                this.stopService(ins);

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    @Override
    public void onTaskRemoved(Intent intent) {

        sendBroadcast(new Intent("AlarmService"));
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (getMemory()) {
        } else {
            Log.d(TAG, "onLowMemory");
            Intent in = new Intent(this, CommandService.class);
            this.stopService(in);
        }
    }

    public Boolean getMemory() {

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        // Do memory intensive work ...
        return !memoryInfo.lowMemory;
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        Log.d(TAG, "onDestroy");
        sendBroadcast(new Intent("AlarmService"));
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding
        return null;
    }
}