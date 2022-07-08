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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.myapplication24.Adapters.Contacts.contactAdapter;
import com.reiserx.myapplication24.Advertisements.InterstitialAdsClass;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.contacts_lists;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ActivityContactsBinding;

import java.util.ArrayList;
import java.util.Objects;

public class contacts extends AppCompatActivity {

    ActivityContactsBinding binding;
    FirebaseDatabase database;
    FirebaseFirestore firestore;

    ArrayList<contacts_lists> data;
    contactAdapter adapter;

    String TAG = "ContactsPage";

    String UserID;

    ArrayList<contacts_lists> dataList, filteredDataList;

    int pastVisiblesItems, totalItemCount, first_visible_item;
    LinearLayoutManager layoutManager;
    Query first;

    boolean scrolling = false;

    QuerySnapshot check;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recs.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();

        UserID = getIntent().getStringExtra("UserID");

        setTitle("Contacts");

//        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("Contacts").child("Contacts.json");
//        reference.getDownloadUrl().addOnSuccessListener(uri -> {
//            getData(uri.toString());
//        });

        bannerAdsClass bannerAdsClass = new bannerAdsClass(this, binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.recs.setLayoutManager(layoutManager);
        adapter = new contactAdapter(this, data);
        binding.recs.setAdapter(adapter);
        Log.d(TAG, "loading");
            first = firestore.collection("Main").document(UserID).collection("Contacts")
                    .orderBy("name", Query.Direction.ASCENDING).limit(30);

        first.addSnapshotListener((queryDocumentSnapshots, error) -> {
                data.clear();
                for (DocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots.getDocuments())) {
                    contacts_lists cn = document.toObject(contacts_lists.class);
                    data.add(cn);
                    dataList.add(cn);
                }
                adapter.notifyItemInserted(pastVisiblesItems);
                if (!data.isEmpty()) {
                    binding.recs.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    setTitle("Contacts ".concat(String.valueOf(data.size())));
                    scroll(queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1));
                    check = queryDocumentSnapshots;
                } else {
                    binding.textView9.setText("No data available\n"+error);
                    binding.recs.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                }
        });
    }

    public void ref (int limit, DocumentSnapshot lastVisibles) {
        Log.d(TAG, "loading");
        if (lastVisibles != null) {
            first = firestore.collection("Main").document(UserID).collection("Contacts")
                    .orderBy("name", Query.Direction.ASCENDING).startAfter(lastVisibles).limit(limit);
        } else {
            first = firestore.collection("Main").document(UserID).collection("Contacts")
                    .orderBy("name", Query.Direction.ASCENDING).limit(limit);
        }

        first.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots.getDocuments())) {
                    contacts_lists cn = document.toObject(contacts_lists.class);
                    data.add(cn);
                    dataList.add(cn);
                }
                adapter.notifyItemInserted(pastVisiblesItems);
                if (!data.isEmpty()) {
                    binding.recs.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    setTitle("Contacts ".concat(String.valueOf(data.size())));
                } else {
                    binding.textView9.setText("No data available");
                    binding.recs.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                }
                scroll(queryDocumentSnapshots.getDocuments()
                        .get(queryDocumentSnapshots.size() - 1));
                scrolling = false;
            } else {
                SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
                snackbarTop.showSnackBar("Loaded all messages", true);
            }
        }).addOnFailureListener(e -> {
            binding.textView9.setText(String.valueOf(e));
            Log.e(TAG, String.valueOf(e));
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        });

    }

    public void scroll(DocumentSnapshot documentSnapshot) {
        binding.recs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { //check for scroll down
                    first_visible_item = layoutManager.findFirstVisibleItemPosition();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.getChildCount();

                    Log.d(TAG, "total: "+totalItemCount);

                    if ((first_visible_item + pastVisiblesItems) == totalItemCount) {
                        if (!scrolling) {
                            scrolling = true;
                            if (check != null) {
                                if (check.size()-1 < 9) {
                                    Toast.makeText(contacts.this, "loaded all", Toast.LENGTH_SHORT).show();
                                } else ref(10, documentSnapshot);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_list_menu, menu);

        MenuItem searchViewItem
                = menu.findItem(R.id.search_bar);
        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        searchView.setOnSearchClickListener(view -> {
            first = firestore.collection("Main").document(UserID).collection("Contacts")
                    .orderBy("name", Query.Direction.ASCENDING);

            first.addSnapshotListener((queryDocumentSnapshots, error) -> {
                data.clear();
                dataList.clear();
                for (DocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots.getDocuments())) {
                    contacts_lists cn = document.toObject(contacts_lists.class);
                    data.add(cn);
                    dataList.add(cn);
                }
                adapter.notifyItemInserted(pastVisiblesItems);
                if (!data.isEmpty()) {
                    binding.recs.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                    setTitle("Contacts ".concat(String.valueOf(data.size())));
                    scroll(queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1));
                    check = queryDocumentSnapshots;
                } else {
                    binding.textView9.setText("No data available\n"+error);
                    binding.recs.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                }
            });
        });
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
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
                performTask performTask = new performTask("get", 6, UserID);
                performTask.Task();
                SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));
                snackbarTop.showSnackBar("Getting contacts", true);
                InterstitialAdsClass interstitialAdsClass = new InterstitialAdsClass(this);
                interstitialAdsClass.loadAds();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private ArrayList<contacts_lists> filter(ArrayList<contacts_lists> dataList, String newText) {
        newText = newText.toLowerCase();
        String text, name;
        filteredDataList = new ArrayList<>();
        for (contacts_lists dataFromDataList : dataList) {
            text = dataFromDataList.getPhoneNumber().toLowerCase();
            name = dataFromDataList.getName().toLowerCase();

            if (text.contains(newText) || name.contains(newText)) {
                filteredDataList.add(dataFromDataList);
            }
        }
        if (filteredDataList.isEmpty()) {
            binding.textView9.setText("No data available");
            binding.recs.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        } else {
            binding.recs.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        }

        return filteredDataList;
    }
}