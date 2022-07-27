package com.reiserx.myapplication24.Activities.Python;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityPythonBinding;

public class Python extends AppCompatActivity {

    ActivityPythonBinding binding;

    SnackbarTop snackbarTop;

    String UserID;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPythonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserID = getIntent().getStringExtra("UserID");
        setTitle("Python");

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        binding.codeView.setEnableAutoIndentation(true);
        binding.codeView.setEnableLineNumber(true);
        binding.codeView.setLineNumberTextColor(Color.GRAY);
        binding.codeView.setLineNumberTextSize(35f);
        binding.codeView.setLineNumberTypeface(Typeface.MONOSPACE);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_python:
                performTask performTask = new performTask(binding.codeView.getText().toString(), 22, UserID);
                performTask.Task();
                hideKeyboard(Python.this);
                snackbarTop.showSnackBar("Code sent, please wait for output", true);
                addListener();
                break;
            case R.id.python_info:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Info");
                alert.setMessage("You can run a python script on ReiserX driver");
                alert.setPositiveButton("ok", null);
                alert.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.python_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void addListener () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("Python").child("output");
        if (valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
        valueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value != null) {
                    binding.textView25.setText(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}