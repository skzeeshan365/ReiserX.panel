package com.reiserx.myapplication24.Adapters.Audios;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.AudiosDownloadUrl;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.AudioFilesLayoutBinding;

import java.util.ArrayList;

public class AudiosAdapter extends RecyclerView.Adapter<AudiosAdapter.SingleViewHolder> {

    Context context;
    ArrayList<AudiosDownloadUrl> data;
    String UserID;
    SnackbarTop snackbarTop;

    String TAG = "uydfgydfgsdyufhsg";

    public AudiosAdapter(Context context, ArrayList<AudiosDownloadUrl> data, String userID, View view) {
        this.context = context;
        this.data = data;
        this.UserID = userID;
        snackbarTop = new SnackbarTop(view);
    }

    @NonNull
    @Override
    public AudiosAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_files_layout, parent, false);
        return new AudiosAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AudiosAdapter.SingleViewHolder holder, int position) {
        AudiosDownloadUrl info = data.get(position);

        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.binding.textView20.setText(TimeAgo.using(info.getTimeStamp()));
        if (info.getName() != null) {
            holder.binding.textView19.setText(info.getName());
        } else holder.binding.textView19.setText("Unknown");

        holder.itemView.setOnClickListener(view -> {
            Uri a = Uri.parse(info.getUrl());

            Intent viewMediaIntent = new Intent();
            viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
            viewMediaIntent.setDataAndType(a, "audio/*");
            viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(viewMediaIntent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Delete screenshot");
            dialog.setMessage("Are you sure want to delete thia screenshot");
            dialog.setPositiveButton("delete", (dialogInterface, i) -> {
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("AudioRecordings").child(info.getId());
                reference.delete();
                FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("AudioRecordings").document(info.getId()).delete().addOnSuccessListener(unused -> snackbarTop.showSnackBar("file deleted", true));
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

        AudioFilesLayoutBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AudioFilesLayoutBinding.bind(itemView);
        }
    }
}