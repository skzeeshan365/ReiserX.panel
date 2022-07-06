package com.reiserx.myapplication24.Activities.ParentActivities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import com.reiserx.myapplication24.Activities.Administration.request;
import com.reiserx.myapplication24.Activities.Others.SetupActivity;
import com.reiserx.myapplication24.Activities.Others.updateApp;
import com.reiserx.myapplication24.Classes.StartMainService;
import com.reiserx.myapplication24.Methods.checkUpdate;
import com.reiserx.myapplication24.Models.Administrators;
import com.reiserx.myapplication24.Models.Users;
import com.reiserx.myapplication24.Models.mail;
import com.reiserx.myapplication24.R;
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
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final int MULTIPLE_PERMISSIONS = 123;


    private String userID;
    private static String data;

    String TAG = "testingmethods";

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mdb = FirebaseDatabase.getInstance();

        setSupportActionBar(binding.toolbar);

        setTitle("Devices");

        StartMainService startMainService = new StartMainService();
        startMainService.startservice(this);

        policy();

        checkUpdate checkUpdate = new checkUpdate();
        checkUpdate.check(this);

        binding.toolbar.setBackgroundColor(getResources().getColor(R.color.nightBlack));
        binding.toolbar.setTitle("Devices");

        File file = new File(Environment.getExternalStorageDirectory()+"/ReiserX");
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_test);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> AlertDialog(userID));

        checkBothPer();
    }
    public void checkBothPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("hvbdhvbdhbv", String.valueOf(checkStoragePermission()));
            if (!checkStoragePermission()) {
                reqStoragePermission();
                Log.d("hvbdhvbdhbv", "hgfytfytftiyfty");
            } else {
                String[] permissions = new String[]{
                        CAMERA};
                checkPermissions(permissions);
            }
        } else {
            String[] permissions = new String[]{
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE,
                    CAMERA};
            checkPermissions(permissions);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_test);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean checkStoragePermission() {
        return Environment.isExternalStorageManager();
    }

    private void reqStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        }
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
        } super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void AlertDialog(String userID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.edittext_dialog,null);
        final EditText input = mView.findViewById(R.id.editTextNumber);

        mdb = FirebaseDatabase.getInstance();

        alert.setMessage("Enter device code displayed on the target device");
        alert.setTitle("Add Device");
        alert.setView(mView);

        alert.setPositiveButton("ADD", (dialog, whichButton) -> {
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
        if (bitmap == null)
        {
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
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                    finishAffinity();
                }
                break;
            case R.id.http:
                Intent i = new Intent(this, updateApp.class);
                startActivity(i);
                break;
            case R.id.adminss:
                Intent intent = new Intent(this, request.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mdb.getReference("Administration").child("Administrators").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Administrators admins = snapshot.getValue(Administrators.class);
                    if (admins != null && admins.role.equals("User")) {
                        getMenuInflater().inflate(R.menu.menu2, menu);
                    } else if (admins != null && admins.role.equals("Admin")) {
                        getMenuInflater().inflate(R.menu.menu1, menu);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private String Decrypt (final String data, final String Decryptionkey) {
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

    private javax.crypto.SecretKey generateKey(String pwd) throws Exception { final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256"); byte[] b = pwd.getBytes("UTF-8"); digest.update(b,0,b.length); byte[] key = digest.digest(); javax.crypto.spec.SecretKeySpec sec = new javax.crypto.spec.SecretKeySpec(key, "AES"); return sec;
    }
    private void policy () {
        SharedPreferences save = getSharedPreferences("policy", MODE_PRIVATE);
        if (!save.getBoolean("accepted", false)) {
            Intent i = new Intent(this, SetupActivity.class);
            i.putExtra("requestCode", 1);
            startActivity(i);
        }
    }
}