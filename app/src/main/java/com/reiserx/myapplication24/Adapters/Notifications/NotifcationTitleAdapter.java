package com.reiserx.myapplication24.Adapters.Notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.myapplication24.Activities.Notifications.notification_history;
import com.reiserx.myapplication24.Models.NotificationTitlePath;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.AppListCustomBinding;

import java.util.ArrayList;
import java.util.List;

public class NotifcationTitleAdapter extends RecyclerView.Adapter<NotifcationTitleAdapter.SingleViewHolder> {

    Context context;
    ArrayList<NotificationTitlePath> data;
    String UserID;
    String appName;

    public NotifcationTitleAdapter(Context context, ArrayList<NotificationTitlePath> data, String userID, String appName) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        this.appName = appName;
    }

    @NonNull
    @Override
    public NotifcationTitleAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_custom, parent, false);
        return new NotifcationTitleAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotifcationTitleAdapter.SingleViewHolder holder, int position) {
        NotificationTitlePath info = data.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.binding.switch3.setVisibility(View.GONE);
        holder.binding.textView14.setVisibility(View.VISIBLE);
        holder.binding.textView14.setText(info.getName().toString());
        holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, notification_history.class);
                intent.putExtra("UserID", UserID);
                intent.putExtra("AppName", appName);
                intent.putExtra("Title", info.getName());
                context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<NotificationTitlePath> FilteredDataList) {
        data = (ArrayList<NotificationTitlePath>) FilteredDataList;
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