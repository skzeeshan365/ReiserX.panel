package com.reiserx.myapplication24.Adapters.Directories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Activities.FileViewers.ImageViewActivity;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.FileUpload;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.RecyclerViewBinding;

import java.util.ArrayList;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    Context context;
    ArrayList<FileUpload> imagedatas;

    String UserID, path;

    SnackbarTop snackbarTop;

    public ImageAdapter(Context context, ArrayList<FileUpload> imagedatas, String UserID, String path, View view) {
        this.context = context;
        this.imagedatas = imagedatas;
        this.UserID = UserID;
        this.path = path;
        snackbarTop = new SnackbarTop(view);
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        FileUpload imagedata = imagedatas.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });

        holder.binding.textView18.setVisibility(View.VISIBLE);
        holder.binding.textView18.setText(imagedata.getName());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("url", imagedata.getUrl());
            context.startActivity(intent);
        });

        Glide.with(context).load(imagedata.getUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.binding.imageView);

        holder.itemView.setOnLongClickListener(view -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Delete file");
            alert.setMessage("This file will be deleted from the server");
            alert.setPositiveButton("delete", (dialogInterface, i) -> {
                String path1 = path.replace(".", "");
                    StorageReference reference = FirebaseStorage.getInstance().getReference("Main").child(UserID).child(path.toLowerCase(Locale.ROOT)).child("Images").child(imagedata.id);
                    reference.delete().addOnSuccessListener(unused -> {
                        FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("Upload").child("Images").child(path1.toLowerCase(Locale.ROOT)).child(imagedata.database_ID).removeValue().addOnSuccessListener(unused1 -> snackbarTop.showSnackBar("file deleted", true));
                    }).addOnFailureListener(e -> Log.d("hthfgjyj", e.toString()));
            });
            alert.setNegativeButton("cancel", null);
            alert.show();
            return false;
        });
        }
    @Override
    public int getItemCount() {
        return imagedatas.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewBinding binding;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerViewBinding.bind(itemView);
        }
    }
}
