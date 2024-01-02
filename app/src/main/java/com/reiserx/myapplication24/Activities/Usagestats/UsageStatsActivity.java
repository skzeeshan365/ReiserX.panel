package com.reiserx.myapplication24.Activities.Usagestats;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.reiserx.myapplication24.Adapters.UsageStats.UsageStatsAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.BackwardCompatibility.RequiresVersion;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.AppUsageInfo;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityUsageStatsBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class UsageStatsActivity extends AppCompatActivity {

    ActivityUsageStatsBinding binding;

    ArrayList<AppUsageInfo> data;
    ArrayList<String> keys;
    UsageStatsAdapter adapter;

    ArrayList<AppUsageInfo> dataList, filteredDataList;

    String UserID;

    FirebaseFirestore firestore;

    SharedPreferences save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsageStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");

        firestore = FirebaseFirestore.getInstance();

        save = getSharedPreferences("UsageStats", MODE_PRIVATE);
        String time = TimeAgo.using(save.getLong(UserID, 0));
        if (!time.equals("52 years ago")) {
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(time);
        }

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        setTitle("Usage stats");

        data = new ArrayList<>();
        keys = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsageStatsAdapter(this, data);
        binding.rec.setAdapter(adapter);
        refresh();
    }
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void refresh () {

        Query query = firestore.collection("Main").document(UserID).collection("UsageStats")
                .orderBy("appName", Query.Direction.ASCENDING);
        query.addSnapshotListener((task, error) -> {
                    if (task != null) {
                        data.clear();
                        dataList.clear();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                            AppUsageInfo cn = document.toObject(AppUsageInfo.class);
                            data.add(cn);
                            dataList.add(cn);
                        }
                        if (!data.isEmpty()) {
                            binding.rec.setVisibility(View.VISIBLE);
                            binding.progHolder.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            setTitle("Usage stats ".concat(String.valueOf(data.size())));
                        } else {
                            binding.textView9.setText("No data available");
                            binding.rec.setVisibility(View.GONE);
                            binding.progHolder.setVisibility(View.VISIBLE);
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.textView9.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.textView9.setText(String.valueOf(error));
                        binding.rec.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.usage_stats_menu, menu);

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
    private ArrayList<AppUsageInfo> filter(ArrayList<AppUsageInfo> dataList, String newText) {
        newText=newText.toLowerCase();
        String text;
        filteredDataList=new ArrayList<>();
        for(AppUsageInfo dataFromDataList:dataList){
            if (dataFromDataList.getAppName() != null) {
                text = dataFromDataList.getAppName().toLowerCase();
            } else text = dataFromDataList.getPackageName().toLowerCase();

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
        } else {
            binding.rec.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        }

        return filteredDataList;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_list_refresh:
                RequiresVersion requiresVersion = new RequiresVersion(this, UserID);
                if (requiresVersion.Requires("2.7")) {
                    SharedPreferences.Editor myEdit = save.edit();
                    Date date = new Date();
                    date.getTime();
                    myEdit.putLong(UserID, date.getTime());
                    myEdit.apply();
                    performTask performTask = new performTask("get", 12, UserID);
                    performTask.Task();
                    SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
                    snackbarTop.showSnackBar("Getting usage stats", true);
                    InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                    interstitialAdsClass.loadAds();
                }
                break;
            case R.id.usage_stats_info:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Usage stats");
                dialog.setMessage("You can get app usage stats of all apps in target device for last 24 hours");
                dialog.setPositiveButton("ok", null);
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}