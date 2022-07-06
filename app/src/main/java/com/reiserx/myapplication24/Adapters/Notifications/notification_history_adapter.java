package com.reiserx.myapplication24.Adapters.Notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.myapplication24.Models.NotificationModel;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.NotificationHistoryCustomViewBinding;

import java.util.ArrayList;
import java.util.List;

public class notification_history_adapter extends RecyclerView.Adapter<notification_history_adapter.SingleViewHolder> {

    Context context;
    ArrayList<NotificationModel> data;
    String UserID;
    ArrayList<String> Keys;
    String AppName;
    String Titles;
    String TAG = "jjfgjfgjhjh";

    public notification_history_adapter(Context context, ArrayList<NotificationModel> data, String userID, ArrayList<String> keys, String AppName, String Titles) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        this.Keys = keys;
        this.AppName = AppName;
        this.Titles = Titles;
    }

    @NonNull
    @Override
    public notification_history_adapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_history_custom_view, parent, false);
        return new notification_history_adapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull notification_history_adapter.SingleViewHolder holder, int position) {
        NotificationModel model = data.get(position);

        holder.binding.ticker.setText("Ticker: "+model.getTicker());
        holder.binding.notifyTitle.setText("Title: "+model.getTitle());
        holder.binding.notifyMessage.setText("Message: "+model.getText());
        holder.binding.notifyTime.setText(TimeAgo.using(model.getTimestamp()));
        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.itemView.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(model.getTitle());
            String message = "Ticker: "+model.getTicker()+"\n"+"Message: "+model.getText();
            alert.setMessage(message);
            alert.setPositiveButton("ok", null);
            alert.show();
        });
        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Delete notification");
            alert.setMessage("This notification will be deleted for ever");
            alert.setPositiveButton("delete", (dialogInterface, i) -> {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("Main").document(UserID).collection("Notifications").document(AppName).collection(Titles).document(Keys.get(holder.getAdapterPosition())).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d(TAG, "done");
                        } else Log.d(TAG, task.getException().toString());
                        if (task.isSuccessful()) {
                            Log.d(TAG, "done");
                        } else Log.d(TAG, task.getException().toString());
                    }
                });
                Toast.makeText(context, "Notification deleted", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<NotificationModel> FilteredDataList) {
        data = (ArrayList<NotificationModel>) FilteredDataList;
        notifyDataSetChanged();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        NotificationHistoryCustomViewBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationHistoryCustomViewBinding.bind(itemView);
        }
    }
}