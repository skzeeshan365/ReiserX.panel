package com.reiserx.myapplication24.Activities.ParentActivities;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Audios.DeviceListAdaptet;
import com.reiserx.myapplication24.Advertisements.bannerAdsClass;
import com.reiserx.myapplication24.Models.Administrators;
import com.reiserx.myapplication24.Models.Users;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.FragmentFirstBinding;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    FirebaseDatabase mdb = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID;

    ArrayList<Users> data;
    DeviceListAdaptet adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Perform your tasks
        SharedPreferences save = view.getContext().getSharedPreferences("Admins", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            UserID = currentUser.getUid();
        }

        bannerAdsClass bannerAdsClass = new bannerAdsClass(view.getContext(), binding.bannerAdHolder);
        bannerAdsClass.adsCode();

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new DeviceListAdaptet(view.getContext(), data);
        binding.rec.setAdapter(adapter);

        mdb.getReference("Administration").child("Administrators").child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Administrators admins = snapshot.getValue(Administrators.class);
                    if (admins != null && admins.role.equals("User")) {
                        mdb.getReference("Administration").child("TargetDevices").child(UserID).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                data.clear();
                                snapshot.exists();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Users u = snapshot1.getValue(Users.class);
                                    data.add(u);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        myEdit.putString("Admin", admins.role);
                        myEdit.putBoolean("fileUploadAccess", admins.getFileUploadAccess());
                        myEdit.apply();
                    } else if (admins != null && admins.role.equals("Admin")) {
                        mdb.getReference("Userdata").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                data.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Users u = snapshot1.getValue(Users.class);
                                    data.add(u);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        myEdit.putString("Admin", "Admin");
                        myEdit.putBoolean("fileUploadAccess", admins.getFileUploadAccess());
                        myEdit.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}