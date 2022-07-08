package com.reiserx.myapplication24.Activities.Notifications;

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
import com.reiserx.myapplication24.Adapters.UsageStats.AppListAdapter;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.AppListInfo;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityRecyclerBinding;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerActivity extends AppCompatActivity {

    ActivityRecyclerBinding binding;

    ArrayList<AppListInfo> data;
    ArrayList<String> keys;
    AppListAdapter adapter;

    ArrayList<AppListInfo> dataList, filteredDataList;

    String UserID;

    FirebaseFirestore firestore;

    Query query;

    boolean status;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    boolean scrolling = false;
    LinearLayoutManager layoutManager;
    Query first;

    String TAG = "thhgshgshfs";

    QuerySnapshot check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");
        status = getIntent().getBooleanExtra("Notification", false);

        firestore = FirebaseFirestore.getInstance();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        setTitle("Apps list");

        data = new ArrayList<>();
        keys = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.rec.setLayoutManager(layoutManager);
        adapter = new AppListAdapter(this, data, UserID, keys, status);
        binding.rec.setAdapter(adapter);
        refresh(30);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void refresh(int i) {

        if (i == 0) {
            query = firestore.collection("Main").document(UserID).collection("App list")
                    .orderBy("packageName", Query.Direction.ASCENDING);
            dataList.clear();
            data.clear();
        } else {
            query = firestore.collection("Main").document(UserID).collection("App list")
                    .orderBy("packageName", Query.Direction.ASCENDING).limit(i);
        }
        query.addSnapshotListener((task, error) -> {
                    if (task != null) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                            AppListInfo cn = document.toObject(AppListInfo.class);
                            if (cn != null) {
                                cn.setAppID(document.getId());
                                data.add(cn);
                                dataList.add(cn);
                            }
                        }
                        if (!data.isEmpty()) {
                            binding.rec.setVisibility(View.VISIBLE);
                            binding.progHolder.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            setTitle("Apps list ".concat(String.valueOf(data.size())));
                        } else {
                            binding.textView9.setText("No data available");
                            binding.rec.setVisibility(View.GONE);
                            binding.progHolder.setVisibility(View.VISIBLE);
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.textView9.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        check = task;
                        scrolls(task.getDocuments()
                                .get(task.size() - 1));
                    } else {
                        binding.textView9.setText(String.valueOf(error));
                        binding.rec.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                    }
                });
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
                                    Toast.makeText(RecyclerActivity.this, "loaded all", Toast.LENGTH_SHORT).show();
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

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void ref(int limit, DocumentSnapshot lastVisible) {
        Log.d(TAG, "loading");
        if (lastVisible != null) {
            first = firestore.collection("Main").document(UserID).collection("App list")
                    .orderBy("packageName", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
        } else {
            first = firestore.collection("Main").document(UserID).collection("App list")
                    .orderBy("packageName", Query.Direction.ASCENDING).limit(limit);
        }

        first.get().addOnSuccessListener((value) -> {
            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(value.getDocuments())) {
                    AppListInfo cn = document.toObject(AppListInfo.class);
                    if (cn != null) {
                        cn.setAppID(document.getId());
                    }
                    data.add(cn);
                    dataList.add(cn);
                }
                if (!data.isEmpty()) {
                    binding.rec.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    setTitle("Apps list ".concat(String.valueOf(data.size())));
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

        searchView.setOnSearchClickListener(view -> {
            refresh(0);
        });
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
    private ArrayList<AppListInfo> filter(ArrayList<AppListInfo> dataList, String newText) {
        newText=newText.toLowerCase();
        String text;
        filteredDataList=new ArrayList<>();
        for(AppListInfo dataFromDataList:dataList){
            if (dataFromDataList.getLabel() != null) {
                text = dataFromDataList.getLabel().toLowerCase();
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_list_refresh) {
            performTask performTask = new performTask("get", 11, UserID);
            performTask.Task();
            SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
            snackbarTop.showSnackBar("Getting devices", true);
        }
        return super.onOptionsItemSelected(item);
    }
}