package com.reiserx.myapplication24.Adapters.Notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.myapplication24.Activities.Notifications.Recycler2;
import com.reiserx.myapplication24.Models.NotificationPath;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.AppListCustomBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationHistoryMainAdapter extends RecyclerView.Adapter<NotificationHistoryMainAdapter.SingleViewHolder> {

    Context context;
    ArrayList<NotificationPath> data;
    String UserID;

    boolean Notifications;

    public NotificationHistoryMainAdapter(Context context, ArrayList<NotificationPath> data, String userID) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
    }

    @NonNull
    @Override
    public NotificationHistoryMainAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_custom, parent, false);
        return new NotificationHistoryMainAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationHistoryMainAdapter.SingleViewHolder holder, int position) {
        NotificationPath info = data.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.binding.switch3.setVisibility(View.GONE);
        holder.binding.textView14.setVisibility(View.VISIBLE);
        holder.binding.textView14.setText(info.getLabel());
        holder.itemView.setOnClickListener(view -> {
                String value = info.getName().replace(".", "");
                String reference = "Main/" + UserID + "/Notification history/Title/"+value;
                Intent intent = new Intent(context, Recycler2.class);
                intent.putExtra("UserID", UserID);
                intent.putExtra("Path", reference);
                intent.putExtra("AppName", info.getName());
            intent.putExtra("label", info.getLabel());
                context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<NotificationPath> FilteredDataList) {
        data = (ArrayList<NotificationPath>) FilteredDataList;
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