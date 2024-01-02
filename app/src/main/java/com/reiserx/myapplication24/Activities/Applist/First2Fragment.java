package com.reiserx.myapplication24.Activities.Applist;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.ApplistAdapter;
import com.reiserx.myapplication24.Adapters.Directories.FoldersAdapter;
import com.reiserx.myapplication24.Models.AppListInfo;
import com.reiserx.myapplication24.Models.Folders;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.FragmentFirst2Binding;

import java.util.ArrayList;

public class First2Fragment extends Fragment implements MenuProvider {

    private FragmentFirst2Binding binding;

    ArrayList<AppListInfo> data;
    ApplistAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirst2Binding.inflate(inflater, container, false);

        requireActivity().removeMenuProvider(this);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences save = requireActivity().getSharedPreferences("blocked", MODE_PRIVATE);
        String UserID = save.getString("UserID", "");

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplistAdapter(getContext(), data, UserID);
        binding.rec.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    AppListInfo appListInfo = snapshot1.getValue(AppListInfo.class);
                    appListInfo.setAppID(snapshot1.getKey());
                    data.add(appListInfo);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.fab2.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_First2Fragment_to_SecondFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.camera_options_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.camera_menuitem_option) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Info");
            alert.setMessage("Make sure to activate the service to refresh blocked apps list.");
            alert.setPositiveButton("ok", null);
            alert.show();
        }
        return false;
    }
}