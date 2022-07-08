package com.reiserx.myapplication24.Activities.Calls;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.myapplication24.Adapters.Contacts.callogsAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.BackwardCompatibility.RequiresVersion;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.callLogs;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityCallogsBinding;

import java.util.ArrayList;
import java.util.Objects;

public class callogs extends AppCompatActivity {

    ActivityCallogsBinding binding;

    ArrayList<callLogs> data;
    callogsAdapter adapter;

    ArrayList<callLogs> dataList, filteredDataList;

    String TAG = "callLogsData";

    String UserID;

    int pastVisiblesItems, totalItemCount, first_visible_item;

    FirebaseFirestore firestore;

    LinearLayoutManager layoutManager;
    Query first;

    boolean scrolling = false;

    QuerySnapshot check;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        UserID = getIntent().getStringExtra("UserID");

        RequiresVersion requiresVersion = new RequiresVersion(this, UserID);

        setTitle("Call logs");

        if (requiresVersion.Requires(2.6f)) {
            data = new ArrayList<>();
            dataList = new ArrayList<>();
            filteredDataList = new ArrayList<>();
            layoutManager = new LinearLayoutManager(this);
            binding.rec.setLayoutManager(layoutManager);
            adapter = new callogsAdapter(this, data);
            binding.rec.setAdapter(adapter);

            first = firestore.collection("Main").document(UserID).collection("Call Logs")
                    .orderBy("dateinsecs", Query.Direction.DESCENDING).limit(30);

            first.addSnapshotListener((value, error) -> {
                if (value != null && !value.isEmpty()) {
                    data.clear();
                    for (DocumentSnapshot document : Objects.requireNonNull(value.getDocuments())) {
                        callLogs cn = document.toObject(callLogs.class);
                        data.add(cn);
                        dataList.add(cn);
                    }
                    if (!data.isEmpty()) {
                        binding.rec.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        setTitle("Call logs ".concat(String.valueOf(data.size())));
                    } else {
                        binding.textView9.setText("No data available");
                        binding.rec.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    check = value;
                    scrolls(value.getDocuments()
                            .get(value.size() - 1));
                } else {
                    binding.textView9.setText(String.valueOf("No data available\n" + error));
                    binding.rec.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Log.d("iufghirnfriwgn", "false ac");
        }

    }

    public void scrolls(DocumentSnapshot lastVisibles) {
        binding.rec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { //check for scroll down
                    first_visible_item = layoutManager.findFirstVisibleItemPosition();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findLastVisibleItemPosition();

                    if ((first_visible_item + pastVisiblesItems) >= totalItemCount) {
                        if (!scrolling) {
                            scrolling = true;
                            if (check != null) {
                                if (check.size()-1 < 9) {
                                    Toast.makeText(callogs.this, "loaded all", Toast.LENGTH_SHORT).show();
                                } else {
                                    ref(10, lastVisibles);
                                    Log.d(TAG, "load");
                                }
                            } else Log.d(TAG, "jidsnf");
                        } else Log.d(TAG, "not scrolling");
                    } else {
                        Log.d(TAG, "total: "+totalItemCount);
                        Log.d(TAG, "first: "+first_visible_item);
                        Log.d(TAG, "last: "+pastVisiblesItems);
                    }
                }
            }
        });
    }

    public void ref(int limit, DocumentSnapshot lastVisible) {
        Log.d(TAG, "loading");
        if (lastVisible != null) {
            first = firestore.collection("Main").document(UserID).collection("Call Logs")
                    .orderBy("dateinsecs", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
        } else {
            first = firestore.collection("Main").document(UserID).collection("Call Logs")
                    .orderBy("dateinsecs", Query.Direction.DESCENDING).limit(limit);
        }

        first.get().addOnSuccessListener((value) -> {
            if (value != null) {
                for (DocumentSnapshot document : Objects.requireNonNull(value.getDocuments())) {
                    callLogs cn = document.toObject(callLogs.class);
                    data.add(cn);
                    dataList.add(cn);
                }
                if (!data.isEmpty()) {
                    binding.rec.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    setTitle("Call logs ".concat(String.valueOf(data.size())));
                } else {
                    binding.textView9.setText("No data available");
                    binding.rec.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                scrolls(value.getDocuments()
                        .get(value.size() - 1));
                scrolling = false;
            } else {
                    SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
                    snackbarTop.showSnackBar("Loaded all data", true);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_list_menu, menu);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_list_refresh:
                performTask performTask = new performTask("get", 8, UserID);
                performTask.Task();
                SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
                snackbarTop.showSnackBar("Getting call logs", true);
                InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                interstitialAdsClass.loadAds();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private ArrayList<callLogs> filter(ArrayList<callLogs> dataList, String newText) {
        newText=newText.toLowerCase();
        String text;
        filteredDataList=new ArrayList<>();
        for(callLogs dataFromDataList:dataList){
            text=dataFromDataList.getPhoneNumber().toLowerCase();

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
}