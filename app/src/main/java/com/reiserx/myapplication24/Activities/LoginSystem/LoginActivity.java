package com.reiserx.myapplication24.Activities.LoginSystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.myapplication24.Activities.ParentActivities.Test;
import com.reiserx.myapplication24.BuildConfig;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.Admin_Accounts;
import com.reiserx.myapplication24.Models.Administrators;
import com.reiserx.myapplication24.Models.deviceInfo;
import com.reiserx.myapplication24.Models.deviceLogin;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.Themes.themeApply;
import com.reiserx.myapplication24.Utilities.generateKey;
import com.reiserx.myapplication24.databinding.ActivityLoginBinding;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase mdb;

    Admin_Accounts adminAccounts;
    Administrators admins;

    ProgressDialog prog;

    String TAG = "jfnehrjnhernf";

    SnackbarTop snackbarTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        themeApply themeApply = new themeApply(this);
        themeApply.applyTheme();

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        binding.checkBox3.setMovementMethod(LinkMovementMethod.getInstance());

        if (isNetworkAvailable(this)) {
            Thread thread = new Thread(() -> {
                try {
                    if (!isInternetAvailable()) {
                        snackbarTop.showSnackBar("No internet connection, please connect to network and retry", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        } else {
            snackbarTop.showSnackBar("No network connection, please connect to network and retry", false);
        }

        binding.button.setVisibility(View.GONE);
        binding.linearLayout3.setVisibility(View.GONE);
        binding.textView5.setVisibility(View.GONE);
        binding.textView22.setVisibility(View.VISIBLE);
        binding.progressBar3.setVisibility(View.VISIBLE);
        binding.checkBox3.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mdb = FirebaseDatabase.getInstance();

        if (Objects.requireNonNull(getSupportActionBar()).isShowing()) {
            getSupportActionBar().hide();
        }

        prog = new ProgressDialog(this);
        prog.setMessage("Processing...");
        prog.setCancelable(false);

        setTitle("");

        mdb.getReference("Administration").child("Banned").child("Devices").child(Build.ID.replace(".", "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent i = new Intent(LoginActivity.this, deactivated.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("title", "BANNED!");
                    i.putExtra("message", "You have been banned from using this service, please contact the developer if you think its a mistake.");
                    startActivity(i);
                    Log.d(TAG, "exist");
                } else {
                    Log.d(TAG, "not exist");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();

                        mdb.getReference("Administration").child("Banned").child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Intent i = new Intent(LoginActivity.this, deactivated.class);
                                    i.putExtra("title", "BANNED!");
                                    i.putExtra("message", "You have been banned from using this service, please contact the developer if you think its a mistake.");
                                    startActivity(i);
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.isEmailVerified()) {
                                        reload(uid, mdb);
                                    } else {
                                        verifyEmail();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                snackbarTop.showSnackBar(String.valueOf(error), false);
                            }
                        });
                        checkFirstRun(uid);
                    } else {
                        if (!Objects.requireNonNull(getSupportActionBar()).isShowing()) {
                            getSupportActionBar().show();
                        }
                        binding.button.setVisibility(View.VISIBLE);
                        binding.linearLayout3.setVisibility(View.VISIBLE);
                        binding.textView5.setVisibility(View.VISIBLE);
                        binding.checkBox3.setVisibility(View.VISIBLE);
                        binding.textView22.setVisibility(View.GONE);
                        binding.progressBar3.setVisibility(View.GONE);

                        Log.d(TAG, "home");
                        Log.d(TAG, "checked");
                        binding.button.setOnClickListener(v -> {
                            String email = binding.editTextTextEmailAddress.getText().toString();
                            String password = binding.editTextTextPassword.getText().toString();

                            if (!email.equals("") && !password.equals("")) {
                                if (binding.checkBox3.isChecked()) {
                                    prog.show();
                                    mdb.getReference("Administration").child("UserData").child(email.replaceAll("\\.", "")).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                adminAccounts = snapshot.getValue(Admin_Accounts.class);
                                                if (adminAccounts != null && adminAccounts.email.toLowerCase(Locale.ROOT).equals(email.toLowerCase(Locale.ROOT))) {
                                                    login(email, password);
                                                } else {
                                                    Register(email, password);
                                                }
                                            } else {
                                                Register(email, password);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            snackbarTop.showSnackBar(String.valueOf(error), false);
                                        }
                                    });
                                } else {
                                    snackbarTop.showSnackBar("Please accept our privacy policy and terms of service agreement", false);
                                }
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Please enter your details", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(String.valueOf(error), false);
            }
        });
    }

    private void login(String email, String password) {

        prog.setMessage("Login in progress...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String UserID = user != null ? user.getUid() : null;
                        processToken(UserID);
                        updateDeviceInfo(UserID);
                    } else {
                        prog.dismiss();
                        // If sign in fails, display a message to the user.
                        String error = Objects.requireNonNull(task.getException()).toString();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("Login failed");
                        dialog.setMessage(error);
                        dialog.setPositiveButton("CLOSE", (dialog1, which) -> finishAffinity());
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                });
    }

    private void Register(String email, String password) {

        prog.setMessage("Registration in progress...");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String UserID = user != null ? user.getUid() : null;
                        String email1 = user != null ? user.getEmail() : null;

                        Admin_Accounts data = new Admin_Accounts(UserID, email1);
                        if (email1 != null) {
                            mdb.getReference("Administration").child("UserData").child(email1.replaceAll("\\.", ""))
                                    .setValue(data)
                                    .addOnSuccessListener(unused -> {
                                        processToken(UserID);
                                        updateDeviceInfo(UserID);
                                    });
                        }
                    } else {
                        prog.dismiss();
                        // If sign in fails, display a message to the user.
                        String error = Objects.requireNonNull(task.getException()).toString();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("Registration failed");
                        dialog.setMessage(error);
                        dialog.setPositiveButton("CLOSE", (dialog1, which) -> finishAffinity());
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                });
    }

    public void processToken(String userID) {

        prog.setMessage("Updating data...");

        SharedPreferences loginKey = getSharedPreferences("loginKey", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = loginKey.edit();

        generateKey generateKey = new generateKey();
        String key = generateKey.randomString(20);

        myEdit.putString("key", key);
        myEdit.apply();

        deviceLogin deviceLogin = new deviceLogin(key);
        FirebaseDatabase.getInstance().getReference().child("Administration").child("Device login").child(userID).setValue(deviceLogin);

        FirebaseMessaging fm = FirebaseMessaging.getInstance();

        fm.getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    fm.subscribeToTopic("All").addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            updateToken(token, userID);
                        }
                    });
                });
    }

    private void reload(String userID, FirebaseDatabase mdb) {

        SharedPreferences loginKey = getSharedPreferences("loginKey", MODE_PRIVATE);

        mdb.getReference("Administration").child("Administrators").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prog.dismiss();
                if (snapshot.exists()) {
                    admins = snapshot.getValue(Administrators.class);
                    if (admins != null && admins.role.equals("User")) {
                        FirebaseDatabase.getInstance().getReference().child("Administration").child("Device login").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    deviceLogin deviceLogin = snapshot.getValue(com.reiserx.myapplication24.Models.deviceLogin.class);
                                    if (deviceLogin != null) {
                                        if (deviceLogin.getKey().equals(loginKey.getString("key", ""))) {
                                            SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
                                            SharedPreferences.Editor myEdit = save.edit();
                                            myEdit.putString("UserID", userID);
                                            myEdit.apply();
                                            Intent i = new Intent(LoginActivity.this, Test.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            i.putExtra("UserID", userID);
                                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                            startActivity(i);
                                            finish();
                                        } else {

                                            Log.d(TAG, "logout 1");
                                            FirebaseUser currentUser = mAuth.getCurrentUser();
                                            if (currentUser != null) {
                                                mAuth.signOut();
                                            }
                                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                            alert.setTitle("Alert");
                                            alert.setMessage("Your account is being used in another device, so you are logged out of this account");
                                            alert.setPositiveButton("close", (dialogInterface, i) -> finishAffinity());
                                            alert.setCancelable(false);
                                            alert.show();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "logout 2");
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        mAuth.signOut();
                                        finishAffinity();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else if (admins != null && admins.role.equals("Admin")) {
                        SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = save.edit();
                        myEdit.putString("UserID", userID);
                        myEdit.apply();
                        Intent i = new Intent(LoginActivity.this, Test.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("UserID", userID);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(LoginActivity.this, deactivated.class);
                        i.putExtra("title", "ACCESS DENIED!");
                        i.putExtra("message", "You don't have access to this service, contact the developer");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(LoginActivity.this, deactivated.class);
                    i.putExtra("title", "ACCESS DENIED!");
                    i.putExtra("message", "You don't have access to this service, contact the developer");
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToken(String token, String userID) {
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference document = firestore.collection("Driver").document("Users").collection(userID).document("Notification Token");
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("Notification token", token);
        document.set(tokenData)
                .addOnSuccessListener(documentReference -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        reload(userID, mdb);
                    } else {
                        verifyEmail();
                    }
                })
                .addOnFailureListener(e -> {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        auth.signOut();
                    }
                    finishAffinity();
                    Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    public void updateDeviceInfo(String userID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference document = firestore.collection("Driver").document("Users").collection(userID).document("DeviceInfo");
        deviceInfo deviceInfo = new deviceInfo(Build.SERIAL, Build.MODEL, Build.ID, Build.MANUFACTURER, Build.USER, Build.VERSION.SDK_INT, Build.VERSION.RELEASE);
        document.set(deviceInfo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset) {
            resetPassword();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void resetPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.custum_2_edittext_dialog, null);
        final EditText input = mView.findViewById(R.id.title_text);
        final EditText input2 = mView.findViewById(R.id.msg_text);
        final CheckBox notifyCheckBox = mView.findViewById(R.id.checkBox);
        final CheckBox forgroundCheckBox = mView.findViewById(R.id.checkBox2);
        final CheckBox job = mView.findViewById(R.id.job);

        alert.setMessage("Enter your email address to reset your password");
        alert.setTitle("Reset password");
        alert.setView(mView);

        input.setHint("Enter your email");

        input2.setVisibility(View.GONE);
        notifyCheckBox.setVisibility(View.GONE);
        forgroundCheckBox.setVisibility(View.GONE);
        job.setVisibility(View.GONE);

        alert.setPositiveButton("reset", (dialog, whichButton) -> {

            String title = input.getText().toString();
            Snackbar.make(findViewById(android.R.id.content), "Resetting password", Snackbar.LENGTH_LONG).show();

            if (!title.trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Password reset");
                builder.setPositiveButton("ok", null);
                mAuth.sendPasswordResetEmail(title.trim()).addOnSuccessListener(unused -> {
                    builder.setMessage("We have a password reset link to your email address, open your email app and reset your password");
                    builder.show();
                    Snackbar.make(findViewById(android.R.id.content), "Password reset complete", Snackbar.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    builder.setMessage("Password reset failed: " + e);
                    builder.show();
                    Snackbar.make(findViewById(android.R.id.content), "Password reset failed", Snackbar.LENGTH_LONG).show();

                });
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Please enter your email", Snackbar.LENGTH_LONG).show();
            }

        });

        alert.setNegativeButton("CANCEL", (dialog, whichButton) -> {
        });

        alert.show();
    }

    private void checkFirstRun(String uid) {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        if (savedVersionCode == DOESNT_EXIST) {
            updateDeviceInfo(uid);
        }
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public void verifyEmail() {
        Intent i = new Intent(LoginActivity.this, deactivated.class);
        i.putExtra("title", "VERIFY EMAIL!");
        i.putExtra("message", "Please verify your email in order to continue");
        startActivity(i);
        finish();
    }
}