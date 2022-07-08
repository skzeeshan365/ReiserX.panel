package com.reiserx.myapplication24.Adapters.Screenshot;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Activities.FileViewers.ImageViewActivity;
import com.reiserx.myapplication24.Advertisements.NativeAdsClass;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.downloadUrl;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.NativeAdsLayoutMediumBinding;
import com.reiserx.myapplication24.databinding.RecyclerViewBinding;

import java.util.ArrayList;

public class screenShotImageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<downloadUrl> data;
    String UserID;

    SnackbarTop snackbarTop;

    String TAG = "uydfgydfgsdyufhsg";

    final int DATA_CONTENT = 1;
    final int ADS_CONTENT = 2;

    public screenShotImageAdapter(Context context, ArrayList<downloadUrl> data, String userID, View viewById) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        snackbarTop = new SnackbarTop(viewById);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_CONTENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false);
            return new DataViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.native_ads_layout_medium, parent, false);
            return new AdsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        downloadUrl info = data.get(position);

        if (holder.getClass() == DataViewHolder.class) {
            DataViewHolder viewHolder = (DataViewHolder) holder;
            viewHolder.binding.getRoot().setOnClickListener(v -> {
            });

            Glide.with(context).load(info.getUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .into(viewHolder.binding.imageView);

            viewHolder.binding.textView18.setVisibility(View.VISIBLE);
            viewHolder.binding.textView18.setText(TimeAgo.using(info.getTimeStamp()));

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("url", info.getUrl());
                context.startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(view -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Delete screenshot");
                dialog.setMessage("Are you sure want to delete thia screenshot");
                dialog.setPositiveButton("delete", (dialogInterface, i) -> {
                    StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("ScreenShots").child(info.getId());
                    reference.delete();
                    FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("ScreenShots").document(info.getId()).delete().addOnSuccessListener(unused -> snackbarTop.showSnackBar("file deleted", true));
                });
                dialog.setNegativeButton("cancel", null);
                dialog.show();
                return false;
            });
        } else if (holder.getClass() == AdsViewHolder.class) {
            AdsViewHolder viewHolder = (AdsViewHolder) holder;
            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.dark));
            NativeAdsClass nativeAdsClass = new NativeAdsClass(context, viewHolder.binding.myTemplate, viewHolder.binding.nativeAdsHolder);
            nativeAdsClass.loadAd();
        }
    }

    @Override
    public int getItemViewType(int position) {
        downloadUrl info = data.get(position);
        if (info.isAd()) {
            return ADS_CONTENT;
        } else {
            return DATA_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewBinding binding;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerViewBinding.bind(itemView);
        }
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {

        NativeAdsLayoutMediumBinding binding;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NativeAdsLayoutMediumBinding.bind(itemView);
        }
    }
}