package com.reiserx.myapplication24.Adapters.Directories;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.myapplication24.Activities.Directories.checkDetails;
import com.reiserx.myapplication24.Activities.Directories.openFiles;
import com.reiserx.myapplication24.Models.Folders;
import com.reiserx.myapplication24.Models.performTask;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.FoldersLayoutBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class directorysAdapter extends RecyclerView.Adapter<directorysAdapter.SingleViewHolder> {
    Context context;
    ArrayList<Folders> data;
    ArrayList<String> list;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressDialog progressDialog;

    public directorysAdapter(Context context, ArrayList<Folders> data, ArrayList<String> list) {
        this.context = context;
        this.data = data;
        this.list = list;
    }

    @NonNull
    @Override
    public directorysAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folders_layout, parent, false);
        return new directorysAdapter.SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull directorysAdapter.SingleViewHolder holder, int position) {
        Folders folders = data.get(position);

        holder.binding.textView4.setText(folders.getFolder());

        checkFile(folders.getFolder(), holder);

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            holder.itemView.setOnLongClickListener(v -> {
                SharedPreferences save = context.getSharedPreferences("Userss", MODE_PRIVATE);
                String userid = save.getString("UserID", "");

                dialog.setTitle("TASK");
                dialog.setMessage("Choose your task".concat(folders.getFolder()));

                dialog.setPositiveButton("DOWNLOAD", (dialog1, which) -> {

                    String path = folders.getFolder();

                    Intent intent = new Intent(context, checkDetails.class);
                    intent.putExtra("Path", path);
                    intent.putExtra("Message", folders.getFolder());
                    intent.putExtra("UserID", userid);
                    context.startActivity(intent);

                });

                dialog.setNegativeButton("DELETE", (dialog12, which) -> {
                    AlertDialog.Builder delete = new AlertDialog.Builder(context);
                    delete.setTitle("DELETE");
                    delete.setMessage("This file/directory will be deleted from target device".concat(folders.getFolder()));
                    delete.setPositiveButton("DELETE", (dialog13, which1) -> {

                        performTask performTask = new performTask(folders.getFolder(), 7, userid);
                        performTask.Task();
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("DELETE");
                        progressDialog.setMessage("Processing...");
                        progressDialog.show();

                        database = FirebaseDatabase.getInstance();
                        database.getReference("Main").child(userid).child("Folders").child("Deleted").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String value = snapshot.getValue(String.class);
                                    progressDialog.dismiss();

                                    Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
                                    database.getReference("Main").child(userid).child("Folders").child("Deleted")
                                            .removeValue()
                                            .addOnSuccessListener(unused -> {
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    });
                    delete.setNegativeButton("CANCEL", null);
                    delete.show();

                });
                dialog.setNeutralButton("REMOVE", (dialog15, which) -> {
                    AlertDialog.Builder remove = new AlertDialog.Builder(context);
                    remove.setTitle("Remove ".concat(folders.getFolder()));
                    remove.setMessage("Are you sure you want to remove this file, the file will be deleted from the server");
                    remove.setPositiveButton("REMOVE", (dialog1, whichs) -> {
                        String key = list.get(position);
                        DatabaseReference ref = database.getReference("Main").child(userid).child("Directorys").child(key);
                        ref.removeValue().addOnSuccessListener(unused -> Toast.makeText(context, folders.getFolder().concat(" has been removed"), Toast.LENGTH_SHORT).show());
                    });
                    remove.show();
                });
                dialog.show();

                return false;
            });

            holder.itemView.setOnClickListener((View.OnClickListener) v -> {
                SharedPreferences save = context.getSharedPreferences("Userss", MODE_PRIVATE);
                String userid = save.getString("UserID", "");

                performTask performTask = new performTask(folders.getFolder(), 1, userid);
                performTask.Task();
                            Intent intent = new Intent(context, openFiles.class);
                            intent.putExtra("Path", folders.getFolder());
                            intent.putExtra("UserID", userid);
                            context.startActivity(intent);
            });
    }

    private void checkFile(String filePath, directorysAdapter.SingleViewHolder holder) {
        File file = new File(filePath);
        if (!file.getName().contains("."))
            holder.binding.imageView2.setImageResource(R.drawable.ic_directory);
        else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"))
            holder.binding.imageView2.setImageResource(R.drawable.ic_baseline_image_24);
        else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".wmv") || file.getName().endsWith(".webm"))
            holder.binding.imageView2.setImageResource(R.drawable.ic_baseline_video_library_24);
        else if (file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4a") || file.getName().endsWith(".wma") || file.getName().endsWith(".m4a"))
            holder.binding.imageView2.setImageResource(R.drawable.ic_baseline_music_note_24);
        else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".docx"))
            holder.binding.imageView2.setImageResource(R.drawable.ic_outline_picture_as_pdf_24);
        else if (file.getName().endsWith(".txt"))
            holder.binding.imageView2.setImageResource(R.drawable.ic_baseline_text_snippet_24);
        else
            holder.binding.imageView2.setImageResource(R.drawable.ic_twotone_folder_24);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFilter(List<Folders> FilteredDataList) {
        data = (ArrayList<Folders>) FilteredDataList;
        notifyDataSetChanged();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        FoldersLayoutBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FoldersLayoutBinding.bind(itemView);
        }
    }
}
