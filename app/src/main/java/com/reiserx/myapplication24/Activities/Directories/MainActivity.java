package com.reiserx.myapplication24.Activities.Directories;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Directories.ImageAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Models.FileUpload;
import com.reiserx.myapplication24.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseDatabase database;

    ArrayList<FileUpload> url;
    ImageAdapter imageAdapter;

    boolean mIsLoading = false;

    String TAG = "ihfsifh";
    int pastVisiblesItems, totalItemCount, first_visible_item;
    LinearLayoutManager layoutManager;

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
        interstitialAdsClass.loadAds();

        String UserID = getIntent().getStringExtra("UserID");
        String getPath = getIntent().getStringExtra("Path");
        String path = getPath.replace(".", "");
        Log.d("hthfgjyj", getPath);

        setTitle(path);

        url = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        imageAdapter = new ImageAdapter(this, url, UserID, getPath, findViewById(android.R.id.content));
        binding.recyclerView.setAdapter(imageAdapter);

        getUsers(null, UserID, path);
        scrolls(UserID, path);
    }

    private void getUsers(String nodeId, String UserID, String path) {
        Query query;

        if (nodeId == null)
            query = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("Upload").child("Images").child(path.toLowerCase(Locale.ROOT))
                    .orderByKey()
                    .limitToFirst(3);
        else
            query = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("Upload").child("Images").child(path.toLowerCase(Locale.ROOT))
                    .orderByKey()
                    .startAfter(nodeId)
                    .limitToFirst(3);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                    FileUpload imagedata = snapshot1.getValue(FileUpload.class);
                    imagedata.setDatabase_ID(snapshot1.getKey());
                    url.add(imagedata);
                }
                imageAdapter.notifyDataSetChanged();
                mIsLoading = false;
                index = url.size()-1;
                setTitle(String.valueOf(url.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }

    public void scrolls(String UserID, String path) {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { //check for scroll down
                    first_visible_item = layoutManager.findFirstVisibleItemPosition();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findLastVisibleItemPosition();

                    if ((first_visible_item + pastVisiblesItems) >= totalItemCount) {
                        if (!mIsLoading) {
                            mIsLoading = true;

                            FileUpload imagedata = url.get(index);
                            getUsers(imagedata.id, UserID, path);
                            Log.d(TAG, "loading");
                        } else {
                            Log.d(TAG, "total: " + totalItemCount);
                            Log.d(TAG, "first: " + first_visible_item);
                            Log.d(TAG, "last: " + pastVisiblesItems);
                        }
                    }
                }
            }
        });
    }
}