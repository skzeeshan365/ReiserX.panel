package com.reiserx.myapplication24.Adapters.Camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Activities.FileViewers.ImageViewActivity;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.downloadUrl;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.RecyclerViewBinding;

import java.util.ArrayList;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.SingleViewHolder> {

    Context context;
    ArrayList<downloadUrl> data;
    String UserID;

    SnackbarTop snackbarTop;

    String TAG = "uydfgydfgsdyufhsg";

    public CameraAdapter(Context context, ArrayList<downloadUrl> data, String userID, View viewById) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        snackbarTop = new SnackbarTop(viewById);
    }

    @NonNull
    @Override
    public CameraAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false);
        return new CameraAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CameraAdapter.SingleViewHolder holder, int position) {
        downloadUrl info = data.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });

        Glide.with(context).load(info.getUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.binding.imageView);

        holder.binding.textView18.setVisibility(View.VISIBLE);
        holder.binding.textView18.setText(TimeAgo.using(info.getTimeStamp()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("url", info.getUrl());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Delete picture");
            dialog.setMessage("Are you sure want to delete this picture");
            dialog.setPositiveButton("delete", (dialogInterface, i) -> {
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("CameraPicture").child(info.getId());
                reference.delete();
                FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("CameraPicture").document(info.getId()).delete().addOnSuccessListener(unused -> snackbarTop.showSnackBar("file deleted", true));
            });
            dialog.setNegativeButton("cancel", null);
            dialog.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerViewBinding.bind(itemView);
        }
    }
}