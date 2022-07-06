package com.reiserx.myapplication24.Adapters.Contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.myapplication24.Classes.copyToClipboard;
import com.reiserx.myapplication24.Models.callLogs;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.CallLogLayoutBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class callogsAdapter extends RecyclerView.Adapter<callogsAdapter.SingleViewHolder> {

    Context context;
    ArrayList<callLogs> data;

    public callogsAdapter(Context context, ArrayList<callLogs> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public callogsAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.call_log_layout, parent, false);
        return new callogsAdapter.SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull callogsAdapter.SingleViewHolder holder, int position) {
        callLogs cn = data.get(position);

        holder.binding.numberTxt.setText(cn.phoneNumber);
        holder.binding.durationTxt.setText(getDurationString(Integer.parseInt(cn.duration)));
        holder.binding.typeTxt.setText(cn.getType());
        String time = TimeAgo.using(Long.parseLong(cn.dateinsecs));
        holder.binding.dateTxt.setText(time);

        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipboard copyToClipboard = new copyToClipboard();
                copyToClipboard.copy(cn.phoneNumber, context);
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    private String getDurationString(int seconds) {
        String a;
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
            long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
            long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

            String secs = getS((int) second, "sec");
            String mins = getS((int) second, "min");
            String hour = getS((int) second, "hr");
            String days = getS((int) second, "day");
            if (day==0 && hours==0 && minute==0) {
                a = second+" "+secs;
            } else if (day==0 && hours==0) {
                a = minute+" "+mins+" "+second+" "+secs;
            } else if (day==0) {
                a = hours+" "+hour+" "+minute+" "+mins+" "+second+" "+secs;
            } else if (second==0) {
                a = second+" sec";
            } else {
                a = day+" "+days+" "+hours+" "+hour+" "+minute+" "+mins+" "+second+" "+secs;
            }
            return a;
    }

    public String getS(int i, String temp) {
        String s;
        if (i>1) {
            s = temp.concat("s");
        } else s = temp;
        return s;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<callLogs> FilteredDataList) {
        data = (ArrayList<callLogs>) FilteredDataList;
        notifyDataSetChanged();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        CallLogLayoutBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CallLogLayoutBinding.bind(itemView);
        }
    }
}
