package com.reiserx.myapplication24.Activities.ParentActivities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.reiserx.myapplication24.Activities.LoginSystem.ProfileActivity;
import com.reiserx.myapplication24.Activities.Others.SetupActivity;
import com.reiserx.myapplication24.Activities.Settings.SettingsActivity;
import com.reiserx.myapplication24.Adapters.Audios.DeviceListAdaptet;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Models.Administrators;
import com.reiserx.myapplication24.Models.Users;
import com.reiserx.myapplication24.Models.mail;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.Themes.themeApply;
import com.reiserx.myapplication24.databinding.ActivityTestBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Test extends AppCompatActivity {

    AppBarConfiguration appBarConfiguration;
    ActivityTestBinding binding;

    FirebaseDatabase mdb;
    FirebaseAuth mAuth;

    private static final int MULTIPLE_PERMISSIONS = 123;


    private String userID;
    private static String data;

    String TAG = "testingmethods";

    ArrayList<Users> data1;
    DeviceListAdaptet adapter;

    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mdb = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        setInstallStateUpdatedListener();

        checkUpdate();

        File file = new File(Environment.getExternalStorageDirectory() + "/ReiserX");
        if (!file.exists()) {
            file.mkdir();
        }

        File files = new File(Environment.getExternalStorageDirectory() + "/Download/ReiserX");
        if (!files.exists()) {
            files.mkdir();
        }

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }

        mdb.getReference("Administration").child("Mail").child("From developer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mail mail = snapshot.getValue(com.reiserx.myapplication24.Models.mail.class);
                    if (mail != null) {
                        announcement(mail.getTitle(), mail.getMessage(), mail.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
        }

        binding.fab.setOnClickListener(view -> AlertDialog(userID));

        checkBothPer();

        SharedPreferences save = getSharedPreferences("Admins", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        data1 = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceListAdaptet(this, data1);
        binding.rec.setAdapter(adapter);

        mdb.getReference("Administration").child("Administrators").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Administrators admins = snapshot.getValue(Administrators.class);
                    if (admins != null && admins.role.equals("User")) {
                        mdb.getReference("Administration").child("TargetDevices").child(userID).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                data1.clear();
                                snapshot.exists();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Users u = snapshot1.getValue(Users.class);
                                    data1.add(u);
                                }
                                adapter.notifyDataSetChanged();
                                if (data1.isEmpty()) {
                                    binding.rec.setVisibility(View.GONE);
                                    binding.textView24.setVisibility(View.VISIBLE);
                                } else {
                                    binding.textView24.setVisibility(View.GONE);
                                    binding.rec.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        myEdit.putString("Admin", admins.role);
                        myEdit.putBoolean("fileUploadAccess", admins.getFileUploadAccess());
                        myEdit.apply();
                    } else if (admins != null && admins.role.equals("Admin")) {
                        mdb.getReference("Userdata").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                data1.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Users u = snapshot1.getValue(Users.class);
                                    data1.add(u);
                                }
                                adapter.notifyDataSetChanged();
                                if (data1.isEmpty()) {
                                    binding.rec.setVisibility(View.GONE);
                                    binding.textView24.setVisibility(View.VISIBLE);
                                } else {
                                    binding.textView24.setVisibility(View.GONE);
                                    binding.rec.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        myEdit.putString("Admin", "Admin");
                        myEdit.putBoolean("fileUploadAccess", admins.getFileUploadAccess());
                        myEdit.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkBothPer() {
        String[] permissions = new String[]{
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE,
                CAMERA};
        checkPermissions(permissions);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_test);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 2296:
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        checkBothPer();
                    }
                }
                break;
            case 12:
                if (data != null && data.getData() != null) {
                    try {
                        readFromImage(data.getData(), userID);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            case 3650:
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                    } else if (resultCode == RESULT_OK) {

                    } else {
                        Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                        checkUpdate();
                    }
                    break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkPermissions(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }   // permissions list of don't granted permission
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void AlertDialog(String userID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.edittext_dialog, null);
        final EditText input = mView.findViewById(R.id.editTextNumber);

        mdb = FirebaseDatabase.getInstance();

        alert.setMessage("Enter device code displayed on ReiserX driver or scan the QR code");
        alert.setTitle("Connect Device");
        alert.setView(mView);

        alert.setPositiveButton("CONNECT", (dialog, whichButton) -> {
            //What ever you want to do with the value
            if (!input.getText().toString().equals("")) {
                long data = Long.parseLong(input.getText().toString());

                mdb.getReference("Userdata").orderByChild("timestamp").equalTo(data).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Users users = snapshot1.getValue(Users.class);
                                Users deviceList = new Users(Objects.requireNonNull(users).uid, users.name, "null", users.timestamp);
                                mdb.getReference("Administration").child("TargetDevices").child(userID).child(users.uid)
                                        .setValue(deviceList)
                                        .addOnSuccessListener(unused -> {
                                        });
                            }
                        } else {
                            Toast.makeText(Test.this, "Device does not exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else Toast.makeText(Test.this, "Please enter the code", Toast.LENGTH_SHORT).show();
        });

        alert.setNegativeButton("SCAN", (dialog, whichButton) -> {
            AlertDialog.Builder scan = new AlertDialog.Builder(this);
            scan.setTitle("Scan device code");
            scan.setMessage("Please choose your option");
            scan.setPositiveButton("camera", (dialogInterface, i) -> {
                Intent scans = new Intent(this, ScanQr.class);
                scans.putExtra("UserID", userID);
                startActivity(scans);
            });
            scan.setNegativeButton("gallery", (dialogInterface, i) -> {
                Intent photoPic = new Intent(Intent.ACTION_PICK);
                photoPic.setType("image/*");
                startActivityForResult(photoPic, 12);
            });
            scan.setNeutralButton("cancel", null);
            scan.show();
        });
        alert.setNeutralButton("cancel", null);

        alert.show();
    }

    private void readFromImage(Uri uri, String userID) throws FileNotFoundException {
        InputStream inputStream = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap == null) {
            Toast.makeText(this, "File is not a QR code", Toast.LENGTH_SHORT).show();

        } else {
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            try {
                Result result = reader.decode(bBitmap);
                try {
                    JSONObject jsonObj = new JSONObject(result.getText());
                    data = jsonObj.getString("data");
                    String uid = jsonObj.getString("uid");

                    mdb.getReference("Userdata").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Users users = snapshot.getValue(Users.class);
                                if (users != null)
                                    if (String.valueOf(users.timestamp).equals(Decrypt(data, users.key))) {
                                        Users deviceList = new Users(Objects.requireNonNull(users).uid, users.name, "null", users.timestamp);
                                        mdb.getReference("Administration").child("TargetDevices").child(userID).child(users.uid)
                                                .setValue(deviceList)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(Test.this, "Device added successfully", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(Test.this, "Access denied", Toast.LENGTH_SHORT).show();
                                    }
                            } else {
                                Toast.makeText(Test.this, "Device does not exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, Arrays.toString(e.getStackTrace()));
                }
            } catch (NotFoundException e) {
                Log.e(TAG, "decode exception", e);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intents;
        switch (item.getItemId()) {
            case R.id.profile_menu_item:
                intents = new Intent(this, ProfileActivity.class);
                startActivity(intents);
                break;
            case R.id.about_menu1:
                intents = new Intent(this, SetupActivity.class);
                startActivity(intents);
                break;
            case R.id.menu1_settings:
                intents = new Intent(this, SettingsActivity.class);
                startActivity(intents);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void announcement(String title, String msg, int id) {
        SharedPreferences save = getSharedPreferences("Announcement", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        if (save.getInt("id", 0) != id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("ok", null);
            builder.show();
            myEdit.putInt("id", id);
            myEdit.apply();
        }
    }

    private String Decrypt(final String data, final String Decryptionkey) {
        String decryptedString;
        try {
            javax.crypto.spec.SecretKeySpec key = (javax.crypto.spec.SecretKeySpec) generateKey(Decryptionkey);
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            byte[] decode = android.util.Base64.decode(data, android.util.Base64.DEFAULT);
            byte[] decval = c.doFinal(decode);
            decryptedString = new String(decval);
        } catch (Exception ex) {
            decryptedString = null;
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
        return decryptedString;
    }

    private javax.crypto.SecretKey generateKey(String pwd) throws Exception {
        final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] b = pwd.getBytes("UTF-8");
        digest.update(b, 0, b.length);
        byte[] key = digest.digest();
        javax.crypto.spec.SecretKeySpec sec = new javax.crypto.spec.SecretKeySpec(key, "AES");
        return sec;
    }

    private void checkUpdate() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        setInstallStateUpdatedListener();
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, 3650);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        TSnackbar.make(findViewById(android.R.id.content), "New update is ready, please install it now", TSnackbar.LENGTH_INDEFINITE)
                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                })
                .show();
    }

    public void setInstallStateUpdatedListener () {
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeInstallStateUpdateListener();
    }
}