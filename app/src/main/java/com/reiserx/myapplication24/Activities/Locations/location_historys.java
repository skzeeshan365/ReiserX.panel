package com.reiserx.myapplication24.Activities.Locations;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.myapplication24.Adapters.Location.location_history_adapter;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.locationModel;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityLocationHistorysBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class location_historys extends AppCompatActivity {

    ActivityLocationHistorysBinding binding;

    FirebaseFirestore firestore;

    ArrayList<locationModel> data;
    location_history_adapter adapter;

    ArrayList<locationModel> dataList, filteredDataList;

    String TAG = "LocationHistory";

    String UserID;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    boolean scrolling = false;
    LinearLayoutManager layoutManager;
    Query first;

    QuerySnapshot check;

    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationHistorysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        UserID = getIntent().getStringExtra("UserID");

        setTitle("Location history");

        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.rec.setLayoutManager(layoutManager);

        adapter = new location_history_adapter(this, data, findViewById(android.R.id.content));
        binding.rec.setAdapter(adapter);

        Query query = firestore.collection("Main").document(UserID).collection("Location")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);

        query.get().addOnSuccessListener(task -> {
                    if (task != null && !task.isEmpty()) {
                        data.clear();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                            locationModel cn = document.toObject(locationModel.class);
                            data.add(cn);
                            dataList.add(cn);
                        }
                        adapter.notifyItemInserted(0);
                        setTitle("Location history "+data.size());
                        check = task;
                        scrolls(task.getDocuments()
                                .get(task.size() - 1));
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
                                    Toast.makeText(location_historys.this, "loaded all", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("NotifyDataSetChanged")
    public void ref(int limit, DocumentSnapshot lastVisible) {
        Log.d(TAG, "loading");
        if (lastVisible != null) {
            first = firestore.collection("Main").document(UserID).collection("Location")
                    .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
        } else {
            firestore.collection("Main").document(UserID).collection("Location")
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(limit);
        }

        first.get().addOnSuccessListener((value) -> {
            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(value.getDocuments())) {
                    locationModel cn = document.toObject(locationModel.class);
                    data.add(cn);
                    dataList.add(cn);
                }
                adapter.notifyDataSetChanged();
                setTitle("Location history "+data.size());
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
        inflater.inflate(R.menu.location_history_menu, menu);

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
        if (item.getItemId() == R.id.auto_delete_location) {
            autoDelete(UserID);
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("SetTextI18n")
    private ArrayList<locationModel> filter(ArrayList<locationModel> dataList, String newText) {
        newText=newText.toLowerCase();
        String text;
        filteredDataList=new ArrayList<>();
        for(locationModel dataFromDataList:dataList){
            text = TimeAgo.using(dataFromDataList.getTimestamp());

            if(text.contains(newText)){
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getData(String urls) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urls)
                        .build();
                Response responses = null;

                responses = client.newCall(request).execute();

                String jsonDataString = responses.body().string();
                JSONArray jsonArray;
                try {
                    if (jsonDataString.startsWith("[") && jsonDataString.endsWith("]")) {
                        jsonArray = new JSONArray(jsonDataString);
                    } else {
                        jsonArray = new JSONArray("[" + jsonDataString + "]");
                    }

                    for (int i = 0; i < jsonArray.length(); ++i) {

                        JSONObject itemObj = jsonArray.getJSONObject(i);

                        String latitude = itemObj.getString("latitude");
                        String longitude = itemObj.getString("longitude");
                        long timestamp = itemObj.getLong("timestamp");
                        Log.d(TAG, "vfsg");
                        locationModel datas = new locationModel(longitude, latitude, timestamp);
                        data.add(datas);
                        dataList.add(datas);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.toString());
                }
            } catch (
                    IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            handler.post(() -> adapter.notifyDataSetChanged());
        });
    }

    @SuppressLint("SetTextI18n")
    public void autoDelete(String UserID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("AutoDeleteLocation");

        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog, null);
        final RadioButton days3 = mView.findViewById(R.id.radioButton2);
        final RadioButton week = mView.findViewById(R.id.radioButton);
        final RadioButton month = mView.findViewById(R.id.radioButton3);
        final RadioButton never = mView.findViewById(R.id.radioButton4);

        final TextView low = mView.findViewById(R.id.textView3);
        final TextView med = mView.findViewById(R.id.textView10);
        final TextView high = mView.findViewById(R.id.textView11);
        low.setVisibility(View.GONE);
        med.setVisibility(View.GONE);
        high.setVisibility(View.GONE);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int value = snapshot.getValue(int.class);
                    switch (value) {

                        case 1:
                            days3.setChecked(true);
                            break;
                        case 2:
                            week.setChecked(true);
                            break;
                        case 3:
                            month.setChecked(true);
                            break;
                        case 4:
                            never.setChecked(true);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alert.setTitle("Auto delete location history");
        alert.setMessage("Please select time period for auto deleting location history");
        alert.setView(mView);

        days3.setText("3 days");
        days3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (days3.isChecked()) {
                duration = 1;
                week.setChecked(false);
                month.setChecked(false);
                never.setChecked(false);
            }
        });

        week.setText("7 days");
        week.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (week.isChecked()) {
                duration = 2;
                days3.setChecked(false);
                month.setChecked(false);
                never.setChecked(false);
            }
        });

        month.setText("1 month");
        month.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (month.isChecked()) {
                duration = 3;
                days3.setChecked(false);
                week.setChecked(false);
                never.setChecked(false);
            }
        });
        never.setText("Never");
        never.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (never.isChecked()) {
                duration = 4;
                days3.setChecked(false);
                week.setChecked(false);
                month.setChecked(false);
            }
        });

        alert.setPositiveButton("save", (dialog, whichButton) -> {
            if (duration == 0) {
                snackbarTop.showSnackBar("Please select time period", false);
            } else {
                reference.setValue(duration);
                snackbarTop.showSnackBar("Saved", true);
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }
}