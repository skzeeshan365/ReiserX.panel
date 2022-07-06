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

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.myapplication24.Activities.Directories.webActivity;
import com.reiserx.myapplication24.Activities.FileViewers.PdfViewerActivity;
import com.reiserx.myapplication24.Classes.SnackbarTop;
import com.reiserx.myapplication24.Models.FileUpload;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.FoldersLayoutBinding;

import java.util.ArrayList;
import java.util.Locale;

public class viewFilesAdapter extends RecyclerView.Adapter<viewFilesAdapter.SingleViewHolder> {
    Context context;
    ArrayList<FileUpload> data;

    String UserID, Path;
    String message;
    SnackbarTop snackbarTop;

    public viewFilesAdapter(Context context, ArrayList<FileUpload> data, String UserID, String getPath, String message, View view) {
        this.context = context;
        this.data = data;
        this.UserID = UserID;
        this.Path = getPath;
        this.message = message;
        snackbarTop = new SnackbarTop(view);
    }

    @NonNull
    @Override
    public viewFilesAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folders_layout, parent, false);
        return new viewFilesAdapter.SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewFilesAdapter.SingleViewHolder holder, int position) {
        FileUpload fileUpload = data.get(position);

        holder.binding.textView4.setText(fileUpload.name);
        String name = fileUpload.name;
        if (name.endsWith(".mp4") || name.endsWith(".wmv") || name.endsWith(".webm")) {
            holder.binding.imageView2.setImageResource(R.drawable.ic_baseline_video_library_24);
        } else if (name.endsWith(".mp3") || name.endsWith(".mp4a") || name.endsWith(".wma") || name.endsWith(".m4a")) {
            holder.binding.imageView2.setImageResource(R.drawable.ic_baseline_music_note_24);
        }

            holder.itemView.setOnClickListener(v -> {
                Intent intent;
                if (message.equals("Videos") || message.equals("Audios")) {
                    intent = new Intent(context, webActivity.class);
                } else {
                    intent = new Intent(context, PdfViewerActivity.class);
                }
                intent.putExtra("url", fileUpload.url);
                intent.putExtra("name", fileUpload.name);
                context.startActivity(intent);
            });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Delete file");
            alert.setMessage("This file will be deleted from the server");
            alert.setPositiveButton("delete", (dialogInterface, i) -> {
                String path = Path.replace(".", "");
                if (message.equals("Videos")) {
                    StorageReference reference = FirebaseStorage.getInstance().getReference("Main").child(UserID).child(Path.toLowerCase(Locale.ROOT)).child("Videos").child(fileUpload.id);
                    reference.delete().addOnSuccessListener(unused -> {
                        FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("Upload").child("Videos").child(path.toLowerCase(Locale.ROOT)).child(fileUpload.id).removeValue().addOnSuccessListener(unused1 -> snackbarTop.showSnackBar("file deleted", true));
                    });
                } else if (message.equals("Audios")){
                    StorageReference reference = FirebaseStorage.getInstance().getReference("Main").child(UserID).child(Path.toLowerCase(Locale.ROOT)).child("Audios").child(fileUpload.id);
                    reference.delete().addOnSuccessListener(unused -> {
                        FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("Upload").child("Audios").child(path.toLowerCase(Locale.ROOT)).child(fileUpload.id).removeValue().addOnSuccessListener(unused12 -> snackbarTop.showSnackBar("file deleted", true));
                    }).addOnFailureListener(e -> {
                        Log.d("htyftb", e.toString());
                    });
                } else if (message.equals("PDF")) {
                    Log.d("htyftb", "ijdud");
                    StorageReference reference = FirebaseStorage.getInstance().getReference("Main").child(UserID).child(Path.toLowerCase(Locale.ROOT)).child("PDF").child(fileUpload.id);
                    reference.delete().addOnSuccessListener(unused -> {
                        FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("Upload").child("PDF").child(path.toLowerCase(Locale.ROOT)).child(fileUpload.id).removeValue().addOnSuccessListener(unused12 -> snackbarTop.showSnackBar("file deleted", true));
                    }).addOnFailureListener(e -> {
                        Log.d("htyftb", e.toString());
                    });
                }
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

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        FoldersLayoutBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FoldersLayoutBinding.bind(itemView);
        }
    }
}
