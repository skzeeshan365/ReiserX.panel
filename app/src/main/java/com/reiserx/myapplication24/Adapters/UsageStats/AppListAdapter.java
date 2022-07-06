package com.reiserx.myapplication24.Adapters.UsageStats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.myapplication24.Models.AppListInfo;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.AppListCustomBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.SingleViewHolder> {

    Context context;
    ArrayList<AppListInfo> data;
    String UserID;
    ArrayList<String> Keys;

    boolean Notifications;

    String TAG = "uydfgydfgsdyufhsg";

    public AppListAdapter(Context context, ArrayList<AppListInfo> data, String userID, ArrayList<String> keys, boolean notifications) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        this.Keys = keys;
        this.Notifications = notifications;
    }

    @NonNull
    @Override
    public AppListAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_custom, parent, false);
        return new AppListAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AppListAdapter.SingleViewHolder holder, int position) {
        AppListInfo info = data.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });

        if (Notifications) {
            holder.binding.switch3.setVisibility(View.VISIBLE);
            holder.binding.textView14.setVisibility(View.GONE);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            if (info.getLabel() != null && !info.getLabel().equals("")) {
                holder.binding.switch3.setText(info.getLabel());
            } else holder.binding.switch3.setText(info.getPackageName());
            holder.binding.textView15.setText(info.getPackageName());

            if (holder.binding.textView15.getText().equals(info.getAppID())) {
                holder.binding.switch3.setChecked(info.isProcessStatus());
                HashMap<String, Object> map = new HashMap<>();
                DocumentReference documents = firestore.collection("Main").document(UserID).collection("App list").document(info.getAppID());

                holder.binding.switch3.setOnClickListener(view -> {
                    if (holder.binding.switch3.isChecked()) {
                        holder.binding.switch3.setChecked(false);
                    } else holder.binding.switch3.setChecked(true);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                if (!holder.binding.switch3.isChecked()) {
                    alert.setTitle("Enanle notification");
                    alert.setMessage("Enable notification for " + info.getPackageName());
                    alert.setPositiveButton("enable", (dialogInterface, i) -> {
                        map.put("processStatus", true);
                        holder.binding.switch3.setChecked(true);
                        documents.update(map);
                    });
                } else {
                    alert.setTitle("Disable notification");
                    alert.setMessage("Disable notification for " + info.getPackageName());
                    alert.setPositiveButton("disable", (dialogInterface, i) -> {
                        map.put("processStatus", false);
                        holder.binding.switch3.setChecked(false);
                        documents.update(map);
                    });
                }
                alert.setNegativeButton("cancel", null);
                alert.show();
                });
            }
        } else {
            holder.binding.switch3.setVisibility(View.GONE);
            holder.binding.textView14.setVisibility(View.VISIBLE);
            if (info.getLabel() != null) {
                holder.binding.textView14.setText(info.getLabel());
            } else holder.binding.textView14.setText(info.getPackageName());
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
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