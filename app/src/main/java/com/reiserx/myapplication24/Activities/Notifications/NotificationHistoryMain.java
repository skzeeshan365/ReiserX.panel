package com.reiserx.myapplication24.Activities.Notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.myapplication24.Adapters.Notifications.NotificationHistoryMainAdapter;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Models.NotificationPath;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityNotificationHistoryMainBinding;

import java.util.ArrayList;

public class NotificationHistoryMain extends AppCompatActivity {

    ActivityNotificationHistoryMainBinding binding;

    ArrayList<NotificationPath> data;
    NotificationHistoryMainAdapter adapter;

    ArrayList<NotificationPath> dataList, filteredDataList;

    String UserID;

    FirebaseFirestore firestore;

    boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationHistoryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");
        String refrence = getIntent().getStringExtra("Path");

        firestore = FirebaseFirestore.getInstance();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.button5.setVisibility(View.GONE);

        setTitle("App notifications");

        status = refrence.contains("AppName");

        DatabaseReference references = FirebaseDatabase.getInstance().getReference().child(refrence);

        data = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationHistoryMainAdapter(this, data, UserID);
        binding.rec.setAdapter(adapter);
        refresh(references);
    }

    public void refresh (DatabaseReference reference) {

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        data.clear();
                        snapshot.exists();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            NotificationPath u = snapshot1.getValue(NotificationPath.class);
                            data.add(u);
                            dataList.add(u);
                        }
                        if (!data.isEmpty()) {
                            binding.rec.setVisibility(View.VISIBLE);
                            binding.progHolder.setVisibility(View.GONE);
                            binding.rec.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.textView9.setText("No notifications, add apps to receive notifications");
                            binding.rec.setVisibility(View.GONE);
                            binding.progHolder.setVisibility(View.VISIBLE);
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.textView9.setVisibility(View.VISIBLE);
                            binding.button5.setVisibility(View.VISIBLE);
                            binding.button5.setOnClickListener(view -> {
                                Intent intent = new Intent(NotificationHistoryMain.this, RecyclerActivity.class);
                                intent.putExtra("UserID", UserID);
                                intent.putExtra("Notification", true);
                                startActivity(intent);
                            });
                        }
                    } else {
                        binding.textView9.setText("No notifications, add apps to receive notifications");
                        binding.rec.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                        binding.button5.setVisibility(View.VISIBLE);
                        binding.button5.setOnClickListener(view -> {
                            Intent intent = new Intent(NotificationHistoryMain.this, RecyclerActivity.class);
                            intent.putExtra("UserID", UserID);
                            intent.putExtra("Notification", true);
                            startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.auto_delete_location) {
            Intent intent = new Intent(this, RecyclerActivity.class);
            intent.putExtra("UserID", UserID);
            intent.putExtra("Notification", true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("SetTextI18n")
    private ArrayList<NotificationPath> filter(ArrayList<NotificationPath> dataList, String newText) {
        newText=newText.toLowerCase();
        String text;
        filteredDataList=new ArrayList<>();
        for(NotificationPath dataFromDataList:dataList){
            text= dataFromDataList.getName().toLowerCase();

            if(text.contains(newText)){
                filteredDataList.add(dataFromDataList);
            }
        }
        if (filteredDataList.isEmpty()) {
            binding.textView9.setText("No data available");
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
            binding.button5.setVisibility(View.GONE);
        } else {
            binding.rec.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        }

        return filteredDataList;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notify_menu, menu);

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
}