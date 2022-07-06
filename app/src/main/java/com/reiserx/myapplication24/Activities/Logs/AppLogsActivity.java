package com.reiserx.myapplication24.Activities.Logs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.reiserx.myapplication24.Activities.ParentActivities.ScanQr;
import com.reiserx.myapplication24.Activities.ParentActivities.Test;
import com.reiserx.myapplication24.Classes.FileUtil;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.LogFileModel;
import com.reiserx.myapplication24.Models.Users;
import com.reiserx.myapplication24.Models.callLogs;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.Utilities.CONSTANTS;
import com.reiserx.myapplication24.databinding.ActivityAppLogsBinding;

import java.util.Locale;
import java.util.Objects;

public class AppLogsActivity extends AppCompatActivity {

    ActivityAppLogsBinding binding;

    String lines;

    String UserID;

    String TAG = "hfhfhuisjhgjgghihhf";

    LogFileModel logFileModel;

    SnackbarTop snackbarTop;

    SharedPreferences save;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserID = getIntent().getStringExtra("UserID");
        setTitle("App logs");

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));



        save = getSharedPreferences("Tokens", MODE_PRIVATE);
        myEdit = save.edit();
        binding.textView21.setTextSize(save.getInt("size", 12));

        DocumentReference documents = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("Logs").document("LogId");
        documents.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                snackbarTop.showSnackBar("Listen failed.", false);
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: " + snapshot.getData());
                logFileModel = snapshot.toObject(LogFileModel.class);
                binding.textView21.setText(logFileModel.getData());
                Objects.requireNonNull(getSupportActionBar()).setSubtitle(TimeAgo.using(logFileModel.getTimestamp()));
            } else {
                snackbarTop.showSnackBar("Current data: null", false);
                Log.d(TAG, "Current data: null");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logfile_menu, menu);

        MenuItem searchViewItem
                = menu.findItem(R.id.app_bar_search);
        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText)
                    {

                        String textToFind = newText.trim();
                        String fullTxt = logFileModel.getData();

                        SpannableString spannable = new SpannableString(fullTxt);

                        final int index = fullTxt.indexOf(textToFind);
                        if(index == -1) {
                            // text does not contain the word
                            snackbarTop.showSnackBar("Text '" + textToFind + "' not found.", false);
                        }
                        else {
                            int lineNum = binding.textView21.getLayout().getLineForOffset(index);
                            int lineStart = binding.textView21.getLayout().getLineEnd(lineNum -1);
                            int lineEnd = binding.textView21.getLayout().getLineEnd(lineNum);
                            // set style to the entire line, as your origional code
                            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), lineStart, lineEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new StyleSpan(Typeface.BOLD), lineStart, lineEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            binding.textView21.setText(spannable);
                            binding.textView21.post(() -> {
                                int line = binding.textView21.getLayout().getLineForOffset(index);
                                int y = binding.textView21.getLayout().getLineTop(line);
                                binding.scrollView2.scrollTo(0, y);
                            });
                        }
                        return false;
                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logfile_get:
                selectLines(UserID);
                break;
            case R.id.logfile_save:
                saveLogFile();
                break;
            case R.id.logfile_fontsize:
                fontsize();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLogFile() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.edittext_dialog,null);
        final EditText input = mView.findViewById(R.id.editTextNumber);

        alert.setMessage("Enter filename please");
        alert.setTitle("Save log file to storage");
        alert.setView(mView);

        input.setText(logFileModel.getTimestamp()+".txt");

        alert.setPositiveButton("save", (dialog, whichButton) -> {
            if (!input.getText().toString().trim().equals("")) {
                String data = input.getText().toString();
                FileUtil.writeFile(CONSTANTS.textFiles()+"/"+data, binding.textView21.getText().toString());
                snackbarTop.showSnackBar("File saved at "+CONSTANTS.textFiles()+"/"+data, true);
            } else snackbarTop.showSnackBar("Please type file name", false);
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }

    public void selectLines(String UserID) {
        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog_2, null);
        final RadioButton btn1 = mView.findViewById(R.id.radioButton1);
        final RadioButton btn2 = mView.findViewById(R.id.radioBtn2);
        final RadioButton btn3 = mView.findViewById(R.id.radioBtn3);
        final RadioButton btn4 = mView.findViewById(R.id.radioBtn4);

        alert.setTitle("Log lines");
        alert.setMessage("Please select number of lines in logfile");
        alert.setView(mView);

        btn1.setText("50");
        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                lines = "50";
            }
        });

        btn2.setText("100");
        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                lines = "100";
            }
        });

        btn3.setText("200");
        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                lines = "200";
            }
        });

        btn4.setText("500");
        btn4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn4.isChecked()) {
                lines = "500";
            }
        });

        alert.setPositiveButton("fetch", (dialog, whichButton) -> {
            if (lines == null) {
                snackbarTop.showSnackBar("Please select file type", false);
            } else {
                performTask performTask = new performTask(lines, 21, UserID);
                performTask.Task();
                snackbarTop.showSnackBar("Request sent", true);
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }

    public void fontsize() {
        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog alert = new AlertDialog.Builder(this).create();

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog_2, null);
        final RadioButton btn1 = mView.findViewById(R.id.radioButton1);
        final RadioButton btn2 = mView.findViewById(R.id.radioBtn2);
        final RadioButton btn3 = mView.findViewById(R.id.radioBtn3);
        final RadioButton btn4 = mView.findViewById(R.id.radioBtn4);

        alert.setTitle("Font size");
        alert.setMessage("Please select Font size");
        alert.setView(mView);

        switch (save.getInt("size", 12)) {
            case 10:
                btn1.setChecked(true);
                break;
            case 12:
                btn2.setChecked(true);
                break;
            case 14:
                btn3.setChecked(true);
                break;
            case 18:
                btn4.setChecked(true);
                break;
            default:
                btn2.setChecked(true);
                break;
        }

        btn1.setText("10dp");
        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                binding.textView21.setTextSize(10);
                alert.dismiss();
                myEdit.putInt("size", 10);
                myEdit.apply();
            }
        });

        btn2.setText("12dp");
        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                binding.textView21.setTextSize(12);
                alert.dismiss();
                myEdit.putInt("size", 12);
                myEdit.apply();
            }
        });

        btn3.setText("14dp");
        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                binding.textView21.setTextSize(14);
                alert.dismiss();
                myEdit.putInt("size", 14);
                myEdit.apply();
            }
        });

        btn4.setText("18dp");
        btn4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn4.isChecked()) {
                binding.textView21.setTextSize(18);
                alert.dismiss();
                myEdit.putInt("size", 18);
                myEdit.apply();
            }
        });

        alert.show();
    }
}