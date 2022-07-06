package com.reiserx.myapplication24.Activities.Directories;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Directories.directorysAdapter;
import com.reiserx.myapplication24.Models.Folders;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityDirectorysBinding;

import java.util.ArrayList;

public class DirectorysActivity extends AppCompatActivity {

    ActivityDirectorysBinding binding;

    private FirebaseDatabase database;

    ArrayList<Folders> data;
    ArrayList<String> list;
    directorysAdapter adapter;

    ArrayList<Folders> dataList, filteredDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectorysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();

        String UserID = getIntent().getStringExtra("UserID");

        SharedPreferences save = getSharedPreferences("Userss", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putString("UserID", UserID);
        myEdit.apply();

        setTitle("Directory");

        data = new ArrayList<>();
        list = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new directorysAdapter(this, data, list);
        binding.recyclerView.setAdapter(adapter);

        database.getReference("Main").child(UserID).child("Directorys").orderByChild("folder").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Folders folders = snapshot1.getValue(Folders.class);
                    data.add(folders);
                    dataList.add(folders);
                    list.add(snapshot1.getKey());
                    database.getReference(UserID).child("Task")
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                            });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchs, menu);

        MenuItem searchViewItem
                = menu.findItem(R.id.search_bar);
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