package com.reiserx.myapplication24.Activities.Notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.reiserx.myapplication24.Adapters.Notifications.notification_history_adapter;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.NotificationModel;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityNotificationHistoryBinding;

import java.util.ArrayList;
import java.util.Objects;

public class notification_history extends AppCompatActivity {

    ActivityNotificationHistoryBinding binding;

    ArrayList<NotificationModel> data;
    notification_history_adapter adapter;

    ArrayList<NotificationModel> dataList, filteredDataList;
    ArrayList<String> keys;

    FirebaseFirestore firestore;
    String UserID, AppName, Titles;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    boolean scrolling = false;
    LinearLayoutManager layoutManager;
    Query first;

    String TAG = "thhgshgshfs";

    QuerySnapshot check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");
        AppName = getIntent().getStringExtra("AppName");
        Titles = getIntent().getStringExtra("Title");

        firestore = FirebaseFirestore.getInstance();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.button5.setVisibility(View.GONE);

        setTitle(Titles);

        data = new ArrayList<>();
        keys = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.rec.setLayoutManager(layoutManager);
        adapter = new notification_history_adapter(this, data, UserID, keys, AppName, Titles);
        binding.rec.setAdapter(adapter);
        refresh();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void refresh () {
        Query query = firestore.collection("Main").document(UserID).collection("Notifications").document(AppName).collection(Titles).orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
                query.addSnapshotListener((task, error) -> {
                    if (task != null && !task.isEmpty()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                            NotificationModel cn = document.toObject(NotificationModel.class);
                            data.add(cn);
                            dataList.add(cn);
                            keys.add(document.getId());
                        }
                        if (!data.isEmpty()) {
                            binding.rec.setVisibility(View.VISIBLE);
                            binding.progHolder.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            setTitle(Titles.concat(" ".concat(String.valueOf(data.size()))));
                        } else {
                            binding.textView9.setText("No notifications, add apps to receive notifications");
                            binding.rec.setVisibility(View.GONE);
                            binding.progHolder.setVisibility(View.VISIBLE);
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.textView9.setVisibility(View.VISIBLE);
                            binding.button5.setVisibility(View.VISIBLE);
                            binding.button5.setOnClickListener(view -> {
                                Intent intent = new Intent(this, RecyclerActivity.class);
                                intent.putExtra("UserID", UserID);
                                intent.putExtra("Notification", true);
                                startActivity(intent);
                            });
                        }
                        check = task;
                        scrolls(task.getDocuments()
                                .get(task.size() - 1));
                    } else {
                        binding.textView9.setText("No notifications, add apps to receive notifications");
                        binding.rec.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                        binding.button5.setVisibility(View.VISIBLE);
                        binding.button5.setOnClickListener(view -> {
                            Intent intent = new Intent(this, RecyclerActivity.class);
                            intent.putExtra("UserID", UserID);
                            intent.putExtra("Notification", true);
                            startActivity(intent);
                        });
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
                                    Toast.makeText(notification_history.this, "loaded all", Toast.LENGTH_SHORT).show();
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

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void ref(int limit, DocumentSnapshot lastVisible) {
        Log.d(TAG, "loading");
        if (lastVisible != null) {
            first =firestore.collection("Main").document(UserID).collection("Notifications").document(AppName).collection(Titles).orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
        } else {
            firestore.collection("Main").document(UserID).collection("Notifications").document(AppName).collection(Titles).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit);
        }

        first.get().addOnSuccessListener((task) -> {
            if (task != null && !task.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                    NotificationModel cn = document.toObject(NotificationModel.class);
                    data.add(cn);
                    dataList.add(cn);
                    keys.add(document.getId());
                }
                if (!data.isEmpty()) {
                    binding.rec.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    setTitle(Titles.concat(" ".concat(String.valueOf(data.size()))));
                } else {
                    binding.textView9.setText("No notifications, add apps to receive notifications");
                    binding.rec.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                    binding.button5.setVisibility(View.VISIBLE);
                    binding.button5.setOnClickListener(view -> {
                        Intent intent = new Intent(this, RecyclerActivity.class);
                        intent.putExtra("UserID", UserID);
                        intent.putExtra("Notification", true);
                        startActivity(intent);
                    });
                }
                scrolls(task.getDocuments()
                        .get(task.size() - 1));
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
    private ArrayList<NotificationModel> filter(ArrayList<NotificationModel> dataList, String newText) {
        newText=newText.toLowerCase();
        String text, title;
        filteredDataList=new ArrayList<>();
        for(NotificationModel dataFromDataList:dataList){
            text= dataFromDataList.getText().toLowerCase();
            title = dataFromDataList.getTitle().toLowerCase();

            if(title.contains(newText) || text.contains(newText)){
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