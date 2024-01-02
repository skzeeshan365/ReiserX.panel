package com.reiserx.myapplication24.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Adapters.Contacts.contactAdapter;
import com.reiserx.myapplication24.Classes.copyToClipboard;
import com.reiserx.myapplication24.Models.AppListInfo;
import com.reiserx.myapplication24.Models.contacts_lists;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.AppListCustomBinding;
import com.reiserx.myapplication24.databinding.ContactListBinding;

import java.util.ArrayList;
import java.util.List;

public class ApplistAdapter extends RecyclerView.Adapter<ApplistAdapter.SingleViewHolder> {
    Context context;
    ArrayList<AppListInfo> data;
    String UserID;
    int CHECK;
    NavController navController;

    public ApplistAdapter(Context context, ArrayList<AppListInfo> data, String UserID, int check, NavController navController) {
        this.context = context;
        this.data = data;
        this.UserID = UserID;
        this.CHECK = check;
        this.navController = navController;
    }

    public ApplistAdapter(Context context, ArrayList<AppListInfo> data, String UserID) {
        this.context = context;
        this.data = data;
        this.UserID = UserID;
    }

    @NonNull
    @Override
    public ApplistAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_custom, parent, false);
        return new ApplistAdapter.SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplistAdapter.SingleViewHolder holder, int position) {
        AppListInfo appListInfo = data.get(position);

        holder.binding.textView14.setText(appListInfo.getLabel());
        holder.binding.switch3.setVisibility(View.GONE);
        holder.binding.textView14.setVisibility(View.VISIBLE);

        holder.binding.getRoot().setOnClickListener(v -> {
            if (CHECK == 1) {
                holder.binding.getRoot().setEnabled(false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps");
                reference.orderByChild("packageName").equalTo(appListInfo.getPackageName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps").push().setValue(appListInfo).addOnSuccessListener(unused -> {
                                Toast.makeText(context, "App blocked", Toast.LENGTH_SHORT).show();
                                navController.popBackStack();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.binding.getRoot().setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Unblock");
            alert.setMessage("Are you sure you want to unblock "+appListInfo.getLabel());
            alert.setPositiveButton("unblock", (dialogInterface, i) -> {
                FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps").child(appListInfo.getAppID()).removeValue();
            });
            alert.setNegativeButton("cancel", null);
            alert.show();
            return false;
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFilter(List<AppListInfo> FilteredDataList) {
        data = (ArrayList<AppListInfo>) FilteredDataList;
        notifyDataSetChanged();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        AppListCustomBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AppListCustomBinding.bind(itemView);
        }
    }
}