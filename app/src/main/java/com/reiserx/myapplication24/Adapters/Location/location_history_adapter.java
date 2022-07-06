package com.reiserx.myapplication24.Adapters.Location;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.myapplication24.Models.locationModel;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.LocationHistoryCustomviewBinding;

import java.util.ArrayList;
import java.util.List;

public class location_history_adapter extends RecyclerView.Adapter<location_history_adapter.SingleViewHolder> {
    Context context;
    ArrayList<locationModel> data;
    View view;

    public location_history_adapter(Context context, ArrayList<locationModel> data, View view) {
        this.context = context;
        this.data = data;
        this.view = view;
    }

    @NonNull
    @Override
    public location_history_adapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_history_customview, parent, false);
        return new location_history_adapter.SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull location_history_adapter.SingleViewHolder holder, int position) {
        locationModel locationModel = data.get(position);

        String time = TimeAgo.using(locationModel.getTimestamp());
        holder.binding.historyTimestamp.setText(time);
        holder.binding.latitude.setText(locationModel.latitude);
        holder.binding.lhistoryOngitude.setText(locationModel.longitude);
        holder.binding.getRoot().setOnClickListener(v -> {
        });

        holder.binding.getRoot().setOnLongClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", locationModel.latitude.concat(",".concat(locationModel.longitude)));
            clipboard.setPrimaryClip(clip);
            topSnackbar("Location copied to clipboard", Color.GREEN, view);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFilter(List<locationModel> FilteredDataList) {
        data = (ArrayList<locationModel>) FilteredDataList;
        notifyDataSetChanged();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        LocationHistoryCustomviewBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LocationHistoryCustomviewBinding.bind(itemView);
        }

    }
    public void topSnackbar(String message, int color, View view) {
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(color);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        snackbar.show();
    }
}
