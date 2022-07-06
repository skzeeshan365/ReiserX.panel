package com.reiserx.myapplication24.Adapters.UsageStats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.myapplication24.Models.AppUsageInfo;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.AppListCustomBinding;

import java.util.ArrayList;
import java.util.List;

public class UsageStatsAdapter extends RecyclerView.Adapter<UsageStatsAdapter.SingleViewHolder> {

    Context context;
    ArrayList<AppUsageInfo> data;

    String TAG = "uydfgydfgsdyufhsg";

    public UsageStatsAdapter(Context context, ArrayList<AppUsageInfo> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public UsageStatsAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_custom, parent, false);
        return new UsageStatsAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UsageStatsAdapter.SingleViewHolder holder, int position) {
        AppUsageInfo info = data.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.binding.switch3.setVisibility(View.GONE);
        holder.binding.textView14.setVisibility(View.VISIBLE);
        holder.binding.textView14.setText(info.getAppName());

        holder.itemView.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(info.getAppName());
            dialog.setMessage("Package name: "+info.getPackageName()+"\n"+"Screen time: "+convertSecondsToHMmSs(info.timeInForeground)+"\n"+"No. of launches: "+info.launchCount);
            dialog.setPositiveButton("ok", null);
            dialog.show();
        });
    }
    public static String convertSecondsToHMmSs(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        return hours+":"+minutes+":"+seconds;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<AppUsageInfo> FilteredDataList) {
        data = (ArrayList<AppUsageInfo>) FilteredDataList;
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