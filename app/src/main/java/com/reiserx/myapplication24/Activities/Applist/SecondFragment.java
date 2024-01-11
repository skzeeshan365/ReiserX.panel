package com.reiserx.myapplication24.Activities.Applist;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.reiserx.myapplication24.Adapters.ApplistAdapter;
import com.reiserx.myapplication24.Models.AppListInfo;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.FragmentSecondBinding;

import java.util.ArrayList;

public class SecondFragment extends Fragment implements MenuProvider {

    private FragmentSecondBinding binding;

    ArrayList<AppListInfo> data, filteredDataList, dataList;
    ApplistAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        requireActivity().removeMenuProvider(this);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences save = requireActivity().getSharedPreferences("blocked", MODE_PRIVATE);
        String UserID = save.getString("UserID", "");

        data = new ArrayList<>();
        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplistAdapter(getContext(), data, UserID, 1, NavHostFragment.findNavController(this));
        binding.rec.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("App list")
                .orderBy("packageName", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        AppListInfo appListInfo = documentSnapshot.toObject(AppListInfo.class);
                        data.add(appListInfo);
                        dataList.add(appListInfo);
                    }
                    adapter.notifyDataSetChanged();
                    if (!data.isEmpty()) {
                        binding.rec.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                    } else {
                        binding.textView9.setText("No data available");
                        binding.rec.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.searchs, menu);

        // Find the search menu item and set its click listener
        MenuItem searchViewItem
                = menu.findItem(R.id.search_bar);
        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);
        searchView.setOnSearchClickListener(view -> {

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
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private ArrayList<AppListInfo> filter(ArrayList<AppListInfo> dataList, String newText) {
        newText = newText.toLowerCase();
        String name;
        filteredDataList = new ArrayList<>();
        for (AppListInfo dataFromDataList : dataList) {
            name = dataFromDataList.getLabel().toLowerCase();

            if (name.contains(newText)) {
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