package com.reiserx.myapplication24.Activities.Directories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Directories.FoldersAdapter;
import com.reiserx.myapplication24.Classes.AdminAccess;
import com.reiserx.myapplication24.Models.Folders;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityOpenFilesBinding;

import java.util.ArrayList;

public class FoldersActivity extends AppCompatActivity {

    ActivityOpenFilesBinding binding;

    ArrayList<Folders> data;
    ArrayList<String> list;
    FoldersAdapter adapter;

    String UserID;
    String path;

    ArrayList<Folders> dataList, filteredDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        UserID = getIntent().getStringExtra("UserID");
        path = getIntent().getStringExtra("Path");

        setTitle(path);

        SharedPreferences save = getSharedPreferences("Userss", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putString("UserID", UserID);
        myEdit.apply();

        data = new ArrayList<>();
        list = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoldersAdapter(this, data, list);
        binding.rec.setAdapter(adapter);

        String path1 = path.replace(".", "");
                    database.getReference("Main").child(UserID).child("Folders").child("Files").child("Primary Folder").child(path1).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            data.clear();
                            dataList.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Folders folders = snapshot1.getValue(Folders.class);
                                if (folders != null && folders.getFolder() != null) {
                                    data.add(folders);
                                    dataList.add(folders);
                                    list.add(snapshot1.getKey());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_files:
                Intent intent = new Intent(this, checkDetails.class);
                intent.putExtra("Path", path);
                intent.putExtra("Message", path);
                intent.putExtra("UserID", UserID);
                startActivity(intent);
                break;
            case R.id.add_data:
                AdminAccess access = new AdminAccess(this);
                if (access.fileUploadAccess()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Add data");
                    alert.setMessage("Please choose Add file or create folder");
                    alert.setPositiveButton("Add file", (dialogInterface, i) -> {
                        Intent addFile = new Intent(this, UploadFilesActivity.class);
                        addFile.putExtra("Path", path);
                        addFile.putExtra("UserID", UserID);
                        startActivity(addFile);
                    });
                    alert.setNegativeButton("create folder", (dialogInterface, i) -> CreateFolder());
                    alert.setNeutralButton("cancel", null);
                    alert.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CreateFolder() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.simple_edittext_dialog,null);
        final EditText input = mView.findViewById(R.id.editTextNumber);

        alert.setMessage("Enter folder name");
        alert.setTitle("Create folder");
        alert.setView(mView);

        alert.setPositiveButton("Create", (dialog, whichButton) -> {
            //What ever you want to do with the value
            if (!input.getText().toString().equals("")) {
                String data = input.getText().toString();
                performTask performTask = new performTask(path, data.trim(), 13, UserID);
                performTask.Task();
            } else Toast.makeText(FoldersActivity.this, "Please enter the code", Toast.LENGTH_SHORT).show();
        });
        alert.setNegativeButton("cancel", null);
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

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
                        filteredDataList = filter(dataList, newText);
                        adapter.setFilter(filteredDataList);
                        return false;
                    }
                });

        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("SetTextI18n")
    private ArrayList<Folders> filter(ArrayList<Folders> dataList, String newText) {
        newText=newText.toLowerCase();
        String text;
        filteredDataList=new ArrayList<>();
        for(Folders dataFromDataList:dataList){
            text=dataFromDataList.getFolder().toLowerCase();
            if(text.contains(newText)){
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }
}