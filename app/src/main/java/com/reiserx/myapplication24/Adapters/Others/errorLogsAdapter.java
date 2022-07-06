package com.reiserx.myapplication24.Adapters.Others;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.myapplication24.Models.exceptionUpload;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ErrorLogsLayoutBinding;

import java.util.ArrayList;

public class errorLogsAdapter extends RecyclerView.Adapter<errorLogsAdapter.SingleViewHolder> {

        Context context;
        ArrayList<exceptionUpload> data;

        public errorLogsAdapter(Context context, ArrayList<exceptionUpload> data) {
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public errorLogsAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.error_logs_layout, parent, false);
            return new errorLogsAdapter.SingleViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull errorLogsAdapter.SingleViewHolder holder, int position) {
            exceptionUpload cn = data.get(position);

            holder.binding.className.setText("Class name: "+cn.getClassName());
            holder.binding.lineNos.setText("Line no: "+String.valueOf(cn.getLineNumber()));
            holder.binding.methodName.setText("Method name: "+cn.getMethodName());
            if (cn.getTimestamp() != 0) {
                holder.binding.errorTime.setText(TimeAgo.using(cn.getTimestamp()));
            }

            holder.binding.getRoot().setOnClickListener(v -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(cn.getMethodName());
                alert.setMessage(cn.getMessage());
                alert.setPositiveButton("OK",null);
                alert.show();
            });
        }
        @Override
        public int getItemCount() {
            return data.size();
        }

        public class SingleViewHolder extends RecyclerView.ViewHolder {

            ErrorLogsLayoutBinding binding;

            public SingleViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ErrorLogsLayoutBinding.bind(itemView);
            }
        }
}
